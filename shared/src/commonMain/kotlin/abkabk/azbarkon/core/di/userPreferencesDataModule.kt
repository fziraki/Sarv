package abkabk.azbarkon.core.di

import abkabk.azbarkon.data.repository.LocalUserPreferencesRepository
import abkabk.azbarkon.domain.repository.UserPreferencesRepository
import org.koin.dsl.module

val userPreferencesDataModule =
    module {
        single<UserPreferencesRepository> {
            LocalUserPreferencesRepository(
                keyValueStore = get(),
            )
        }
    }
