package integral.studios.hydro.util.nms;

import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@UtilityClass
public class NmsUtil {
    public EntityPlayer getEntityPlayer(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    /**
     * Gets the friction of a block at {@param x} {@param y} {@param z}
     * @param world The world to check in
     * @param x The x location
     * @param y The y location
     * @param z The z location
     * @return The block friction
     */
    public float getBlockFriction(World world, double x, double y, double z) {
        return world.getType(new BlockPosition(x, y, z)).getBlock().frictionFactor;
    }
}