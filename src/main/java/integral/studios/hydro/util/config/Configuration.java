package integral.studios.hydro.util.config;

import lombok.Getter;

@Getter
public class Configuration {
    private final boolean databaseEnabled = true;

    private final String databaseHost = "127.0.0.1";
    private final String databaseName = "hydro_db";
    private final String databaseLogsName = "hydro_logs";
}
