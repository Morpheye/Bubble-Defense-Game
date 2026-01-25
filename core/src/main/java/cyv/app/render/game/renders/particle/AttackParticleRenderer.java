package cyv.app.render.game.renders.particle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import cyv.app.BubbleGame;
import cyv.app.game.components.particle.Particle;
import cyv.app.render.game.ObjectRenderer;

public class AttackParticleRenderer extends ObjectRenderer<Particle> {
    private Texture tex;

    public AttackParticleRenderer(BubbleGame gameIn) {
        super(gameIn);
        this.tex = getGameIn().getAssets().getTexture("particle_attack");
    }

    @Override
    public void render(SpriteBatch batch, Particle p, float delta) {
        float renderX = p.getLastX() * (1 - delta) + p.getX() * delta;
        float renderY = p.getLastY() * (1 - delta) + p.getY() * delta;
        float rotation = p.getLastRotation() * (1 - delta) + p.getRotation() * delta;
        float radius = p.getRadius();
        float size = radius * 2f;

        float a = Math.min(1, (p.getLifetime() - p.getTimeLived() + (1 - delta)) / p.getFadeTime());
        batch.setColor(1, 1, 1, a);

        batch.draw(tex, renderX - radius, renderY - radius, radius, radius, size, size,
            1f, 1f, rotation, 0, 0, tex.getWidth(), tex.getHeight(), false, p.isMirrored());

        batch.setColor(1, 1, 1, 1);
    }
}
