# Fix Gradle Plugin Serialization Error and Improve Code Robustness

The Kotlin serialization plugin is used in the `app` module but is not declared in the root `build.gradle.kts` with a version. Additionally, I've identified minor improvements for several core files to ensure they align with the project's native-first, reactive goals.

## Proposed Changes

### Build Configuration

#### [build.gradle.kts](file:///C:/DEV/simpe-bridge-appmovil/build.gradle.kts)

- Add the `org.jetbrains.kotlin.plugin.serialization` plugin with version "2.0.21".

```diff
 plugins {
     id("com.android.application") version "8.13.2" apply false
     id("org.jetbrains.kotlin.android") version "2.0.21" apply false
     id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
+    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21" apply false
     id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false
 }
```

#### [app/build.gradle.kts](file:///C:/DEV/simpe-bridge-appmovil/app/build.gradle.kts)

- Ensure the plugin is applied without a version (it will inherit from the root).
- Ensure `kotlinx-serialization-json` dependency is present.

---

### Data Layer

#### [SupabaseClient.kt](file:///C:/DEV/simpe-bridge-appmovil/app/src/main/java/com/simpe/bridge/appmovil/data/auth/SupabaseClient.kt)

- I will keep this as is unless I find a reason to change the URL/Key, as it seems to be the source of truth for Supabase connection.

#### [SupabaseMessageService.kt](file:///C:/DEV/simpe-bridge-appmovil/app/src/main/java/com/simpe/bridge/appmovil/data/remote/SupabaseMessageService.kt)

- Verify that `@Serializable` and `@SerialName` are correctly used (they are).

---

### SMS Handling

#### [SmsReceiver.kt](file:///C:/DEV/simpe-bridge-appmovil/app/src/main/java/com/simpe/bridge/appmovil/sms/SmsReceiver.kt)

- Improve error logging and ensure `goAsync()` is handled correctly to prevent background execution issues.
- Add more descriptive logs for the SINPE detection phase.

---

### UI and Auth

#### [LoginScreen.kt](file:///C:/DEV/simpe-bridge-appmovil/app/src/main/java/com/simpe/bridge/appmovil/ui/screens/login/LoginScreen.kt)

- Minor UI tweaks for consistency with Material 3 if needed.

#### [SessionManager.kt](file:///C:/DEV/simpe-bridge-appmovil/app/src/main/java/com/simpe/bridge/appmovil/data/auth/SessionManager.kt)

- Ensure `EncryptedSharedPreferences` is correctly initialized.

## Verification Plan

### Automated Tests
- Run Gradle sync: `./gradlew help`
- Build the app: `./gradlew :app:assembleDebug`

### Manual Verification
- Deploy to emulator/device: `adb install app/build/outputs/apk/debug/app-debug.apk`
- Monitor logs: `adb logcat | grep SmsReceiver`
