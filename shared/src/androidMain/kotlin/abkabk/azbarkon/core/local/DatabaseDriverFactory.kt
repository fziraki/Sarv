@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package abkabk.azbarkon.core.local

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.azbarkon.db.AzbarKonDatabase

actual class DatabaseDriverFactory(
    private val context: Context,
) {
    actual fun createDriver(): SqlDriver {
        copyDatabaseIfNeeded(context)

        val callback =
            object : AndroidSqliteDriver.Callback(AzbarKonDatabase.Schema) {
                override fun onUpgrade(
                    db: SupportSQLiteDatabase,
                    oldVersion: Int,
                    newVersion: Int,
                ) {
                    if (oldVersion == 0 && hasPrePopulatedSchema(db)) {
                        db.execSQL("PRAGMA user_version = $newVersion")
                        return
                    }
                    super.onUpgrade(db, oldVersion, newVersion)
                }
            }

        return AndroidSqliteDriver(
            schema = AzbarKonDatabase.Schema,
            context = context,
            name = DATABASE_NAME,
            callback = callback,
        )
    }
}

private fun hasPrePopulatedSchema(db: SupportSQLiteDatabase): Boolean =
    db.query(
        "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = 'poet' LIMIT 1",
    ).use { it.moveToFirst() }
