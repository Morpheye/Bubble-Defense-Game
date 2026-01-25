package cyv.app.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TextureManager {
    private final Map<String, Texture> textures = new HashMap<>();
    private final Map<String, TextureRegion[][]> tileMaps = new HashMap<>();

    public TextureManager() {
        // by default, comes with the "pixel" texture
        Pixmap map = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        map.setColor(Color.WHITE);
        map.fill();
        textures.put("pixel", new Texture(map));
        map.dispose();
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
        return textures.get(id);
    }

    public void loadTextureMap(String id, String path, int width, int height) {
        if (tileMaps.containsKey(id)) throw new IllegalArgumentException("Id " + id + " already exists.");
        Texture tex = new Texture(path);
        TextureRegion[][] tiles = TextureRegion.split(tex,
            tex.getWidth() / width, tex.getHeight() / height);
        tileMaps.put(id, tiles);
    }

    public void unloadTileMap(String id) {
        TextureRegion[][] reg = tileMaps.remove(id);
        if (reg != null) reg[0][0].getTexture().dispose();
    }

    public TextureRegion[][] getTileMap(String id) {
        return tileMaps.get(id);
    }

    public void dispose() {
        for (String id : new ArrayList<>(textures.keySet())) {
            unloadTexture(id);
        }

        for (String id : new ArrayList<>(tileMaps.keySet())) {
            unloadTileMap(id);
        }
    }
}
