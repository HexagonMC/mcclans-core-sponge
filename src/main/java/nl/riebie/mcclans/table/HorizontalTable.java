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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.riebie.mcclans.messages.Messages;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class HorizontalTable<T> {

    //input
    private int pageSize;
    private int page;
    private Text tableName;
    private Text message;
    private List<T> items;
    private CommandSource sender;

    private List<Row> rows = new ArrayList<>();
    private List<Column> columns = new ArrayList<>();

    private int size;
    private TableAdapter<T> tableAdapter;
    private Comparator<T> comparator;

    private double largestColumn;
    private double headerSpaceLength;

    private boolean closed;

    //output messages
    private List<Text> topMessage = new ArrayList<>();
    private Text headerMessage;
    private List<Text> rowMessage = new ArrayList<>();


    public HorizontalTable(String tableName, int pageSize, TableAdapter<T> adapter) {
        this.tableAdapter = adapter;
        this.tableName = Text.of(tableName);
        this.pageSize = pageSize;
    }

    public HorizontalTable(Text tableName, int pageSize, TableAdapter<T> adapter) {
        this.tableAdapter = adapter;
        this.tableName = tableName;
        this.pageSize = pageSize;
    }

    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public void setMessage(Text message) {
        this.message = message;
    }

    public void draw(List<T> items, int page, CommandSource sender) {
        this.page = page;
        this.items = items;
        this.sender = sender;
        execute();
    }

    public void defineColumn(String header, int spacing) {
        defineColumn(header, spacing, false);
    }

    public void defineColumn(String header, int spacing, boolean trim) {
        if (!closed) {
            this.columns.add(new Column(header, spacing, trim));
        }
    }

    private void draw() {
        double s = size / Double.valueOf(pageSize);
        int maxPages = (int) Math.ceil(s);

        printHeader(page, maxPages);
        for (int i = 0; i < pageSize; i++) {
            if (i < rows.size()) {
                double totalCount = 0;
                Text.Builder row = Text.builder();
                Row rowObject = rows.get(i);
                for (Column column : this.columns) {
                    Text value = rows.get(i).getValue(column.key);
                    if (column.trim) {
                        value = new TextTrimmer(value, column.spacing).trim();
                    }
                    double numberOfSpaces = column.spacing - getSpaces(value);
                    Text spaces = getSpacesString(numberOfSpaces);
                    row.append(Text.of(value, spaces));
                    totalCount += numberOfSpaces + getSpaces(value);
                }
                rowObject.setSpaceLength(totalCount);
                if (totalCount > largestColumn) {
                    largestColumn = totalCount;
                }
                rowMessage.add(row.build());
            }
        }
    }

    private void printHeader(int currentPage, int maxPages) {
        topMessage.add(Text.EMPTY);
        topMessage.add(Text.join(Text.builder().color(TextColors.DARK_GRAY).append(Text.of("== ")).build(),
                this.tableName,
                Text.builder().color(TextColors.GREEN).append(Text.of(" "), Text.of(currentPage)).build(),
                Text.builder().color(TextColors.GRAY).append(Text.of("/")).build(),
                Text.builder().color(TextColors.GREEN).append(Text.of(maxPages)).build(),
                Text.builder().color(TextColors.DARK_GRAY).append(Text.of(" ==")).build()));

        if (maxPages > 1) {
            topMessage.add(Text.builder().color(TextColors.GRAY).append(Text.of("Type /clan page <1-", maxPages, "> to scroll through pages")).build());
        }
        topMessage.add(Text.EMPTY);
        if (message != null) {
            topMessage.add(message);
            topMessage.add(Text.EMPTY);
        }
        double totalCount = 0;
        Text.Builder row = Text.builder();
        for (Column column : this.columns) {
            Text header = Text.builder(column.key + ":").color(TextColors.DARK_GREEN).build();
            double numberOfSpaces = column.spacing - getSpaces(header);
            Text spaces = getSpacesString(numberOfSpaces);
            row.color(TextColors.DARK_GREEN).append(Text.of(header)).color(TextColors.RESET).append(Text.of(spaces));
            double rowSize = (column.spacing - totalCount) + getSpaces(header);
            totalCount += rowSize;
        }
        headerSpaceLength = totalCount;
        largestColumn = totalCount;
        headerMessage = row.build();
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

        return spaces.build();
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

    private void execute() {
        if (comparator != null) {
            Collections.sort(items, comparator);
        }

        size = items.size();
        int id = page - 1;
        closed = true;
        double s = size / Double.valueOf(pageSize);
        int maxPages = (int) Math.ceil(s);
        if (id < maxPages && id >= 0) {
            for (int i = id * pageSize; i < (id * pageSize) + pageSize; i++) {
                if (i < items.size()) {
                    T item = items.get(i);
                    Row row = new Row(columns);
                    rows.add(row);
                    tableAdapter.fillRow(row, item, i);
                }
            }

            draw();
            List<Text> message = new ArrayList<>();
            message.addAll(topMessage);
            message.add(headerMessage);
            message.addAll(rowMessage);

            new MessageCallback(message, sender).send();
        } else if (maxPages == 0) {
            new MessageCallback(Messages.getWarningMessage(Messages.THIS_COMMAND_HAS_NO_INFORMATION_TO_DISPLAY), sender).send();
        } else {
            new MessageCallback(Messages.getWarningMessage(Messages.PAGE_DOES_NOT_EXIST), sender).send();
        }

    }


    class MessageCallback {

        private final List<Text> message;
        private final CommandSource sender;

        public MessageCallback(Text message, CommandSource sender) {
            this.message = new ArrayList<>();
            this.message.add(message);
            this.sender = sender;

        }

        public MessageCallback(List<Text> message, CommandSource sender) {
            this.message = message;
            this.sender = sender;
        }

        public void send() {
            sender.sendMessages(message.toArray(new Text[message.size()]));
        }
    }

}
