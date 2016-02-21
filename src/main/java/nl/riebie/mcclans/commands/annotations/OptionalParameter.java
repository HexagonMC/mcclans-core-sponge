package nl.riebie.mcclans.commands.annotations;

import nl.riebie.mcclans.commands.constraints.length.LengthConstraints;
import nl.riebie.mcclans.commands.constraints.regex.RegexConstraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Mirko on 21/02/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface OptionalParameter {
    Class<?> value();

    boolean multiline() default false;

    LengthConstraints length() default LengthConstraints.EMPTY;

    RegexConstraints regex() default RegexConstraints.EMPTY;
}
