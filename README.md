# SIMPE Bridge - Android Collector

![Versión](https://img.shields.io/badge/version-auto--generated-blue)
![Platform](https://img.shields.io/badge/platform-Android-green)
![Kotlin](https://img.shields.io/badge/language-Kotlin-purple)

**SIMPE Bridge** es una solución de grado industrial para la captura, persistencia y trazabilidad de mensajes SMS en dispositivos Android. Diseñada con un enfoque en sistemas críticos, seguridad y auditoría distribuida.

## 🚀 Características Principales

- **Captura de SMS en Tiempo Real**: Listener nativo optimizado para funcionar en segundo plano.
- **Pipeline de Procesamiento Robusto**:
    - **Validación**: Filtros de integridad y reglas de negocio.
    - **Idempotencia**: Prevención de duplicados mediante hashing SHA-256.
    - **Seguridad**: Firmado de payloads con HMAC SHA-256.
- **Trazabilidad Total**: Generación de Message IDs y Correlation IDs únicos para auditoría end-to-end.
- **UI Moderna (Jetpack Compose)**: Dashboard reactivo, modo oscuro dinámico y visualización detallada de metadatos.
- **Arquitectura Limpia**: MVVM + Repository Pattern + Use Cases.

## 🛠️ Stack Tecnológico

- **Lenguaje**: Kotlin 1.9+
- **UI**: Jetpack Compose (Material 3)
- **Persistencia**: Room Database (Offline-first)
- **Asincronía**: Coroutines & Flow
- **Serialización**: Gson (API Ready)

## 📦 Automatización y DevOps

El proyecto cuenta con un sistema de **Auto-Versioning** determinista. Cada vez que el código se compila o se despliega al móvil, el `versionCode` y `versionName` se actualizan automáticamente basados en el timestamp (YYMMDDHHMM), asegurando que cada build sea única y rastreable.

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
