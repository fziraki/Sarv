package abkabk.azbarkon.core.ui_base

import abkabk.azbarkon.core.domain.result.DataError
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.error_disk_full
import azbarkoncmp.shared.generated.resources.error_no_internet
import azbarkoncmp.shared.generated.resources.error_not_found
import azbarkoncmp.shared.generated.resources.error_server
import azbarkoncmp.shared.generated.resources.error_unauthorized
import azbarkoncmp.shared.generated.resources.error_unknown

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
        else -> UiText.Resource(Res.string.error_unknown)
    }
