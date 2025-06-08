package integral.studios.hydro.util.config;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Configuration {
    private boolean databaseEnabled = true;

    private String databaseHost = "127.0.0.1";

    private String databaseName = "hydro_db";

    private String databaseLogsName = "hydro_logs";

    private boolean performanceMode = false;

    boolean autoBans = true;

    private String primaryColor = "&3";

    private String secondaryColor = "&b";
}