package com.nicholasnassar.ninja;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.nicholasnassar.ninja.components.PhysicsComponent;

import java.util.Comparator;

public class DepthComparator implements Comparator<Entity> {
    @Override
    public int compare(Entity entity, Entity entity2) {
        ComponentMapper<PhysicsComponent> physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);

        PhysicsComponent physics = physicsMapper.get(entity);

        PhysicsComponent physics2 = physicsMapper.get(entity2);

        return (int) (physics.getDepth() - physics2.getDepth());
    }
}
