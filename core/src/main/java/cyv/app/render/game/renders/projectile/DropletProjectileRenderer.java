package cyv.app.render.game.renders.projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import cyv.app.BubbleGame;
import cyv.app.game.components.particle.Particle;
import cyv.app.game.components.projectile.Projectile;
import cyv.app.render.game.renders.ObjectRenderer;

public class DropletProjectileRenderer extends ObjectRenderer<Projectile> {
    private final Texture tex;

    public DropletProjectileRenderer(BubbleGame gameIn) {
        super(gameIn);
        // TODO: make an actual fucking water droplet texture
        this.tex = getGameIn().getAssets().getTexture("player_bubble_back");
    }

    @Override
    public void render(SpriteBatch batch, Projectile p, float delta) {
        float renderX = p.getLastX() * (1 - delta) + p.getX() * delta;
        float renderY = p.getLastY() * (1 - delta) + p.getY() * delta;
        float rotation = p.getLastRotation() * (1 - delta) + p.getRotation() * delta;
        float radius = p.getRadius();
        float size = radius * 2f;

        batch.draw(tex, renderX - radius, renderY - radius, radius, radius, size, size,
            1f, 1f, rotation, 0, 0, tex.getWidth(), tex.getHeight(), false, false);
    }
}
