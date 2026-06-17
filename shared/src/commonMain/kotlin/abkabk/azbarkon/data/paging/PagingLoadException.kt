package abkabk.azbarkon.data.paging

import abkabk.azbarkon.core.domain.result.DataError

class PagingLoadException(
    val error: DataError.Local,
) : Exception()
