# UniHub — Requirements

## 1. Document Information

| Field        | Value        |
|--------------|--------------|
| Project      | UniHub       |
| Document     | Requirements |
| Version      | 0.1          |
| Status       | Draft        |
| Last Updated | 2026-08-13   |

## 2. Purpose

This document defines the functional and non-functional requirements of UniHub.

The requirements establish what the application must do, the constraints under which it must operate, and the quality characteristics expected from the final product.

The document will be updated as the project evolves. Requirements added after the initial scope definition must be evaluated according to their impact on the project's schedule and MVP.

## 3. Product Scope

UniHub is a mobile application designed to help university students manage their academic activities from a single place.

The application will allow users to:

- Manage academic subjects.
- Manage academic events and classes.
- Associate events with physical locations or remote meeting links.
- Manage tasks and deadlines.
- Register academic grades.
- Calculate weighted academic averages.
- Simulate the grade required to achieve a target final grade.
- View relevant academic information through a dashboard.
- Synchronize information with the cloud.
- Authenticate securely.
- Use artificial intelligence to create and manage academic information through natural language.
- Receive planning recommendations based on their academic workload.

## 4. Functional Requirements

### 4.1 User Authentication

#### FR-001 — User Authentication

The system shall allow users to authenticate using their Google account through Firebase Authentication.

**Acceptance Criteria**

- The user can sign in with Google.
- The system creates or retrieves the user's profile.
- The authenticated session is persisted.
- The user can sign out.
- Authentication errors are displayed clearly.
- Unauthenticated users cannot access protected academic information.

#### FR-002 — User Profile

The system shall maintain a basic user profile.

The profile may contain:

- Name.
- Email.
- Profile picture.
- User preferences.
- Application settings.

The system shall not require unnecessary personal information.

### 4.2 Subjects

#### FR-003 — Create Subject

The user shall be able to create an academic subject.

A subject may contain:

- Name.
- Code.
- Credits.
- Professor.
- Color.
- Notes.

#### FR-004 — Edit Subject

The user shall be able to modify an existing subject.

#### FR-005 — Delete Subject

The user shall be able to delete a subject.

If the subject has associated events, tasks, or grades, the application must handle the relationship safely and prevent accidental data loss where appropriate.

#### FR-006 — View Subjects

The user shall be able to view their subjects and access the information associated with each subject.

### 4.3 Events and Calendar

#### FR-007 — Create Event

The user shall be able to create an academic or personal event.

An event shall support:

- Start date.
- End date.
- Start time.
- End time.
- Name.
- Tags.
- Optional subject.
- Notes.
- Location type.

#### FR-008 — Physical Event Location

The user shall be able to associate an event with a physical location.

The location may be selected through:

- Address search.
- Map selection.
- Device location as an aid for selecting a location.

The system shall store the information necessary to display the selected location later.

#### FR-009 — Remote Event

The user shall be able to mark an event as remote.

A remote event may contain:

- Meeting URL.
- Notes.
- Associated subject.
- Date and time.

The user shall be able to open the meeting URL from the event details.

#### FR-010 — View Event Location

The user shall be able to view the location of a physical event on a map.

The application may open the location using Google Maps.

UniHub shall not implement its own turn-by-turn navigation system.

#### FR-011 — Edit Event

The user shall be able to modify an existing event.

#### FR-012 — Delete Event

The user shall be able to delete an event.

The application shall request confirmation when appropriate.

#### FR-013 — Calendar

The user shall be able to visualize their events using calendar views.

The calendar shall support, at minimum:

- Monthly view.
- Weekly view.
- Daily view.

### 4.4 Tasks

#### FR-014 — Create Task

The user shall be able to create a task.

A task may contain:

- Title.
- Description.
- Subject.
- Due date.
- Priority.
- Status.
- Tags.
- Notes.

#### FR-015 — Task Status

The system shall support the following task states:

- Pending.
- In Progress.
- Completed.

#### FR-016 — Task Priority

The system shall support:

- Low.
- Medium.
- High.

#### FR-017 — Edit Task

The user shall be able to modify an existing task.

#### FR-018 — Complete Task

The user shall be able to mark a task as completed.

#### FR-019 — Delete Task

The user shall be able to delete a task.

### 4.5 Academic Performance

#### FR-020 — Register Grade

The user shall be able to register a grade associated with a subject.

A grade shall support:

- Name or evaluation type.
- Grade value.
- Weight.
- Subject.
- Optional notes.

#### FR-021 — Calculate Subject Grade

The system shall calculate the current or projected final grade of a subject based on the grades and weights registered by the user.

The calculation shall be deterministic and shall not depend on artificial intelligence.

#### FR-022 — Calculate Weighted Average

The system shall calculate the user's weighted academic average using the grades and credits of the corresponding subjects.

#### FR-023 — Grade Simulator

The system shall allow the user to determine the grade required in remaining evaluations to reach a desired final grade.

The simulator shall allow the user to provide:

- Current grades.
- Evaluation weights.
- Target final grade.

The system shall calculate the required grade.

### 4.6 Dashboard

#### FR-024 — Dashboard

The system shall provide a dashboard containing relevant information about the user's academic activity.

The dashboard may display:

- Current date.
- Upcoming events.
- Next academic activity.
- Next physical location the user needs to attend.
- Pending tasks.
- Upcoming deadlines.
- Academic average.
- Relevant academic summary.

The dashboard shall prioritize actionable information rather than displaying the user's current physical location.

### 4.7 Notifications

#### FR-025 — Event Notifications

The system shall allow notifications for upcoming events.

#### FR-026 — Task Notifications

The system shall allow notifications for upcoming task deadlines.

#### FR-027 — Notification Configuration

The user shall be able to configure applicable notification preferences.

UniHub shall not implement location-based arrival notifications.

### 4.8 Artificial Intelligence

#### FR-028 — AI Chat

The system shall provide a simple conversational interface for interacting with the user's academic information.

The interface shall support text input.

#### FR-029 — AI Event Creation

The AI assistant shall be able to interpret natural-language requests for creating events.

Example:

> "Add Computational Mobile class Thursday from 6 to 8 PM at UdeA."

The system shall extract the relevant information and request confirmation when necessary before creating the event.

#### FR-030 — AI Task Creation

The AI assistant shall be able to interpret natural-language requests for creating tasks.

Example:

> "Create a research report task for Friday at 11:59 PM."

#### FR-031 — AI Subject Creation

The AI assistant shall be able to interpret natural-language requests for creating subjects.

#### FR-032 — AI Grade Registration

The AI assistant shall be able to interpret natural-language requests for registering grades.

#### FR-033 — AI Academic Assistant

The AI assistant shall be able to provide planning recommendations based on relevant academic information.

The assistant may consider:

- Upcoming events.
- Pending tasks.
- Deadlines.
- Subjects.
- Academic workload.
- Available time provided by the user.

The assistant shall provide recommendations rather than automatically modifying the user's schedule.

#### FR-034 — Voice Input

The system should support voice input for AI interactions.

Voice input shall be converted into text before being processed by the AI system.

If voice integration introduces significant technical risk, text interaction shall remain the mandatory MVP functionality.

### 4.9 Cloud Connectivity

#### FR-035 — Local Persistence

The application shall persist core academic information locally.

Core information includes:

- Subjects.
- Events.
- Tasks.
- Grades.

#### FR-036 — Cloud Persistence

The system shall synchronize applicable user information with Firebase Firestore.

#### FR-037 — User Data Ownership

Cloud data shall be associated with the authenticated user.

A user shall only be able to access their own protected academic information.

### 4.10 Ktor API

#### FR-038 — Ktor Backend

The project shall provide a backend developed using Ktor.

#### FR-039 — API Authentication

Protected Ktor endpoints shall validate Firebase authentication tokens.

#### FR-040 — API Health Endpoint

The API shall provide a health endpoint for verifying service availability.

Example endpoint:

`GET /api/health`

#### FR-041 — Academic API

The Ktor backend shall provide at least one useful academic endpoint.

#### FR-042 — AI API

If the final architecture uses Ktor for AI processing, the backend shall expose an endpoint for AI-related operations.

The exact API design shall be defined in `docs/api/API.md`.

## 5. Non-Functional Requirements

### 5.1 Usability

#### NFR-001 — Usability

The application shall provide a clear and predictable user experience suitable for frequent academic use.

Common actions should require as few steps as reasonably possible.

### 5.2 UI Consistency

#### NFR-002 — UI Consistency

The application shall use a centralized Design System.

Components shall not define arbitrary colors, spacing, typography, or shapes independently.

### 5.3 Accessibility

#### NFR-003 — Accessibility

The application shall consider accessibility through:

- Sufficient color contrast.
- Content descriptions.
- Scalable typography.
- Appropriate touch targets.
- Clear error messages.
- Information not being communicated through color alone.

### 5.4 Responsive UI

#### NFR-004 — Responsive UI

The interface shall adapt to different Android screen sizes and orientations where applicable.

### 5.5 Performance

#### NFR-005 — Performance

The application shall avoid blocking the main UI thread.

Long-running operations shall use Kotlin Coroutines and appropriate asynchronous mechanisms.

### 5.6 Offline Resilience

#### NFR-006 — Offline Resilience

Core locally persisted information should remain available when the device temporarily has no network connection.

The application shall communicate synchronization failures clearly.

### 5.7 Reliability

#### NFR-007 — Reliability

The application shall handle expected failures without crashing.

Expected failure scenarios include:

- Network unavailable.
- Firebase unavailable.
- AI unavailable.
- Invalid user input.
- Authentication failure.
- API errors.
- Location permission denied.
- Map service unavailable.

### 5.8 Security

#### NFR-008 — Security

Sensitive information shall be protected using appropriate Android and Firebase security mechanisms.

The project shall not store secrets directly in source code.

### 5.9 Authorization

#### NFR-009 — Authorization

Users shall only access resources belonging to their authenticated account.

Authorization shall be enforced at the backend or database level and shall not depend solely on UI restrictions.

### 5.10 Data Validation

#### NFR-010 — Data Validation

Input shall be validated before being persisted or sent to external services.

Validation shall occur at appropriate application boundaries.

### 5.11 Maintainability

#### NFR-011 — Maintainability

The codebase shall follow:

- Clean Architecture.
- SOLID principles.
- Clear naming.
- Separation of concerns.
- Feature-based organization.
- Small and cohesive classes.
- Testable business logic.

### 5.12 Testability

#### NFR-012 — Testability

Critical business logic shall be independently testable.

Particular priority shall be given to:

- Academic calculations.
- Grade simulator.
- Use Cases.
- Repositories.
- ViewModels.
- AI action validation.

### 5.13 Documentation

#### NFR-013 — Documentation

The project shall maintain technical documentation covering:

- Requirements.
- Architecture.
- Database.
- API.
- Security.
- Deployment.
- Design System.
- Contribution guidelines.

### 5.14 Version Control

#### NFR-014 — Version Control

All source code and documentation shall be maintained in GitHub.

The repository shall use meaningful commits, branches, milestones, and releases.

### 5.15 CI/CD

#### NFR-015 — CI/CD

GitHub Actions shall be used to automate relevant quality checks such as:

- Build.
- Lint.
- Unit tests.
- Other automated checks considered necessary.

### 5.16 Backend Portability

#### NFR-016 — Backend Portability

The Ktor backend shall be containerizable using Docker.

### 5.17 Privacy

#### NFR-017 — Privacy

The application shall collect and process only information necessary for its functionality.

Location information shall be used specifically for event-location functionality and shall not be continuously tracked.

### 5.18 AI Safety and Reliability

#### NFR-018 — AI Safety and Reliability

AI-generated actions shall not be executed blindly.

The system shall:

1. Interpret the user's request.
2. Generate structured information.
3. Validate the information.
4. Request confirmation when appropriate.
5. Execute the corresponding application Use Case.

AI failure shall not prevent the core application from functioning.

### 5.19 Scalability

#### NFR-019 — Scalability

The architecture should allow new features to be added without requiring major modifications to existing domain logic.

### 5.20 Observability

#### NFR-020 — Observability

The application and backend should provide sufficient error information during development to diagnose failures without exposing sensitive user information.

## 6. Constraints

The project has an estimated development period of approximately 16 weeks.

The project must also accommodate:

- Other university courses.
- Professional internship responsibilities.
- Required personal rest time.

Therefore, the MVP shall prioritize functionality required to demonstrate the project's mobile-computing concepts.

Features not essential to the MVP shall be classified as Stretch Goals.

## 7. Out of Scope

The following features are explicitly outside the initial scope:

- Social network.
- Messaging between students.
- Friends system.
- Gamification.
- Leaderboards.
- Continuous location tracking.
- Geofencing.
- Arrival notifications.
- "I am here" functionality.
- Location-based reminders.
- GPS navigation developed by UniHub.
- Automatic schedule changes generated by AI.
- Autonomous AI actions without validation or confirmation.

## 8. Requirement Priorities

Requirements will be classified using the following priority levels:

| Priority | Meaning |
|---|---|
| Must | Required for the MVP |
| Should | Important but not critical |
| Could | Useful if time permits |
| Won't | Explicitly excluded from the current release |

The priority of each requirement may be refined during development.
