# Script para extraer el APK generado a la carpeta de scripts
Write-Host "--- Generando APK para distribución ---" -ForegroundColor Cyan
./gradlew assembleDebug

if ($LASTEXITCODE -eq 0) {
    Copy-Item -Path app/build/outputs/apk/debug/app-debug.apk -Destination ./simpe-bridge-debug.apk
    Write-Host "--- APK extraído como 'simpe-bridge-debug.apk' en esta carpeta. ---" -ForegroundColor Green
} else {
    Write-Host "--- ERROR: Falló la construcción. ---" -ForegroundColor Red
}
