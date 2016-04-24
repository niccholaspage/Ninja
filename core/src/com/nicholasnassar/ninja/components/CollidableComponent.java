package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;

public class CollidableComponent implements Component {
    private final boolean destroy;

    private boolean onWall;

    public CollidableComponent() {
        this(false);
    }

    public CollidableComponent(boolean destroy) {
        this.destroy = destroy;

        onWall = false;
    }

    public boolean shouldDestroy() {
        return destroy;
    }

    public boolean isOnWall() {
        return onWall;
    }

    public void setOnWall(boolean onWall) {
        this.onWall = onWall;
    }
}
