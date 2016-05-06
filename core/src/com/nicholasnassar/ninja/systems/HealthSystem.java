package com.nicholasnassar.ninja.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.nicholasnassar.ninja.components.HealthComponent;

public class HealthSystem extends IteratingSystem {
    private final ComponentMapper<HealthComponent> healthMapper;

    public HealthSystem() {
        super(Family.all(HealthComponent.class).get());

        healthMapper = ComponentMapper.getFor(HealthComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HealthComponent health = healthMapper.get(entity);

        if (health.isDead()) {
            getEngine().removeEntity(entity);
        }
    }
}
