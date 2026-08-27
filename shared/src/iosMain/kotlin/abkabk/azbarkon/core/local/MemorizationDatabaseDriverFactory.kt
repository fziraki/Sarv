package abkabk.azbarkon.core.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.azbarkon.memorization.MemorizationDatabase

actual class MemorizationDatabaseDriverFactory {
    actual fun createDriver(): SqlDriver =
        NativeSqliteDriver(
            schema = MemorizationDatabase.Schema,
            name = MEMORIZATION_DATABASE_NAME,
        )
}
