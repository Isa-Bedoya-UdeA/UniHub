package com.unihub.server.routes

import com.unihub.server.auth.FirebaseTokenVerifier
import com.unihub.server.auth.authenticatedUser
import com.unihub.server.auth.authenticateFirebase
import com.unihub.server.models.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.math.round

fun Routing.configureAcademicRoutes(tokenVerifier: FirebaseTokenVerifier) {
    route("/api/academic") {
        authenticateFirebase(tokenVerifier) {
            get("/summary") {
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
                    AcademicSummaryResponse(
                        uid = user.uid,
                        activeProgram = "Ingeniería de Sistemas",
                        activePeriod = "2025-1",
                        approvedCredits = 0,
                        totalCredits = 160,
                        cumulativeGpa = 0.0,
                        subjectsCount = 0
                    )
                )
            }

            post("/calculate") {
                val user = call.authenticatedUser
                if (user == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiErrorResponse.create(
                            code = ApiErrorCodes.UNAUTHORIZED,
                            message = "User context not found"
                        )
                    )
                    return@post
                }

                val request = try {
                    call.receive<AcademicCalculateRequest>()
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiErrorResponse.create(
                            code = ApiErrorCodes.VALIDATION_ERROR,
                            message = "Malformed request payload"
                        )
                    )
                    return@post
                }

                if (request.currentGrade < 0.0 || request.currentGrade > 5.0) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiErrorResponse.create(
                            code = ApiErrorCodes.VALIDATION_ERROR,
                            message = "currentGrade must be between 0.0 and 5.0"
                        )
                    )
                    return@post
                }

                if (request.targetGrade < 0.0 || request.targetGrade > 5.0) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiErrorResponse.create(
                            code = ApiErrorCodes.VALIDATION_ERROR,
                            message = "targetGrade must be between 0.0 and 5.0"
                        )
                    )
                    return@post
                }

                if (request.evaluatedWeight < 0.0 || request.evaluatedWeight >= 100.0) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiErrorResponse.create(
                            code = ApiErrorCodes.VALIDATION_ERROR,
                            message = "evaluatedWeight must be between 0.0 and less than 100.0"
                        )
                    )
                    return@post
                }

                val remainingWeight = 100.0 - request.evaluatedWeight
                val rawRequiredGrade = (request.targetGrade * 100.0 - request.currentGrade * request.evaluatedWeight) / remainingWeight
                val requiredGrade = round(rawRequiredGrade * 100.0) / 100.0

                val isAchievable = requiredGrade in 0.0..5.0
                val message = when {
                    requiredGrade <= 0.0 -> "Ya has alcanzado tu nota objetivo independientemente del porcentaje restante."
                    requiredGrade <= 5.0 -> "Necesitas una nota promedio de $requiredGrade en el $remainingWeight% restante."
                    else -> "No es matemáticamente posible alcanzar la nota objetivo $request.targetGrade (necesitarías $requiredGrade)."
                }

                call.respond(
                    HttpStatusCode.OK,
                    AcademicCalculateResponse(
                        currentGrade = request.currentGrade,
                        evaluatedWeight = request.evaluatedWeight,
                        remainingWeight = remainingWeight,
                        targetGrade = request.targetGrade,
                        requiredGrade = requiredGrade,
                        isAchievable = isAchievable,
                        message = message
                    )
                )
            }
        }
    }
}
