package com.poisonedyouth.http4k.configuration

import arrow.core.Either
import arrow.core.raise.either
import com.poisonedyouth.http4k.adapter.rest.UserHttpHandler
import com.poisonedyouth.http4k.failure.Failure
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.ServerConfig
import org.http4k.server.asServer
import org.koin.java.KoinJavaComponent.inject
import java.time.Duration
import java.util.Properties

fun installServer(configurationFileName: String = "application.properties"): Either<Failure, Http4kServer> =
    either {
        val userHttpHandler: UserHttpHandler by inject(UserHttpHandler::class.java)
        val serverConfiguration = loadConfiguration(configurationFileName).bind()

        val config =
            Jetty(
                serverConfiguration.port,
                ServerConfig.StopMode.Graceful(Duration.ofSeconds(serverConfiguration.shutdownSeconds)),
            )
        DebuggingFilters.PrintRequestAndResponse()
            .then(customFilter)
            .then(userHttpHandler.userRoutes)
            .asServer(config)
    }

private data class ServerConfiguration(
    val port: Int,
    val shutdownSeconds: Long,
)

private fun loadConfiguration(configurationFileName: String): Either<Failure, ServerConfiguration> =
    either {
        val configFile = Properties()
        configFile.load(ServerConfiguration::class.java.classLoader.getResourceAsStream(configurationFileName))

        ServerConfiguration(
            port = configFile.getIntPropertyOrRaise("http4k.server.port").bind(),
            shutdownSeconds = configFile.getIntPropertyOrRaise("http4k.server.shutdown.seconds").bind().toLong(),
        )
    }
