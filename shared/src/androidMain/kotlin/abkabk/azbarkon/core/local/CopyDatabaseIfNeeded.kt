package abkabk.azbarkon.core.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.azbarkon.db.AzbarKonDatabase
import java.io.File
import java.io.IOException

internal const val DATABASE_NAME = "ganjoor.s3db"

fun copyDatabaseIfNeeded(context: Context) {
    val dbFile = context.getDatabasePath(DATABASE_NAME)

    if (!dbFile.exists()) {
        copyBundledDatabase(context, dbFile)
    }

    syncBundledDatabaseVersion(dbFile.path)
}

private fun copyBundledDatabase(context: Context, dbFile: File) {
    dbFile.parentFile?.mkdirs()
    try {
        context.assets.open(DATABASE_NAME).use { input ->
            dbFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    } catch (_: IOException) {
    }
}

private fun syncBundledDatabaseVersion(dbPath: String) {
    val db =
        SQLiteDatabase.openDatabase(
            dbPath,
            null,
            SQLiteDatabase.OPEN_READWRITE,
        )
    try {
        if (db.version != 0) return

        val hasPoetTable =
            db.rawQuery(
                "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = 'poet' LIMIT 1",
                null,
            ).use { it.moveToFirst() }

        if (hasPoetTable) {
            db.version = AzbarKonDatabase.Schema.version.toInt()
        }
    } finally {
        db.close()
    }
}
