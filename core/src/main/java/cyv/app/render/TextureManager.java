package cyv.app.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TextureManager {
    public final Texture PIXEL;
    private final Map<String, Texture> textures = new HashMap<>();
    private final Map<String, TextureRegion[][]> textureMaps = new HashMap<>();
    private boolean texturesLoaded = false;

    public TextureManager() {
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
        loadTexture("unit_hearth", "textures/entities/hearth.png");
        loadTexture("water_icon", "textures/gui/water_icon.png");

        // gui
        loadTexture("gui_aim_highlight", "textures/gui/aim_highlight.png");
        loadTexture("gui_water_indicator", "textures/gui/water_indicator.png");
        loadTexture("gui_pause_button", "textures/gui/pause.png");

        // blueprints
        loadTexture("blueprint_selected", "textures/gui/blueprint_selected.png");
        loadTexture("blueprint_empty", "textures/gui/blueprint_empty.png");
        loadTexture("blueprint_droplet_turret", "textures/gui/blueprints/droplet_turret.png");

        // units
        loadTextureMap("unit_droplet_turret", "textures/entities/units/droplet_turret.png", 2, 1);
        loadTexture("unit_water_pump", "textures/entities/units/water_pump.png");
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

    public void dispose() {
        for (String id : new ArrayList<>(textures.keySet())) {
            unloadTexture(id);
        }

        for (String id : new ArrayList<>(textureMaps.keySet())) {
            unloadTextureMap(id);
        }
    }
}
