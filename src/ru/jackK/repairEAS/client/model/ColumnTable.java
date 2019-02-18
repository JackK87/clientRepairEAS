package ru.jackK.repairEAS.client.model;

public class ColumnTable {
    private String name;

    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setType(String type) {
        this.type = type;
    }

    private String type;

    public ColumnTable(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return getName();
    }
}
