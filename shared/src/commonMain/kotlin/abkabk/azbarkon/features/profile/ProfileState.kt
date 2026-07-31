package abkabk.azbarkon.features.profile

import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.domain.model.ThemeMode
import abkabk.azbarkon.domain.model.profile.BadgeUi
import abkabk.azbarkon.domain.model.profile.GameProfileStats
import abkabk.azbarkon.domain.model.profile.LevelListItemUi
import abkabk.azbarkon.domain.model.profile.MemorizationProfileStats
import abkabk.azbarkon.domain.model.profile.ProfileLevelProgress
import abkabk.azbarkon.domain.model.profile.ProfileSheet
import androidx.compose.runtime.Stable

@Stable
data class ProfileState(
    val screenState: UiScreenState = UiScreenState.Idle,
    val activeSheet: ProfileSheet? = null,
    val themeMode: ThemeMode = ThemeMode.System,
    val isDailyBeytNotificationEnabled: Boolean = false,
    val isMemorizationReminderEnabled: Boolean = true,
    val levelProgress: ProfileLevelProgress = ProfileLevelProgress(levelId = 1, levelName = "", currentXp = 0, targetXp = 900),
    val memorizationStats: MemorizationProfileStats = MemorizationProfileStats(),
    val gameStats: GameProfileStats = GameProfileStats(),
    val previewBadges: List<BadgeUi> = emptyList(),
    val allBadges: List<BadgeUi> = emptyList(),
    val allLevels: List<LevelListItemUi> = emptyList(),
    val reviewedVersesCount: Int = 0,
    val hasCompletedGhazal: Boolean = false,
    val avatarIndex: Int = -1,
)

sealed interface ProfileAction {
    data object OnLoad : ProfileAction

    data object OnRetryClick : ProfileAction

    data object OnSettingsClick : ProfileAction

    data object OnDismissSheet : ProfileAction

    data object OnViewAllBadgesClick : ProfileAction

    data object OnLevelsIconClick : ProfileAction

    data class OnDailyBeytNotificationToggle(
        val enabled: Boolean,
    ) : ProfileAction

    data class OnMemorizationReminderToggle(
        val enabled: Boolean,
    ) : ProfileAction

    data class OnThemeModeSelected(
        val mode: ThemeMode,
    ) : ProfileAction

    data class OnNotificationPermissionResult(
        val granted: Boolean,
    ) : ProfileAction

    data object OnAvatarClick : ProfileAction

    data class OnAvatarSelected(
        val index: Int,
    ) : ProfileAction
}

sealed interface ProfileEvent {
    data class ShowSnackbar(
        val message: abkabk.azbarkon.core.uidata.UiText,
    ) : ProfileEvent

    data object RequestNotificationPermission : ProfileEvent
}
