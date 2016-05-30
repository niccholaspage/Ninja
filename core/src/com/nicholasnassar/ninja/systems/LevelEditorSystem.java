package com.nicholasnassar.ninja.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.nicholasnassar.ninja.ControlManager;
import com.nicholasnassar.ninja.DepthComparator;
import com.nicholasnassar.ninja.Level;
import com.nicholasnassar.ninja.Spawner;
import com.nicholasnassar.ninja.components.*;
import com.nicholasnassar.ninja.screens.GameScreen;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;

public class LevelEditorSystem extends EntitySystem implements InputProcessor {
    private final GameScreen screen;

    private final Spawner spawner;

    private final ComponentMapper<PhysicsComponent> physicsMapper;

    private final ComponentMapper<TileComponent> tileMapper;

    private final ComponentMapper<SaveComponent> saveMapper;

    private final ComponentMapper<CollidableComponent> collidableMapper;

    private final Camera camera;

    private ImmutableArray<Entity> entities;

    private ImmutableArray<Entity> saveEntities;

    private final ShapeRenderer shapeRenderer;

    private Entity selectedEntity;

    private String selectedBuild;

    private float mouseX;

    private float mouseY;

    private int selectedButton;

    private boolean foreground;

    private DepthComparator depthComparator;

    public LevelEditorSystem(GameScreen screen, Spawner spawner, Camera camera) {
        this.screen = screen;

        this.spawner = spawner;

        this.camera = camera;

        physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);

        tileMapper = ComponentMapper.getFor(TileComponent.class);

        saveMapper = ComponentMapper.getFor(SaveComponent.class);

        collidableMapper = ComponentMapper.getFor(CollidableComponent.class);

        shapeRenderer = new ShapeRenderer();

        selectedEntity = null;

        selectedBuild = null;

        foreground = true;

        depthComparator = new DepthComparator();
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PhysicsComponent.class, VisualComponent.class).exclude(InvisibleComponent.class).get());

        saveEntities = engine.getEntitiesFor(Family.all(SaveComponent.class, PhysicsComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        if (screen.getState() != GameScreen.STATE_EDITING) {
            return;
        }

        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Level level = screen.getLevel();

        shapeRenderer.setColor(Color.BLACK);

        for (int i = 0; i < level.getHeight(); i++) {
            shapeRenderer.line(0, i * GameScreen.PIXELS_PER_METER, level.getWidth() * GameScreen.PIXELS_PER_METER, i * GameScreen.PIXELS_PER_METER);
        }

        for (int i = 0; i < level.getWidth(); i++) {
            shapeRenderer.line(i * GameScreen.PIXELS_PER_METER, 0, i * GameScreen.PIXELS_PER_METER, level.getHeight() * GameScreen.PIXELS_PER_METER);
        }

        shapeRenderer.setColor(Color.YELLOW);

        float top = level.getHeight() * GameScreen.PIXELS_PER_METER - 0.1f;

        //Top
        shapeRenderer.line(0, top, level.getWidth() * GameScreen.PIXELS_PER_METER, top);

        //Bot
        shapeRenderer.line(0, 0, level.getWidth() * GameScreen.PIXELS_PER_METER, 0);

        float last = level.getWidth() * GameScreen.PIXELS_PER_METER - 0.1f;

        shapeRenderer.line(last, 0, last, level.getHeight() * GameScreen.PIXELS_PER_METER);

        float first = 0.1f;

        shapeRenderer.line(first, 0, first, level.getHeight() * GameScreen.PIXELS_PER_METER);

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (screen.getState() != GameScreen.STATE_EDITING) {
            return true;
        }

        selectedButton = button;

        if (removeLogic()) {
            return true;
        }

        if (button == Input.Buttons.LEFT) {
            Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);

            screen.getCamera().unproject(mouse);

            mouseX = mouse.x;

            mouseY = mouse.y;

            Entity[] entities = this.entities.toArray(Entity.class);

            Arrays.sort(entities, Collections.reverseOrder(depthComparator));

            for (Entity entity : entities) {
                PhysicsComponent physics = physicsMapper.get(entity);

                Vector3 position = physics.getPosition();

                float x = position.x * GameScreen.PIXELS_PER_METER;

                float y = position.y * GameScreen.PIXELS_PER_METER;

                float width = physics.getWidth() * GameScreen.PIXELS_PER_METER;

                float height = physics.getHeight() * GameScreen.PIXELS_PER_METER;

                if (mouse.x >= x && mouse.x <= x + width && mouse.y >= y && mouse.y <= y + height) {
                    selectedEntity = entity;

                    break;
                }
            }

            if (selectedEntity == null && selectedBuild != null) {
                placeLogic();
            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT && screen.getState() == GameScreen.STATE_EDITING) {
            Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);

            screen.getCamera().unproject(mouse);

            selectedEntity = null;

            if (selectedBuild != null && mouseX == mouse.x && mouseY == mouse.y) {
                placeLogic();
            }
        }

        selectedButton = -1;

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (screen.getState() != GameScreen.STATE_EDITING) {
            return true;
        }

        if (removeLogic()) {
            return true;
        }

        if (selectedEntity != null) {
            Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);

            screen.getCamera().unproject(mouse);

            PhysicsComponent physics = physicsMapper.get(selectedEntity);

            Vector3 position = physics.getPosition();

            float mouseX = mouse.x / GameScreen.PIXELS_PER_METER - physics.getWidth() / 2;

            float mouseY = mouse.y / GameScreen.PIXELS_PER_METER - physics.getHeight() / 2;

            if (tileMapper.has(selectedEntity)) {
                mouseX = (int) ((mouse.x - physics.getWidth() / 2) / GameScreen.PIXELS_PER_METER);

                mouseY = (int) ((mouse.y - physics.getHeight() / 2) / GameScreen.PIXELS_PER_METER);
            }

            if (isTileFree(mouseX, mouseY)) {
                position.set(mouseX, mouseY, position.z);
            }
        } else if (selectedBuild != null) {
            placeLogic();
        }

        return true;
    }

    private boolean removeLogic() {
        if (selectedButton == Input.Buttons.RIGHT || (ControlManager.touchControls && selectedBuild == null)) {
            Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);

            screen.getCamera().unproject(mouse);

            float mouseX = mouse.x / GameScreen.PIXELS_PER_METER;

            float mouseY = mouse.y / GameScreen.PIXELS_PER_METER;

            for (Entity entity : entities) {
                SaveComponent save = saveMapper.get(entity);

                if (save != null && !save.getId().equals("spawner_player")) {
                    PhysicsComponent physics = physicsMapper.get(entity);

                    Vector3 position = physics.getPosition();

                    if (mouseX >= position.x && mouseX <= position.x + physics.getWidth() && mouseY >= position.y &&
                            mouseY <= position.y + physics.getHeight()) {
                        getEngine().removeEntity(entity);
                    }
                }
            }

            return true;
        }

        return false;
    }

    private void placeLogic() {
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);

        screen.getCamera().unproject(mouse);

        float objectX = (int) (mouse.x / GameScreen.PIXELS_PER_METER);

        float objectY = (int) (mouse.y / GameScreen.PIXELS_PER_METER);

        if (isTileFree(objectX, objectY)) {
            spawner.spawnEntity(selectedBuild, foreground, objectX, objectY);
        }
    }

    private boolean isTileFree(float x, float y) {
        Level level = screen.getLevel();

        if (x < 0 || y < 0 || x > level.getWidth() - 1 || y > level.getHeight() - 1) {
            return false;
        }

        boolean ignoreBackground = selectedEntity == null && selectedBuild != null && !selectedBuild.startsWith("block_");

        for (Entity entity : entities) {
            if (ignoreBackground && tileMapper.has(entity) && !collidableMapper.has(entity)) {
                continue;
            }

            PhysicsComponent physics = physicsMapper.get(entity);

            Vector3 position = physics.getPosition();

            if (position.x == x && position.y == y) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        if (screen.getState() != GameScreen.STATE_EDITING) {
            return true;
        }

        float zoom = ((OrthographicCamera) camera).zoom + ((float) amount / 2);

        ((OrthographicCamera) camera).zoom = Math.max(1, Math.min(3, zoom));

        return true;
    }

    public void setSelectedBuild(String selectedBuild) {
        this.selectedBuild = selectedBuild;
    }

    public void load(final FileHandle handle) {
        load(handle.readString());
    }

    public void load(final String text) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < saveEntities.size(); i++) {
                    getEngine().removeEntity(saveEntities.get(i));

                    i--;
                }

                screen.load(text);
            }
        });
    }

    public boolean changeLevelSize(int width, int height) {
        if (width < 30 || height < 16) {
            return false;
        }

        for (Entity entity : entities) {
            Vector3 position = physicsMapper.get(entity).getPosition();

            float x = position.x;

            float y = position.y;

            if (x > width - 1 || y > height - 1) {
                return false;
            }
        }

        Level level = screen.getLevel();

        level.setWidth(width);

        level.setHeight(height);

        getEngine().getSystem(PhysicsSystem.class).remakeGrid();

        return true;
    }

    public void save(FileHandle handle) {
        save(handle.writer(false));
    }

    public void save(Writer writer) {
        Json json = new Json();

        json.setWriter(new JsonWriter(writer));

        Level level = screen.getLevel();

        json.writeObjectStart();

        json.writeObjectStart("level_info");
        json.writeValue("width", level.getWidth());
        json.writeValue("height", level.getHeight());
        json.writeObjectEnd();

        json.writeArrayStart("entities");

        for (Entity entity : saveEntities) {
            SaveComponent save = saveMapper.get(entity);

            PhysicsComponent physics = physicsMapper.get(entity);

            json.writeObjectStart();
            json.writeValue("id", save.getId());
            if (tileMapper.has(entity)) {
                json.writeValue("foreground", collidableMapper.has(entity));
            }
            json.writeValue("x", physics.getPosition().x);
            json.writeValue("y", physics.getPosition().y);
            json.writeObjectEnd();
        }

        json.writeArrayEnd();

        json.writeObjectEnd();

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setForeground(boolean foreground) {
        this.foreground = foreground;
    }
}
