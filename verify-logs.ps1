# Script para verificar que los logs están en el APK
Write-Host "=== VERIFICANDO QUE LOS LOGS ESTÁN EN EL APK ===" -ForegroundColor Cyan

# Verificar fecha del APK
$apkPath = "app\build\outputs\apk\debug\app-debug.apk"
if (Test-Path $apkPath) {
    $apkInfo = Get-Item $apkPath
    Write-Host "APK encontrado:" -ForegroundColor Green
    Write-Host "  Fecha: $($apkInfo.LastWriteTime)" -ForegroundColor White
    Write-Host "  Tamaño: $([math]::Round($apkInfo.Length / 1MB, 2)) MB" -ForegroundColor White
    
    # Verificar si el APK contiene los logs
    Write-Host ""
    Write-Host "Buscando logs en el APK..." -ForegroundColor Yellow
    
    # Extraer y buscar en el APK
    $tempDir = "$env:TEMP\apk_verify_$(Get-Random)"
    New-Item -ItemType Directory -Path $tempDir -Force | Out-Null
    
    try {
        # Copiar APK como ZIP
        $zipPath = "$tempDir\app.zip"
        Copy-Item $apkPath $zipPath
        
        # Extraer
        Expand-Archive -Path $zipPath -DestinationPath $tempDir -Force
        
        # Buscar en classes.dex
        $dexFiles = Get-ChildItem -Path $tempDir -Filter "*.dex" -Recurse
        $foundLogs = $false
        
        foreach ($dex in $dexFiles) {
            $content = [System.IO.File]::ReadAllBytes($dex.FullName)
            $text = [System.Text.Encoding]::ASCII.GetString($content)
            
            if ($text -match "PLACES_DEBUG") {
                Write-Host "✓ PLACES_DEBUG encontrado en $($dex.Name)" -ForegroundColor Green
                $foundLogs = $true
            }
            if ($text -match "TAG_DEBUG") {
                Write-Host "✓ TAG_DEBUG encontrado en $($dex.Name)" -ForegroundColor Green
                $foundLogs = $true
            }
            if ($text -match "AI_DEBUG") {
                Write-Host "✓ AI_DEBUG encontrado en $($dex.Name)" -ForegroundColor Green
                $foundLogs = $true
            }
        }
        
        if (-not $foundLogs) {
            Write-Host ""
            Write-Host "✗ LOS LOGS NO ESTÁN EN EL APK" -ForegroundColor Red
            Write-Host "Esto significa que necesitas recompilar la app" -ForegroundColor Yellow
            Write-Host ""
            Write-Host "Ejecuta:" -ForegroundColor Cyan
            Write-Host "  .\gradlew.bat clean" -ForegroundColor White
            Write-Host "  .\gradlew.bat assembleDebug" -ForegroundColor White
        } else {
            Write-Host ""
            Write-Host "✓ Los logs están en el APK" -ForegroundColor Green
            Write-Host "Si no los ves en logcat, el problema es el filtro" -ForegroundColor Yellow
        }
        
    } catch {
        Write-Host "Error al verificar: $_" -ForegroundColor Red
    } finally {
        # Limpiar
        if (Test-Path $tempDir) {
            Remove-Item -Path $tempDir -Recurse -Force -ErrorAction SilentlyContinue
        }
    }
} else {
    Write-Host "APK no encontrado en $apkPath" -ForegroundColor Red
    Write-Host "Ejecuta: .\gradlew.bat assembleDebug" -ForegroundColor Yellow
}
