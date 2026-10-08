package com.unihub.server.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.configureHealthRoutes() {
    get("/health") {
        call.respond(
            HttpStatusCode.OK,
            mapOf("status" to "ok")
        )
    }
}
