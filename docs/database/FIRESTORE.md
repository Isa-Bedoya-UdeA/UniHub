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
| `notes`            | string    |       No | User notes                 |
| `createdAt`        | timestamp |      Yes | Creation timestamp         |
| `updatedAt`        | timestamp |      Yes | Last update timestamp      |

## 9. Event

Path: `users/{userId}/events/{eventId}`

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
| `locationType`     | string    |      Yes | `PHYSICAL`, `REMOTE`, or `NONE`  |
| `notes`            | string    |       No | Private event notes              |
| `createdAt`        | timestamp |      Yes | Creation timestamp               |
| `updatedAt`        | timestamp |      Yes | Last update timestamp            |

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

## 14. Grade

Path: `users/{userId}/grades/{gradeId}`

| Field              | Type      | Required | Description                |
|--------------------|-----------|---------:|----------------------------|
| `subjectId`        | string    |      Yes | Associated subject         |
| `academicPeriodId` | string    |      Yes | Associated academic period |
| `name`             | string    |      Yes | Evaluation name            |
| `value`            | number    |      Yes | Grade value                |
| `weight`           | number    |      Yes | Evaluation weight          |
| `createdAt`        | timestamp |      Yes | Creation timestamp         |
| `updatedAt`        | timestamp |      Yes | Last update timestamp      |

## 15. Tag

Path: `users/{userId}/tags/{tagId}`

| Field       | Type      | Required | Description           |
|-------------|-----------|---------:|-----------------------|
| `name`      | string    |      Yes | Tag name              |
| `createdAt` | timestamp |      Yes | Creation timestamp    |
| `updatedAt` | timestamp |      Yes | Last update timestamp |

## 16. Preferences

Path: `users/{userId}/preferences/settings`

| Field       | Type      | Required | Description                        |
|-------------|-----------|---------:|------------------------------------|
| `themeMode` | string    |      Yes | `LIGHT`, `DARK`, or `SYSTEM`       |

## 17. Logical Relationships

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
