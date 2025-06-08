package integral.studios.hydro.model;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import integral.studios.hydro.model.check.violation.log.ViolationLog;
import integral.studios.hydro.model.emulator.EmulationEngine;
import integral.studios.hydro.model.tracker.Tracker;
import integral.studios.hydro.model.tracker.impl.*;
import integral.studios.hydro.model.check.Check;
import integral.studios.hydro.model.check.CheckData;
import integral.studios.hydro.model.tracker.impl.*;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Data
public class PlayerData {
    private final Map<Check, Set<ViolationLog>> violations = new HashMap<>();
    private final Set<Tracker> trackers = new HashSet<>();
    private final CheckData checkData = new CheckData();

    private TransactionTracker transactionTracker;
    private KeepAliveTracker keepAliveTracker;
    private CollisionTracker collisionTracker;
    private AttributeTracker attributeTracker;
    private PositionTracker positionTracker;
    private RotationTracker rotationTracker;
    private TeleportTracker teleportTracker;
    private VelocityTracker velocityTracker;
    private EntityTracker entityTracker;
    private ActionTracker actionTracker;
    private WorldTracker worldTracker;

    private EmulationEngine emulationEngine;

    private int entityId = -1;

    private boolean banning;

    private boolean sniffingClicks;

    private int ticksExisted;

    private ClientVersion clientVersion;

    public UUID uuid;

    public PlayerData(User user) {
        uuid = user.getUUID();

        clientVersion = user.getClientVersion();

        collisionTracker = new CollisionTracker(this);
        positionTracker = new PositionTracker(this);
        rotationTracker = new RotationTracker(this);
        teleportTracker = new TeleportTracker(this);
        worldTracker = new WorldTracker(this);
        transactionTracker = new TransactionTracker(this);
        keepAliveTracker = new KeepAliveTracker(this);
        entityTracker = new EntityTracker(this);
        attributeTracker = new AttributeTracker(this);
        velocityTracker = new VelocityTracker(this);
        actionTracker = new ActionTracker(this);

        trackers.add(collisionTracker);
        trackers.add(positionTracker);
        trackers.add(rotationTracker);
        trackers.add(teleportTracker);
        trackers.add(worldTracker);
        trackers.add(transactionTracker);
        trackers.add(keepAliveTracker);
        trackers.add(entityTracker);
        trackers.add(attributeTracker);
        trackers.add(velocityTracker);
        trackers.add(actionTracker);

        emulationEngine = new EmulationEngine(this);

        checkData.enable(this);
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
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

    public int getEntityId() {
        if (entityId != -1)
            return entityId;

        try {
            entityId = getBukkitPlayer().getEntityId();
        } catch (NullPointerException exception) {
            // The player hasn't been loaded yet, they just joined, shouldnt be an issue
            exception.printStackTrace();
        }

        return entityId;
    }
}