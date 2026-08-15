# UniHub — Project Planning

> **Aplicación móvil de organización académica para estudiantes universitarios**

## 1. Identificación del problema

### 1.1 Problema

Los estudiantes universitarios deben gestionar simultáneamente clases, tareas, evaluaciones, proyectos, reuniones, horarios y resultados académicos. Esta información suele estar distribuida entre calendarios, aplicaciones de notas, plataformas institucionales, hojas de cálculo y otras herramientas independientes.

Esto dificulta tener una visión unificada de:

* Qué actividades tiene el estudiante.
* Cuándo debe realizarlas.
* Dónde debe asistir.
* A qué materia pertenece cada actividad.
* Qué tareas están pendientes.
* Cómo está progresando académicamente.
* Qué nota necesita para alcanzar un resultado determinado.

Además, introducir manualmente toda esta información puede resultar tedioso. Un estudiante puede recibir información en lenguaje natural como:

> "El jueves tengo clase de Computación Móvil de 6 a 8 en la UdeA."

UniHub busca centralizar esta información en una aplicación móvil y utilizar inteligencia artificial como una interfaz alternativa para registrar, consultar y organizar información académica.

### 1.2 Concepto

**UniHub** es una aplicación móvil de organización académica para estudiantes universitarios.

Su propósito es centralizar:

```text
Calendario
    +
Materias
    +
Tareas
    +
Notas
    +
Rendimiento académico
    +
Ubicación de eventos
    +
Eventos remotos
    +
Inteligencia artificial
```

La aplicación permitirá crear y consultar actividades académicas, asociarlas opcionalmente a materias y ubicaciones, gestionar tareas y calificaciones, calcular el promedio ponderado y simular resultados académicos.

La IA funcionará como una interfaz conversacional sencilla para realizar acciones como:

```text
"Agrega una clase de Computación Móvil
el jueves de 6 a 8 en la UdeA."

"Agrega una tarea de investigación
para el viernes con prioridad alta."

"Registra un 4.2 en Matemáticas
para el segundo parcial."

"¿Cómo debería organizar mi día
si tengo estas tres tareas pendientes?"
```

### 1.3 Nombre

**UniHub**

El nombre representa un espacio centralizado para gestionar diferentes aspectos de la vida universitaria desde una misma aplicación.

### 1.4 Objetivo general

Desarrollar una aplicación móvil Android para la gestión integral de actividades académicas universitarias, incorporando calendario, materias, tareas, rendimiento académico, geolocalización contextual, inteligencia artificial, conectividad en la nube y mecanismos de seguridad mediante una arquitectura de software limpia y mantenible.

### 1.5 Objetivos específicos

1. Diseñar e implementar una interfaz móvil moderna, accesible y consistente siguiendo principios de UI/UX y Material Design.
2. Permitir la gestión de materias, eventos, tareas y calificaciones.
3. Permitir asociar eventos presenciales con ubicaciones geográficas y eventos remotos con enlaces de reunión.
4. Implementar el cálculo de promedio ponderado por materia y semestre.
5. Implementar un simulador de notas necesarias para alcanzar una calificación objetivo.
6. Implementar autenticación mediante Google y Firebase Authentication.
7. Implementar persistencia local y sincronización de información con Firebase.
8. Integrar inteligencia artificial mediante texto y entrada de voz cuando sea técnicamente viable.
9. Implementar un asistente académico capaz de utilizar información contextual del estudiante para generar recomendaciones de planificación.
10. Desarrollar una API REST utilizando Ktor.
11. Aplicar Clean Architecture, SOLID, patrones de diseño, corrutinas, testing y CI/CD.
12. Documentar técnicamente el proyecto y su proceso de desarrollo.

## 2. Estilo visual

### 2.1 Dirección visual

UniHub tendrá una identidad visual:

* Moderna.
* Universitaria.
* Profesional.
* Limpia.
* Minimalista.
* Accesible.
* Orientada a productividad.
* Basada en Material 3.
* Optimizada para uso frecuente.

La interfaz priorizará la jerarquía de información, la legibilidad y la reducción de carga cognitiva sobre elementos decorativos innecesarios.

### 2.2 Identidad cromática

La identidad visual de UniHub utilizará una combinación de azul índigo, violeta/lila y cyan como acento. El modo claro utilizará fondos y superficies neutras y luminosas, mientras que el modo oscuro utilizará fondos azulados profundos para mantener el contraste y la jerarquía visual.

Los colores se organizarán en cuatro grupos:

- Brand colors: Primary, Secondary y Accent.
- Boxes: Background, Surface, Cards y Border.
- Text: Primary Text, Secondary Text y Disabled Text.
- Semantic colors: Info, Success, Warning y Error.

Los valores concretos estarán centralizados dentro de `DesignSystem`, separados entre los esquemas de modo claro y modo oscuro.

### 2.3 Temas

La aplicación soportará:

* Light Theme.
* Dark Theme.
* System Theme.

Los componentes no deberán utilizar colores hardcoded. Todos los colores deberán provenir del sistema de diseño.

## 3. Vistas

### 3.1 Splash

Responsabilidades:

* Inicialización de la aplicación.
* Restauración de sesión.
* Carga de configuración inicial.
* Determinación de la ruta inicial.

### 3.2 Onboarding

Presentará brevemente:

* Organización académica.
* Calendario.
* Materias.
* Tareas.
* Rendimiento académico.
* IA.
* Eventos presenciales y remotos.

Deberá poder omitirse.

### 3.3 Authentication

* Inicio de sesión con Google.
* Restauración de sesión.
* Manejo de errores.
* Cierre de sesión.

### 3.4 Dashboard

El Dashboard será la vista principal.

Mostrará información relevante del día:

```text
Buenos días

PRÓXIMAS ACTIVIDADES
─────────────────────
08:00  Computación Móvil
       UdeA

10:00  Investigación
       Remoto

14:00  Reunión de proyecto
       Ruta N

TAREAS PENDIENTES
─────────────────
• Informe de Investigación
• Proyecto de Computación Móvil

RENDIMIENTO ACADÉMICO
──────────────────────
Promedio actual: 4.1
```

El Dashboard priorizará:

1. Actividades próximas.
2. Horarios.
3. Próximas ubicaciones a las que debe asistir.
4. Tareas y deadlines.
5. Resumen académico.

La ubicación actual del usuario no será una funcionalidad central.

### 3.5 Calendar

Permitirá consultar:

* Vista mensual.
* Vista semanal.
* Vista diaria.
* Eventos.
* Clases.
* Reuniones.
* Tareas asociadas a fechas.

### 3.6 Event Detail

Cada evento tendrá:

```text
Start date
End date
Start time
End time
Name
Tags
Subject (optional)
Location type
Location
Meeting URL (remote)
Notes
```

### 3.7 Event Location

Los eventos presenciales podrán tener una ubicación.

El usuario podrá:

* Buscar una dirección.
* Seleccionar una ubicación en un mapa.
* Utilizar la ubicación del dispositivo para facilitar la selección.
* Visualizar la ubicación en Google Maps.
* Guardar dirección y coordenadas.

No se implementará seguimiento continuo de ubicación.

### 3.8 Remote Event

Los eventos podrán ser remotos.

```text
Location type:
    REMOTE

Meeting URL:
    https://...
```

El usuario podrá abrir el enlace directamente desde el detalle del evento.

### 3.9 Subjects

Una materia tendrá como información:

```text
Name
Code
Credits
Professor (optional)
Color
Notes
```

Las materias podrán relacionarse con:

* Eventos.
* Tareas.
* Calificaciones.

### 3.10 Tasks

Una tarea tendrá:

```text
Title
Description
Subject
Due date
Priority
Status
Tags
Notes
```

Estados:

```text
Pending
In Progress
Completed
```

Prioridades:

```text
Low
Medium
High
```

### 3.11 Academic Performance

Permitirá:

* Registrar evaluaciones.
* Definir porcentajes.
* Registrar calificaciones.
* Calcular notas finales.
* Calcular promedio por materia.
* Calcular promedio ponderado.
* Consultar el rendimiento académico.

### 3.12 Grade Simulator

El simulador estará inspirado conceptualmente en herramientas como [Cuánto Necesito Para el Final](https://cuantonecesitoparaelfinal.com/).

Ejemplo:

```text
Materia: Matemáticas

Nota actual: 3.2
Porcentaje evaluado: 60%
Porcentaje restante: 40%

Nota objetivo: 3.0

Resultado:

Necesitas aproximadamente 2.70
en el porcentaje restante.
```

El cálculo será:

* Determinista.
* Local.
* Independiente de IA.
* Completamente testeable.

### 3.13 AI Assistant

La aplicación contará con un mini chat para interactuar con la información académica.

Permitirá:

* Crear eventos.
* Crear tareas.
* Crear materias.
* Registrar notas.
* Consultar información.
* Obtener recomendaciones de planificación.

Entrada:

```text
Texto
Voz → Speech-to-Text
```

Salida:

```text
Respuesta
+
Acción estructurada
+
Confirmación cuando corresponda
```

## 4. Funcionalidades

### 4.1 Authentication

* Google OAuth.
* Firebase Authentication.
* Persistencia de sesión.
* Logout.
* Manejo de errores.

### 4.2 Subjects

* Crear materia.
* Editar materia.
* Eliminar materia.
* Consultar materia.
* Asociar eventos.
* Asociar tareas.
* Asociar calificaciones.

### 4.3 Events

* Crear evento.
* Editar evento.
* Eliminar evento.
* Consultar evento.
* Fecha de inicio.
* Fecha de finalización.
* Hora de inicio.
* Hora de finalización.
* Nombre.
* Tags.
* Materia opcional.
* Notas.
* Modalidad presencial.
* Modalidad remota.
* Ubicación.
* Enlace de reunión.

### 4.4 Tasks

* CRUD.
* Materia.
* Prioridad.
* Deadline.
* Estado.
* Tags.
* Notas.

### 4.5 Calendar

* Vista mensual.
* Vista semanal.
* Vista diaria.
* Eventos.
* Tareas con fecha.
* Navegación entre fechas.

### 4.6 Notifications

Se implementarán notificaciones relacionadas con:

* Inicio próximo de eventos.
* Deadlines de tareas.
* Recordatorios configurables.

No se implementarán notificaciones basadas en geofencing.

### 4.7 Academic

* Registro de calificaciones.
* Porcentajes.
* Cálculo de nota final.
* Promedio por materia.
* Promedio ponderado.
* Simulador de nota necesaria.
* Resumen académico.

### 4.8 Geolocation

La geolocalización tendrá un alcance deliberadamente limitado.

Se utilizará exclusivamente para facilitar la gestión de ubicaciones de eventos:

```text
Event
   ↓
Physical location
   ↓
Search / Map selection
   ↓
Google Maps
   ↓
Address + Coordinates
   ↓
Event
```

No se implementarán:

* Geofencing.
* Seguimiento continuo.
* Exploración por GPS.
* Desbloqueos por ubicación.
* Automatizaciones basadas en ubicación.

## 5. Inteligencia Artificial

### 5.1 Objetivo

La IA será una funcionalidad de apoyo y no una dependencia del funcionamiento principal.

UniHub deberá continuar funcionando aunque el servicio de IA no esté disponible.

### 5.2 AI Command Assistant

El usuario podrá utilizar lenguaje natural.

Ejemplos:

```text
"Agrega una clase de Computación Móvil
el jueves de 6 a 8 en la UdeA."

"Agrega una tarea de Investigación
para el viernes a las 11:59 PM."

"Crea una materia llamada Matemáticas
de 4 créditos."

"Registra un 4.5 en el segundo parcial
de Matemáticas."
```

La IA convertirá estas solicitudes en acciones estructuradas.

Ejemplo conceptual:

```json
{
  "action": "CREATE_EVENT",
  "name": "Computación Móvil",
  "start": "...",
  "end": "...",
  "subject": "...",
  "location": "..."
}
```

La aplicación validará los datos antes de ejecutar la operación.

### 5.3 AI Academic Assistant

El asistente podrá analizar información como:

```text
Current date
Upcoming events
Pending tasks
Deadlines
Subjects
Academic performance
Available time
```

Ejemplo:

```text
Usuario:
"¿Cómo debería organizar mi día?"

UniHub:
"Tienes una clase a las 10:00,
una tarea pendiente para mañana
y una entrega importante el viernes.

Te recomiendo:

1. Terminar la tarea pendiente.
2. Avanzar en la entrega del viernes.
3. Utilizar el espacio libre de 3:00 PM
   para estudiar Matemáticas."
```

La recomendación no deberá modificar automáticamente el calendario ni las tareas.

### 5.4 Voice Input

Flujo:

```text
Voice
  ↓
Speech-to-Text
  ↓
AI
  ↓
Structured Action
  ↓
Validation
  ↓
User Confirmation
  ↓
Use Case
```

La entrada de voz será MVP si la integración resulta estable.

Si existen dificultades técnicas importantes, la entrada textual será prioritaria y la voz pasará a Stretch Goal.

### 5.5 AI Reliability

Se implementarán:

* System prompt.
* Context builder.
* Structured output.
* Input validation.
* Action validation.
* Confirmation.
* Fallback.
* Error handling.
* Loading state.
* Request control.
* Manejo de ausencia de conexión.

La IA no tendrá acceso indiscriminado a toda la base de datos. Se proporcionará únicamente el contexto necesario para cada operación.

## 6. Ktor

Ktor funcionará como backend REST complementario.

No se desarrollará un backend innecesariamente complejo.

### 6.1 Responsabilidades

Ktor permitirá demostrar:

* Desarrollo backend.
* API REST.
* Autenticación.
* Validación.
* DTOs.
* Middleware.
* Comunicación cliente-servidor.
* Manejo de errores.

### 6.2 Endpoints iniciales

```text
GET  /api/health

GET  /api/profile

GET  /api/academic/summary

POST /api/academic/calculate

POST /api/ai/parse
```

Los endpoints definitivos podrán cambiar durante la implementación.

### 6.3 Authentication

El cliente enviará un Firebase ID Token.

```text
Android
   ↓
Firebase Authentication
   ↓
ID Token
   ↓
Ktor
   ↓
Token Validation
   ↓
Authorized Request
```

Ktor deberá validar el token antes de procesar operaciones protegidas.

## 7. Seguridad

### 7.1 Authentication

* Firebase Authentication.
* Google OAuth.
* Firebase ID Tokens.
* Gestión segura de sesión.

### 7.2 Authorization

* Firebase Security Rules.
* Ownership de recursos.
* Validación de usuario.
* Middleware de autenticación en Ktor.

Cada usuario únicamente podrá acceder a sus propios datos.

### 7.3 Local Security

Se utilizarán mecanismos propios de Android para proteger información sensible.

Cuando sea necesario:

* Android Keystore.
* Encriptación de información sensible.
* Almacenamiento seguro de credenciales y tokens.

### 7.4 Cloud Security

Las reglas de Firestore deberán garantizar:

```text
User A
  ↓
User A data only

User B
  ↓
User B data only
```

No se confiará únicamente en restricciones implementadas en la interfaz.

### 7.5 API Security

* Firebase ID Token validation.
* Input validation.
* DTO validation.
* Error handling.
* No secrets en el código fuente.
* Secrets mediante GitHub Actions Secrets.
* Configuración mediante variables de entorno.

### 7.6 Additional Security

Si el tiempo lo permite:

* Firebase App Check.
* Rate limiting básico.
* Logging seguro.
* Revisión de dependencias.

## 8. Arquitectura y diseño

### 8.1 Clean Architecture

UniHub utilizará una arquitectura organizada en cuatro capas principales:

```text
Presentation
      ↓
Application
      ↓
Domain
      ↑
Infrastructure
```

Las capas internas no deberán depender directamente de frameworks o servicios externos.

### 8.2 Domain

Contendrá el núcleo del negocio.

No dependerá de:

* Android.
* Jetpack Compose.
* Firebase.
* Room.
* Ktor.
* Google Maps.
* Gemini.

Contendrá:

* Domain models.
* Value objects cuando sean necesarios.
* Repository contracts.
* Reglas fundamentales del dominio.

Ejemplos:

```text
User
Subject
Event
Task
Grade
AcademicSummary
Location
```

### 8.3 Application

Contendrá los casos de uso y la orquestación de operaciones.

Ejemplos:

```text
CreateEventUseCase
UpdateEventUseCase
DeleteEventUseCase
CreateTaskUseCase
CompleteTaskUseCase
CreateSubjectUseCase
RegisterGradeUseCase
CalculateWeightedAverageUseCase
CalculateRequiredGradeUseCase
GetDashboardUseCase
ProcessAICommandUseCase
```

Cada Use Case deberá tener una responsabilidad específica.

### 8.4 Infrastructure

Contendrá las implementaciones concretas y comunicación con sistemas externos:

* Firebase.
* Firestore.
* Room.
* Ktor.
* Google Maps.
* AI.
* Android Location APIs.
* Notification APIs.

Aquí estarán:

```text
DTOs
Entities
DAOs
Data Sources
Mappers
Repository Implementations
API clients
External service adapters
```

### 8.5 Presentation

Contendrá:

* Jetpack Compose.
* Screens.
* ViewModels.
* UI State.
* UI Events.
* UI components.

Las Screens no accederán directamente a Firebase, Room, Ktor o APIs externas.

## 9. Estructura de carpetas

La aplicación se organizará por features para mantener alta cohesión y reducir acoplamiento.

```text
app/src/main/java/com/example/unihub/
│
├── core/
│   │
│   ├── di/
│   │   ├── NetworkModule.kt
│   │   ├── DatabaseModule.kt
│   │   ├── FirebaseModule.kt
│   │   └── AppModule.kt
│   │
│   ├── designsystem/
│   │   │
│   │   ├── color/
│   │   │   ├── Color.kt
│   │   │   └── ColorScheme.kt
│   │   │
│   │   ├── component/
│   │   │   ├── UniHubButton.kt
│   │   │   ├── UniHubCard.kt
│   │   │   ├── UniHubTextField.kt
│   │   │   ├── UniHubTopBar.kt
│   │   │   └── UniHubDialog.kt
│   │   │
│   │   ├── shape/
│   │   │   └── Shape.kt
│   │   │
│   │   ├── spacing/
│   │   │   └── Spacing.kt
│   │   │
│   │   ├── typography/
│   │   │   └── Typography.kt
│   │   │
│   │   └── theme/
│   │       └── UniHubTheme.kt
│   │
│   ├── navigation/
│   │   ├── AppNavigation.kt
│   │   └── Routes.kt
│   │
│   ├── util/
│   └── common/
│
└── features/
    │
    ├── auth/
    ├── dashboard/
    ├── calendar/
    ├── events/
    ├── subjects/
    ├── tasks/
    ├── academic/
    ├── gradesimulator/
    ├── location/
    ├── ai/
    └── settings/
```

Cada feature seguirá una estructura consistente:

```text
feature/
│
├── domain/
│   ├── model/
│   └── repository/
│
├── application/
│   └── usecase/
│
├── infrastructure/
│   ├── data/
│   │   ├── remote/
│   │   │   ├── dto/
│   │   │   └── datasource/
│   │   │
│   │   └── local/
│   │       ├── entity/
│   │       ├── dao/
│   │       └── datasource/
│   │
│   └── repository/
│
└── presentation/
    ├── state/
    ├── viewmodel/
    └── screen/
        ├── FeatureScreen.kt
        └── components/
```

Ejemplo para Authentication:

```text
features/auth/
│
├── domain/
│   ├── model/
│   │   └── User.kt
│   └── repository/
│       └── AuthRepository.kt
│
├── application/
│   └── usecase/
│       ├── LoginUseCase.kt
│       ├── LogoutUseCase.kt
│       └── GetCurrentUserUseCase.kt
│
├── infrastructure/
│   ├── data/
│   │   └── remote/
│   │       ├── datasource/
│   │       └── dto/
│   └── repository/
│       └── AuthRepositoryImpl.kt
│
└── presentation/
    ├── state/
    │   └── LoginUiState.kt
    ├── viewmodel/
    │   └── LoginViewModel.kt
    └── screen/
        ├── LoginScreen.kt
        └── components/
```

## 10. Patrones de diseño

Se utilizarán patrones únicamente cuando aporten valor real.

### MVVM

```text
Screen
  ↓
ViewModel
  ↓
UseCase
  ↓
Repository
```

### Repository Pattern

Domain definirá contratos:

```text
EventRepository
TaskRepository
SubjectRepository
AcademicRepository
```

Infrastructure implementará estos contratos.

### Use Case Pattern

Las operaciones importantes estarán aisladas en casos de uso.

### Dependency Injection

Se utilizará Hilt para proporcionar dependencias.

### Reactive State

Se utilizarán:

* Flow.
* StateFlow.
* Coroutines.

### Mapper Pattern

Se utilizarán mappers para transformar:

```text
DTO
 ↓
Data Model
 ↓
Domain Model
 ↓
UI Model
```

Cuando las diferencias entre representaciones lo justifiquen.

## 11. SOLID

### Single Responsibility Principle

Cada clase tendrá una responsabilidad clara.

### Open/Closed Principle

Las funcionalidades deberán poder extenderse sin modificar innecesariamente código existente.

### Liskov Substitution Principle

Las implementaciones deberán respetar completamente sus contratos.

### Interface Segregation Principle

Las interfaces deberán ser pequeñas y específicas.

### Dependency Inversion Principle

Las capas internas dependerán de abstracciones, no de implementaciones concretas.

## 12. Coroutines y concurrencia

Kotlin Coroutines se utilizará para:

* Operaciones de red.
* Room.
* Firebase.
* IA.
* Flujos reactivos.
* Operaciones potencialmente costosas.

Principios:

```text
No blocking operations on Main
        ↓
Suspend functions
        ↓
Structured concurrency
        ↓
StateFlow
```

Los ViewModels manejarán operaciones respetando el ciclo de vida correspondiente.

## 13. UI/UX

### 13.1 Principios

UniHub seguirá:

* Material 3.
* Consistencia visual.
* Jerarquía clara.
* Feedback inmediato.
* Accesibilidad.
* Navegación predecible.
* Reducción de carga cognitiva.
* Touch targets adecuados.
* Formularios claros.
* Confirmación para acciones destructivas.

### 13.2 Estados de interfaz

Cada pantalla importante deberá considerar:

```text
Loading
Success
Empty
Error
Offline
```

### 13.3 Navegación

Propuesta inicial:

```text
                  UniHub
                     │
          ┌──────────┼──────────┐
          ↓          ↓          ↓
       Dashboard   Calendar    Tasks
                     │
                Event Detail

          ┌──────────┼──────────┐
          ↓          ↓          ↓
       Subjects   Academic      AI
```

Settings estará disponible desde el perfil o navegación secundaria.

### 13.4 Bottom Navigation

La navegación principal podrá utilizar:

```text
Home
Calendar
Tasks
Academic
AI
```

La estructura definitiva será validada mediante prototipos.

### 13.5 Accessibility

Se contemplará:

* Contraste adecuado.
* Content descriptions.
* Texto escalable.
* Áreas táctiles apropiadas.
* No depender únicamente del color.
* Compatibilidad con lectores de pantalla.
* Estados claramente diferenciados.

## 14. MVP vs Stretch Goals

### 14.1 MVP

#### Core

* [ ] Authentication.
* [ ] Dashboard.
* [ ] Subjects CRUD.
* [ ] Events CRUD.
* [ ] Tasks CRUD.
* [ ] Calendar.
* [ ] Notifications.
* [ ] Academic grades.
* [ ] Weighted average.
* [ ] Grade simulator.
* [ ] Light Theme.
* [ ] Dark Theme.

#### Geolocation

* [ ] Event location.
* [ ] Google Maps integration.
* [ ] Address search.
* [ ] Map selection.
* [ ] Coordinates.
* [ ] Address persistence.
* [ ] Map preview.
* [ ] Remote events.
* [ ] Meeting URL.

#### Firebase

* [ ] Firebase Authentication.
* [ ] Google OAuth.
* [ ] Firestore.
* [ ] Local persistence.
* [ ] Cloud synchronization.
* [ ] Security Rules.

#### AI

* [ ] AI mini chat.
* [ ] Natural-language event creation.
* [ ] Natural-language task creation.
* [ ] Natural-language subject creation.
* [ ] Natural-language grade registration.
* [ ] Academic planning assistant.

#### Ktor

* [ ] Ktor server.
* [ ] REST API.
* [ ] Authentication middleware.
* [ ] Firebase ID Token validation.
* [ ] DTOs.
* [ ] Validation.
* [ ] Error handling.
* [ ] At least two useful endpoints.

#### Quality

* [ ] Unit tests.
* [ ] Use Case tests.
* [ ] Repository tests.
* [ ] ViewModel tests.
* [ ] Basic UI tests.
* [ ] GitHub Actions.
* [ ] Docker.
* [ ] Documentation.

### 14.2 Stretch Goals

Solo se implementarán después de completar y estabilizar el MVP.

* [ ] Google Calendar integration.
* [ ] Advanced recurring events.
* [ ] Advanced notification customization.
* [ ] Voice input if not completed in MVP.
* [ ] AI-generated weekly planning.
* [ ] Advanced academic statistics.
* [ ] Android home-screen widget.
* [ ] Advanced offline synchronization.
* [ ] Export/import academic information.
* [ ] Advanced accessibility improvements.

### 14.3 Fuera del alcance

No se implementarán:

* Social network.
* Messaging.
* Friends system.
* Gamification.
* Leaderboards.
* Geofencing.
* Continuous location tracking.
* "Estoy aquí".
* Location-based reminders.
* Location-based exploration.
* GPS navigation propia.
* Automatizaciones basadas en la ubicación actual.

## 15. Entregas

Las fechas establecidas por el profesor se consideran **fechas máximas de entrega**, no fechas de inicio del trabajo.

La estrategia será completar los entregables principales antes de cada fecha y utilizar las semanas siguientes para estabilización y funcionalidades adicionales.

### 15.1 Anteproyecto — Semana 3

#### Documento

* [ ] Justificación.
* [ ] Descripción del problema.
* [ ] Antecedentes.
* [ ] Objetivo general.
* [ ] Objetivos específicos.
* [ ] Cronograma.

#### Presentación

* [ ] PowerPoint.
* [ ] Exposición de 5 minutos.
* [ ] Demo del prototipo.

#### Diseño

* [ ] Figma.
* [ ] Mockups.
* [ ] Prototype.
* [ ] Design System v0.1.
* [ ] Navigation Map.
* [ ] USM.

#### Arquitectura

* [ ] ERD / MER v0.1.
* [ ] Architecture Diagram v0.1.
* [ ] Component Diagram v0.1.
* [ ] Firestore Model v0.1.
* [ ] Security Model v0.1.

#### Repositorio

* [ ] GitHub.
* [ ] README.md.
* [ ] SPEC.md.
* [ ] ARCHITECTURE.md.
* [ ] DATABASE.md.
* [ ] SECURITY.md.
* [ ] DESIGNSYSTEM.md.
* [ ] CONTRIBUTING.md.
* [ ] LICENSE.

#### Código inicial

* [ ] Android project.
* [ ] Jetpack Compose.
* [ ] Navigation skeleton.
* [ ] Theme.
* [ ] Design System.
* [ ] Dependency Injection.
* [ ] Initial feature structure.

### 15.2 Avance — Semana 9

El objetivo no será solamente cumplir el "50%". La aplicación deberá tener una base funcional sólida.

#### UI

* [ ] UI 100%.
* [ ] Navigation 100%.
* [ ] Light Theme.
* [ ] Dark Theme.
* [ ] Design System.
* [ ] Loading states.
* [ ] Empty states.
* [ ] Error states.

#### Funcionalidad

* [ ] Authentication.
* [ ] Subjects.
* [ ] Events.
* [ ] Tasks.
* [ ] Calendar.
* [ ] Academic grades.
* [ ] Grade simulator.
* [ ] Local persistence.
* [ ] Firebase persistence.
* [ ] Location-based events.
* [ ] Notifications.

#### AI

* [ ] AI integration functional.
* [ ] Mini chat.
* [ ] At least one functional action through natural language.

#### Ktor

* [ ] Ktor API.
* [ ] Authentication middleware.
* [ ] At least one useful endpoint.

#### Documento

* [ ] Descripción del avance.
* [ ] Dificultades encontradas.
* [ ] Estrategias utilizadas.
* [ ] Conclusiones parciales.
* [ ] Trabajo futuro.

#### End-to-End Flow

```text
Login
  ↓
Dashboard
  ↓
Create Subject
  ↓
Create Event
  ↓
Select Location
  ↓
Save Event
  ↓
Display Event on Calendar
  ↓
Create Task
  ↓
Register Grade
  ↓
Calculate Academic Summary
```

### 15.3 Entrega Final — Semana 16

#### Aplicación

* [ ] MVP completo.
* [ ] Release build.
* [ ] Signed APK.
* [ ] Testing completo.
* [ ] Corrección de bugs críticos.

#### Presentación

* [ ] Introducción.
* [ ] Explicación de la aplicación.
* [ ] Demostración.
* [ ] Conclusiones.
* [ ] Trabajo futuro.
* [ ] Demo script.

#### Documentación

* [ ] README final.
* [ ] SPEC final.
* [ ] Architecture Diagram final.
* [ ] Component Diagram final.
* [ ] MER final.
* [ ] DATABASE.md.
* [ ] SECURITY.md.
* [ ] API.md.
* [ ] DESIGNSYSTEM.md.
* [ ] DEPLOY.md.
* [ ] CONTRIBUTING.md.

## 16. Cronograma de 16 semanas

### Semana 1 — Discovery

```text
Problem definition
Requirements
Scope definition
User journeys
Functional requirements
Non-functional requirements

GitHub repository
README.md
SPEC.md
LICENSE
CONTRIBUTING.md

Initial backlog
Initial milestones
```

**Objetivo:** definir claramente qué se construirá y qué quedará fuera del proyecto.

### Semana 2 — Architecture + UX

```text
Figma
Design System v0.1
Navigation Map
Wireframes
Mockups

Architecture v0.1
MER v0.1
Component Diagram
Architecture Diagram
Firestore model
Security model

Android project
Compose
Navigation
Theme
DI
Crear Issues en Github para todas las milestones
```

**Objetivo:** tener diseño y arquitectura suficientemente definidos para comenzar la implementación.

### Semana 3 — ANTEPROYECTO 🚩

```text
✓ Documento
✓ PowerPoint
✓ Figma
✓ Prototype
✓ GitHub
✓ README
✓ SPEC.md
✓ ARCHITECTURE.md
✓ DATABASE.md
✓ SECURITY.md
✓ DESIGNSYSTEM.md
✓ Architecture Diagram
✓ Component Diagram
✓ MER
✓ USM
✓ Initial Android project
✓ Design System
✓ Navigation skeleton
```

**Objetivo:** presentar una propuesta sólida, con diseño, arquitectura y una base de implementación ya iniciada.

### Semana 4 — Core UI

```text
Splash
Onboarding
Authentication UI
Dashboard
Bottom Navigation
Settings
Calendar UI
Tasks UI
Subjects UI
Academic UI

Design System refinement
```

**Objetivo:** construir la estructura visual principal de la aplicación.

### Semana 5 — Academic Core

```text
Subject model
Subject CRUD

Task model
Task CRUD

Event model
Event CRUD

Calendar
Event detail
Task detail

Academic model
Grade model
Weighted average
Grade simulator
```

**Objetivo:** completar el núcleo funcional académico.

### Semana 6 — Local Persistence + Notifications

```text
Room
Entities
DAOs
Mappers
Local repositories

Offline-first foundation

Notifications
Event reminders
Task reminders
Deadline reminders

ViewModel states
Flow
Coroutines
Error handling
```

**Objetivo:** lograr que la aplicación funcione de forma consistente localmente.

### Semana 7 — Geolocation + Firebase Foundation

```text
Location permissions
Google Maps integration
Location search
Map selection
Event coordinates
Address persistence

Remote events
Meeting URL

Firebase project
Firestore
Authentication
Google OAuth
Initial Security Rules
```

**Objetivo:** completar la funcionalidad de ubicación de eventos y comenzar la infraestructura cloud.

### Semana 8 — Cloud Integration + Advance Freeze

```text
Firebase Authentication
Firestore repositories
Cloud synchronization
Security Rules
User ownership

Dashboard real data
Calendar real data
Tasks real data
Academic real data

AI integration prototype
Ktor prototype

Bug fixing
Integration testing
```

**Objetivo:** tener internamente un MVP funcional antes de la entrega oficial de avance.

### Semana 9 — AVANCE 🚩

```text
UI 100%
Local persistence 100%
Authentication 100%
Firebase persistence 100%
Academic core 100%
Calendar 100%
Tasks 100%
Location events 100%

AI:
At least one functional integration

Ktor:
Minimal functional API

Testing:
Core unit tests

Documentation:
Progress updated
```

#### Entrega

```text
✓ Documento de avance
✓ Repository updated
✓ Functional build
✓ UI complete
✓ 2–3+ major functionalities
✓ End-to-end flow
```

**Objetivo:** superar el requisito mínimo del profesor y llegar a una aplicación que ya pueda demostrarse.

### Semana 10 — AI

```text
AI mini chat
Gemini integration
Prompt system
Context builder

Create Event
Create Task
Create Subject
Register Grade

Structured output
Validation
Confirmation

Academic assistant
Planning recommendations

Fallback
Error handling
Loading states
Request control
```

**Objetivo:** integrar la IA con los casos de uso reales de UniHub.

### Semana 11 — Ktor

```text
Ktor server
REST API
Authentication middleware
Firebase ID Token validation

DTO
Serialization
Validation
Error responses

/api/health
/api/profile
/api/academic/summary
/api/academic/calculate
/api/ai/parse
```

**Objetivo:** completar el backend mínimo necesario para cumplir los requisitos técnicos.

### Semana 12 — Integration

```text
Android ↔ Ktor
Android ↔ Firebase
Android ↔ AI
Android ↔ Maps

End-to-end flows
Repository consistency
Error handling
Offline behavior
Synchronization

Notifications
AI actions
Academic calculations
```

**Objetivo:** integrar todas las piezas principales.

A partir de esta semana no deberán incorporarse funcionalidades grandes que no estén en el MVP.

### Semana 13 — Security + Quality

```text
Firebase Security Rules
Token validation
App Check if viable
Input validation
Secrets management
Secure local storage

Network failures
Offline states
Error handling

Architecture review
SOLID review
Code cleanup
Refactoring
```

**Objetivo:** convertir el prototipo funcional en un proyecto técnicamente sólido.

### Semana 14 — Polish + UX

```text
Animations
Transitions
Microinteractions

Dark theme refinement
Accessibility
Typography
Spacing
Shapes

Empty states
Loading states
Error states
Confirmation dialogs

Form validation
Keyboard behavior
Responsive layouts
```

**Objetivo:** alcanzar calidad visual y de experiencia de usuario.

### Semana 15 — Testing + Documentation

```text
Unit tests
UseCase tests
Repository tests
ViewModel tests
UI tests
Integration tests

Manual testing
Regression testing
Bug fixing

Performance review
Crash/error review
```

Actualizar:

```text
ARCHITECTURE.md
DATABASE.md
SECURITY.md
DEPLOY.md
API.md
DESIGNSYSTEM.md
README.md
SPEC.md
CONTRIBUTING.md
```

Generar:

```text
Screenshots
Video Demo
Architecture diagrams
Component diagrams
Final ERD
Final USM
```

**Objetivo:** cerrar el proyecto técnicamente y dejarlo listo para release.

### Semana 16 — FINAL 🚩

```text
Release build
Signed APK
Final testing
Final bug fixing

Final presentation
Demo script
Screenshots

Final architecture diagram
Final component diagram
Final documentation

GitHub cleanup
Milestones
Releases
Tags
README final
Final retrospective
```

#### Demo objetivo

La demostración de cinco minutos deberá centrarse en un flujo coherente:

```text
Login
  ↓
Dashboard
  ↓
Create / View Subject
  ↓
Calendar
  ↓
Create Event
  ↓
Select Physical Location
  ↓
Create Task
  ↓
Register Grade
  ↓
Grade Simulator
  ↓
AI Assistant
  ↓
Natural-language Event Creation
  ↓
Academic Planning Recommendation
```

## 17. GitHub

### 17.1 Branch Strategy

Para un proyecto individual se utilizará una estrategia sencilla:

```text
main
develop
feature/*
fix/*
```

### main

Código estable y entregable.

### develop

Integración de funcionalidades.

### feature/*

Ejemplos:

```text
feature/auth
feature/calendar
feature/tasks
feature/academic
feature/location
feature/ai
feature/ktor
```

### fix/*

Correcciones específicas.

### 17.2 Milestones

```text
M0 — Discovery
M1 — Anteproyecto
M2 — MVP Core
M3 — Avance
M4 — AI
M5 — Ktor
M6 — Security & Quality
M7 — Final Release
```

### 17.3 Releases

```text
v0.1.0 — Anteproyecto
v0.5.0 — Avance
v0.8.0 — Feature Complete
v0.9.0 — Release Candidate
v1.0.0 — Final
```

## 18. Documentación

La documentación deberá mantenerse progresivamente durante el desarrollo y no escribirse únicamente al final.

### Root

```text
README.md
SPEC.md
CONTRIBUTING.md
LICENSE
planning.md
```

### docs/

```text
docs/
│
├── architecture/
│   ├── ARCHITECTURE.md
│   ├── COMPONENTS.md
│   └── diagrams/
│
├── database/
│   ├── DATABASE.md
│   └── diagrams/
│
├── security/
│   └── SECURITY.md
│
├── api/
│   └── API.md
│
├── ui/
│   └── DESIGNSYSTEM.md
│
├── deployment/
│   └── DEPLOY.md
│
└── research/
    └── REFERENCES.md
```

## 19. Testing Strategy

### 19.1 Unit Tests

Prioridad:

```text
Grade calculations
Weighted average
Required grade simulator
Domain rules
UseCases
Mappers
Validators
```

### 19.2 Repository Tests

```text
Local datasource
Remote datasource
Repository implementations
Synchronization
```

### 19.3 ViewModel Tests

```text
Loading
Success
Error
User actions
State transitions
```

### 19.4 UI Tests

Prioridad:

```text
Login
Dashboard
Create Event
Create Task
Academic calculation
AI action confirmation
```

### 19.5 Integration Tests

Como mínimo:

```text
Authentication
Persistence
Critical end-to-end flow
```

## 20. CI/CD

GitHub Actions automatizará progresivamente:

```text
Push
  ↓
Checkout
  ↓
Build
  ↓
Lint
  ↓
Unit Tests
  ↓
UI / Instrumentation Tests
  ↓
Artifact
```

Los secrets no deberán almacenarse en el repositorio.

## 21. Docker

Docker se utilizará principalmente para el backend Ktor.

```text
Docker
   ↓
Ktor Server
```

Objetivos:

* Reproducibilidad.
* Entorno consistente.
* Build del backend.
* Preparación para deployment.

No se dockerizará Android Studio ni la aplicación Android.

## 22. Stack

| Área            | Tecnología                                           |
|-----------------|------------------------------------------------------|
| Language        | Kotlin                                               |
| Android         | Android SDK                                          |
| UI              | Jetpack Compose                                      |
| Design          | Material 3                                           |
| Architecture    | Clean Architecture                                   |
| Presentation    | MVVM                                                 |
| DI              | Hilt                                                 |
| Async           | Kotlin Coroutines                                    |
| Reactive State  | Flow / StateFlow                                     |
| Local DB        | Room                                                 |
| Cloud           | Firebase Firestore                                   |
| Authentication  | Firebase Authentication                              |
| OAuth           | Google                                               |
| Notifications   | Firebase Cloud Messaging                             |
| AI              | Gemini / Firebase AI Logic                           |
| Maps            | Google Maps Platform                                 |
| Backend         | Ktor                                                 |
| Serialization   | Kotlinx Serialization                                |
| Security        | Firebase Rules + Android Keystore + Token Validation |
| CI/CD           | GitHub Actions                                       |
| Containers      | Docker                                               |
| Testing         | JUnit + MockK + Compose UI Testing                   |
| Version Control | Git + GitHub                                         |
| Design          | Figma                                                |
| Diagrams        | LucidChart                                           |
| USM             | Excalidraw                                           |
| Documentation   | Markdown                                             |

## 23. AI-Assisted Development Strategy

La IA será utilizada como herramienta de productividad, manteniendo las decisiones arquitectónicas y técnicas bajo control del desarrollador.

### 23.1 Herramientas

```text
Android Studio + Gemini
OpenCode Go
ChatGPT
```

### 23.2 Android Studio + Gemini

Usos principales:

* Autocomplete.
* Compose.
* Refactoring.
* Explicación de errores.
* Debugging.
* Generación de código repetitivo.
* Tests.
* Documentación de código.

### 23.3 OpenCode Go

Usos principales:

* Implementación de features.
* Refactoring multiarchivo.
* Generación de tests.
* Revisión de arquitectura.
* Documentación.
* Debugging.
* Análisis de código.

### 23.4 ChatGPT

Usos principales:

* Análisis de requisitos.
* Diseño arquitectónico.
* Diseño de UX.
* Modelado.
* UML.
* Documentación.
* Revisión técnica.
* Investigación.
* Planificación.
* Preparación de presentación.

### 23.5 Principio de desarrollo asistido por IA

```text
IA genera
    ↓
Developer revisa
    ↓
Comprende
    ↓
Test
    ↓
Refactor
    ↓
Commit
```

No se incorporará código generado automáticamente sin comprender:

* Qué hace.
* Por qué existe.
* Qué dependencias introduce.
* Qué riesgos presenta.
* Cómo se prueba.
* Cómo encaja en la arquitectura.

## 24. Estrategia de alcance

La prioridad general será:

```text
1. Funcionalidad
2. Arquitectura
3. Persistencia
4. Seguridad
5. Testing
6. UX
7. Polish
8. Stretch Goals
```

Si el tiempo disponible disminuye inesperadamente, se conservarán:

```text
Authentication
Subjects
Events
Tasks
Calendar
Academic calculations
Grade simulator
Firebase
Location-based events
Basic AI integration
Basic Ktor API
```

Se eliminarán primero:

```text
Advanced AI
Advanced voice features
Advanced analytics
Google Calendar integration
Widgets
Advanced offline synchronization
```

No se sacrificará una funcionalidad fundamental para implementar un Stretch Goal.

## 25. Definition of Done

Una funcionalidad será considerada terminada cuando:

```text
✓ Implemented
✓ UI complete
✓ Loading state
✓ Empty state
✓ Error state
✓ Validation
✓ Persistence if applicable
✓ Unit tests where applicable
✓ Integrated with architecture
✓ Documentation updated
✓ Manually tested
✓ No known critical bugs
```

## 26. Criterio de éxito

UniHub será considerado exitoso si al finalizar permite a un estudiante:

```text
Autenticarse
    ↓
Crear sus materias
    ↓
Registrar clases y eventos
    ↓
Asociarlos a ubicaciones o modalidad remota
    ↓
Gestionar tareas
    ↓
Registrar calificaciones
    ↓
Consultar rendimiento
    ↓
Simular notas necesarias
    ↓
Consultar calendario
    ↓
Utilizar IA para gestionar información
    ↓
Recibir recomendaciones académicas
    ↓
Mantener información sincronizada
```

Todo esto deberá funcionar dentro de una aplicación Android coherente, segura, testeable y respaldada por una arquitectura profesional.

## 27. Resultado esperado

Al finalizar las 16 semanas, UniHub deberá ser un **MVP académico completamente funcional**, no solamente un prototipo visual.

El proyecto deberá demostrar de manera integrada:

```text
Mobile Development
        +
Jetpack Compose
        +
Clean Architecture
        +
Firebase
        +
OAuth
        +
AI
        +
Geolocation
        +
Ktor
        +
Security
        +
Coroutines
        +
Local Persistence
        +
Cloud Connectivity
        +
Docker
        +
GitHub Actions
        +
Testing
        +
UI/UX
```

La complejidad estará deliberadamente controlada para que el proyecto sea viable para una sola persona durante aproximadamente cuatro meses, reservando las últimas semanas principalmente para integración, estabilización, testing, documentación y preparación de la entrega final.
