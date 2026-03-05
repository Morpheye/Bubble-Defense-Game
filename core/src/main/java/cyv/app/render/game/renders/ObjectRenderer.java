package cyv.app.render.game.renders;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import cyv.app.Skydouser;

public abstract class ObjectRenderer<T> {
    private final Skydouser gameIn;

    public ObjectRenderer(Skydouser gameIn) {
        this.gameIn = gameIn;
    }

    public abstract void render(SpriteBatch t, T obj, float delta);

    protected Skydouser getGameIn() {
        return gameIn;
    }
}
