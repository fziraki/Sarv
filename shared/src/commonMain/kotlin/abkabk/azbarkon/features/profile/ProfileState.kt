package abkabk.azbarkon.features.profile

import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.core.ui_base.UiText
import abkabk.azbarkon.domain.model.UserInfo
import androidx.compose.runtime.Stable

@Stable
data class ProfileState(
    val screenState: UiScreenState = UiScreenState.Idle,
    val userInfo: UserInfo? = null,
    val isDailyBeytNotificationEnabled: Boolean = false,
)

sealed interface ProfileAction {
    data object OnLoad : ProfileAction

    data object OnRetryClick : ProfileAction

    data class OnDailyBeytNotificationToggle(
        val enabled: Boolean,
    ) : ProfileAction

    data class OnNotificationPermissionResult(
        val granted: Boolean,
    ) : ProfileAction
}

sealed interface ProfileEvent {
    data class ShowSnackbar(
        val message: UiText,
    ) : ProfileEvent

    data object RequestNotificationPermission : ProfileEvent
}
