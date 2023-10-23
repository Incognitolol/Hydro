package integral.studios.hydro;

import com.github.retrooper.packetevents.PacketEvents;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import integral.studios.hydro.model.listener.PacketEventsListener;
import integral.studios.hydro.model.listener.PledgeListener;
import integral.studios.hydro.service.*;
import integral.studios.hydro.util.config.Configuration;
import integral.studios.hydro.model.listener.PlayerListener;
import integral.studios.hydro.util.registry.ServiceRegistry;
import integral.studios.hydro.util.registry.ServiceRegistryImpl;
import integral.studios.hydro.util.task.ExportLogsTask;
import integral.studios.hydro.util.task.ServerTickTask;
import integral.studios.hydro.util.chat.CC;
import integral.studios.hydro.util.chat.CommandUtil;
import dev.thomazz.pledge.api.Pledge;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

@Getter
public class Hydro extends JavaPlugin {
    public static final String PREFIX = CC.BLUE + "[" + "♿" + "] " + CC.RESET;

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static Hydro instance;

    private final ServiceRegistry serviceRegistry = new ServiceRegistryImpl();

    private Configuration configuration;

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

        // lol
        pledge.setRange(-30000, -1);
        pledge.setTimeoutTicks(400);
        pledge.setFrameInterval(0);

        pledge.start(this);
    }

    private void registerPacketEvents() {
        PacketEvents.getAPI().init();
    }

    private void registerListeners() {
        PacketEvents.getAPI().getEventManager().registerListeners(new PacketEventsListener());

        getServer().getPluginManager().registerEvents(new PledgeListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    private void registerService() {
        install(PlayerDataService.class, new PlayerDataService());
        install(ViolationService.class, new ViolationService());
        install(DataBaseService.class, new DataBaseService());
        install(CommandService.class, new CommandService());
        install(CheckService.class, new CheckService());
        install(LogService.class, new LogService());
    }

    private void registerTasks() {
        install(ServerTickTask.class, new ServerTickTask());

        get(ServerTickTask.class).runTaskTimer(this, 0L, 1L);

        install(ExportLogsTask.class, new ExportLogsTask());

        if (configuration.isDatabaseEnabled()) {
            get(ExportLogsTask.class).runTaskTimerAsynchronously(this, 20 * 60 * 2, 20 * 60 * 2);
        }
    }

    private void registerCommands() {
        get(CommandService.class).getCommands().forEach(CommandUtil::registerCommand);
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
