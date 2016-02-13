package nl.riebie.mcclans.commands.annotations;

import nl.riebie.mcclans.commands.constraints.length.EmptyLengthConstraint;
import nl.riebie.mcclans.commands.constraints.length.LengthConstraint;
import nl.riebie.mcclans.commands.constraints.regex.EmptyRegexConstraint;
import nl.riebie.mcclans.commands.constraints.regex.RegexConstraint;

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

    Class<? extends LengthConstraint> length() default EmptyLengthConstraint.class;

    Class<? extends RegexConstraint> regex() default EmptyRegexConstraint.class;
}
