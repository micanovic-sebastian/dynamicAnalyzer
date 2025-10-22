# PowerShell-Skript zum Ausführen der Java-Anwendung mit angehängtem JABS-Agenten.
# Führen Sie dieses Skript aus dem Stammverzeichnis des Projekts aus.

# --- Konfiguration ---
# Parameter für die Konfigurationsdatei, mit einem Standardwert
param (
    [string]$configFile = "config2.json"
)

# Pfade zu den JAR-Dateien des Agenten und der Anwendung, relativ zum Projektstamm.
$agentJar = "agent/target/agent-shaded.jar"
$appJar = "app/target/app-0.1-SNAPSHOT.jar"

# --- Überprüfung der Dateien ---
if (-not (Test-Path $agentJar)) {
    Write-Error "Agenten-JAR nicht gefunden unter: $agentJar. Bitte erstellen Sie das Projekt zuerst mit 'mvn clean package'."
    exit 1
}
if (-not (Test-Path $appJar)) {
    Write-Error "Anwendungs-JAR nicht gefunden unter: $appJar. Bitte erstellen Sie das Projekt zuerst mit 'mvn clean package'."
    exit 1
}
# Überprüfen, ob die Konfigurationsdatei existiert
if (-not (Test-Path $configFile)) {
    Write-Warning "Konfigurationsdatei nicht gefunden unter: $configFile. Der Agent wird die Standardkonfiguration (keine Sperren) verwenden."
}

# --- JVM-Argumente ---
$jvmArgs = @(
    "--add-opens=java.base/java.io=ALL-UNNAMED",
    "--add-opens=java.base/java.lang=ALL-UNNAMED",
    "--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED",
    "-XX:-UseInlineCaches"
)

# --- Ausführung ---
Write-Host "Anwendung wird mit JABS-Agenten gestartet..."
Write-Host "Agenten-JAR: $agentJar"
Write-Host "App-JAR:     $appJar"
Write-Host "Config-Datei: $configFile"
Write-Host "-------------------------------------------------"

# Das Argument für den Agenten wird mit '=' nach dem JAR-Pfad angehängt.
& java $jvmArgs -javaagent:"$agentJar=$configFile" -jar "$appJar"

Write-Host "-------------------------------------------------"
Write-Host "Anwendung beendet."