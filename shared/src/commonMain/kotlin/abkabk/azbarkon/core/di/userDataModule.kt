package abkabk.azbarkon.core.di

import abkabk.azbarkon.data.repository.StubUserRepository
import abkabk.azbarkon.domain.repository.UserRepository
import org.koin.dsl.module

val userDataModule =
    module {
        single<UserRepository> {
            StubUserRepository()
        }
    }
