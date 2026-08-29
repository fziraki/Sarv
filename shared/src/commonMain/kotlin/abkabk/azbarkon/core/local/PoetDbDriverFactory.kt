package abkabk.azbarkon.core.local

import app.cash.sqldelight.db.SqlDriver

expect class PoetDbDriverFactory {
    fun open(path: String): SqlDriver
}
