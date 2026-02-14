package cyv.app.render.game.renders.unit;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import cyv.app.BubbleGame;
import cyv.app.game.Level;
import cyv.app.game.components.BallObject;
import cyv.app.game.components.player.common.UnitWaterPump;
import cyv.app.render.game.renders.ObjectRenderer;
import cyv.app.render.game.renders.UnitRenderer;

import static cyv.app.game.components.player.AbstractUnitObject.UNIT_SIZE;

public class WaterPumpRenderer extends UnitRenderer {
    private final TextureRegion pumpTex;
    private final TextureRegion waterTex;

    public WaterPumpRenderer(BubbleGame gameIn) {
        super(gameIn);
        TextureRegion[][] t = getGameIn().getAssets().getTextureMap("unit_water_pump");
        pumpTex = t[0][0];
        waterTex = t[0][1];
    }

    @Override
    public void render(SpriteBatch batch, BallObject b, float delta) {
        float renderX = b.getLastX() * (1 - delta) + b.getX() * delta;
        float renderY = b.getLastY() * (1 - delta) + b.getY() * delta;
        float radius = b.getRadius();
        float size = radius * 2f;

        // render water
        UnitWaterPump u = (UnitWaterPump) b;
        long np = u.getNextProductionTime();
        long t = u.getTimeLived();
        float ratio = 1 - (np - (t - 1 + delta)) / UnitWaterPump.MAX_PRODUCTION_TIME;
        float d = 0.25f + (0.5f - 0.25f) * ratio;

        batch.setColor(0, 0.75f, 1, 0.75f);
        float waterSizeY = ratio * size;
        batch.draw(waterTex, renderX - radius, renderY - size * d, size, waterSizeY);
        batch.setColor(1, 1, 1, 1);

        // render pump
        batch.draw(pumpTex, renderX - radius, renderY - radius, size, size);
    }

    @Override
    public void renderHologram(SpriteBatch batch, Level levelIn, float renderX, float renderY) {
        float radius = UNIT_SIZE;
        float size = radius * 2f;
        batch.setColor(1, 1, 1, 0.5f);
        batch.draw(pumpTex, renderX - radius, renderY - radius, size, size);
        batch.setColor(1, 1, 1, 1);
    }
}
