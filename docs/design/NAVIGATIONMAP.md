# UniHub — Navigation Map

## 1. Purpose

This document defines the navigation structure of UniHub, including the main application areas, screen relationships, navigation flows, and route names that will be implemented in `core/navigation/Routes.kt`.

The Navigation Map is a design artifact: it describes where users can navigate. The actual Navigation Compose implementation will be developed as part of the navigation skeleton.

## 2. Navigation Principles

- Use clear top-level destinations.
- Keep frequently used academic functions accessible.
- Use Bottom Navigation for primary destinations.
- Use forward navigation for detail and creation screens.
- Preserve context when navigating to details and returning.
- Avoid unnecessary navigation depth.
- Keep authentication separate from authenticated navigation.
- Routes represent destinations, not UI implementation details.
- Entity identifiers such as `subjectId`, `eventId`, `taskId`, and `gradeId` are passed as navigation arguments.

## 3. Application Navigation Structure

```text
                                  ┌─────────────────┐
                                  │      Splash     │
                                  └────────┬────────┘
                                           │
                         ┌─────────────────┴─────────────────┐
                         │                                   │
                    First launch                         Returning user
                         │                                   │
                         ↓                                   ↓
                  ┌──────────────┐                    ┌──────────────┐
                  │  Onboarding  │                    │ Auth Check   │
                  └──────┬───────┘                    └──────┬───────┘
                         │                                   │
                         └─────────────────┬─────────────────┘
                                           ↓
                                  ┌─────────────────┐
                                  │      Login      │
                                  └────────┬────────┘
                                           │
                                  Google / Firebase
                                           │
                                           ↓
                              ┌───────────────────────┐
                              │    Main Application   │
                              └───────────┬───────────┘
                                          │
             ┌────────────────────────────┼────────────────────────────┐
             │                            │                            │
             ↓                            ↓                            ↓
      ┌───────────────┐            ┌───────────────┐            ┌───────────────┐
      │   Dashboard   │            │    Calendar   │            │   Subjects    │
      └───────┬───────┘            └───────┬───────┘            └───────┬───────┘
              │                            │                            │
      ┌───────┼────────┐           ┌──────┼──────────┐          ┌──────┼──────────┐
      │       │        │           │      │          │          │      │          │
      ↓       ↓        ↓           ↓      ↓          ↓          ↓      ↓          ↓
   Event    Task    Academic    Event   Create    Calendar   Subject  Create    Edit
   Detail  Detail   Summary    Detail   Event      Date      Detail   Subject   Subject

                    ┌─────────────────────────────────────────────┐
                    │              Other Main Areas               │
                    └──────────────────────┬──────────────────────┘
                                           │
                              ┌────────────┼────────────┐
                              ↓            ↓            ↓
                         ┌──────────┐ ┌──────────┐ ┌────────────┐
                         │ Academic │ │    AI    │ │  Settings  │
                         └────┬─────┘ └────┬─────┘ └─────┬──────┘
                              │            │             │
                              ↓            ↓             ↓
                         ┌──────────┐ ┌──────────┐ ┌────────────┐
                         │ Grades & │ │ AI Chat  │ │  Profile   │
                         │ Simulator│ │Assistant │ │ Preferences│
                         └──────────┘ └──────────┘ └────────────┘
```

## 4. Authentication Flow

```text
Splash
  │
  ├── New / unauthenticated user
  │          ↓
  │      Onboarding
  │          ↓
  │        Login
  │          ↓
  │   Google / Firebase Auth
  │          ↓
  └──────→ Dashboard

Returning authenticated user
  │
  └──────────────→ Dashboard
```

| Screen     | Route        | Purpose                                            |
|------------|--------------|----------------------------------------------------|
| Splash     | `splash`     | Initial application state and authentication check |
| Onboarding | `onboarding` | Introduces UniHub to a first-time user             |
| Login      | `login`      | Authenticates the user with Google/Firebase        |
| Main       | `main`       | Root of the authenticated application              |

Unauthenticated users must not access authenticated destinations.

## 5. Main Navigation

The authenticated application contains these primary areas:

```text
Main
│
├── Dashboard
├── Calendar
├── Subjects
├── Academic
└── Settings
```

The Bottom Navigation Bar provides access to the primary recurring areas. AI remains accessible contextually from the Dashboard and other appropriate entry points rather than being forced into the primary navigation.

## 6. Dashboard Flow

```text
Dashboard
│
├── Today's schedule
│      └── Event Detail
│
├── Upcoming events
│      └── Event Detail
│
├── Pending tasks
│      └── Task Detail
│
├── Academic summary
│      └── Academic
│
└── AI Assistant
       └── AI Chat
```

The Dashboard prioritizes today's activities, the next location the user needs to attend, pending tasks, academic progress, and quick AI-assisted actions.

## 7. Calendar Flow

```text
Calendar
│
├── Day / Week / Month view
│       └── Event Detail
│
├── Create Event
│
└── Event Detail
       ├── Edit Event
       ├── Delete Event
       ├── View Location
       └── Open Meeting Link
```

Event creation supports title, subject association, start/end date and time, recurrence, location, remote meeting link, notes, and tags.

Recurring academic schedules may contain multiple event series when weekly blocks have different times. For example, Tuesday and Thursday from 16:00–18:00 and Saturday from 14:00–16:00 are represented as separate recurring event series associated with the same subject.

## 8. Subject Flow

```text
Subjects
│
├── Subject Detail
│      ├── Subject information
│      ├── Events
│      ├── Tasks
│      └── Grades
│
├── Create Subject
│
└── Edit Subject
```

A subject may have multiple scheduled event series.

## 9. Task Flow

```text
Dashboard
   │
   ↓
Task Detail
   ├── Edit Task
   ├── Complete Task
   └── Delete Task
```

Task creation may be available contextually and through AI-assisted creation.

## 10. Academic Flow

```text
Academic
│
├── Academic Period
│      ├── Subjects
│      ├── Grades
│      └── Academic Summary
│
├── Grade Detail
├── Grade Calculator
└── Grade Simulator
```

The academic area provides weighted-average calculation, grade records, academic-period organization, and simulation of required grades.

## 11. AI Flow

AI acts as an interaction layer over UniHub functionality.

```text
Dashboard
    │
    ↓
AI Assistant
    │
    ├── Text input
    └── Voice input
          │
          ↓
     Intent / Action
          │
     ┌────┼─────────────┐
     ↓    ↓             ↓
  Event Subject        Task
     │    │             │
     └────┼─────────────┘
          ↓
     Confirmation
          ↓
      Save data
```

The AI Assistant may create events, subjects, tasks, and grades, and may help plan the user's day using existing tasks and events. Data-modifying actions should request confirmation when the intent is ambiguous.

## 12. Settings Flow

```text
Settings
│
├── Profile
├── Appearance
│     ├── Light
│     ├── Dark
│     └── System
├── Notifications
├── Privacy & Security
└── Sign Out
```

## 13. Route Definitions

These are the initial route contracts for `core/navigation/Routes.kt`.

### Root routes

| Route        | Destination                    |
|--------------|--------------------------------|
| `splash`     | Splash                         |
| `onboarding` | Onboarding                     |
| `login`      | Login                          |
| `main`       | Authenticated application root |

### Main destinations

| Route       | Destination |
|-------------|-------------|
| `dashboard` | Dashboard   |
| `calendar`  | Calendar    |
| `subjects`  | Subjects    |
| `academic`  | Academic    |
| `settings`  | Settings    |

### Detail and creation routes

| Route                      | Destination        |
|----------------------------|--------------------|
| `event/create`             | Create Event       |
| `event/{eventId}`          | Event Detail       |
| `event/{eventId}/edit`     | Edit Event         |
| `subject/create`           | Create Subject     |
| `subject/{subjectId}`      | Subject Detail     |
| `subject/{subjectId}/edit` | Edit Subject       |
| `task/create`              | Create Task        |
| `task/{taskId}`            | Task Detail        |
| `academic/grades`          | Grades             |
| `academic/grade/{gradeId}` | Grade Detail       |
| `academic/calculator`      | Grade Calculator   |
| `academic/simulator`       | Grade Simulator    |
| `ai/chat`                  | AI Assistant       |
| `settings/profile`         | Profile            |
| `settings/notifications`   | Notifications      |
| `settings/security`        | Privacy & Security |

Dynamic routes must receive entity identifiers as navigation arguments.

## 14. Navigation Responsibilities

Suggested structure:

```text
core/
└── navigation/
    ├── Routes.kt
    ├── NavGraph.kt
    ├── UniHubNavHost.kt
    └── BottomNavigationItem.kt
```

Responsibilities include route contracts, navigation graphs, navigation arguments, authenticated and unauthenticated flows, and Bottom Navigation configuration.

Feature packages own their screens and presentation logic; `core/navigation` coordinates how those screens are reached.

## 15. Navigation and Clean Architecture

Navigation does not contain business logic.

The expected dependency flow remains:

```text
Screen
  ↓
ViewModel
  ↓
UseCase
  ↓
Repository
```

Navigation coordinates destinations without calculating academic results, accessing Firestore directly, or implementing domain rules.

## 16. Navigation States and Edge Cases

The implementation must account for:

- Unauthenticated access to protected destinations.
- Returning authenticated users.
- Invalid or missing entity IDs.
- Deleted entities opened from an old navigation state.
- Back navigation after creation or editing.
- Deep navigation from Dashboard to detail screens.
- Configuration changes and process recreation.
- Loading, empty, and error states.
- Network-dependent destinations.
