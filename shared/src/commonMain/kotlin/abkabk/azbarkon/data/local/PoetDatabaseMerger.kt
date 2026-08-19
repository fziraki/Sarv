package abkabk.azbarkon.data.local

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.azbarkon.db.AzbarKonDatabase
import io.github.aakira.napier.Napier

// ponytail: raw-SQL bind counts are positional, so the repeated 4/5/2/... are intentional
@Suppress("MagicNumber")
fun mergePoetDatabase(
    database: AzbarKonDatabase,
    target: SqlDriver,
    source: SqlDriver,
    poetId: Long,
) {
    // ponytail: source files carry real Ganjoor ids (no clash with bundled db); only verse
    // rowids are relative (1..n) per source file, so compose a unique one from (poemId, rowid)
    database.transaction {
        val (poetName, rootCatId) = source.poetRow(poetId)
        Napier.d(
            message = "merge: poetId=$poetId rootCatId=$rootCatId",
            tag = "PoetDebug",
        )
        target.execute(null, "INSERT OR IGNORE INTO poet (id, name, cat_id, description) VALUES (?, ?, ?, ?)", 4) {
            bindLong(0, poetId)
            bindString(1, poetName)
            bindLong(2, rootCatId)
            bindString(3, null)
        }

        source.executeQuery(
            identifier = null,
            sql = "SELECT id, poet_id, text, parent_id, url FROM cat",
            mapper = { cursor ->
                while (cursor.next().value) {
                    target.execute(null, "INSERT OR IGNORE INTO cat (id, poet_id, text, parent_id, url) VALUES (?, ?, ?, ?, ?)", 5) {
                        bindLong(0, cursor.getLong(0) ?: 0L)
                        bindLong(1, cursor.getLong(1) ?: 0L)
                        bindString(2, cursor.getString(2))
                        val parentId = cursor.getLong(3)
                        if (parentId != null) bindLong(3, parentId) else bindLong(3, null)
                        bindString(4, cursor.getString(4))
                    }
                }
                QueryResult.Unit
            },
            parameters = 0,
        )

        target.execute(null, "UPDATE poet SET cat_id = ? WHERE id = ?", 2) {
            bindLong(0, rootCatId)
            bindLong(1, poetId)
        }

        source.executeQuery(
            identifier = null,
            sql = "SELECT id, cat_id, title, url FROM poem",
            mapper = { cursor ->
                var minId = Long.MAX_VALUE
                var maxId = Long.MIN_VALUE
                var count = 0L
                while (cursor.next().value) {
                    val srcId = cursor.getLong(0) ?: 0L
                    if (srcId < minId) minId = srcId
                    if (srcId > maxId) maxId = srcId
                    count++
                    target.execute(null, "INSERT OR IGNORE INTO poem (id, cat_id, title, url) VALUES (?, ?, ?, ?)", 4) {
                        bindLong(0, srcId)
                        bindLong(1, cursor.getLong(1) ?: 0L)
                        bindString(2, cursor.getString(2))
                        bindString(3, cursor.getString(3))
                    }
                }
                Napier.d(
                    message = "merge: poem source count=$count minId=$minId maxId=$maxId -> first=${minId}",
                    tag = "PoetDebug",
                )
                QueryResult.Unit
            },
            parameters = 0,
        )

        source.executeQuery(
            identifier = null,
            sql = "SELECT rowid, poem_id, vorder, position, text FROM verse",
            mapper = { cursor ->
                while (cursor.next().value) {
                    val srcPoemId = cursor.getLong(1) ?: 0L
                    val newRowid = (srcPoemId shl 32) or (cursor.getLong(0) ?: 0L)
                    target.execute(null, "INSERT OR IGNORE INTO verse (rowid, poem_id, vorder, position, text) VALUES (?, ?, ?, ?, ?)", 5) {
                        bindLong(0, newRowid)
                        bindLong(1, srcPoemId)
                        bindLong(2, cursor.getLong(2) ?: 0L)
                        bindLong(3, cursor.getLong(3) ?: 0L)
                        bindString(4, cursor.getString(4))
                    }
                    target.execute(null, "INSERT INTO verse_fts4 (docid, text) VALUES (?, ?)", 2) {
                        bindLong(0, newRowid)
                        bindString(1, cursor.getString(4))
                    }
                }
                QueryResult.Unit
            },
            parameters = 0,
        )
    }
}

private fun SqlDriver.poetRow(poetId: Long): Pair<String?, Long> =
    executeQuery(
        identifier = null,
        sql = "SELECT name, cat_id FROM poet WHERE id = $poetId",
        mapper = { cursor ->
            if (cursor.next().value) {
                QueryResult.Value((cursor.getString(0) to (cursor.getLong(1) ?: 0L)))
            } else {
                QueryResult.Value((null to 0L))
            }
        },
        parameters = 0,
    ).value
