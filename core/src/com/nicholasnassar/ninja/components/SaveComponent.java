package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;

public class SaveComponent implements Component {
    private final String id;

    public SaveComponent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return id.substring(0, id.indexOf("_"));
    }

    public String getType() {
        return id.substring(id.indexOf("_") + 1);
    }
}
