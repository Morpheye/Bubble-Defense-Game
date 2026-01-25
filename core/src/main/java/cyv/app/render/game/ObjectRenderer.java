package cyv.app.render.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import cyv.app.BubbleGame;

public abstract class ObjectRenderer<T> {
    private final BubbleGame gameIn;

    public ObjectRenderer(BubbleGame gameIn) {
        this.gameIn = gameIn;
    }

    public abstract void render(SpriteBatch t, T obj, float delta);

    protected BubbleGame getGameIn() {
        return gameIn;
    }
}
