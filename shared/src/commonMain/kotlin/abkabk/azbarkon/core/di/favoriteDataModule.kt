package abkabk.azbarkon.core.di

import abkabk.azbarkon.data.repository.LocalFavoritePoemRepository
import abkabk.azbarkon.domain.repository.FavoritePoemRepository
import org.koin.dsl.module

val favoriteDataModule =
    module {
        single<FavoritePoemRepository> {
            LocalFavoritePoemRepository(
                keyValueStore = get(),
            )
        }
    }
