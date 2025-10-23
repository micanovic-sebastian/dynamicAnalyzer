

# Parameter für die Konfigurationsdatei, mit einem Standardwert
param (
    [string]$configFile = "config.json"
)

# Pfade zu den JAR-Dateien des Agenten und der Anwendung, relativ zum Projektstamm.
$agentJar = "agent/target/agent-shaded.jar"
$appJar = "app/target/app-0.1-SNAPSHOT.jar"

# Überprüfung der Dateien
if (-not (Test-Path $agentJar)) {
    Write-Error "Agenten-JAR nicht gefunden unter: $agentJar. Bitte erstellen Sie das Projekt zuerst mit 'mvn clean package'."
    exit 1
}
if (-not (Test-Path $appJar)) {
    Write-Error "Anwendungs-JAR nicht gefunden unter: $appJar. Bitte erstellen Sie das Projekt zuerst mit 'mvn clean package'."
    exit 1
}
# Überprüfen ob die Konfigurationsdatei vorhanden ist
if (-not (Test-Path $configFile)) {
    Write-Warning "Konfigurationsdatei nicht gefunden unter: $configFile. Der Agent wird die Standardkonfiguration (keine Sperren) verwenden."
}

# JVM-Argumente
$jvmArgs = @(
    "--add-opens=java.base/java.io=ALL-UNNAMED",
    "--add-opens=java.base/java.lang=ALL-UNNAMED",
    "--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED",
    "-XX:-UseInlineCaches"
)

# Ausführung
Write-Host "Agenten-JAR: $agentJar"
Write-Host "App-JAR:     $appJar"
Write-Host "Config-Datei: $configFile"

# Das Argument für den Agenten wird mit '=' nach dem JAR-Pfad angehängt.
& java $jvmArgs -javaagent:"$agentJar=$configFile" -jar "$appJar"

Write-Host "Anwendung beendet."