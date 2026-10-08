# UniHub — API Documentation

## 1. Overview

UniHub uses a Ktor REST API as the backend boundary for operations that require server-side processing, external service integration, Firebase authentication validation, academic calculations, AI command parsing, or centralized business logic.

The API follows RESTful conventions and uses JSON for request/response payloads.

## 2. Base URL

**Development:**
```text
http://localhost:8080
```

**Production:**
```text
[Configure based on deployment]
```

## 3. Authentication

Protected API endpoints require Firebase ID tokens provided via the standard Authorization header:

```http
Authorization: Bearer <Firebase ID Token>
```

Server-side verification validates the token using the Firebase Admin SDK or verifier middleware to establish the authenticated user's Firebase UID. Unauthenticated or invalid token requests receive an `HTTP 401 Unauthorized` response.

## 4. Endpoints

### 4.1 Health Check

**GET** `/api/health` (also accessible at `/health`)

Returns the health status of the Ktor server. Public endpoint.

**Response (200 OK):**
```json
{
  "status": "ok",
  "service": "unihub-api"
}
```

**Status Codes:**
- `200 OK` — Server is healthy

---

### 4.2 Profile

**GET** `/api/profile`

Retrieves profile information for the authenticated user based on their validated Firebase ID Token.

**Authentication:** Required (`Bearer <Firebase ID Token>`)

**Success Response (200 OK):**
```json
{
  "uid": "abc123firebaseUid",
  "email": "student@unihub.app",
  "displayName": "Alex Student",
  "photoUrl": "https://example.com/photo.jpg"
}
```

**Error Responses:**
- `401 Unauthorized` — Missing or invalid authentication token.

---

### 4.3 Academic Summary

**GET** `/api/academic/summary`

Retrieves an academic summary for the authenticated user.

**Authentication:** Required (`Bearer <Firebase ID Token>`)

**Success Response (200 OK):**
```json
{
  "uid": "abc123firebaseUid",
  "activeProgram": "Ingeniería de Sistemas",
  "activePeriod": "2025-1",
  "approvedCredits": 0,
  "totalCredits": 160,
  "cumulativeGpa": 0.0,
  "subjectsCount": 0
}
```

**Error Responses:**
- `401 Unauthorized` — Missing or invalid authentication token.

---

### 4.4 Academic Calculate

**POST** `/api/academic/calculate`

Calculates the required grade needed in the remaining percentage of a course to reach a target final grade.

**Authentication:** Required (`Bearer <Firebase ID Token>`)

**Request Body:**
```json
{
  "currentGrade": 3.0,
  "evaluatedWeight": 60.0,
  "targetGrade": 3.5
}
```

**Validation Rules:**
- `currentGrade`: float/double between `0.0` and `5.0`.
- `evaluatedWeight`: float/double between `0.0` and `< 100.0`.
- `targetGrade`: float/double between `0.0` and `5.0`.

**Success Response (200 OK):**
```json
{
  "currentGrade": 3.0,
  "evaluatedWeight": 60.0,
  "remainingWeight": 40.0,
  "targetGrade": 3.5,
  "requiredGrade": 4.25,
  "isAchievable": true,
  "message": "Necesitas una nota promedio de 4.25 en el 40.0% restante."
}
```

**Error Responses:**
- `400 Bad Request` — Validation error or invalid ranges.
  ```json
  {
    "error": {
      "code": "VALIDATION_ERROR",
      "message": "currentGrade must be between 0.0 and 5.0"
    }
  }
  ```
- `401 Unauthorized` — Missing or invalid token.

---

### 4.5 AI Parse

**POST** `/api/ai/parse`

Parses natural language instructions into structured academic actions (e.g., `CREATE_EVENT`, `CREATE_TASK`, `CREATE_SUBJECT`, `REGISTER_GRADE`).

**Authentication:** Required (`Bearer <Firebase ID Token>`)

**Request Body:**
```json
{
  "prompt": "Agrega una clase de Computación Móvil el jueves de 6 a 8 en la UdeA.",
  "context": "optional contextual string"
}
```

**Fields:**
- `prompt` (required): Natural language instruction (1 to 2000 characters).
- `context` (optional): Additional academic context.

**Success Response (200 OK):**
```json
{
  "action": "CREATE_EVENT",
  "rawPrompt": "Agrega una clase de Computación Móvil el jueves de 6 a 8 en la UdeA.",
  "structuredData": {
    "detected_action": "CREATE_EVENT",
    "input_length": "68"
  },
  "reply": "Entendido, creando evento para Computación Móvil."
}
```

**Error Responses:**
- `400 Bad Request` — Empty prompt or character length exceeded (> 2000).
- `401 Unauthorized` — Missing or invalid token.

---

### 4.6 AI Chat

**POST** `/api/v1/ai/chat`

Send a message to the AI assistant and receive a response. Uses a multi-provider fallback chain (OpenRouter primary, Groq fallback).

**Authentication:** Required (`Bearer <Firebase ID Token>`) when Firebase is configured on the server. Falls back to unauthenticated when no token verifier is available (development mode).

**Request Headers:**
```http
Content-Type: application/json
```

**Request Body:**
```json
{
  "message": "¿Cómo puedo organizar mi día de estudio?",
  "context": "optional context string",
  "conversationHistory": []
}
```

**Success Response (200 OK):**
```json
{
  "response": "Para organizar tu día de estudio, te recomiendo...",
  "provider": "OpenRouter",
  "model": "openrouter/free"
}
```

**Error Codes:**
- `AI_UNAVAILABLE` — AI service is temporarily unavailable
- `AI_RATE_LIMITED` — Rate limit exceeded, retry later
- `AI_INVALID_REQUEST` — Invalid request format
- `AI_CONFIGURATION_ERROR` — Server configuration error
- `AI_NETWORK_ERROR` — Network or timeout error
- `AI_UNKNOWN_ERROR` — Unknown error

---

## 5. Error Handling

All standard errors follow a consistent response structure:

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "The request contains invalid fields."
  }
}
```

Standardized Error Codes:
- `UNAUTHORIZED` — Missing or invalid authentication credentials
- `FORBIDDEN` — Resource access not allowed
- `VALIDATION_ERROR` — Request body or query parameters failed validation
- `NOT_FOUND` — Resource not found
- `BAD_REQUEST` — Malformed JSON or invalid syntax
- `INTERNAL_ERROR` — Unexpected server error

## 6. Rate Limiting

The API implements rate limiting to ensure fair usage. If you receive a `429 Too Many Requests` response, wait before retrying.

The AI providers also have their own rate limits. The system automatically retries with exponential backoff before falling back to alternative providers.

## 7. Security

- All API keys for AI providers are stored server-side and never exposed to the client.
- Firebase ID tokens are validated server-side using Firebase Admin SDK.
- The Android app communicates with the Ktor backend using HTTPS and Bearer tokens.
- Request validation is performed server-side independently of client checks.
- Sensitive information and full tokens are never logged.

## 8. Versioning

The API uses URL-based versioning for versioned endpoints. The current version is `v1`.

## 9. CORS

The API supports CORS (Cross-Origin Resource Sharing) for web-based clients.

Allowed methods:
- GET
- POST
- PUT
- DELETE
- OPTIONS

## 10. Related Documentation

- [Architecture](../architecture/ARCHITECTURE.md)
- [Security](../security/SECURITY.md)
- [Deployment](../deploy/DEPLOY.md)
