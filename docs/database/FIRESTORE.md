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
- Avoid deeply nested collections when they provide no practical benefit.
- Avoid unnecessary duplication.
- Denormalize only when it improves required reads and the consistency cost is understood.
- Keep recurrence definitions separate from generated calendar occurrences.
- Do not store one document for every recurring event occurrence.
- Do not expose infrastructure models directly to the Domain layer.

## 4. Root Collection Structure

The proposed Firestore structure is:

```text
users/
    {userId}/
        profile/
            {profileId}/

        academicPeriods/
            {academicPeriodId}/

        subjects/
            {subjectId}/

        events/
            {eventId}/

        recurrenceRules/
            {recurrenceRuleId}/
                days/
                    {dayId}/

        locations/
            {locationId}/

        tasks/
            {taskId}/

        grades/
            {gradeId}/

        tags/
            {tagId}/
```

All application data is scoped beneath:

```text
users/{userId}/
```

This structure makes ownership explicit and simplifies Firestore Security Rules.

## 5. User Profile

Path:

```text
users/{userId}/profile/{profileId}
```

Suggested fields:

| Field             | Type      | Required | Description           |
|-------------------|-----------|---------:|-----------------------|
| `name`            | string    |      Yes | Display name          |
| `email`           | string    |      Yes | User email            |
| `profileImageUrl` | string    |       No | Profile image URL     |
| `createdAt`       | timestamp |      Yes | Creation timestamp    |
| `updatedAt`       | timestamp |      Yes | Last update timestamp |

The Firebase UID is obtained from the document path and authentication context and is not treated as a user-controlled ownership value.

## 6. Academic Period

Path:

```text
users/{userId}/academicPeriods/{academicPeriodId}
```

An Academic Period represents a university semester or equivalent academic period.

| Field       | Type        | Required | Description                     |
|-------------|-------------|---------:|---------------------------------|
| `name`      | string      |      Yes | Display name, e.g. `2026-2`     |
| `startDate` | string/date |      Yes | Period start                    |
| `endDate`   | string/date |      Yes | Period end                      |
| `isActive`  | boolean     |      Yes | Whether it is the active period |
| `createdAt` | timestamp   |      Yes | Creation timestamp              |
| `updatedAt` | timestamp   |      Yes | Last update timestamp           |

Multiple academic periods may coexist. The active period is selected by application state rather than deleting previous data.

## 7. Subject

Path:

```text
users/{userId}/subjects/{subjectId}
```

| Field              | Type      | Required | Description                |
|--------------------|-----------|---------:|----------------------------|
| `academicPeriodId` | string    |      Yes | Associated academic period |
| `name`             | string    |      Yes | Subject name               |
| `code`             | string    |       No | University course code     |
| `professor`        | string    |       No | Professor or instructor    |
| `credits`          | number    |       No | Academic credits           |
| `notes`            | string    |       No | User notes                 |
| `createdAt`        | timestamp |      Yes | Creation timestamp         |
| `updatedAt`        | timestamp |      Yes | Last update timestamp      |

A subject belongs to one academic period and can have multiple event definitions.

## 8. Event

Path:

```text
users/{userId}/events/{eventId}
```

| Field              | Type      | Required | Description                      |
|--------------------|-----------|---------:|----------------------------------|
| `title`            | string    |      Yes | Event title                      |
| `subjectId`        | string    |       No | Associated subject               |
| `academicPeriodId` | string    |       No | Associated academic period       |
| `startAt`          | timestamp |      Yes | Event start                      |
| `endAt`            | timestamp |      Yes | Event end                        |
| `recurrenceRuleId` | string    |       No | Associated recurrence definition |
| `locationId`       | string    |       No | Physical location                |
| `meetingUrl`       | string    |       No | Remote meeting URL               |
| `isRemote`         | boolean   |      Yes | Remote event indicator           |
| `notes`            | string    |       No | Private event notes              |
| `createdAt`        | timestamp |      Yes | Creation timestamp               |
| `updatedAt`        | timestamp |      Yes | Last update timestamp            |

An event may be academic or personal, physical or remote, associated with a subject or independent of one.

## 9. Recurrence Rule

Path:

```text
users/{userId}/recurrenceRules/{recurrenceRuleId}
```

| Field       | Type        | Required | Description                               |
|-------------|-------------|---------:|-------------------------------------------|
| `frequency` | string      |      Yes | `DAILY`, `WEEKLY`, or supported frequency |
| `interval`  | number      |      Yes | Repetition interval                       |
| `startDate` | string/date |      Yes | Recurrence start                          |
| `endDate`   | string/date |      Yes | Recurrence end                            |
| `createdAt` | timestamp   |      Yes | Creation timestamp                        |
| `updatedAt` | timestamp   |      Yes | Last update timestamp                     |

One recurrence rule represents one event time interval.

## 10. Recurrence Days

Path:

```text
users/{userId}/recurrenceRules/{recurrenceRuleId}/days/{dayId}
```

| Field       | Type   | Required | Description             |
|-------------|--------|---------:|-------------------------|
| `dayOfWeek` | number |      Yes | ISO weekday from 1 to 7 |

Example:

```text
recurrenceRules/
    {ruleId}/
        days/
            {day1}/
                dayOfWeek: 2
            {day2}/
                dayOfWeek: 4
```

For different weekly time intervals, use separate event definitions.

```text
Event A → Rule A → Tuesday + Thursday → 16:00–18:00
Event B → Rule B → Saturday → 14:00–16:00
```

Both can reference the same subject and academic period.

## 11. Location

Path:

```text
users/{userId}/locations/{locationId}
```

| Field       | Type      | Required | Description                  |
|-------------|-----------|---------:|------------------------------|
| `name`      | string    |      Yes | Location display name        |
| `address`   | string    |       No | Formatted address            |
| `latitude`  | number    |       No | Latitude                     |
| `longitude` | number    |       No | Longitude                    |
| `placeId`   | string    |       No | Google Maps place identifier |
| `createdAt` | timestamp |      Yes | Creation timestamp           |
| `updatedAt` | timestamp |      Yes | Last update timestamp        |

Location data is used for events. UniHub does not use location for arrival-based notifications.

## 12. Task

Path:

```text
users/{userId}/tasks/{taskId}
```

| Field              | Type      | Required | Description                |
|--------------------|-----------|---------:|----------------------------|
| `title`            | string    |      Yes | Task title                 |
| `description`      | string    |       No | Task description           |
| `subjectId`        | string    |       No | Associated subject         |
| `academicPeriodId` | string    |       No | Associated academic period |
| `dueAt`            | timestamp |       No | Deadline                   |
| `status`           | string    |      Yes | Task state                 |
| `priority`         | string    |       No | Priority                   |
| `notes`            | string    |       No | User notes                 |
| `createdAt`        | timestamp |      Yes | Creation timestamp         |
| `updatedAt`        | timestamp |      Yes | Last update timestamp      |

A task can be academic or personal.

## 13. Grade

Path:

```text
users/{userId}/grades/{gradeId}
```

| Field              | Type      | Required | Description                |
|--------------------|-----------|---------:|----------------------------|
| `subjectId`        | string    |      Yes | Associated subject         |
| `academicPeriodId` | string    |      Yes | Associated academic period |
| `name`             | string    |      Yes | Evaluation name            |
| `value`            | number    |      Yes | Grade value                |
| `weight`           | number    |      Yes | Evaluation weight          |
| `createdAt`        | timestamp |      Yes | Creation timestamp         |
| `updatedAt`        | timestamp |      Yes | Last update timestamp      |

Academic calculations are deterministic and do not depend on AI.

## 14. Tag

Path:

```text
users/{userId}/tags/{tagId}
```

| Field       | Type      | Required | Description           |
|-------------|-----------|---------:|-----------------------|
| `name`      | string    |      Yes | Tag name              |
| `createdAt` | timestamp |      Yes | Creation timestamp    |
| `updatedAt` | timestamp |      Yes | Last update timestamp |

Tags may be associated with events or tasks. The final representation will be selected according to actual query requirements.

## 15. Logical Relationships

Firestore does not enforce SQL-style foreign keys. The application therefore maintains logical references.

```text
AcademicPeriod
      │
      └────── 0..N Subject
                    │
                    ├────── 0..N Event
                    ├────── 0..N Task
                    └────── 0..N Grade

Event
  ├────── 0..1 RecurrenceRule
  └────── 0..1 Location
```

Referenced identifiers must belong to the authenticated user before a relationship is created or updated.

## 16. Recurring Event Storage

UniHub does not create one Firestore document per recurring occurrence.

Instead:

```text
Event
  ↓
RecurrenceRule
  ↓
RecurrenceDay(s)
  ↓
Calendar occurrence calculation
```

For example:

```text
Event:
    title: "Computación Móvil"
    startAt: 2026-08-04T16:00
    endAt: 2026-08-04T18:00

RecurrenceRule:
    frequency: WEEKLY
    startDate: 2026-08-03
    endDate: 2026-12-06

RecurrenceDays:
    TUESDAY
    THURSDAY
```

The calendar generates occurrences for the visible date range.

## 17. Room Synchronization

Room remains the local persistence layer.

Remote-to-local flow:

```text
Firestore
    ↓
Remote DTO
    ↓
Mapper
    ↓
Room Entity
    ↓
DAO
    ↓
Local Repository
    ↓
Domain Model
    ↓
Use Case
    ↓
ViewModel
    ↓
Compose UI
```

Local-to-cloud flow:

```text
Compose UI
    ↓
ViewModel
    ↓
Use Case
    ↓
Repository
    ↓
Room
    ↓
Synchronization
    ↓
Firestore
```

The final synchronization strategy must define conflict behavior before multi-device synchronization is considered complete.

## 18. Offline Behavior

UniHub should remain useful without network connectivity.

The MVP prioritizes:

- Reading locally persisted subjects.
- Reading events.
- Reading tasks.
- Reading grades.
- Performing academic calculations locally.
- Creating and editing supported local data.

Cloud synchronization resumes when connectivity is restored.

Network-dependent operations must expose appropriate loading, offline, and error states.

## 19. Security Boundary

The proposed structure supports user-scoped Firestore Security Rules:

```text
users/{userId}/...
        │
        └── request.auth.uid == userId
```

Security details are documented in:

[Security Documentation](../security/SECURITY.md)

Client-side UI restrictions are not considered authorization.

## 20. Query and Index Considerations

Expected queries include:

```text
Events by user + date range
Events by user + subject
Events by user + recurrence
Tasks by user + due date
Tasks by user + status
Grades by user + subject
Grades by user + academic period
Subjects by user + academic period
```

Firestore composite indexes should be created only when required by actual queries.

## 21. Data Validation

The application must validate:

- Required fields.
- Valid identifiers.
- Event start/end consistency.
- Recurrence date ranges.
- Valid recurrence days.
- Subject ownership.
- Academic-period ownership.
- Location ownership.
- Grade values.
- Grade weights.
- Task status values.
- Meeting URLs where applicable.

Security Rules provide a second boundary for ownership and supported write conditions.

## 22. Data Lifecycle

Academic periods are not deleted simply because a semester ends.

Multiple periods may coexist:

```text
2026-1
2026-2
2027-1
...
```

The active academic period determines the default context shown by the application.

Historical information remains available unless explicitly deleted by the user.

## 23. Relationship with the Relational Model

The normalized Room model is documented in:

[Database Documentation](DATABASE.md)

The Room model remains relational and normalized.

Firestore is intentionally organized around document access patterns and ownership.

Therefore:

```text
Room tables ≠ Firestore collections
```

What must remain consistent is the business meaning of the data.

## 24. Evolution

Any schema change must be reflected in:

1. Firestore model.
2. Room model.
3. Domain models where affected.
4. Mappers.
5. Repository implementations.
6. Security Rules.
7. Relevant tests.
8. Database documentation.

Existing cloud documents must be considered before introducing breaking changes.

