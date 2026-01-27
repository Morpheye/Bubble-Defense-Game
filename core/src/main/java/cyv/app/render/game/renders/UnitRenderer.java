package cyv.app.render.game.renders;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import cyv.app.BubbleGame;
import cyv.app.game.Level;
import cyv.app.game.components.BallObject;

public abstract class UnitRenderer extends ObjectRenderer<BallObject> {
    public UnitRenderer(BubbleGame gameIn) {
        super(gameIn);
    }

    public abstract void render(SpriteBatch t, BallObject obj, float delta);

    public abstract void renderHologram(SpriteBatch batch, Level levelIn, float renderX, float renderY);
}
