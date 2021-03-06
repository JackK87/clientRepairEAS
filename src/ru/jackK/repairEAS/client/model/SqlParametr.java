package ru.jackK.repairEAS.client.model;

public class SqlParametr {

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String type;
    private String value;

    public SqlParametr(String type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
