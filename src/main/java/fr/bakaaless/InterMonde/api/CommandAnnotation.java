package fr.bakaaless.InterMonde.api;

import fr.bakaaless.InterMonde.permissions.Permissions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandAnnotation {

    String[] command();
    String description();
    String usage() default "intermonde help";
    Permissions permission() default Permissions.NONE;

}
