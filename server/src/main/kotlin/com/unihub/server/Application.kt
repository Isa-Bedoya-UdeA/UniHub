package com.unihub.server

import com.unihub.server.auth.AuthenticationException
import com.unihub.server.auth.FirebaseAdminTokenVerifier
import com.unihub.server.auth.FirebaseTokenVerifier
import com.unihub.server.config.EnvConfig
import com.unihub.server.models.ApiErrorCodes
import com.unihub.server.models.ApiErrorResponse
import com.unihub.server.routes.configureAcademicRoutes
import com.unihub.server.routes.configureAiRoutes
import com.unihub.server.routes.configureHealthRoutes
import com.unihub.server.routes.configureProfileRoutes
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    System.setProperty("java.net.preferIPv4Stack", "true")
    val port = EnvConfig.get("PORT").toIntOrNull() ?: 8080
    val host = EnvConfig.get("HOST", "0.0.0.0")

    embeddedServer(Netty, port = port, host = host) {
        configureServer()
    }.start(wait = true)
}

fun Application.configureServer(
    tokenVerifier: FirebaseTokenVerifier = FirebaseAdminTokenVerifier()
) {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
    }

    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ApiErrorResponse.create(
                    code = ApiErrorCodes.VALIDATION_ERROR,
                    message = cause.message ?: "Invalid request format"
                )
            )
        }
        exception<AuthenticationException> { call, cause ->
            call.respond(
                HttpStatusCode.Unauthorized,
                ApiErrorResponse.create(
                    code = ApiErrorCodes.UNAUTHORIZED,
                    message = cause.message ?: "Authentication failed"
                )
            )
        }
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ApiErrorResponse.create(
                    code = ApiErrorCodes.VALIDATION_ERROR,
                    message = cause.message ?: "Invalid request parameters"
                )
            )
        }
        exception<Throwable> { call, _ ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiErrorResponse.create(
                    code = ApiErrorCodes.INTERNAL_ERROR,
                    message = "An internal server error occurred"
                )
            )
        }
    }

    routing {
        configureHealthRoutes()
        configureProfileRoutes(tokenVerifier)
        configureAcademicRoutes(tokenVerifier)
        configureAiRoutes(tokenVerifier)
    }
}
