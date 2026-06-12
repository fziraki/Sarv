@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

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
