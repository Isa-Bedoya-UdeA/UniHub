# UniHub

> A mobile academic organizer for university students to manage subjects, schedules, tasks, grades, deadlines, and academic planning from a single application, with optional AI-assisted interaction.

**Status:** In Development  
**Platform:** Android  
**Language:** Kotlin  
**UI:** Jetpack Compose  
**Repository:** [GitHub](https://github.com/Isa-Bedoya-UdeA/UniHub)

## Overview

UniHub is a mobile application designed to help university students organize their academic life in one place.

The application combines academic planning, calendar management, task management, grade tracking, weighted-average calculation, and grade simulation. It also provides contextual information about where the student needs to be for upcoming physical events and supports remote events through meeting links.

UniHub includes an AI assistant that can be used through text and, where supported, voice input. The assistant can help users create academic information such as events, tasks, subjects, and grades, as well as provide planning recommendations based on the user's academic workload.

The project is developed as a university project focused on applying mobile-computing concepts such as geolocation, connectivity, artificial intelligence, security, cloud services, backend development, virtualization, and software architecture.

For the complete functional scope, see:

**[Requirements](docs/requirements/REQUIREMENTS.md)** · **[Project Planning](docs/PLANNING.md)**  · **[SPEC](SPEC.md)**

## Features

### Academic Organization

- Manage university subjects.
- Store subject information such as credits, professor, code, color, and notes.
- Organize academic information by subject.

### Calendar and Events

- Create, edit, and delete events.
- Define start and end dates and times.
- Associate events with subjects.
- Add tags and notes.
- Support physical and remote events.
- Search for physical locations.
- Select locations using a map.
- View event locations on a map.
- Add meeting links to remote events.
- View upcoming activities from the dashboard.

### Tasks

- Create, edit, complete, and delete academic tasks.
- Associate tasks with subjects.
- Define deadlines and priorities.
- Track pending, in-progress, and completed tasks.
- Receive notifications for relevant deadlines.

### Academic Performance

- Register grades and evaluation weights.
- Calculate subject grades.
- Calculate weighted academic averages.
- Simulate the grade required to reach a target final grade.

### Dashboard

- View relevant information for the current day.
- See upcoming events and deadlines.
- See pending tasks.
- See the next physical location the student needs to attend.
- View an academic performance summary.

### AI Assistant

- Interact with UniHub through a simple AI chat.
- Create events using natural-language instructions.
- Create tasks using natural-language instructions.
- Create subjects using natural-language instructions.
- Register grades using natural-language instructions.
- Ask for recommendations about how to organize the day.
- Use voice input where supported.
- Validate AI-generated actions before modifying application data.

### Connectivity

- Local persistence for core academic information.
- Firebase Authentication.
- Firebase Firestore synchronization.
- Ktor backend API.
- Network-aware error handling.
- Offline access to locally persisted information.

### Security

- Google authentication through Firebase Authentication.
- User-level authorization.
- Protected backend endpoints.
- Firebase Security Rules.
- Secure handling of sensitive information.
- Input validation.
- Token validation.
- Protection of application secrets.

## Tech Stack

| Category                 | Technology                                        |
|--------------------------|---------------------------------------------------|
| Language                 | Kotlin                                            |
| Android                  | Android SDK                                       |
| UI                       | Jetpack Compose                                   |
| Architecture             | Clean Architecture                                |
| Architecture Layers      | Domain, Application, Infrastructure, Presentation |
| Dependency Injection     | Hilt                                              |
| Asynchronous Programming | Kotlin Coroutines                                 |
| Local Persistence        | Room                                              |
| Cloud Database           | Firebase Firestore                                |
| Authentication           | Firebase Authentication + Google OAuth            |
| Maps / Location          | Google Maps Platform                              |
| Backend                  | Ktor                                              |
| API                      | REST                                              |
| AI                       | Gemini / Firebase AI services                     |
| Containerization         | Docker                                            |
| CI/CD                    | GitHub Actions                                    |
| Version Control          | Git + GitHub                                      |
| Design                   | Figma                                             |
| Diagrams                 | Lucidchart / Excalidraw                           |
| Project Management       | Trello                                            |
| Testing                  | JUnit + Android testing tools                     |

Technology choices and architectural decisions are documented in the project's technical documentation.

## Architecture

UniHub follows **Clean Architecture** with a feature-oriented structure.

The main application layers are:

- **Domain:** business entities and repository contracts.
- **Application:** use cases and application-level orchestration.
- **Infrastructure:** local storage, remote services, DTOs, mappers, and repository implementations.
- **Presentation:** Jetpack Compose screens, UI state, events, and ViewModels.

The architecture is designed to maintain separation of concerns, testability, maintainability, and independence between business logic and external frameworks.

For the complete architecture specification, diagrams, and architectural decisions, see:

**[Architecture Documentation](docs/architecture/ARCHITECTURE.md)**

## Project Structure

```text
UniHub/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/com/example/unihub/
│           │   ├── core/
│           │   │   ├── di/
│           │   │   ├── theme/
│           │   │   └── util/
│           │   │
│           │   └── features/
│           │       ├── auth/
│           │       ├── subjects/
│           │       ├── events/
│           │       ├── tasks/
│           │       ├── academic/
│           │       ├── ai/
│           │       ├── dashboard/
│           │       └── settings/
│           │
│           └── res/
│
├── docs/
│   ├── api/
│   │   └── API.md
│   ├── architecture/
│   │   └── ARCHITECTURE.md
│   ├── database/
│   │   └── DATABASE.md
│   ├── deploy/
│   │   └── DEPLOY.md
│   ├── design/
│   │   ├── DESIGNSYSTEM.md
│   │   └── USERJOURNEY.md
│   ├── requirements/
│   │   └── REQUIREMENTS.md
│   ├── security/
│   │   └── SECURITY.md
│   ├── assets/
│   │   ├── screenshots/
│   │   └── demo/
│   └── PLANNING.md
│
├── SPEC.md
├── CONTRIBUTING.md
├── LICENSE
└── README.md
```

The exact implementation structure may evolve as the project develops. Architectural changes must be reflected in the architecture documentation.

## Getting Started

### Prerequisites

Before running UniHub locally, install and configure:

- Android Studio.
- A compatible Android SDK.
- JDK required by the project's Gradle configuration.
- Git.
- Access to the project's Firebase configuration.
- Google Maps configuration if map functionality is enabled locally.
- Docker, if the Ktor backend is being run locally.

### Clone the Repository

```bash
git clone https://github.com/Isa-Bedoya-UdeA/UniHub.git
cd UniHub
```

### Open the Android Project

Open the repository using Android Studio and allow Gradle to synchronize the project.

### Configure Services

Required environment-specific configuration should be provided through local configuration files or environment variables and must not be committed to the repository.

Firebase, Google Maps, AI services, and Ktor configuration are documented in their respective technical documents.

See:

- [API Documentation](docs/api/API.md)
- [Database Documentation](docs/database/DATABASE.md)
- [Security Documentation](docs/security/SECURITY.md)
- [Deployment Documentation](docs/deploy/DEPLOY.md)

### Run the Application

Select an Android emulator or physical Android device and run the `app` configuration from Android Studio.

The exact build and deployment process will be documented in:

**[Deployment Documentation](docs/deploy/DEPLOY.md)**

## Documentation

Project documentation is maintained separately from the source code.

| Document                                          | Description                                                                        |
|---------------------------------------------------|------------------------------------------------------------------------------------|
| [Specification](SPEC.md)                          | Product scope, objectives, and overall project specification                       |
| [Requirements](docs/requirements/REQUIREMENTS.md) | Functional and non-functional requirements                                         |
| [Planning](docs/PLANNING.md)                      | Project planning, milestones, MVP, stretch goals, and 16-week schedule             |
| [Architecture](docs/architecture/ARCHITECTURE.md) | Application architecture, patterns, principles, and architectural decisions        |
| [Database](docs/database/DATABASE.md)             | Local and cloud data models, persistence, and database decisions                   |
| [API](docs/api/API.md)                            | Ktor REST API specification and endpoints                                          |
| [Security](docs/security/SECURITY.md)             | Authentication, authorization, data protection, validation, and security decisions |
| [Design System](docs/design/DESIGNSYSTEM.md)      | UI/UX rules, visual language, components, colors, typography, spacing, and themes  |
| [User Journey](docs/design/USERJOURNEY.md)        | Main user journeys and interaction flows                                           |
| [Deployment](docs/deploy/DEPLOY.md)               | Build, deployment, environment configuration, and release procedures               |
| [Contributing](CONTRIBUTING.md)                   | Development and contribution guidelines                                            |
| [License](LICENSE)                                | Project license                                                                    |

## Screenshots

Application screenshots will be added as the UI is implemented.

### Dashboard

![UniHub Dashboard](docs/assets/screenshots/dashboard.png)

### Calendar

![UniHub Calendar](docs/assets/screenshots/calendar.png)

### Academic Performance

![UniHub Academic Performance](docs/assets/screenshots/academic.png)

### AI Assistant

![UniHub AI Assistant](docs/assets/screenshots/ai-assistant.png)

> Screenshots are stored in `docs/assets/screenshots/`.

## Demo

The final demonstration video will be added to the repository when the application reaches the corresponding development stage.

**Demo video:** [Watch the UniHub demo](docs/assets/demo/UniHub-Demo.mp4)

> Demo assets are stored in `docs/assets/demo/`.

## Development

Development follows a milestone-based workflow aligned with the 16-week project plan.

The project uses GitHub for:

- Source control.
- Issue tracking.
- Milestones.
- Pull requests.
- Releases.
- Continuous integration.

Trello is used for project task management.

Design and planning tools include:

- Figma and Stitch for UI/UX design and prototypes.
- Lucidchart for technical diagrams.
- Excalidraw for selected user/system diagrams.

Development principles include:

- Clean Architecture.
- SOLID.
- Separation of concerns.
- Feature-oriented organization.
- Reusable UI components.
- Testable business logic.
- Meaningful Git commits.
- Documentation alongside architectural changes.

See:

**[Contributing Guidelines](CONTRIBUTING.md)**

**[Project Planning](docs/PLANNING.md)**

## Testing

Testing will cover the most critical parts of the application, with priority given to deterministic business logic and core user flows.

Planned testing includes:

- Unit tests.
- Use Case tests.
- Repository tests.
- ViewModel tests.
- UI tests.
- Integration tests.
- Backend/API tests.
- Manual end-to-end testing.

Particular attention will be given to:

- Weighted-average calculations.
- Grade simulation.
- Task and event logic.
- Authentication flows.
- Repository behavior.
- AI action validation.
- Offline and synchronization behavior.

Testing configuration and project quality procedures will be documented as the implementation progresses.

## License

This project is licensed under the terms specified in the [LICENSE](LICENSE) file.

## Contributors

Developers:

- Isabela Bedoya Gaviria — Systems Engineering Student, Universidad de Antioquia
- Rafael Angel Aleman Castillo — Systems Engineering Student, Universidad de Antioquia
