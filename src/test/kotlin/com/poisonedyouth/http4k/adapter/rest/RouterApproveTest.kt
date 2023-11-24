package com.poisonedyouth.http4k.adapter.rest

import com.poisonedyouth.http4k.adapter.TestServerApplication
import com.poisonedyouth.http4k.domain.Email
import com.poisonedyouth.http4k.domain.Name
import com.poisonedyouth.http4k.domain.NewUser
import com.poisonedyouth.http4k.domain.UserRepository
import com.poisonedyouth.http4k.domain.UserUseCase
import com.poisonedyouth.http4k.helper.DatabaseTestHelper
import com.poisonedyouth.http4k.helper.getRightOrFail
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.testing.Approver
import org.http4k.testing.JsonApprovalTest
import org.http4k.testing.assertApproved
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.koin.test.KoinTest
import org.koin.test.inject

@ExtendWith(JsonApprovalTest::class)
class RouterApproveTest : KoinTest {
    private val userRepository: UserRepository by inject()
    private val userUseCase: UserUseCase by inject()
    private val userHttpHandler = UserHttpHandler(userUseCase)

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
    fun `create user - check response content`(approver: Approver) {
        approver.assertApproved(
            userHttpHandler.addUserHandler(
                Request(Method.POST, "/user").body(
                    """
                    {
                        "firstName": "John",
                        "lastName": "Doe",
                        "email": "john.doe@mail.com"
                    }
                    """.trimIndent(),
                ),
            ),
            Status.CREATED,
        )
    }

    @Test
    fun `get all user - check response content`(approver: Approver) {
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
        approver.assertApproved(
            userHttpHandler.getAllUserHandler(
                Request(Method.GET, "/user"),
            ),
            Status.OK,
        )
    }

    @Test
    fun `get user - check response content`(approver: Approver) {
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
        approver.assertApproved(
            userHttpHandler.getSingleUserHandler(
                Request(Method.GET, "/user").query("id", "1"),
            ),
            Status.OK,
        )
    }

    @Test
    fun `get user - not available - check response content`(approver: Approver) {
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
        approver.assertApproved(
            userHttpHandler.getSingleUserHandler(
                Request(Method.GET, "/user").query("id", "3"),
            ),
            Status.NOT_FOUND,
        )
    }

    @Test
    fun `get user - invalid id - check response content`(approver: Approver) {
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
        approver.assertApproved(
            userHttpHandler.getSingleUserHandler(
                Request(Method.GET, "/user").query("id", "invalid"),
            ),
            Status.BAD_REQUEST,
        )
    }

    @Test
    fun `delete user - check response content`(approver: Approver) {
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
        approver.assertApproved(
            userHttpHandler.deleteUserHandler(
                Request(Method.DELETE, "/user").query("id", "1"),
            ),
            Status.ACCEPTED,
        )
    }
}
