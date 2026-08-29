package abkabk.azbarkon.core.uidata

import abkabk.azbarkon.core.domain.result.DataError
import sarv.shared.generated.resources.Res
import sarv.shared.generated.resources.error_db_query
import sarv.shared.generated.resources.error_disk_full
import sarv.shared.generated.resources.error_no_internet
import sarv.shared.generated.resources.error_not_found
import sarv.shared.generated.resources.error_server
import sarv.shared.generated.resources.error_unauthorized
import sarv.shared.generated.resources.error_unknown

fun DataError.toUiText(): UiText =
    when (this) {
        DataError.Network.NO_INTERNET -> UiText.Resource(Res.string.error_no_internet)
        DataError.Network.UNAUTHORIZED -> UiText.Resource(Res.string.error_unauthorized)
        DataError.Network.NOT_FOUND -> UiText.Resource(Res.string.error_not_found)
        DataError.Network.SERVER_ERROR,
        DataError.Network.SERVICE_UNAVAILABLE,
        -> UiText.Resource(Res.string.error_server)
        DataError.Local.DISK_FULL -> UiText.Resource(Res.string.error_disk_full)
        DataError.Local.NOT_FOUND -> UiText.Resource(Res.string.error_not_found)
        DataError.Local.QUERY_FAILED -> UiText.Resource(Res.string.error_db_query)
        else -> UiText.Resource(Res.string.error_unknown)
    }
