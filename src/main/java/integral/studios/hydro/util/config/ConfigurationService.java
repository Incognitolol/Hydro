package integral.studios.hydro.util.config;

import integral.studios.hydro.Hydro;
import integral.studios.hydro.service.CheckService;

/**
 * @author Salers
 * made on integral.studios.hydro.service
 */
public class ConfigurationService {
    public ConfigurationService() {
        CheckService checkService = Hydro.get(CheckService.class);
        
        for (String check : checkService.getAlphabeticallySortedChecks()) {
            String path = "checks." + check.toLowerCase();
            
            if (!Hydro.get().getConfig().contains(path)) {
                Hydro.get().getConfig().set(path + ".enabled", true);
                Hydro.get().getConfig().set(path + ".auto-ban", false);
                Hydro.get().getConfig().set(path + ".max-violations", 100D);
            }
        }

        Hydro.get().saveConfig();
        Hydro.get().reloadConfig();
    }

    public boolean isEnabled(String check) {
        return Hydro.get().getConfig().getBoolean("checks." + check.toLowerCase() + ".enabled");
    }

    public boolean isAutoban(String check) {
        return Hydro.get().getConfig().getBoolean("checks." + check.toLowerCase() + ".auto-ban");
    }

    public double getMaxViolations(String check) {
        return Hydro.get().getConfig().getDouble("checks." + check.toLowerCase() + ".max-violations");
    }

    public void toggleCheckActivation(String check) {
        Hydro.get().getConfig().set("checks." + check.toLowerCase() + ".enabled", !isEnabled(check));

        Hydro.get().saveConfig();
        Hydro.get().reloadConfig();
    }
}