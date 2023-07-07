package integral.studios.hydro.util.chat;

import integral.studios.hydro.Hydro;
import integral.studios.hydro.model.command.framework.BaseCommand;
import integral.studios.hydro.util.reflection.ReflectionUtil;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

@UtilityClass
public class CommandUtil {
    private final CommandMap commandMap = ReflectionUtil.getFieldValue(ReflectionUtil.getField(Bukkit.getServer().getClass(), "commandMap"), Bukkit.getServer());

    public void registerCommand(BaseCommand command) {
        try {
            commandMap.register(Hydro.get().getName(), command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
