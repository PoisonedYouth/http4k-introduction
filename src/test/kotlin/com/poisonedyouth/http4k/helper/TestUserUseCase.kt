package com.poisonedyouth.http4k.helper

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.poisonedyouth.http4k.adapter.rest.NewUserDto
import com.poisonedyouth.http4k.domain.Email
import com.poisonedyouth.http4k.domain.Name
import com.poisonedyouth.http4k.domain.User
import com.poisonedyouth.http4k.domain.UserId
import com.poisonedyouth.http4k.domain.UserUseCase
import com.poisonedyouth.http4k.failure.Failure
import com.poisonedyouth.http4k.failure.GenericFailure
import com.poisonedyouth.http4k.failure.ValidationFailure

class TestUserUseCase : UserUseCase {
    override fun create(user: NewUserDto): Either<Failure, User> {
        return when (user.firstName) {
            "valid" ->
                User(
                    id = UserId(1).getRightOrFail(),
                    name = Name(user.firstName, user.lastName).getRightOrFail(),
                    email = Email(user.email).getRightOrFail(),
                ).right()

            "validationFailure" -> ValidationFailure(message = "Failed!").left()

            else -> GenericFailure(message = "Failed!", throwable = RuntimeException("Failed!")).left()
        }
    }

    override fun find(id: Int): Either<Failure, User> {
        TODO("Not yet implemented")
    }

    override fun delete(id: Int): Either<Failure, Unit> {
        TODO("Not yet implemented")
    }

    override fun getAll(): Either<Failure, List<User>> {
        TODO("Not yet implemented")
    }
}
