package abkabk.azbarkon.features.profile

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.datasource.MemorizationLocalDataSource
import abkabk.azbarkon.domain.memorization.MemorizationReviewNotificationCoordinator
import abkabk.azbarkon.domain.model.ThemeMode
import abkabk.azbarkon.domain.model.profile.ProfileSheet
import abkabk.azbarkon.domain.model.memorization.SrsCard
import abkabk.azbarkon.domain.model.memorization.SrsGrade
import abkabk.azbarkon.testing.FakeDailyBeytNotificationScheduler
import abkabk.azbarkon.testing.FakeMemorizationRepository
import abkabk.azbarkon.testing.FakeMemorizationReviewNotificationScheduler
import abkabk.azbarkon.testing.FakeNotificationPermissionGateway
import abkabk.azbarkon.testing.FakeUserPreferencesRepository
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
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

    private fun createViewModel(
        preferences: FakeUserPreferencesRepository = FakeUserPreferencesRepository(),
        dailyBeytScheduler: FakeDailyBeytNotificationScheduler = FakeDailyBeytNotificationScheduler(),
        permissionGateway: FakeNotificationPermissionGateway = FakeNotificationPermissionGateway(granted = true),
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

        override suspend fun findPoetIdByName(nameFragment: String): Result<Int, DataError.Local> =
            Result.Error(DataError.Local.UNKNOWN)

        override suspend fun findCategoryByPoetAndText(
            poetId: Int,
            textFragment: String,
        ): Result<Pair<Int, String>, DataError.Local> = Result.Error(DataError.Local.UNKNOWN)
    }
}
