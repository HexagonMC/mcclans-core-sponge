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

package nl.riebie.mcclans.config.model;

import nl.riebie.mcclans.config.constraints.ColorConstraint;
import nl.riebie.mcclans.config.constraints.MaximumNumberConstraint;
import nl.riebie.mcclans.config.constraints.MinimumNumberConstraint;
import nl.riebie.mcclans.config.constraints.OneOfStringConstraint;
import nl.riebie.mcclans.config.types.*;
import nl.riebie.mcclans.utils.MessageBoolean;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Kippers on 22/12/2015.
 */
public class ConfigOption {

    public String key;
    public String comment;
    public Object value;
    public Object valueIfConstraintFailed;
    private Type mType;
    private List<Constraint> mConstraints;

    public ConfigOption(String key, @Nullable String comment, Object value, Object valueIfConstraintFailed, Type type, List<Constraint> constraints) {
        this.key = key;
        this.comment = comment;
        this.value = value;
        this.valueIfConstraintFailed = valueIfConstraintFailed;
        this.mType = type;
        this.mConstraints = constraints;
    }

    public MessageBoolean isOfType(Object value) {
        MessageBoolean isOfType = new MessageBoolean();
        isOfType.bool = mType.isOfType(value);
        if (!isOfType.bool) {
            isOfType.message = mType.getTypeDescription();
        }
        return isOfType;
    }

    public MessageBoolean meetsConstraints(Object value) {
        MessageBoolean meetsConstraint = new MessageBoolean();
        meetsConstraint.bool = true;
        for (Constraint constraint : mConstraints) {
            if (!constraint.meetsConstraint(value)) {
                meetsConstraint.bool = false;
                meetsConstraint.message = constraint.getConstraintDescription();
                return meetsConstraint;
            }
        }
        return meetsConstraint;
    }

    public boolean hasComment() {
        return comment != null && comment.length() != 0;
    }

    public static Builder builder(String key, Object value) {
        return new Builder(key, value);
    }

    public static class Builder {
        private String mKey;
        private String mComment = "";
        private Object mValue;
        private Object mValueIfConstraintFailed;
        private Type mType;
        private List<Constraint> mConstraints = new ArrayList<>();

        private Builder(String key, Object value) {
            this.mKey = key;
            this.mValue = value;
            this.mType = Type.inferType(value);
        }

        public Builder setComment(@Nullable String comment) {
            this.mComment = comment;
            return this;
        }

        public Builder setValueIfConstraintFailed(@Nullable Object value) {
            mValueIfConstraintFailed = value;
            return this;
        }

        public Builder addMinimumNumberConstraint(int min) {
            mConstraints.add(new MinimumNumberConstraint(min));
            return this;
        }

        public Builder addMinimumNumberConstraint(double min) {
            mConstraints.add(new MinimumNumberConstraint(min));
            return this;
        }

        public Builder addMaximumNumberConstraint(int max) {
            mConstraints.add(new MaximumNumberConstraint(max));
            return this;
        }

        public Builder addMaximumNumberConstraint(double max) {
            mConstraints.add(new MaximumNumberConstraint(max));
            return this;
        }

        public Builder addOneOfStringConstraint(boolean caseSensitive, String... possibilities) {
            mConstraints.add(new OneOfStringConstraint(caseSensitive, possibilities));
            return this;
        }

        public Builder addColorConstraint() {
            mConstraints.add(new ColorConstraint());
            return this;
        }

        public Builder addConstraints(Constraint... constraints) {
            mConstraints.addAll(Arrays.asList(constraints));
            return this;
        }

        public ConfigOption build() {
            return new ConfigOption(mKey, mComment, mValue, (mValueIfConstraintFailed == null) ? mValue : mValueIfConstraintFailed, mType, mConstraints);
        }
    }

    public interface Constraint {

        boolean meetsConstraint(Object value);

        String getConstraintDescription();
    }

    public static abstract class Type {
        public static final Type BOOLEAN_TYPE = new BooleanType();
        public static final Type DOUBLE_TYPE = new DoubleType();
        public static final Type INTEGER_TYPE = new IntegerType();
        public static final Type STRING_TYPE = new StringType();
        public static final Type LIST_TYPE = new ListType();
        public static final Type MAP_TYPE = new MapType();
        public static final Type UNKNOWN_TYPE = new UnknownType();

        public abstract boolean isOfType(Object value);

        public abstract String getTypeDescription();

        private static Type inferType(Object value) {
            if (value instanceof Integer) {
                return Type.INTEGER_TYPE;
            } else if (value instanceof Double) {
                return Type.DOUBLE_TYPE;
            } else if (value instanceof Boolean) {
                return Type.BOOLEAN_TYPE;
            } else if (value instanceof String) {
                return Type.STRING_TYPE;
            } else if (value instanceof List) {
                return Type.LIST_TYPE;
            } else if (value instanceof Map) {
                return Type.MAP_TYPE;
            } else {
                return Type.UNKNOWN_TYPE;
            }
        }
    }
}
