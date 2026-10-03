# UniHub — Documento de Avance Semana 9

> **Proyecto:** UniHub — Aplicación móvil de organización académica universitaria  
> **Semana:** 9 (Entrega de Avance / Hito 50%)  
> **Estado:** 100% de los requisitos del avance completados

---

## 1. Resumen Ejecutivo

UniHub ha alcanzado el hito de la **Semana 9 (Avance)** cumpliendo la totalidad de los entregables funcionales, arquitecturales y técnicos planificados:

1. **Persistencia Local (100%):** Base de datos relacional SQLite con Room v6, 15 entidades, DAOs reactivos con Kotlin Coroutines y Flow, migraciones y convertidores de tipos.
2. **Persistencia en la Nube (100%):** Cloud Firestore con arquitectura por usuario (`users/{userId}`), datasources remotos para todas las entidades, reglas de seguridad (`firestore.rules`) y sincronización bidireccional periódica con `WorkManager` (`SyncWorker`) y reactiva al iniciar sesión.
3. **Integración Funcional de IA:** Servidor backend en **Ktor** con cadena de fallback de proveedores (OpenRouter como principal con `gpt-4o-mini`, Groq como respaldo con `llama-3.3-70b-versatile`), además de un fallback local inteligente (`LocalAiFallbackUseCase`) en el cliente Android que analiza la agenda y tareas locales en modo offline.
4. **Pruebas Unitarias Core:** Suite de pruebas unitarias que validan mapeadores de entidades, lógica de negocio académica (simulador de notas, cálculo condicional de créditos por materia terminada o período finalizado), viewmodels y servicios de IA.
5. **UI / UX y Design System:** 100% de pantallas implementadas en Jetpack Compose, soporte de modo oscuro/claro con carga sincrónica inmediata, navegación completa y componentes unificados.

---

## 2. Detalle de Entregables de la Semana 9

### 2.1 Persistencia Local (100%)
* **Tecnología:** Room Database v6 (`AppDatabase.kt`).
* **Entidades (15):**
  - Autenticación y Usuario: `UserEntity`.
  - Académico: `StudyEntity`, `AcademicPeriodEntity`, `SubjectEntity` (con bandera `is_completed`), `GradeEntity`.
  - Agenda y Eventos: `EventEntity`, `EventReminderEntity`, `RecurrenceRuleEntity`, `RecurrenceDayEntity`, `EventTagEntity`.
  - Tareas y Etiquetas: `TaskEntity`, `TaskTagEntity`, `TagEntity`.
  - Ubicación y Ajustes: `LocationEntity`, `UserPreferencesEntity`.
* **Características:**
  - DAOs reactivos que emiten `Flow` para actualización automática de la UI.
  - Métodos de eliminación y consulta en lote por `subjectId` para soporte de repetición limpia de materias.
  - `Converters.kt` para almacenamiento de enums, fechas (`LocalDateTime`, `Instant`) y tipos complejos.

### 2.2 Persistencia Firebase (100%)
* **Tecnología:** Cloud Firestore y Firebase Authentication.
* **Colecciones estructuradas:**
  ```text
  users/{userId}/
      profile/{profileId}
      studies/{studyId}
      academicPeriods/{academicPeriodId}
      subjects/{subjectId}
      events/{eventId} (subcolecciones: reminders, eventTags)
      recurrenceRules/{recurrenceRuleId} (subcolección: days)
      locations/{locationId}
      tasks/{taskId} (subcolección: taskTags)
      grades/{gradeId}
      tags/{tagId}
      preferences/settings
  ```
* **Sincronización:**
  - `SyncWorker`: Trabajo periódico en segundo plano administrado por `WorkManager` (cada 15 min con restricciones de red).
  - `syncExistingUser`: Descarga automática al iniciar sesión para poblar la base local.
  - Sincronización de escritura inmediata en cada repositorio (Room primero + Firestore asíncrono con manejo de excepciones).
* **Seguridad:** Reglas en `firestore.rules` que validan `request.auth.uid == userId` en todas las subcolecciones.

### 2.3 Inteligencia Artificial (Integración Funcional)
* **Microservicio Backend Ktor (`:server`):**
  - Puerto 8080 (Dockerizado y ejecutable directamente con `./gradlew :server:runDirect`).
  - Cadena de proveedores de IA con fallback automático:
    1. **OpenRouter:** Modelo `openai/gpt-4o-mini` (o configurable a `mistralai`).
    2. **Groq (Fallback):** Modelo `llama-3.3-70b-versatile` con latencia ultra baja.
  - Robustez: Configuración de deserialización JSON con `ignoreUnknownKeys = true`, `isLenient = true`, resolución forzada de IPv4 para evitar cuellos de botella en redes locales y manejo de reintentos con backoff exponencial.
* **Cliente Móvil (`AiChatScreen` & `AiChatViewModel`):**
  - Chat interactivo con renderizado de respuestas en Markdown, chips de sugerencias rápidas e historial de mensajes.
  - Acciones de teclado IME ("Enviar" ejecuta la consulta directamente).
  - **Fallback Offline Inteligente (`LocalAiFallbackUseCase`):** Si el servidor Ktor no está disponible o no hay conexión, un agente heurístico local consulta Room para responder preguntas sobre la agenda de hoy/mañana, materias inscritas, tareas pendientes y cálculo de promedio.

### 2.4 Testing Unitario Core
* **Pruebas implementadas:**
  - `CalculateRequiredGradeUseCaseTest`: Simulación de notas requeridas (ponderaciones, notas faltantes, límites de aprobación).
  - `AcademicSummaryLogicTest`: Regla de negocio de suma de créditos (solo se suman si la materia está marcada como completada `isCompleted` o si ya concluyó la fecha de fin del período académico).
  - `SubjectMapperTest`: Conversión bidireccional entre `Subject`, `SubjectEntity` y `SubjectDto`.
  - `AcademicMapperTest`: Conversiones de períodos académicos, notas y programas de estudio.
  - `TaskMapperTest`: Mapeo de tareas, estados (`PENDING`, `IN_PROGRESS`, `COMPLETED`) y recordatorios.
  - `EventMapperTest`: Mapeo de eventos presenciales y virtuales, reglas de recurrencia y alarmas.
  - `PreferencesMapperTest`: Conversión de preferencias de usuario y tema.
  - `DateUtilsTest`: Parseo y formateo de fechas locales y zonas horarias.
  - `DashboardViewModelTest`: Estado inicial, carga de materias y métricas del dashboard.
  - `AiChatViewModelTest`: Flujo de envío de mensajes, estados de carga y manejo de errores.
  - `AiServiceTest`: Fallback del servidor Ktor entre OpenRouter y Groq.

---

## 3. Correcciones de Usabilidad y Casos Borde Implementados

1. **Creación de tareas contextual vs general:**
   - **Desde Detalle de Materia:** La tarea se enlaza directamente a la materia actual sin mostrar selector ni chips innecesarios.
   - **Desde Pantalla de Tareas:** Se muestra un menú desplegable limpio (`UniHubSelect`) que permite asociar opcionalmente una de las materias existentes o "Ninguna".
2. **Repetición de Materias (`RepeatSubjectUseCase`):**
   - Cuando un estudiante decide repetir una materia perdida, se ejecuta un borrado en cascada tanto en Room como en Cloud Firestore de todas las notas, tareas asociadas (y sus etiquetas) y eventos (junto a sus recordatorios).
   - Se actualiza la materia a `isCompleted = false`.
   - Se evita la reaparición de datos antiguos (evita que `SyncWorker` los vuelva a descargar).
3. **Cálculo de Créditos Aprobados:**
   - Los créditos de una materia no suman al total acumulado del programa de estudio mientras la materia esté en curso, a menos que el usuario la marque como terminada o finalice la fecha límite del semestre.
4. **Navegación en Mi Agenda:**
   - Se añadió el botón **"Hoy"** en la barra superior del calendario para retornar inmediatamente al día actual en las vistas de mes, semana o día.
5. **Teclado en Pantalla y Accesibilidad:**
   - En todos los campos de texto, las acciones de teclado `ImeAction.Next` avanzan el foco al siguiente control y `ImeAction.Send` ejecuta el guardado o envío.
6. **Tema Oscuro Persistente Inmediato:**
   - Lectura síncrona desde `SharedPreferences` al inicio (`MainActivity`), evitando el retraso al abrir la aplicación antes de que responda Firestore.

---

## 4. Flujo End-to-End para Demostración

```text
1. Inicio y Autenticación
   ├── Splash Screen (Restaura sesión y tema)
   └── Login con Google OAuth o credenciales Firebase

2. Dashboard Principal
   ├── Vista general de agenda del día
   ├── Tareas prioritarias pendientes
   └── Resumen de promedio y créditos acumulados

3. Gestión Académica
   ├── Creación de Programa de Estudio y Período Académico
   ├── Creación de Materia con créditos, color y profesor
   ├── Detalle de la Materia:
   │     ├── Agregar notas porcentuales y simular nota requerida
   │     ├── Agregar tarea asociada (selector contextual implícito)
   │     ├── Marcar materia como terminada / en curso (actualiza créditos)
   │     └── Opción "Repetir materia" (limpieza completa de notas/tareas/eventos)

4. Agenda y Calendario
   ├── Vista mensual, semanal y diaria
   ├── Botón "Hoy" para salto rápido a la fecha actual
   └── Creación de eventos con selector de materia y ubicación

5. Asistente Académico con IA
   ├── Consulta en lenguaje natural: "¿Qué clases tengo hoy?"
   ├── Consulta de planificación: "¿Cuáles tareas tengo pendientes para esta semana?"
   └── Funcionamiento transparente con servidor Ktor o fallback local offline
```

---

## 5. Guion de Presentación del Avance (Para el Profesor)

### Diapositiva 1: Portada
- **Título:** UniHub — Avance Semana 9 (MVP 50%)
- **Integrantes:** Isa Bedoya (UdeA)
- **Materia:** Computación Móvil

### Diapositiva 2: Objetivos del Hito Alcanzado
- Persistencia local y en la nube 100% operativa (Offline-first).
- Clean Architecture (Capa de Presentación Compose, Dominio con Use Cases, Datos con Room + Firestore).
- Asistente de IA funcional mediante microservicio Ktor y fallback local.

### Diapositiva 3: Arquitectura de Persistencia
- **Local:** SQLite con Room v6 (15 entidades, DAOs reactivos, migraciones).
- **Remoto:** Cloud Firestore estructurado por usuario, protegido con reglas de seguridad.
- **Sincronización:** `WorkManager` con política de reintentos exponenciales y sync en login.

### Diapositiva 4: Demostración en Vivo (Flujo End-to-End)
1. **Flujo Académico:** Registro de materia, adición de notas y simulación con `GradeSimulator`.
2. **Flujo de Tareas:** Creación de tarea desde la materia (contextual) vs desde el módulo general (con selector `UniHubSelect`).
3. **Flujo de Repetición:** Demostrar cómo "Repetir materia" purga de forma segura notas, tareas y eventos en la base local y en Firestore.
4. **Flujo de Asistente IA:** Preguntar al asistente sobre la agenda y tareas del día y ver la respuesta contextual.

### Diapositiva 5: Conclusiones y Próximos Pasos (Semanas 10–16)
- Integración avanzada de acciones por lenguaje natural con la IA (creación directa de eventos desde el chat).
- Integración de geocercas/recordatorios basados en ubicación geográfica real.
- Pulido de UX, pruebas de integración y empaquetado para Release.
