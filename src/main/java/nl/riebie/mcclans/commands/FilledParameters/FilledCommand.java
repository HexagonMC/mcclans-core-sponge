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
    private boolean hasOptional;
    private boolean hasMultiline;

    public FilledCommand(String name, Method method, Permission permission, String description) {
        this.method = method;
        this.permission = permission;
        this.description = description;
        this.name = name;
    }

    public String getName() {
        return name;
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

    public void addParameter(Class<?> optional, boolean isMultiline, Class<?> listType, int minimalLength, int maximalLength, String regex, Class<?> parameterType) {
        NormalFilledParameter normalFilledParameter = new NormalFilledParameter(optional, isMultiline, listType, minimalLength, maximalLength, regex, parameterType);
        checkStateAndAddParameter(normalFilledParameter);
        if (normalFilledParameter.isOptional()) {
            hasOptional = true;
        }
    }

    public void addParameter(Class<?> parameterType) {

        checkStateAndAddParameter(new NormalFilledParameter(parameterType));
    }

    public void addCommandSenderParameter() {

        checkStateAndAddParameter(new CommandSenderFilledParameter());
    }


    public void addCommandSourceParameter() {

        checkStateAndAddParameter(new CommandSourceFilledParameter());
    }

    public void addPageParameter() {
        checkStateAndAddParameter(new PageFilledParameter());
        hasPageParameter = true;
    }

    //TODO fix that a page parameter doesn't really have to be the last parameter
    //TODO also fix crash when a parameter isn't marked by anything special in a command
    private void checkStateAndAddParameter(FilledParameter parameter) {
        if (hasPageParameter) {
            throw new IllegalStateException("The PageParameter should always be the last parameter in a command");
        } else if (hasOptional && !(parameter instanceof PageFilledParameter)) {
            throw new IllegalStateException("Only the last parameter can be optional");
        } else if (hasMultiline && !(parameter instanceof PageFilledParameter)) {
            throw new IllegalStateException("Only the last parameter can be multiline");
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

    public Permission getPermission() {
        return permission;
    }
}
