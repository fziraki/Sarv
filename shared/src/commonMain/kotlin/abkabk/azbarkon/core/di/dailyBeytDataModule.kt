package abkabk.azbarkon.core.di

import abkabk.azbarkon.data.local.SqlDelightDailyBeytLocalDataSource
import abkabk.azbarkon.data.repository.OfflineFirstDailyBeytRepository
import abkabk.azbarkon.domain.datasource.DailyBeytLocalDataSource
import abkabk.azbarkon.domain.repository.DailyBeytRepository
import org.koin.dsl.module

val dailyBeytDataModule =
    module {
        single<DailyBeytLocalDataSource> {
            SqlDelightDailyBeytLocalDataSource(
                verseQueries = get(),
            )
        }
        single<DailyBeytRepository> {
            OfflineFirstDailyBeytRepository(
                localDataSource = get(),
            )
        }
    }
