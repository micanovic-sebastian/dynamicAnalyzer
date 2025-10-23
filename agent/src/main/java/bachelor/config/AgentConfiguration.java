package bachelor.config;

import java.util.Collections;
import java.util.List;

// config.json
public class AgentConfiguration {

    private List<String> blockedPackages;
    private List<String> blockedClasses;
    private List<String> blockedMethods;



    public List<String> getBlockedPackages() {
        return blockedPackages != null ? blockedPackages : Collections.emptyList();
    }

    public List<String> getBlockedClasses() {
        return blockedClasses != null ? blockedClasses : Collections.emptyList();
    }

    public List<String> getBlockedMethods() {
        return blockedMethods != null ? blockedMethods : Collections.emptyList();
    }

}