package com.poisonedyouth.http4k.adapter.persistence

import arrow.core.Either
import arrow.core.raise.either
import com.poisonedyouth.http4k.domain.Email
import com.poisonedyouth.http4k.domain.Name
import com.poisonedyouth.http4k.domain.NewUser
import com.poisonedyouth.http4k.domain.User
import com.poisonedyouth.http4k.domain.UserId
import com.poisonedyouth.http4k.domain.UserRepository
import com.poisonedyouth.http4k.failure.Failure
import com.poisonedyouth.http4k.failure.evalInTransaction
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class ExposedUserRepository : UserRepository {
    override fun save(user: NewUser): Either<Failure, User> =
        either {
            evalInTransaction {
                val id =
                    UserTable.insertAndGetId {
                        it[firstname] = user.name.first
                        it[lastname] = user.name.last
                        it[email] = user.email.value
                    }.value
                User(
                    id = UserId(id).bind(),
                    name = user.name,
                    email = user.email,
                )
            }.bind()
        }

    override fun findById(id: UserId): Either<Failure, User?> =
        either {
            evalInTransaction {
                UserTable.select {
                    UserTable.id eq id.value
                }.singleOrNull()?.mapRowToUser()?.bind()
            }.bind()
        }

    private fun ResultRow.mapRowToUser(): Either<Failure, User> =
        either {
            User(
                id = UserId(this@mapRowToUser[UserTable.id].value).bind(),
                name = Name(this@mapRowToUser[UserTable.firstname], this@mapRowToUser[UserTable.lastname]).bind(),
                email = Email(this@mapRowToUser[UserTable.email]).bind(),
            )
        }

    override fun update(user: User): Either<Failure, Unit> =
        evalInTransaction {
            UserTable.update({ UserTable.id eq user.id.value }) {
                it[firstname] = user.name.first
                it[lastname] = user.name.last
                it[email] = user.email.value
            }
        }

    override fun deleteById(id: UserId): Either<Failure, Unit> =
        evalInTransaction {
            UserTable.deleteWhere {
                UserTable.id eq id.value
            }
        }

    override fun all(): Either<Failure, List<User>> =
        either {
            evalInTransaction {
                UserTable.selectAll().map { it.mapRowToUser().bind() }
            }.bind()
        }
}

object UserTable : IntIdTable("app_user") {
    val firstname = varchar("firstname", 255)
    val lastname = varchar("lastname", 255)
    val email = varchar("email", 255)
}
