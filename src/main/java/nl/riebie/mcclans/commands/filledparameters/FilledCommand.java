/*
 * Copyright (c) 2016 riebie, Kippers <https://bitbucket.org/Kippers/mcclans-core-sponge>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package nl.riebie.mcclans.commands.filledparameters;


import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.constraints.ParameterConstraint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by riebie on 16/01/2016.
 */
public final class FilledCommand {

    private String name;
    private Method method;
    private String clanPermission;
    private String spongePermission;
    private String description;
    private List<FilledParameter> parameters = new ArrayList<>();
    private List<FilledCommand> children = new ArrayList<>();
    private boolean isPlayerOnly;
    private final boolean isClanOnly;
    private String path;

    private boolean hasPageParameter;
    private boolean hasOptional;

    public FilledCommand(Command command, Method method, String path) {
        this.method = method;

        this.clanPermission = command.clanPermission();
        this.spongePermission = command.spongePermission();
        this.description = command.description();
        this.name = command.name();
        this.isPlayerOnly = command.isPlayerOnly();
        this.isClanOnly = command.isClanOnly();

        this.path = path == null ? name : String.format("%s %s", path, name);
    }

    public String getName() {
        return name;
    }

    public boolean hasChildren(){
        return !children.isEmpty();
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

    public void addParameter(String name, boolean isOptional, boolean isMultiline, boolean multilineString, ParameterConstraint constraint, Type parameterType) {
        NormalFilledParameter normalFilledParameter = new NormalFilledParameter(name, isOptional, isMultiline, multilineString, constraint, parameterType);
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
        }

        parameters.add(parameter);
    }

    public List<FilledParameter> getParameters() {
        return parameters;
    }

    public void execute(Object object, Object... objects) {
        try {
            method.invoke(object, objects);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public List<FilledCommand> getChildren() {
        return children;
    }

    public boolean hasPageParameter() {
        return hasPageParameter;
    }

    public String getClanPermission() {
        return clanPermission;
    }

    public boolean isPlayerOnly() {
        return isPlayerOnly;
    }

    public String getDescription() {
        return description;
    }

    public boolean isClanOnly() {
        return isClanOnly;
    }

    public String getSpongePermission() {
        return spongePermission;
    }

    public String getFullPath() {
        return path;
    }
}
