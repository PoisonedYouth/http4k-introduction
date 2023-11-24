package com.poisonedyouth.http4k.adapter.rest

import arrow.core.Either
import arrow.core.raise.either
import com.poisonedyouth.http4k.domain.Email
import com.poisonedyouth.http4k.domain.Name
import com.poisonedyouth.http4k.domain.NewUser
import com.poisonedyouth.http4k.domain.User
import com.poisonedyouth.http4k.failure.Failure

data class NewUserDto(
    val firstName: String,
    val lastName: String,
    val email: String,
)

fun NewUserDto.toUser(): Either<Failure, NewUser> =
    either {
        NewUser(
            name = Name(this@toUser.firstName, this@toUser.lastName).bind(),
            email = Email(this@toUser.email).bind(),
        )
    }

data class UserDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
)

fun User.toUserDto() =
    UserDto(
        id = this.id.value,
        firstName = this.name.first,
        lastName = this.name.last,
        email = this.email.value,
    )
