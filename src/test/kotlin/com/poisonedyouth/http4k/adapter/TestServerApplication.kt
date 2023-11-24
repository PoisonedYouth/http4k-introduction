package com.poisonedyouth.http4k.adapter

import com.poisonedyouth.http4k.configuration.ServerApplication
import com.poisonedyouth.http4k.configuration.installDatabase
import com.poisonedyouth.http4k.configuration.installKoinApplication
import com.poisonedyouth.http4k.configuration.installServer
import com.poisonedyouth.http4k.configuration.stopKoinApplication
import com.poisonedyouth.http4k.helper.getRightOrFail
import org.http4k.server.Http4kServer

object TestServerApplication : ServerApplication {
    private lateinit var server: Http4kServer

    override fun start() {
        installKoinApplication()
        server = installServer("application-test.properties").getRightOrFail()
        installDatabase("application-test.properties")
        server.start()
    }

    override fun stop() {
        stopKoinApplication()
        server.stop()
    }
}
