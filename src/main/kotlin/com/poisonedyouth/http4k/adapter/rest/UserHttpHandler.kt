package com.poisonedyouth.http4k.adapter.rest

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import com.poisonedyouth.http4k.domain.UserUseCase
import com.poisonedyouth.http4k.failure.Failure
import com.poisonedyouth.http4k.failure.GenericFailure
import com.poisonedyouth.http4k.failure.NotFoundFailure
import com.poisonedyouth.http4k.failure.ValidationFailure
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

private val newUserDtoLens = Body.auto<NewUserDto>().toLens()
private val userDtoLens = Body.auto<UserDto>().toLens()
private val allUserDtoLens = Body.auto<List<UserDto>>().toLens()
private val errorDtoLens = Body.auto<ErrorDto>().toLens()
private val messageResultDtoLens = Body.auto<MessageResultDto>().toLens()

class UserHttpHandler(
    private val userUseCase: UserUseCase,
) {
    val addUserHandler: HttpHandler =
        { request ->
            val newUserDto = newUserDtoLens(request)
            userUseCase.create(newUserDto).fold(
                { failure ->
                    handleFailure(failure)
                },
            ) { user ->
                Response(Status.CREATED).with(userDtoLens of user.toUserDto())
            }
        }

    val getAllUserHandler: HttpHandler =
        {
            userUseCase.getAll().fold(
                { failure ->
                    handleFailure(failure)
                },
            ) { userList ->
                Response(Status.OK).with(allUserDtoLens of userList.map { it.toUserDto() })
            }
        }

    val deleteUserHandler: HttpHandler =
        {
            it.handleIntParameter("id").fold(
                { failure ->
                    handleFailure(failure)
                },
            ) { id ->
                handleValidDeleteRequest(id)
            }
        }

    val getSingleUserHandler: HttpHandler =
        {
            it.handleIntParameter("id").fold(
                { failure ->
                    handleFailure(failure)
                },
            ) { id ->
                handleValidGetRequest(id)
            }
        }

    val userRoutes: RoutingHttpHandler =
        routes(
            "/user" bind Method.POST to addUserHandler,
            "/user/all" bind Method.GET to getAllUserHandler,
            "/user" bind Method.GET to getSingleUserHandler,
            "/user" bind Method.DELETE to deleteUserHandler,
        )

    private fun handleValidGetRequest(id: Int): Response {
        return userUseCase.find(id).fold(
            { failure ->
                handleFailure(failure)
            },
        ) { user ->
            Response(Status.OK).with(userDtoLens of user.toUserDto())
        }
    }

    private fun handleValidDeleteRequest(id: Int): Response {
        return userUseCase.delete(id).fold(
            { failure ->
                handleFailure(failure)
            },
        ) { _ ->
            Response(Status.ACCEPTED).with(messageResultDtoLens of MessageResultDto("Deleted user."))
        }
    }

    private fun handleFailure(failure: Failure): Response {
        val status =
            when (failure) {
                is GenericFailure -> Status.INTERNAL_SERVER_ERROR
                is ValidationFailure -> Status.BAD_REQUEST
                is NotFoundFailure -> Status.NOT_FOUND
            }
        return Response(status).with(
            errorDtoLens of
                ErrorDto(
                    message = failure.message,
                ),
        )
    }

    private fun Request.handleIntParameter(name: String): Either<Failure, Int> =
        either {
            val result = this@handleIntParameter.query(name)
            ensureNotNull(result) {
                ValidationFailure("Missing parameter '$name'.")
            }
            val intResult = result.toIntOrNull()
            ensureNotNull(intResult) {
                ValidationFailure("Parameter '$name' is of wrong type.")
            }
            intResult
        }
}
