package com.nicholasnassar.ninja.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectSet;
import com.nicholasnassar.ninja.Level;
import com.nicholasnassar.ninja.SpatialMap;
import com.nicholasnassar.ninja.components.*;
import com.nicholasnassar.ninja.screens.GameScreen;

public class PhysicsSystem extends EntitySystem {
    private static final float GRAVITY = 96.5f;

    private static final float MAX = 1170f;

    private final GameScreen screen;

    private ImmutableArray<Entity> entities;

    private final ComponentMapper<PhysicsComponent> physicsMapper;

    private final ComponentMapper<GravityComponent> gravityMapper;

    private final ComponentMapper<CollidableComponent> collideMapper;

    private final ComponentMapper<DestroyOutsideComponent> destroyMapper;

    private final ComponentMapper<DamageComponent> damageMapper;

    private final ComponentMapper<HealthComponent> healthMapper;

    private final ComponentMapper<StateComponent> stateMapper;

    private final ObjectSet<Entity> retrieveArray;

    private final SpatialMap spatialMap;

    public PhysicsSystem(GameScreen screen) {
        this.screen = screen;

        physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);

        gravityMapper = ComponentMapper.getFor(GravityComponent.class);

        collideMapper = ComponentMapper.getFor(CollidableComponent.class);

        destroyMapper = ComponentMapper.getFor(DestroyOutsideComponent.class);

        damageMapper = ComponentMapper.getFor(DamageComponent.class);

        healthMapper = ComponentMapper.getFor(HealthComponent.class);

        stateMapper = ComponentMapper.getFor(StateComponent.class);

        spatialMap = new SpatialMap(screen.getLevel().getWidth(), screen.getLevel().getHeight());

        retrieveArray = new ObjectSet<Entity>();
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PhysicsComponent.class).get());
    }

    public void remakeGrid() {
        spatialMap.resize(screen.getLevel().getWidth(), screen.getLevel().getHeight());
    }

    @Override
    public void update(float deltaTime) {
        spatialMap.clear();

        for (Entity entity : entities) {
            if (!screen.canProcess(entity)) {
                continue;
            }

            spatialMap.addEntity(physicsMapper.get(entity), entity);
        }

        for (Entity entity : entities) {
            if (!screen.canProcess(entity)) {
                continue;
            }

            PhysicsComponent physics = physicsMapper.get(entity);

            Vector3 position = physics.getPosition();

            Vector2 velocity = physics.getVelocity();

            GravityComponent gravity = gravityMapper.get(entity);

            StateComponent state = stateMapper.get(entity);

            if (gravity != null) {
                velocity.y -= GRAVITY * deltaTime;

                if (velocity.y < -MAX) {
                    velocity.y = -MAX;
                }
            }

            if (velocity.x != 0 || velocity.y != 0) {
                retrieveArray.clear();

                spatialMap.retrieve(retrieveArray, physics);

                float newX = position.x + velocity.x * deltaTime;

                float y = position.y;

                float newY = y + velocity.y * deltaTime;

                float width = physics.getWidth();

                float height = physics.getHeight();

                CollidableComponent collide = collideMapper.get(entity);

                DamageComponent damage = damageMapper.get(entity);

                if (collide != null) {
                    collide.setOnWall(false);

                    boolean stuckInTop = false;

                    if (state != null && state.getPreviousState() == StateComponent.STATE_GROUND_ROLL && state.getState()
                            == StateComponent.STATE_WALKING) {
                        for (Entity loopEntity : retrieveArray) {
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

                            if (newX < x2 + width2 && newX + width > x2 && y < y2 + height2 && 1.6875f + y > y2 && y < y2 - 1.6875f) {
                                stuckInTop = true;
                            }
                        }
                    }

                    if (!stuckInTop) {
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

                    if (stuckInTop) {
                        newY = position.y;

                        state.setState(StateComponent.STATE_GROUND_ROLL);

                        state.setElapsedTime(0.04f);
                    }
                }

                position.x = newX;

                position.y = newY;
            }

            setValidConstraints(entity, position);
        }
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
