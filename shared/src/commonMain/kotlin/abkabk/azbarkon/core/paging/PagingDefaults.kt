package abkabk.azbarkon.core.paging

import androidx.paging.PagingConfig

const val PAGE_SIZE = 10

val DEFAULT_PAGING_CONFIG =
    PagingConfig(
        pageSize = PAGE_SIZE,
        initialLoadSize = PAGE_SIZE,
        prefetchDistance = PAGE_SIZE / 2,
        enablePlaceholders = false,
    )
