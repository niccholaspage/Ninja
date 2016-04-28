package com.nicholasnassar.ninja.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.nicholasnassar.ninja.Level;
import com.nicholasnassar.ninja.components.CollidableComponent;
import com.nicholasnassar.ninja.components.DestroyOutsideComponent;
import com.nicholasnassar.ninja.components.GravityComponent;
import com.nicholasnassar.ninja.components.PhysicsComponent;
import com.nicholasnassar.ninja.screens.GameScreen;

public class PhysicsSystem extends IteratingSystem {
    public static final float GRAVITY = 96.5f;

    public static final float MAX = 1170f;

    private final GameScreen screen;

    private final ComponentMapper<PhysicsComponent> physicsMapper;

    private final ComponentMapper<GravityComponent> gravityMapper;

    private final ComponentMapper<CollidableComponent> collideMapper;

    private final ComponentMapper<DestroyOutsideComponent> destroyMapper;

    public PhysicsSystem(GameScreen screen) {
        super(Family.all(PhysicsComponent.class).get());

        this.screen = screen;

        physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);

        gravityMapper = ComponentMapper.getFor(GravityComponent.class);

        collideMapper = ComponentMapper.getFor(CollidableComponent.class);

        destroyMapper = ComponentMapper.getFor(DestroyOutsideComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (!screen.canProcess(entity)) {
            return;
        }

        PhysicsComponent physics = physicsMapper.get(entity);

        Vector3 position = physics.getPosition();

        Vector2 velocity = physics.getVelocity();

        GravityComponent gravity = gravityMapper.get(entity);

        if (gravity != null) {
            velocity.y -= GRAVITY * deltaTime;

            if (velocity.y < -MAX) {
                velocity.y = -MAX;
            }
        }

        if (velocity.x != 0 || velocity.y != 0) {
            float newX = position.x + velocity.x * deltaTime;

            float y = position.y;

            float newY = y + velocity.y * deltaTime;

            float width = physics.getWidth();

            float height = physics.getHeight();

            CollidableComponent collide = collideMapper.get(entity);

            if (collide != null) {
                collide.setOnWall(false);

                for (Entity loopEntity : getEntities()) {
                    CollidableComponent loopCollide = collideMapper.get(loopEntity);

                    if (entity == loopEntity || loopCollide == null) {
                        continue;
                    }

                    if (collide.getException() == loopCollide.getException()) {
                        continue;
                    }

                    PhysicsComponent loopPhysics = physicsMapper.get(loopEntity);

                    Vector3 pos2 = loopPhysics.getPosition();

                    float x2 = pos2.x;

                    float y2 = pos2.y;

                    float width2 = loopPhysics.getWidth();

                    float height2 = loopPhysics.getHeight();

                    float radius = physics.getRadius();

                    float radius2 = loopPhysics.getRadius();

                    if (width2 > 0 && height2 > 0) {
                        if (position.x != newX && overlaps(newX, x2, y, y2, width, width2, height, height2, radius, radius2)) {
                            if (collide.shouldDestroy()) {
                                getEngine().removeEntity(entity);

                                continue;
                            }

                            if (loopCollide.shouldDestroy()) {
                                getEngine().removeEntity(loopEntity);

                                continue;
                            }

                            if (velocity.x != 0) {
                                if (velocity.x > 0) {
                                    newX = x2 - width;
                                } else {
                                    newX = x2 + width2;
                                }

                                velocity.x = 0;

                                collide.setOnWall(true);

                                if (velocity.y < 0) {
                                    velocity.y /= 2;
                                }
                            }

                            break;
                        }
                    }
                }

                if (gravity != null) {
                    gravity.setGrounded(false);
                }

                for (Entity loopEntity : getEntities()) {
                    CollidableComponent loopCollide = collideMapper.get(loopEntity);

                    if (entity == loopEntity || loopCollide == null) {
                        continue;
                    }

                    if (collide.getException() == loopCollide.getException()) {
                        continue;
                    }

                    PhysicsComponent loopPhysics = physicsMapper.get(loopEntity);

                    Vector3 pos2 = loopPhysics.getPosition();

                    float x2 = pos2.x;

                    float y2 = pos2.y;

                    float width2 = loopPhysics.getWidth();

                    float height2 = loopPhysics.getHeight();

                    float radius = physics.getRadius();

                    float radius2 = loopPhysics.getRadius();

                    if (width2 > 0 && height2 > 0) {
                        if (position.y != newY && overlaps(newX, x2, newY, y2, width, width2, height, height2, radius, radius2)) {
                            if (collide.shouldDestroy()) {
                                getEngine().removeEntity(entity);

                                continue;
                            }

                            if (loopCollide.shouldDestroy()) {
                                getEngine().removeEntity(loopEntity);

                                continue;
                            }

                            if (velocity.y > 0) {
                                velocity.y = 0;

                                newY = y2 - height;
                            } else {
                                newY = y2 + height2;

                                velocity.y = 0;

                                if (gravity != null) {
                                    gravity.setGrounded(true);
                                }
                            }

                            break;
                        }
                    }
                }
            }

            position.x = newX;

            position.y = newY;
        }

        setValidConstraints(entity, position);
    }

    private void setValidConstraints(Entity entity, Vector3 position) {
        Level level = screen.getLevel();

        float levelWidth = level.getWidth() - 1;

        float levelHeight = level.getHeight() - 1;

        float newX = position.x;

        float newY = position.y;

        if (!destroyMapper.has(entity)) {
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

            position.set(newX, newY, position.z);
        } else {
            PhysicsComponent physics = physicsMapper.get(entity);

            if (newX < -physics.getWidth()) {
                getEngine().removeEntity(entity);
            } else if (newX - physics.getWidth() > levelWidth) {
                getEngine().removeEntity(entity);
            }

            if (newY < -physics.getHeight()) {
                getEngine().removeEntity(entity);
            } else if (newY - physics.getHeight() > levelHeight) {
                getEngine().removeEntity(entity);
            }

            position.set(newX, newY, position.z);
        }
    }

    private boolean overlaps(float x, float x2, float y, float y2, float width, float width2, float height, float height2, float radius, float radius2) {
        if (radius > 0 && radius2 > 0) {
            float dx = x - x2;
            float dy = y - y2;

            double distance = Math.sqrt(dx * dx + dy * dy);

            return distance < radius + radius2;
        }

        if (radius > 0 && radius2 < 0) {
            float closestX = clamp(x, x2, x2 + width2);
            float closestY = clamp(y, y2, y2 + height2);

            float distanceX = x - closestX;
            float distanceY = y - closestY;

            float distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
            return distanceSquared < (radius * radius);
        }

        if (radius2 > 0 && radius < 0) {
            float closestX = clamp(x2, x, x + width);
            float closestY = clamp(y2, y, y + height);

            float distanceX = x2 - closestX;
            float distanceY = y2 - closestY;

            float distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
            return distanceSquared < (radius2 * radius2);
        }

        return x < x2 + width2 && x + width > x2 && y < y2 + height2 && height + y > y2;
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }
}
