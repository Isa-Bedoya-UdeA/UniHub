# Script completo para reinstalación limpia

Write-Host "=== PASO 1: Deteniendo procesos ===" -ForegroundColor Cyan
Get-Process -Name "adb" -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Get-Process -Name "java" -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Get-Process -Name "gradle*" -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

Write-Host "=== PASO 2: Desinstalando app del dispositivo ===" -ForegroundColor Cyan
Write-Host "Conecta tu dispositivo y presiona Enter cuando esté listo..."
Read-Host

# Iniciar ADB
$adbPath = "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk\platform-tools\adb.exe"
if (-not (Test-Path $adbPath)) {
    $adbPath = "adb"
}

Write-Host "Desinstalando com.unihub.app..."
& $adbPath uninstall com.unihub.app
Start-Sleep -Seconds 2

Write-Host "=== PASO 3: Limpiando carpetas build ===" -ForegroundColor Cyan
$buildPaths = @("app\build", "server\build", "build")
foreach ($path in $buildPaths) {
    $fullPath = Join-Path $PSScriptRoot $path
    if (Test-Path $fullPath) {
        try {
            Remove-Item -Path $fullPath -Recurse -Force -ErrorAction Stop
            Write-Host "Eliminado: $path" -ForegroundColor Green
        } catch {
            Write-Host "No se pudo eliminar: $path" -ForegroundColor Yellow
        }
    }
}

Write-Host "=== PASO 4: Compilando APK ===" -ForegroundColor Cyan
Set-Location $PSScriptRoot
.\gradlew.bat assembleDebug --no-daemon

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Build falló" -ForegroundColor Red
    exit 1
}

Write-Host "=== PASO 5: Instalando APK ===" -ForegroundColor Cyan
$apkPath = Join-Path $PSScriptRoot "app\build\outputs\apk\debug\app-debug.apk"
if (Test-Path $apkPath) {
    Write-Host "Instalando $apkPath..."
    & $adbPath install $apkPath
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "=== INSTALACIÓN COMPLETADA ===" -ForegroundColor Green
        Write-Host ""
        Write-Host "Ahora puedes:" -ForegroundColor Cyan
        Write-Host "1. Abrir la app en tu dispositivo" -ForegroundColor White
        Write-Host "2. Filtrar logcat por: PLACES_DEBUG | TAG_DEBUG | AI_DEBUG" -ForegroundColor White
        Write-Host "3. Probar las funcionalidades" -ForegroundColor White
    } else {
        Write-Host "ERROR: Instalación falló" -ForegroundColor Red
    }
} else {
    Write-Host "ERROR: APK no encontrado en $apkPath" -ForegroundColor Red
}
