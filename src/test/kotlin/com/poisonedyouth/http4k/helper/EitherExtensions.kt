package com.poisonedyouth.http4k.helper

import arrow.core.Either
import com.poisonedyouth.http4k.failure.Failure
import org.junit.jupiter.api.fail

fun <T> Either<Failure, T>.getRightOrFail(): T =
    this.fold(
        {
            fail("Expected result to be right() but got left: $it")
        },
    ) {
        it
    }
