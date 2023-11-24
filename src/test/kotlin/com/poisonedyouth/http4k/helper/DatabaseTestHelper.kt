package com.poisonedyouth.http4k.helper

import com.poisonedyouth.http4k.adapter.persistence.UserTable
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseTestHelper {
    fun clearDatabase() =
        transaction {
            connection.prepareStatement("TRUNCATE TABLE ${UserTable.tableName} RESTART IDENTITY", false).executeUpdate()
        }
}
