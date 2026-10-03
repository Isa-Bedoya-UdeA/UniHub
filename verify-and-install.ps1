# Script para verificar e instalar APK
Write-Host "=== VERIFICANDO APK ===" -ForegroundColor Cyan

$apkPath = Join-Path $PSScriptRoot "app\build\outputs\apk\debug\app-debug.apk"

if (Test-Path $apkPath) {
    $apkInfo = Get-Item $apkPath
    Write-Host "APK encontrado: $apkPath" -ForegroundColor Green
    Write-Host "Tamaño: $([math]::Round($apkInfo.Length / 1MB, 2)) MB" -ForegroundColor Gray
    Write-Host "Fecha de creación: $($apkInfo.CreationTime)" -ForegroundColor Gray
    Write-Host "Última modificación: $($apkInfo.LastWriteTime)" -ForegroundColor Gray
    Write-Host ""
    
    $hoursOld = ((Get-Date) - $apkInfo.LastWriteTime).TotalHours
    if ($hoursOld -gt 1) {
        Write-Host "ADVERTENCIA: El APK tiene más de 1 hora ($([math]::Round($hoursOld, 1)) horas)" -ForegroundColor Yellow
        Write-Host "Esto puede significar que no tiene los últimos cambios" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "¿Deseas recompilar? (s/n): " -ForegroundColor Yellow -NoNewline
        $response = Read-Host
        if ($response -eq 's' -or $response -eq 'S') {
            Write-Host ""
            Write-Host "=== RECOMPILANDO ===" -ForegroundColor Cyan
            Set-Location $PSScriptRoot
            .\gradlew.bat clean
            .\gradlew.bat assembleDebug
            
            if ($LASTEXITCODE -ne 0) {
                Write-Host "ERROR: Build falló" -ForegroundColor Red
                exit 1
            }
            
            $apkInfo = Get-Item $apkPath
            Write-Host "Nuevo APK creado: $($apkInfo.LastWriteTime)" -ForegroundColor Green
        }
    } else {
        Write-Host "El APK es reciente (< 1 hora)" -ForegroundColor Green
    }
} else {
    Write-Host "APK no encontrado. Compilando..." -ForegroundColor Yellow
    Set-Location $PSScriptRoot
    .\gradlew.bat assembleDebug
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Build falló" -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "=== INSTALANDO APK ===" -ForegroundColor Cyan
$adbPath = "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk\platform-tools\adb.exe"
if (-not (Test-Path $adbPath)) { $adbPath = "adb" }

# Desinstalar primero
Write-Host "Desinstalando app existente..." -ForegroundColor Gray
& $adbPath uninstall com.unihub.app
Start-Sleep -Seconds 2

# Instalar
Write-Host "Instalando nuevo APK..." -ForegroundColor Gray
& $adbPath install $apkPath

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Instalación falló" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "=== VERIFICANDO LOGS ===" -ForegroundColor Cyan
Write-Host "Iniciando app y capturando logs..." -ForegroundColor Gray
Write-Host ""

# Iniciar app y capturar logs
& $adbPath shell am start -n com.unihub.app/.presentation.MainActivity

Write-Host ""
Write-Host "Capturando logs por 5 segundos..." -ForegroundColor Yellow
Write-Host "Busca líneas con PLACES_DEBUG" -ForegroundColor Yellow
Write-Host ""

# Capturar logs por 5 segundos
$startTime = Get-Date
while (((Get-Date) - $startTime).TotalSeconds -lt 5) {
    $logOutput = & $adbPath logcat -d -t 100 | Select-String "PLACES_DEBUG"
    if ($logOutput) {
        Write-Host "¡LOGS ENCONTRADOS!" -ForegroundColor Green
        $logOutput | ForEach-Object { Write-Host $_.Line -ForegroundColor Cyan }
        break
    }
    Start-Sleep -Milliseconds 500
}

if (-not $logOutput) {
    Write-Host ""
    Write-Host "NO SE ENCONTRARON LOGS DE PLACES_DEBUG" -ForegroundColor Red
    Write-Host ""
    Write-Host "Esto puede significar:" -ForegroundColor Yellow
    Write-Host "1. El APK no se actualizó correctamente" -ForegroundColor Yellow
    Write-Host "2. Los logs se están filtrando" -ForegroundColor Yellow
    Write-Host "3. Hay un problema con el código" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Intenta ejecutar: adb logcat | Select-String PLACES_DEBUG" -ForegroundColor Yellow
}
