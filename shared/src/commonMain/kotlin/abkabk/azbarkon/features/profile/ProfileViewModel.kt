package abkabk.azbarkon.features.profile

import abkabk.azbarkon.core.uidata.BaseViewModel
import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.domain.memorization.MemorizationReviewNotificationCoordinator
import abkabk.azbarkon.domain.model.ThemeMode
import abkabk.azbarkon.domain.model.profile.BadgeCatalog
import abkabk.azbarkon.domain.model.profile.GameLevelCatalog
import abkabk.azbarkon.domain.model.profile.LevelListItemUi
import abkabk.azbarkon.domain.model.profile.MemorizationProfileStats
import abkabk.azbarkon.domain.model.profile.ProfileSheet
import abkabk.azbarkon.domain.platform.DailyBeytNotificationScheduler
import abkabk.azbarkon.domain.platform.NotificationPermissionGateway
import abkabk.azbarkon.domain.repository.MemorizationRepository
import abkabk.azbarkon.domain.repository.UserPreferencesRepository
import abkabk.azbarkon.features.poets.GHAZAL_CATEGORY
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val memorizationRepository: MemorizationRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val dailyBeytNotificationScheduler: DailyBeytNotificationScheduler,
    private val notificationPermissionGateway: NotificationPermissionGateway,
    private val memorizationReviewNotificationCoordinator: MemorizationReviewNotificationCoordinator,
) : BaseViewModel<ProfileAction, ProfileState, ProfileEvent>(
        initialState =
            ProfileState(
                isDailyBeytNotificationEnabled =
                    userPreferencesRepository.isDailyBeytNotificationEnabled(),
                isMemorizationReminderEnabled =
                    userPreferencesRepository.isMemorizationReminderEnabled(),
                themeMode = userPreferencesRepository.getThemeMode(),
            ),
    ) {
    init {
        onAction(ProfileAction.OnLoad)
        observeProfileData()
    }

    override fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.OnLoad,
            ProfileAction.OnRetryClick,
            -> setState { copy(screenState = UiScreenState.Success) }

            ProfileAction.OnSettingsClick -> {
                setState { copy(activeSheet = ProfileSheet.Settings) }
            }

            ProfileAction.OnDismissSheet -> {
                setState { copy(activeSheet = null) }
            }

            ProfileAction.OnViewAllBadgesClick -> {
                setState { copy(activeSheet = ProfileSheet.Badges) }
            }

            ProfileAction.OnLevelsIconClick -> {
                setState { copy(activeSheet = ProfileSheet.Levels) }
            }

            is ProfileAction.OnDailyBeytNotificationToggle -> {
                if (action.enabled) {
                    enableDailyBeytNotifications()
                } else {
                    disableDailyBeytNotifications()
                }
            }

            is ProfileAction.OnMemorizationReminderToggle -> {
                setMemorizationReminder(action.enabled)
            }

            is ProfileAction.OnThemeModeSelected -> {
                userPreferencesRepository.setThemeMode(action.mode)
                setState { copy(themeMode = action.mode) }
            }

            is ProfileAction.OnNotificationPermissionResult -> {
                handleNotificationPermissionResult(action.granted)
            }
        }
    }

    private fun observeProfileData() {
        viewModelScope.launch {
            combine(
                memorizationRepository.observeActiveSummary(),
                memorizationRepository.observePracticeStreak(),
                userPreferencesRepository.observeGameStats(),
                userPreferencesRepository.observeThemeMode(),
            ) { summary, practiceStreak, gameStats, themeMode ->
                ProfileSnapshot(
                    summary = summary,
                    practiceStreak = practiceStreak,
                    gameStats = gameStats,
                    themeMode = themeMode,
                )
            }.collect { snapshot ->
                refreshFromSnapshot(snapshot)
            }
        }
    }

    private suspend fun refreshFromSnapshot(snapshot: ProfileSnapshot) {
        val levelProgress = GameLevelCatalog.progressFromCoinBalance(snapshot.gameStats.coinBalance)
        val reviewedVerses = memorizationRepository.countReviewedVerses()
        val activePoems =
            memorizationRepository.getActivePoems().let { result ->
                when (result) {
                    is abkabk.azbarkon.core.domain.result.Result.Success -> result.data
                    is abkabk.azbarkon.core.domain.result.Result.Error -> emptyList()
                }
            }
        val completedPoems =
            activePoems.filter { poem ->
                poem.totalCards > 0 && poem.reviewedCards >= poem.totalCards
            }
        val completedPoemCount = completedPoems.size
        val hasCompletedGhazal = completedPoems.any { it.categoryName == GHAZAL_CATEGORY }
        setState {
            copy(
                screenState = UiScreenState.Success,
                themeMode = snapshot.themeMode,
                isDailyBeytNotificationEnabled =
                    userPreferencesRepository.isDailyBeytNotificationEnabled(),
                isMemorizationReminderEnabled =
                    userPreferencesRepository.isMemorizationReminderEnabled(),
                levelProgress = levelProgress,
                memorizationStats =
                    MemorizationProfileStats(
                        practiceStreak = snapshot.practiceStreak,
                        completedPoemCount = completedPoemCount,
                    ),
                gameStats = snapshot.gameStats,
                reviewedVersesCount = reviewedVerses,
                hasCompletedGhazal = hasCompletedGhazal,
            )
        }
        rebuildDerivedUi()
    }

    private fun rebuildDerivedUi() {
        val current = state.value
        val badges =
            BadgeCatalog.badges.map { badge ->
                BadgeCatalog.toBadgeUi(
                    badge = badge,
                    hasCompletedGhazal = current.hasCompletedGhazal,
                    reviewedVersesCount = current.reviewedVersesCount,
                    gameVisitStreak = current.gameStats.visitStreak,
                    completedPoemCount = current.memorizationStats.completedPoemCount,
                    perfectGameSessions = current.gameStats.perfectGameSessions,
                )
            }
        val levels =
            GameLevelCatalog.levels.map { level ->
                LevelListItemUi(
                    level = GameLevelCatalog.toGameLevel(level),
                    state =
                        GameLevelCatalog.levelRowState(
                            levelId = level.id,
                            currentLevelId = current.levelProgress.levelId,
                        ),
                )
            }
        setState {
            copy(
                previewBadges = badges,
                allBadges = badges,
                allLevels = levels,
            )
        }
    }

    private fun setMemorizationReminder(enabled: Boolean) {
        userPreferencesRepository.setMemorizationReminderEnabled(enabled)
        setState { copy(isMemorizationReminderEnabled = enabled) }
        viewModelScope.launch {
            memorizationReviewNotificationCoordinator.sync()
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

    private data class ProfileSnapshot(
        val summary: abkabk.azbarkon.domain.model.memorization.MemorizationSummary,
        val practiceStreak: Int,
        val gameStats: abkabk.azbarkon.domain.model.profile.GameProfileStats,
        val themeMode: ThemeMode,
    )
}
