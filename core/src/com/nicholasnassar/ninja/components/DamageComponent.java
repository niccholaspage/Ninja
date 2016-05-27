package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.IntIntMap;

public class DamageComponent implements Component {
    private final IntIntMap damage;

    public static final int ON_COLLIDE = 0;

    public DamageComponent(int... damages) {
        damage = new IntIntMap();

        for (int i = 0; i < damages.length; i += 2) {
            damage.put(damages[i], damages[i + 1]);
        }
    }

    public int getDamage(int when) {
        return damage.get(when, -1);
    }

    public boolean when(int when) {
        return damage.containsKey(when);
    }
}
