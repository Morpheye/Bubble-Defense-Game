package cyv.app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import cyv.app.contents.LevelGroup;
import cyv.app.contents.LevelProvider;
import cyv.app.contents.levels.World1;
import cyv.app.game.Level;
import cyv.app.game.PlayerController;
import cyv.app.game.StandardLevel;
import cyv.app.game.blueprints.BlueprintRegistry;
import cyv.app.game.components.enemy.EnemyGeneratorRegistry;
import cyv.app.render.TextureManager;
import cyv.app.render.game.GameScreen;
import cyv.app.render.game.renders.RendererRegistry;
import cyv.app.render.levelSelect.LevelSelectScreen;

import java.util.Arrays;
import java.util.function.Supplier;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class BubbleGame extends Game {
    private TextureManager assets;
    private SpriteBatch batcher;
    private ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        this.assets = new TextureManager();
        assets.loadNormalTextures();
        RendererRegistry.registerRenders(this);
        BlueprintRegistry.registerBlueprints(this);
        EnemyGeneratorRegistry.registerGenerators();

        this.batcher = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        // TODO: make level select screen
        if (true) {
            setScreen(new LevelSelectScreen(this, new World1()));
            return;
        }

        Level level = StandardLevel.parseLevel(Gdx.files.internal("levels/level_1_2.json").readString("UTF-8"));
        GameScreen screen = new GameScreen(this, () -> level, null);
        PlayerController controller = new PlayerController(Arrays.asList(
            BlueprintRegistry.getBlueprint("blueprint_droplet_turret"),
            BlueprintRegistry.getBlueprint("blueprint_water_pump"),
            BlueprintRegistry.getBlueprint("blueprint_ripple_turret")
        ));
        screen.setPlayerController(controller);
        setScreen(screen);

    }

    // Default utility objects

    public TextureManager getAssets() {
        return this.assets;
    }

    public SpriteBatch getBatcher() {
        return batcher;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    // Flow control

    /**
     * Begins a new level from the given level group
     * @param provider Levle provider
     * @param parent Level group
     */
    public void beginLevel(LevelProvider provider, LevelGroup parent) {
        beginLevel(provider::produce, parent.getName());
    }

    public void beginLevel(Supplier<Level> provider, String parent) {
        GameScreen gameScreen = new GameScreen(this, provider, parent);
        setScreen(gameScreen);
    }

    @Override
    public void dispose() {
        this.assets.dispose();
        this.batcher.dispose();
        this.shapeRenderer.dispose();
    }
}
