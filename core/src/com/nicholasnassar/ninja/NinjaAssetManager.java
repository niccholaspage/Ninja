package com.nicholasnassar.ninja;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.*;

public class NinjaAssetManager {
    private final AssetManager manager;

    private final OrderedMap<String, Array<TextureRegion>> blocks;
    private final OrderedMap<String, IntMap<Array<Animation>>> creatureAnimations;
    private final ObjectMap<String, TextureRegion> entities;
    private final ObjectMap<String, Music> music;

    public NinjaAssetManager() {
        manager = new AssetManager();

        blocks = new OrderedMap<String, Array<TextureRegion>>();

        creatureAnimations = new OrderedMap<String, IntMap<Array<Animation>>>();

        entities = new ObjectMap<String, TextureRegion>();

        music = new ObjectMap<String, Music>();

        manager.load("ui/uiskin.json", Skin.class);
        manager.load("ui/buttons/foreground.png", Texture.class);
        manager.load("ui/buttons/background.png", Texture.class);
        manager.load("ui/buttons/delete.png", Texture.class);

        manager.load("pack.atlas", TextureAtlas.class);

        music.put("main_menu", null);

        for (String key : music.keys()) {
            manager.load("sounds/music/" + key + ".mp3", Music.class);
        }
    }

    public Skin getSkin() {
        return manager.get("ui/uiskin.json", Skin.class);
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

            if (regionName.startsWith("animations")) {
                spriteAnimations.put(name, sprite);
            } else if (regionName.startsWith("entities")) {
                entities.put(name, sprite);
            }
        }

        addBlocks(atlas);

        JsonValue json = new JsonReader().parse(Gdx.files.internal("animations.json"));

        for (JsonValue creature : json.get("animations").iterator()) {
            IntMap<Array<Animation>> animations = new IntMap<Array<Animation>>();

            String creatureName = creature.get(0).name;

            for (JsonValue state : creature.get(0).iterator()) {
                String[] states = state.get(0).name.split(",");

                int[] stateInts = new int[states.length];

                for (int i = 0; i < stateInts.length; i++) {
                    stateInts[i] = Integer.parseInt(states[i]);
                }

                Array<Animation> loadAnimations = new Array<Animation>();

                for (JsonValue animation : state.get(0).iterator()) {
                    String name = animation.getString("sprite");

                    float fps = animation.getFloat("fps");

                    int frameWidth = animation.getInt("frame_width", -1);

                    int frameHeight = animation.getInt("frame_height", 1);

                    Animation anim;

                    if (frameWidth != -1 && frameHeight != -1) {
                        anim = new Animation(fps, spriteAnimations.get(name).split(frameWidth, frameHeight)[0]);
                    } else {
                        anim = new Animation(fps, spriteAnimations.get(name));
                    }

                    if (animation.has("play_mode")) {
                        anim.setPlayMode(Animation.PlayMode.valueOf(animation.getString("play_mode").toUpperCase()));
                    }

                    loadAnimations.add(anim);
                }

                for (int stateNum : stateInts) {
                    animations.put(stateNum, loadAnimations);
                }
            }

            creatureAnimations.put(creatureName, animations);
        }

        for (String key : music.keys()) {
            Music music = manager.get("sounds/music/" + key + ".mp3");

            music.setLooping(true);

            this.music.put(key, music);
        }
    }

    private void addBlocks(TextureAtlas atlas) {
        JsonValue json = new JsonReader().parse(Gdx.files.internal("blocks.json"));

        for (JsonValue block : json.get("blocks").iterator()) {
            String sprite = block.getString("sprite");

            blocks.put(sprite, Array.with(atlas.createSprite("blocks/" + sprite).split(16, 16)[0]));
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
        Music track = music.get(key);

        track.setVolume(OptionsManager.musicVolume);

        return track;
    }

    public OrderedMap<String, IntMap<Array<Animation>>> getCreatureAnimations() {
        return creatureAnimations;
    }

    public IntMap<Array<Animation>> getAnimationsFor(String creature) {
        return creatureAnimations.get(creature);
    }

    public boolean update() {
        return manager.update();
    }

    public void dispose() {
        manager.dispose();
    }
}
