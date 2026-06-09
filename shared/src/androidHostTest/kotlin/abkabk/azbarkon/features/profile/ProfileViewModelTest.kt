package abkabk.azbarkon.features.profile

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.GameLevel
import abkabk.azbarkon.domain.model.UserInfo
import abkabk.azbarkon.domain.repository.UserRepository
import abkabk.azbarkon.testing.FakeDailyBeytNotificationScheduler
import abkabk.azbarkon.testing.FakeNotificationPermissionGateway
import abkabk.azbarkon.testing.FakeUserPreferencesRepository
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
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
            val viewModel =
                ProfileViewModel(
                    userRepository = FakeUserRepository(),
                    userPreferencesRepository = preferences,
                    dailyBeytNotificationScheduler = scheduler,
                    notificationPermissionGateway = permissionGateway,
                )

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
                ProfileViewModel(
                    userRepository = FakeUserRepository(),
                    userPreferencesRepository = preferences,
                    dailyBeytNotificationScheduler = scheduler,
                    notificationPermissionGateway = FakeNotificationPermissionGateway(granted = true),
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
            val permissionGateway = FakeNotificationPermissionGateway(granted = false)
            val viewModel =
                ProfileViewModel(
                    userRepository = FakeUserRepository(),
                    userPreferencesRepository = preferences,
                    dailyBeytNotificationScheduler = scheduler,
                    notificationPermissionGateway = permissionGateway,
                )

            viewModel.onAction(ProfileAction.OnDailyBeytNotificationToggle(enabled = true))
            viewModel.onAction(ProfileAction.OnNotificationPermissionResult(granted = false))

            assertThat(viewModel.state.value.isDailyBeytNotificationEnabled).isFalse()
            assertThat(preferences.isDailyBeytNotificationEnabled()).isFalse()
            assertThat(scheduler.disableCallCount).isEqualTo(1)
        }

    @Test
    fun `permission grant enables daily beyt with immediate notification`() =
        runTest {
            val scheduler = FakeDailyBeytNotificationScheduler()
            val preferences = FakeUserPreferencesRepository()
            val permissionGateway = FakeNotificationPermissionGateway(granted = false)
            val viewModel =
                ProfileViewModel(
                    userRepository = FakeUserRepository(),
                    userPreferencesRepository = preferences,
                    dailyBeytNotificationScheduler = scheduler,
                    notificationPermissionGateway = permissionGateway,
                )

            viewModel.onAction(ProfileAction.OnDailyBeytNotificationToggle(enabled = true))
            viewModel.onAction(ProfileAction.OnNotificationPermissionResult(granted = true))

            assertThat(viewModel.state.value.isDailyBeytNotificationEnabled).isTrue()
            assertThat(scheduler.enableCallCount).isEqualTo(1)
            assertThat(scheduler.lastShowImmediately).isEqualTo(true)
        }

    private class FakeUserRepository : UserRepository {
        override suspend fun getUserInfo(): Result<UserInfo, DataError> =
            Result.Success(
                UserInfo(
                    completedLevel = GameLevel(id = 1, name = "مبتدی", totalScore = 100),
                    inProgressLevel = GameLevel(id = 2, name = "متوسط", totalScore = 200),
                    currentScore = 10,
                    streakNumber = 1,
                    poetsNumber = 2,
                    poemsNumber = 3,
                    badges = emptyList(),
                ),
            )
    }
}
