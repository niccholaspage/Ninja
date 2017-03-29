package com.nicholasnassar.ninja;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.nicholasnassar.ninja.components.*;
import com.nicholasnassar.ninja.screens.GameScreen;

public class Spawner {
    private final NinjaAssetManager manager;

    private final Engine engine;

    private final boolean levelEditing;

    public Spawner(NinjaAssetManager manager, Engine engine, boolean levelEditing) {
        this.manager = manager;

        this.engine = engine;

        this.levelEditing = levelEditing;
    }

    private Entity spawnSoldier(float x, float y) {
        Entity entity = new Entity();

        IntMap<Array<Animation<TextureRegion>>> animations = manager.getAnimationsFor("soldier");

        TextureRegion region = animations.get(0).get(0).getKeyFrame(0);

        float width = region.getRegionWidth() / GameScreen.PIXELS_PER_METER;

        float height = region.getRegionHeight() / GameScreen.PIXELS_PER_METER;

        entity.add(new PhysicsComponent(x, y, width, height));
        entity.add(new JumpComponent(26f));
        entity.add(new DirectionComponent());
        entity.add(new GravityComponent());
        entity.add(new CollidableComponent(CollidableComponent.CREATURE_EXCEPTION));
        entity.add(new HealthComponent(20));

        VisualComponent visual = new VisualComponent(animations);
        entity.add(new StateComponent(visual));
        entity.add(visual);
        entity.add(new SpeedComponent(5f));
        entity.add(new AIComponent());
        entity.add(new ColorComponent((float) Math.random(), (float) Math.random(), (float) Math.random()));
        entity.add(new CooldownComponent());

        engine.addEntity(entity);

        return entity;
    }

    public Entity spawnPlayer(float x, float y) {
        Entity entity = new Entity();

        IntMap<Array<Animation<TextureRegion>>> animations = manager.getAnimationsFor("player");

        TextureRegion region = animations.get(0).get(0).getKeyFrame(0);

        float width = region.getRegionWidth() / GameScreen.PIXELS_PER_METER;

        float height = region.getRegionHeight() / GameScreen.PIXELS_PER_METER;

        entity.add(new PhysicsComponent(x, y, 10, width, height));
        entity.add(new JumpComponent(28.6f, 1));
        entity.add(new DirectionComponent());
        entity.add(new GravityComponent());
        entity.add(new CollidableComponent(CollidableComponent.CREATURE_EXCEPTION));
        entity.add(new HealthComponent(50));
        entity.add(new CooldownComponent());

        VisualComponent visual = new VisualComponent(animations);
        entity.add(new StateComponent(visual));
        entity.add(visual);
        entity.add(new SpeedComponent(7.5f));
        entity.add(new ControllableComponent());

        engine.addEntity(entity);

        return entity;
    }

    public Entity spawnFreeMovingGuy() {
        Entity entity = new Entity();

        entity.add(new PhysicsComponent(0, 0, 0, 0));
        entity.add(new SpeedComponent(20f));
        entity.add(new ControllableComponent());
        entity.add(new LevelEditorComponent());

        engine.addEntity(entity);

        return entity;
    }

    public Entity spawnCamera(Camera camera) {
        Entity entity = new Entity();

        entity.add(new CameraComponent(camera));

        engine.addEntity(entity);

        return entity;
    }

    private Entity spawnBlock(String type, boolean foreground, float x, float y) {
        Entity entity = new Entity();

        TextureRegion region = manager.getBlock(type);

        float width = region.getRegionWidth() / GameScreen.PIXELS_PER_METER;

        float height = region.getRegionHeight() / GameScreen.PIXELS_PER_METER;

        entity.add(new PhysicsComponent(x, y, -10, width, height));

        entity.add(new VisualComponent(region));

        entity.add(new TileComponent());

        if (levelEditing) {
            entity.add(new SaveComponent("block_" + type));
        }

        if (foreground) {
            entity.add(new CollidableComponent(CollidableComponent.TILE_EXCEPTION));
        } else {
            entity.add(new ColorComponent(0.5f, 0.5f, 0.5f));
        }

        engine.addEntity(entity);

        return entity;
    }

    public Entity spawnLevelEditor() {
        Entity entity = new Entity();

        entity.add(new SpeedComponent(5f));
        entity.add(new PhysicsComponent(0, 0, 0, 0));

        return entity;
    }

    public Entity spawnShuriken(float x, float y, float xDiff, int direction) {
        Entity entity = new Entity();

        TextureRegion region = new TextureRegion(manager.getEntity("shuriken"));

        float width = region.getRegionWidth() / GameScreen.PIXELS_PER_METER;

        float height = region.getRegionHeight() / GameScreen.PIXELS_PER_METER;

        if (direction == DirectionComponent.RIGHT) {
            x += xDiff;
        } else {
            x -= xDiff;
        }

        PhysicsComponent physics = new PhysicsComponent(x, y + .5f, 0, width, height);

        physics.getVelocity().x = direction == DirectionComponent.RIGHT ? 15.625f : -15.625f;

        physics.setSizeScale(0.3125f);

        entity.add(physics);
        entity.add(new VisualComponent(region, 1000f));
        entity.add(new DirectionComponent());
        entity.add(new CollidableComponent(CollidableComponent.THROWABLE_EXCEPTION, true));
        entity.add(new DestroyOutsideComponent());
        entity.add(new DamageComponent(DamageComponent.ON_COLLIDE, 10));

        engine.addEntity(entity);

        return entity;
    }

    public Entity spawnSpawner(String id, float x, float y) {
        Entity entity = new Entity();

        TextureRegion region = manager.getAnimationsFor(id).get(0).get(0).getKeyFrame(0);

        float width = region.getRegionWidth() / GameScreen.PIXELS_PER_METER;

        float height = region.getRegionHeight() / GameScreen.PIXELS_PER_METER;

        entity.add(new SaveComponent("spawner_" + id));
        entity.add(new VisualComponent(region));
        entity.add(new PhysicsComponent(x, y, width, height));

        engine.addEntity(entity);

        return entity;
    }

    public Entity spawnEntity(String id, boolean foreground, float x, float y) {
        String category = id.substring(0, id.indexOf("_"));

        String type = id.substring(id.indexOf("_") + 1);

        if (category.equals("block")) {
            return spawnBlock(type, foreground, x, y);
        } else if (category.equals("creature")) {
            if (type.equals("player")) {
                return spawnPlayer(x, y);
            } else if (type.equals("soldier")) {
                return spawnSoldier(x, y);
            }
        } else if (category.equals("spawner")) {
            return spawnSpawner(type, x, y);
        }

        return null;
    }
}
