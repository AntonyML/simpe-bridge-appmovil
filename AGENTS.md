# AGENTS.md

## Project Scope

* Build and maintain a **100% native Android app in Kotlin**.
* Strictly **no WebView, no hybrid frameworks, no cross-platform runtimes**.
* Core goal:

  * Capture incoming SMS
  * Persist locally (offline-first)
  * Display in a modern reactive UI
  * Provide debugging tools (copy + JSON inspection)

---

## Mandatory Tech Stack

* Kotlin
* Jetpack Compose (Material 3)
* MVVM + Clean Architecture
* Room (local persistence)
* Coroutines + Flow

---

## Required Project Structure

Maintain this structure strictly:

app/src/main/java/.../

* ui/

  * screens/

    * messages/
    * qr/
    * settings/
  * components/
  * navigation/
  * theme/
* data/

  * local/
  * repository/
* domain/

  * usecases/
* sms/

  * SmsReceiver.kt
* MainActivity.kt

---

## Architecture Rules (STRICT)

* UI (Compose) must be **stateless** and driven by ViewModel state.
* ViewModels:

  * Expose UI state via `StateFlow`
  * Do NOT contain Android UI logic
* Domain layer:

  * Contains business rules only
  * No Android dependencies
* Data layer:

  * Handles Room + repositories
* SMS parsing logic:

  * Must be isolated in `sms/`
  * Must NOT leak into UI or ViewModel

---

## Core Feature Contract (NON-NEGOTIABLE)

### SMS Handling

* Parse incoming SMS:

  * sender
  * body
  * timestamp
* Must handle PDUs correctly
* Must work in background

### Persistence

* Every message MUST be saved in Room
* No in-memory only flows allowed
* App must work offline fully

### Reactive UI

* Messages list must update automatically via Flow
* No manual refresh patterns

---

## Database Contract

### Entity: Message

* id (auto-generated primary key)
* sender
* content
* timestamp

### DAO:

* insert(message)
* getAll(): Flow<List<Message>>

---

## UI/UX Requirements (HIGH PRIORITY)

### General Design

* Use Material 3
* Light theme with strong contrast
* Rounded cards
* Clean spacing
* Modern (2025–2026 style)

---

### Navigation (STRICT)

Bottom Navigation with ONLY 3 tabs:

1. Messages (MAIN)
2. QR (disabled, visual only)
3. Settings

NO additional tabs allowed.

---

### Messages Screen (CORE SCREEN)

Must include:

#### 1. Top Bar

* App name: SIMPE Bridge
* Status indicator (e.g. "Solo Android")
* Message counter
* Primary action button (Test SMS)

#### 2. Dashboard Section (inside Messages screen)

* Total SMS
* System status
* Spam detected (placeholder allowed)
* Risk score (placeholder allowed)

#### 3. Message List

* LazyColumn
* Each item as a modern card
* Show:

  * sender
  * preview text
  * timestamp

#### 4. Empty State

* Icon + text
* Clear CTA guidance

---

## Message Interaction (CRITICAL FEATURE)

Each message MUST support:

### Open Detail

* Modal or new screen
* Show full message content
* Show metadata

### Actions

* Copy message text
* Copy FULL JSON (for debugging)

### JSON format MUST include:

* sender
* body
* timestamp
* id (if available)

---

## QR Screen (PLACEHOLDER)

* Non-functional
* Visually disabled (greyed out)
* Label: "Próximamente"
* Simulated scanner UI only

---

## Settings Screen

* Toggle SMS listener ON/OFF
* Permission status display
* Basic app info

---

## Permissions (STRICT)

Must implement:

* RECEIVE_SMS
* READ_SMS

Optional if needed:

* POST_NOTIFICATIONS
* RECEIVE_BOOT_COMPLETED
* FOREGROUND_SERVICE

### Runtime Behavior

* Request permissions properly
* Handle denied state
* UI must reflect permission status

---

## Code Quality Rules

* No business logic in Composables
* No Android framework code in domain
* Prefer:

  * immutable state
  * data classes
  * sealed UI states (Loading, Empty, Success)
* Use:

  * collectAsState()
  * remember()

---

## Anti-Patterns (FORBIDDEN)

* ❌ XML layouts
* ❌ Global mutable state
* ❌ Logic inside Composables
* ❌ Skipping Room persistence
* ❌ Tight coupling between layers
* ❌ Multiple sources of truth

---

## Validation Checklist (MUST PASS)

* App compiles with no errors
* SMS is received and parsed correctly
* Messages persist in Room
* UI updates reactively
* Permissions work correctly
* Message detail + copy + JSON actions work

---

## External References (CONCEPTUAL ONLY)

* SmsForwarder project → permissions & receiver patterns
* Legacy SMS parsing → parsing ideas only
* Old frontend → UI inspiration only

NO direct code copying.

---

## Documentation

* See README.md for setup and usage
* Keep this file concise and authoritative
