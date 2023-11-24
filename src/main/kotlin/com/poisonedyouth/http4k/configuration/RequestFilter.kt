package com.poisonedyouth.http4k.configuration

import org.http4k.core.Filter

val customFilter =
    Filter { httpHandler ->
        { request ->
            if (request.header("x-target") == null) {
                httpHandler(request).header("x-target", "target")
            } else {
                httpHandler(request).header("x-target", request.header("x-target"))
            }
        }
    }
