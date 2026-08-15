package abkabk.azbarkon.features.profile

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.data.backup.UserBackupManager
import abkabk.azbarkon.domain.datasource.MemorizationLocalDataSource
import abkabk.azbarkon.domain.memorization.MemorizationReviewNotificationCoordinator
import abkabk.azbarkon.domain.model.ThemeMode
import abkabk.azbarkon.domain.model.memorization.SrsCard
import abkabk.azbarkon.domain.model.memorization.SrsGrade
import abkabk.azbarkon.domain.model.memorization.StoredActivePoem
import abkabk.azbarkon.domain.model.memorization.StoredReviewLog
import abkabk.azbarkon.domain.model.profile.ProfileSheet
import abkabk.azbarkon.testing.FakeDailyBeytNotificationScheduler
import abkabk.azbarkon.testing.FakeMemorizationRepository
import abkabk.azbarkon.testing.FakeMemorizationReviewNotificationScheduler
import abkabk.azbarkon.testing.FakeNotificationPermissionGateway
import abkabk.azbarkon.testing.FakeShareService
import abkabk.azbarkon.testing.FakeUserBackupManager
import abkabk.azbarkon.testing.FakeUserPreferencesRepository
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `enabling daily beyt schedules notification when permission granted`() =
        runTest {
            val scheduler = FakeDailyBeytNotificationScheduler()
            val preferences = FakeUserPreferencesRepository()
            val permissionGateway = FakeNotificationPermissionGateway(granted = true)
            val viewModel = createViewModel(preferences, scheduler, permissionGateway)

            viewModel.onAction(ProfileAction.OnDailyBeytNotificationToggle(enabled = true))

            assertThat(viewModel.state.value.isDailyBeytNotificationEnabled).isTrue()
            assertThat(preferences.isDailyBeytNotificationEnabled()).isTrue()
            assertThat(scheduler.enableCallCount).isEqualTo(1)
            assertThat(scheduler.lastDeliveryHour).isEqualTo(8)
            assertThat(scheduler.lastShowImmediately).isEqualTo(true)
        }

    @Test
    fun `disabling daily beyt cancels notification`() =
        runTest {
            val scheduler = FakeDailyBeytNotificationScheduler()
            val preferences = FakeUserPreferencesRepository()
            val viewModel =
                createViewModel(
                    preferences,
                    scheduler,
                    FakeNotificationPermissionGateway(granted = true),
                )

            viewModel.onAction(ProfileAction.OnDailyBeytNotificationToggle(enabled = true))
            viewModel.onAction(ProfileAction.OnDailyBeytNotificationToggle(enabled = false))

            assertThat(viewModel.state.value.isDailyBeytNotificationEnabled).isFalse()
            assertThat(preferences.isDailyBeytNotificationEnabled()).isFalse()
            assertThat(scheduler.disableCallCount).isEqualTo(1)
        }

    @Test
    fun `permission denial reverts daily beyt toggle`() =
        runTest {
            val scheduler = FakeDailyBeytNotificationScheduler()
            val preferences = FakeUserPreferencesRepository()
            val viewModel =
                createViewModel(
                    preferences,
                    scheduler,
                    FakeNotificationPermissionGateway(granted = false),
                )

            viewModel.onAction(ProfileAction.OnDailyBeytNotificationToggle(enabled = true))
            viewModel.onAction(ProfileAction.OnNotificationPermissionResult(granted = false))

            assertThat(viewModel.state.value.isDailyBeytNotificationEnabled).isFalse()
            assertThat(preferences.isDailyBeytNotificationEnabled()).isFalse()
            assertThat(scheduler.disableCallCount).isEqualTo(1)
        }

    @Test
    fun `settings click opens settings sheet`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.onAction(ProfileAction.OnSettingsClick)

            assertThat(viewModel.state.value.activeSheet).isEqualTo(ProfileSheet.Settings)
        }

    @Test
    fun `dismiss sheet clears active sheet`() =
        runTest {
            val viewModel = createViewModel()
            viewModel.onAction(ProfileAction.OnSettingsClick)

            viewModel.onAction(ProfileAction.OnDismissSheet)

            assertThat(viewModel.state.value.activeSheet).isNull()
        }

    @Test
    fun `theme mode selection persists preference`() =
        runTest {
            val preferences = FakeUserPreferencesRepository()
            val viewModel = createViewModel(preferences)

            viewModel.onAction(ProfileAction.OnThemeModeSelected(ThemeMode.Dark))

            assertThat(viewModel.state.value.themeMode).isEqualTo(ThemeMode.Dark)
            assertThat(preferences.getThemeMode()).isEqualTo(ThemeMode.Dark)
        }

    @Test
    fun `disabling memorization reminder updates preference`() =
        runTest {
            val preferences = FakeUserPreferencesRepository()
            val viewModel = createViewModel(preferences)

            viewModel.onAction(ProfileAction.OnMemorizationReminderToggle(enabled = false))

            assertThat(viewModel.state.value.isMemorizationReminderEnabled).isFalse()
            assertThat(preferences.isMemorizationReminderEnabled()).isFalse()
        }

    @Test
    fun `export data shares backup json`() =
        runTest {
            val backupManager = FakeUserBackupManager()
            val shareService = FakeShareService()
            val viewModel = createViewModel(backupManager = backupManager, shareService = shareService)

            viewModel.onAction(ProfileAction.OnExportData)

            assertThat(shareService.lastSharedFileBytes).isNotNull()
            assertThat(shareService.lastSharedFileName).isEqualTo("azbarkon-backup.json")
            assertThat(shareService.lastSharedFileMimeType).isEqualTo("application/json")
        }

    @Test
    fun `import selection asks for confirmation then imports`() =
        runTest {
            val backupManager = FakeUserBackupManager()
            val preferences = FakeUserPreferencesRepository()
            val viewModel = createViewModel(backupManager = backupManager, preferences = preferences)
            val json = backupManager.exportJson()

            viewModel.onAction(ProfileAction.OnImportDataSelected(json))
            assertThat(viewModel.state.value.pendingImportJson).isEqualTo(json)

            viewModel.onAction(ProfileAction.OnConfirmImport)

            assertThat(viewModel.state.value.pendingImportJson).isNull()
            assertThat(backupManager.importCallCount).isEqualTo(1)
            assertThat(backupManager.lastImportedJson).isEqualTo(json)
        }

    @Test
    fun `import cancel clears pending json without importing`() =
        runTest {
            val backupManager = FakeUserBackupManager()
            val viewModel = createViewModel(backupManager = backupManager)

            viewModel.onAction(ProfileAction.OnImportDataSelected("json"))
            viewModel.onAction(ProfileAction.OnCancelImport)

            assertThat(viewModel.state.value.pendingImportJson).isNull()
            assertThat(backupManager.importCallCount).isEqualTo(0)
        }

    @Test
    fun `successful import applies imported prefs and shows success snackbar`() =
        runTest {
            val preferences = FakeUserPreferencesRepository()
            val backupManager =
                FakeUserBackupManager(preferences = preferences)
            val viewModel = createViewModel(backupManager = backupManager, preferences = preferences)
            val json = backupManager.exportJson()

            viewModel.onAction(ProfileAction.OnImportDataSelected(json))
            viewModel.onAction(ProfileAction.OnConfirmImport)

            assertThat(viewModel.state.value.isDailyBeytNotificationEnabled).isTrue()
            assertThat(preferences.isDailyBeytNotificationEnabled()).isTrue()
        }

    @Test
    fun `failed import shows error snackbar and keeps prefs`() =
        runTest {
            val preferences = FakeUserPreferencesRepository()
            val backupManager = FakeUserBackupManager(failImport = true)
            val viewModel = createViewModel(backupManager = backupManager, preferences = preferences)
            val json = backupManager.exportJson()

            viewModel.onAction(ProfileAction.OnImportDataSelected(json))
            viewModel.onAction(ProfileAction.OnConfirmImport)

            assertThat(viewModel.state.value.pendingImportJson).isNull()
            assertThat(preferences.isDailyBeytNotificationEnabled()).isFalse()
            assertThat(backupManager.lastImportedJson).isEqualTo(json)
        }

    private fun createViewModel(
        preferences: FakeUserPreferencesRepository = FakeUserPreferencesRepository(),
        dailyBeytScheduler: FakeDailyBeytNotificationScheduler = FakeDailyBeytNotificationScheduler(),
        permissionGateway: FakeNotificationPermissionGateway = FakeNotificationPermissionGateway(granted = true),
        backupManager: UserBackupManager = FakeUserBackupManager(),
        shareService: abkabk.azbarkon.domain.platform.ShareService = FakeShareService(),
    ): ProfileViewModel {
        val memorizationRepository = FakeMemorizationRepository()
        val reviewScheduler = FakeMemorizationReviewNotificationScheduler()
        val coordinator =
            MemorizationReviewNotificationCoordinator(
                localDataSource = FakeCoordinatorLocalDataSource(),
                scheduler = reviewScheduler,
                userPreferencesRepository = preferences,
            )
        return ProfileViewModel(
            memorizationRepository = memorizationRepository,
            userPreferencesRepository = preferences,
            dailyBeytNotificationScheduler = dailyBeytScheduler,
            notificationPermissionGateway = permissionGateway,
            memorizationReviewNotificationCoordinator = coordinator,
            userBackupManager = backupManager,
            shareService = shareService,
        )
    }

    private class FakeCoordinatorLocalDataSource : MemorizationLocalDataSource {
        override suspend fun countActivePoems(): Int = 0

        override suspend fun isPoemActive(poemId: Int): Boolean = false

        override suspend fun insertActivePoem(
            poemId: Int,
            addedAtMillis: Long,
            status: String,
        ) = Unit

        override suspend fun deleteActivePoem(poemId: Int) = Unit

        override suspend fun getActivePoemIds(): List<Int> = emptyList()

        override suspend fun getActivePoemAddedAt(poemId: Int): Long? = null

        override suspend fun insertCards(cards: List<SrsCard>) = Unit

        override suspend fun getCardById(cardId: Long): SrsCard? = null

        override suspend fun getDueCards(
            nowMillis: Long,
            poemId: Int?,
        ): List<SrsCard> = emptyList()

        override suspend fun countDueCards(
            nowMillis: Long,
            poemId: Int?,
        ): Int = 0

        override suspend fun updateCard(card: SrsCard) = Unit

        override suspend fun countCardsByPoemId(poemId: Int): Int = 0

        override suspend fun countReviewedCardsByPoemId(poemId: Int): Int = 0

        override suspend fun getAverageInterval(poemId: Int): Int = 0

        override suspend fun getMaxConsecutiveCorrect(poemId: Int): Int = 0

        override suspend fun insertReviewLog(
            cardId: Long,
            grade: SrsGrade,
            previousInterval: Int,
            newInterval: Int,
            reviewTimeMillis: Long,
        ) = Unit

        override suspend fun getReviewDayKeys(): List<Int> = emptyList()

        override suspend fun countReviewedVerses(): Int = 0

        override suspend fun dumpActivePoems(): List<StoredActivePoem> = emptyList()

        override suspend fun dumpCards(): List<SrsCard> = emptyList()

        override suspend fun dumpReviewLogs(): List<StoredReviewLog> = emptyList()

        override suspend fun replaceAll(
            activePoems: List<StoredActivePoem>,
            cards: List<SrsCard>,
            reviewLogs: List<StoredReviewLog>,
        ) = Unit

        override suspend fun findPoetIdByName(nameFragment: String): Result<Int, DataError.Local> =
            Result.Error(DataError.Local.UNKNOWN)

        override suspend fun findCategoryByPoetAndText(
            poetId: Int,
            textFragment: String,
        ): Result<Pair<Int, String>, DataError.Local> = Result.Error(DataError.Local.UNKNOWN)
    }
}
