package cyv.app.render.game.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import cyv.app.render.FontRenderer;
import cyv.app.render.ResourceManager;
import cyv.app.render.game.GameScreen;

public abstract class GameScreenEffect {
    private final GameScreen screenIn;
    private int ticks = 0;

    public GameScreenEffect(GameScreen screenIn) {
        this.screenIn = screenIn;
    }

    public abstract int getLifetime();

    public abstract void render(SpriteBatch batcher, ResourceManager manager, Viewport viewport,
                       FontRenderer fontRenderer, float delta);

    public final int getTicks() {
        return ticks;
    }

    protected GameScreen getScreenIn() {
        return screenIn;
    }

    public void tick() {
        ticks++;
    }
}
