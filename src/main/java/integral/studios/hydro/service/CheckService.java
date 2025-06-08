package integral.studios.hydro.service;

import integral.studios.hydro.model.check.Check;
import integral.studios.hydro.model.PlayerData;
import lombok.Getter;
import org.atteo.classindex.ClassIndex;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class CheckService {
    private final Set<Class<? extends Check>> checkClasses = new HashSet<>();

    private final Set<Constructor<?>> constructors = new HashSet<>();

    public CheckService() {
        ClassIndex.getSubclasses(Check.class, Check.class.getClassLoader()).forEach(clazz -> {
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

    public List<String> getAlphabeticallySortedChecks() {
        return checkClasses.stream().map(Class::getSimpleName).sorted(Collator.getInstance()).collect(Collectors.toList());

    }
}