package bachelor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class ConfigLoader {

    public static AgentConfiguration loadConfig(String configPath) {
        if (configPath == null || configPath.isEmpty()) {
            System.err.println("No config path provided. No blocks will be applied.");
            return new AgentConfiguration(); // Returns empty list
        }

        File configFile = new File(configPath);
        if (!configFile.exists()) {
            System.err.println("Config file not found at '" + configPath + "'. No blocks will be applied.");
            return new AgentConfiguration();
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            AgentConfiguration config = mapper.readValue(configFile, AgentConfiguration.class);
            System.out.println("Configuration loaded from '" + configPath + "'.");
            return config;
        } catch (IOException e) {
            System.err.println("Failed to parse config file '" + configPath + "'. No blocks will be applied.");
            e.printStackTrace();
            return new AgentConfiguration();
        }
    }
}