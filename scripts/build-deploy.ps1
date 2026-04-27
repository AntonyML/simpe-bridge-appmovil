# Script para construir e instalar la app automáticamente
Write-Host "--- Construyendo e Instalando SIMPE Bridge ---" -ForegroundColor Cyan

# 1. Limpiar y Construir
./gradlew assembleDebug

if ($LASTEXITCODE -eq 0) {
    Write-Host "--- Construcción Exitosa. Instalando en dispositivo... ---" -ForegroundColor Green

    # 2. Instalar el APK
    adb install -r app/build/outputs/apk/debug/app-debug.apk

    if ($LASTEXITCODE -eq 0) {
        Write-Host "--- Instalación Completa. Iniciando App... ---" -ForegroundColor Green
        # 3. Iniciar la actividad principal
        adb shell am start -n com.simpe.bridge.appmovil/com.simpe.bridge.appmovil.MainActivity
    } else {
        Write-Host "--- ERROR: Falló la instalación. ¿Está el celular conectado? ---" -ForegroundColor Red
    }
} else {
    Write-Host "--- ERROR: Falló la construcción de Gradle. ---" -ForegroundColor Red
}
