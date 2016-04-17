package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;

public class DirectionComponent implements Component {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;

    private int direction;

    public DirectionComponent() {
        direction = RIGHT;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
