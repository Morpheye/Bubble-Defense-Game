package cyv.app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import cyv.app.game.Level;
import cyv.app.game.PlayerController;
import cyv.app.game.StandardLevel;
import cyv.app.game.blueprints.BlueprintRegistry;
import cyv.app.game.components.enemy.EnemyGeneratorRegistry;
import cyv.app.render.TextureManager;
import cyv.app.render.game.GameScreen;
import cyv.app.render.game.renders.RendererRegistry;

import java.util.Arrays;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class BubbleGame extends Game {
    private TextureManager assets;

    @Override
    public void create() {
        this.assets = new TextureManager();
        assets.loadNormalTextures();
        RendererRegistry.registerRenders(this);
        BlueprintRegistry.registerBlueprints(this);
        EnemyGeneratorRegistry.registerGenerators();

        // TODO: make level select screen
        final int SIZE_X = 16 + 2;
        final int SIZE_Y = 9 + 2;
        Level level = StandardLevel.parseLevel(Gdx.files.internal("levels/level_1_1.json").readString("UTF-8"));
        GameScreen screen = new GameScreen(this, level);
        PlayerController controller = new PlayerController(Arrays.asList(
            BlueprintRegistry.getBlueprint("blueprint_droplet_turret"),
            BlueprintRegistry.getBlueprint("blueprint_water_pump"),
            BlueprintRegistry.getBlueprint("blueprint_ripple_turret")
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
