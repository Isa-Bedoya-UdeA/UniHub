# UniHub Design System

## 1. Purpose

This document defines the visual and component system for UniHub. It
establishes the design tokens, typography, spacing, shapes, reusable UI
components, interaction states, accessibility rules, and implementation
structure shared between Figma, Stitch-generated concepts, and the
Android Jetpack Compose application.

The objective is to keep the interface visually consistent while
allowing individual screens to be designed and implemented without
redefining basic visual rules.

## 2. Design Principles

### 2.1 Clarity first

Academic information must be scannable. Users should quickly identify
subjects, events, tasks, dates, times, grades, and pending actions.

### 2.2 Visual hierarchy

Hierarchy is established through typography, spacing, surfaces, brand
color, icons, and semantic colors. Color should reinforce hierarchy
rather than replace it.

### 2.3 Consistency

Repeated concepts use the same component and visual treatment throughout
the application.

### 2.4 Progressive disclosure

Complex information is revealed progressively. Summary cards show the
most important information first, while details appear on dedicated
screens or dialogs.

### 2.5 Academic context

UniHub should feel like a serious academic productivity tool rather than
a generic task manager. The interface prioritizes schedules, deadlines,
subjects, academic performance, planning, and event locations.

## 3. Design System Structure

``` text
core/designsystem/
├── color/
│   ├── Color.kt
│   └── ColorScheme.kt
├── component/
│   ├── UniHubButton.kt
│   ├── UniHubIconButton.kt
│   ├── UniHubChip.kt
│   ├── UniHubCard.kt
│   ├── UniHubTextField.kt
│   ├── UniHubTextArea.kt
│   ├── UniHubDropdown.kt
│   ├── UniHubDialog.kt
│   ├── UniHubAlert.kt
│   ├── UniHubProgressBar.kt
│   ├── UniHubCircularProgress.kt
│   ├── UniHubTopBar.kt
│   ├── UniHubBottomNavigation.kt
│   ├── UniHubFloatingActionButton.kt
│   ├── UniHubEmptyState.kt
│   ├── UniHubLoadingState.kt
│   ├── UniHubErrorState.kt
│   ├── UniHubSubjectCard.kt
│   ├── UniHubEventCard.kt
│   ├── UniHubTaskItem.kt
│   ├── UniHubGradeCard.kt
│   ├── UniHubDashboardItem.kt
│   ├── UniHubCalendar.kt
│   ├── UniHubCalendarEvent.kt
│   ├── UniHubLocationCard.kt
│   ├── UniHubRemoteMeetingCard.kt
│   ├── UniHubAcademicSummary.kt
│   ├── UniHubGradeSimulator.kt
│   ├── UniHubStudySelector.kt
│   ├── StudyOption.kt
│   ├── UniHubAIAssistantInput.kt
│   └── UniHubAIMessage.kt
├── shape/
│   └── Shape.kt
├── spacing/
│   └── Spacing.kt
├── theme/
│   └── Theme.kt
└── typography/
    └── Typography.kt
```

Feature-specific components remain inside their feature package. The
Design System contains reusable components shared by multiple features.

## 4. Color System

### 4.1 Light theme

| Category | Token          | Hex       | Usage                                                    |
|----------|----------------|-----------|----------------------------------------------------------|
| Brand    | Primary        | `#4F46E5` | Primary actions, active navigation, important highlights |
| Brand    | Secondary      | `#7C3AED` | Secondary actions, gradients, active secondary elements  |
| Brand    | Accent         | `#06B6D4` | Highlights, indicators, small interactive accents        |
| Boxes    | Background     | `#F4F7FC` | Application background                                   |
| Boxes    | Surface        | `#FFFFFF` | Main surfaces                                            |
| Boxes    | Cards          | `#F8F9FF` | Cards and information containers                         |
| Boxes    | Border         | `#DDE3F0` | Borders and dividers                                     |
| Text     | Primary Text   | `#17203A` | Titles and primary content                               |
| Text     | Secondary Text | `#667085` | Descriptions, subtitles, metadata                        |
| Text     | Disabled Text  | `#A3ACBC` | Disabled content                                         |
| Semantic | Info           | `#76ABEB` | Informational states                                     |
| Semantic | Success        | `#65CD9B` | Success, completed, approved                             |
| Semantic | Warning        | `#EBB55D` | Warnings and pending states                              |
| Semantic | Error          | `#F98087` | Errors, destructive actions, critical states             |

### 4.2 Dark theme

| Category | Token          | Hex       | Usage                                                    |
|----------|----------------|-----------|----------------------------------------------------------|
| Brand    | Primary        | `#6366F1` | Primary actions, active navigation, important highlights |
| Brand    | Secondary      | `#8B5CF6` | Secondary actions, gradients, active secondary elements  |
| Brand    | Accent         | `#22D3EE` | Highlights, indicators, small interactive accents        |
| Boxes    | Background     | `#0B1020` | Application background                                   |
| Boxes    | Surface        | `#12182A` | Main surfaces                                            |
| Boxes    | Cards          | `#181F33` | Cards and information containers                         |
| Boxes    | Border         | `#2A3550` | Borders and dividers                                     |
| Text     | Primary Text   | `#F3F5FF` | Titles and primary content                               |
| Text     | Secondary Text | `#AAB4CC` | Descriptions, subtitles, metadata                        |
| Text     | Disabled Text  | `#667089` | Disabled content                                         |
| Semantic | Info           | `#76ABEB` | Informational states                                     |
| Semantic | Success        | `#65CD9B` | Success, completed, approved                             |
| Semantic | Warning        | `#EBB55D` | Warnings and pending states                              |
| Semantic | Error          | `#F98087` | Errors, destructive actions, critical states             |

### 4.3 Color rules

- `Primary` is the main brand action color.
- `Secondary` supports the primary hierarchy.
- `Accent` is used sparingly for highlights and indicators.
- Semantic colors communicate state and are not decorative.
- Error and warning states must not rely on color alone.
- Do not introduce application-level colors outside these tokens without
  documenting a design-system decision.

## 5. Typography

| Font                  | Use        | Weight | Size |
|-----------------------|------------|-------:|-----:|
| **Plus Jakarta Sans** | H1         |    700 | 28sp |
| **Plus Jakarta Sans** | H2         |    700 | 24sp |
| **Plus Jakarta Sans** | H3         |    600 | 20sp |
| **Plus Jakarta Sans** | H4         |    600 | 18sp |
| **Inter**             | Body       |    400 | 16sp |
| **Inter**             | Body Small |    400 | 14sp |
| **Inter**             | Label      |    600 | 12sp |

### 5.1 Typography rules

- Use Plus Jakarta Sans for headings and prominent hierarchy.
- Use Inter for body content and functional information.
- Use `sp` for text.
- Do not add additional font families to individual screens.
- Allow long academic names and event titles to wrap.
- The interface must remain usable with larger system font settings.

## 6. Spacing

UniHub uses a 4 dp base system with an 8 dp layout rhythm.

``` text
space-xxs: 4dp
space-xs: 8dp
space-sm: 12dp
space-md: 16dp
space-lg: 20dp
space-xl: 24dp
space-2xl: 32dp
space-3xl: 40dp
space-4xl: 48dp
space-5xl: 64dp
```

### 6.1 Layout tokens

``` text
screen-padding-horizontal: 16dp
screen-padding-vertical: 24dp
section-spacing: 24dp
content-spacing: 16dp
item-spacing: 12dp
```

### 6.2 Touch targets

``` text
touch-target-min: 48dp
icon-button-size: 48dp
fab-size: 56dp
```

Every interactive element must have a minimum interactive area of 48 ×
48 dp, even when the visible icon is smaller.

## 7. Shapes

``` text
radius-none: 0dp
radius-sm: 8dp
radius-md: 12dp
radius-lg: 16dp
radius-xl: 20dp
radius-2xl: 24dp
radius-full: 999dp

button-radius: 16dp
input-radius: 16dp
card-radius: 20dp
dialog-radius: 28dp
bottom-sheet-radius: 28dp
chip-radius: 16dp
calendar-day-radius: 24dp
event-radius: 12dp
```

## 8. Icons

``` text
icon-xs: 16dp
icon-sm: 20dp
icon-md: 24dp
icon-lg: 32dp
icon-xl: 40dp
```

Use one coherent icon family, preferably Material Symbols or an
equivalent consistent set. Interactive icons require a minimum 48 dp
touch target and an accessible description.

## 9. Component Catalog

### 9.1 Foundation components

``` text
UniHubButton
UniHubIconButton
UniHubChip
UniHubCard
UniHubTextField
UniHubTextArea
UniHubDropdown
UniHubDialog
UniHubAlert
UniHubProgressBar
UniHubCircularProgress
UniHubTopBar
UniHubBottomNavigation
UniHubFloatingActionButton
```

### 9.2 Feedback and state components

``` text
UniHubLoadingState
UniHubEmptyState
UniHubErrorState
UniHubSnackbar
UniHubConfirmationDialog
UniHubSuccessMessage
```

### 9.3 Academic components

``` text
UniHubSubjectCard
UniHubEventCard
UniHubTaskItem
UniHubGradeCard
UniHubDashboardItem
UniHubCalendar
UniHubCalendarEvent
UniHubLocationCard
UniHubRemoteMeetingCard
UniHubAcademicSummary
UniHubGradeSimulator
UniHubStudySelector
```

### 9.4 AI components

``` text
UniHubAIAssistantInput
UniHubAIMessage
```

## 10. Buttons

### 10.1 `UniHubButton`

``` text
height: 48dp
min-touch-target: 48dp
horizontal-padding: 24dp
radius: 16dp
icon-size: 20dp
```

Variants:

``` text
Primary
Secondary
Outlined
Text
Destructive
```

Rules:

- Use one dominant primary action per context when possible.
- Use concise action-oriented labels.
- Use icon + text when the icon alone is ambiguous.
- Disabled buttons remain visually distinguishable.

### 10.2 `UniHubIconButton`

``` text
touch-target: 48dp
icon-size: 24dp
```

Use for back, edit, delete, search, more options, calendar navigation,
and voice input.

### 10.3 `UniHubFloatingActionButton`

``` text
size: 56dp
radius: 16dp
icon-size: 24dp
```

Use only for a central creation action such as creating an event or
task.

## 11. Chips

### `UniHubChip`

``` text
height: 32dp
horizontal-padding: 12dp
icon-size: 18dp
radius: 16dp
```

Variants:

``` text
Filter
Tag
Status
Subject
```

Examples:

``` text
Programming
Exam
Pending
Completed
Remote
```

## 12. General Cards

### `UniHubCard`

``` text
radius: 20dp
padding: 16dp
large-padding: 20dp
gap: 12dp
```

Cards group information with a clear purpose. Avoid unnecessary card
nesting.

## 13. Academic Cards

### `UniHubSubjectCard`

Content:

``` text
Subject name
Course code
Professor
Schedule summary
Current grade
Optional subject indicator
```

Primary action:

``` text
Open subject
```

### `UniHubEventCard`

Content:

``` text
Event title
Subject
Date
Start time
End time
Location / Remote
Optional tag
```

Physical location:

``` text
[Location icon] Universidad de Antioquia
```

Remote location:

``` text
[Video icon] Remote meeting
```

The component represents event context, not continuous location
tracking.

### `UniHubTaskItem`

Content:

``` text
Task title
Subject
Due date
Priority/status
Completion control
```

The task should expose the due date clearly and support a direct
completion action.

### `UniHubGradeCard`

Content:

``` text
Subject
Current average
Credits / weight
Progress toward target
Optional target grade
```

Numerical values must remain visible even when semantic colors are used.

## 14. Dashboard

### `UniHubDashboardItem`

Supported information:

``` text
Upcoming event
Pending task
Academic average
Next class
Next location
Grade alert
```

Each item should answer one clear question:

``` text
What is next?
Where do I need to go?
What is due soon?
How am I performing?
```

### `UniHubAcademicSummary`

Compact summary:

``` text
Current average
Completed credits
Pending tasks
Upcoming evaluations
```

Avoid excessive metrics.

## 15. Calendar

### `UniHubCalendar`

Supported views:

``` text
Month
Week
Day
```

The MVP should implement only the views required by the product scope.

### Calendar day

``` text
calendar-day-size: 48dp
calendar-day-radius: 24dp
```

States:

``` text
Default
Today
Selected
Has events
Disabled
```

### `UniHubCalendarEvent`

Types:

``` text
Class
Exam
Task deadline
Meeting
Other
```

Use labels, icons, or tags in addition to color.

## 16. Form Components

### `UniHubTextField`

``` text
height: 56dp
horizontal-padding: 16dp
vertical-padding: 16dp
icon-size: 24dp
radius: 16dp
```

States:

``` text
Default
Focused
Filled
Error
Disabled
Read-only
```

### `UniHubTextArea`

Used for:

``` text
Notes
Private notes
Descriptions
Additional context
```

Allow vertical expansion for long content.

### `UniHubDropdown`

Use for predefined selections such as:

``` text
Subject
Tag
Event type
Academic period
```

## 17. Location Components

### `UniHubLocationCard`

For physical events:

``` text
Location name
Address
Map preview
Open map action
```

Location selection can support:

``` text
Search
Map selection
Current location
```

Current location assists location selection and does not imply
persistent location tracking.

### `UniHubRemoteMeetingCard`

Content:

``` text
Remote
Meeting platform / label
Meeting URL
Open meeting action
```

Physical and remote modes should be mutually exclusive unless a future
requirement explicitly supports hybrid events.

## 18. Dialogs and Alerts

### `UniHubDialog`

``` text
radius: 28dp
padding: 24dp
```

Use for confirmations, important decisions, short forms, and destructive
actions.

### `UniHubConfirmationDialog`

Structure:

``` text
Title
Short explanation
Secondary action
Primary action
```

Destructive actions use the semantic error color and explicit wording.

### `UniHubAlert`

Variants:

``` text
Info
Success
Warning
Error
```

Structure:

``` text
Icon
Title or short label
Message
Optional action
```

## 19. Progress

### `UniHubProgressBar`

Use for:

``` text
Academic progress
Loading
Completion
```

### `UniHubCircularProgress`

Use for:

``` text
Short loading states
Compact progress indicators
Percentage-based academic indicators
```

## 20. Loading, Empty and Error States

### `UniHubLoadingState`

Communicate what is loading without unnecessarily blocking unrelated
content.

### `UniHubEmptyState`

Structure:

``` text
Illustration or icon
Title
Short explanation
Primary action
```

Examples:

``` text
No events yet
No tasks
No subjects
No grades
No search results
```

### `UniHubErrorState`

Structure:

``` text
Error icon
Short explanation
Retry action
```

Technical error details should not be exposed directly to users.

## 21. AI Components

### `UniHubAIAssistantInput`

The assistant is a lightweight productivity interface rather than a
social chatbot.

Input modes:

``` text
Text
Voice
```

Structure:

``` text
Text field
Voice button
Send button
```

Examples:

``` text
"Add Calculus class tomorrow from 6 to 8 AM."

"Create a task for Database homework due Friday."

"Add a grade of 4.2 to Algorithms."

"How should I organize my afternoon?"
```

Actions that modify user data must be confirmed or clearly represented
before persistence when the request is ambiguous.

### `UniHubAIMessage`

Types:

``` text
User
Assistant
System / confirmation
Error
```

## 22. Academic Tools

### `UniHubGradeSimulator`

Purpose:

Calculate the grade required to reach a target.

Structure:

``` text
Current grade components
Weight
Target grade
Required grade
```

The result must be understandable without requiring interpretation of a
chart.

### `UniHubAcademicSummary`

Use for:

``` text
Weighted average
Credits
Subject performance
Target progress
```

### `UniHubStudySelector`

Purpose:

Allow the user to select the active academic program (study) from a dropdown.

Structure:

``` text
Leading icon (School)
Selected study name
Institution subtitle
Dropdown with study options
```

Uses `StudyOption` as its UI model to avoid coupling the Design System to
domain models. Each option shows the study name and institution.

Use in:

``` text
Academic view (progress)
Subjects view
Any screen that filters content by study
```

## 23. Navigation

### `UniHubTopBar`

Use for:

``` text
Screen title
Back navigation
Contextual actions
```

Do not overload the top bar.

### `UniHubBottomNavigation`

Recommended primary destinations:

``` text
Home
Calendar
Tasks
Academics
```

The final navigation labels can be adjusted during the navigation-map
stage.

Rules:

- Keep primary destinations limited.
- Use icons and labels when space permits.
- Make the selected destination visually obvious.
- Keep navigation consistent across screens.

## 24. Interaction States

Interactive components should define the following states where
applicable:

``` text
Default
Pressed
Focused
Selected
Disabled
Loading
Error
```

### 24.1 Pressed

Use subtle tonal or surface changes.

### 24.2 Focused

Focus must remain visible for keyboard and accessibility users.

### 24.3 Disabled

Disabled controls must remain recognizable but clearly unavailable.

### 24.4 Loading

Prevent duplicate submissions while an operation is processing.

## 25. Motion

Motion should be functional and restrained.

Use animation for:

- Navigation transitions.
- Expanding and collapsing content.
- State changes.
- Progress feedback.
- Confirmation of successful actions.

Avoid continuous decorative animation, large parallax effects, delayed
common tasks, or information communicated only through movement.

## 26. Responsive Layout

UniHub is primarily designed for Android phones but must not assume one
exact screen size.

Use:

``` text
dp
sp
weight
fillMaxWidth
wrapContent
adaptive layouts
```

Avoid hard-coded pixel dimensions.

Default screen spacing:

``` text
screen-padding-horizontal: 16dp
```

Use larger spacing for sections and hierarchy.

## 27. Accessibility

Accessibility is part of the Design System.

Android guidance recommends a minimum interactive touch target of 48 ×
48 dp. Text should use `sp`, body text should not be smaller than 12sp,
and text/background contrast should generally reach at least 4.5:1.

Rules:

- Minimum interactive target: 48 × 48 dp.
- Body text minimum: 12sp.
- Use `sp` for typography.
- Provide meaningful content descriptions for actionable icons.
- Decorative images should not create unnecessary accessibility
  announcements.
- Do not rely exclusively on color to communicate status.
- Provide labels for form controls.
- Support larger system font settings.
- Test important flows with TalkBack.
- Ensure focus order is logical.
- Provide accessible alternatives to gesture-only actions.

## 28. Figma Structure

``` text
UniHub
├── 00 — Foundations
│   ├── Colors
│   ├── Typography
│   ├── Spacing
│   ├── Shapes
│   └── Icons
│
├── 01 — Components
│   ├── Buttons
│   ├── Chips
│   ├── Cards
│   ├── Inputs
│   ├── Navigation
│   ├── Dialogs
│   ├── Alerts
│   ├── Progress
│   └── States
│
├── 02 — Academic Components
│   ├── Subject Card
│   ├── Event Card
│   ├── Task Item
│   ├── Grade Card
│   ├── Calendar
│   └── Grade Simulator
│
├── 03 — AI Components
│   ├── Assistant Input
│   ├── User Message
│   ├── Assistant Message
│   └── Confirmation
│
└── 04 — Screens
    ├── Onboarding
    ├── Authentication
    ├── Dashboard
    ├── Calendar
    ├── Subjects
    ├── Tasks
    ├── Academics
    ├── AI Assistant
    └── Settings
```

Figma components should use variants for states such as default,
pressed, selected, disabled, loading, and error.

## 29. Stitch Generation Guidelines

Stitch prompts should reference this Design System instead of inventing
a separate visual language.

Base visual specification:

``` text
Platform: Android mobile
Style: modern academic productivity app
Primary light: #4F46E5
Primary dark: #6366F1
Secondary light: #7C3AED
Secondary dark: #8B5CF6
Accent light: #06B6D4
Accent dark: #22D3EE
Light background: #F4F7FC
Dark background: #0B1020
Light cards: #F8F9FF
Dark cards: #181F33
Typography: Plus Jakarta Sans + Inter
Corner style: rounded
Touch target minimum: 48dp
Grid: 4dp base, primarily 8dp rhythm
```

Generated screens must:

- Use the existing component vocabulary.
- Preserve the established color hierarchy.
- Use the established typography.
- Use the established spacing and radii.
- Support light and dark themes.
- Avoid unrelated colors.
- Avoid excessive gradients.
- Avoid excessive decorative illustrations.
- Prioritize academic information and actions.

## 30. Component Naming Convention

Compose components use the `UniHub` prefix:

``` text
UniHubButton
UniHubCard
UniHubSubjectCard
UniHubEventCard
UniHubTextField
UniHubCalendar
```

Supporting private components may omit the prefix.

Example:

``` text
UniHubEventCard
├── EventHeader
├── EventTime
├── EventLocation
└── EventTags
```

## 31. Design-to-Code Rules

Figma and Compose should use equivalent design tokens.

``` text
Figma token
    ↓
Design System specification
    ↓
Compose token
    ↓
Reusable component
    ↓
Feature screen
```

Do not hard-code brand colors, typography sizes, spacing values, or
radii inside feature screens when an equivalent Design System token
exists.

## 32. Component Definition of Done

A reusable component is complete when it has:

- Defined purpose.
- Defined dimensions.
- Defined spacing.
- Defined typography.
- Defined colors.
- Defined shape.
- Defined interaction states.
- Light theme.
- Dark theme.
- Accessibility considerations.
- Figma component or variant.
- Compose implementation.
- Preview for relevant states.
- Usage documented when behavior is not obvious.

## 33. Design System Scope

The Design System covers:

``` text
Colors
Typography
Spacing
Shapes
Icons
Buttons
Chips
Cards
Inputs
Navigation
Dialogs
Alerts
Progress
Feedback states
Academic components
Calendar
Location components
AI components
Accessibility
```

Screen-specific compositions, business rules, navigation logic, API
behavior, and domain logic belong to their corresponding feature or
architecture documentation.

## 34. References

- [Android Developers —
  Accessibility](https://developer.android.com/design/ui/mobile/guides/foundations/accessibility)
- [Android Developers — Make apps more
  accessible](https://developer.android.com/guide/topics/ui/accessibility/apps.html)
- [Android Developers — Grids and
  units](https://developer.android.com/design/ui/mobile/guides/layout-and-content/grids-and-units)
