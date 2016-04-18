package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PhysicsComponent implements Component {
    private final Vector3 position;

    private final Vector2 velocity;

    private final float width;

    private final float height;

    private final float radius;

    public PhysicsComponent(float x, float y, float width, float height) {
        this(x, y, 0, width, height);
    }

    public PhysicsComponent(float x, float y, float z, float width, float height) {
        position = new Vector3(x, y, z);

        velocity = new Vector2();

        this.width = width;

        this.height = height;

        radius = -1;
    }

    public PhysicsComponent(float x, float y, float z, float radius, boolean circle) {
        position = new Vector3(x, y, z);

        velocity = new Vector2();

        width = -1;

        height = -1;

        this.radius = radius;
    }

    public Vector3 getPosition() {
        return position;
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

    public float getRadius() {
        return radius;
    }
}
