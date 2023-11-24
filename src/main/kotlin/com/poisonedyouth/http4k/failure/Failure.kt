package com.poisonedyouth.http4k.failure

import arrow.core.Either
import dev.nesk.akkurate.ValidationResult
import org.jetbrains.exposed.sql.transactions.transaction

sealed interface Failure {
    val message: String
}

data class GenericFailure(
    override val message: String,
    val throwable: Throwable,
) : Failure

data class ValidationFailure(
    override val message: String,
) : Failure

data class NotFoundFailure(
    override val message: String,
) : Failure

fun <T> ValidationResult<T>.toEither(): Either<Failure, T> =
    when (this) {
        is ValidationResult.Success<T> -> Either.Right(this.value)
        is ValidationResult.Failure -> {
            Either.Left(ValidationFailure(this.violations.joinToString { it.message }))
        }
    }

fun <T> evalInTransaction(exec: () -> T): Either<Failure, T> {
    return Either.catch {
        transaction {
            exec()
        }
    }.mapLeft {
        GenericFailure("Failed to execute operation", it)
    }
}

fun <T> eval(exec: () -> T): Either<Failure, T> {
    return Either.catch {
        exec()
    }.mapLeft {
        GenericFailure("Failed to execute operation", it)
    }
}
