package abkabk.azbarkon.core.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.azbarkon.memorization.MemorizationDatabase

actual class MemorizationDatabaseDriverFactory(
    private val context: Context,
) {
    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(
            schema = MemorizationDatabase.Schema,
            context = context,
            name = MEMORIZATION_DATABASE_NAME,
        )
}
