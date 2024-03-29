package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PhysicsComponent implements Component {
    private final Vector3 position;

    private final Vector2 velocity;

    private float width;

    private float height;

    private final float radius;

    private float sizeScale;

    private float lockVelocityX;

    public PhysicsComponent(float x, float y, float width, float height) {
        this(x, y, 0, width, height);
    }

    public PhysicsComponent(float x, float y, float z, float width, float height) {
        position = new Vector3(x, y, z);

        velocity = new Vector2();

        this.width = width;

        this.height = height;

        radius = -1;

        sizeScale = 1;

        lockVelocityX = 0;
    }

    public PhysicsComponent(float x, float y, float z, float radius, boolean circle) {
        position = new Vector3(x, y, z);

        velocity = new Vector2();

        width = -1;

        height = -1;

        this.radius = radius;

        sizeScale = 1;

        lockVelocityX = 0;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getWidth() {
        return width * sizeScale;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getHeight() {
        return height * sizeScale;
    }

    public float getRadius() {
        return radius;
    }

    public void setSizeScale(float sizeScale) {
        this.sizeScale = sizeScale;
    }

    public float getSizeScale() {
        return sizeScale;
    }

    public boolean isXVelocityLocked() {
        return lockVelocityX > 0;
    }

    public float getLockVelocityX() {
        return lockVelocityX;
    }

    public void setLockVelocityX(float lockVelocityX) {
        if (lockVelocityX < 0) {
            lockVelocityX = 0;
        }

        this.lockVelocityX = lockVelocityX;
    }
}
