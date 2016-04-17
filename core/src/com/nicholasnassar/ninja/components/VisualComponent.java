package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.IntMap;

public class VisualComponent implements Component {
    private final IntMap<Animation> animations;

    private final TextureRegion region;

    public VisualComponent(TextureRegion region) {
        this.region = region;

        animations = null;
    }

    public VisualComponent(IntMap<Animation> animations) {
        this.animations = animations;

        region = null;
    }

    public TextureRegion getRegion() {
        if (region == null) {
            return animations.get(0).getKeyFrame(0);
        }

        return region;
    }

    public TextureRegion getRegion(int state, float elapsedTime) {
        return animations.get(state).getKeyFrame(elapsedTime);
    }

    public boolean isAnimation() {
        return animations != null;
    }
}
