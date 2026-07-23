package abkabk.azbarkon.core.di

import abkabk.azbarkon.core.local.DatabaseDriverFactory
import abkabk.azbarkon.core.local.MemorizationDatabaseDriverFactory
import abkabk.azbarkon.core.notifications.DailyBeytNotificationPresenter
import abkabk.azbarkon.core.notifications.DailyBeytWorker
import abkabk.azbarkon.core.notifications.MemorizationReviewNotificationPresenter
import abkabk.azbarkon.core.notifications.MemorizationReviewWorker
import abkabk.azbarkon.core.widget.RandomDistichWidgetPreferences
import abkabk.azbarkon.core.widget.RandomDistichWidgetRefresher
import abkabk.azbarkon.core.widget.RandomDistichWidgetUpdater
import abkabk.azbarkon.core.platform.ClipboardManager
import abkabk.azbarkon.core.platform.KeyValueStore
import abkabk.azbarkon.core.platform.ImageExportManager
import abkabk.azbarkon.core.platform.ShareManager
import abkabk.azbarkon.core.player.AudioPlayer
import abkabk.azbarkon.core.player.Media3AudioPlayer
import abkabk.azbarkon.data.platform.AndroidDailyBeytNotificationScheduler
import abkabk.azbarkon.data.platform.AndroidMemorizationReviewNotificationScheduler
import abkabk.azbarkon.data.platform.AndroidNotificationPermissionGateway
import abkabk.azbarkon.data.cache.CoilPoetImagePrefetcher
import abkabk.azbarkon.domain.datasource.PoetImagePrefetcher
import abkabk.azbarkon.domain.platform.DailyBeytNotificationScheduler
import abkabk.azbarkon.domain.platform.MemorizationReviewNotificationScheduler
import abkabk.azbarkon.domain.platform.NotificationPermissionGateway
import androidx.media3.exoplayer.ExoPlayer
import com.azbarkon.db.AzbarKonDatabase
import com.azbarkon.memorization.MemorizationDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val androidPlatformModule =
    module {
        single {
            DatabaseDriverFactory(
                context = androidContext(),
            )
        }

        single {
            AzbarKonDatabase(
                driver = get<DatabaseDriverFactory>().createDriver(),
            )
        }

        single {
            MemorizationDatabaseDriverFactory(
                context = androidContext(),
            )
        }

        single {
            MemorizationDatabase(
                driver = get<MemorizationDatabaseDriverFactory>().createDriver(),
            )
        }

        single {
            KeyValueStore(context = androidContext())
        }

        single<PoetImagePrefetcher> {
            CoilPoetImagePrefetcher(platformContext = androidContext())
        }

        single {
            ClipboardManager(context = androidContext())
        }

        single {
            ShareManager(context = androidContext())
        }

        single {
            ImageExportManager(context = androidContext())
        }

        single<DailyBeytNotificationScheduler> {
            AndroidDailyBeytNotificationScheduler(
                context = androidContext(),
                userPreferencesRepository = get(),
                dailyBeytRepository = get(),
                notificationPresenter = get(),
            )
        }

        single<NotificationPermissionGateway> {
            AndroidNotificationPermissionGateway(
                context = androidContext(),
            )
        }

        single<MemorizationReviewNotificationScheduler> {
            AndroidMemorizationReviewNotificationScheduler(
                context = androidContext(),
                localDataSource = get(),
            )
        }

        single {
            MemorizationReviewNotificationPresenter(
                context = androidContext(),
            )
        }

        single {
            DailyBeytNotificationPresenter(
                context = androidContext(),
            )
        }

        single {
            RandomDistichWidgetPreferences(
                context = androidContext(),
            )
        }

        single {
            RandomDistichWidgetUpdater()
        }

        single {
            RandomDistichWidgetRefresher()
        }

        workerOf(::DailyBeytWorker)
        workerOf(::MemorizationReviewWorker)

        single<AudioPlayer> {
            Media3AudioPlayer(ExoPlayer.Builder(androidContext()).build())
        }
    }
