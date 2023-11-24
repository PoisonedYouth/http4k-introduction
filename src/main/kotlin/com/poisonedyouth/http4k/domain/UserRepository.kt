package com.poisonedyouth.http4k.domain

import arrow.core.Either
import com.poisonedyouth.http4k.failure.Failure

interface UserRepository {
    fun save(user: NewUser): Either<Failure, User>

    fun findById(id: UserId): Either<Failure, User?>

    fun update(user: User): Either<Failure, Unit>

    fun deleteById(id: UserId): Either<Failure, Unit>

    fun all(): Either<Failure, List<User>>
}
