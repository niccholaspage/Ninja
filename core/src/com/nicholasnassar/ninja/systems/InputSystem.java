package com.nicholasnassar.ninja.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.nicholasnassar.ninja.ControlManager;
import com.nicholasnassar.ninja.components.*;
import com.nicholasnassar.ninja.screens.GameScreen;

public class InputSystem extends IteratingSystem {
    private final GameScreen screen;

    private final ComponentMapper<PhysicsComponent> physicsMapper;

    private final ComponentMapper<SpeedComponent> speedMapper;

    private final ComponentMapper<JumpComponent> jumpMapper;

    private final ComponentMapper<GravityComponent> gravityMapper;

    private final ComponentMapper<LevelEditorComponent> levelMapper;

    private final ComponentMapper<DirectionComponent> directionMapper;

    private final ComponentMapper<StateComponent> stateMapper;

    private final ComponentMapper<CollidableComponent> collideMapper;

    public InputSystem(GameScreen screen) {
        super(Family.all(PhysicsComponent.class, SpeedComponent.class, ControllableComponent.class).get());

        this.screen = screen;

        physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);

        speedMapper = ComponentMapper.getFor(SpeedComponent.class);

        jumpMapper = ComponentMapper.getFor(JumpComponent.class);

        gravityMapper = ComponentMapper.getFor(GravityComponent.class);

        levelMapper = ComponentMapper.getFor(LevelEditorComponent.class);

        directionMapper = ComponentMapper.getFor(DirectionComponent.class);

        stateMapper = ComponentMapper.getFor(StateComponent.class);

        collideMapper = ComponentMapper.getFor(CollidableComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (!screen.canProcess(entity)) {
            return;
        }

        PhysicsComponent physics = physicsMapper.get(entity);

        SpeedComponent speed = speedMapper.get(entity);

        GravityComponent gravity = gravityMapper.get(entity);

        float force = speed.getSpeed();

        boolean stop = true;

        Vector2 velocity = physics.getVelocity();

        CollidableComponent collide = collideMapper.get(entity);

        physics.setLockVelocityX(physics.getLockVelocityX() - deltaTime);

        if (!physics.isXVelocityLocked()) {
            if (Gdx.input.isKeyPressed(ControlManager.MOVE_LEFT)) {
                velocity.x = -force;

                stop = false;
            }

            if (Gdx.input.isKeyPressed(ControlManager.MOVE_RIGHT)) {
                velocity.x = force;

                stop = false;
            }

            if (Gdx.input.isKeyPressed(ControlManager.MOVE_LEFT) && Gdx.input.isKeyPressed(ControlManager.MOVE_RIGHT)) {
                stop = true;
            }
        } else if (gravity != null && gravity.isGrounded()) {
            physics.setLockVelocityX(0);
        } else if (collide != null && collide.isOnWall()) {
            physics.setLockVelocityX(0.1f);
        }

        if (!levelMapper.has(entity) && Gdx.input.isKeyJustPressed(ControlManager.THROW)) {
            Vector3 position = physics.getPosition();

            screen.getSpawner().spawnShuriken(position.x, position.y, directionMapper.get(entity).getDirection());
        }

        if (levelMapper.has(entity)) {
            if (Gdx.input.isKeyPressed(ControlManager.MOVE_UP)) {
                velocity.y = force;
            } else if (Gdx.input.isKeyPressed(ControlManager.MOVE_DOWN)) {
                velocity.y = -force;
            } else {
                velocity.y = 0;
            }
        }

        if (stop && !physics.isXVelocityLocked()) {
            velocity.x = 0;
        }

        StateComponent state = stateMapper.get(entity);

        if (state != null && state.getState() == StateComponent.STATE_WALKING &&
                Gdx.input.isKeyJustPressed(ControlManager.ROLL)) {
            state.setState(StateComponent.STATE_GROUND_ROLL);
        }

        if (state != null) {
            state.setCanSlide(Gdx.input.isKeyPressed(ControlManager.MOVE_LEFT) || Gdx.input.isKeyPressed(ControlManager.MOVE_RIGHT));
        }

        JumpComponent jump = jumpMapper.get(entity);

        if (gravity != null && jump != null) {
            boolean wallSliding = state != null && state.getState() == StateComponent.STATE_WALL_SLIDE;

            if (Gdx.input.isKeyJustPressed(ControlManager.JUMP) && (gravity.isGrounded() || jump.getAvailableJumps() > 0 || wallSliding)) {
                if (wallSliding && !Gdx.input.isKeyPressed(ControlManager.MOVE_UP)) {
                    if (velocity.x != 0) {
                        if (velocity.x > 0) {
                            velocity.x = -force * 1.5f;
                        } else if (velocity.x < 0) {
                            velocity.x = force * 1.5f;
                        }
                        physics.setLockVelocityX(Float.MAX_VALUE);
                    }
                }

                velocity.y = jump.getJumpHeight();

                jump.setAvailableJumps(jump.getAvailableJumps() - 1);

                if (state != null) {
                    state.setElapsedTime(0);
                }
            }
        }
    }
}
