# Contributing to UniHub

Thank you for contributing to UniHub.

UniHub is an individual university project developed with a focus on professional software engineering practices. Contributions, suggestions, reviews, and technical improvements should respect the project's architecture, scope, documentation, and academic objectives.

## Development Setup

### Prerequisites

Before contributing, make sure the required development environment is installed:

- Android Studio.
- Compatible JDK.
- Android SDK required by the project.
- Git.
- Docker, when working with the Ktor backend.
- Access to required Firebase and Google services for the relevant features.

### Repository Setup

Clone the repository:

```bash
git clone https://github.com/Isa-Bedoya-UdeA/UniHub.git
cd UniHub
```

Open the project in Android Studio and allow Gradle to synchronize.

Do not commit local credentials, API keys, service-account files, or other secrets.

### Project Documentation

Before making architectural or feature changes, review the relevant documentation:

- [Specification](SPEC.md)
- [Requirements](docs/requirements/REQUIREMENTS.md)
- [Planning](docs/PLANNING.md)
- [Architecture](docs/architecture/ARCHITECTURE.md)
- [Database](docs/database/DATABASE.md)
- [API](docs/api/API.md)
- [Security](docs/security/SECURITY.md)
- [Design System](docs/design/DESIGNSYSTEM.md)
- [User Journey](docs/design/USERJOURNEY.md)
- [Deployment](docs/deploy/DEPLOY.md)

## Branching Strategy

The repository uses a simple feature-oriented branching strategy.

### Main Branch

`main` contains stable project code suitable for releases and academic milestones.

Direct commits to `main` should be avoided when changes can be developed through a feature branch.

### Feature Branches

New features should use descriptive branches.

Examples:

```text
feature/authentication
feature/event-management
feature/grade-simulator
feature/ai-assistant
feature/location-picker
```

### Fix Branches

Bug fixes should use:

```text
fix/calendar-timezone
fix/grade-calculation
fix/authentication-error
```

### Documentation Branches

Documentation-only changes may use:

```text
docs/requirements
docs/architecture
docs/readme
```

### Branch Lifecycle

A typical workflow is:

```text
main
  ↓
Create branch
  ↓
Implement change
  ↓
Run tests and checks
  ↓
Update documentation
  ↓
Pull Request
  ↓
Review
  ↓
Merge
```

## Commit Convention

Commits should be concise, descriptive, and focused on one logical change.

The project follows a Conventional Commits-inspired format:

```text
<type>: <description>
```

### Commit Types

| Type | Purpose |
|---|---|
| `feat` | New functionality |
| `fix` | Bug fix |
| `refactor` | Code restructuring without behavior change |
| `docs` | Documentation changes |
| `test` | Tests |
| `build` | Build system or dependency changes |
| `ci` | CI/CD changes |
| `style` | Formatting or non-functional style changes |
| `chore` | Maintenance tasks |

Examples:

```text
feat: add event creation flow
fix: correct weighted average calculation
docs: update architecture documentation
test: add grade simulator tests
build: update compose dependencies
ci: add unit test workflow
```

Commits should describe what changed rather than why the entire project exists.

## Pull Requests

Even though UniHub is primarily developed individually, Pull Requests should be used when practical to preserve a professional Git workflow and provide a clear history of significant changes.

A Pull Request should:

- Have a clear title.
- Describe the change.
- Reference the relevant issue or task when applicable.
- Explain important implementation decisions.
- Include screenshots or recordings for relevant UI changes.
- Mention testing performed.
- Identify documentation updated.
- Avoid unrelated changes.

### Pull Request Checklist

Before merging:

```text
[ ] Code compiles
[ ] Tests pass
[ ] Lint/checks pass
[ ] No secrets are committed
[ ] UI follows the Design System
[ ] Architecture rules are respected
[ ] Requirements are still satisfied
[ ] Documentation is updated when necessary
[ ] Screenshots are included for significant UI changes
```

## Issues

Issues should represent a specific task, bug, improvement, question, or technical decision.

### Recommended Issue Information

An issue should contain:

- Title.
- Description.
- Context.
- Expected behavior.
- Actual behavior, for bugs.
- Steps to reproduce, for bugs.
- Acceptance criteria.
- Relevant screenshots or logs when useful.
- Related milestone.

### Feature Issues

Feature requests should explain:

1. What the user needs.
2. Why the feature is useful.
3. What behavior is expected.
4. How completion can be verified.

New functionality should be checked against `docs/requirements/REQUIREMENTS.md` and `SPEC.md`.

## Code Style

### Kotlin

Kotlin code should follow standard Kotlin conventions and the project's architectural rules.

Prefer:

- Clear names.
- Small functions.
- Single responsibility.
- Immutable state where practical.
- Explicit domain models.
- Constructor injection.
- `suspend` functions for appropriate asynchronous operations.
- Kotlin Coroutines for non-blocking work.
- Sealed types for well-defined UI states or events when appropriate.

Avoid:

- Large classes with multiple responsibilities.
- Business logic inside Composables.
- Business logic inside ViewModels when it belongs in Use Cases.
- Direct access to infrastructure from the Domain layer.
- Hard-coded secrets.
- Unnecessary global state.
- Blocking operations on the main thread.

### Jetpack Compose

Composable functions should primarily describe UI.

Business rules should remain outside Composables.

Reusable visual elements should use the project's Design System rather than defining independent styles.

Follow the rules documented in:

**[Design System](docs/design/DESIGNSYSTEM.md)**

### Architecture

The project follows Clean Architecture using:

```text
Domain
Application
Infrastructure
Presentation
```

Dependencies should follow the architectural boundaries documented in:

**[Architecture Documentation](docs/architecture/ARCHITECTURE.md)**

The Domain layer must remain independent from Android and infrastructure frameworks whenever possible.

### SOLID

Implementation should follow SOLID principles, especially:

- Single Responsibility Principle.
- Open/Closed Principle.
- Liskov Substitution Principle.
- Interface Segregation Principle.
- Dependency Inversion Principle.

Use these principles pragmatically. Avoid introducing abstractions that do not provide a real architectural benefit.

## Documentation

Documentation is considered part of the implementation.

When a change affects the following areas, update the corresponding document:

| Change | Documentation |
|---|---|
| Product scope | `SPEC.md` |
| Requirements | `docs/requirements/REQUIREMENTS.md` |
| Architecture | `docs/architecture/ARCHITECTURE.md` |
| Database | `docs/database/DATABASE.md` |
| API | `docs/api/API.md` |
| Security | `docs/security/SECURITY.md` |
| Deployment | `docs/deploy/DEPLOY.md` |
| UI/UX | `docs/design/DESIGNSYSTEM.md` |
| User flows | `docs/design/USERJOURNEY.md` |
| Project schedule | `docs/PLANNING.md` |

Architectural decisions should be documented rather than existing only in source code or commit messages.

## Testing

Changes should include appropriate tests when applicable.

### Unit Tests

Use unit tests for deterministic business logic, including:

- Grade calculations.
- Weighted averages.
- Grade simulation.
- Use Cases.
- Validation rules.

### Repository Tests

Repository implementations should be tested when their behavior contains meaningful application logic or synchronization behavior.

### ViewModel Tests

ViewModels should be tested for important state transitions and user actions.

### UI Tests

Important user flows should be covered with UI tests where practical.

Examples:

- Authentication.
- Creating an event.
- Creating a task.
- Registering a grade.
- Grade simulation.
- AI action confirmation.

### Backend Tests

Ktor endpoints and relevant backend services should be tested.

### Before Merging

At minimum, contributors should verify:

```text
[ ] Project builds successfully
[ ] Relevant tests pass
[ ] No obvious regressions were introduced
[ ] UI changes were manually checked
[ ] Documentation was updated if necessary
```

## Code of Conduct

Contributors are expected to maintain a respectful, professional, and constructive environment.

Communication should focus on:

- Technical reasoning.
- Evidence.
- Clear feedback.
- Respectful disagreement.
- Collaboration.

Harassment, discrimination, personal attacks, or intentionally disruptive behavior are not acceptable.

## Scope and Academic Context

UniHub is an academic project with a fixed delivery schedule.

Contributions should prioritize:

1. Completing the MVP.
2. Meeting the course requirements.
3. Maintaining software quality.
4. Keeping the architecture understandable.
5. Preserving documentation.
6. Avoiding unnecessary scope expansion.

A technically interesting feature should not be introduced if it creates disproportionate complexity or puts the core project at risk.
