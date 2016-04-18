package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;

public class CollidableComponent implements Component {
    private final boolean destroy;

    public CollidableComponent() {
        this(false);
    }

    public CollidableComponent(boolean destroy) {
        this.destroy = destroy;
    }

    public boolean shouldDestroy() {
        return destroy;
    }
}
