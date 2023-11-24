package com.poisonedyouth.http4k.configuration

import com.poisonedyouth.http4k.adapter.rest.UserHttpHandler
import com.poisonedyouth.http4k.domain.UserUseCase
import com.poisonedyouth.http4k.helper.TestUserUseCase
import org.assertj.core.api.Assertions.assertThat
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

class UserHttpHandlerTest : KoinTest {
    private val userUseCase: UserUseCase by inject()
    private val userHttpHandler = UserHttpHandler(userUseCase)

    companion object {
        @BeforeAll
        @JvmStatic
        fun startApplication() {
            startKoin {
                modules(
                    module {
                        singleOf(::TestUserUseCase) bind UserUseCase::class
                    },
                )
            }
        }

        @AfterAll
        @JvmStatic
        fun stopApplication() {
            stopKoin()
        }
    }

    @Test
    fun `addUserHandler returns created user`() {
        // given + when
        val actual =
            userHttpHandler.addUserHandler(
                Request(Method.POST, "/user").body(
                    """
                    {
                        "firstName": "valid",
                        "lastName": "Doe",
                        "email": "john.doe@mail.com"
                    }
                    """.trimIndent(),
                ),
            )

        // then
        assertThat(actual.status).isEqualTo(Status.CREATED)
        JSONAssert.assertEquals(
            """
            {
                "id":1,
                "firstName":"valid",
                "lastName":"Doe",
                "email":"john.doe@mail.com"
            }
            """.trimIndent(),
            actual.body.stream.reader().readText(),
            JSONCompareMode.LENIENT,
        )
    }

    @Test
    fun `addUserHandler returns internal server error for generic failure`() {
        // given + when
        val actual =
            userHttpHandler.addUserHandler(
                Request(Method.POST, "/user").body(
                    """
                    {
                        "firstName": "genericFailure",
                        "lastName": "Doe",
                        "email": "mary.doe@mail.com"
                    }
                    """.trimIndent(),
                ),
            )

        // then
        assertThat(actual.status).isEqualTo(Status.INTERNAL_SERVER_ERROR)
        JSONAssert.assertEquals(
            """
            {
                "message": "Failed!"
            }
            """.trimIndent(),
            actual.body.stream.reader().readText(),
            JSONCompareMode.LENIENT,
        )
    }

    @Test
    fun `addUserHandler returns bad request for validation failure`() {
        // given + when
        val actual =
            userHttpHandler.addUserHandler(
                Request(Method.POST, "/user").body(
                    """
                    {
                        "firstName": "validationFailure",
                        "lastName": "Doe",
                        "email": "mary.doe@mail.com"
                    }
                    """.trimIndent(),
                ),
            )

        // then
        assertThat(actual.status).isEqualTo(Status.BAD_REQUEST)
        JSONAssert.assertEquals(
            """
            {
                "message": "Failed!"
            }
            """.trimIndent(),
            actual.body.stream.reader().readText(),
            JSONCompareMode.LENIENT,
        )
    }
}
