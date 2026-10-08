package com.unihub.server.auth

import com.unihub.server.models.ApiErrorCodes
import com.unihub.server.models.ApiErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*

val AuthenticatedUserKey = AttributeKey<AuthenticatedUser>("AuthenticatedUser")

val ApplicationCall.authenticatedUser: AuthenticatedUser?
    get() = attributes.getOrNull(AuthenticatedUserKey)

fun ApplicationCall.requireAuthenticatedUser(): AuthenticatedUser {
    return authenticatedUser ?: throw AuthenticationException("Authentication required")
}

class AuthenticationException(message: String) : RuntimeException(message)

fun Route.authenticateFirebase(
    verifier: FirebaseTokenVerifier,
    build: Route.() -> Unit
): Route {
    val authenticatedRoute = createChild(object : RouteSelector() {
        override suspend fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
            return RouteSelectorEvaluation.Transparent
        }
    })

    authenticatedRoute.intercept(ApplicationCallPipeline.Plugins) {
        val authHeader = call.request.header(HttpHeaders.Authorization)
        if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ", ignoreCase = true)) {
            call.respond(
                HttpStatusCode.Unauthorized,
                ApiErrorResponse.create(
                    code = ApiErrorCodes.UNAUTHORIZED,
                    message = "Missing or invalid Authorization header"
                )
            )
            finish()
            return@intercept
        }

        val token = authHeader.substringAfter("Bearer ", "").trim()
        val result = verifier.verifyToken(token)

        result.fold(
            onSuccess = { user ->
                call.attributes.put(AuthenticatedUserKey, user)
                proceed()
            },
            onFailure = {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ApiErrorResponse.create(
                        code = ApiErrorCodes.UNAUTHORIZED,
                        message = "Invalid or expired authentication token"
                    )
                )
                finish()
            }
        )
    }

    authenticatedRoute.build()
    return authenticatedRoute
}
