package com.nicholasnassar.ninja;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
    private final ObjectMap<String, TextureRegion> entities;
    private final ObjectMap<String, Music> music;

    public NinjaAssetManager() {
        manager = new AssetManager();

        blocks = new OrderedMap<String, Array<TextureRegion>>();

        creatureAnimations = new OrderedMap<String, IntMap<Animation>>();

        entities = new ObjectMap<String, TextureRegion>();

        music = new ObjectMap<String, Music>();

        manager.load("ui/uiskin.json", Skin.class);
        manager.load("ui/buttons/foreground.png", Texture.class);
        manager.load("ui/buttons/background.png", Texture.class);

        manager.load("pack.atlas", TextureAtlas.class);

        music.put("main_menu", null);

        for (String key : music.keys()) {
            manager.load("sounds/music/" + key + ".mp3", Music.class);
        }
    }

    public Skin getSkin() {
        return manager.get("ui/uiskin.json", Skin.class);
    }

    private Texture getTexture(String name) {
        return manager.get(name);
    }

    public TextureRegion getEntity(String name) {
        return entities.get(name);
    }

    public void finishedLoading() {
        TextureAtlas atlas = manager.get("pack.atlas");

        ObjectMap<String, Sprite> spriteAnimations = new ObjectMap<String, Sprite>();

        for (TextureAtlas.AtlasRegion region : atlas.getRegions()) {
            String regionName = region.name;

            String name = regionName.substring(regionName.indexOf("/") + 1);

            Sprite sprite = atlas.createSprite(regionName);

            if (regionName.startsWith("blocks")) {
                blocks.put(name, Array.with(sprite.split(16, 16)[0]));
            } else if (regionName.startsWith("animations")) {
                spriteAnimations.put(name, sprite);
            } else if (regionName.startsWith("entities")) {
                entities.put(name, sprite);
            }
        }

        IntMap<Animation> animations = new IntMap<Animation>();

        Animation idle = new Animation(1 / 4f, spriteAnimations.get("player_idle").split(13, 27)[0]);

        idle.setPlayMode(Animation.PlayMode.LOOP);

        animations.put(StateComponent.STATE_IDLE, idle);

        Animation walk = new Animation(1 / 15f, spriteAnimations.get("player_walk").split(13, 27)[0]);

        walk.setPlayMode(Animation.PlayMode.LOOP);

        animations.put(StateComponent.STATE_WALKING, walk);

        animations.put(StateComponent.STATE_IN_AIR, new Animation(1 / 10f, spriteAnimations.get("player_in_air").split(17, 27)[0]));

        TextureRegion[] split = spriteAnimations.get("player_roll").split(20, 27)[0];

        TextureRegion[] newSplit = new TextureRegion[split.length - 1];

        for (int i = 0; i < newSplit.length; i++) {
            newSplit[i] = split[i];
        }

        Animation roll = new Animation(1 / 25f, newSplit);

        animations.put(StateComponent.STATE_GROUND_ROLL, roll);

        creatureAnimations.put("player", animations);

        animations = new IntMap<Animation>();

        idle = new Animation(1 / 4f, spriteAnimations.get("soldier_idle").split(18, 32)[0]);

        idle.setPlayMode(Animation.PlayMode.LOOP);

        animations.put(StateComponent.STATE_IDLE, idle);
        animations.put(StateComponent.STATE_IN_AIR, idle);

        walk = new Animation(1 / 15f, spriteAnimations.get("soldier_walk").split(18, 32)[0]);

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
