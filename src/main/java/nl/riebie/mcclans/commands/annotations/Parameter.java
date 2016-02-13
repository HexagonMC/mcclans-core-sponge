package nl.riebie.mcclans.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Mirko on 16/01/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Parameter {
    boolean optional() default false;

    boolean multiline() default false;

    int minimalLength() default -1;

    int maximalLength() default -1;

    String regex() default "";
}
