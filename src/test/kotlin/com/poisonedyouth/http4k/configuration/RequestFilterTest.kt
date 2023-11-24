package com.poisonedyouth.http4k.configuration

import org.assertj.core.api.Assertions.assertThat
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.then
import org.junit.jupiter.api.Test

class RequestFilterTest {
    @Test
    fun `customFilter is adding missing x-target header`() {
        // given
        val handler: HttpHandler = customFilter.then { Response(Status.OK) }

        // when
        val response: Response = handler(Request(Method.GET, "/users"))

        // then
        assertThat(response.header("x-target")).isEqualTo("target")
    }

    @Test
    fun `customFilter not overrides header when already exists`() {
        // given
        val handler: HttpHandler = customFilter.then { Response(Status.OK) }

        // when
        val response: Response = handler(Request(Method.GET, "/users").header("x-target", "other"))

        // then
        assertThat(response.header("x-target")).isEqualTo("other")
    }
}
