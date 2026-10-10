package com.unihub.server.routes

import com.unihub.server.auth.FirebaseTokenVerifier
import com.unihub.server.auth.authenticatedUser
import com.unihub.server.auth.authenticateFirebase
import com.unihub.server.models.ApiErrorCodes
import com.unihub.server.models.ApiErrorResponse
import com.unihub.server.models.ProfileResponse
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.configureProfileRoutes(tokenVerifier: FirebaseTokenVerifier) {
    route("/api") {
        authenticateFirebase(tokenVerifier) {
            get("/profile") {
                val user = call.authenticatedUser
                if (user == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiErrorResponse.create(
                            code = ApiErrorCodes.UNAUTHORIZED,
                            message = "User context not found"
                        )
                    )
                    return@get
                }

                call.respond(
                    HttpStatusCode.OK,
                    ProfileResponse(
                        uid = user.uid,
                        email = user.email,
                        displayName = user.displayName,
                        photoUrl = user.photoUrl
                    )
                )
            }
        }
    }
}
