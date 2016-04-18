package com.nicholasnassar.ninja.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
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

    private final ComponentMapper<OffsetComponent> offsetMapper;

    private final ComponentMapper<ColorComponent> colorMapper;

    public RenderSystem(GameScreen screen, SpriteBatch batch, Camera camera) {
        super(Family.all(PhysicsComponent.class, VisualComponent.class).exclude(InvisibleComponent.class).get(), new DepthComparator());

        this.screen = screen;

        this.batch = batch;

        this.camera = camera;

        physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);

        textureMapper = ComponentMapper.getFor(VisualComponent.class);

        directionMapper = ComponentMapper.getFor(DirectionComponent.class);

        stateMapper = ComponentMapper.getFor(StateComponent.class);

        offsetMapper = ComponentMapper.getFor(OffsetComponent.class);

        colorMapper = ComponentMapper.getFor(ColorComponent.class);
    }

    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

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

            if (screen.getState() == GameScreen.STATE_RUNNING) {
                elapsedTime += deltaTime;

                state.setElapsedTime(elapsedTime);
            }

            int stateInt = state.getState();

            region = visual.getRegion(stateInt, elapsedTime);
        } else {
            region = visual.getRegion();
        }

        DirectionComponent direction = directionMapper.get(entity);

        float rotation = visual.getRotation();

        if (rotation != -1) {
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
        } else {
            rotation = 0;
        }

        boolean flip = false;

        if (direction != null && direction.getDirection() == DirectionComponent.LEFT) {
            flip = true;
        }

        Vector3 position = physics.getPosition();

        int width = region.getRegionWidth();

        int height = region.getRegionHeight();

        int x = (int) (position.x * GameScreen.PIXELS_PER_METER);

        int y = (int) (position.y * GameScreen.PIXELS_PER_METER);

        OffsetComponent offset = offsetMapper.get(entity);

        if (state != null && offset != null) {
            x += offset.getStateOffsets().get(state.getState(), 0);
        }

        ColorComponent color = colorMapper.get(entity);

        if (color != null) {
            batch.setColor(color.getColor());
        } else {
            batch.setColor(Color.WHITE);
        }

        //batch.draw(region, flip ? x + width : x, y, width / 2, height / 2, flip ? -width : width, height, 1, 1, flip ? -rotation : rotation);
        batch.draw(region, flip ? x + width : x, y, flip ? -width / 2 : width / 2, height / 2, flip ? -width : width, height, 1, 1, rotation);
    }
}
