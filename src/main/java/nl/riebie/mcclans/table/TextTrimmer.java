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

package nl.riebie.mcclans.table;

import org.spongepowered.api.text.Text;

import java.util.List;

/**
 * Created by riebie on 06/08/2016.
 */
public class TextTrimmer {
    private final Text input;
    private final double spacesToTrim;

    private Text.Builder output = Text.builder();
    private double totalLength;

    public TextTrimmer(Text input, double spacesToTrim) {
        this.input = input;
        this.spacesToTrim = spacesToTrim;
    }

    public Text trim() {
        handleTextNode(input);
        return output.build();
    }

    private void handleTextNode(Text value){
        List<Text> children = value.getChildren();
        if (children.size() > 0) {
            for (Text child : children) {
                if (totalLength >= spacesToTrim) {
                    break;
                }
                handleTextNode(child);
            }
        } else {
            appendText(value);
        }
    }

    private void appendText(Text value){
        String plainText = value.toPlain();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < plainText.length(); i++) {
            char c = plainText.charAt(i);
            totalLength += getSpacesForChar(c);
            if (totalLength > spacesToTrim - 1.5f) {
                stringBuilder.append("..");
                break;
            }
            stringBuilder.append(c);
        }
        output.append(Text.builder(stringBuilder.toString()).color(value.getColor()).build());
    }

    private static float getSpacesForChar(char c) {
        if (c == 'f' || c == 'k' || c == '{' || c == '}' || c == '<' || c == '>' || c == '(' || c == ')') {
            return 1.25f;
        } else if (c == 'l') {
            return 0.75f;
        } else if (c == 'i' || c == ',' || c == '.' || c == ':' || c == ';' || c == '!') {
            return 0.5f;
        } else if (c == ' ' || c == 'I' || c == '[' || c == ']' || c == 't') {
            return 1f;
        } else {
            return 1.5f;
        }
    }

}
