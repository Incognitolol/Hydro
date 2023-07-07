package integral.studios.hydro.service;

import integral.studios.hydro.model.command.framework.BaseCommand;
import lombok.Getter;
import org.atteo.classindex.ClassIndex;

import java.util.HashSet;
import java.util.Set;

@Getter
public class CommandService {
    private final Set<BaseCommand> commands = new HashSet<>();

    public CommandService() {
        ClassIndex.getSubclasses(BaseCommand.class, BaseCommand.class.getClassLoader())
                .forEach(clazz -> {
                    try {
                        commands.add(clazz.newInstance());
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
    }
}
