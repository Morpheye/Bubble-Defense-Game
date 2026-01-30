package cyv.app.render.game.renders.unit;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import cyv.app.BubbleGame;
import cyv.app.game.Level;
import cyv.app.game.components.BallObject;
import cyv.app.render.game.renders.ObjectRenderer;
import cyv.app.render.game.renders.UnitRenderer;

import static cyv.app.game.components.player.AbstractUnitObject.UNIT_SIZE;

public class WaterPumpRenderer extends UnitRenderer {
    private final Texture hTex;

    public WaterPumpRenderer(BubbleGame gameIn) {
        super(gameIn);
        this.hTex = getGameIn().getAssets().getTexture("unit_water_pump");
    }

    @Override
    public void render(SpriteBatch batch, BallObject b, float delta) {
        float renderX = b.getLastX() * (1 - delta) + b.getX() * delta;
        float renderY = b.getLastY() * (1 - delta) + b.getY() * delta;
        float radius = b.getRadius();
        float size = radius * 2f;
        batch.draw(hTex, renderX - radius, renderY - radius, size, size);
    }

    @Override
    public void renderHologram(SpriteBatch batch, Level levelIn, float renderX, float renderY) {
        float radius = UNIT_SIZE;
        float size = radius * 2f;
        batch.setColor(1, 1, 1, 0.5f);
        batch.draw(hTex, renderX - radius, renderY - radius, size, size);
        batch.setColor(1, 1, 1, 1);
    }
}
