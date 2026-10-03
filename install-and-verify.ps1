# Script robusto para instalar y verificar la app

Write-Host "=== PASO 1: Verificando conexión del dispositivo ===" -ForegroundColor Cyan
$adbPath = "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk\platform-tools\adb.exe"
if (-not (Test-Path $adbPath)) {
    $adbPath = "adb"
}

# Verificar dispositivos conectados
$devices = & $adbPath devices
Write-Host $devices

if ($devices -match "device$") {
    Write-Host "Dispositivo conectado correctamente" -ForegroundColor Green
} else {
    Write-Host "ERROR: No hay dispositivos conectados o el dispositivo no está autorizado" -ForegroundColor Red
    Write-Host "Por favor:" -ForegroundColor Yellow
    Write-Host "1. Conecta tu dispositivo por USB" -ForegroundColor Yellow
    Write-Host "2. Autoriza la depuración USB en el dispositivo" -ForegroundColor Yellow
    Write-Host "3. Ejecuta este script nuevamente" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "=== PASO 2: Desinstalando app existente ===" -ForegroundColor Cyan
& $adbPath uninstall com.unihub.app
Start-Sleep -Seconds 2

Write-Host ""
Write-Host "=== PASO 3: Verificando APK ===" -ForegroundColor Cyan
$apkPath = Join-Path $PSScriptRoot "app\build\outputs\apk\debug\app-debug.apk"
if (-not (Test-Path $apkPath)) {
    Write-Host "APK no encontrado. Compilando..." -ForegroundColor Yellow
    Set-Location $PSScriptRoot
    .\gradlew.bat assembleDebug --no-daemon
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Build falló" -ForegroundColor Red
        exit 1
    }
}

Write-Host "APK encontrado: $apkPath" -ForegroundColor Green
$apkSize = (Get-Item $apkPath).Length / 1MB
Write-Host "Tamaño del APK: $([math]::Round($apkSize, 2)) MB" -ForegroundColor Gray

Write-Host ""
Write-Host "=== PASO 4: Instalando APK ===" -ForegroundColor Cyan
& $adbPath install -r $apkPath

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Instalación falló" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "=== PASO 5: Verificando instalación ===" -ForegroundColor Cyan
$installedApps = & $adbPath shell pm list packages com.unihub.app
if ($installedApps -match "com.unihub.app") {
    Write-Host "App instalada correctamente" -ForegroundColor Green
} else {
    Write-Host "ERROR: App no está instalada" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "=== PASO 6: Iniciando la app ===" -ForegroundColor Cyan
& $adbPath shell am start -n com.unihub.app/.presentation.MainActivity

Write-Host ""
Write-Host "=== INSTALACIÓN COMPLETADA ===" -ForegroundColor Green
Write-Host ""
Write-Host "Ahora en Android Studio:" -ForegroundColor Cyan
Write-Host "1. Abre la ventana de Logcat" -ForegroundColor White
Write-Host "2. En el filtro, escribe: PLACES_DEBUG" -ForegroundColor White
Write-Host "3. Deberías ver logs como:" -ForegroundColor White
Write-Host "   === UniHubApplication.onCreate() STARTED ===" -ForegroundColor Gray
Write-Host "   === initializePlaces() CALLED ===" -ForegroundColor Gray
Write-Host ""
Write-Host "Si no ves los logs, intenta:" -ForegroundColor Yellow
Write-Host "- Cambiar el filtro a 'No Filters'" -ForegroundColor Yellow
Write-Host "- Luego aplicar el filtro 'PLACES_DEBUG'" -ForegroundColor Yellow
Write-Host "- O buscar en todo el log: PLACES_DEBUG" -ForegroundColor Yellow
