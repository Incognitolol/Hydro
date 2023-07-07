package integral.studios.hydro.model;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import integral.studios.hydro.model.check.Check;
import integral.studios.hydro.model.check.CheckData;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.model.check.violation.log.ViolationLog;
import integral.studios.hydro.model.tracker.impl.*;
import lombok.Data;
import org.bukkit.entity.Player;

import java.lang.reflect.*;
import java.util.*;

@Data
public class PlayerData {
    private static final Map<Field, Constructor<?>> CONSTRUCTORS = new HashMap<>();

    static {
        Arrays.stream(PlayerData.class.getDeclaredFields())
                .filter(field -> Tracker.class.isAssignableFrom(field.getType()))
                .forEach(field -> {
                    Class<? extends Tracker> clazz = (Class<? extends Tracker>) field.getType();

                    try {
                        CONSTRUCTORS.put(field, clazz.getConstructor(PlayerData.class));
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                });
    }

    private final Map<Check, Set<ViolationLog>> violations = new HashMap<>();
    private final Set<Tracker> trackers = new HashSet<>();
    private final CheckData checkData = new CheckData();

    private final Player bukkitPlayer;

    private int entityId;

    private MovementEmulationTracker movementEmulationTracker;
    private TransactionTracker transactionTracker;
    private KeepAliveTracker keepAliveTracker;
    private CollisionTracker collisionTracker;
    private AttributeTracker attributeTracker;
    private MovementTracker movementTracker;
    private RotationTracker rotationTracker;
    private TeleportTracker teleportTracker;
    private VelocityTracker velocityTracker;
    private EntityTracker entityTracker;
    private ActionTracker actionTracker;
     private WorldTracker worldTracker;

    private boolean banning;

    private boolean sniffingClicks;

    private int ticksExisted;

    private ClientVersion clientVersion;

    public PlayerData(Player player) {
        bukkitPlayer = player;

        entityId = bukkitPlayer.getEntityId();

        CONSTRUCTORS.forEach((field, constructor) -> {
            try {
                Tracker tracker = (Tracker) constructor.newInstance(this);

                trackers.add(tracker);

                field.set(this, tracker);
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        checkData.enable(this);
    }

    /**
     * Adds a logged violation to {@param check}
     * @param check The check to add the logged violation to
     * @param log The violation log
     */
    public void addViolation(Check check, ViolationLog log) {
        violations.computeIfAbsent(check, c -> new HashSet<>())
                .add(log);
    }

    /**
     * Gets all violation level for {@param check}
     * @param check The check to get the violation level for
     * @return The violation level
     */
    public int getViolationLevel(Check check) {
        return violations.computeIfAbsent(check, c -> new HashSet<>())
                .stream()
                .mapToInt(ViolationLog::getLevel)
                .sum();
    }

    /**
     * Gets all violation level within a certain time length
     * @param check The check to get the violation level for
     * @param length The time length to collect the violations from
     * @return The violation level
     */
    public int getViolationLevel(Check check, long length) {
        long now = System.currentTimeMillis();

        return violations.computeIfAbsent(check, c -> new HashSet<>())
                .stream()
                .filter(violation -> now + length > violation.getTimestamp())
                .mapToInt(ViolationLog::getLevel)
                .sum();
    }

    public ClientVersion getClientVersion() {
        ClientVersion clientVersion = PacketEvents.getAPI().getProtocolManager().getClientVersion(bukkitPlayer);

        if (clientVersion == null) {
            return ClientVersion.getById(PacketEvents.getAPI().getServerManager().getVersion().getProtocolVersion());
        }

        return clientVersion;
    }
}