package com.poisonedyouth.http4k.domain

import arrow.core.Either
import com.poisonedyouth.http4k.adapter.rest.NewUserDto
import com.poisonedyouth.http4k.failure.Failure

interface UserUseCase {
    fun create(user: NewUserDto): Either<Failure, User>

    fun find(id: Int): Either<Failure, User>

    fun delete(id: Int): Either<Failure, Unit>

    fun getAll(): Either<Failure, List<User>>
}
