package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;

public class CollidableComponent implements Component {
    private final boolean destroy;

    private final int exception;

    private boolean onWall;

    public static final int TILE_EXCEPTION = 0;
    public static final int CREATURE_EXCEPTION = 1;
    public static final int THROWABLE_EXCEPTION = 2;

    public CollidableComponent(int exception) {
        this(exception, false);
    }

    public CollidableComponent(int exception, boolean destroy) {
        this.exception = exception;

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

    public int getException() {
        return exception;
    }
}
