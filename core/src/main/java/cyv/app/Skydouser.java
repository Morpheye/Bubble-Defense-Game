package cyv.app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
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

import java.util.function.Function;
import java.util.function.Supplier;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Skydouser extends Game {
    public static final boolean DEV = true;
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

        /*
        Music music = assets.getMusic("01_stolen_by_the_sky");
        music.setLooping(true);
        music.play();
         */

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
     * @param provider Level provider
     * @param parent Level group
     */
    public void beginLevel(LevelProvider provider, Function<LevelProvider, Level> func, LevelGroup parent) {
        GameScreen gameScreen = new GameScreen(this, provider, func, parent.getName());
        setScreen(gameScreen);
    }

    @Override
    public void dispose() {
        this.assets.dispose();
        this.batcher.dispose();
        this.shapeRenderer.dispose();
    }
}
