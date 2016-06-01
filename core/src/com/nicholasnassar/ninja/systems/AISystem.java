package com.nicholasnassar.ninja.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.nicholasnassar.ninja.components.*;
import com.nicholasnassar.ninja.screens.GameScreen;

public class AISystem extends IteratingSystem {
    private final GameScreen screen;

    private final ComponentMapper<AIComponent> aiMapper;

    private final ComponentMapper<PhysicsComponent> physicsMapper;

    private final ComponentMapper<SpeedComponent> speedMapper;

    private final ComponentMapper<JumpComponent> jumpMapper;

    private final ComponentMapper<GravityComponent> gravityMapper;

    private final ComponentMapper<CooldownComponent> cooldownMapper;

    private final ComponentMapper<StateComponent> stateMapper;

    private final ComponentMapper<DirectionComponent> directionMapper;

    public AISystem(GameScreen screen) {
        super(Family.all(AIComponent.class, PhysicsComponent.class, SpeedComponent.class, JumpComponent.class).get());

        this.screen = screen;

        aiMapper = ComponentMapper.getFor(AIComponent.class);

        physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);

        speedMapper = ComponentMapper.getFor(SpeedComponent.class);

        jumpMapper = ComponentMapper.getFor(JumpComponent.class);

        gravityMapper = ComponentMapper.getFor(GravityComponent.class);

        cooldownMapper = ComponentMapper.getFor(CooldownComponent.class);

        stateMapper = ComponentMapper.getFor(StateComponent.class);

        directionMapper = ComponentMapper.getFor(DirectionComponent.class);
    }

    @Override
    protected void processEntity(final Entity entity, float deltaTime) {
        AIComponent ai = aiMapper.get(entity);

        ai.setLastAction(ai.getLastAction() - deltaTime);

        if (ai.getLastAction() > 0) {
            return;
        }

        ai.setLastAction((float) (Math.random() * 0.5) + 0.3f);

        PhysicsComponent physics = physicsMapper.get(entity);

        Vector2 velocity = physics.getVelocity();

        SpeedComponent speed = speedMapper.get(entity);

        GravityComponent gravity = gravityMapper.get(entity);

        int roll = (int) (Math.random() * 3);

        if (roll == 0) {
            velocity.x = 0;
        } else if (roll == 1) {
            velocity.x = -speed.getSpeed();
        } else if (roll == 2) {
            velocity.x = speed.getSpeed();
        }

        if (Math.random() < 0.2f && gravity.isGrounded()) {
            velocity.y = jumpMapper.get(entity).getJumpHeight();
        }

        if (Math.random() < 0.4f && cooldownMapper.get(entity).canUse(CooldownComponent.THROW)) {
            StateComponent state = stateMapper.get(entity);

            final Vector3 position = physics.getPosition();

            getEngine().getSystem(RunnableSystem.class).postRunnable(new Runnable() {
                @Override
                public void run() {
                    screen.getSpawner().spawnShuriken(position.x, position.y + 0.5f, 1.5f, directionMapper.get(entity).getDirection());
                }
            }, 0.35f);

            state.setState(StateComponent.STATE_THROW);

            cooldownMapper.get(entity).addCooldown(CooldownComponent.THROW, 1);
        }
    }
}
