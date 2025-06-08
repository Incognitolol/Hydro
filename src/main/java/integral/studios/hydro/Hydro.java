package integral.studios.hydro;

import co.aikar.commands.MessageType;
import co.aikar.commands.PaperCommandManager;
import com.github.retrooper.packetevents.PacketEvents;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.samjakob.spigui.GuiManager;
import integral.studios.hydro.service.*;
import dev.thomazz.pledge.api.Pledge;
import integral.studios.hydro.model.command.impl.HydroCommands;
import integral.studios.hydro.model.listener.packetevents.PacketEventsJoinQuitListener;
import integral.studios.hydro.model.listener.packetevents.PacketEventsPacketListener;
import integral.studios.hydro.model.listener.pledge.PledgeListener;
import integral.studios.hydro.util.chat.CC;
import integral.studios.hydro.util.config.Configuration;
import integral.studios.hydro.util.config.ConfigurationService;
import integral.studios.hydro.util.menu.ButtonListener;
import integral.studios.hydro.util.registry.ServiceRegistry;
import integral.studios.hydro.util.registry.ServiceRegistryImpl;
import integral.studios.hydro.util.task.ExportLogsTask;
import integral.studios.hydro.util.task.ServerTickTask;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Collections;

@Getter
public class Hydro extends JavaPlugin {
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mma");

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static Hydro instance;

    private final ServiceRegistry serviceRegistry = new ServiceRegistryImpl();

    private Configuration configuration;

    public static String PREFIX;

    private Pledge pledge;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        instance = this;

        registerConfiguration();
        registerPledge();
        registerPacketEvents();
        registerService();
        registerListeners();
        registerTasks();
        registerCommands();

        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }

    private void registerConfiguration() {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File configFile = new File(dataFolder, "config.json");

        try {
            if (!configFile.exists()) {
                configFile.createNewFile();

                try (FileWriter writer = new FileWriter(configFile)) {
                    GSON.toJson(configuration = new Configuration(), writer);
                }
            }

            try (FileReader reader = new FileReader(configFile)) {
                configuration = GSON.fromJson(reader, Configuration.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerPledge() {
        pledge = Pledge.build();

        pledge.setRange(-30000, -1);
        pledge.setTimeoutTicks(400);
        pledge.setFrameInterval(0);

        pledge.start(this);
    }

    private void registerPacketEvents() {
        PacketEvents.getAPI().init();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PledgeListener(), this);

        PacketEvents.getAPI().getEventManager().registerListeners(new PacketEventsPacketListener());
        PacketEvents.getAPI().getEventManager().registerListeners(new PacketEventsJoinQuitListener());

        getServer().getPluginManager().registerEvents(new ButtonListener(), this);
    }

    private void registerService() {
        install(PlayerDataService.class, new PlayerDataService());
        install(ViolationService.class, new ViolationService());
        install(DataBaseService.class, new DataBaseService());
        install(CheckService.class, new CheckService());
        install(GuiService.class, new GuiService());
        install(LogService.class, new LogService());
        install(PaperCommandManager.class, new PaperCommandManager(this));
        install(ConfigurationService.class, new ConfigurationService());
        install(GuiManager.class, new GuiManager(this));
    }

    private void registerTasks() {
        install(ServerTickTask.class, new ServerTickTask());

        get(ServerTickTask.class).runTaskTimer(this, 0L, 1L);

        install(ExportLogsTask.class, new ExportLogsTask());

        if (configuration.isDatabaseEnabled()) {
            get(ExportLogsTask.class).runTaskTimerAsynchronously(this, 20 * 60 * 2, 20 * 60 * 2);
        }
    }

    public void registerCommands() {
        get(PaperCommandManager.class).enableUnstableAPI("help");

        ChatColor primaryCC = ChatColor.getByChar(CC.PRI.charAt(1));
        ChatColor secondaryCC = ChatColor.getByChar(CC.SEC.charAt(1));

        get(PaperCommandManager.class).setFormat(MessageType.ERROR, ChatColor.RED, ChatColor.YELLOW, ChatColor.RED);
        get(PaperCommandManager.class).setFormat(MessageType.INFO, primaryCC, secondaryCC, ChatColor.WHITE);
        get(PaperCommandManager.class).setFormat(MessageType.HELP, primaryCC, secondaryCC, ChatColor.GRAY);
        get(PaperCommandManager.class).setFormat(MessageType.SYNTAX, primaryCC, secondaryCC, ChatColor.WHITE);

        Collections.singletonList(new HydroCommands()).forEach(hydroCommand -> {
            get(PaperCommandManager.class).registerCommand(hydroCommand);
        });
    }

    public boolean isLagging() {
        return get(ServerTickTask.class).isLagging();
    }

    public static Hydro get() {
        return instance;
    }

    public static <T> T install(Class<T> key, T service) {
        return instance.serviceRegistry.put(key, service);
    }

    public static <T> T get(Class<T> tClass) {
        return instance.serviceRegistry.get(tClass);
    }
}