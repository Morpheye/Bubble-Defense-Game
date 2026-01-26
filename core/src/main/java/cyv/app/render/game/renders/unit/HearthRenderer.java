package cyv.app.render.game.renders.unit;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import cyv.app.BubbleGame;
import cyv.app.game.components.BallObject;
import cyv.app.render.game.renders.ObjectRenderer;

public class HearthRenderer extends ObjectRenderer<BallObject> {
    private final Texture hTex;

    public HearthRenderer(BubbleGame gameIn) {
        super(gameIn);
        this.hTex = getGameIn().getAssets().getTexture("unit_hearth");
    }

    @Override
    public void render(SpriteBatch batch, BallObject b, float delta) {
        float renderX = b.getLastX() * (1 - delta) + b.getX() * delta;
        float renderY = b.getLastY() * (1 - delta) + b.getY() * delta;
        float radius = b.getRadius();
        float size = radius * 2f;
        batch.draw(hTex, renderX - radius, renderY - radius, size, size);
    }
}
