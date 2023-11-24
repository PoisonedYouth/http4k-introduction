package com.poisonedyouth.http4k.adapter

import com.poisonedyouth.http4k.domain.Email
import com.poisonedyouth.http4k.domain.Name
import com.poisonedyouth.http4k.domain.NewUser
import com.poisonedyouth.http4k.domain.UserRepository
import com.poisonedyouth.http4k.helper.DatabaseTestHelper
import com.poisonedyouth.http4k.helper.getRightOrFail
import org.assertj.core.api.Assertions.assertThat
import org.http4k.client.JavaHttpClient
import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.test.KoinTest
import org.koin.test.inject
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

class RouterApplicationTest : KoinTest {
    private val userRepository: UserRepository by inject()

    companion object {
        @BeforeAll
        @JvmStatic
        fun startApplication() {
            TestServerApplication.start()
        }

        @AfterAll
        @JvmStatic
        fun stopApplication() {
            TestServerApplication.stop()
        }
    }

    @BeforeEach
    fun cleanDatabase() {
        DatabaseTestHelper.clearDatabase()
    }

    @Test
    fun `create user request is creating new user`() {
        // given
        val request =
            Request(Method.POST, "http://localhost:9999/user")
                .header(
                    "content-type",
                    ContentType.APPLICATION_JSON.value,
                )
                .body(
                    """
                    {
                        "firstName": "John",
                        "lastName": "Doe",
                        "email": "john.doe@mail.com"
                    }
                    """.trimIndent(),
                )

        val client: HttpHandler = JavaHttpClient()

        // when
        val actual = client(request)

        // then
        assertThat(actual.status).isEqualTo(Status.CREATED)
        JSONAssert.assertEquals(
            """
            {
                "id":1,
                "firstName":"John",
                "lastName":"Doe",
                "email":"john.doe@mail.com"
            }
            """.trimIndent(),
            actual.body.stream.reader().readText(),
            JSONCompareMode.LENIENT,
        )
    }

    @Test
    fun `delete user request is removing existing user`() {
        // given
        userRepository.save(
            NewUser(
                name = Name("John", "Doe").getRightOrFail(),
                email = Email("john.doe@mail.com").getRightOrFail(),
            ),
        )

        val request =
            Request(Method.DELETE, "http://localhost:9999/user")
                .query("id", "1")

        val client: HttpHandler = JavaHttpClient()

        // when
        val actual = client(request)

        // then
        assertThat(actual.status).isEqualTo(Status.ACCEPTED)
        assertThat(actual.body.stream.reader().readText())
            .isEqualTo("{\"message\":\"Deleted user.\"}")
    }

    @Test
    fun `get user request is returning existing user`() {
        // given
        userRepository.save(
            NewUser(
                name = Name("John", "Doe").getRightOrFail(),
                email = Email("john.doe@mail.com").getRightOrFail(),
            ),
        )

        val request =
            Request(Method.GET, "http://localhost:9999/user")
                .query("id", "1")

        val client: HttpHandler = JavaHttpClient()

        // when
        val actual = client(request)

        // then
        assertThat(actual.status).isEqualTo(Status.OK)
        assertThat(actual.body.stream.reader().readText())
            .isEqualTo("{\"id\":1,\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@mail.com\"}")
    }

    @Test
    fun `get user request is returning bad request for invalid parameter`() {
        // given
        userRepository.save(
            NewUser(
                name = Name("John", "Doe").getRightOrFail(),
                email = Email("john.doe@mail.com").getRightOrFail(),
            ),
        )

        val request =
            Request(Method.GET, "http://localhost:9999/user")
                .query("id", "invalid")

        val client: HttpHandler = JavaHttpClient()

        // when
        val actual = client(request)

        // then
        assertThat(actual.status).isEqualTo(Status.BAD_REQUEST)
        JSONAssert.assertEquals(
            """
            {
              "message":"Parameter 'id' is of wrong type."
            }
            """.trimIndent(),
            actual.body.stream.reader().readText(),
            JSONCompareMode.LENIENT,
        )
    }

    @Test
    fun `get all user request is returning all existing user`() {
        // given
        userRepository.save(
            NewUser(
                name = Name("John", "Doe").getRightOrFail(),
                email = Email("john.doe@mail.com").getRightOrFail(),
            ),
        )
        userRepository.save(
            NewUser(
                name = Name("Max", "Cavalera").getRightOrFail(),
                email = Email("max.cavalera@mail.com").getRightOrFail(),
            ),
        )

        val request = Request(Method.GET, "http://localhost:9999/user/all")

        val client: HttpHandler = JavaHttpClient()

        // when
        val actual = client(request)

        // then
        assertThat(actual.status).isEqualTo(Status.OK)
        JSONAssert.assertEquals(
            """
            [
                {
                    "id":1,
                    "firstName":"John",
                    "lastName":"Doe",
                    "email":"john.doe@mail.com"
                },
                {
                    "id":2,
                    "firstName":"Max",
                    "lastName":"Cavalera",
                    "email":"max.cavalera@mail.com"
                }
            ]
            """.trimIndent(),
            actual.body.stream.reader().readText(),
            JSONCompareMode.LENIENT,
        )
    }
}
