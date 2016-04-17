package com.nicholasnassar.ninja.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.nicholasnassar.ninja.Level;
import com.nicholasnassar.ninja.components.CollidableComponent;
import com.nicholasnassar.ninja.components.GravityComponent;
import com.nicholasnassar.ninja.components.LevelEditorComponent;
import com.nicholasnassar.ninja.components.PhysicsComponent;
import com.nicholasnassar.ninja.screens.GameScreen;

public class PhysicsSystem extends IteratingSystem {
    public static final float GRAVITY = 1.65f;

    public static final float MAX = 20f;

    private final GameScreen screen;

    private final ComponentMapper<PhysicsComponent> physicsMapper;

    private final ComponentMapper<GravityComponent> gravityMapper;

    private final ComponentMapper<LevelEditorComponent> levelMapper;

    private final ComponentMapper<CollidableComponent> collideMapper;

    public PhysicsSystem(GameScreen screen) {
        super(Family.all(PhysicsComponent.class).get());

        this.screen = screen;

        physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);

        gravityMapper = ComponentMapper.getFor(GravityComponent.class);

        levelMapper = ComponentMapper.getFor(LevelEditorComponent.class);

        collideMapper = ComponentMapper.getFor(CollidableComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (!screen.canProcess(entity)) {
            return;
        }

        PhysicsComponent physics = physicsMapper.get(entity);

        Vector2 position = physics.getPosition();

        Vector2 velocity = physics.getVelocity();

        GravityComponent gravity = gravityMapper.get(entity);

        if (gravity != null) {
            velocity.y -= GRAVITY;

            if (velocity.y < -MAX) {
                velocity.y = -MAX;
            }

            float newX = position.x + velocity.x * deltaTime;

            float y = position.y;

            float newY = y + velocity.y * deltaTime;

            float width = physics.getWidth();

            float height = physics.getHeight();

            for (Entity loopEntity : getEntities()) {
                if (entity == loopEntity || !collideMapper.has(loopEntity)) {
                    continue;
                }

                PhysicsComponent loopPhysics = physicsMapper.get(loopEntity);

                Vector2 pos2 = loopPhysics.getPosition();

                float x2 = pos2.x;

                float y2 = pos2.y;

                float width2 = loopPhysics.getWidth();

                float height2 = loopPhysics.getHeight();

                if (width2 > 0 && height2 > 0) {
                    if (position.x != newX && overlaps(newX, x2, y, y2, width, width2, height, height2)) {
                        if (velocity.x > 0) {
                            velocity.x = 0;

                            newX = x2 - width;
                        } else {
                            velocity.x = 0;

                            newX = position.x;
                        }

                        break;
                    }
                }
            }

            gravity.setGrounded(false);

            for (Entity loopEntity : getEntities()) {
                if (entity == loopEntity || !collideMapper.has(loopEntity)) {
                    continue;
                }

                PhysicsComponent loopPhysics = physicsMapper.get(loopEntity);

                Vector2 pos2 = loopPhysics.getPosition();

                float x2 = pos2.x;

                float y2 = pos2.y;

                float width2 = loopPhysics.getWidth();

                float height2 = loopPhysics.getHeight();

                if (width2 > 0 && height2 > 0) {
                    if (position.y != newY && overlaps(newX, x2, newY, y2, width, width2, height, height2)) {
                        if (velocity.y > 0) {
                            velocity.y = 0;

                            newY = y2 - height;
                        } else {
                            newY = y2 + height2;

                            velocity.y = 0;

                            gravity.setGrounded(true);
                        }

                        break;
                    }
                }
            }

            position.x = newX;

            position.y = newY;

            setValidConstraints(position);
        } else {
            position.add(velocity.x * deltaTime, velocity.y * deltaTime);

            setValidConstraints(position);
        }
    }

    private void setValidConstraints(Vector2 position) {
        Level level = screen.getLevel();

        float levelWidth = level.getWidth() - 1;

        float levelHeight = level.getHeight() - 1;

        float newX = position.x;

        float newY = position.y;

        if (newX < 0) {
            newX = 0;
        } else if (newX > levelWidth) {
            newX = levelWidth;
        }

        if (newY < 0) {
            newY = 0;
        } else if (newY > levelHeight) {
            newY = levelHeight;
        }

        position.set(newX, newY);
    }

    private boolean overlaps(float x, float x2, float y, float y2, float width, float width2, float height, float height2) {
        return x < x2 + width2 && x + width > x2 && y < y2 + height2 && height + y > y2;
    }
}
