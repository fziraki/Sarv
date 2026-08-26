# Builds and uploads per-poet SQLDelight databases for Sarv.

## Build (local, requires the full dump at `../shared/sqlite/ganjoor.s3db`)

```powershell
python tools/poet_db_builder.py
```

Outputs to `tools/out/`:

- `ganjoor.s3db` — default DB: metadata of all 249 poets, verses of
  `DEFAULT_POET_IDS` only. **Copy this over
  `shared/src/androidMain/assets/ganjoor.s3db.zip` (zipped) before shipping.**
- `poet_{id}.s3db` — one file per remaining poet.
- `manifest.json` — id/name/slug/file/size for every poet.

The app expects `poet_{id}.s3db` files served from
`Constants.POET_DB_RELEASE_URL` (a GitHub release "latest/download" URL).

## Upload

```powershell
gh release create poets-v1 tools/out/poet_*.s3db tools/out/manifest.json --title "Poet databases v1"
```

Set the release URL in `shared/src/commonMain/kotlin/abkabk/azbarkon/core/util/Constants.kt`
(`POET_DB_RELEASE_URL`). Default poet ids are editable at the top of the script.
