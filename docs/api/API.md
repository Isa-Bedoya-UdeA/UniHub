# UniHub — API Documentation

## 1. Overview

UniHub uses a Ktor REST API as the backend boundary for operations that require server-side processing, external service integration, or centralized business logic.

The API follows RESTful conventions and uses JSON for request/response payloads.

## 2. Base URL

**Development:**
```
http://localhost:8080
```

**Production:**
```
[Configure based on deployment]
```

## 3. Authentication

API endpoints may require Firebase ID tokens for authentication. Token validation is handled server-side.

## 4. Endpoints

### 4.1 Health Check

**GET** `/health`

Returns the health status of the Ktor server.

**Response:**
```json
{
  "status": "ok"
}
```

**Status Codes:**
- `200 OK` — Server is healthy

---

### 4.2 AI Chat

**POST** `/api/v1/ai/chat`

Send a message to the AI assistant and receive a response.

The AI system uses a multi-provider architecture with automatic fallback:
1. **OpenRouter** (primary)
2. **Groq** (fallback)

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "message": "string"
}
```

**Fields:**
- `message` (required): The user's message to the AI assistant. Must be between 1 and 2000 characters.

**Success Response (200 OK):**
```json
{
  "response": "string",
  "provider": "string",
  "model": "string"
}
```

**Fields:**
- `response`: The AI assistant's response text
- `provider`: The AI provider that generated the response (optional, for debugging)
- `model`: The specific model used (optional, for debugging)

**Error Responses:**

**400 Bad Request**
```json
{
  "error": "AI_INVALID_REQUEST"
}
```
The request was malformed or the message was empty/too long.

**429 Too Many Requests**
```json
{
  "error": "AI_RATE_LIMITED"
}
```
The AI service is temporarily rate-limited. Retry after a short delay.

**502 Bad Gateway**
```json
{
  "error": "AI_UNAVAILABLE"
}
```
The AI service is temporarily unavailable.

**503 Service Unavailable**
```json
{
  "error": "AI_CONFIGURATION_ERROR"
}
```
The AI service is not properly configured.

**504 Gateway Timeout**
```json
{
  "error": "AI_NETWORK_ERROR"
}
```
The AI service timed out.

**500 Internal Server Error**
```json
{
  "error": "AI_UNKNOWN_ERROR"
}
```
An unknown error occurred.

**Error Codes:**
- `AI_UNAVAILABLE` — AI service is temporarily unavailable
- `AI_RATE_LIMITED` — Rate limit exceeded, retry later
- `AI_INVALID_REQUEST` — Invalid request format
- `AI_CONFIGURATION_ERROR` — Server configuration error
- `AI_NETWORK_ERROR` — Network or timeout error
- `AI_UNKNOWN_ERROR` — Unknown error

**Example Request:**
```bash
curl -X POST http://localhost:8080/api/v1/ai/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "¿Cómo puedo organizar mi día de estudio?"
  }'
```

**Example Response:**
```json
{
  "response": "Para organizar tu día de estudio, te recomiendo:\n\n1. **Prioriza tareas urgentes**: Revisa tus tareas con fechas de entrega cercanas y complétalas primero.\n\n2. **Bloques de tiempo**: Dedica bloques de 45-50 minutos a cada materia, con descansos de 10 minutos entre ellos.\n\n3. **Revisión activa**: Al final del día, repasa lo que aprendiste para reforzar la memoria.",
  "provider": "OpenRouter",
  "model": "openrouter/free"
}
```

## 5. Error Handling

All errors follow a consistent format:

```json
{
  "error": "string"
}
```

The `error` field contains a standardized error code that the client can use to determine the appropriate user-facing message.

## 6. Rate Limiting

The API implements rate limiting to ensure fair usage. If you receive a `429 Too Many Requests` response, wait before retrying.

The AI providers also have their own rate limits. The system automatically retries with exponential backoff before falling back to alternative providers.

## 7. Security

- All API keys for AI providers are stored server-side and never exposed to the client
- The Android app communicates only with the Ktor backend, never directly with AI providers
- Request validation is performed server-side
- Sensitive information is not logged

## 8. Versioning

The API uses URL-based versioning. The current version is `v1`.

Future versions will be introduced as `/api/v2/...` when breaking changes are necessary.

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
