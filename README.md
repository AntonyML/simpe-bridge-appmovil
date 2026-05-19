# SINPE Bridge - Android App

Kotlin MVVM app para capturar y procesar SMS SINPE.

## Quick Start

### In Android Studio

1. Open `simpe-bridge-appmovil`
2. Build APK:
```bash
./gradlew assembleDebug
```

3. Initialize HTTP client in `MainActivity.kt`:
```kotlin
val httpConfig = HttpClientConfig(
    baseUrl = "https://api.tonyml.com",
    apiKey = "test-api-key-12345",
    timeoutSeconds = 30,
    enableLogging = BuildConfig.DEBUG,
)

SinpeBridgeHttpManager.initialize(this, httpConfig)
```

## Features

- ✅ SMS capture (BroadcastReceiver)
- ✅ HMAC SHA-256 signing
- ✅ Device anonymization
- ✅ Automatic retries
- ✅ Multipart image uploads
- ✅ Request tracing
- ✅ Offline queue

## Architecture

- **MVVM**: ViewModel + Repository pattern
- **Room**: Local database (offline-first)
- **Coroutines**: Async/await
- **Compose**: Modern UI (Material 3)

## Auto-Versioning

Each build generates unique `versionCode` and `versionName` based on timestamp (YYMMDDHHMM).

## ⌨️ Desarrollo sin Android Studio

Para máxima flexibilidad, se han incluido scripts de automatización en la carpeta `/scripts`:

- `build-deploy.ps1`: Compila, instala y lanza la app.
- `view-logs.ps1`: Monitoreo en tiempo real (Logcat filtrado).
- `extract-apk.ps1`: Genera un APK distribuible.
- `clean.ps1`: Limpieza profunda del entorno Gradle.

> [!TIP]
> Consulta el archivo [INSTRUCCIONES.txt](./scripts/INSTRUCCIONES.txt) dentro de la carpeta `/scripts` para más detalles sobre el uso de la CLI.

## 🔐 Seguridad

Este proyecto implementa capas de seguridad críticas para el manejo de información sensible:
1. **Content Hashing**: Idempotencia garantizada en la base de datos local.
2. **Device Anonymization**: Identificación única del dispositivo mediante hash de hardware.
3. **Traceability IDs**: Cada evento es rastreable mediante UUIDs únicos.

---
© 2026 SIMPE Bridge Team - Proyecto Universitario / Profesional
