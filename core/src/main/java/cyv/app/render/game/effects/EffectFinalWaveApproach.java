package cyv.app.render.game.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import cyv.app.render.FontRenderer;
import cyv.app.render.ResourceManager;
import cyv.app.render.game.GameScreen;

public class EffectFinalWaveApproach extends GameScreenEffect {
    public EffectFinalWaveApproach(GameScreen screenIn) {
        super(screenIn);
    }

    @Override
    public int getLifetime() {
        return 80;
    }

    @Override
    public void render(SpriteBatch batcher, ResourceManager manager, Viewport viewport,
                       FontRenderer fontRenderer, float delta) {
        final float FADE_IN_TIME = 10;
        final float FADE_OUT_TIME = 30;
        float ticks = getTicks() + delta;
        float alpha;
        if (ticks < FADE_IN_TIME) {
            alpha = ticks / FADE_IN_TIME;
        } else if (ticks > getLifetime() - FADE_OUT_TIME) {
            alpha = (getLifetime() - ticks) / FADE_OUT_TIME;
        } else alpha = 1;
        alpha = Math.min(1, Math.max(0, alpha));
        fontRenderer.setColor(1, 1, 1, alpha);

        fontRenderer.setSize(50);
        fontRenderer.drawCenterBoth(batcher, "FINAL WAVE APPROACHING",
            viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2);
        fontRenderer.setColor(1, 1, 1, 1);
    }

    @Override
    public void tick() {
        super.tick();
        if (getTicks() == 1) getScreenIn().playSound("thunder");
    }
}
