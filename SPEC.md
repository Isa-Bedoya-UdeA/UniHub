# UniHub — Specification

## 1\. Overview

UniHub is a mobile academic organization application for university students.

The application centralizes academic information that is commonly distributed across calendars, task managers, grade calculators, notes, and communication tools.

UniHub is designed around the student's daily academic context: what they need to do, what they need to attend, where they need to go, what deadlines are approaching, and how their academic performance is progressing.

The project is developed for the Mobile Computing course at Universidad de Antioquia and is intended to demonstrate practical use of mobile-computing concepts including:

* Mobile UI development.
* Geolocation.
* Connectivity.
* Artificial intelligence.
* Cloud services.
* Authentication and authorization.
* Security.
* Backend development.
* Virtualization and containerization.
* Testing.
* Software architecture.

The project is planned for approximately 16 weeks.

## 2\. Product Vision

UniHub aims to provide a single, practical academic workspace where a university student can organize and understand their academic life without having to manually combine information from multiple applications.

The product focuses on reducing organizational friction rather than adding unnecessary functionality.

The central experience is built around three questions:

1. What do I have to do?
2. Where do I need to be?
3. How am I doing academically?

AI is used as an alternative and faster way to interact with the application. It can interpret natural-language requests, create academic information through validated application actions, and provide planning recommendations.

AI is not intended to replace the application's deterministic business logic.

## 3\. Functional Overview

UniHub provides the following functional areas:

### Academic Subjects

Users can create and manage university subjects and associate other academic information with them.

### Events and Calendar

Users can create academic or personal events with dates, times, subjects, tags, notes, and either a physical location or a remote meeting link.

Physical event locations can be searched or selected on a map and later viewed on a map.

### Tasks

Users can create and manage academic tasks with deadlines, priorities, subjects, notes, and completion status.

### Academic Performance

Users can register grades and weights, calculate academic averages, and simulate the grade required to achieve a target final grade.

Users who already have completed semesters can optionally register their previously approved credits and cumulative GPA when creating or editing an academic program. This allows the system to calculate accurate overall academic progress without requiring users to manually recreate all past subjects and grades.

### Dashboard

The dashboard presents actionable academic information such as upcoming activities, pending tasks, deadlines, academic performance, and the next physical location the user needs to attend.

### AI Assistant

Users can interact with UniHub through a simple AI chat using text and, where supported, voice input.

The assistant can interpret requests involving:

* Events.
* Tasks.
* Subjects.
* Grades.
* Academic planning.

AI-generated modifications are validated before being executed.

### Connectivity and Cloud

Core information is persisted locally and synchronized with Firebase where applicable.

Firebase Authentication provides user authentication, Firebase Firestore provides cloud persistence, and Firebase Storage provides cloud file storage for user assets such as profile images.

### Backend

A Ktor REST API provides backend functionality required by the project and demonstrates client-server communication.

### Notifications

UniHub can notify users about relevant upcoming events and deadlines.

The application does not use continuous location tracking or arrival-based notifications.

## 4\. Core Features

### 4.1 Authentication

* Google authentication through Firebase Authentication.
* Persistent authenticated sessions.
* User profile.
* Secure access to protected information.

### 4.2 Study Management

* Create academic programs (studies).
* Edit academic programs.
* Delete academic programs.
* Set an active study.
* Each study has a name, institution, and total credits target.
* Subjects and academic periods are associated with a study.
* Academic progress is calculated per study.

### 4.3 Subject Management

* Create subjects.
* Edit subjects.
* Delete subjects.
* View subjects.
* Associate events, tasks, and grades with subjects.
* Subjects belong to a study and an academic period.

### 4.4 Event Management

* Create events.
* Edit events.
* Delete events.
* Calendar views.
* Start and end date/time.
* Tags.
* Notes.
* Optional subject association.
* Physical locations.
* Remote events.
* Meeting URLs.

### 4.5 Location

Geolocation functionality is limited to event-related location management.

The user can:

* Search for an address.
* Select a location on a map.
* Use the device's location as an aid when selecting a location.
* View an event location on a map.
* Open the location in Google Maps.

UniHub does not continuously track the user.

### 4.6 Task Management

* Create tasks.
* Edit tasks.
* Delete tasks.
* Complete tasks.
* Assign priorities.
* Set deadlines.
* Associate tasks with subjects.

### 4.7 Academic Calculations

* Register grades.
* Register evaluation weights.
* Calculate subject grades.
* Calculate weighted academic averages.
* Simulate required grades for a target result.

Academic calculations are implemented as deterministic application logic.

### 4.8 AI Interaction

The AI assistant can:

* Receive text input.
* Receive voice input where supported.
* Interpret natural-language requests.
* Generate structured actions.
* Request confirmation when appropriate.
* Create events.
* Create tasks.
* Create subjects.
* Register grades.
* Recommend daily planning strategies.

### 4.9 Local and Cloud Persistence

Core academic information is persisted locally and synchronized with Firebase when applicable.

The application is designed to remain useful during temporary network interruptions.

### 4.10 Security

The system includes:

* Firebase Authentication.
* Google OAuth.
* Authorization rules.
* Firebase Security Rules.
* Backend token validation.
* Input validation.
* Secure handling of secrets.
* Appropriate protection of sensitive information.

## 5\. User Flows

### 5.1 First-Time User

```text
Open UniHub
    ↓
Onboarding
    ↓
Google Authentication
    ↓
Initial Setup
    ↓
Create Subjects
    ↓
Dashboard
```

### 5.2 Create Physical Class

```text
Dashboard
    ↓
Calendar
    ↓
Create Event
    ↓
Enter Date and Time
    ↓
Select Subject
    ↓
Select Physical Location
    ↓
Confirm
    ↓
Save Event
```

### 5.3 Create Remote Event

```text
Calendar
    ↓
Create Event
    ↓
Select Remote
    ↓
Enter Date and Time
    ↓
Add Meeting URL
    ↓
Save
```

### 5.4 Create Task

```text
Tasks
    ↓
Create Task
    ↓
Enter Information
    ↓
Select Subject
    ↓
Set Deadline and Priority
    ↓
Save
```

### 5.5 AI Event Creation

```text
AI Assistant
    ↓
Text / Voice Input
    ↓
AI Interpretation
    ↓
Structured Action
    ↓
Validation
    ↓
Confirmation
    ↓
Create Event Use Case
    ↓
Event Saved
```

### 5.6 Academic Performance

```text
Academic
    ↓
Select Subject
    ↓
Register Grades
    ↓
Calculate Current Result
    ↓
Grade Simulator
    ↓
Set Target
    ↓
Calculate Required Grade
```

### 5.7 Daily Planning

```text
Dashboard
    ↓
Review Events, Tasks and Deadlines
    ↓
AI Assistant
    ↓
Ask for Planning Recommendation
    ↓
AI Analyzes Relevant Context
    ↓
Suggested Plan
```

For the complete user journeys and interaction details, see:

[**User Journey**](docs/design/USERJOURNEY.md)

## 6\. Requirements

The complete functional and non-functional requirements are maintained separately to keep this specification focused on the product definition.

[**Requirements**](docs/requirements/REQUIREMENTS.md)

## 7\. MVP

The MVP is the smallest complete version of UniHub that demonstrates the main product concept and the Mobile Computing course requirements.

The MVP includes:

* Google/Firebase Authentication.
* Subject management.
* Event and calendar management.
* Physical and remote event locations.
* Task management.
* Local persistence.
* Firebase synchronization.
* Academic grade registration.
* Weighted academic calculations.
* Grade simulator.
* Dashboard.
* Notifications.
* At least one functional AI integration.
* Basic Ktor REST API.
* Core security controls.
* Light and dark themes.
* Complete core UI.
* Automated tests for critical business logic.

The MVP must demonstrate at least one complete end-to-end flow.

Example:

```text
Login
    ↓
Dashboard
    ↓
Create Event
    ↓
Associate Subject
    ↓
Select Location
    ↓
Save Locally
    ↓
Synchronize with Firebase
    ↓
Display Event
```

## 8\. Important Features

The following features are important to the product and should be implemented when possible without putting the project schedule at risk:

* AI event creation.
* AI task creation.
* AI grade registration.
* AI-assisted daily planning.
* Voice input.
* Map-based event locations.
* Remote meeting links.
* Academic dashboard.
* Notifications.
* Offline access to core information.
* Ktor API integration.
* Cloud synchronization.
* Security controls.
* Comprehensive technical documentation.

Important features are prioritized according to their contribution to the course objectives and the application's core user experience.

## 9\. Stretch Features

Stretch features may be implemented only after the MVP is stable.

Possible stretch features include:

* More advanced AI planning.
* Richer calendar interactions.
* Additional dashboard analytics.
* More advanced synchronization handling.
* Expanded notification customization.
* Additional academic statistics.
* Additional UI animations and microinteractions.
* More comprehensive automated integration testing.
* Additional backend endpoints.

Stretch features must never compromise the stability of the MVP, academic deliverables, documentation, testing, or final APK.

## 10\. Out of Scope

The following functionality is explicitly outside the current product scope:

* Social network functionality.
* Student-to-student messaging.
* Friends system.
* Leaderboards.
* Gamification.
* Continuous GPS tracking.
* Geofencing.
* Arrival notifications.
* Location-based reminders.
* "I am here" functionality.
* Automatic navigation.
* A proprietary navigation system.
* Automatic AI modification of the user's schedule without validation.
* Autonomous AI actions without appropriate validation or confirmation.
* Features unrelated to the student's academic organization problem.

## 11\. Constraints

### 11.1 Time

The project is planned for approximately 16 weeks.

The schedule must account for other university courses, professional internship responsibilities, and required personal rest time.

### 11.2 Academic Requirements

The project must satisfy the course requirements for:

* Mobile application development.
* Geolocation.
* Connectivity.
* Artificial intelligence.
* Security.
* Backend/API development.
* Virtualization/containerization.
* Repository-based development.
* Testing.

### 11.3 Delivery Milestones

The project includes three major academic milestones:

#### Anteproject — Week 3

The project must have sufficient definition and design to present the concept.

Expected artifacts include:

* Project document.
* Presentation.
* Mockups.
* Initial architecture.
* Initial data model.
* User/System Model.
* Repository.
* Initial project structure.

#### Progress Report — Week 9

The project must have at least 50% implementation, including the complete UI and at least two or three functional features.

The planned target is stronger than the minimum requirement and includes:

* UI at 100%.
* Local persistence at 100%.
* At least one functional AI integration.
* Google/Firebase Authentication.
* Minimum Ktor API.
* A complete end-to-end flow.

#### Final Delivery — Week 16

The final delivery includes:

* Functional APK.
* Final presentation.
* Demonstration.
* Conclusions.
* Future work.
* Final documentation.
* Final repository state.

### 11.4 Technical Constraints

The project uses the technologies and architectural approaches defined in the planning and technical documentation.

Relevant documentation:

* [Architecture](docs/architecture/ARCHITECTURE.md)
* [API](docs/api/API.md)
* [Database](docs/database/DATABASE.md)
* [Security](docs/security/SECURITY.md)
* [Deployment](docs/deploy/DEPLOY.md)
* [Design System](docs/design/DESIGNSYSTEM.md)
* [Planning](docs/PLANNING.md)

### 11.5 Scope Management

New features must be evaluated against:

1. MVP completeness.
2. Academic requirements.
3. Available development time.
4. Testing effort.
5. Documentation effort.
6. Impact on existing architecture.

A feature should be moved to the Stretch scope if implementing it threatens the stability or completion of the MVP.

