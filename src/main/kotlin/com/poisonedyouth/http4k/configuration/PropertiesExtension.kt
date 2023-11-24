package com.poisonedyouth.http4k.configuration

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import com.poisonedyouth.http4k.failure.Failure
import com.poisonedyouth.http4k.failure.ValidationFailure
import java.util.Properties

fun Properties.getStringPropertyOrRaise(name: String): Either<Failure, String> =
    either {
        val stringProperty: String? = this@getStringPropertyOrRaise.getProperty(name)
        ensureNotNull(stringProperty) {
            ValidationFailure("Property with name '$name' does not exist.")
        }
        stringProperty
    }

fun Properties.getIntPropertyOrRaise(name: String): Either<Failure, Int> =
    either {
        val stringProperty: String? = this@getIntPropertyOrRaise.getProperty(name)
        ensureNotNull(stringProperty) {
            ValidationFailure("Property with name '$name' does not exist.")
        }
        val intProperty = stringProperty.toIntOrNull()
        ensureNotNull(intProperty) {
            ValidationFailure("Property with name '$name' is no valid integer '$stringProperty'.")
        }
        intProperty
    }
