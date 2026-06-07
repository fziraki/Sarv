package abkabk.azbarkon.core.di

import org.koin.core.module.Module

val sharedModules: List<Module> =
    listOf(
        networkModule,
        databaseModule,
        poetsDataModule,
        poemDataModule,
        userDataModule,
        homePresentationModule,
        profilePresentationModule,
        poetsPresentationModule,
    )
