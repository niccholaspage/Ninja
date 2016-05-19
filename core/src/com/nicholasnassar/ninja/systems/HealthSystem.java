package com.nicholasnassar.ninja.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.nicholasnassar.ninja.components.AIComponent;
import com.nicholasnassar.ninja.components.CameraComponent;
import com.nicholasnassar.ninja.components.HealthComponent;
import com.nicholasnassar.ninja.screens.GameScreen;

public class HealthSystem extends IteratingSystem {
    private final GameScreen screen;

    private final ComponentMapper<CameraComponent> cameraMapper;

    private final ComponentMapper<HealthComponent> healthMapper;

    private ImmutableArray<Entity> aiEntities;

    private Entity player;

    public HealthSystem(GameScreen screen) {
        super(Family.all(HealthComponent.class).get());

        this.screen = screen;

        cameraMapper = ComponentMapper.getFor(CameraComponent.class);

        healthMapper = ComponentMapper.getFor(HealthComponent.class);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        aiEntities = engine.getEntitiesFor(Family.all(AIComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HealthComponent health = healthMapper.get(entity);

        if (health.isDead()) {
            if (entity == player) {
                screen.death();
            }

            getEngine().removeEntity(entity);
        }
    }

    public void setPlayer(Entity player) {
        this.player = player;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (aiEntities.size() < 1) {
            screen.win();
        }

        HealthComponent health = healthMapper.get(player);

        screen.getHealth().setText(health.getHealth() + " / " + health.getMaxHealth() + " HP");
    }
}
