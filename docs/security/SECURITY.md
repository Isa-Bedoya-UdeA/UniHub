# UniHub — Security Model

## 1. Purpose

This document defines the security model for UniHub. It establishes the mechanisms and responsibilities used to protect authentication, authorization, local data, cloud data, API communication, secrets, and user actions.

Security is treated as an architectural concern rather than only a UI or Firebase configuration concern.

## 2. Security Objectives

UniHub must protect:

- User identity and authentication state.
- Academic information such as subjects, grades, tasks, and events.
- Private notes and other user-provided content.
- Cloud data ownership.
- API endpoints.
- Authentication tokens.
- Third-party service credentials and configuration.
- Data transmitted between the Android application and backend services.

The main security properties are:

| Property        | Objective                                                                   |
|-----------------|-----------------------------------------------------------------------------|
| Confidentiality | Prevent unauthorized access to user information                             |
| Integrity       | Prevent unauthorized modification of user data                              |
| Authentication  | Verify the identity of users and backend requests                           |
| Authorization   | Ensure users can access only resources they own                             |
| Availability    | Handle network and service failures safely                                  |
| Accountability  | Keep sufficient non-sensitive information for debugging and security review |

## 3. Security Architecture

```text
┌──────────────────────────────┐
│            User              │
└──────────────┬───────────────┘
               │
               │ Google OAuth
               ↓
┌──────────────────────────────┐
│   Firebase Authentication    │
└──────────────┬───────────────┘
               │
               │ Firebase ID Token
               ↓
┌──────────────────────────────┐
│       Android / UniHub       │
│                              │
│  Secure session handling     │
│  Input validation            │
│  Local protection            │
└───────┬──────────────┬───────┘
        │              │
        │              │ HTTPS + ID Token
        │              ↓
        │      ┌──────────────────┐
        │      │    Ktor API      │
        │      │                  │
        │      │ Token validation │
        │      │ Authorization    │
        │      │ Validation       │
        │      └────────┬─────────┘
        │               │
        │               ↓
        │      ┌──────────────────┐
        │      │ External / Cloud │
        │      │ Services         │
        │      └──────────────────┘
        │
        └───────────────→ Firebase / Firestore
```

The Android client is considered untrusted. Security decisions must not depend exclusively on client-side checks.

## 4. Authentication

### 4.1 Firebase Authentication

Firebase Authentication is the identity provider for UniHub.

The planned MVP authentication flow is:

```text
User
  ↓
Google OAuth
  ↓
Firebase Authentication
  ↓
Firebase User
  ↓
Firebase ID Token
  ↓
Authenticated UniHub session
```

UniHub will not implement its own password storage.

### 4.2 Firebase UID

The Firebase UID is the stable identifier used to associate application data with the authenticated user.

```text
Firebase UID
    ↓
User.user_id
    ↓
Firestore user ownership
```

The UID must not be replaced by an identifier generated only by the Android client.

### 4.3 Session Handling

The application must:

- Observe authentication state.
- Restore valid sessions when possible.
- Redirect unauthenticated users to Login.
- Prevent access to authenticated screens without a valid session.
- Sign out through Firebase Authentication.
- Avoid persisting authentication credentials in arbitrary local files.

The application should rely on the authentication SDK's supported session/token management rather than implementing custom token storage.

## 5. Authorization

Authentication answers:

> Who is the user?

Authorization answers:

> What is the user allowed to access?

UniHub uses ownership-based authorization.

```text
User A
  ↓
Owns A's academic data

User B
  ↓
Owns B's academic data
```

A user must not be able to read, modify, or delete another user's:

- Subjects.
- Academic periods.
- Events.
- Tasks.
- Grades.
- Locations.
- Tags.
- Other private application data.

Authorization must be enforced at the data and backend boundaries, not only by hiding UI elements.

## 6. Firestore Security

Cloud Firestore is protected with Firebase Security Rules.

The fundamental rule is:

```text
Authenticated user
        ↓
Can access only resources whose ownerId == request.auth.uid
```

With the proposed user-scoped structure:

```text
users/{userId}/...
        ↓
request.auth.uid == userId
```

Security Rules must deny access by default unless a specific rule grants it.

Write operations must validate the authenticated user against the path and, where applicable, validate the ownership of referenced resources.

## 7. Ktor API Security

Ktor provides the server-side API boundary required by the project.

Protected requests follow:

```text
Android
  ↓
HTTPS request
  ↓
Authorization: Bearer <Firebase ID Token>
  ↓
Ktor authentication middleware
  ↓
Firebase token validation
  ↓
Authenticated request
  ↓
Authorization checks
  ↓
Validation
  ↓
Application logic
```

Ktor must not trust a user ID supplied by the client when the identity can be obtained from the validated Firebase token.

### 7.1 Protected endpoints

Any endpoint that accesses user data must require authentication.

Examples:

```text
GET /api/profile
GET /api/academic/summary
GET /api/academic/calculate
POST /api/ai/parse
```

The health endpoint may be public if it exposes no sensitive information:

```text
GET /api/health
```

### 7.2 Input validation

Ktor endpoints must validate:

- Required fields.
- Data types.
- String lengths.
- Date and time consistency.
- Numeric ranges.
- Enum values.
- URLs where applicable.
- Ownership-sensitive identifiers.

For academic program data:

- `approvedCredits` must be a non-negative integer when provided.
- `cumulativeGpa` must be a valid GPA value within the institution's scale (typically 0.0 to 5.0) when provided.
- Both fields are optional and may be null.

Invalid requests must return controlled HTTP errors and must not expose internal implementation details.

## 8. Network Security

Communication between the Android application and Ktor must use HTTPS.

Sensitive information must not be transmitted through plain HTTP.

The application must not disable TLS certificate validation for production builds.

## 9. Local Data Security

Room provides local persistence but is not itself an encryption mechanism.

Potentially sensitive information includes:

- Private notes.
- Academic records.
- Authentication-related information.
- User preferences that reveal private behavior.

When additional local protection is required, Android security mechanisms should be used.

Planned mechanisms include:

- Android Keystore for cryptographic key protection.
- Secure storage for sensitive secrets.
- Encryption for specifically classified sensitive local fields where required.

The application must not store Firebase private credentials, server-only secrets, or confidential OAuth credentials inside the Android source code.

## 10. Secrets Management

Secrets must not be committed to Git.

Sensitive configuration must use appropriate environment or secret-management mechanisms.

For GitHub Actions:

```text
GitHub Repository
      ↓
GitHub Actions Secrets
      ↓
CI/CD environment
      ↓
Build / deployment
```

For the Ktor server:

```text
Environment variables
        ↓
Ktor configuration
```

The repository may contain safe configuration examples, but never real credentials.

## 11. Firestore Security Rules

Firestore Security Rules enforce ownership-based access control at the database level.

### 11.1 Ownership Principle

Every document in Firestore belongs to an authenticated user. The fundamental authorization check is:

```text
request.auth != null
&& request.auth.uid == userId
```

### 11.2 Protected Paths

All user data is organized under `/users/{userId}/` with the following structure:

```text
users/{userId}/
    profile/{profileId}
    studies/{studyId}
    academicPeriods/{academicPeriodId}
    subjects/{subjectId}
    events/{eventId}
        reminders/{reminderId}
    recurrenceRules/{recurrenceRuleId}
        days/{dayId}
    locations/{locationId}
    tasks/{taskId}
    grades/{gradeId}
    tags/{tagId}
    preferences/{document}
```

### 11.3 Rule Structure

The security rules use helper functions for consistency:

```text
isAuthenticated() → checks if user is authenticated
isOwner(userId) → checks if authenticated user owns the resource
```

All collections under `/users/{userId}/` enforce ownership validation. Users can only:
- Read their own data
- Create data under their own UID
- Update their own data
- Delete their own data

### 11.4 Default Deny

A catch-all rule at the end denies access to any path not explicitly allowed:

```text
match /{document=**} {
  allow read, write: if false;
}
```

### 11.5 Rule Deployment

Security rules are maintained in `firestore.rules` at the repository root and deployed using:

```bash
firebase deploy --only firestore:rules
```

Rules must be reviewed and updated whenever the data model changes.

### 11.6 Cloudinary Media Storage

Profile images are stored in Cloudinary using unsigned upload presets.

#### Upload Authorization

Unsigned uploads use a restricted upload preset (`unihub_profile_images`) configured in the Cloudinary console with:

- Image-only resource type
- Allowed formats: JPEG, PNG, WebP
- Fixed folder: `unihub/profile-images`
- Maximum file size: 5 MB (validated client-side)

The upload preset is not a secret and is intentionally exposed in client code for the current MVP. Stronger signed upload authorization can be introduced through the Ktor backend later.

#### Security Constraints

- No Cloudinary API Secret is stored in the Android client.
- Profile image URLs use HTTPS (`secure_url`).
- The Android client validates MIME type and file size before upload.

#### Profile Image Source Priority

The application resolves the profile image using:

1. Firestore `profileImageUrl` (Cloudinary) — highest priority
2. Firebase Authentication `photoUrl` (Google account photo) — fallback
3. Default placeholder — last resort

The Firestore Cloudinary URL is never overwritten by the Firebase Auth photo URL during sign-in.

## 12. Google Maps and Third-Party Services

Google Maps Platform credentials must be restricted according to Google's Android security recommendations and limited to the required application and APIs.

Third-party credentials must follow least-privilege principles.

Server-only credentials must never be exposed to the Android client.

## 13. AI Security and Privacy

The AI assistant can process user-provided academic information.

The application must avoid sending unnecessary private data to the AI service.

The context builder should follow data minimization:

```text
User request
    ↓
Determine required context
    ↓
Select only relevant data
    ↓
Build AI context
    ↓
AI request
```

AI must not be treated as an authorization mechanism.

The AI may suggest actions, but the application remains responsible for:

- Authentication.
- Authorization.
- Validation.
- Business rules.
- Persistence.

For data-modifying commands:

```text
Natural language
      ↓
AI interpretation
      ↓
Structured command
      ↓
Application validation
      ↓
Confirmation when required
      ↓
Use Case
      ↓
Repository
```

The AI must never directly write to Firestore or Room.

## 14. Voice Input

Voice input is converted to text before AI processing.

The same validation and authorization rules apply regardless of whether the command originated from typed text or voice transcription.

Voice input must not bypass confirmation or application validation.

## 15. Notifications

Notifications must not expose unnecessary private information.

Sensitive notes, private academic details, or confidential content should not be displayed in notification previews unless explicitly designed and enabled by the user.

## 16. Threat Model

| Threat                        | Mitigation                                                |
|-------------------------------|-----------------------------------------------------------|
| Unauthorized Firestore access | Firebase Security Rules                                   |
| Access to another user's data | Ownership validation                                      |
| Forged API identity           | Firebase ID Token validation                              |
| Malicious API input           | DTO and domain validation                                 |
| Token leakage                 | SDK-managed authentication session and secure transport   |
| Hard-coded secrets            | Environment variables / GitHub Secrets                    |
| Local data exposure           | Android security mechanisms and encryption where required |
| AI-generated invalid action   | Structured output, validation, confirmation               |
| AI prompt manipulation        | Treat AI output as untrusted input                        |
| Excessive AI data exposure    | Context minimization                                      |
| Malicious third-party client  | Server-side authorization                                 |
| Network interception          | HTTPS/TLS                                                 |

## 17. Error Handling

Security-related failures must return generic, controlled messages.

Do not expose:

- Stack traces.
- Database implementation details.
- Authentication internals.
- Secrets.
- Tokens.
- Internal service configuration.

For example:

```text
Internal:
"Firestore permission evaluation failed."

User:
"Unable to access this information."
```

Detailed diagnostics may be logged in controlled development environments without exposing sensitive information.

## 18. Dependency Security

Dependencies must be kept reasonably current and reviewed before introducing new libraries.

GitHub Actions may automate:

- Build verification.
- Tests.
- Static checks.
- Dependency and security checks when configured.

Unnecessary dependencies should not be introduced.

## 19. Security Rules for Development

Development must follow these principles:

- Never commit secrets.
- Never bypass authentication to simplify a feature.
- Never rely only on UI authorization.
- Never trust IDs supplied by clients without ownership validation.
- Never log authentication tokens.
- Never log private academic information unnecessarily.
- Never allow AI output to bypass domain validation.
- Never expose production credentials in local configuration.
- Keep Firebase Security Rules versioned in the repository.

## 20. Security Testing

Security-related tests should cover:

- Unauthenticated access.
- Authenticated access.
- Access to another user's data.
- Unauthorized writes.
- Invalid ownership.
- Invalid API tokens.
- Invalid request payloads.
- Malformed IDs.
- Invalid AI-generated commands.
- Sensitive-data handling.

| Scenario                               | Expected result            |
|----------------------------------------|----------------------------|
| No authentication + protected resource | Denied                     |
| User A + User A resource               | Allowed                    |
| User A + User B resource               | Denied                     |
| Invalid Firebase token                 | Denied                     |
| Missing required API field             | Rejected                   |
| Invalid grade value                    | Rejected                   |
| AI command with invalid data           | Rejected                   |
| AI command with valid data             | Validated before execution |

## 21. Security Priorities

Given the project's four-month timeframe, security priorities are:

1. Firebase Authentication.
2. Firestore ownership rules.
3. Ktor Firebase ID Token validation.
4. HTTPS.
5. Input validation.
6. Secure secret management.
7. Secure local handling of sensitive data.
8. AI data minimization.
9. Security testing.
10. Firebase App Check if feasible within the schedule.

Advanced security mechanisms must not compromise MVP stability.

## 22. Firebase App Check

Firebase App Check has been evaluated for the UniHub project. The complete evaluation, including technical feasibility analysis, cost-benefit assessment, and implementation plan, is documented in:

**[Firebase App Check Evaluation](FIREBASE_APP_CHECK_EVALUATION.md)**

### Current Status

- **MVP Phase:** Not implemented. The existing security measures (Firebase Authentication, Firestore Security Rules with ownership validation, Ktor token validation) provide adequate protection for the university MVP.
- **Production Phase:** Recommended for implementation when the app is published to the Google Play Store.

### Rationale

The project already implements defense-in-depth with multiple security layers:

1. **Authentication:** Firebase Authentication with Google OAuth.
2. **Authorization:** Firestore Security Rules enforce ownership (`request.auth.uid == userId`) on all collections and subcollections.
3. **Backend Protection:** Ktor validates Firebase ID Tokens using Firebase Admin SDK.
4. **Secret Management:** API keys are stored only in the backend environment, never in the Android client.

Firebase App Check would add device attestation to prevent automated abuse, but requires Google Play Console configuration and adds development complexity that is not justified for the current MVP phase.

### Implementation Requirements (Future)

When ready for production:

1. Google Play Console account ($25 one-time fee).
2. App published in Play Store (at least closed testing).
3. Play Integrity API configured as attestation provider.
4. App Check SDK integrated in Android app.
5. Firestore rules updated with `request.app_check.status == 'VALID'` condition.
6. Optional: Ktor backend integration for additional API protection.

## 23. Input Validation Summary

### Server-Side (Ktor)

All endpoints validate:

- Required fields presence and non-blank values.
- Numeric ranges (grades: 0.0-5.0, weights: 0.0-100.0).
- NaN and Infinity rejection for numeric fields.
- String length limits (prompts: 2000 characters).
- Malformed request payloads return `400 Bad Request`.
- Missing or invalid authentication returns `401 Unauthorized`.

### Client-Side (Android)

Forms validate:

- Required fields (names, titles, descriptions).
- Numeric ranges and validity (no NaN, Infinity, or out-of-range values).
- String length limits (names: 100 characters, notes: 500 characters).
- Date and time consistency.
- Enum values and identifiers.

Validation occurs in ViewModels before data reaches Use Cases or Repositories.

## 24. Security Audit Trail

### Week 13 Security Improvements

1. **Input Validation Enhancement:** Added NaN and Infinity validation in `AcademicRoutes.kt` (Ktor) and `GradeFormViewModel.kt` (Android).
2. **Error Handling:** Eliminated empty catch blocks in `RepeatSubjectUseCase.kt`, replacing them with appropriate warning logs.
3. **Security Testing:** Added comprehensive tests for input validation edge cases and authentication scenarios in `AcademicRoutesTest.kt` and `AuthMiddlewareTest.kt`.
4. **App Check Evaluation:** Completed technical feasibility study and documented recommendation (see section 22).
5. **Secrets Audit:** Verified that all sensitive credentials are properly excluded from version control and no secrets are hardcoded in source code.

