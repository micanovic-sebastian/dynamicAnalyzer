package bachelor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Lädt die Konfigurationsdatei für den Agenten
 * Diese Klasse kümmert sich um das Einlesen und Parsen der JSON-Konfiguration
 */
public class ConfigLoader {

    private static final Logger logger = LogManager.getLogger(ConfigLoader.class);

    /**
     * Lädt die Agenten-Konfiguration aus einer JSON-Datei
     * @param configPath Der Pfad zur config.json Datei
     * @return Ein AgentConfiguration-Objekt befüllt mit den Regeln
     */
    public static AgentConfiguration loadConfig(String configPath) {
        if (configPath == null || configPath.isEmpty()) {
            logger.warn("No config path provided. No blocks will be applied.");
            return new AgentConfiguration();
        }

        File configFile = new File(configPath);
        if (!configFile.exists()) {
            logger.error("Config file not found at '{}'. No blocks will be applied.", configPath);
            return new AgentConfiguration();
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            AgentConfiguration config = mapper.readValue(configFile, AgentConfiguration.class);
            logger.info("Configuration loaded from '{}'.", configPath);
            return config;
        } catch (IOException e) {
            logger.error("Failed to parse config file '{}'. No blocks will be applied.", configPath, e);
            return new AgentConfiguration();
        }
    }
}