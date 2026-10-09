# Script para construir y ejecutar el servidor Ktor directamente
Write-Host "=== Deteniendo procesos de Gradle ===" -ForegroundColor Yellow
Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object { $_.Path -like "*gradle*" } | Stop-Process -Force -ErrorAction SilentlyContinue
Get-Process -Name "gradle*" -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

Write-Host "=== Limpiando archivos de caché bloqueados ===" -ForegroundColor Yellow
$serverBuildPath = Join-Path $PSScriptRoot "server\build"
if (Test-Path $serverBuildPath) {
    try {
        Remove-Item -Path $serverBuildPath -Recurse -Force -ErrorAction Stop
        Write-Host "Caché del servidor limpiado" -ForegroundColor Green
    } catch {
        Write-Host "No se pudo limpiar completamente, continuando..." -ForegroundColor Yellow
    }
}

# Carga las variables de entorno desde .env
$envFile = Join-Path $PSScriptRoot "server\.env"

if (Test-Path $envFile) {
    Get-Content $envFile | ForEach-Object {
        if ($_ -match '^\s*([^#][^=]*)=(.*)$') {
            $key = $matches[1].Trim()
            $value = $matches[2].Trim()
            [System.Environment]::SetEnvironmentVariable($key, $value, "Process")
        }
    }
    Write-Host "Variables de entorno cargadas desde .env" -ForegroundColor Green
} else {
    Write-Host "ADVERTENCIA: No se encontró el archivo .env en server/" -ForegroundColor Yellow
}

Write-Host "=== Construyendo el servidor ===" -ForegroundColor Cyan
Set-Location (Join-Path $PSScriptRoot "server")
.\..\gradlew.bat build -x test --no-daemon

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Falló la construcción del servidor" -ForegroundColor Red
    exit 1
}

Write-Host "=== Construcción completada ===" -ForegroundColor Green
Write-Host ""

# Buscar el JAR construido
$jarPath = Get-ChildItem -Path "build\libs" -Filter "*.jar" | Select-Object -First 1

if (-not $jarPath) {
    Write-Host "ERROR: No se encontró el archivo JAR" -ForegroundColor Red
    exit 1
}

Write-Host "=== Iniciando servidor Ktor en puerto 8080 ===" -ForegroundColor Cyan
Write-Host "JAR: $($jarPath.Name)" -ForegroundColor Gray
Write-Host "Presiona Ctrl+C para detener el servidor" -ForegroundColor Gray
Write-Host ""

# Intentar configurar adb reverse para dispositivos Android conectados
$adbExe = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
if (Test-Path $adbExe) {
    try {
        $adbDevices = & $adbExe devices | Where-Object { $_ -match '\tdevice$' } | ForEach-Object { ($_ -split '\t')[0] }
        foreach ($dev in $adbDevices) {
            Write-Host "Configurando adb reverse tcp:8080 tcp:8080 para $dev..." -ForegroundColor Green
            & $adbExe -s $dev reverse tcp:8080 tcp:8080 2>$null
        }
    } catch {
        Write-Host "No se pudo configurar adb reverse automáticamente." -ForegroundColor Yellow
    }
}


# Ejecutar el JAR directamente con Java 21
$java21Path = "C:\Users\User\.gradle\jdks\eclipse_adoptium-21-amd64-windows.2\bin\java.exe"
if (Test-Path $java21Path) {
    & $java21Path "-Djava.net.preferIPv4Stack=true" "-Xmx1024m" -jar $jarPath.FullName
} else {
    Write-Host "ERROR: Java 21 no encontrado en $java21Path" -ForegroundColor Red
    Write-Host "Usando Java del PATH..." -ForegroundColor Yellow
    & java "-Djava.net.preferIPv4Stack=true" "-Xmx1024m" -jar $jarPath.FullName
}
