package abkabk.azbarkon.core.domain.result

import io.github.aakira.napier.Napier

inline fun <T> dbQuery(block: () -> T): Result<T, DataError.Local> =
    try {
        Result.Success(block())
    } catch (e: IllegalStateException) {
        Napier.e("Database query failed", e)
        Result.Error(DataError.Local.QUERY_FAILED)
    }
