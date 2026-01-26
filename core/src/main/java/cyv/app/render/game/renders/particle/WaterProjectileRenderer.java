package cyv.app.render.game.renders.particle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import cyv.app.BubbleGame;
import cyv.app.game.components.particle.Particle;
import cyv.app.game.components.projectile.Projectile;
import cyv.app.render.game.renders.ObjectRenderer;
import cyv.app.util.MathUtils;

public class WaterProjectileRenderer extends ObjectRenderer<Particle> {
    private final Texture tex;

    public WaterProjectileRenderer(BubbleGame gameIn) {
        super(gameIn);
        this.tex = getGameIn().getAssets().getTexture("water_icon");
    }

    @Override
    public void render(SpriteBatch batch, Particle p, float delta) {
        float renderX = p.getLastX() * (1 - delta) + p.getX() * delta;
        float renderY = p.getLastY() * (1 - delta) + p.getY() * delta;
        float radius = p.getRadius();
        float size = radius * 2f;

        float a = Math.min(1, (p.getLifetime() - p.getTimeLived() + (1 - delta)) / p.getFadeTime());
        batch.setColor(1, 1, 1, a);

        batch.draw(tex, renderX - radius, renderY - radius, size, size);

        batch.setColor(1, 1, 1, 1);
    }
}
