package com.poisonedyouth.http4k.domain

import arrow.core.Either
import com.poisonedyouth.http4k.domain.validation.accessors.first
import com.poisonedyouth.http4k.domain.validation.accessors.last
import com.poisonedyouth.http4k.domain.validation.accessors.value
import com.poisonedyouth.http4k.failure.Failure
import com.poisonedyouth.http4k.failure.toEither
import dev.nesk.akkurate.Validator
import dev.nesk.akkurate.annotations.Validate
import dev.nesk.akkurate.constraints.builders.isNotEmpty
import dev.nesk.akkurate.constraints.builders.isPositive
import dev.nesk.akkurate.constraints.constrain
import dev.nesk.akkurate.constraints.otherwise

data class User(
    val id: UserId,
    val name: Name,
    val email: Email,
)

data class NewUser(
    val name: Name,
    val email: Email,
)

private val emailValidator =
    Validator<Email> {
        this.value.constrain { emailString ->
            emailString.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$".toRegex())
        } otherwise { "${this.value.unwrap()} must be a valid email." }
    }

private val userIdValidator =
    Validator<UserId> {
        this.value.isPositive() otherwise { "${this.value.unwrap()} must be positive." }
    }

private val nameValidator =
    Validator<Name> {
        this.first.isNotEmpty() otherwise { "Firstname must not be empty." }
        this.last.isNotEmpty() otherwise { "Lastname must not be empty." }
    }

@JvmInline
@Validate
value class Email private constructor(val value: String) {
    companion object {
        operator fun invoke(value: String): Either<Failure, Email> {
            return emailValidator(Email(value)).toEither()
        }
    }
}

@JvmInline
@Validate
value class UserId private constructor(val value: Int) {
    companion object {
        operator fun invoke(value: Int): Either<Failure, UserId> {
            return userIdValidator(UserId((value))).toEither()
        }
    }
}

@Validate
class Name private constructor(val first: String, val last: String) {
    companion object {
        operator fun invoke(
            firstName: String,
            lastName: String,
        ): Either<Failure, Name> {
            return nameValidator(Name(firstName, lastName)).toEither()
        }
    }
}
