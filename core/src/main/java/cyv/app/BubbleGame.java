package cyv.app;

import com.badlogic.gdx.Game;
import cyv.app.game.Level;
import cyv.app.game.components.player.HearthObject;
import cyv.app.render.TextureManager;
import cyv.app.render.game.GameScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class BubbleGame extends Game {
    private TextureManager assets;

    @Override
    public void create() {
        loadAssets();

        // TODO: make level select screen
        setScreen(new GameScreen(this, new Level(10, 7, new HearthObject(500, 350)) {}));
    }

    private void loadAssets() {
        this.assets = new TextureManager();

        // core assets
        assets.loadTexture("player_bubble_back", "textures/entities/player_bubble_back.png");
        assets.loadTexture("enemy_bubble_back", "textures/entities/enemy_bubble_back.png");
        assets.loadTexture("hearth", "textures/entities/hearth.png");

        // particles
        assets.loadTexture("particle_attack", "textures/particles/attack.png");

        // tiles
        assets.loadTileMap("grass", "textures/tiles/grass.png");
    }

    public TextureManager getAssets() {
        return this.assets;
    }

    @Override
    public void dispose() {
        this.assets.dispose();
    }

}
