param(
    [string]$BaseUrl = "http://localhost:8081",
    [string]$Cliente = "Teste Automatizado",
    [int]$ValorHora = 150
)

$ErrorActionPreference = "Stop"

function Invoke-Api {
    param(
        [ValidateSet("GET", "POST")]
        [string]$Method,
        [string]$Path,
        [object]$Body
    )

    $params = @{
        Method      = $Method
        Uri         = "$BaseUrl$Path"
        ContentType = "application/json"
    }

    if ($null -ne $Body) {
        $params.Body = $Body | ConvertTo-Json -Depth 10
    }

    try {
        return Invoke-RestMethod @params
    }
    catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        $responseBody = $_.ErrorDetails.Message
        throw "Falha em $Method $Path (HTTP $statusCode): $responseBody"
    }
}

function Assert-True {
    param(
        [bool]$Condition,
        [string]$Message
    )

    if (-not $Condition) {
        throw "Falha: $Message"
    }
}

Write-Host "Testando API em $BaseUrl" -ForegroundColor Cyan

$painelInicial = Invoke-Api -Method GET -Path "/api/painel"
Assert-True ($null -ne $painelInicial) "A API não retornou o painel."
Write-Host "[OK] API respondeu ao painel."

$nomeQuadra = "Quadra Teste $(Get-Date -Format 'yyyyMMddHHmmss')"
$quadra = Invoke-Api `
    -Method POST `
    -Path "/api/admin/quadras" `
    -Body @{
        nome = $nomeQuadra
        valorHora = $ValorHora
    }

Assert-True ($null -ne $quadra.id) "A quadra não foi criada."
Write-Host "[OK] Quadra criada: '$($quadra.nome)' (id $($quadra.id))."

$reserva = Invoke-Api `
    -Method POST `
    -Path "/api/admin/quadras/$($quadra.id)/reservas" `
    -Body @{
        clienteResponsavel = $Cliente
        inicioReserva = $null
        duracaoMinutos = 1
    }

Assert-True ($null -ne $reserva.id) "A reserva não foi criada."
Assert-True ($reserva.quadraId -eq $quadra.id) "A reserva foi associada à quadra errada."
Assert-True ($reserva.status -eq "EM_ANDAMENTO") "A reserva não iniciou imediatamente."
Write-Host "[OK] Reserva criada e iniciada: id $($reserva.id)."

$hoje = Get-Date -Format "yyyy-MM-dd"
$disponibilidade = Invoke-Api `
    -Method GET `
    -Path "/api/admin/quadras/$($quadra.id)/disponibilidade?inicio=$hoje&fim=$hoje"

Assert-True ($null -ne $disponibilidade) "A disponibilidade não foi retornada."
Write-Host "[OK] Disponibilidade consultada."

$pagamento = Invoke-Api `
    -Method POST `
    -Path "/api/admin/reservas/$($reserva.id)/pagar"

Assert-True ($pagamento.statusPagamento -eq "PAGO") "O pagamento não foi registrado."
Write-Host "[OK] Pagamento registrado."

$quadraLiberada = Invoke-Api `
    -Method POST `
    -Path "/api/admin/quadras/$($quadra.id)/liberar"

Assert-True ($quadraLiberada.status -eq "LIVRE") "A quadra não foi liberada."
Write-Host "[OK] Quadra liberada."

$reservas = Invoke-Api -Method GET -Path "/api/admin/reservas"
$reservaFinal = @($reservas) | Where-Object { $_.id -eq $reserva.id } | Select-Object -First 1

Assert-True ($null -ne $reservaFinal) "A reserva não apareceu no histórico."
Assert-True ($reservaFinal.status -eq "CONCLUIDA") "A reserva não foi concluída."
Assert-True ($reservaFinal.statusPagamento -eq "PAGO") "O pagamento não ficou salvo como pago."

Write-Host "[OK] Fluxo completo concluído com sucesso." -ForegroundColor Green
Write-Host "Quadra: $nomeQuadra | Reserva: $($reserva.id) | Cliente: $Cliente"
