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

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VerticalTable {
    private int pageSize;
    private List<HashMap<String, Text>> rows = new ArrayList<>();
    private List<String> headers = new ArrayList<>();
    private String tableName;

    private List<Double> rowSizes = new ArrayList<>();

    private boolean holographic;
    private double largestRow;

    public VerticalTable(String tableName, int pageSize) {
        this.tableName = tableName;
        this.pageSize = pageSize;
        this.rows.add(new HashMap<String, Text>());
    }

    public VerticalTable(boolean holographic, String tableName, int pageSize) {
        this.tableName = tableName;
        this.pageSize = pageSize;
        this.rows.add(new HashMap<String, Text>());
        this.holographic = holographic;
    }

    public void setValue(String column, Text value) {
        if (!headers.contains(column)) {
            headers.add(column);
            rows.get(0).put(column, value);
        }
    }

    public void draw(CommandSource sender, int page) {
        rowSizes.clear();

        List<String> compareHeaders = new ArrayList<>(headers);
        java.util.Collections.sort(compareHeaders, new HeaderComparator());
        double numberOfSpaces = getSpaces(Text.of(compareHeaders.get(0))) + 5;
        List<Text> message = new ArrayList<>();

        for (String header : headers) {
            Text drawHeader = Text.builder().color(TextColors.DARK_GREEN).append(Text.join(Text.of(header), Text.of(": "))).toText();
            double betweenSpaces = numberOfSpaces - getSpaces(drawHeader);
            Text spaces = getSpacesString(betweenSpaces);


            Text row = Text.join(drawHeader, spaces, rows.get(0).get(header));
            double totalSize = numberOfSpaces + getSpaces(rows.get(0).get(header));
            rowSizes.add(totalSize);
            if (totalSize > largestRow) {
                largestRow = totalSize;
            }

            message.add(row);
        }

        if (holographic) {
            message = fixAlignCenter(message);
        }

        sender.sendMessage(Text.of(""));
        sender.sendMessage(Text.join(Text.builder().color(TextColors.DARK_GRAY).append(Text.of("== ")).toText(), Text.of(this.tableName), Text.builder().color(TextColors.DARK_GRAY).append(Text.of(" ==")).toText()));
        sender.sendMessage(Text.of(""));
        sender.sendMessages(message.toArray(new Text[message.size()]));
    }

    private Text getSpacesString(double numberOfSpaces) {
        Text.Builder spaces = Text.builder();
        if (Math.floor(numberOfSpaces) == 0) {
            return Text.EMPTY;
        }
        double x = numberOfSpaces - Math.floor(numberOfSpaces);
        int numberOfSpecialSpaces = (int) (x / 0.25);
        int numberOfNormalSpaces = (int) (Math.floor(numberOfSpaces) - numberOfSpecialSpaces);
        if (numberOfNormalSpaces < 0) {
            if (numberOfSpecialSpaces == 3) {
                numberOfSpecialSpaces = 0;
                numberOfNormalSpaces = (int) Math.ceil(numberOfSpaces);
            } else {
                numberOfSpecialSpaces += numberOfNormalSpaces;
                numberOfNormalSpaces = 0;
            }
        }
        for (int k = 0; k < numberOfNormalSpaces; k++) {
            spaces.append(Text.of(" "));
        }

        for (int l = 0; l < numberOfSpecialSpaces; l++) {
            spaces.append(Text.builder().style(TextStyles.BOLD).append(Text.of(" ")).build());
        }
        return spaces.toText();
    }

    private List<Text> fixAlignCenter(List<Text> message) {
        List<Text> adjustedMessage = new ArrayList<>();
        for (int j = 0; j < message.size(); j++) {
            Text column = message.get(j);
            double spaces = rowSizes.get(j);

            adjustedMessage.add(Text.join(column, getSpacesString(largestRow - spaces)));
        }

        adjustedMessage.add(Text.builder().color(TextColors.DARK_GRAY).append(Text.of("=*=")).toText());
        return adjustedMessage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    private static double getSpaces(Text string) {
        double spaces = 0;

        for (int i = 0; i < string.toPlain().length(); i++) {
            char c = string.toPlain().charAt(i);
            spaces += getSpacesForChar(c);
        }
        return spaces;
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