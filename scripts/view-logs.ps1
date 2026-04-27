# Script para ver los logs en tiempo real filtrados por la app
Write-Host "--- Monitoreando Logs de SIMPE Bridge ---" -ForegroundColor Cyan
adb logcat *:S com.simpe.bridge.appmovil:V
