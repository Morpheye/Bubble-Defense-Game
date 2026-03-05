package cyv.app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import cyv.app.contents.LevelGroup;
import cyv.app.contents.LevelProvider;
import cyv.app.contents.levels.World1;
import cyv.app.game.Level;
import cyv.app.game.blueprints.BlueprintRegistry;
import cyv.app.game.components.enemy.EnemyGeneratorRegistry;
import cyv.app.render.ResourceManager;
import cyv.app.render.game.GameScreen;
import cyv.app.render.game.renders.RendererRegistry;
import cyv.app.render.levelSelect.LevelSelectScreen;

import java.util.function.Supplier;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Skydouser extends Game {
    private ResourceManager assets;
    private SpriteBatch batcher;
    private ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        this.assets = new ResourceManager();
        assets.loadNormalTextures();
        assets.loadSounds();
        RendererRegistry.registerRenders(this);
        BlueprintRegistry.registerBlueprints(this);
        EnemyGeneratorRegistry.registerGenerators();

        this.batcher = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        setScreen(new LevelSelectScreen(this, new World1()));
    }

    // Default utility objects

    public ResourceManager getAssets() {
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
