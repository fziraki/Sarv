package abkabk.azbarkon.core.di

import abkabk.azbarkon.data.repository.LocalSavedPoemRepository
import abkabk.azbarkon.domain.repository.SavedPoemRepository
import org.koin.dsl.module

val savedPoemDataModule =
    module {
        single<SavedPoemRepository> {
            LocalSavedPoemRepository(
                keyValueStore = get(),
            )
        }
    }
