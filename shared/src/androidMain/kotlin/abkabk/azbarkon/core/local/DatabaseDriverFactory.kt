@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package abkabk.azbarkon.core.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.azbarkon.db.AzbarKonDatabase

actual class DatabaseDriverFactory(
    private val context: Context,
) {
    actual fun createDriver(): SqlDriver {
        copyDatabaseIfNeeded(context)

        return AndroidSqliteDriver(
            schema = AzbarKonDatabase.Schema,
            context = context,
            name = "ganjoor.s3db",
        )
    }
}
