package com.nicholasnassar.ninja.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.IntMap;
import com.nicholasnassar.ninja.components.CooldownComponent;

import java.util.Iterator;

public class CooldownSystem extends IteratingSystem {
    private final ComponentMapper<CooldownComponent> cooldownMapper;

    public CooldownSystem() {
        super(Family.all(CooldownComponent.class).get());

        cooldownMapper = ComponentMapper.getFor(CooldownComponent.class);
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        CooldownComponent cooldown = cooldownMapper.get(entity);

        Iterator<IntMap.Entry<Float>> iterator = cooldown.getCooldowns().iterator();

        while (iterator.hasNext()) {
            IntMap.Entry<Float> entry = iterator.next();

            float value = entry.value - deltaTime;

            if (value <= 0) {
                iterator.remove();
            } else {
                cooldown.getCooldowns().put(entry.key, value);
            }
        }
    }
}
