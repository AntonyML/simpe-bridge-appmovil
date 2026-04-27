# AGENTS.md

## Project Scope
- Build and maintain a 100% native Android app in Kotlin.
- Do not use Tauri, Next.js runtime, WebView, or hybrid wrappers.
- Primary goal: capture incoming SMS, persist locally, and display reactively.

## Required App Structure
- Keep this module structure in `app/src/main/java/...`:
  - `ui/screens`
  - `ui/components`
  - `data/local`
  - `data/repository`
  - `domain/usecases`
  - `sms/SmsReceiver.kt`
  - `MainActivity.kt`

## Architecture Rules
- Enforce Clean Architecture boundaries:
  - UI -> ViewModel
  - Domain -> UseCases
  - Data -> Repository + Room
  - SMS receiver isolated in `sms` package
- Do not put Room, Android framework, or parsing logic in composables.
- Keep business rules in domain use cases.

## Core Feature Contract
- Incoming SMS handling must extract:
  - sender
  - body
  - timestamp
- Persist every parsed message in Room.
- UI list must observe DB reactively via Flow or LiveData.
- Offline persistence is mandatory.

## Database Contract
- Room entity `Message` fields:
  - `id` (auto-generated primary key)
  - `sender`
  - `content`
  - `timestamp`
- DAO minimum methods:
  - insert
  - getAll (Flow preferred)

## Android Permissions And Manifest
- Required permissions:
  - `android.permission.RECEIVE_SMS`
  - `android.permission.READ_SMS`
- Optional when needed:
  - `android.permission.RECEIVE_BOOT_COMPLETED`
- Implement runtime permission request flow for Android 6+.
- Declare `SmsReceiver` in `AndroidManifest.xml` with proper SMS intent filter.

## Implementation Guidance
- Use Jetpack Compose for UI.
- Keep UI simple, readable, and functional.
- Use Kotlin coroutines and structured concurrency.
- Prefer immutable UI state models.

## Validation Checklist For Agents
- Project compiles with no errors.
- `SmsReceiver` receives and parses SMS PDUs correctly.
- Parsed messages are inserted into Room successfully.
- Main screen renders persisted messages and updates reactively.
- Permissions and receiver declarations are valid in manifest.

## External Reference Inputs
- Reuse ideas (not code copying) from:
  - `E:\Dev\Proyectos_Universidad\SmsForwarder-main\SmsForwarder-main` for permissions and receiver practices.
  - legacy `src-tauri/plugins/sms-listener` logic only as conceptual parsing reference.
  - old `frontend/` only for UI flow inspiration, never for web runtime usage.

## Documentation
- Current project documentation: [README.md](README.md)
- Keep AGENTS.md concise. Link to docs instead of duplicating long explanations.