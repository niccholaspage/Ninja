package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.IntMap;

public class CooldownComponent implements Component {
    private final IntMap<Float> cooldowns;

    public static final int THROW = 0;

    public static final int ROLL = 1;

    public CooldownComponent() {
        cooldowns = new IntMap<Float>();
    }

    public void addCooldown(int skill, float time) {
        cooldowns.put(skill, time);
    }

    public boolean canUse(int skill) {
        return !cooldowns.containsKey(skill);
    }

    public IntMap<Float> getCooldowns() {
        return cooldowns;
    }
}
