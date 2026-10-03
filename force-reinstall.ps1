# Script simple para reinstalación forzada
Write-Host "=== PASO 1: Verificando dispositivo ===" -ForegroundColor Cyan
$adbPath = "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk\platform-tools\adb.exe"
if (-not (Test-Path $adbPath)) { $adbPath = "adb" }

$devices = & $adbPath devices
Write-Host $devices

Write-Host ""
Write-Host "=== PASO 2: Desinstalando app ===" -ForegroundColor Cyan
& $adbPath uninstall com.unihub.app
Start-Sleep -Seconds 3

Write-Host ""
Write-Host "=== PASO 3: Verificando que se desinstaló ===" -ForegroundColor Cyan
$check = & $adbPath shell pm list packages com.unihub.app
if ($check -match "com.unihub.app") {
    Write-Host "ADVERTENCIA: La app aún está instalada" -ForegroundColor Yellow
    Write-Host "Intentando desinstalar para todos los usuarios..." -ForegroundColor Yellow
    & $adbPath shell pm uninstall -k com.unihub.app
    Start-Sleep -Seconds 2
} else {
    Write-Host "App desinstalada correctamente" -ForegroundColor Green
}

Write-Host ""
Write-Host "=== PASO 4: Compilando APK nuevo ===" -ForegroundColor Cyan
Set-Location $PSScriptRoot
.\gradlew.bat clean
.\gradlew.bat assembleDebug

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Build falló" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "=== PASO 5: Instalando APK ===" -ForegroundColor Cyan
$apkPath = Join-Path $PSScriptRoot "app\build\outputs\apk\debug\app-debug.apk"
& $adbPath install -r $apkPath

Write-Host ""
Write-Host "=== PASO 6: Iniciando app ===" -ForegroundColor Cyan
& $adbPath shell am start -n com.unihub.app/.presentation.MainActivity

Write-Host ""
Write-Host "=== COMPLETADO ===" -ForegroundColor Green
Write-Host ""
Write-Host "AHORA EN ANDROID STUDIO:" -ForegroundColor Yellow
Write-Host "1. Abre Logcat" -ForegroundColor White
Write-Host "2. En el filtro escribe EXACTAMENTE: PLACES_DEBUG" -ForegroundColor White
Write-Host "3. Deberías ver logs como:" -ForegroundColor White
Write-Host "   === UniHubApplication.onCreate() STARTED ===" -ForegroundColor Gray
Write-Host ""
Write-Host "Si NO ves los logs, el APK no se actualizó." -ForegroundColor Red
