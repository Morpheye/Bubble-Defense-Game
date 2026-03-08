package cyv.app.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResourceManager {
    public final Texture PIXEL;
    private final Map<String, Texture> textures = new HashMap<>();
    private final Map<String, TextureRegion[][]> textureMaps = new HashMap<>();
    private final Map<String, Sound> sounds = new HashMap<>();
    private final Map<String, Music> music = new HashMap<>();;
    private boolean texturesLoaded = false;

    public ResourceManager() {
        loadPregameTextures();
        PIXEL = textures.get("pixel");
    }

    private void loadPregameTextures() {
        // by default, comes with the "pixel" texture
        Pixmap map = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        map.setColor(Color.WHITE);
        map.fill();
        textures.put("pixel", new Texture(map));
        map.dispose();
    }

    public void loadNormalTextures() {
        if (texturesLoaded) return;
        texturesLoaded = true;

        // core assets
        loadTexture("player_bubble_back", "textures/entities/player_bubble_back.png");
        loadTexture("enemy_bubble_back", "textures/entities/enemy_bubble_back.png");
        loadTexture("bubble_selected", "textures/entities/bubble_selected.png");
        loadTexture("unit_hearth", "textures/entities/hearth.png");
        loadTexture("water_icon", "textures/gui/water_icon.png");

        // gui
        loadTexture("gui_aim_highlight", "textures/gui/aim_highlight.png");
        loadTexture("gui_water_indicator", "textures/gui/water_indicator.png");
        loadTexture("gui_pause_button", "textures/gui/buttons/pause.png");

        loadTexture("wave_progress_indicator", "textures/gui/wave_progress.png");

        loadTexture("level_select_panel", "textures/gui/level_select_panel.png");
        loadTexture("level_select_level_panel", "textures/gui/level_select_level_panel.png");

        // buttons
        loadTexture("gui_button_wide", "textures/gui/buttons/wide_normal.png");
        loadTexture("gui_button_wide_hovered", "textures/gui/buttons/wide_hovered.png");
        loadTexture("gui_button_standard", "textures/gui/buttons/standard_normal.png");
        loadTexture("gui_button_standard_hovered", "textures/gui/buttons/standard_hovered.png");

        // blueprints
        loadTexture("blueprint_selected", "textures/gui/blueprint_selected.png");
        loadTexture("blueprint_unselected", "textures/gui/blueprint_unselected.png");
        loadTexture("blueprint_empty", "textures/gui/blueprint_empty.png");
        loadTexture("blueprint_droplet_turret", "textures/gui/blueprints/droplet_turret.png");
        loadTexture("blueprint_water_pump", "textures/gui/blueprints/water_pump.png");
        loadTexture("blueprint_ripple_turret", "textures/gui/blueprints/ripple_turret.png");

        // units
        loadTextureMap("unit_droplet_turret", "textures/entities/units/droplet_turret.png", 2, 1);
        loadTextureMap("unit_water_pump", "textures/entities/units/water_pump.png", 2, 1);
        loadTextureMap("unit_ripple_turret", "textures/entities/units/ripple_turret.png", 2, 1);

        // particles
        loadTexture("particle_attack", "textures/particles/attack.png");

        // projectiles
        loadTexture("projectile_droplet", "textures/projectiles/droplet.png");
        loadTexture("projectile_ripple", "textures/projectiles/ripple.png");

        // tiles
        final int IMAGE_TILE_WIDTH = 4;
        final int IMAGE_TILE_HEIGHT = 4;
        loadTextureMap("grass", "textures/tiles/grass.png", IMAGE_TILE_WIDTH, IMAGE_TILE_HEIGHT);
    }

    public void loadSounds() {
        // gui
        loadSound("gui_click", "sounds/gui/click.wav");

        // in-game
        loadSound("projectile_droplet_spawn", "sounds/projectiles/droplet_spawn.mp3");
        loadSound("projectile_droplet_hit", "sounds/projectiles/droplet_hit.mp3");

        // music
        loadMusic("01_stolen_by_the_sky", "sounds/music/01_stolen_by_the_sky.mp3");
    }

    public void loadTexture(String id, String path) {
        if (textures.containsKey(id)) throw new IllegalArgumentException("Id " + id + " already exists.");
        Texture tex = new Texture(path);
        textures.put(id, tex);
    }

    public void unloadTexture(String id) {
        Texture tex = textures.remove(id);
        if (tex != null) tex.dispose();
    }

    public Texture getTexture(String id) {
        Texture tex = textures.get(id);
        if (tex == null) return null;
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return tex;
    }

    public void loadTextureMap(String id, String path, int width, int height) {
        if (textureMaps.containsKey(id)) throw new IllegalArgumentException("Id " + id + " already exists.");
        Texture tex = new Texture(path);
        TextureRegion[][] tiles = TextureRegion.split(tex,
            tex.getWidth() / width, tex.getHeight() / height);
        textureMaps.put(id, tiles);
    }

    public void unloadTextureMap(String id) {
        TextureRegion[][] reg = textureMaps.remove(id);
        if (reg != null) reg[0][0].getTexture().dispose();
    }

    public TextureRegion[][] getTextureMap(String id) {
        return textureMaps.get(id);
    }

    public void loadSound(String id, String path) {
        if (sounds.containsKey(id)) throw new IllegalArgumentException("Id " + id + " already exists.");
        Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
        sounds.put(id, sound);
    }

    public void unloadSound(String id) {
        Sound sound = sounds.remove(id);
        if (sound != null) sound.dispose();
    }

    public Sound getSound(String id) {
        return sounds.get(id);
    }

    public void loadMusic(String id, String path) {
        if (music.containsKey(id)) throw new IllegalArgumentException("Id " + id + " already exists.");
        Music music = Gdx.audio.newMusic(Gdx.files.internal(path));
        this.music.put(id, music);
    }

    public void unloadMusic(String id) {
        Music music = this.music.remove(id);
        if (music != null) music.dispose();
    }

    public Music getMusic(String id) {
        return music.get(id);
    }

    public void dispose() {
        for (String id : new ArrayList<>(textures.keySet())) {
            unloadTexture(id);
        }

        for (String id : new ArrayList<>(textureMaps.keySet())) {
            unloadTextureMap(id);
        }

        for (String id : new ArrayList<>(sounds.keySet())) {
            unloadSound(id);
        }

        for (String id : new ArrayList<>(music.keySet())) {
            unloadMusic(id);
        }
    }
}
