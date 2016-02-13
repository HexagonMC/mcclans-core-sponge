package nl.riebie.mcclans.commands.FilledParameters;


import nl.riebie.mcclans.api.enums.Permission;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mirko on 16/01/2016.
 */
public final class FilledCommand {
    private String name;
    private Method method;
    private Permission permission;
    private String description;
    private List<FilledParameter> parameters = new ArrayList<>();
    private List<FilledCommand> children = new ArrayList<>();
    private boolean hasPageParameter;

    public FilledCommand(String name, Method method, Permission permission, String description) {
        this.method = method;
        this.permission = permission;
        this.description = description;
        this.name = name;
    }

    public FilledCommand getChild(String name) {
        for (FilledCommand command : children) {
            if (command.name.equals(name)) {
                return command;
            }
        }
        return null;
    }

    public void addChild(FilledCommand child) {
        children.add(child);
    }

    public void addParameter(boolean optional, int minimalLength, int maximalLength, String regex, Class<?> parameterType) {
        checkStateAndAddParameter(new NormalFilledParameter(optional, minimalLength, maximalLength, regex, parameterType));
    }

    public void addParameter(Class<?> parameterType) {

        checkStateAndAddParameter(new NormalFilledParameter(parameterType));
    }

    public void addCommandSenderParameter() {

        checkStateAndAddParameter(new CommandSenderFilledParameter());
    }

    public void addPageParameter() {
        checkStateAndAddParameter(new PageFilledParameter());
        hasPageParameter = true;
    }

    private void checkStateAndAddParameter(FilledParameter parameter) {
        if (hasPageParameter) {
            throw new IllegalStateException("the PageParameter should be the last parameter in a command");
        }

        parameters.add(parameter);
    }

    public List<FilledParameter> getParameters() {
        return parameters;
    }

    public void execute(Object object, Object... objects) {
        try {
            method.invoke(object, objects);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public List<FilledCommand> getChildren() {
        return children;
    }

    public boolean isHasPageParameter() {
        return hasPageParameter;
    }
}
