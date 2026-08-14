# UniHub — User Journey

## 1. Document Information

| Field        | Value        |
|--------------|--------------|
| Project      | UniHub       |
| Document     | User Journey |
| Version      | 0.1          |
| Status       | Draft        |
| Last Updated | 2026-08-13   |

## 2. Purpose

This document describes how a university student interacts with UniHub to accomplish common academic goals.

The User Journey focuses on the user's perspective rather than the technical implementation.

It will be used to guide:

- UX design.
- Navigation.
- Screen design.
- Feature prioritization.
- Usability testing.
- Figma prototypes.
- Functional requirements.

## 3. Primary User

### Student

The primary UniHub user is a university student who must manage multiple academic responsibilities simultaneously.

### Goals

The student wants to:

- Know what they have to do today.
- Know where they need to go.
- Keep track of classes and meetings.
- Remember deadlines.
- Organize tasks.
- Monitor academic performance.
- Understand what grades they need.
- Spend less time manually entering information.
- Quickly obtain an overview of their academic workload.

### Frustrations

Typical problems include:

- Information spread across different applications.
- Forgetting deadlines.
- Difficulty keeping calendars organized.
- Difficulty tracking academic grades.
- Repeatedly entering similar information.
- Not knowing how to prioritize tasks.
- Having to manually calculate required grades.

## 4. Overall User Journey

```text
Discover UniHub
       ↓
Install application
       ↓
Sign in
       ↓
Initial setup
       ↓
Create subjects
       ↓
Add events and classes
       ↓
Add tasks
       ↓
Register grades
       ↓
Dashboard becomes useful
       ↓
Daily planning
       ↓
AI assistance
       ↓
Academic monitoring
       ↓
Continuous use
```

## 5. Journey 1 — First-Time Setup

### Goal

Configure enough information for UniHub to become useful.

### Step 1 — Open UniHub

The user sees the splash screen.

**User need**

Understand what application is being opened and wait for initialization.

**System response**

The application checks:

- Authentication state.
- Required configuration.
- Local data.

### Step 2 — Onboarding

The user sees a short explanation of the main capabilities.

```text
Organize
Your Classes
Your Tasks
Your Grades
Your Academic Life
```

The user can:

- Continue.
- Skip onboarding.

### Step 3 — Sign In

The user chooses:

```text
Continue with Google
```

**System**

Firebase Authentication handles authentication.

**Success**

The user enters UniHub.

**Failure**

The application displays a clear authentication error and allows retrying.

### Step 4 — Initial Academic Setup

The user creates their first subjects.

Example:

```text
Computación Móvil
Investigación
Matemáticas
Bases de Datos
```

**UX Goal**

Avoid forcing the user to configure every possible option before using the application.

The user should be able to start with minimal information.

## 6. Journey 2 — Adding a University Class

### Goal

Register a recurring or individual class in the calendar.

### User intention

> "I have Computational Mobile on Thursday from 6 PM to 8 PM at the university."

### Standard flow

```text
Dashboard
   ↓
Calendar
   ↓
Add Event
   ↓
Event Form
   ↓
Select Subject
   ↓
Select Location
   ↓
Save
```

### Event information

The user enters:

```text
Name:
Computación Móvil

Date:
Thursday

Time:
6:00 PM — 8:00 PM

Subject:
Computación Móvil

Location:
UdeA

Notes:
...
```

### Location flow

```text
Select Location
       ↓
Search address
       OR
Select on map
       OR
Use current location to help select
       ↓
Confirm location
       ↓
Return to Event
```

### Result

The event appears in:

- Calendar.
- Dashboard.
- Subject information.

The location can be viewed on a map from the event details.

## 7. Journey 3 — Adding a Remote Meeting

### Goal

Register a virtual class or meeting.

### Example

> "I have a project meeting on Friday from 2 PM to 3 PM on Google Meet."

### Flow

```text
Calendar
   ↓
Add Event
   ↓
Remote
   ↓
Date + Time
   ↓
Meeting URL
   ↓
Subject (optional)
   ↓
Save
```

### Result

The event displays:

```text
Project Meeting
Friday
2:00 PM — 3:00 PM

Remote

Join meeting
```

## 8. Journey 4 — Creating a Task

### Goal

Record an academic task and its deadline.

### Example

> "I need to finish the research report by Friday."

### Standard flow

```text
Tasks
   ↓
Add Task
   ↓
Title
   ↓
Subject
   ↓
Deadline
   ↓
Priority
   ↓
Save
```

### Result

The task appears in:

- Tasks.
- Dashboard.
- Relevant subject.
- Upcoming deadlines.

## 9. Journey 5 — Using AI to Create Information

### Goal

Reduce manual data entry.

### Example

The user opens the AI assistant and writes:

> "Add a Computational Mobile class Thursday from 6 to 8 PM at UdeA."

### Journey

```text
AI
 ↓
User message
 ↓
AI interpretation
 ↓
Structured action
 ↓
Validation
 ↓
Confirmation
 ↓
Create Event
 ↓
Success
```

### Confirmation

The application may show:

```text
Create this event?

Computación Móvil
Thursday
6:00 PM — 8:00 PM
UdeA

[Cancel] [Create]
```

### Result

The event is created using the same application Use Case used by the normal UI.

The AI does not bypass the application's business logic.

## 10. Journey 6 — Using Voice

### Goal

Create academic information without manually typing.

### Example

The user says:

> "Create a task for my research class. It is due Friday at midnight and the priority is high."

### Flow

```text
AI
 ↓
Microphone
 ↓
Speech-to-Text
 ↓
Text interpretation
 ↓
Structured action
 ↓
Validation
 ↓
Confirmation
 ↓
Create Task
```

If voice processing fails:

```text
Voice failure
    ↓
Show error
    ↓
Allow text input
```

## 11. Journey 7 — Planning the Day

### Goal

Understand what should be prioritized during the day.

### User situation

The student has:

```text
Class:
10:00 AM

Task:
Research report — due tomorrow

Task:
Programming assignment — due Friday

Meeting:
3:00 PM
```

The student asks:

> "How should I organize my day?"

### Flow

```text
AI
 ↓
Read relevant academic context
 ↓
Analyze deadlines
 ↓
Analyze schedule
 ↓
Generate recommendation
 ↓
Display plan
```

### Example result

```text
Your main priority today should be
the research report because it is due tomorrow.

Suggested plan:

09:00 — Review research sources
10:00 — Computational Mobile class
12:00 — Lunch / break
01:00 — Work on research report
03:00 — Project meeting
04:00 — Continue research report
```

The recommendation is informational.

The system does not automatically modify the user's calendar.

## 12. Journey 8 — Registering Grades

### Goal

Keep academic performance up to date.

### Standard flow

```text
Academic
   ↓
Select Subject
   ↓
Add Grade
   ↓
Evaluation
   ↓
Grade
   ↓
Weight
   ↓
Save
```

Example:

```text
Subject:
Mathematics

Evaluation:
Midterm

Grade:
4.2

Weight:
30%
```

### Result

UniHub recalculates:

- Current subject grade.
- Remaining percentage.
- Academic summary.

## 13. Journey 9 — Simulating a Required Grade

### Goal

Know what grade is required to reach a target final grade.

### User question

> "What do I need to get a 3.5 in this subject?"

### Flow

```text
Academic
   ↓
Subject
   ↓
Grade Simulator
   ↓
Current grades
   ↓
Target grade
   ↓
Calculate
```

### Example

```text
Current weighted grade: 2.4
Remaining percentage: 40%
Target final grade: 3.0

Required remaining grade:
3.9
```

### UX principle

The result must be easy to understand and must clearly indicate whether the target is:

- Achievable.
- Unachievable.
- Already achieved.

## 14. Journey 10 — Starting the Day

### Goal

Quickly understand what requires attention today.

### User opens UniHub

The Dashboard shows:

```text
TODAY

08:00
Research class
UdeA

10:00
Computational Mobile
UdeA

12:00
Research report
Due tomorrow

03:00
Project meeting
Remote
```

### Primary question answered

> "What do I have to do today?"

The Dashboard should answer this question without requiring the user to navigate through multiple screens.

## 15. Journey 11 — Going to the Next Activity

### Goal

Know where the student needs to go for the next physical event.

The Dashboard displays:

```text
NEXT ACTIVITY

Computational Mobile
6:00 PM — 8:00 PM

UdeA

View location
```

The user taps the location.

```text
Event
 ↓
Location
 ↓
Map preview
 ↓
Open in Google Maps
```

UniHub provides the location information but does not implement navigation itself.

## 16. Journey 12 — Managing a Busy Week

### Goal

Understand workload across multiple days.

The user opens Calendar.

```text
Monday
    Classes
    Tasks

Tuesday
    Class
    Assignment

Wednesday
    Meeting
    Deadline

Thursday
    Classes

Friday
    Project deadline
```

The user can then:

- Open individual events.
- Open tasks.
- Review deadlines.
- Ask AI for planning recommendations.

## 17. Journey 13 — Offline Usage

### Goal

Continue using important functionality when there is no internet connection.

### Scenario

The student opens UniHub without network access.

### Expected behavior

Previously synchronized information remains available locally.

The user can continue viewing:

- Subjects.
- Events.
- Tasks.
- Grades.
- Academic calculations.

If the user performs an operation requiring network connectivity, the application should communicate the limitation clearly.

Once connectivity returns:

```text
Local changes
     ↓
Synchronization
     ↓
Firebase
```

The exact conflict-resolution strategy will be defined in `DATABASE.md`.

## 18. Journey 14 — Error Recovery

### Scenario

The user attempts to create an event while the network is unavailable.

### Expected experience

The application should:

1. Avoid crashing.
2. Explain what happened.
3. Preserve user input when possible.
4. Offer retry.
5. Use local persistence when supported.

Example:

```text
Unable to synchronize right now.

Your information is saved locally
and will be synchronized when possible.

[Retry]
```

## 19. Core User Flows

The following flows represent the most important interactions for the MVP.

### Flow A — Authentication

```text
Open App
   ↓
Onboarding
   ↓
Google Login
   ↓
Dashboard
```

### Flow B — Event

```text
Dashboard
   ↓
Calendar
   ↓
Create Event
   ↓
Event Information
   ↓
Location / Remote
   ↓
Save
   ↓
Calendar
```

### Flow C — Task

```text
Dashboard
   ↓
Tasks
   ↓
Create Task
   ↓
Subject
   ↓
Deadline
   ↓
Priority
   ↓
Save
```

### Flow D — Academic

```text
Academic
   ↓
Subject
   ↓
Grades
   ↓
Calculate Average
   ↓
Grade Simulator
```

### Flow E — AI

```text
AI
   ↓
Text / Voice
   ↓
Interpretation
   ↓
Structured Action
   ↓
Validation
   ↓
Confirmation
   ↓
Application Use Case
```

## 20. Primary User Journey

The complete MVP journey can be summarized as:

```text
                    ┌───────────────┐
                    │    UniHub     │
                    └───────┬───────┘
                            ↓
                    ┌───────────────┐
                    │   Sign In     │
                    └───────┬───────┘
                            ↓
                    ┌───────────────┐
                    │   Dashboard   │
                    └───────┬───────┘
                            ↓
              ┌─────────────┼─────────────┐
              ↓             ↓             ↓
          Subjects       Calendar       Tasks
              │             │             │
              │             ↓             │
              │        Events ───── Location
              │             │
              └─────────────┼─────────────┘
                            ↓
                       Academic
                            ↓
                  Grades / Simulator
                            ↓
                           AI
                            ↓
                 Plan / Create / Query
```

## 21. UX Principles Derived from the Journey

### Principle 1 — Dashboard First

The user should quickly understand what requires attention today.

### Principle 2 — Minimize Data Entry

Information that can be reused should not need to be entered repeatedly.

### Principle 3 — AI Is an Alternative Interface

Every important AI action should correspond to a normal application workflow.

### Principle 4 — No AI Dependency

The user must be able to use UniHub without AI.

### Principle 5 — Location Has Context

Location exists primarily to describe where an event takes place.

It is not a tracking feature.

### Principle 6 — Academic Calculations Are Deterministic

Grades and averages must be calculated by application logic, not generated by AI.

### Principle 7 — Confirmation for Risky Actions

AI-generated mutations should be validated and confirmed when ambiguity or potential data loss exists.

### Principle 8 — Actionable Information

The interface should prioritize information that helps the student decide what to do next.

## 22. Future UX Validation

The following journeys should be tested with prototypes before finalizing the interface:

- First-time setup.
- Creating a class.
- Creating a task.
- Finding an upcoming event.
- Viewing an event location.
- Registering a grade.
- Using the grade simulator.
- Creating an event through AI.
- Asking AI for daily planning.
- Recovering from an offline/network error.

Testing results may lead to changes in navigation, screen structure, or interaction patterns.
