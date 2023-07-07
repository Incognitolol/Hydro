package integral.studios.hydro.util.task;

import integral.studios.hydro.Hydro;
import integral.studios.hydro.service.DataBaseService;
import integral.studios.hydro.model.check.violation.log.Log;
import integral.studios.hydro.service.LogService;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Queue;
import java.util.stream.Collectors;

public class ExportLogsTask extends BukkitRunnable {
    private final DataBaseService dataBaseService = Hydro.get(DataBaseService.class);
    private final LogService logService = Hydro.get(LogService.class);

    @Override
    public void run() {
        Queue<Log> queuedLogs = logService.getQueuedLogs();

        if (queuedLogs.isEmpty()) {
            return;
        }

        dataBaseService.getLogsCollection().insertMany(queuedLogs.stream()
                .map(Log::toDocument)
                .collect(Collectors.toList()));

        queuedLogs.clear();
    }
}
