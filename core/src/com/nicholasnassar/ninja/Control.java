package com.nicholasnassar.ninja;

public class Control {
    private final String id;

    private final String name;

    private final int defaultKey;

    private int key;

    public Control(String id, String name, int defaultKey) {
        this.id = id;

        this.name = name;

        this.defaultKey = defaultKey;

        key = defaultKey;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDefaultKey() {
        return defaultKey;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
