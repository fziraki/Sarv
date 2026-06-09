package abkabk.azbarkon.core.di

import org.koin.core.module.Module

val sharedModules: List<Module> =
    listOf(
        networkModule,
        databaseModule,
        poetsDataModule,
        poemDataModule,
        dailyBeytDataModule,
        searchDataModule,
        savedPoemDataModule,
        chatDataModule,
        userDataModule,
        userPreferencesDataModule,
        homePresentationModule,
        profilePresentationModule,
        poetsPresentationModule,
        searchPresentationModule,
        chatPresentationModule,
    )
