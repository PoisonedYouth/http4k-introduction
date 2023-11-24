package com.poisonedyouth.http4k.configuration

import com.poisonedyouth.http4k.adapter.persistence.ExposedUserRepository
import com.poisonedyouth.http4k.adapter.rest.UserHttpHandler
import com.poisonedyouth.http4k.domain.UserRepository
import com.poisonedyouth.http4k.domain.UserUseCase
import com.poisonedyouth.http4k.service.UserService
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val userModule =
    module {
        singleOf(::ExposedUserRepository) bind UserRepository::class
        singleOf(::UserService) bind UserUseCase::class
        single { UserHttpHandler(get()) }
    }

fun installKoinApplication() {
    startKoin {
        modules(userModule)
    }
}

fun stopKoinApplication() {
    stopKoin()
}
