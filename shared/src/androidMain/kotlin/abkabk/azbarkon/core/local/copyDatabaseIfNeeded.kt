package abkabk.azbarkon.core.local

import android.content.Context

fun copyDatabaseIfNeeded(context: Context) {

    val dbFile = context.getDatabasePath("ganjoor.s3db")

    if (dbFile.exists()) return

    dbFile.parentFile?.mkdirs()

    context.assets.open("ganjoor.s3db").use { input ->

        dbFile.outputStream().use { output ->

            input.copyTo(output)
        }
    }
}