package com.nicholasnassar.ninja.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.nicholasnassar.ninja.Control;
import com.nicholasnassar.ninja.MobileInput;
import com.nicholasnassar.ninja.OptionsManager;
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

    private final Control moveLeft, moveRight, moveUp, moveDown, throwC, jump, roll;

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

        moveLeft = OptionsManager.getControl("move_left");
        moveRight = OptionsManager.getControl("move_right");
        moveUp = OptionsManager.getControl("move_up");
        moveDown = OptionsManager.getControl("move_down");
        throwC = OptionsManager.getControl("throw");
        jump = OptionsManager.getControl("jump");
        roll = OptionsManager.getControl("roll");
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

        boolean moveLeft = OptionsManager.isPressed(this.moveLeft);
        boolean moveRight = OptionsManager.isPressed(this.moveRight);
        boolean moveUp = OptionsManager.isPressed(this.moveUp);
        boolean moveDown = OptionsManager.isPressed(this.moveDown);
        boolean throwPressed = OptionsManager.isJustPressed(throwC);
        boolean jumpPressed = OptionsManager.isJustPressed(jump);
        boolean rollPressed = OptionsManager.isJustPressed(roll);

        if (mobileInput != null) {
            moveLeft = moveLeft || mobileInput.isLeftDown();

            moveRight = moveRight || mobileInput.isRightDown();

            moveUp = moveUp || mobileInput.isUpDown();

            moveDown = moveDown || mobileInput.isDownDown();

            rollPressed = rollPressed || mobileInput.isDownDown();

            throwPressed = throwPressed || mobileInput.isThrowPressed();

            jumpPressed = jumpPressed || mobileInput.isJumpPressed();

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

            getEngine().getSystem(RunnableSystem.class).postRunnable(new Runnable() {
                public void run() {
                    screen.getSpawner().spawnShuriken(position.x, position.y + 0.5f, 1, direction.getDirection());
                }
            }, 0.35f);

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

            if (jumpPressed && state.getState() != StateComponent.STATE_GROUND_ROLL && (gravity.isGrounded() || jump.getAvailableJumps() > 0 || wallSliding)) {
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
