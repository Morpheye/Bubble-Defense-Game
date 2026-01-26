package cyv.app;

import com.badlogic.gdx.Game;
import cyv.app.game.Level;
import cyv.app.game.PlayerController;
import cyv.app.game.blueprints.BlueprintRegistry;
import cyv.app.game.components.player.HearthObject;
import cyv.app.render.TextureManager;
import cyv.app.render.game.GameScreen;
import cyv.app.render.game.RendererRegistry;

import java.util.Collections;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class BubbleGame extends Game {
    private TextureManager assets;

    @Override
    public void create() {
        this.assets = new TextureManager();
        loadAssets();
        RendererRegistry.registerRenders(this);
        BlueprintRegistry.registerBlueprints();

        // TODO: make level select screen
        Level level = new Level(10, 7, new HearthObject(500, 350)) {
            public int waterGenerationDelay() {return 50;}
            public int getStartingWater() {return 15;}
        };
        GameScreen screen = new GameScreen(this, level);
        PlayerController controller = new PlayerController(Collections.emptyList());
        screen.setPlayerController(controller);
        setScreen(screen);

    }

    private void loadAssets() {
        // core assets
        assets.loadTexture("player_bubble_back", "textures/entities/player_bubble_back.png");
        assets.loadTexture("enemy_bubble_back", "textures/entities/enemy_bubble_back.png");
        assets.loadTexture("unit_hearth", "textures/entities/hearth.png");
        assets.loadTexture("water_icon", "textures/gui/water_icon.png");

        // gui
        assets.loadTexture("gui_water_indicator", "textures/gui/water_indicator.png");

        // units
        assets.loadTextureMap("unit_droplet_turret", "textures/entities/units/droplet_turret.png", 2, 1);

        // particles
        assets.loadTexture("particle_attack", "textures/particles/attack.png");

        // projectiles
        assets.loadTexture("projectile_droplet", "textures/projectiles/droplet.png");

        // tiles
        final int IMAGE_TILE_WIDTH = 4;
        final int IMAGE_TILE_HEIGHT = 4;
        assets.loadTextureMap("grass", "textures/tiles/grass.png", IMAGE_TILE_WIDTH, IMAGE_TILE_HEIGHT);
    }

    public TextureManager getAssets() {
        return this.assets;
    }

    @Override
    public void dispose() {
        this.assets.dispose();
    }

}
