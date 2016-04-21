package com.nicholasnassar.ninja;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.nicholasnassar.ninja.components.StateComponent;

public class NinjaAssetManager {
    private final AssetManager manager;

    private final OrderedMap<String, Array<TextureRegion>> blocks;
    private final OrderedMap<String, IntMap<Animation>> creatureAnimations;
    private final ObjectMap<String, Music> music;

    public NinjaAssetManager() {
        manager = new AssetManager();

        blocks = new OrderedMap<String, Array<TextureRegion>>();

        creatureAnimations = new OrderedMap<String, IntMap<Animation>>();

        music = new ObjectMap<String, Music>();

        manager.load("ui/uiskin.json", Skin.class);
        manager.load("ui/buttons/foreground.png", Texture.class);
        manager.load("ui/buttons/background.png", Texture.class);

        manager.load("sprites/entities/player_idle.png", Texture.class);
        manager.load("sprites/entities/player_walk.png", Texture.class);
        manager.load("sprites/entities/player_in_air.png", Texture.class);
        manager.load("sprites/entities/soldier_idle.png", Texture.class);
        manager.load("sprites/entities/soldier_walk.png", Texture.class);
        manager.load("sprites/entities/shuriken.png", Texture.class);

        blocks.put("dirt", null);
        blocks.put("grass", null);
        blocks.put("stone", null);
        blocks.put("sand", null);
        blocks.put("sandstone", null);
        blocks.put("concrete", null);
        blocks.put("cactus", null);

        for (String key : blocks.keys()) {
            manager.load("sprites/blocks/" + key + ".png", Texture.class);
        }

        music.put("main_menu", null);

        for (String key : music.keys()) {
            manager.load("sounds/music/" + key + ".mp3", Music.class);
        }
    }

    public Skin getSkin() {
        return manager.get("ui/uiskin.json", Skin.class);
    }

    public Texture getTexture(String name) {
        return manager.get(name);
    }

    public void finishedLoading() {
        for (String key : blocks.keys()) {
            blocks.put(key, Array.with(TextureRegion.split(getTexture("sprites/blocks/" + key + ".png"), 16, 16)[0]));
        }

        String entityPath = "sprites/entities/";

        IntMap<Animation> animations = new IntMap<Animation>();

        Animation idle = new Animation(1 / 4f, TextureRegion.split(getTexture(entityPath + "player_idle.png"), 13, 27)[0]);

        idle.setPlayMode(Animation.PlayMode.LOOP);

        animations.put(StateComponent.STATE_IDLE, idle);

        Animation walk = new Animation(1 / 15f, TextureRegion.split(getTexture(entityPath + "player_walk.png"), 13, 27)[0]);

        walk.setPlayMode(Animation.PlayMode.LOOP);

        animations.put(StateComponent.STATE_WALKING, walk);

        animations.put(StateComponent.STATE_IN_AIR, new Animation(1 / 10f, TextureRegion.split(getTexture(entityPath + "player_in_air.png"), 17, 27)[0]));

        creatureAnimations.put("player", animations);

        animations = new IntMap<Animation>();

        idle = new Animation(1 / 4f, TextureRegion.split(getTexture(entityPath + "soldier_idle.png"), 18, 32)[0]);

        idle.setPlayMode(Animation.PlayMode.LOOP);

        animations.put(StateComponent.STATE_IDLE, idle);
        animations.put(StateComponent.STATE_IN_AIR, idle);

        walk = new Animation(1 / 15f, TextureRegion.split(getTexture(entityPath + "soldier_walk.png"), 18, 32)[0]);

        walk.setPlayMode(Animation.PlayMode.LOOP);
        animations.put(StateComponent.STATE_WALKING, walk);

        creatureAnimations.put("soldier", animations);

        for (String key : music.keys()) {
            Music music = manager.get("sounds/music/" + key + ".mp3");

            music.setLooping(true);

            this.music.put(key, music);
        }
    }

    public TextureRegion getUIElement(String key) {
        return new TextureRegion((Texture) manager.get("ui/" + key));
    }

    public TextureRegion getBlock(String key) {
        return getBlock(key, (int) (Math.random() * blocks.get(key).size));

    }

    private TextureRegion getBlock(String key, int variation) {
        return blocks.get(key).get(variation);
    }

    public OrderedMap<String, Array<TextureRegion>> getBlocks() {
        return blocks;
    }

    public Music getMusic(String key) {
        return music.get(key);
    }

    public OrderedMap<String, IntMap<Animation>> getCreatureAnimations() {
        return creatureAnimations;
    }

    public IntMap<Animation> getAnimationsFor(String creature) {
        return creatureAnimations.get(creature);
    }

    public boolean update() {
        return manager.update();
    }

    public void dispose() {
        manager.dispose();
    }
}
