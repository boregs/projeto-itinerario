param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('Begin', 'Finish')]
    [string]$Action,
    [Parameter(Mandatory = $true)]
    [ValidatePattern('^[a-zA-Z0-9_-]+$')]
    [string]$Step,
    [string]$Message,
    [string]$Validation = 'Pendente.'
)

$ErrorActionPreference = 'Stop'
$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$snapshotDirectory = Join-Path $projectRoot '.local/tracking'
New-Item -ItemType Directory -Force -Path $snapshotDirectory | Out-Null
$snapshotPath = Join-Path $snapshotDirectory "$Step.json"

function Get-ProjectSnapshot {
    $snapshot = @{}
    Push-Location $projectRoot
    try {
        $files = @(& rg --files --hidden -g '!.local/**' -g '!target/**' -g '!node_modules/**' -g '!.git/**' -g '!.env' -g '!.env.*' -g '!CHANGELOG_DEV.md')
        if ($LASTEXITCODE -gt 1) { throw 'Falha ao listar arquivos com rg.' }
        if (Test-Path -LiteralPath '.env.example') { $files += '.env.example' }
        foreach ($file in $files) {
            $snapshot[$file.Replace('\', '/')] = (Get-FileHash -LiteralPath $file -Algorithm SHA256).Hash
        }
    } finally {
        Pop-Location
    }
    return $snapshot
}

$current = Get-ProjectSnapshot
if ($Action -eq 'Begin') {
    if (Test-Path -LiteralPath $snapshotPath) { throw 'Já existe um snapshot para esta etapa. Use outro identificador.' }
    $current | ConvertTo-Json | Set-Content -LiteralPath $snapshotPath -Encoding utf8
    Write-Output "Etapa $Step iniciada."
    exit 0
}

if (-not (Test-Path -LiteralPath $snapshotPath)) { throw 'Snapshot inicial não encontrado.' }
if ([string]::IsNullOrWhiteSpace($Message)) { throw 'Informe uma mensagem de commit sugerida.' }
$previous = Get-Content -LiteralPath $snapshotPath -Raw | ConvertFrom-Json -AsHashtable
$logPath = Join-Path $projectRoot 'CHANGELOG_DEV.md'
if (-not (Test-Path -LiteralPath $logPath)) {
    @(
        '# Histórico de desenvolvimento',
        '',
        'Rastreamento iniciado em 17/10/2026. As etapas anteriores estão resumidas no README; não há reconstrução de diffs históricos.',
        '',
        'As mensagens abaixo são sugestões de commit, não commits executados. Arquivos locais com segredos, dependências, builds e snapshots não entram nas listas. Este documento é atualizado em todas as etapas.'
    ) | Set-Content -LiteralPath $logPath -Encoding utf8
}
if ((Get-Content -LiteralPath $logPath -Raw).Contains("## Etapa $Step ")) { throw 'Esta etapa já foi registrada.' }
$lines = @('', "## Etapa $Step — $(Get-Date -Format 'yyyy-MM-dd')", '', "Commit sugerido: ``$($Message.Replace("`n", ' ').Replace("`r", ' '))``", '', "Validação: $Validation", '', '| Situação | Arquivo |', '| --- | --- |')
foreach ($file in (@($previous.Keys) + @($current.Keys) | Sort-Object -Unique)) {
    if (-not $previous.ContainsKey($file)) { $state = 'Adicionado' }
    elseif (-not $current.ContainsKey($file)) { $state = 'Removido' }
    elseif ($previous[$file] -ne $current[$file]) { $state = 'Modificado' }
    else { continue }
    $lines += "| $state | ``$file`` |"
}
$lines += '| Atualizado | `CHANGELOG_DEV.md` |'
$lines | Add-Content -LiteralPath $logPath -Encoding utf8
Write-Output "Etapa $Step registrada em CHANGELOG_DEV.md."
