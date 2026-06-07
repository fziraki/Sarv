package abkabk.azbarkon.core.di

import abkabk.azbarkon.data.local.SqlDelightPoemLocalDataSource
import abkabk.azbarkon.data.platform.PlatformClipboardService
import abkabk.azbarkon.data.platform.PlatformShareService
import abkabk.azbarkon.data.repository.OfflineFirstPoemRepository
import abkabk.azbarkon.domain.datasource.PoemLocalDataSource
import abkabk.azbarkon.domain.platform.ClipboardService
import abkabk.azbarkon.domain.platform.ShareService
import abkabk.azbarkon.domain.repository.PoemRepository
import org.koin.dsl.module

val poemDataModule =
    module {
        single<ClipboardService> {
            PlatformClipboardService(clipboardManager = get())
        }

        single<ShareService> {
            PlatformShareService(shareManager = get())
        }
        single<PoemLocalDataSource> {
            SqlDelightPoemLocalDataSource(
                poemQueries = get(),
                verseQueries = get(),
            )
        }
        single<PoemRepository> {
            OfflineFirstPoemRepository(
                localDataSource = get(),
            )
        }
    }
