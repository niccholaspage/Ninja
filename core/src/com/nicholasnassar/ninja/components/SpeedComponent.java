package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;

public class SpeedComponent implements Component {
    private final float speed;

    public SpeedComponent(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }
}
