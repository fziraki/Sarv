@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package abkabk.azbarkon.core.local

import app.cash.sqldelight.db.SqlDriver

expect class MemorizationDatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

internal const val MEMORIZATION_DATABASE_NAME = "azbarkon_memorization.db"
