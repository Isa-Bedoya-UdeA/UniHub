# Script para ejecutar el servidor Ktor
# Maneja archivos bloqueados y limpia el caché

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

Write-Host "=== Iniciando servidor Ktor en puerto 8080 ===" -ForegroundColor Cyan
Write-Host "Presiona Ctrl+C para detener el servidor" -ForegroundColor Gray

# Configurar argumentos de JVM para el servidor
$env:JAVA_OPTS = "-Djava.net.preferIPv4Stack=true -Xmx1024m -Xms256m"
$env:GRADLE_OPTS = "-Djava.net.preferIPv4Stack=true -Xmx1024m -Xms256m"

# Ejecutar el servidor Ktor
Set-Location (Join-Path $PSScriptRoot "server")
.\..\gradlew.bat run --no-daemon
