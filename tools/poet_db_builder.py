"""Builds the default slim database and one database per poet from the full ganjoor dump.

Usage:
    python tools/poet_db_builder.py

Input:  shared/sqlite/ganjoor.s3db (full dump, not in git)
Output: tools/out/
    ganjoor.s3db          default DB: all 249 poets' metadata + verses of DEFAULT_POET_IDS
    poet_{id}.s3db        one DB per remaining poet (poet/cat/poem/verse + fts4 index)
    manifest.json         {id, name, slug, file, size_bytes} for every poet

Upload the poet_*.s3db files to a GitHub release and point the app at it
(Constants.POET_DB_RELEASE_URL). The default DB replaces the current 160MB zip asset.
"""

import json
import os
import sqlite3
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
SRC_PATH = os.path.normpath(os.path.join(HERE, "..", "shared", "sqlite", "ganjoor.s3db"))
OUT_DIR = os.path.join(HERE, "out")

# Ganjoor's most-visited poets + Baba Tahir. Edit freely, then re-run.
DEFAULT_POET_IDS = [2, 3, 4, 5, 6, 7, 8, 9, 28]  # hafez khayyam ferdowsi molana nezami saadi parvin attar babataher

# ponytail: keep in sync with SarvDatabase.Schema.version (2 = one empty 1.sqm migration)
DB_VERSION = 2

SCHEMA = """
CREATE TABLE poet (
    id INTEGER NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    cat_id INTEGER NOT NULL,
    description TEXT NOT NULL
);
CREATE TABLE cat (
    id INTEGER NOT NULL PRIMARY KEY,
    poet_id INTEGER NOT NULL,
    text TEXT NOT NULL,
    parent_id INTEGER NOT NULL,
    url TEXT NOT NULL
);
CREATE TABLE poem (
    id INTEGER PRIMARY KEY,
    cat_id INTEGER,
    title NVARCHAR(255),
    url NVARCHAR(255)
);
CREATE TABLE verse (
    poem_id INTEGER,
    vorder INTEGER,
    position INTEGER,
    text TEXT
);
CREATE TABLE poet_meta (
    id INTEGER NOT NULL PRIMARY KEY,
    slug TEXT NOT NULL
);
CREATE VIRTUAL TABLE verse_fts4 USING fts4(text, content='verse');
"""


def create_empty(path):
    if os.path.exists(path):
        os.remove(path)
    db = sqlite3.connect(path)
    db.executescript(SCHEMA)
    return db


def copy_rows(src, dst, sql, rows, columns):
    dst.executemany(sql, (tuple(r) for r in rows))
    return len(rows)


def verses_for_poets(src, poet_ids):
    ids = ",".join(str(i) for i in poet_ids)
    return src.execute(
        "SELECT * FROM verse WHERE poem_id IN "
        f"(SELECT id FROM poem WHERE cat_id IN (SELECT id FROM cat WHERE poet_id IN ({ids})))"
    ).fetchall()


def main():
    if not os.path.exists(SRC_PATH):
        sys.exit(f"Source DB not found: {SRC_PATH}")
    os.makedirs(OUT_DIR, exist_ok=True)

    src = sqlite3.connect(SRC_PATH)

    all_poets = src.execute("SELECT id, name, cat_id, description FROM poet ORDER BY id").fetchall()
    print(f"source poets: {len(all_poets)}")

    default_ids = set(DEFAULT_POET_IDS)
    missing = default_ids - {p[0] for p in all_poets}
    if missing:
        sys.exit(f"DEFAULT_POET_IDS not in source: {missing}")

    manifest = [
        {
            "id": pid,
            "name": name,
            "slug": src.execute("SELECT url FROM cat WHERE id = ?", (cat_id,)).fetchone() or ("",),
            "file": "ganjoor.s3db" if pid in default_ids else f"poet_{pid}.s3db",
            "size_bytes": 0,
        }
        for pid, name, cat_id, _ in all_poets
    ]
    for m in manifest:
        slug_row = src.execute("SELECT url FROM cat WHERE id = (SELECT cat_id FROM poet WHERE id = ?)", (m["id"],)).fetchone()
        m["slug"] = (slug_row[0] or "").strip("/").rsplit("/", 1)[-1] if slug_row else ""

    # --- default DB: all poets metadata, verses only for DEFAULT_POET_IDS ---
    default_path = os.path.join(OUT_DIR, "ganjoor.s3db")
    dst = create_empty(default_path)
    dst.executemany("INSERT INTO poet VALUES (?, ?, ?, ?)", all_poets)
    dst.executemany("INSERT INTO poet_meta VALUES (?, ?)", [(m["id"], m["slug"]) for m in manifest])
    cats = src.execute(
        f"SELECT * FROM cat WHERE poet_id IN ({','.join(str(i) for i in default_ids)})"
    ).fetchall()
    copy_rows(src, dst, "INSERT INTO cat VALUES (?, ?, ?, ?, ?)", cats, None)
    poems = src.execute(
        "SELECT * FROM poem WHERE cat_id IN (SELECT id FROM cat "
        f"WHERE poet_id IN ({','.join(str(i) for i in default_ids)}))"
    ).fetchall()
    copy_rows(src, dst, "INSERT INTO poem VALUES (?, ?, ?, ?)", poems, None)
    verses = verses_for_poets(src, sorted(default_ids))
    copy_rows(src, dst, "INSERT INTO verse VALUES (?, ?, ?, ?)", verses, None)
    dst.execute("INSERT INTO verse_fts4(verse_fts4) VALUES('rebuild')")
    dst.execute(f"PRAGMA user_version = {DB_VERSION}")
    dst.commit()
    dst.close()
    default_size = os.path.getsize(default_path)
    print(f"default: {len(default_ids)} poets, {len(cats)} cats, {len(poems)} poems, {len(verses)} verses, {default_size/1e6:.1f} MB")

    # --- one DB per remaining poet ---
    for pid in sorted(all_poets, key=lambda p: p[0]):
        poet_id = pid[0]
        if poet_id in default_ids:
            continue
        poet_path = os.path.join(OUT_DIR, f"poet_{poet_id}.s3db")
        dst = create_empty(poet_path)
        dst.execute("INSERT INTO poet VALUES (?, ?, ?, ?)", tuple(pid))
        slug = src.execute("SELECT url FROM cat WHERE id = ?", (pid[2],)).fetchone()
        dst.execute("INSERT INTO poet_meta VALUES (?, ?)", (poet_id, (slug[0] if slug else "").strip("/").rsplit("/", 1)[-1]))
        poet_cats = src.execute("SELECT * FROM cat WHERE poet_id = ?", (poet_id,)).fetchall()
        copy_rows(src, dst, "INSERT INTO cat VALUES (?, ?, ?, ?, ?)", poet_cats, None)
        poet_poems = src.execute(
            "SELECT * FROM poem WHERE cat_id IN (SELECT id FROM cat WHERE poet_id = ?)", (poet_id,)
        ).fetchall()
        copy_rows(src, dst, "INSERT INTO poem VALUES (?, ?, ?, ?)", poet_poems, None)
        poet_verses = verses_for_poets(src, [poet_id])
        copy_rows(src, dst, "INSERT INTO verse VALUES (?, ?, ?, ?)", poet_verses, None)
        dst.execute("INSERT INTO verse_fts4(verse_fts4) VALUES('rebuild')")
        dst.execute(f"PRAGMA user_version = {DB_VERSION}")
        dst.commit()
        dst.close()
        manifest[next(i for i, m in enumerate(manifest) if m["id"] == poet_id)]["size_bytes"] = os.path.getsize(poet_path)
        print(f"  poet_{poet_id} {pid[1]}: {len(poet_cats)} cats, {len(poet_verses)} verses, {os.path.getsize(poet_path)/1e6:.1f} MB")

    for m in manifest:
        if m["file"] == "ganjoor.s3db":
            m["size_bytes"] = default_size

    with open(os.path.join(OUT_DIR, "manifest.json"), "w", encoding="utf-8") as f:
        json.dump(manifest, f, ensure_ascii=False, indent=2)
    print(f"\ndone. output in {OUT_DIR}")
    print(f"upload: gh release create poets-v1 {os.path.join(OUT_DIR, 'poet_*.s3db')} {os.path.join(OUT_DIR, 'manifest.json')}")


if __name__ == "__main__":
    main()
