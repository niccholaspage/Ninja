package com.nicholasnassar.ninja.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.nicholasnassar.ninja.ControlManager;
import com.nicholasnassar.ninja.MobileInput;
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

    private final ComponentMapper<CooldownComponent> cooldownMapper;

    private final MobileInput mobileInput;

    public InputSystem(GameScreen screen, MobileInput mobileInput) {
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

        cooldownMapper = ComponentMapper.getFor(CooldownComponent.class);

        this.mobileInput = mobileInput;
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

        boolean moveLeft = Gdx.input.isKeyPressed(ControlManager.MOVE_LEFT);

        boolean moveRight = Gdx.input.isKeyPressed(ControlManager.MOVE_RIGHT);

        boolean moveUp = Gdx.input.isKeyPressed(ControlManager.MOVE_UP);

        boolean moveDown = Gdx.input.isKeyPressed(ControlManager.MOVE_DOWN);

        boolean throwPressed = Gdx.input.isKeyJustPressed(ControlManager.THROW);

        boolean jumpPressed = Gdx.input.isKeyJustPressed(ControlManager.JUMP);

        boolean rollPressed = Gdx.input.isKeyJustPressed(ControlManager.ROLL);

        if (mobileInput != null) {
            moveLeft = mobileInput.isLeftDown();

            moveRight = mobileInput.isRightDown();

            moveUp = mobileInput.isUpDown();

            moveDown = rollPressed = mobileInput.isDownDown();

            throwPressed = mobileInput.isThrowPressed();

            jumpPressed = mobileInput.isJumpPressed();

            mobileInput.reset();
        }

        StateComponent state = stateMapper.get(entity);

        final DirectionComponent direction = directionMapper.get(entity);

        if (!physics.isXVelocityLocked()) {
            boolean rolling = state != null && state.getState() == StateComponent.STATE_GROUND_ROLL;

            boolean left = direction != null && direction.getDirection() == DirectionComponent.LEFT;

            if (moveLeft) {
                velocity.x = -force;

                stop = false;
            }

            if (rolling && left) {
                velocity.x = -force * 3;

                stop = false;
            }

            if (moveRight) {
                velocity.x = force;

                stop = false;
            }

            if (rolling && !left) {
                velocity.x = force * 3;

                stop = false;
            }

            if (!rolling && moveLeft && moveRight) {
                stop = true;
            }
        } else if (gravity != null && gravity.isGrounded()) {
            physics.setLockVelocityX(0);
        } else if (collide != null && collide.isOnWall()) {
            physics.setLockVelocityX(0.1f);
        }

        if (!levelMapper.has(entity) && throwPressed && cooldownMapper.get(entity).canUse(CooldownComponent.THROW)) {
            final Vector3 position = physics.getPosition();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    long time = System.currentTimeMillis();

                    while (System.currentTimeMillis() < time + 350) {
                    }
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            screen.getSpawner().spawnShuriken(position.x, position.y + 0.5f, direction.getDirection());
                        }
                    });
                }
            }).start();

            state.setState(StateComponent.STATE_THROW);

            cooldownMapper.get(entity).addCooldown(CooldownComponent.THROW, 1);
        }

        if (levelMapper.has(entity)) {
            if (moveUp) {
                velocity.y = force;
            } else if (moveDown) {
                velocity.y = -force;
            } else {
                velocity.y = 0;
            }
        }

        if (stop && !physics.isXVelocityLocked()) {
            velocity.x = 0;
        }

        if (state != null && state.getState() == StateComponent.STATE_WALKING && rollPressed && cooldownMapper.get(entity).canUse(CooldownComponent.ROLL)) {
            cooldownMapper.get(entity).addCooldown(CooldownComponent.ROLL, 0.5f);

            state.setState(StateComponent.STATE_GROUND_ROLL);
        }

        if (state != null) {
            state.setCanSlide(moveLeft || moveRight);
        }

        JumpComponent jump = jumpMapper.get(entity);

        if (gravity != null && jump != null) {
            boolean wallSliding = state != null && state.getState() == StateComponent.STATE_WALL_SLIDE;

            if (jumpPressed && (gravity.isGrounded() || jump.getAvailableJumps() > 0 || wallSliding)) {
                if (wallSliding && !moveUp) {
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
