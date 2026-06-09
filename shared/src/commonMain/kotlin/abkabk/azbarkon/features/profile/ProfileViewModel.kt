package abkabk.azbarkon.features.profile

import abkabk.azbarkon.core.domain.result.onFailure
import abkabk.azbarkon.core.domain.result.onSuccess
import abkabk.azbarkon.core.ui_base.BaseViewModel
import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.core.ui_base.toUiText
import abkabk.azbarkon.domain.platform.DailyBeytNotificationScheduler
import abkabk.azbarkon.domain.platform.NotificationPermissionGateway
import abkabk.azbarkon.domain.repository.UserPreferencesRepository
import abkabk.azbarkon.domain.repository.UserRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val dailyBeytNotificationScheduler: DailyBeytNotificationScheduler,
    private val notificationPermissionGateway: NotificationPermissionGateway,
) : BaseViewModel<ProfileAction, ProfileState, ProfileEvent>(
        initialState =
            ProfileState(
                isDailyBeytNotificationEnabled =
                    userPreferencesRepository.isDailyBeytNotificationEnabled(),
            ),
    ) {
    init {
        onAction(ProfileAction.OnLoad)
    }

    override fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.OnLoad,
            ProfileAction.OnRetryClick,
            -> loadUserInfo()

            is ProfileAction.OnDailyBeytNotificationToggle -> {
                if (action.enabled) {
                    enableDailyBeytNotifications()
                } else {
                    disableDailyBeytNotifications()
                }
            }

            is ProfileAction.OnNotificationPermissionResult -> {
                handleNotificationPermissionResult(action.granted)
            }
        }
    }

    private fun enableDailyBeytNotifications() {
        setState {
            copy(isDailyBeytNotificationEnabled = true)
        }

        if (notificationPermissionGateway.areNotificationsEnabled()) {
            userPreferencesRepository.setDailyBeytNotificationEnabled(true)
            dailyBeytNotificationScheduler.enable(showImmediately = true)
        } else {
            viewModelScope.launch {
                sendEvent(ProfileEvent.RequestNotificationPermission)
            }
        }
    }

    private fun handleNotificationPermissionResult(granted: Boolean) {
        if (granted) {
            userPreferencesRepository.setDailyBeytNotificationEnabled(true)
            dailyBeytNotificationScheduler.enable(showImmediately = true)
            setState {
                copy(isDailyBeytNotificationEnabled = true)
            }
        } else {
            userPreferencesRepository.setDailyBeytNotificationEnabled(false)
            dailyBeytNotificationScheduler.disable()
            setState {
                copy(isDailyBeytNotificationEnabled = false)
            }
        }
    }

    private fun disableDailyBeytNotifications() {
        userPreferencesRepository.setDailyBeytNotificationEnabled(false)
        dailyBeytNotificationScheduler.disable()
        setState {
            copy(isDailyBeytNotificationEnabled = false)
        }
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            setState {
                copy(screenState = UiScreenState.Loading)
            }

            userRepository.getUserInfo()
                .onSuccess { userInfo ->
                    setState {
                        copy(
                            screenState = UiScreenState.Success,
                            userInfo = userInfo,
                            isDailyBeytNotificationEnabled =
                                userPreferencesRepository.isDailyBeytNotificationEnabled(),
                        )
                    }
                }.onFailure { error ->
                    val message = error.toUiText()
                    setState {
                        copy(
                            screenState =
                                UiScreenState.Error(
                                    message = message,
                                ),
                        )
                    }
                    sendEvent(ProfileEvent.ShowSnackbar(message))
                }
        }
    }
}
