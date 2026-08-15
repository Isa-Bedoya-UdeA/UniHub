# UniHub — Database

## 1. Document Information

| Field          | Value                     |
|----------------|---------------------------|
| Project        | UniHub                    |
| Document       | Database                  |
| Version        | 0.1                       |
| Status         | Initial Design            |
| Persistence    | Room + Firebase Firestore |
| Local database | SQLite through Room       |
| Cloud database | Cloud Firestore           |

## 2. Purpose

This document defines the initial data model and persistence strategy for UniHub.

The relational model is designed for the local Room database and represents the entities, attributes, keys, constraints, and cardinalities required by the current product scope. The cloud model uses Firebase Firestore and therefore does not reproduce the relational schema literally.

The model covers the current academic core of the application: users, academic periods, subjects, events, locations, tasks, grades, and tags.

The design is versioned and may evolve as implementation, synchronization requirements, and testing provide new information.

## 3. Persistence Strategy

UniHub uses two persistence mechanisms with different responsibilities.

| Storage                 | Role                                       | Data model               |
|-------------------------|--------------------------------------------|--------------------------|
| Room                    | Local persistence and offline-first access | Relational               |
| Firebase Firestore      | Cloud persistence and synchronization      | Document-oriented        |
| Firebase Authentication | Authentication and identity                | Managed identity service |

Room is the relational source for the application's local data access layer. Firestore is the cloud representation of user-owned data and should be treated as a document model rather than as a SQL database.

Authentication identity is managed by Firebase Authentication. The authenticated Firebase UID is used as the stable user identifier when associating cloud data with a user.

## 4. Relational Model

### 4.1 Design Conventions

Relational entities and Room table names use singular PascalCase naming. Firestore collection names are intentionally plural because they represent document collections, not relational tables.

The Room relational model follows these conventions:

- Primary keys use `TEXT` identifiers generated as UUIDs unless otherwise specified.
- Foreign keys use the same `TEXT` type as the referenced primary key.
- Required attributes are `NOT NULL`.
- Optional attributes are nullable.
- Dates and times use a consistent application representation and are converted through Room converters where necessary.
- Boolean values use Room-supported Boolean mapping.
- Enumerated states are represented as controlled string values or Kotlin enums converted by Room.
- Many-to-many relationships are represented through associative tables.
- Domain models are not used directly as Room entities.

### 4.2 `User`

Represents the local application profile associated with an authenticated Firebase user.

| Column              | Type | Constraints  | Description                                         |
|---------------------|------|--------------|-----------------------------------------------------|
| `user_id`           | TEXT | PK, NOT NULL | Firebase UID and stable application user identifier |
| `name`              | TEXT | NOT NULL     | User display name                                   |
| `email`             | TEXT | NOT NULL     | User email                                          |
| `profile_image_url` | TEXT | NULL         | Profile image URL                                   |
| `created_at`        | TEXT | NOT NULL     | Profile creation timestamp                          |
| `updated_at`        | TEXT | NOT NULL     | Last profile update timestamp                       |

**Relationships**

- `User` 1 — 0..N `AcademicPeriod`
- `User` 1 — 0..N `Subject`
- `User` 1 — 0..N `Event`
- `User` 1 — 0..N `Location`
- `User` 1 — 0..N `Task`
- `User` 1 — 0..N `Grade`
- `User` 1 — 0..N `Tag`

### 4.3 `AcademicPeriod`

Represents an academic period in which subjects are taken.

| Column               | Type    | Constraints  | Description                            |
|----------------------|---------|--------------|----------------------------------------|
| `academic_period_id` | TEXT    | PK, NOT NULL | Unique period identifier               |
| `user_id`            | TEXT    | FK, NOT NULL | Owner                                  |
| `name`               | TEXT    | NOT NULL     | Period name                            |
| `start_date`         | TEXT    | NOT NULL     | Period start date                      |
| `end_date`           | TEXT    | NOT NULL     | Period end date                        |
| `is_current`         | INTEGER | NOT NULL     | Whether the period is currently active |
| `created_at`         | TEXT    | NOT NULL     | Creation timestamp                     |
| `updated_at`         | TEXT    | NOT NULL     | Last update timestamp                  |

**Relationships**

- `User` 1 — 0..N `AcademicPeriod`
- `AcademicPeriod` 1 — 0..N `Subject`
- `AcademicPeriod` 1 — 0..N `Event`
- `AcademicPeriod` 1 — 0..N `Task`

### 4.4 `Subject`

Represents a university subject.

| Column               | Type    | Constraints  | Description                    |
|----------------------|---------|--------------|--------------------------------|
| `subject_id`         | TEXT    | PK, NOT NULL | Unique subject identifier      |
| `user_id`            | TEXT    | FK, NOT NULL | Owner                          |
| `academic_period_id` | TEXT    | FK, NOT NULL | Academic period                |
| `name`               | TEXT    | NOT NULL     | Subject name                   |
| `code`               | TEXT    | NULL         | Subject code                   |
| `credits`            | INTEGER | NULL         | Academic credits               |
| `professor`          | TEXT    | NULL         | Professor name                 |
| `color`              | TEXT    | NULL         | User/interface color reference |
| `notes`              | TEXT    | NULL         | Additional notes               |
| `created_at`         | TEXT    | NOT NULL     | Creation timestamp             |
| `updated_at`         | TEXT    | NOT NULL     | Last update timestamp          |

**Relationships**

- `User` 1 — 0..N `Subject`
- `AcademicPeriod` 1 — 0..N `Subject`
- `Subject` 1 — 0..N `Event`
- `Subject` 1 — 0..N `Task`
- `Subject` 1 — 0..N `Grade`

A subject may exist without events, tasks, or grades.

### 4.5 `Location`

Represents a reusable physical location associated with an event.

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
- `Location` 1 — 0..N `Event`

A location may be stored independently so that the same physical location can be reused by multiple events.

### 4.6 `Event`

Represents academic or personal calendar events.

| Column               | Type | Constraints  | Description                |
|----------------------|------|--------------|----------------------------|
| `event_id`           | TEXT | PK, NOT NULL | Unique event identifier    |
| `user_id`            | TEXT | FK, NOT NULL | Owner                      |
| `academic_period_id` | TEXT | FK, NULL     | Optional academic period   |
| `subject_id`         | TEXT | FK, NULL     | Optional subject           |
| `location_id`        | TEXT | FK, NULL     | Optional physical location |
| `title`              | TEXT | NOT NULL     | Event name                 |
| `start_at`           | TEXT | NOT NULL     | Start date and time        |
| `end_at`             | TEXT | NOT NULL     | End date and time          |
| `location_type`      | TEXT | NOT NULL     | Physical, remote, or none  |
| `meeting_url`        | TEXT | NULL         | Remote meeting URL         |
| `notes`              | TEXT | NULL         | Additional notes           |
| `created_at`         | TEXT | NOT NULL     | Creation timestamp         |
| `updated_at`         | TEXT | NOT NULL     | Last update timestamp      |

**Relationships**

- `User` 1 — 0..N `Event`
- `AcademicPeriod` 1 — 0..N `Event`
- `Subject` 1 — 0..N `Event`
- `Location` 0..1 — 0..N `Event`

An event may have no subject, no academic period, and no location. A remote event may use `meeting_url` instead of `location_id`.

### 4.7 `Task`

Represents an academic task or deadline.

| Column               | Type | Constraints  | Description                        |
|----------------------|------|--------------|------------------------------------|
| `task_id`            | TEXT | PK, NOT NULL | Unique task identifier             |
| `user_id`            | TEXT | FK, NOT NULL | Owner                              |
| `academic_period_id` | TEXT | FK, NULL     | Optional academic period           |
| `subject_id`         | TEXT | FK, NULL     | Optional subject                   |
| `title`              | TEXT | NOT NULL     | Task title                         |
| `description`        | TEXT | NULL         | Task description                   |
| `due_at`             | TEXT | NULL         | Deadline                           |
| `priority`           | TEXT | NOT NULL     | Low, medium, or high               |
| `status`             | TEXT | NOT NULL     | Pending, in progress, or completed |
| `notes`              | TEXT | NULL         | Additional notes                   |
| `created_at`         | TEXT | NOT NULL     | Creation timestamp                 |
| `updated_at`         | TEXT | NOT NULL     | Last update timestamp              |

**Relationships**

- `User` 1 — 0..N `Task`
- `AcademicPeriod` 1 — 0..N `Task`
- `Subject` 1 — 0..N `Task`

A task may exist without a subject.

### 4.8 `Grade`

Represents an evaluation or grade registered for a subject.

| Column       | Type | Constraints  | Description                 |
|--------------|------|--------------|-----------------------------|
| `grade_id`   | TEXT | PK, NOT NULL | Unique grade identifier     |
| `user_id`    | TEXT | FK, NOT NULL | Owner                       |
| `subject_id` | TEXT | FK, NOT NULL | Subject receiving the grade |
| `name`       | TEXT | NOT NULL     | Evaluation name or type     |
| `value`      | REAL | NOT NULL     | Grade value                 |
| `weight`     | REAL | NOT NULL     | Evaluation weight           |
| `notes`      | TEXT | NULL         | Additional notes            |
| `created_at` | TEXT | NOT NULL     | Creation timestamp          |
| `updated_at` | TEXT | NOT NULL     | Last update timestamp       |

**Relationships**

- `User` 1 — 0..N `Grade`
- `Subject` 1 — 0..N `Grade`

A grade cannot exist without a subject.

The grade simulator does not require a persistent `grade_simulations` table in the initial model because its calculations are deterministic application logic based on existing grades, weights, and user-provided target values.

### 4.9 `Tag`

Represents a reusable tag that can be assigned to events and tasks.

| Column       | Type | Constraints  | Description           |
|--------------|------|--------------|-----------------------|
| `tag_id`     | TEXT | PK, NOT NULL | Unique tag identifier |
| `user_id`    | TEXT | FK, NOT NULL | Owner                 |
| `name`       | TEXT | NOT NULL     | Tag name              |
| `created_at` | TEXT | NOT NULL     | Creation timestamp    |

**Relationships**

- `User` 1 — 0..N `Tag`
- `Event` N — 0..N `Tag` through `EventTag`
- `Task` N — 0..N `Tag` through `TaskTag`

### 4.10 `EventTag`

Associative table for the many-to-many relationship between events and tags.

| Column     | Type | Constraints      | Description      |
|------------|------|------------------|------------------|
| `event_id` | TEXT | PK, FK, NOT NULL | Event identifier |
| `tag_id`   | TEXT | PK, FK, NOT NULL | Tag identifier   |

**Relationships**

- `Event` 1 — 0..N `EventTag`
- `Tag` 1 — 0..N `EventTag`

The composite primary key `(event_id, tag_id)` prevents the same tag from being assigned to the same event more than once.

### 4.11 `TaskTag`

Associative table for the many-to-many relationship between tasks and tags.

| Column    | Type | Constraints      | Description     |
|-----------|------|------------------|-----------------|
| `task_id` | TEXT | PK, FK, NOT NULL | Task identifier |
| `tag_id`  | TEXT | PK, FK, NOT NULL | Tag identifier  |

**Relationships**

- `Task` 1 — 0..N `TaskTag`
- `Tag` 1 — 0..N `TaskTag`

The composite primary key `(task_id, tag_id)` prevents the same tag from being assigned to the same task more than once.

## 5. Relational Relationships Summary

| Relationship              | Cardinality | Foreign key                  |
|---------------------------|-------------|------------------------------|
| User → Academic Period    | 1 : 0..N    | `AcademicPeriod.user_id`     |
| User → Subject            | 1 : 0..N    | `Subject.user_id`            |
| Academic Period → Subject | 1 : 0..N    | `Subject.academic_period_id` |
| User → Event              | 1 : 0..N    | `Event.user_id`              |
| Academic Period → Event   | 1 : 0..N    | `Event.academic_period_id`   |
| Subject → Event           | 1 : 0..N    | `Event.subject_id`           |
| User → Location           | 1 : 0..N    | `Location.user_id`           |
| Location → Event          | 0..1 : 0..N | `Event.location_id`          |
| User → Task               | 1 : 0..N    | `Task.user_id`               |
| Academic Period → Task    | 1 : 0..N    | `Task.academic_period_id`    |
| Subject → Task            | 1 : 0..N    | `Task.subject_id`            |
| User → Grade              | 1 : 0..N    | `Grade.user_id`              |
| Subject → Grade           | 1 : 0..N    | `Grade.subject_id`           |
| User → Tag                | 1 : 0..N    | `Tag.user_id`                |
| Event → Tag               | 0..N : 0..N | `EventTag`                   |
| Task → Tag                | 0..N : 0..N | `TaskTag`                    |

## 6. Referential Integrity

Room should enforce foreign-key relationships wherever the local model requires them.

| Parent         | Child                   | Recommended behavior                  |
|----------------|-------------------------|---------------------------------------|
| User           | All user-owned entities | CASCADE or controlled account cleanup |
| AcademicPeriod | Subject                 | RESTRICT / controlled deletion        |
| AcademicPeriod | Event                   | SET NULL                              |
| AcademicPeriod | Task                    | SET NULL                              |
| Subject        | Event                   | SET NULL                              |
| Subject        | Task                    | SET NULL                              |
| Subject        | Grade                   | RESTRICT or controlled deletion       |
| Location       | Event                   | SET NULL                              |
| Event          | EventTag                | CASCADE                               |
| Task           | TaskTag                 | CASCADE                               |
| Tag            | EventTag                | CASCADE                               |
| Tag            | TaskTag                 | CASCADE                               |

The exact Room `onDelete` behavior should be validated against the final UX for deletion. In particular, deleting a subject must not silently delete academic grades unless that behavior is explicitly confirmed.

## 7. Room Implementation

The relational model will be implemented with Room.

### 7.1 Package Location

Feature-specific Room entities and DAOs belong inside the corresponding feature's infrastructure layer:

```text
com.unihub.app/
└── features/
    └── xFeature/
        └── infrastructure/
            └── data/
                └── local/
                    ├── dao/
                    │   └── XxxDao.kt
                    ├── datasource/
                    │   └── XxxDataSource.kt
                    └── entity/
                        └── XxxEntity.kt
```

The feature-oriented architecture prevents the database model from becoming a global package unrelated to the feature that owns the data.

### 7.2 Mapping

The persistence flow is:

```text
Room Entity
    ↓
Mapper
    ↓
Domain Model
    ↓
Use Case
    ↓
ViewModel
    ↓
Compose UI
```

For writes:

```text
Compose UI
    ↓
ViewModel
    ↓
Use Case
    ↓
Repository
    ↓
Mapper
    ↓
Room Entity
    ↓
DAO
```

Room entities must not be exposed directly to the presentation layer.

## 8. Firestore Document Model

Firestore uses a document-oriented structure and therefore does not require the same tables, foreign keys, or join tables as Room.

The initial cloud model is organized by authenticated user:

```text
users/
    {userId}/
        profile/
            name
            email
            profileImageUrl
            createdAt
            updatedAt

        academicPeriods/
            {academicPeriodId}/
                name
                startDate
                endDate
                isCurrent
                createdAt
                updatedAt

        subjects/
            {subjectId}/
                academicPeriodId
                name
                code
                credits
                professor
                color
                notes
                createdAt
                updatedAt

        locations/
            {locationId}/
                name
                address
                latitude
                longitude
                placeId
                createdAt
                updatedAt

        events/
            {eventId}/
                academicPeriodId
                subjectId
                locationId
                title
                startAt
                endAt
                locationType
                meetingUrl
                notes
                tags
                createdAt
                updatedAt

        tasks/
            {taskId}/
                academicPeriodId
                subjectId
                title
                description
                dueAt
                priority
                status
                tags
                notes
                createdAt
                updatedAt

        grades/
            {gradeId}/
                subjectId
                name
                value
                weight
                notes
                createdAt
                updatedAt
```

Tags are embedded as identifiers or lightweight values inside event and task documents in the initial Firestore model. The relational `event_tags` and `task_tags` tables are therefore not reproduced as Firestore collections.

## 9. Firestore Ownership and Security

All cloud academic data belongs to an authenticated user.

The intended ownership boundary is:

```text
Authenticated Firebase UID
        ↓
users/{userId}
        ↓
User-owned subcollections
        ↓
Academic data
```

Firestore Security Rules must ensure that a user can only read or modify documents belonging to their own user scope.

The cloud model must not rely solely on the Android UI to enforce ownership.

Authentication is handled by Firebase Authentication, while Firestore stores application data associated with the authenticated UID.

## 10. Local and Cloud Synchronization

The initial persistence strategy follows an offline-first approach for core academic information.

The conceptual flow is:

```text
User Action
    ↓
ViewModel
    ↓
Use Case
    ↓
Repository
    ↓
Local Room
    ↓
UI updates through Flow / StateFlow
    ↓
Synchronization
    ↓
Firestore
```

For cloud-backed reads and synchronization:

```text
Firestore
    ↓
Remote Data Source
    ↓
DTO / Cloud Model
    ↓
Mapper
    ↓
Domain Model
    ↓
Repository
    ↓
Room
    ↓
Flow / StateFlow
    ↓
UI
```

Room remains the local persistence mechanism used by the application while Firestore provides cloud synchronization.

Conflict resolution, synchronization timestamps, retry policies, and offline queue behavior require validation during implementation and are therefore not treated as finalized by this initial document.

## 11. Data Boundaries

The architecture separates the representations used at different boundaries.

```text
Firestore Document
        ↓
Remote DTO / Cloud Model
        ↓
Mapper
        ↓
Domain Model
        ↓
Repository
        ↓
Use Case
```

For local persistence:

```text
Room Entity
        ↓
Mapper
        ↓
Domain Model
        ↓
Repository
        ↓
Use Case
```

The following representations must not be mixed:

- Room entities are persistence models.
- Firestore documents are cloud persistence models.
- DTOs represent external data contracts.
- Domain models represent business concepts.
- UI state represents presentation state.

## 12. Data Validation Rules

The application should validate data before persistence.

Examples include:

- Event end time must not precede its start time.
- Task priority must be one of the supported values.
- Task status must be one of the supported values.
- Grade values must respect the configured academic grading scale.
- Grade weights must be valid for the academic calculation rules.
- Required subject names must not be empty.
- Required event titles must not be empty.
- Remote events should contain a valid meeting URL when a meeting link is required.
- Physical events should contain sufficient location information to display their location.
- A grade must reference an existing subject owned by the same user.

Validation belongs primarily in application/domain logic, with persistence constraints providing an additional safety boundary.

## 13. Indexing Considerations

Room indexes should be added for query patterns that are used frequently.

Candidates include:

- `subjects(user_id, academic_period_id)`
- `events(user_id, start_at)`
- `events(user_id, subject_id)`
- `tasks(user_id, due_at)`
- `tasks(user_id, status)`
- `tasks(user_id, subject_id)`
- `grades(user_id, subject_id)`

Firestore indexes will be created according to the queries required by the application and generated or managed through the Firebase configuration.

Indexing decisions should be validated after the main queries are implemented rather than adding indexes without a demonstrated query need.

## 14. MER

The current relational model is represented in the following Entity-Relationship Model:

![MER](mer.png)

The image should be stored alongside this document in:

```text
docs/database/mer.png
```

The MER must reflect the relational model defined in this document, including:

- Primary keys.
- Foreign keys.
- Required and optional attributes.
- Data types.
- Cardinalities.
- Associative tables.
- Referential relationships.

## 15. Database Evolution

This is the initial database baseline for UniHub.

Changes to the data model must be reflected in:

1. The Room entities and migrations.
2. The Firestore document model.
3. Repository implementations.
4. Mappers.
5. Relevant requirements.
6. The MER.
7. This document.

Room schema changes must use proper database migrations once the application contains persisted user data.

Firestore schema changes must consider existing documents and backward compatibility where necessary.
