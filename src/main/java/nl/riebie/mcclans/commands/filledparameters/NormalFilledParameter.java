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

/**
 * Created by riebie on 16/01/2016.
 */
public class NormalFilledParameter implements FilledParameter {

    private Class<?> listType;
    private int minimalLength = -1;
    private int maximalLength = -1;
    private boolean multiline = false;
    private String regex = "";
    private Class<?> parameterType;
    private Class<?> optionalType;
    private String name;

    public NormalFilledParameter(String name, Class<?> optional, boolean multiline, Class<?> listType, int minimalLength, int maximalLength, String regex, Class<?> parameterType) {
        this.name = name;
        this.optionalType = optional;
        this.listType = listType;
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
        return optionalType != null;
    }

    public Class<?> getOptionalType(){
        return optionalType;
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

    public Class<?> getListType() {
        return listType;
    }

    public String getName() {
        return name;
    }
}
