# UniHub — Firestore Model

## 1. Purpose

This document defines the Cloud Firestore document model used by UniHub for cloud persistence and synchronization.

Firestore is the cloud persistence layer. Room remains the local relational persistence layer.

The two models represent the same business information but are not required to be structurally identical because Firestore is document-oriented.

## 2. Persistence Strategy

| Storage                 | Purpose                                      | Model                          |
|-------------------------|----------------------------------------------|--------------------------------|
| Room                    | Local persistence and offline-first behavior | Relational                     |
| Firestore               | Cloud persistence and synchronization        | Document-oriented              |
| Firebase Authentication | Identity and authentication                  | Managed authentication service |

The Firebase Authentication UID is the stable identity used for user ownership.

```text
Google OAuth
    ↓
Firebase Authentication
    ↓
Firebase UID
    ↓
User-owned Firestore data
```

## 3. Design Principles

The Firestore model follows these principles:

- Every protected resource belongs to an authenticated user.
- User data is isolated by user ownership.
- Data is organized around application access patterns.
- All documents related to a user are under the `users/{userId}/` root.
- Maintain consistency with the relational model's business logic.

## 4. Root Collection Structure

The proposed Firestore structure is:

```text
users/
    {userId}/
        profile/
            {profileId}/

        studies/
            {studyId}/

        academicPeriods/
            {academicPeriodId}/

        subjects/
            {subjectId}/

        events/
            {eventId}/
                reminders/
                    {reminderId}/
                eventTags/
                    {tagId}/

        recurrenceRules/
            {recurrenceRuleId}/
                days/
                    {dayId}/

        locations/
            {locationId}/

        tasks/
            {taskId}/
                taskTags/
                    {tagId}/

        grades/
            {gradeId}/

        tags/
            {tagId}/
        
        preferences/
            settings/
```

## 5. User Profile

Path: `users/{userId}/profile/{profileId}`

| Field             | Type      | Required | Description           |
|-------------------|-----------|---------:|-----------------------|
| `name`            | string    |      Yes | Display name          |
| `email`           | string    |      Yes | User email            |
| `profileImageUrl` | string    |       No | Profile image URL     |
| `createdAt`       | timestamp |      Yes | Creation timestamp    |
| `updatedAt`       | timestamp |      Yes | Last update timestamp |

## 6. Study (Academic Program)

Path: `users/{userId}/studies/{studyId}`

| Field             | Type      | Required | Description                                                     |
|-------------------|-----------|---------:|-----------------------------------------------------------------|
| `name`            | string    |      Yes | Program name (e.g. "Ing de Sistemas")                           |
| `institution`     | string    |      Yes | Institution (e.g. "UdeA")                                       |
| `totalCredits`    | number    |      Yes | Target credits for completion                                   |
| `approvedCredits` | number    |       No | Manually entered credits approved before using the app          |
| `cumulativeGpa`   | number    |       No | Manually entered cumulative GPA before using the app            |
| `isActive`        | boolean   |      Yes | Whether it is the user's primary study                          |
| `createdAt`       | timestamp |      Yes | Creation timestamp                                              |
| `updatedAt`       | timestamp |      Yes | Last update timestamp                                           |

**Notes**

- `approvedCredits` and `cumulativeGpa` are optional fields for users who already have completed semesters and want to register their previous academic status without having to create all past subjects.
- When these fields are populated, the system combines them with current period data to calculate the overall academic progress.
- These fields are particularly useful for users migrating from other systems or starting to use the app mid-career.

## 7. Academic Period

Path: `users/{userId}/academicPeriods/{academicPeriodId}`

| Field       | Type        | Required | Description                     |
|-------------|-------------|---------:|---------------------------------|
| `studyId`   | string      |      Yes | Associated study/program        |
| `name`      | string      |      Yes | Display name, e.g. `2026-2`     |
| `startDate` | string/date |      Yes | Period start                    |
| `endDate`   | string/date |      Yes | Period end                      |
| `isActive`  | boolean     |      Yes | Whether it is the active period |
| `createdAt` | timestamp   |      Yes | Creation timestamp              |
| `updatedAt` | timestamp   |      Yes | Last update timestamp           |

## 8. Subject

Path: `users/{userId}/subjects/{subjectId}`

| Field              | Type      | Required | Description                |
|--------------------|-----------|---------:|----------------------------|
| `studyId`          | string    |      Yes | Associated study/program   |
| `academicPeriodId` | string    |      Yes | Associated academic period |
| `name`             | string    |      Yes | Subject name               |
| `code`             | string    |       No | University course code     |
| `professor`        | string    |       No | Professor or instructor    |
| `credits`          | number    |       No | Academic credits           |
| `color`            | string    |       No | Hex color code (e.g. "#4F46E5") |
| `notes`            | string    |       No | User notes                 |
| `createdAt`        | timestamp |      Yes | Creation timestamp         |
| `updatedAt`        | timestamp |      Yes | Last update timestamp      |

## 9. Event

Path: `users/{userId}/events/{eventId}`

| Field              | Type      | Required | Description                                |
|--------------------|-----------|---------:|--------------------------------------------|
| `title`            | string    |      Yes | Event title                                |
| `subjectId`        | string    |       No | Associated subject                         |
| `academicPeriodId` | string    |       No | Associated academic period                 |
| `startAt`          | timestamp |      Yes | Event start                                |
| `endAt`            | timestamp |      Yes | Event end                                  |
| `recurrenceRuleId` | string    |       No | Associated recurrence definition           |
| `locationId`       | string    |       No | Physical location                          |
| `meetingUrl`       | string    |       No | Remote meeting URL                         |
| `locationType`     | string    |      Yes | `PHYSICAL`, `REMOTE`, or `NONE`            |
| `eventType`        | string    |      Yes | `CLASS`, `EXAM`, `MEETING`, `PERSONAL`, or `OTHER` |
| `notes`            | string    |       No | Private event notes                        |
| `createdAt`        | timestamp |      Yes | Creation timestamp                         |
| `updatedAt`        | timestamp |      Yes | Last update timestamp                      |

### 9.1 Event Reminders (Subcollection)

Path: `users/{userId}/events/{eventId}/reminders/{reminderId}`

| Field          | Type    | Required | Description                                               |
|----------------|---------|---------:|-----------------------------------------------------------|
| `id`           | string  |      Yes | Unique reminder identifier                                |
| `eventId`      | string  |      Yes | Associated event                                          |
| `reminderType` | string  |      Yes | `MINUTES_BEFORE`, `HOURS_BEFORE`, or `DAYS_BEFORE`        |
| `value`        | number  |      Yes | Number of time units before the event to trigger reminder |
| `isEnabled`    | boolean |      Yes | Whether this reminder is active                           |

### 9.2 Event Tags (Subcollection)

Path: `users/{userId}/events/{eventId}/eventTags/{tagId}`

| Field     | Type   | Required | Description      |
|-----------|--------|---------:|------------------|
| `eventId` | string |      Yes | Event identifier |
| `tagId`   | string |      Yes | Tag identifier   |

## 10. Recurrence Rule

Path: `users/{userId}/recurrenceRules/{recurrenceRuleId}`

| Field       | Type        | Required | Description                               |
|-------------|-------------|---------:|-------------------------------------------|
| `frequency` | string      |      Yes | `DAILY`, `WEEKLY`, or supported frequency |
| `interval`  | number      |      Yes | Repetition interval                       |
| `startDate` | string/date |      Yes | Recurrence start                          |
| `endDate`   | string/date |      Yes | Recurrence end                            |
| `createdAt` | timestamp   |      Yes | Creation timestamp                        |
| `updatedAt` | timestamp   |      Yes | Last update timestamp                     |

## 11. Recurrence Days

Path: `users/{userId}/recurrenceRules/{recurrenceRuleId}/days/{dayId}`

| Field       | Type   | Required | Description             |
|-------------|--------|---------:|-------------------------|
| `dayOfWeek` | number |      Yes | ISO weekday from 1 to 7 |

## 12. Location

Path: `users/{userId}/locations/{locationId}`

| Field       | Type      | Required | Description                  |
|-------------|-----------|---------:|------------------------------|
| `name`      | string    |      Yes | Location display name        |
| `address`   | string    |       No | Formatted address            |
| `latitude`  | number    |       No | Latitude                     |
| `longitude` | number    |       No | Longitude                    |
| `placeId`   | string    |       No | Google Maps place identifier |
| `createdAt` | timestamp |      Yes | Creation timestamp           |
| `updatedAt` | timestamp |      Yes | Last update timestamp        |

## 13. Task

Path: `users/{userId}/tasks/{taskId}`

| Field                        | Type      | Required | Description                                        |
|------------------------------|-----------|---------:|----------------------------------------------------|
| `title`                      | string    |      Yes | Task title                                         |
| `description`                | string    |       No | Task description                                   |
| `subjectId`                  | string    |       No | Associated subject                                 |
| `academicPeriodId`           | string    |       No | Associated academic period (from subject)          |
| `dueAt`                      | string    |       No | Deadline (ISO-8601)                                |
| `status`                     | string    |      Yes | `PENDING`, `IN_PROGRESS`, or `COMPLETED`           |
| `priority`                   | string    |      Yes | `LOW`, `MEDIUM`, or `HIGH`                         |
| `reminderType`               | string    |       No | `MINUTES_BEFORE`, `HOURS_BEFORE`, or `DAYS_BEFORE` |
| `reminderValue`              | number    |       No | Number of time units before deadline               |
| `isDeadlineReminderEnabled`  | boolean   |      Yes | Whether deadline notification is active            |
| `createdAt`                  | string    |      Yes | Creation timestamp (ISO-8601)                      |
| `updatedAt`                  | string    |      Yes | Last update timestamp (ISO-8601)                   |

**Notes**

- The `notes` field was removed. Use `description` for task text content.
- `reminderType` and `reminderValue` replace the previous `reminderAt` field. Reminders are relative to the deadline.
- All timestamps use ISO-8601 string format for consistency.

### 13.1 Task Tags (Subcollection)

Path: `users/{userId}/tasks/{taskId}/taskTags/{tagId}`

| Field    | Type   | Required | Description     |
|----------|--------|---------:|-----------------|
| `taskId` | string |      Yes | Task identifier |
| `tagId`  | string |      Yes | Tag identifier  |

## 14. Grade

Path: `users/{userId}/grades/{gradeId}`

| Field              | Type      | Required | Description                             |
|--------------------|-----------|---------:|-----------------------------------------|
| `subjectId`        | string    |      Yes | Associated subject                      |
| `academicPeriodId` | string    |      Yes | Associated academic period (from subject)|
| `name`             | string    |      Yes | Evaluation name                         |
| `value`            | number    |      Yes | Grade value                             |
| `weight`           | number    |      Yes | Evaluation weight                       |
| `notes`            | string    |       No | Additional notes                        |
| `createdAt`        | timestamp |      Yes | Creation timestamp                      |
| `updatedAt`        | timestamp |      Yes | Last update timestamp                   |

## 15. Tag

Path: `users/{userId}/tags/{tagId}`

| Field       | Type      | Required | Description           |
|-------------|-----------|---------:|-----------------------|
| `name`      | string    |      Yes | Tag name              |
| `createdAt` | timestamp |      Yes | Creation timestamp    |

**Notes**

- Tags are shared across tasks and events.
- The same tag can be associated with multiple tasks or events.
- Tags are created automatically when a user types a new tag name in the task form.
- Tags are stored in a separate collection and referenced by ID in the `taskTags` and `eventTags` subcollections.

## 16. Preferences

Path: `users/{userId}/preferences/settings`

| Field       | Type      | Required | Description                        |
|-------------|-----------|---------:|------------------------------------|
| `themeMode` | string    |      Yes | `LIGHT`, `DARK`, or `SYSTEM`       |

## 17. Profile Image Storage

Profile images are stored in Cloudinary, not in Firestore.

Cloudinary path: `unihub/profile-images/...`

The Firestore profile document stores only the `profileImageUrl` reference (HTTPS `secure_url`) obtained after upload.

```text
User selects image
    → Validate MIME type and file size
    → Upload to Cloudinary (unsigned upload)
    → Obtain HTTPS secure_url
    → Update profileImageUrl in Firestore profile document
    → Update User in Room
```

Maximum file size: 5 MB. Allowed content types: `image/jpeg`, `image/png`, `image/webp`.

### Profile Image Source Priority

The application resolves the profile image URL using the following priority:

1. Firestore profile `profileImageUrl` (Cloudinary URL)
2. Firebase Authentication `photoUrl` (Google account photo)
3. Default placeholder image

When signing in, if the Firestore profile already contains a `profileImageUrl`, it takes precedence over the Firebase Auth photo URL. The Google account photo is only used as a fallback when no Cloudinary image has been uploaded.

## 18. Logical Relationships

```text
Study
  │
  └────── 0..N AcademicPeriod
                │
                └────── 0..N Subject
                              │
                              ├────── 0..N Event
                              ├────── 0..N Task
                              └────── 0..N Grade
```

## 19. Synchronization Strategy

### 19.1 Architecture

Room remains the local source of truth for observable application data. Firestore provides cloud persistence and cross-device synchronization.

```text
Write flow:
    UI → UseCase → Repository
        → Room (local, immediate)
        → Firestore (remote, best-effort)

Read flow:
    Firestore (sync)
        → Remote DTO → Room Entity
        → Room Flow → Repository → UseCase → ViewModel → UI
```

### 19.2 Write Strategy

Local-first: writes are applied to Room immediately. The UI updates from Room Flow observation. Firestore writes are attempted after the local write succeeds. If the Firestore write fails (network unavailable, permission denied), the local state remains valid and the write is retried during the next synchronization cycle.

### 19.3 Synchronization Mechanism

#### Login Synchronization

After successful authentication, all user data is pulled from Firestore into Room:

1. Firebase Authentication completes.
2. Profile is fetched from `users/{uid}/profile/main`.
3. All entity collections are fetched (studies, academic periods, subjects, events, tasks, tags, locations, grades, preferences).
4. Remote DTOs are mapped to Room entities and inserted.
5. UI observes Room and updates automatically.

This ensures that after reinstalling the app and signing in with the same account, all cloud-persisted data is restored.

#### Background Synchronization

Background synchronization uses WorkManager with:

- Periodic execution (15-minute intervals)
- Network connectivity constraint
- Exponential backoff on failure
- Authenticated user context
- Independent try/catch per collection (profile, studies, academic periods, subjects, events, tasks, tags, locations, preferences)
- A failure in one collection does not prevent other collections from syncing

### 19.4 Conflict Resolution

The current MVP uses a last-write-wins strategy with stable IDs. Entity IDs are generated locally and preserved across synchronization. Firestore documents use the same IDs as Room entities.

### 19.5 Loop Prevention

Synchronization is unidirectional during each cycle:

1. Remote data is pulled into Room (Firestore → Room).
2. Local writes push to Firestore (Room → Firestore).

The repository layer distinguishes between local-originated writes and remote-originated writes to avoid echo loops.

### 19.6 Offline Behavior

When network connectivity is unavailable:

- Local Room data remains fully accessible.
- Writes succeed locally and are queued for the next sync cycle.
- Firestore failures do not rollback valid local state.

### 19.7 Limitations

- No real-time Firestore listeners in the current MVP.
- No per-field conflict resolution.
- No offline write queue persistence beyond WorkManager retry.
- Synchronization is periodic, not instantaneous.
