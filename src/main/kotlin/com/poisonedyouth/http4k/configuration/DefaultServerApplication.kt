package com.poisonedyouth.http4k.configuration

import arrow.core.Either
import com.poisonedyouth.http4k.failure.Failure
import org.http4k.server.Http4kServer
import org.slf4j.LoggerFactory

object DefaultServerApplication : ServerApplication {
    private val logger = LoggerFactory.getLogger("main")

    private lateinit var server: Either<Failure, Http4kServer>

    override fun start() {
        logger.info("Starting server...")
        installKoinApplication()
        server = installServer()
        installDatabase().fold(
            { failure ->
                logger.error("Unable to establish database connection because of '${failure.message}. Terminating server start...")
            },
        ) {
            server.fold(
                { failure ->
                    logger.error("Unable to configure server because of '${failure.message}. Terminating server start...")
                },
            ) {
                it.start()
                logger.info("Server started on port ${it.port()}.")
            }
        }
    }

    override fun stop() {
        server.onRight {
            logger.info("Stopping server...")
            it.stop()
        }
    }
}
