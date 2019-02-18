package ru.jackK.repairEAS.client.model;

public class RowTable {
    public ColumnTable column;
    public String value;

    public String getValue() {
        return value;
    }
    public ColumnTable getColumn() {
        return column;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public void setColumn(ColumnTable column) {
        this.column = column;
    }
}
