package com.nicholasnassar.ninja.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.nicholasnassar.ninja.DepthComparator;
import com.nicholasnassar.ninja.components.*;
import com.nicholasnassar.ninja.screens.GameScreen;

public class RenderSystem extends SortedIteratingSystem {
    private final GameScreen screen;

    private final SpriteBatch batch;

    private final Camera camera;

    private final ComponentMapper<PhysicsComponent> physicsMapper;

    private final ComponentMapper<VisualComponent> textureMapper;

    private final ComponentMapper<DirectionComponent> directionMapper;

    private final ComponentMapper<StateComponent> stateMapper;

    private final ComponentMapper<ColorComponent> colorMapper;

    private float cameraX;

    private float cameraY;

    private float cameraXAndWidth;

    private float cameraYAndHeight;

    public RenderSystem(GameScreen screen, SpriteBatch batch, Camera camera) {
        super(Family.all(PhysicsComponent.class, VisualComponent.class).exclude(InvisibleComponent.class).get(), new DepthComparator());

        this.screen = screen;

        this.batch = batch;

        this.camera = camera;

        physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);

        textureMapper = ComponentMapper.getFor(VisualComponent.class);

        directionMapper = ComponentMapper.getFor(DirectionComponent.class);

        stateMapper = ComponentMapper.getFor(StateComponent.class);

        colorMapper = ComponentMapper.getFor(ColorComponent.class);
    }

    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        float zoom = ((OrthographicCamera) camera).zoom;

        cameraX = camera.position.x / GameScreen.PIXELS_PER_METER - zoom * camera.viewportWidth / GameScreen.PIXELS_PER_METER;

        cameraXAndWidth = camera.position.x / GameScreen.PIXELS_PER_METER + zoom * camera.viewportWidth / GameScreen.PIXELS_PER_METER;

        cameraY = camera.position.y / GameScreen.PIXELS_PER_METER - zoom * camera.viewportHeight / GameScreen.PIXELS_PER_METER;

        cameraYAndHeight = camera.position.y + zoom * camera.viewportHeight / GameScreen.PIXELS_PER_METER;

        super.update(deltaTime);

        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent physics = physicsMapper.get(entity);

        VisualComponent visual = textureMapper.get(entity);

        TextureRegion region;

        StateComponent state = stateMapper.get(entity);

        if (visual.isAnimation()) {
            float elapsedTime = state.getElapsedTime();

            int stateInt = state.getState();

            if (visual.hasMultipleAnimations(stateInt) && visual.getAnimation(stateInt).isAnimationFinished(elapsedTime)) {
                visual.randomize(stateInt);

                elapsedTime = 0;
            } else if (screen.getState() == GameScreen.STATE_RUNNING) {
                elapsedTime += deltaTime;
            }

            state.setElapsedTime(elapsedTime);

            region = visual.getRegion(stateInt, elapsedTime);
        } else {
            region = visual.getRegion();
        }

        DirectionComponent direction = directionMapper.get(entity);

        float rotation = visual.getRotation();

        if (rotation != -1) {
            if (screen.getState() == GameScreen.STATE_RUNNING) {
                if (rotation < 0) {
                    rotation = 360;
                } else {
                    boolean subtract = direction.getDirection() == DirectionComponent.RIGHT;

                    if (subtract) {
                        rotation -= visual.getRotationSpeed() * deltaTime;
                    } else {
                        rotation += visual.getRotationSpeed() * deltaTime;
                    }
                }

                visual.setRotation(rotation);
            }
        } else {
            rotation = 0;
        }

        Vector3 position = physics.getPosition();

        if (!(position.x < cameraXAndWidth && position.x + physics.getWidth() > cameraX &&
                position.y < cameraYAndHeight && physics.getHeight() + position.y > cameraY)) {
            return;
        }

        float width = region.getRegionWidth() * physics.getSizeScale();

        float height = region.getRegionHeight() * physics.getSizeScale();

        int x = (int) (position.x * GameScreen.PIXELS_PER_METER);

        int y = (int) (position.y * GameScreen.PIXELS_PER_METER);

        x -= width / 2 - physics.getWidth() * GameScreen.PIXELS_PER_METER / 2;

        boolean flip = false;

        if (direction != null && direction.getDirection() == DirectionComponent.LEFT) {
            flip = true;
        }

        ColorComponent color = colorMapper.get(entity);

        if (color != null) {
            batch.setColor(color.getColor());
        } else {
            batch.setColor(Color.WHITE);
        }

        batch.draw(region, flip ? x + width : x, y, flip ? -width / 2 : width / 2, height / 2, flip ? -width : width, height, 1, 1, rotation);
    }
}
