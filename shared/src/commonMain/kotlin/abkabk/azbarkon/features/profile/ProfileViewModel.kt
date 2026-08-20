package abkabk.azbarkon.features.profile

import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.notifications.MAX_NOTIFICATION_PERMISSION_DECLINES
import abkabk.azbarkon.core.uidata.BaseViewModel
import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.core.uidata.UiText
import abkabk.azbarkon.data.backup.UserBackupManager
import abkabk.azbarkon.domain.memorization.MemorizationReviewNotificationCoordinator
import abkabk.azbarkon.domain.model.ThemeMode
import abkabk.azbarkon.domain.model.profile.BadgeCatalog
import abkabk.azbarkon.domain.model.profile.GameLevelCatalog
import abkabk.azbarkon.domain.model.profile.LevelListItemUi
import abkabk.azbarkon.domain.model.profile.MemorizationProfileStats
import abkabk.azbarkon.domain.model.profile.ProfileSheet
import abkabk.azbarkon.domain.platform.DailyBeytNotificationScheduler
import abkabk.azbarkon.domain.platform.NotificationPermissionGateway
import abkabk.azbarkon.domain.platform.ShareService
import abkabk.azbarkon.domain.repository.MemorizationRepository
import abkabk.azbarkon.domain.repository.UserPreferencesRepository
import abkabk.azbarkon.features.poets.GHAZAL_CATEGORY
import androidx.lifecycle.viewModelScope
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.profile_export_success
import azbarkoncmp.shared.generated.resources.profile_import_failed
import azbarkoncmp.shared.generated.resources.profile_import_success
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val memorizationRepository: MemorizationRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val dailyBeytNotificationScheduler: DailyBeytNotificationScheduler,
    private val notificationPermissionGateway: NotificationPermissionGateway,
    private val memorizationReviewNotificationCoordinator: MemorizationReviewNotificationCoordinator,
    private val userBackupManager: UserBackupManager,
    private val shareService: ShareService,
) : BaseViewModel<ProfileAction, ProfileState, ProfileEvent>(
        initialState =
            ProfileState(
                isDailyBeytNotificationEnabled =
                    userPreferencesRepository.isDailyBeytNotificationEnabled(),
                isMemorizationReminderEnabled =
                    userPreferencesRepository.isMemorizationReminderEnabled(),
                isRemoteNotificationGranted =
                    notificationPermissionGateway.areNotificationsEnabled(),
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
            -> setState { copy(screenState = UiScreenState.Success) }

            ProfileAction.OnSettingsClick -> {
                setState {
                    copy(
                        activeSheet = ProfileSheet.Settings,
                        isRemoteNotificationGranted =
                            notificationPermissionGateway.areNotificationsEnabled(),
                    )
                }
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

            is ProfileAction.OnRemoteNotificationClick -> {
                enableRemoteNotifications()
            }

            is ProfileAction.OnThemeModeSelected -> {
                userPreferencesRepository.setThemeMode(action.mode)
                setState { copy(themeMode = action.mode) }
            }

            is ProfileAction.OnNotificationPermissionResult -> {
                handleNotificationPermissionResult(action.granted, action.target)
            }

            ProfileAction.OnExportData -> {
                exportData()
            }

            is ProfileAction.OnImportDataSelected -> {
                setState { copy(pendingImportJson = action.json) }
            }

            ProfileAction.OnConfirmImport -> {
                state.value.pendingImportJson?.let { json ->
                    setState { copy(pendingImportJson = null) }
                    importData(json)
                }
            }

            ProfileAction.OnCancelImport -> {
                setState { copy(pendingImportJson = null) }
            }
        }
    }

    private fun exportData() {
        viewModelScope.launch {
            val json = userBackupManager.exportJson()
            shareService.shareFile(
                bytes = json.encodeToByteArray(),
                fileName = "azbarkon-backup.json",
                mimeType = "application/json",
                title = null,
            )
            sendEvent(
                ProfileEvent.ShowSnackbar(UiText.Resource(Res.string.profile_export_success)),
            )
        }
    }

    private fun importData(json: String) {
        viewModelScope.launch {
            when (val result = userBackupManager.importJson(json)) {
                is Result.Success -> {
                    val prefs = result.data.prefs
                    userPreferencesRepository.setThemeMode(
                        ThemeMode.entries.getOrElse(prefs.themeMode) { ThemeMode.System },
                    )
                    userPreferencesRepository.adjustCoinBalance(0)
                    memorizationReviewNotificationCoordinator.sync()
                    if (prefs.dailyBeytNotificationsEnabled) {
                        dailyBeytNotificationScheduler.enable(showImmediately = false)
                    } else {
                        dailyBeytNotificationScheduler.disable()
                    }
                    setState {
                        copy(
                            isDailyBeytNotificationEnabled = prefs.dailyBeytNotificationsEnabled,
                            isMemorizationReminderEnabled = prefs.memorizationReminderEnabled,
                        )
                    }
                    sendEvent(
                        ProfileEvent.ShowSnackbar(UiText.Resource(Res.string.profile_import_success)),
                    )
                }

                is Result.Error -> {
                    sendEvent(
                        ProfileEvent.ShowSnackbar(UiText.Resource(Res.string.profile_import_failed)),
                    )
                }
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
                sendEvent(
                    ProfileEvent.RequestNotificationPermission(
                        NotificationPermissionTarget.DailyBeyt,
                    ),
                )
            }
        }
    }

    private fun handleNotificationPermissionResult(
        granted: Boolean,
        target: NotificationPermissionTarget,
    ) {
        when (target) {
            NotificationPermissionTarget.DailyBeyt ->
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

            NotificationPermissionTarget.Remote -> {
                setState {
                    copy(isRemoteNotificationGranted = granted)
                }
            }
        }
    }

    private fun enableRemoteNotifications() {
        if (notificationPermissionGateway.areNotificationsEnabled()) {
            return
        }

        if (userPreferencesRepository.getNotificationPermissionDeclineCount() >=
            MAX_NOTIFICATION_PERMISSION_DECLINES
        ) {
            viewModelScope.launch {
                sendEvent(ProfileEvent.OpenAppNotificationSettings)
            }
        } else {
            viewModelScope.launch {
                sendEvent(
                    ProfileEvent.RequestNotificationPermission(
                        NotificationPermissionTarget.Remote,
                    ),
                )
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
