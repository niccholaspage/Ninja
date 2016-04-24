package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

public class VisualComponent implements Component {
    private final IntMap<Array<Animation>> animations;

    private final TextureRegion region;

    private float rotation;

    private final float rotationSpeed;

    private int currentAnimation;

    public VisualComponent(TextureRegion region) {
        this(region, -1);
    }

    public VisualComponent(TextureRegion region, float rotationSpeed) {
        this.region = region;

        animations = null;

        if (rotationSpeed != -1) {
            rotation = 360;
        } else {
            rotation = -1;
        }

        currentAnimation = 0;

        this.rotationSpeed = rotationSpeed;
    }

    public VisualComponent(IntMap<Array<Animation>> animations) {
        this.animations = animations;

        region = null;

        rotation = -1;

        rotationSpeed = -1;
    }

    public Animation getAnimation(int state) {
        return animations.get(state).get(currentAnimation);
    }

    public TextureRegion getRegion() {
        if (region == null) {
            return animations.get(0).get(0).getKeyFrame(0);
        }

        return region;
    }

    public TextureRegion getRegion(int state, float elapsedTime) {
        return animations.get(state).get(currentAnimation).getKeyFrame(elapsedTime);
    }

    public boolean isAnimation() {
        return animations != null;
    }

    public float getRotation() {
        return rotation;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void randomize(int state) {
        currentAnimation = (int) (Math.random() * animations.get(state).size);
    }
}
