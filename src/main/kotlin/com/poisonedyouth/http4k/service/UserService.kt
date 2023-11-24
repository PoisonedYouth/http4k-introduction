package com.poisonedyouth.http4k.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import com.poisonedyouth.http4k.adapter.rest.NewUserDto
import com.poisonedyouth.http4k.adapter.rest.toUser
import com.poisonedyouth.http4k.domain.User
import com.poisonedyouth.http4k.domain.UserId
import com.poisonedyouth.http4k.domain.UserRepository
import com.poisonedyouth.http4k.domain.UserUseCase
import com.poisonedyouth.http4k.failure.Failure
import com.poisonedyouth.http4k.failure.NotFoundFailure

class UserService(
    private val userRepository: UserRepository,
) : UserUseCase {
    override fun create(user: NewUserDto): Either<Failure, User> =
        either {
            val validUser = user.toUser().bind()
            return userRepository.save(validUser)
        }

    override fun find(id: Int): Either<Failure, User> =
        either {
            val userId = UserId(id).bind()
            val existingUser = userRepository.findById(userId).bind()
            ensureNotNull(existingUser) {
                NotFoundFailure("User with id '$id' not found.")
            }
        }

    override fun delete(id: Int): Either<Failure, Unit> =
        either {
            val userId = UserId(id).bind()
            return userRepository.deleteById(userId)
        }

    override fun getAll(): Either<Failure, List<User>> {
        return userRepository.all()
    }
}
