package cyv.app;

import com.badlogic.gdx.Game;
import cyv.app.game.Level;
import cyv.app.game.PlayerController;
import cyv.app.game.blueprints.BlueprintRegistry;
import cyv.app.game.components.player.HearthObject;
import cyv.app.render.TextureManager;
import cyv.app.render.game.GameScreen;
import cyv.app.render.game.RendererRegistry;

import java.util.Arrays;
import java.util.Collections;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class BubbleGame extends Game {
    private TextureManager assets;

    @Override
    public void create() {
        this.assets = new TextureManager();
        assets.loadNormalTextures();
        RendererRegistry.registerRenders(this);
        BlueprintRegistry.registerBlueprints(this);

        // TODO: make level select screen
        Level level = new Level(10, 7, new HearthObject(500, 350)) {
            public int waterGenerationDelay() {return 50;}
            public int getStartingWater() {return 50;}
        };
        GameScreen screen = new GameScreen(this, level);
        PlayerController controller = new PlayerController(Arrays.asList(
            BlueprintRegistry.getBlueprint("blueprint_droplet_turret"),
            BlueprintRegistry.getBlueprint("blueprint_droplet_turret")
        ));
        screen.setPlayerController(controller);
        setScreen(screen);

    }

    public TextureManager getAssets() {
        return this.assets;
    }

    @Override
    public void dispose() {
        this.assets.dispose();
    }

}
