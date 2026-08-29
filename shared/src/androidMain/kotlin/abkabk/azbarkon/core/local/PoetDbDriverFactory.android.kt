package abkabk.azbarkon.core.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.sarv.db.SarvDatabase

actual class PoetDbDriverFactory(
    private val context: Context,
) {
    actual fun open(path: String): SqlDriver =
        AndroidSqliteDriver(
            schema = SarvDatabase.Schema,
            context = context,
            name = path,
            callback = AndroidSqliteDriver.Callback(SarvDatabase.Schema),
        )
}
