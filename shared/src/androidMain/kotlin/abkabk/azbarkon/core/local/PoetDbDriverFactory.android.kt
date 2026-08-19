@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package abkabk.azbarkon.core.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.azbarkon.db.AzbarKonDatabase

actual class PoetDbDriverFactory(
    private val context: Context,
) {
    actual fun open(path: String): SqlDriver =
        AndroidSqliteDriver(
            schema = AzbarKonDatabase.Schema,
            context = context,
            name = path,
            callback = AndroidSqliteDriver.Callback(AzbarKonDatabase.Schema),
        )
}
