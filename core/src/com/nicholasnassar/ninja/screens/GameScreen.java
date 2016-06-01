package com.nicholasnassar.ninja.screens;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nicholasnassar.ninja.*;
import com.nicholasnassar.ninja.components.*;
import com.nicholasnassar.ninja.systems.*;
import com.nicholasnassar.ninja.ui.SelectButton;

public class GameScreen extends NinjaScreen {
    private final Engine engine;

    private final Viewport viewport;

    private final ComponentMapper<CameraComponent> cameraMapper;

    private final ComponentMapper<PhysicsComponent> physicsMapper;

    private final Camera camera;

    private final Spawner spawner;

    private Entity cameraEntity;

    private Entity freeMovingGuy;

    private final Stage uiStage;

    public static final float PIXELS_PER_METER = 16f;

    private int state;

    private final Level level;

    private final Table pauseMenu;

    private final MobileInput mobileInput;

    private Table levelProperties;

    private Table blockTable;

    private Table creatureTable;

    private final Label health;

    private final Label fps;

    private LevelEditorSystem levelEditorSystem;

    private boolean isLoading;

    public static final int STATE_RUNNING = 0;

    public static final int STATE_PAUSED = 1;

    public static final int STATE_EDITING = 2;

    public GameScreen(final NinjaGame game, final SpriteBatch batch, boolean levelEditor) {
        super(batch);

        cameraMapper = ComponentMapper.getFor(CameraComponent.class);

        physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);

        camera = new OrthographicCamera();

        viewport = new ExtendViewport(300, 250, camera);

        //uiStage = new Stage(new ScreenViewport());
        uiStage = new Stage(new ExtendViewport(792, 445.5f));

        Skin skin = game.getSkin();

        pauseMenu = new Table();

        TextButton mainMenuButton = new TextButton("Main Menu", skin);

        pauseMenu.align(Align.center);

        pauseMenu.setFillParent(true);

        pauseMenu.add(new Label("Paused!", skin));
        pauseMenu.row();
        pauseMenu.add(mainMenuButton).padBottom(5f);

        uiStage.addActor(pauseMenu);

        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);

                game.setScreen(new MainMenuScreen(game, batch));
            }
        });

        level = new Level(100, 20);

        if (ControlManager.touchControls) {
            Touchpad touchpad = new Touchpad(10, skin);

            uiStage.addActor(touchpad);

            TextButton jumpButton = new TextButton("Jump", skin);

            uiStage.addActor(jumpButton);

            TextButton throwButton = new TextButton("Throw", skin);

            uiStage.addActor(throwButton);

            TextButton pauseButton = new TextButton("Pause", skin);

            uiStage.addActor(pauseButton);

            mobileInput = new MobileInput(this, touchpad, jumpButton, throwButton, pauseButton);
        } else {
            mobileInput = null;
        }

        Gdx.input.setInputProcessor(uiStage);

        engine = new Engine();

        engine.addSystem(new CameraSystem(this));
        engine.addSystem(new RenderSystem(this, batch, camera));
        engine.addSystem(new InputSystem(this, mobileInput));
        engine.addSystem(new AISystem(this));
        engine.addSystem(new StateSystem());
        engine.addSystem(new PhysicsSystem(this));
        engine.addSystem(new HealthSystem(this));
        engine.addSystem(new CooldownSystem());
        engine.addSystem(new RunnableSystem());

        spawner = new Spawner(game.getAssetManager(), engine, levelEditor);

        health = new Label("", game.getSkin());

        fps = new Label("", game.getSkin());

        uiStage.addActor(health);

        uiStage.addActor(fps);

        if (levelEditor) {
            setupLevelEditor(game, spawner);

            return;
        }

        levelProperties = null;

        cameraEntity = spawner.spawnCamera(camera);

        state = STATE_RUNNING;

        isLoading = false;

        load(Gdx.files.internal("levels/metal.lvl"));

        updateSystems(STATE_RUNNING);

        flipEntities(true);
    }

    public Camera getCamera() {
        return camera;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);

        uiStage.getViewport().update(width, height, true);

        float stageWidth = uiStage.getViewport().getWorldWidth();

        float stageHeight = uiStage.getViewport().getWorldHeight();

        if (levelProperties != null) {
            levelProperties.setPosition(stageWidth - 130, stageHeight - 5);

            blockTable.setPosition(8, stageHeight - 25);

            creatureTable.setPosition(8, stageHeight - 67);
        }

        if (mobileInput != null) {
            mobileInput.resize((int) stageWidth, (int) stageHeight);
        }

        health.setPosition(8, stageHeight - 25);

        fps.setPosition(8, stageHeight - 50);
    }

    @Override
    public void render(float delta) {
        //super.render(delta); - We don't need this, we pause the game when escape is pressed (no exit anymore, oops.).
        if (ControlManager.isJustPressed(back)) {
            togglePause();
        }

        delta = (float) Math.min(0.05, delta);

        fps.setText("FPS: " + Gdx.graphics.getFramesPerSecond());

        viewport.apply();

        engine.update(delta);

        uiStage.act(delta);

        uiStage.getViewport().apply();

        uiStage.draw();
    }

    public int getState() {
        return state;
    }

    private void updateSystems(int state) {
        boolean shouldProcess = state == STATE_RUNNING;

        engine.getSystem(AISystem.class).setProcessing(shouldProcess);
        engine.getSystem(HealthSystem.class).setProcessing(shouldProcess);
        engine.getSystem(CooldownSystem.class).setProcessing(shouldProcess);
        engine.getSystem(RunnableSystem.class).setProcessing(shouldProcess);

        pauseMenu.setVisible(state == STATE_PAUSED);
        health.setVisible(state == STATE_RUNNING);
        fps.setVisible(state == STATE_RUNNING);

        if (levelProperties != null) {
            if (mobileInput != null) {
                mobileInput.update(state);
            }

            CameraComponent camera = cameraMapper.get(cameraEntity);

            if (state == STATE_EDITING) {
                camera.setSelected(1);

                PhysicsComponent physics = physicsMapper.get(camera.getTargets()[0]);

                PhysicsComponent physics2 = physicsMapper.get(camera.getTargets()[1]);

                physics2.getPosition().set(physics.getPosition());
            } else {
                camera.setSelected(0);

                ((OrthographicCamera) camera.getCamera()).zoom = 1f;
            }

            if (state == STATE_EDITING || this.state == STATE_EDITING) {
                flipEntities(shouldProcess);
            }

            levelProperties.setVisible(!isLoading && state == STATE_EDITING);
            blockTable.setVisible(state == STATE_EDITING);
            creatureTable.setVisible(state == STATE_EDITING);
        }

        this.state = state;
    }

    private void flipEntities(boolean on) {
        ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(PhysicsComponent.class, VisualComponent.class).exclude(InvisibleComponent.class).get());

        ImmutableArray<Entity> saveEntities = engine.getEntitiesFor(Family.all(SaveComponent.class, PhysicsComponent.class).get());

        ComponentMapper<SaveComponent> saveMapper = ComponentMapper.getFor(SaveComponent.class);

        if (on) {
            for (Entity entity : saveEntities) {
                SaveComponent save = saveMapper.get(entity);

                if (save.getCategory().equals("spawner")) {
                    PhysicsComponent physics = physicsMapper.get(entity);

                    Vector3 position = physics.getPosition();

                    Entity newSpawn = spawner.spawnEntity("creature_" + save.getType(), true, position.x, position.y);

                    if (save.getType().equals("player")) {
                        updateCamera(newSpawn);
                    }

                    entity.add(new InvisibleComponent());
                }
            }
        } else {
            for (int i = 0; i < entities.size(); i++) {
                Entity entity = entities.get(i);

                if (!saveMapper.has(entity)) {
                    engine.removeEntity(entity);

                    i--;
                }
            }

            for (Entity entity : saveEntities) {
                entity.remove(InvisibleComponent.class);
            }
        }
    }

    public void togglePause() {
        if (state == STATE_RUNNING) {
            updateSystems(STATE_PAUSED);
        } else if (state == STATE_PAUSED) {
            updateSystems(STATE_RUNNING);
        }
    }

    public Spawner getSpawner() {
        return spawner;
    }

    private void setupLevelEditor(final NinjaGame game, Spawner spawner) {
        Skin skin = game.getSkin();

        state = STATE_EDITING;

        engine.addSystem(levelEditorSystem = new LevelEditorSystem(this, spawner, camera));

        Entity player = spawner.spawnPlayer(10, 10);

        spawner.spawnLevelEditor();

        cameraEntity = spawner.spawnCamera(camera);

        cameraMapper.get(cameraEntity).updateTargets(player, freeMovingGuy = spawner.spawnFreeMovingGuy());

        levelProperties = new Table();

        levelProperties.top();

        TextButton loadButton = new TextButton("Load", skin);

        loadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getPlatformFeatures().loadLevel(levelEditorSystem);
            }
        });

        levelProperties.add(loadButton).colspan(2).right().padBottom(5);

        levelProperties.row();

        TextButton save = new TextButton("Save", skin);

        save.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getPlatformFeatures().saveLevel(levelEditorSystem);
            }
        });

        levelProperties.add(save).colspan(2).right().padBottom(5);

        levelProperties.row();

        levelProperties.add(new Label("Level Width: ", skin));

        TextField width = new TextField(level.getWidth() + "", skin);

        width.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
                if (key == '\r' || key == '\n') {
                    if (!changeWidth(textField.getText())) {
                        textField.setText(level.getWidth() + "");
                    }

                    uiStage.setKeyboardFocus(null);
                }
            }
        });

        width.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());

        levelProperties.add(width).padBottom(5);

        levelProperties.row();

        levelProperties.add(new Label("Level Height: ", skin));

        TextButton playButton = new TextButton("Play", skin);

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateSystems(STATE_RUNNING);
            }
        });

        TextField height = new TextField(level.getHeight() + "", skin);

        height.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());

        height.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
                if (key == '\r' || key == '\n') {
                    if (!changeHeight(textField.getText())) {
                        textField.setText(level.getHeight() + "");
                    }

                    uiStage.setKeyboardFocus(null);
                }
            }
        });

        levelProperties.add(height).padBottom(5);

        uiStage.addActor(levelProperties);

        levelProperties.row();

        levelProperties.add(playButton).colspan(2).right();

        ObjectMap<String, Array<TextureRegion>> blocks = game.getAssetManager().getBlocks();

        blockTable = new Table();

        creatureTable = new Table();

        blockTable.align(Align.left);

        for (String key : blocks.keys()) {
            TextureRegion region = game.getAssetManager().getBlock(key);

            SelectButton button = new SelectButton(levelEditorSystem, region, "block_" + key, blockTable, creatureTable);

            blockTable.add(button).padRight(5).padBottom(8);
        }

        if (ControlManager.touchControls) {
            TextureRegion region = game.getAssetManager().getUIElement("buttons/delete.png");

            SelectButton button = new SelectButton(levelEditorSystem, region, null, blockTable, creatureTable);

            button.getColor().a = 0.5f;

            blockTable.add(button).padRight(5).padBottom(8);
        }

        blockTable.row().align(Align.left);

        final ImageButton foregroundButton = new ImageButton(new TextureRegionDrawable(game.getAssetManager().getUIElement("buttons/foreground.png")));

        blockTable.add(foregroundButton);

        final ImageButton backgroundButton = new ImageButton(new TextureRegionDrawable(game.getAssetManager().getUIElement("buttons/background.png")));

        backgroundButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                levelEditorSystem.setForeground(false);

                foregroundButton.getColor().a = 1f;
                backgroundButton.getColor().a = 0.5f;
            }
        });

        foregroundButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                levelEditorSystem.setForeground(true);

                backgroundButton.getColor().a = 1f;
                foregroundButton.getColor().a = 0.5f;
            }
        });

        foregroundButton.getColor().a = 0.5f;

        blockTable.add(backgroundButton);

        uiStage.addActor(blockTable);

        creatureTable.align(Align.left);

        OrderedMap<String, IntMap<Array<Animation>>> creatureAnimations = game.getAssetManager().getCreatureAnimations();

        for (int i = 1; i < creatureAnimations.size; i++) {
            String key = creatureAnimations.orderedKeys().get(i);

            TextureRegion region = creatureAnimations.get(key).get(0).get(0).getKeyFrame(0);

            SelectButton button = new SelectButton(levelEditorSystem, region, "spawner_" + key, blockTable, creatureTable);

            creatureTable.add(button).padRight(5).padBottom(8);
        }

        uiStage.addActor(creatureTable);

        InputMultiplexer multiplexer = new InputMultiplexer();

        multiplexer.addProcessor(uiStage);
        multiplexer.addProcessor(levelEditorSystem);

        Gdx.input.setInputProcessor(multiplexer);

        //TODO? better way to do this
        TextButton editButton = new TextButton("Edit", skin);

        editButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateSystems(STATE_EDITING);
            }
        });

        pauseMenu.row();

        pauseMenu.add(editButton);

        spawner.spawnSpawner("player", 10, 10);

        updateSystems(STATE_EDITING);
    }

    private boolean changeWidth(String width) {
        return levelEditorSystem.changeLevelSize(Integer.parseInt(width), level.getHeight());
    }

    private boolean changeHeight(String height) {
        return levelEditorSystem.changeLevelSize(level.getWidth(), Integer.parseInt(height));
    }

    public void load(FileHandle handle) {
        load(handle.readString("UTF-8"));
    }

    public void load(String text) {
        isLoading = true;

        if (this.levelProperties != null) {
            levelProperties.setVisible(false);
        }

        JsonValue json = new JsonReader().parse(text);

        JsonValue levelProperties = json.get("level_info");

        level.setWidth(levelProperties.getInt("width"));
        level.setHeight(levelProperties.getInt("height"));

        engine.getSystem(PhysicsSystem.class).remakeGrid();

        JsonValue entities = json.get("entities");

        for (JsonValue entity : entities.iterator()) {
            String id = entity.getString("id");

            if (id.startsWith("creature_")) {
                continue;
            }

            spawner.spawnEntity(id, !entity.has("foreground") || entity.getBoolean("foreground"), (float) entity.getDouble("x"), (float) entity.getDouble("y"));
        }

        isLoading = false;

        if (this.levelProperties != null) {
            this.levelProperties.setVisible(true);
        }
    }

    private void updateCamera(Entity player) {
        CameraComponent cameraComponent = cameraMapper.get(cameraEntity);

        if (this.levelProperties != null) {
            cameraComponent.updateTargets(player, freeMovingGuy);

            PhysicsComponent physics = physicsMapper.get(player);

            PhysicsComponent physics2 = physicsMapper.get(freeMovingGuy);

            physics2.getPosition().set(physics.getPosition());
        } else {
            cameraComponent.updateTargets(player);
        }

        engine.getSystem(HealthSystem.class).setPlayer(player);
    }

    @Override
    public void dispose() {
        uiStage.dispose();

        if (levelEditorSystem != null) {
            levelEditorSystem.dispose();
        }
    }

    public boolean canProcess(Entity entity) {
        boolean levelEditing = entity.getComponent(LevelEditorComponent.class) != null;

        if (state == STATE_RUNNING && !levelEditing) {
            return true;
        } else if (state == STATE_EDITING && levelEditing) {
            return true;
        } else {
            return false;
        }
    }

    public Label getHealth() {
        return health;
    }
}
