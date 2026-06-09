package abkabk.azbarkon.core.di

import abkabk.azbarkon.data.local.SqlDelightChatLocalDataSource
import abkabk.azbarkon.data.repository.OfflineFirstChatRepository
import abkabk.azbarkon.domain.datasource.ChatLocalDataSource
import abkabk.azbarkon.domain.repository.ChatRepository
import org.koin.dsl.module

val chatDataModule =
    module {
        single<ChatLocalDataSource> {
            SqlDelightChatLocalDataSource(
                verseQueries = get(),
            )
        }
        single<ChatRepository> {
            OfflineFirstChatRepository(
                localDataSource = get(),
            )
        }
    }
