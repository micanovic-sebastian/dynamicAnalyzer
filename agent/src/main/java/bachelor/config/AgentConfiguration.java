package bachelor.config;

import java.util.Collections;
import java.util.List;

/**
 * Ein Datencontainer für die Konfiguration des Agenten
 * Wird von Jackson (ConfigLoader) aus der JSON-Datei befüllt
 */
public class AgentConfiguration {

    private List<String> blockedPackages;
    private List<String> blockedClasses;
    private List<String> blockedMethods;

      /**
     * Gibt die Liste der blockierten Paketnamen zurück
     * @return Eine Liste von Paket-Präfixen
     */
    public List<String> getBlockedPackages() {
        return blockedPackages != null ? blockedPackages : Collections.emptyList();
    }

    /**
     * Gibt die Liste der blockierten Klassennamen zurück
     * @return Eine Liste von voll qualifizierten Klassennamen
     */
    public List<String> getBlockedClasses() {
        return blockedClasses != null ? blockedClasses : Collections.emptyList();
    }

    /**
     * Gibt die Liste der blockierten Methodennamen zurück
     * @return Eine Liste von Methodennamen (ohne Signatur)
     */
    public List<String> getBlockedMethods() {
        return blockedMethods != null ? blockedMethods : Collections.emptyList();
    }

}