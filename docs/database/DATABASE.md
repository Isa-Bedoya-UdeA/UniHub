# UniHub — Database

## 1. Document Information

| Field          | Value                     |
|----------------|---------------------------|
| Project        | UniHub                    |
| Document       | Database                  |
| Version        | 1.0                       |
| Status         | Initial version           |
| Persistence    | Room + Firebase Firestore |
| Local database | SQLite through Room       |
| Cloud database | Cloud Firestore           |

## 2. Purpose

This document defines the relational data model and persistence strategy for UniHub.

The relational model is designed for Room and describes the entities, attributes, keys, constraints, cardinalities, and referential relationships required by the current product scope.

The Firestore model represents the same application data as user-owned documents and is intentionally not a literal copy of the relational schema.

The model includes academic programs (studies), periods, subjects, events, event reminders, recurring event rules, locations, tasks, grades, and tags.

## 3. Persistence Strategy

| Storage                 | Role                                       | Data model               |
|-------------------------|--------------------------------------------|--------------------------|
| Room                    | Local persistence and offline-first access | Relational               |
| Firebase Firestore      | Cloud persistence and synchronization      | Document-oriented        |
| Firebase Authentication | Authentication and identity                | Managed identity service |

The authenticated Firebase UID is used as the stable application `User.user_id`.

Room entities are persistence representations. Domain models must not be exposed directly from the infrastructure layer to the presentation layer.

## 4. Relational Model

### 4.1 Design Conventions

Relational entities use singular PascalCase names.

```text
User
Study
AcademicPeriod
Subject
Event
EventReminder
RecurrenceRule
RecurrenceDay
Location
Task
Grade
Tag
EventTag
TaskTag
UserPreferences
```

The following conventions apply:

- Primary keys use `TEXT` identifiers.
- Foreign keys use the same `TEXT` type as their referenced primary key.
- Required attributes are `NOT NULL`.
- Optional attributes are nullable.
- Date and date-time values use ISO-8601-compatible `TEXT` representations.
- Time-only values use `TEXT` in `HH:mm` format.
- Boolean values use Room-supported Boolean mapping.
- Enumerated states use controlled string values or Kotlin enums converted by Room.
- Many-to-many relationships use associative entities.
- Recurrence days are normalized into a separate entity instead of storing multiple weekdays in one column.
- Domain models are mapped to and from Room entities through mappers.

### 4.2 `User`

| Column              | Type | Constraints  | Description                                         |
|---------------------|------|--------------|-----------------------------------------------------|
| `user_id`           | TEXT | PK, NOT NULL | Firebase UID and stable application user identifier |
| `name`              | TEXT | NOT NULL     | User display name                                   |
| `email`             | TEXT | NOT NULL     | User email                                          |
| `profile_image_url` | TEXT | NULL         | Profile image URL                                   |
| `created_at`        | TEXT | NOT NULL     | Creation timestamp                                  |
| `updated_at`        | TEXT | NOT NULL     | Last update timestamp                               |

**Relationships**

- `User` 1 — 0..N `Study`
- `User` 1 — 0..N `AcademicPeriod`
- `User` 1 — 0..N `Subject`
- `User` 1 — 0..N `Event`
- `User` 1 — 0..N `RecurrenceRule`
- `User` 1 — 0..N `Location`
- `User` 1 — 0..N `Task`
- `User` 1 — 0..N `Grade`
- `User` 1 — 0..N `Tag`
- `User` 1 — 1 `UserPreferences`

### 4.3 `Study`

Represents an academic program or career.

| Column          | Type    | Constraints  | Description                                 |
|-----------------|---------|--------------|---------------------------------------------|
| `study_id`      | TEXT    | PK, NOT NULL | Unique study identifier                     |
| `user_id`       | TEXT    | FK, NOT NULL | Owner                                       |
| `name`          | TEXT    | NOT NULL     | Name of the study (e.g., "Ing de Sistemas") |
| `institution`   | TEXT    | NOT NULL     | Institution (e.g., "UdeA")                  |
| `total_credits` | INTEGER | NOT NULL     | Total credits required for the program      |
| `is_active`     | INTEGER | NOT NULL     | Whether this is the currently active study  |
| `created_at`    | TEXT    | NOT NULL     | Creation timestamp                          |
| `updated_at`    | TEXT    | NOT NULL     | Last update timestamp                       |

**Relationships**

- `User` 1 — 0..N `Study`
- `Study` 1 — 0..N `AcademicPeriod`
- `Study` 1 — 0..N `Subject`

### 4.4 `AcademicPeriod`

Represents an academic period, normally a university semester.

| Column               | Type    | Constraints  | Description                                        |
|----------------------|---------|--------------|----------------------------------------------------|
| `academic_period_id` | TEXT    | PK, NOT NULL | Unique period identifier                           |
| `user_id`            | TEXT    | FK, NOT NULL | Owner                                              |
| `study_id`           | TEXT    | FK, NOT NULL | Associated study/program                           |
| `name`               | TEXT    | NOT NULL     | Period name, such as `2026-2`                      |
| `start_date`         | TEXT    | NOT NULL     | Period start date                                  |
| `end_date`           | TEXT    | NOT NULL     | Period end date                                    |
| `is_current`         | INTEGER | NOT NULL     | Whether this is the user's current academic period |
| `created_at`         | TEXT    | NOT NULL     | Creation timestamp                                 |
| `updated_at`         | TEXT    | NOT NULL     | Last update timestamp                              |

**Relationships**

- `User` 1 — 0..N `AcademicPeriod`
- `Study` 1 — 0..N `AcademicPeriod`
- `AcademicPeriod` 1 — 0..N `Subject`
- `AcademicPeriod` 1 — 0..N `Event`
- `AcademicPeriod` 1 — 0..N `Task`

### 4.5 `Subject`

| Column               | Type    | Constraints  | Description                    |
|----------------------|---------|--------------|--------------------------------|
| `subject_id`         | TEXT    | PK, NOT NULL | Unique subject identifier      |
| `user_id`            | TEXT    | FK, NOT NULL | Owner                          |
| `study_id`           | TEXT    | FK, NOT NULL | Associated study/program       |
| `academic_period_id` | TEXT    | FK, NOT NULL | Academic period                |
| `name`               | TEXT    | NOT NULL     | Subject name                   |
| `code`               | TEXT    | NULL         | University subject code        |
| `credits`            | INTEGER | NULL         | Academic credits               |
| `professor`          | TEXT    | NULL         | Professor name                 |
| `color`              | TEXT    | NULL         | User/interface color reference |
| `notes`              | TEXT    | NULL         | Additional notes               |
| `created_at`         | TEXT    | NOT NULL     | Creation timestamp             |
| `updated_at`         | TEXT    | NOT NULL     | Last update timestamp          |

**Relationships**

- `User` 1 — 0..N `Subject`
- `Study` 1 — 0..N `Subject`
- `AcademicPeriod` 1 — 0..N `Subject`
- `Subject` 1 — 0..N `Event`
- `Subject` 1 — 0..N `Task`
- `Subject` 1 — 0..N `Grade`

### 4.6 `Location`

| Column        | Type | Constraints  | Description                              |
|---------------|------|--------------|------------------------------------------|
| `location_id` | TEXT | PK, NOT NULL | Unique location identifier               |
| `user_id`     | TEXT | FK, NOT NULL | Owner                                    |
| `name`        | TEXT | NULL         | User-facing location name                |
| `address`     | TEXT | NULL         | Human-readable address                   |
| `latitude`    | REAL | NULL         | Geographic latitude                      |
| `longitude`   | REAL | NULL         | Geographic longitude                     |
| `place_id`    | TEXT | NULL         | External place identifier when available |
| `created_at`  | TEXT | NOT NULL     | Creation timestamp                       |
| `updated_at`  | TEXT | NOT NULL     | Last update timestamp                    |

**Relationships**

- `User` 1 — 0..N `Location`
- `Location` 0..1 — 0..N `Event`

### 4.7 `Event`

Represents an individual calendar event or the event definition from which recurring occurrences are calculated.

| Column               | Type | Constraints      | Description                                         |
|----------------------|------|------------------|-----------------------------------------------------|
| `event_id`           | TEXT | PK, NOT NULL     | Unique event identifier                             |
| `user_id`            | TEXT | FK, NOT NULL     | Owner                                               |
| `academic_period_id` | TEXT | FK, NULL         | Optional academic period                            |
| `subject_id`         | TEXT | FK, NULL         | Optional subject                                    |
| `location_id`        | TEXT | FK, NULL         | Optional physical location                          |
| `recurrence_rule_id` | TEXT | FK, NULL, UNIQUE | Optional recurrence rule                            |
| `title`              | TEXT | NOT NULL         | Event name                                          |
| `start_at`           | TEXT | NOT NULL         | Start date and time of the first or only occurrence |
| `end_at`             | TEXT | NOT NULL         | End date and time of the first or only occurrence   |
| `location_type`      | TEXT | NOT NULL         | `PHYSICAL`, `REMOTE`, or `NONE`                     |
| `meeting_url`        | TEXT | NULL             | Remote meeting URL                                  |
| `notes`              | TEXT | NULL             | Additional notes                                    |
| `created_at`         | TEXT | NOT NULL         | Creation timestamp                                  |
| `updated_at`         | TEXT | NOT NULL         | Last update timestamp                               |

**Relationships**

- `User` 1 — 0..N `Event`
- `AcademicPeriod` 0..1 — 0..N `Event`
- `Subject` 0..1 — 0..N `Event`
- `Location` 0..1 — 0..N `Event`
- `RecurrenceRule` 0..1 — 0..1 `Event`
- `Event` 1 — 0..N `EventReminder`

### 4.8 `EventReminder`

Represents a reminder configuration for an event. Multiple reminders can be configured per event with different time units (minutes, hours, or days before the event).

| Column          | Type    | Constraints  | Description                                               |
|-----------------|---------|--------------|-----------------------------------------------------------|
| `reminder_id`   | TEXT    | PK, NOT NULL | Unique reminder identifier                                |
| `event_id`      | TEXT    | FK, NOT NULL | Associated event                                          |
| `reminder_type` | TEXT    | NOT NULL     | `MINUTES_BEFORE`, `HOURS_BEFORE`, or `DAYS_BEFORE`        |
| `value`         | INTEGER | NOT NULL     | Number of time units before the event to trigger reminder |
| `is_enabled`    | INTEGER | NOT NULL     | Whether this reminder is active (default: 1)              |

**Relationships**

- `Event` 1 — 0..N `EventReminder`

**Notes**

- Reminders are deleted when the associated event is deleted (CASCADE).
- Multiple reminders can be configured per event.
- Each reminder can be individually enabled or disabled.
- The `reminder_type` determines the time unit for the `value` field.

### 4.9 `RecurrenceRule`

Represents the recurrence configuration of an event.

| Column               | Type    | Constraints  | Description                                   |
|----------------------|---------|--------------|-----------------------------------------------|
| `recurrence_rule_id` | TEXT    | PK, NOT NULL | Unique recurrence rule identifier             |
| `user_id`            | TEXT    | FK, NOT NULL | Owner                                         |
| `frequency`          | TEXT    | NOT NULL     | `DAILY` or `WEEKLY`                           |
| `interval`           | INTEGER | NOT NULL     | Number of frequency units between repetitions |
| `start_date`         | TEXT    | NOT NULL     | First date included in the recurrence         |
| `end_date`           | TEXT    | NOT NULL     | Last date included in the recurrence          |
| `created_at`         | TEXT    | NOT NULL     | Creation timestamp                            |
| `updated_at`         | TEXT    | NOT NULL     | Last update timestamp                         |

### 4.10 `RecurrenceDay`

Represents one weekday selected by a weekly recurrence rule.

| Column               | Type    | Constraints      | Description                    |
|----------------------|---------|------------------|--------------------------------|
| `recurrence_rule_id` | TEXT    | PK, FK, NOT NULL | Recurrence rule                |
| `day_of_week`        | INTEGER | PK, NOT NULL     | ISO weekday number from 1 to 7 |

### 4.11 `Task`

| Column               | Type | Constraints  | Description                              |
|----------------------|------|--------------|------------------------------------------|
| `task_id`            | TEXT | PK, NOT NULL | Unique task identifier                   |
| `user_id`            | TEXT | FK, NOT NULL | Owner                                    |
| `academic_period_id` | TEXT | FK, NULL     | Optional academic period                 |
| `subject_id`         | TEXT | FK, NULL     | Optional subject                         |
| `title`              | TEXT | NOT NULL     | Task title                               |
| `description`        | TEXT | NULL         | Task description                         |
| `due_at`             | TEXT | NULL         | Deadline                                 |
| `priority`           | TEXT | NOT NULL     | `LOW`, `MEDIUM`, or `HIGH`               |
| `status`             | TEXT | NOT NULL     | `PENDING`, `IN_PROGRESS`, or `COMPLETED` |
| `notes`              | TEXT | NULL         | Additional notes                         |
| `created_at`         | TEXT | NOT NULL     | Creation timestamp                       |
| `updated_at`         | TEXT | NOT NULL     | Last update timestamp                    |

**Relationships**

- `User` 1 — 0..N `Task`
- `AcademicPeriod` 0..1 — 0..N `Task`
- `Subject` 0..1 — 0..N `Task`

### 4.12 `Grade`

| Column       | Type | Constraints  | Description                 |
|--------------|------|--------------|-----------------------------|
| `grade_id`   | TEXT | PK, NOT NULL | Unique grade identifier     |
| `user_id`    | TEXT | FK, NOT NULL | Owner                       |
| `subject_id` | TEXT | FK, NOT NULL | Subject receiving the grade |
| `name`       | TEXT | NOT NULL     | Evaluation name             |
| `value`      | REAL | NOT NULL     | Grade value                 |
| `weight`     | REAL | NOT NULL     | Evaluation weight           |
| `notes`      | TEXT | NULL         | Additional notes            |
| `created_at` | TEXT | NOT NULL     | Creation timestamp          |
| `updated_at` | TEXT | NOT NULL     | Last update timestamp       |

### 4.13 `Tag`

| Column       | Type | Constraints  | Description           |
|--------------|------|--------------|-----------------------|
| `tag_id`     | TEXT | PK, NOT NULL | Unique tag identifier |
| `user_id`    | TEXT | FK, NOT NULL | Owner                 |
| `name`       | TEXT | NOT NULL     | Tag name              |
| `created_at` | TEXT | NOT NULL     | Creation timestamp    |

### 4.14 `EventTag`

| Column     | Type | Constraints      | Description      |
|------------|------|------------------|------------------|
| `event_id` | TEXT | PK, FK, NOT NULL | Event identifier |
| `tag_id`   | TEXT | PK, FK, NOT NULL | Tag identifier   |

### 4.15 `TaskTag`

| Column    | Type | Constraints      | Description     |
|-----------|------|------------------|-----------------|
| `task_id` | TEXT | PK, FK, NOT NULL | Task identifier |
|  `tag_id` | TEXT | PK, FK, NOT NULL | Tag identifier  |

### 4.16 `UserPreferences`

| Column       | Type | Constraints  | Description                                  |
|--------------|------|--------------|----------------------------------------------|
| `user_id`    | TEXT | PK, NOT NULL | Owner                                        |
| `theme_mode` | TEXT | NOT NULL     | Application theme: `LIGHT`, `DARK`, `SYSTEM` |

## 5. Relational Relationships Summary

| Relationship                       | Cardinality | Foreign key                        |
|------------------------------------|------------:|------------------------------------|
| `User` → `Study`                   |    1 : 0..N | `Study.user_id`                    |
| `User` → `AcademicPeriod`          |    1 : 0..N | `AcademicPeriod.user_id`           |
| `Study` → `AcademicPeriod`         |    1 : 0..N | `AcademicPeriod.study_id`          |
| `User` → `Subject`                 |    1 : 0..N | `Subject.user_id`                  |
| `Study` → `Subject`                |    1 : 0..N | `Subject.study_id`                 |
| `AcademicPeriod` → `Subject`       |    1 : 0..N | `Subject.academic_period_id`       |
| `User` → `Event`                   |    1 : 0..N | `Event.user_id`                    |
| `AcademicPeriod` → `Event`         | 0..1 : 0..N | `Event.academic_period_id`         |
| `Subject` → `Event`                | 0..1 : 0..N | `Event.subject_id`                 |
| `Location` → `Event`               | 0..1 : 0..N | `Event.location_id`                |
| `Event` → `EventReminder`          |    1 : 0..N | `EventReminder.event_id`           |
| `User` → `RecurrenceRule`          |    1 : 0..N | `RecurrenceRule.user_id`           |
| `RecurrenceRule` → `Event`         |       1 : 1 | `Event.recurrence_rule_id`         |
| `RecurrenceRule` → `RecurrenceDay` |    1 : 1..N | `RecurrenceDay.recurrence_rule_id` |
| `User` → `Location`                |    1 : 0..N | `Location.user_id`                 |
| `User` → `Task`                    |    1 : 0..N | `Task.user_id`                     |
| `AcademicPeriod` → `Task`          | 0..1 : 0..N | `Task.academic_period_id`          |
| `Subject` → `Task`                 | 0..1 : 0..N | `Task.subject_id`                  |
| `User` → `Grade`                   |    1 : 0..N | `Grade.user_id`                    |
| `Subject` → `Grade`                |    1 : 0..N | `Grade.subject_id`                 |
| `User` → `Tag`                     |    1 : 0..N | `Tag.user_id`                      |
| `Event` → `EventTag`               |    1 : 0..N | `EventTag.event_id`                |
| `Tag` → `EventTag`                 |    1 : 0..N | `EventTag.tag_id`                  |
| `Task` → `TaskTag`                 |    1 : 0..N | `TaskTag.task_id`                  |
| `Tag` → `TaskTag`                  |    1 : 0..N | `TaskTag.tag_id`                   |
|  `User` → `UserPreferences`        |       1 : 1 | `UserPreferences.user_id`          |

## 6. Referential Integrity

| Parent           | Child               | Recommended behavior           |
|------------------|---------------------|--------------------------------|
| `User`           | User-owned entities | Controlled account cleanup     |
| `Study`          | `AcademicPeriod`    | RESTRICT                       |
| `Study`          | `Subject`           | RESTRICT                       |
| `AcademicPeriod` | `Subject`           | RESTRICT / controlled deletion |
| `AcademicPeriod` | `Event`             | SET NULL                       |
| `AcademicPeriod` | `Task`              | SET NULL                       |
| `Subject`        | `Event`             | SET NULL                       |
| `Subject`        | `Task`              | SET NULL                       |
| `Subject`        | `Grade`             | RESTRICT / controlled deletion |
| `Location`       | `Event`             | SET NULL                       |
| `Event`          | `EventReminder`     | CASCADE                        |
| `RecurrenceRule` | `Event`             | CASCADE                        |
| `RecurrenceRule` | `RecurrenceDay`     | CASCADE                        |
| `Event`          | `EventTag`          | CASCADE                        |
| `Task`           | `TaskTag`           | CASCADE                        |
| `Tag`            | `EventTag`          | CASCADE                        |
| `Tag`            | `TaskTag`           | CASCADE                        |

## 7. Room Implementation

Feature-specific Room entities and DAOs belong inside the corresponding feature's infrastructure layer.

## 8. Firestore Document Model

Firestore is document-oriented and does not reproduce the Room schema literally.

```text
users/
    {userId}/
        profile/
            ...

        studies/
            {studyId}/
                ...

        academicPeriods/
            {academicPeriodId}/
                ...

        subjects/
            {subjectId}/
                ...

        events/
            {eventId}/
                reminders/
                    {reminderId}/
                        ...

        recurrenceRules/
            {recurrenceRuleId}/
                days/
                    {dayId}/
                        ...

        locations/
            {locationId}/
                ...

        tasks/
            {taskId}/
                ...

        grades/
            {gradeId}/
                ...

        tags/
            {tagId}/
                ...
        
        preferences/
            settings/
                themeMode: "SYSTEM"
```
