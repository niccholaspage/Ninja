package com.nicholasnassar.ninja.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.nicholasnassar.ninja.Level;
import com.nicholasnassar.ninja.components.*;
import com.nicholasnassar.ninja.QuadTree;
import com.nicholasnassar.ninja.screens.GameScreen;

public class PhysicsSystem extends IteratingSystem {
    public static final float GRAVITY = 96.5f;

    public static final float MAX = 1170f;

    private final GameScreen screen;

    private final ComponentMapper<PhysicsComponent> physicsMapper;

    private final ComponentMapper<GravityComponent> gravityMapper;

    private final ComponentMapper<CollidableComponent> collideMapper;

    private final ComponentMapper<DestroyOutsideComponent> destroyMapper;

    private final ComponentMapper<DamageComponent> damageMapper;

    private final ComponentMapper<HealthComponent> healthMapper;

    private final Array<Entity> retrieveArray;

    private QuadTree quadTree;

    public PhysicsSystem(GameScreen screen) {
        super(Family.all(PhysicsComponent.class).get());

        this.screen = screen;

        physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);

        gravityMapper = ComponentMapper.getFor(GravityComponent.class);

        collideMapper = ComponentMapper.getFor(CollidableComponent.class);

        destroyMapper = ComponentMapper.getFor(DestroyOutsideComponent.class);

        damageMapper = ComponentMapper.getFor(DamageComponent.class);

        healthMapper = ComponentMapper.getFor(HealthComponent.class);

        quadTree = new QuadTree(0, new Rectangle(0, 0, screen.getLevel().getWidth(), screen.getLevel().getHeight()));

        retrieveArray = new Array<Entity>();
    }

    public void remakeGrid() {
        quadTree = new QuadTree(0, new Rectangle(0, 0, screen.getLevel().getWidth(), screen.getLevel().getHeight()));
    }

    @Override
    public void update(float deltaTime) {
        quadTree.clear();

        super.update(deltaTime);

        for (Entity entity : getEntities()) {
            if (!screen.canProcess(entity)) {
                return;
            }

            PhysicsComponent physics = physicsMapper.get(entity);

            Vector3 position = physics.getPosition();

            retrieveArray.clear();

            quadTree.retrieve(retrieveArray, physics);

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

                DamageComponent damage = damageMapper.get(entity);

                if (collide != null) {
                    collide.setOnWall(false);

                    for (Entity loopEntity : retrieveArray) {
                        CollidableComponent loopCollide = collideMapper.get(loopEntity);

                        DamageComponent loopDamage = damageMapper.get(loopEntity);

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

                        if (width2 > 0 && height2 > 0) {
                            if (position.x != newX && overlaps(newX, x2, y, y2, width, width2, height, height2)) {
                                if (damage != null && damage.when(DamageComponent.ON_COLLIDE)) {
                                    HealthComponent health = healthMapper.get(loopEntity);

                                    if (health != null) {
                                        health.damage(damage.getDamage(DamageComponent.ON_COLLIDE));
                                    }
                                }

                                if (collide.shouldDestroy()) {
                                    getEngine().removeEntity(entity);

                                    continue;
                                }

                                if (loopDamage != null && loopDamage.when(DamageComponent.ON_COLLIDE)) {
                                    HealthComponent health = healthMapper.get(entity);

                                    if (health != null) {
                                        health.damage(loopDamage.getDamage(DamageComponent.ON_COLLIDE));
                                    }
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

                    for (Entity loopEntity : retrieveArray) {
                        CollidableComponent loopCollide = collideMapper.get(loopEntity);

                        DamageComponent loopDamage = damageMapper.get(loopEntity);

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

                        if (width2 > 0 && height2 > 0) {
                            if (position.y != newY && overlaps(newX, x2, newY, y2, width, width2, height, height2)) {
                                if (damage != null && damage.when(DamageComponent.ON_COLLIDE)) {
                                    HealthComponent health = healthMapper.get(loopEntity);

                                    if (health != null) {
                                        health.damage(damage.getDamage(DamageComponent.ON_COLLIDE));
                                    }
                                }

                                if (collide.shouldDestroy()) {
                                    getEngine().removeEntity(entity);

                                    continue;
                                }

                                if (loopDamage != null && loopDamage.when(DamageComponent.ON_COLLIDE)) {
                                    HealthComponent health = healthMapper.get(entity);

                                    if (health != null) {
                                        health.damage(loopDamage.getDamage(DamageComponent.ON_COLLIDE));
                                    }
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
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (!screen.canProcess(entity)) {
            return;
        }

        quadTree.insert(entity);
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

    private boolean overlaps(float x, float x2, float y, float y2, float width, float width2, float height, float height2) {
        return x < x2 + width2 && x + width > x2 && y < y2 + height2 && height + y > y2;
    }
}
