package com.nicholasnassar.ninja.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.nicholasnassar.ninja.components.DirectionComponent;
import com.nicholasnassar.ninja.components.GravityComponent;
import com.nicholasnassar.ninja.components.PhysicsComponent;
import com.nicholasnassar.ninja.components.StateComponent;

public class StateSystem extends IteratingSystem {
    private final ComponentMapper<PhysicsComponent> physicsMapper;

    private final ComponentMapper<StateComponent> stateMapper;

    private final ComponentMapper<DirectionComponent> directionMapper;

    private final ComponentMapper<GravityComponent> gravityMapper;

    public StateSystem() {
        super(Family.all(PhysicsComponent.class).one(StateComponent.class, DirectionComponent.class).get());

        physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);

        stateMapper = ComponentMapper.getFor(StateComponent.class);

        directionMapper = ComponentMapper.getFor(DirectionComponent.class);

        gravityMapper = ComponentMapper.getFor(GravityComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent state = stateMapper.get(entity);

        DirectionComponent direction = directionMapper.get(entity);

        PhysicsComponent physics = physicsMapper.get(entity);

        GravityComponent gravity = gravityMapper.get(entity);

        Vector2 velocity = physics.getVelocity();

        if (state != null) {
            if (gravity != null && !gravity.isGrounded()) {
                state.setState(StateComponent.STATE_IN_AIR);
            } else if (velocity.x == 0) {
                state.setState(StateComponent.STATE_IDLE);
            } else {
                state.setState(StateComponent.STATE_WALKING);
            }
        }

        if (direction != null) {
            if (velocity.x < 0) {
                direction.setDirection(DirectionComponent.LEFT);
            } else if (velocity.x > 0) {
                direction.setDirection(DirectionComponent.RIGHT);
            }
        }
    }
}
