package abkabk.azbarkon.core.paging

import androidx.paging.PagingConfig

const val PAGE_SIZE = 10

const val MIN_PAGE_LOAD_MILLIS = 500L

val DEFAULT_PAGING_CONFIG =
    PagingConfig(
        pageSize = PAGE_SIZE,
        initialLoadSize = PAGE_SIZE,
        prefetchDistance = 1,
        enablePlaceholders = false,
    )
