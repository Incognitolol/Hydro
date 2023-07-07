package integral.studios.hydro.model.command.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE } )
public @interface CommandManifest {
    String permission() default "hydro.mod";

    // We might want to run some commands async, such as commands that perform database operations
    boolean async() default false;
}
