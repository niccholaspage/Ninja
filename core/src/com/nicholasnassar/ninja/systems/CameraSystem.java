package com.nicholasnassar.ninja.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.nicholasnassar.ninja.Level;
import com.nicholasnassar.ninja.components.CameraComponent;
import com.nicholasnassar.ninja.components.PhysicsComponent;
import com.nicholasnassar.ninja.screens.GameScreen;

public class CameraSystem extends IteratingSystem {
    private final GameScreen screen;

    private final ComponentMapper<CameraComponent> cameraMapper;

    private final ComponentMapper<PhysicsComponent> physicsMapper;

    public CameraSystem(GameScreen screen) {
        super(Family.all(CameraComponent.class).get());

        this.screen = screen;

        cameraMapper = ComponentMapper.getFor(CameraComponent.class);

        physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CameraComponent cam = cameraMapper.get(entity);

        Camera camera = cam.getCamera();

        Entity target = cam.getTarget();

        PhysicsComponent physics = physicsMapper.get(target);

        Vector2 position = physics.getPosition();

        Level level = screen.getLevel();

        float maxWidth = level.getWidth() * GameScreen.PIXELS_PER_METER - camera.viewportWidth / 2;

        float maxHeight = level.getHeight() * GameScreen.PIXELS_PER_METER - camera.viewportHeight / 2;

        camera.position.x = Math.min(maxWidth, Math.max(camera.viewportWidth / 2, (int) (position.x * GameScreen.PIXELS_PER_METER)));

        camera.position.y = Math.min(maxHeight, Math.max(camera.viewportHeight / 2, (int) (position.y * GameScreen.PIXELS_PER_METER)));

        camera.update();
    }
}
