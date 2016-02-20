package nl.riebie.mcclans.commands.FilledParameters;

/**
 * Created by Mirko on 16/01/2016.
 */
public class NormalFilledParameter implements FilledParameter {
    private boolean optional = false;
    private int minimalLength = -1;
    private int maximalLength = -1;
    private boolean multiline = false;
    private String regex = "";
    private Class<?> parameterType;

    public NormalFilledParameter(boolean optional, boolean multiline, int minimalLength, int maximalLength, String regex, Class<?> parameterType) {
        this.optional = optional;
        this.minimalLength = minimalLength;
        this.maximalLength = maximalLength;
        this.multiline = multiline;
        this.regex = regex;
        this.parameterType = parameterType;
    }

    public NormalFilledParameter(Class<?> parameterType) {
        this.parameterType = parameterType;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isMultiline() {
        return multiline;
    }

    public int getMinimalLength() {
        return minimalLength;
    }

    public int getMaximalLength() {
        return maximalLength;
    }

    public String getRegex() {
        return regex;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }

}
