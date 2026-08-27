package abkabk.azbarkon.core.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration
import com.sarv.db.SarvDatabase
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        copyDatabaseIfNeeded()

        if (hasBundledDatabase()) {
            val documentsPath =
                documentsDirectory()
                    ?: error("Documents directory unavailable")

            return NativeSqliteDriver(
                DatabaseConfiguration(
                    name = DATABASE_NAME,
                    version = SarvDatabase.Schema.version.toInt(),
                    create = { },
                    upgrade = { connection, oldVersion, newVersion ->
                        if (oldVersion == 0) return@DatabaseConfiguration
                        wrapConnection(connection) { driver ->
                            SarvDatabase.Schema.migrate(
                                driver,
                                oldVersion.toLong(),
                                newVersion.toLong(),
                            )
                        }
                    },
                    extendedConfig =
                        DatabaseConfiguration.Extended(
                            basePath = documentsPath,
                        ),
                ),
            )
        }

        return NativeSqliteDriver(
            schema = SarvDatabase.Schema,
            name = DATABASE_NAME,
        )
    }
}
