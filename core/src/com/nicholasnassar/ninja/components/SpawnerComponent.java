package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;

public class SpawnerComponent implements Component {
    private final String creature;

    public SpawnerComponent(String creature) {
        this.creature = "creature_" + creature;
    }

    public String getCreature() {
        return creature;
    }
}
