package cyv.app.render.game.renders.projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import cyv.app.BubbleGame;
import cyv.app.game.components.particle.Particle;
import cyv.app.game.components.projectile.Projectile;
import cyv.app.render.game.renders.ObjectRenderer;
import cyv.app.util.MathUtils;

public class DropletProjectileRenderer extends ObjectRenderer<Projectile> {
    private final Texture tex;

    public DropletProjectileRenderer(BubbleGame gameIn) {
        super(gameIn);
        this.tex = getGameIn().getAssets().getTexture("projectile_droplet");
    }

    @Override
    public void render(SpriteBatch batch, Projectile p, float delta) {
        float renderX = p.getLastX() * (1 - delta) + p.getX() * delta;
        float renderY = p.getLastY() * (1 - delta) + p.getY() * delta;
        float radius = p.getRadius();
        float size = radius * 2f;

        float rotation = MathUtils.lerpAngleDeg(p.getLastRotation(), p.getRotation(), delta);
        boolean facingLeft = rotation > 90f || rotation < -90f;
        float renderRotation = facingLeft ? rotation + 180f : rotation;
        float scaleX = facingLeft ? -1f : 1f;

        batch.draw(tex, renderX - radius, renderY - radius, radius, radius, size, size,
            scaleX, 1f, renderRotation, 0, 0, tex.getWidth(), tex.getHeight(), false, false);
    }
}
