package integral.studios.hydro.util.menu;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import integral.studios.hydro.Hydro;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public abstract class Menu {

    public static Map<UUID, Menu> currentlyOpenedMenus = Maps.newHashMap();

    @Getter
    private Map<Integer, Button> buttons = Maps.newHashMap();

    private boolean cancelClick = true;
    private boolean autoUpdate = false;
    private boolean updateAfterClick = true;
    private boolean closedByMenu = false;
    private boolean placeholder = false;
    private String staticTitle = null;

    public static Map<String, BukkitRunnable> checkTasks = Maps.newHashMap();

    private Button placeholderButton = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " ");

    protected Inventory inventory;

    private Method openInventoryMethod;

    private Inventory createInventory(Player player) {
        Map<Integer, Button> invButtons = getButtons(player);
        String title = ChatColor.translateAlternateColorCodes('&', getTitle(player));
        inventory = Bukkit.createInventory(player, size(invButtons), title.length() > 32 ? title.substring(0, 32) : title);
        for (Map.Entry<Integer, Button> buttonEntry : invButtons.entrySet()) {
            buttons.put(buttonEntry.getKey(), buttonEntry.getValue());
            inventory.setItem(buttonEntry.getKey(), buttonEntry.getValue().getButtonItem(player));
        }

        if (isPlaceholder()) {
            for (int index = 0; index < size(invButtons); ++index) {
                if (invButtons.get(index) == null) {
                    buttons.put(index, placeholderButton);
                    inventory.setItem(index, placeholderButton.getButtonItem(player));
                }
            }
        }

        return inventory;
    }

    public Method getOpenInventoryMethod() {
        if (openInventoryMethod == null) {
            try {
                (openInventoryMethod = CraftHumanEntity.class.getDeclaredMethod("openCustomInventory", Inventory.class, EntityPlayer.class, String.class)).setAccessible(true);
            } catch (NoSuchMethodException ex) {
                ex.printStackTrace();
            }
        }
        return openInventoryMethod;
    }

    public Menu() {
    }

    public Menu(String staticTitle) {
        this.staticTitle = (String) Preconditions.checkNotNull((Object)staticTitle);
    }

    public void openMenu(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        try {
            getOpenInventoryMethod().invoke(player, createInventory(player), entityPlayer, "minecraft:container");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        update(player);
    }

    public void update(Player player) {
        cancelCheck(player);
        currentlyOpenedMenus.put(player.getUniqueId(), this);
        onOpen(player);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancelCheck(player);
                    currentlyOpenedMenus.remove(player.getUniqueId());
                }

                if (isAutoUpdate()) {
                    player.getOpenInventory().getTopInventory().setContents(createInventory(player).getContents());
                }
            }
        };

        runnable.runTaskTimer(Hydro.get(), 10L, 10L);
        checkTasks.put(player.getName(), runnable);
    }

    public static void cancelCheck(Player player) {
        if (checkTasks.containsKey(player.getName())) {
            checkTasks.remove(player.getName()).cancel();
        }
    }

    public int size(Map<Integer, Button> buttons) {
        int highest = 0;
        for (int buttonValue : buttons.keySet()) {
            if (buttonValue > highest) {
                highest = buttonValue;
            }
        }
        return (int) (Math.ceil((highest + 1) / 9.0) * 9.0);
    }

    public int getSlot(int x, int y) {
        return ((9 * y) + x);
    }

    public String getTitle(Player player) {
        return this.staticTitle;
    }

    public abstract Map<Integer, Button> getButtons(Player player);

    public void onOpen(Player player) {
    }

    public void onClose(Player player) {
    }
}
