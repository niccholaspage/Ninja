package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;

public class DamageComponent implements Component {
    private final int damage;

    private final int when;

    public static final int ON_COLLIDE = 0;

    public DamageComponent(int damage, int when) {
        this.damage = damage;

        this.when = when;
    }

    public int getDamage() {
        return damage;
    }

    public int getWhen() {
        return when;
    }
}
