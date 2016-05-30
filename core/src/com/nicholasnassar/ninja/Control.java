package com.nicholasnassar.ninja;

public class Control {
    private final String name;

    private final int defaultKey;

    private int key;

    public Control(String name, int defaultKey) {
        this.name = name;

        this.defaultKey = defaultKey;

        key = defaultKey;
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
