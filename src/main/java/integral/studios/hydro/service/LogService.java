package integral.studios.hydro.service;

import integral.studios.hydro.model.check.violation.log.Log;
import lombok.Getter;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
public class LogService {
    private final Queue<Log> queuedLogs = new ConcurrentLinkedQueue<>();
}