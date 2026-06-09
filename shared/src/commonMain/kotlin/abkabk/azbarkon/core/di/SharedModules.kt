package abkabk.azbarkon.core.di

import org.koin.core.module.Module

val sharedModules: List<Module> =
    listOf(
        networkModule,
        databaseModule,
        poetsDataModule,
        poemDataModule,
        searchDataModule,
        savedPoemDataModule,
        userDataModule,
        homePresentationModule,
        profilePresentationModule,
        poetsPresentationModule,
        searchPresentationModule,
    )
