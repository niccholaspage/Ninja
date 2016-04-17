package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class PhysicsComponent implements Component {
    private final Vector2 position;

    private final float depth;

    private final Vector2 velocity;

    private final float width;

    private final float height;

    public PhysicsComponent(float x, float y, float width, float height) {
        this(x, y, 0, width, height);
    }

    public PhysicsComponent(float x, float y, float depth, float width, float height) {
        position = new Vector2(x, y);

        this.depth = depth;

        velocity = new Vector2();

        this.width = width;

        this.height = height;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getDepth() {
        return depth;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
