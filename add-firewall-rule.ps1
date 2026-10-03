# Agregar regla del firewall para el puerto 8080
# Ejecutar como administrador

Write-Host "Agregando regla del firewall para el puerto 8080..." -ForegroundColor Cyan

try {
    New-NetFirewallRule -DisplayName "Ktor Server 8080" -Direction Inbound -Protocol TCP -LocalPort 8080 -Action Allow -ErrorAction Stop
    Write-Host "[OK] Regla agregada exitosamente" -ForegroundColor Green
    Write-Host ""
    Write-Host "Ahora puedes:" -ForegroundColor Yellow
    Write-Host "1. Iniciar el servidor Ktor: .\run-server.ps1" -ForegroundColor White
    Write-Host "2. Instalar la app en tu dispositivo" -ForegroundColor White
    Write-Host "3. Probar la funcionalidad de IA" -ForegroundColor White
} catch {
    Write-Host "[ERROR] Error al agregar la regla: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Asegurate de ejecutar este script como administrador" -ForegroundColor Yellow
}
