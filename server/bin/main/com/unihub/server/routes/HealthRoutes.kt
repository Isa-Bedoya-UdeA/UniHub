package com.unihub.server.routes

import com.unihub.server.models.HealthResponse
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.configureHealthRoutes() {
    val healthResponse = HealthResponse(status = "ok", service = "unihub-api")

    get("/health") {
        call.respond(HttpStatusCode.OK, healthResponse)
    }

    get("/api/health") {
        call.respond(HttpStatusCode.OK, healthResponse)
    }

    // Ultra-light ping endpoint returning raw text with zero overhead
    get("/ping") {
        call.respondText("OK", ContentType.Text.Plain, HttpStatusCode.OK)
    }
}
