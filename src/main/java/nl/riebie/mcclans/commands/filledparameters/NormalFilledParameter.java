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

import nl.riebie.mcclans.commands.constraints.ParameterConstraint;

import java.lang.reflect.Type;

/**
 * Created by riebie on 16/01/2016.
 */
public class NormalFilledParameter implements FilledParameter {

    private final ParameterConstraint constraint;
    private final boolean multiline;
    private final Type parameterType;
    private final boolean isOptional;
    private final boolean multilineString;
    private final String name;

    public NormalFilledParameter(String name, boolean optional, boolean multiline, boolean multilineString, ParameterConstraint constraint, Type parameterType) {
        this.name = name;
        this.isOptional = optional;
        this.multilineString = multilineString;this.multiline = multiline;
        this.constraint = constraint;
        this.parameterType = parameterType;
    }

    public NormalFilledParameter(Class<?> parameterType) {
        this.parameterType = parameterType;
        this.name = null;
        this.isOptional = false;
        this.multiline = false;
        this.multilineString = false;
        this.constraint = null;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public boolean isMultiline() {
        return multiline;
    }

    public ParameterConstraint getConstraint(){
        return constraint;
    }

    public Type getParameterType() {
        return parameterType;
    }

    public String getName() {
        return name;
    }

    public boolean isMultilineString() {
        return multilineString;
    }
}
