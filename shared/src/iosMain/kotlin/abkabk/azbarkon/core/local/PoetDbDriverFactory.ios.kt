package abkabk.azbarkon.core.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import com.sarv.db.SarvDatabase

actual class PoetDbDriverFactory {
    actual fun open(path: String): SqlDriver {
        val dir = path.substringBeforeLast('/')
        val name = path.substringAfterLast('/')
        return NativeSqliteDriver(
            DatabaseConfiguration(
                name = name,
                version = SarvDatabase.Schema.version.toInt(),
                create = { },
                upgrade = { _, _, _ -> },
                extendedConfig = DatabaseConfiguration.Extended(basePath = dir),
            ),
        )
    }
}
