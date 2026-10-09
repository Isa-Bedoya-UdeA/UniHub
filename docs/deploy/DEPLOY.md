# UniHub — Deployment and Operations

## 1. Overview

This document describes the deployment architecture, continuous integration, continuous delivery (CI/CD), environment management, and release process for UniHub.

UniHub consists of two deployable system components:

1. **Android Client Application (`:app`)**: Native Android APK distributed to users and emulators.
2. **Ktor Backend Service (`:server`)**: Standalone Kotlin Ktor service providing authorized AI proxy completions and server-side utilities.

---

## 2. Infrastructure & Hosting Architecture

| Component | Target Runtime | Packaging | Deployment Strategy |
|---|---|---|---|
| Android App | Android 8.0+ (API 26+) | APK / Android App Bundle (`.aab`) | GitHub Releases / Direct APK / Play Console |
| Ktor Backend | JVM 21 (Docker container) | Fat JAR / Container image | Cloud Run / Virtual Machine / Docker Container |
| Cloud Database & Auth | Firebase Cloud Platform | Managed Service | Firebase Console / CLI deployment of Rules |

---

## 3. Environment Configuration

### 3.1 Android Mobile Client

The mobile app relies on compile-time configuration and runtime properties:

- `secrets.properties`: Local configuration containing secrets not checked into source control (e.g., `MAPS_API_KEY`).
- `google-services.json`: Firebase client project configuration.
- `KTOR_BASE_URL`: Injected via Gradle property or BuildConfig. Defaults to `http://10.0.2.2:8080` for Android Emulator local testing.

### 3.2 Ktor Server

The backend requires the following environment variables or system properties:

- `PORT`: HTTP port to bind (default: `8080`).
- `OPENROUTER_API_KEY`: Primary AI provider key.
- `GROQ_API_KEY`: Fallback AI provider key.
- `FIREBASE_CREDENTIALS_PATH`: Optional path to Firebase service account JSON for token verification in staging/production.

---

## 4. Continuous Integration & Quality Gates (CI/CD)

Continuous integration is automated via GitHub Actions workflow (`.github/workflows/build.yml`).

### 4.1 CI Workflow Pipeline Stages

1. **Checkout & Environment Setup**:
   - Ubuntu latest runner.
   - JDK 21 setup with Gradle caching.
   - Grant gradlew execution permissions.
2. **Secrets Configuration**:
   - Injection of `MAPS_API_KEY` from repository secrets into `secrets.properties`.
3. **Compilation & Assembly**:
   - `./gradlew assembleDebug :server:assemble` verifying both Android and backend artifacts compile cleanly.
4. **Automated Test Execution**:
   - `./gradlew testDebugUnitTest :server:test` executing the full automated test suite (Unit, UseCase, Repository, ViewModel, and Server integration tests).
5. **Code Coverage Enforcement (80% Minimum Threshold)**:
   - JaCoCo coverage reports are evaluated for core business and application packages.
   - Pipeline enforces that pull requests and merges into `main`, `master`, and `develop` maintain at least **80% line coverage** on application and core domain use cases. If total line coverage falls below 80%, the CI job fails.
6. **Lint & Static Analysis**:
   - Android lint execution (`./gradlew lintDebug`).
7. **Artifact Archiving**:
   - Test result XML/HTML reports uploaded to workflow run artifacts.
   - Compiled `app-debug.apk` archived on successful main builds.

---

## 5. Build and Release Commands

### 5.1 Local Build Verification

To run tests and assemble artifacts locally:

```bash
# Run unit and integration tests across all modules
./gradlew testDebugUnitTest :server:test

# Build Android debug APK
./gradlew :app:assembleDebug

# Build Ktor server fat JAR
./gradlew :server:jar
```

### 5.2 Server Execution

Run the server locally with direct task:

```bash
./gradlew :server:runDirect
```

Or run via Docker:

```bash
docker build -t unihub-server:latest -f server/Dockerfile .
docker run -p 8080:8080 --env-file .env unihub-server:latest
```

---

## 6. Production Release Procedure

1. Verify all unit tests and CI checks pass cleanly on the release branch.
2. Update version name and version code in `gradle/libs.versions.toml` or `app/build.gradle.kts`.
3. Generate signed release bundle using production keystore.
4. Create GitHub Release tagging the commit (e.g., `v1.0.0`) and attach release notes.
5. Deploy updated backend container image to the production hosting platform.
