package integral.studios.hydro.service;

import integral.studios.hydro.model.check.Check;
import integral.studios.hydro.model.PlayerData;
import lombok.Getter;
import org.atteo.classindex.ClassIndex;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

@Getter
public class CheckService {
    private final Set<Class<? extends Check>> checkClasses = new HashSet<>();

    private final Set<Constructor<?>> constructors = new HashSet<>();

    public CheckService() {
        ClassIndex.getSubclasses(Check.class, Check.class.getClassLoader())
                .forEach(clazz -> {
                    if (Modifier.isAbstract(clazz.getModifiers())) {
                        return;
                    }

                    checkClasses.add(clazz);
                });

        checkClasses.forEach(clazz -> {
            try {
                constructors.add(clazz.getConstructor(PlayerData.class));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        });
    }
}
