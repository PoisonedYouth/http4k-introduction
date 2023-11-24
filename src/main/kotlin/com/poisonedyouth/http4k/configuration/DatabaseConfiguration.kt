package com.poisonedyouth.http4k.configuration

import arrow.core.Either
import arrow.core.raise.either
import com.poisonedyouth.http4k.adapter.persistence.UserTable
import com.poisonedyouth.http4k.failure.Failure
import com.poisonedyouth.http4k.failure.eval
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.Properties

private data class DatabaseConfiguration(
    val jdbcUrl: String,
    val user: String,
    val password: String,
    val driverName: String,
    val hikariConfiguration: HikariConfiguration,
)

private data class HikariConfiguration(
    val minimumIdle: Int,
    val maximumPoolSize: Int,
)

fun installDatabase(configurationFileName: String = "application.properties"): Either<Failure, Unit> =
    either {
        val databaseConfiguration = loadConfiguration(configurationFileName).bind()

        val hikariConfig =
            HikariConfig().apply {
                jdbcUrl = databaseConfiguration.jdbcUrl
                username = databaseConfiguration.user
                password = databaseConfiguration.password
                driverClassName = databaseConfiguration.driverName
                minimumIdle = databaseConfiguration.hikariConfiguration.minimumIdle
                maximumPoolSize = databaseConfiguration.hikariConfiguration.maximumPoolSize
            }
        val dataSource = HikariDataSource(hikariConfig)

        Database.connect(dataSource)
        transaction {
            SchemaUtils.create(UserTable)
        }
    }

private fun loadConfiguration(configurationFileName: String): Either<Failure, DatabaseConfiguration> =
    either {
        val configFile = Properties()
        eval {
            configFile.load(DatabaseConfiguration::class.java.classLoader.getResourceAsStream(configurationFileName))
        }.bind()

        val hikariConfiguration =
            HikariConfiguration(
                minimumIdle = configFile.getIntPropertyOrRaise("http4k.database.hikari.connections.minimumIdle").bind(),
                maximumPoolSize =
                    configFile.getIntPropertyOrRaise("http4k.database.hikari.connections.maximumPoolSize")
                        .bind(),
            )

        DatabaseConfiguration(
            jdbcUrl = configFile.getStringPropertyOrRaise("http4k.database.url").bind(),
            user = configFile.getStringPropertyOrRaise("http4k.database.user").bind(),
            password = configFile.getStringPropertyOrRaise("http4k.database.password").bind(),
            driverName = configFile.getStringPropertyOrRaise("http4k.database.drivername").bind(),
            hikariConfiguration = hikariConfiguration,
        )
    }
