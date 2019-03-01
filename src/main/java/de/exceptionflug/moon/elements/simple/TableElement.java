package de.exceptionflug.moon.elements.simple;

import de.exceptionflug.moon.DomElement;

import java.util.ArrayList;
import java.util.List;

public class TableElement extends DomElement {

    private Row tableHead;
    private List<Row> rows = new ArrayList<>();

    public TableElement(final Row tableHead) {
        this.tableHead = tableHead;
    }

    public List<Row> getRows() {
        return rows;
    }

    public Row getTableHead() {
        return tableHead;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("<table><tr>");
        for(final DomElement element : tableHead.getValues()) {
            builder.append("<th>").append(element).append("</th>");
        }
        builder.append("</tr>");
        for(final Row row : rows) {
            builder.append("<tr>");
            for(final DomElement element : row.getValues()) {
                builder.append("<td>").append(element).append("</td>");
            }
            builder.append("</tr>");
        }
        builder.append("</table>");
        return builder.toString();
    }

    public static class Row {

        private List<DomElement> values = new ArrayList<>();

        public Row(final List<DomElement> values) {
            this.values = values;
        }

        public Row() {
        }

        public List<DomElement> getValues() {
            return values;
        }

        public void setValues(final List<DomElement> values) {
            this.values = values;
        }
    }

}
