package cyv.app.render.game.renders.unit;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import cyv.app.BubbleGame;
import cyv.app.game.Level;
import cyv.app.game.components.BallObject;
import cyv.app.game.components.player.HearthObject;
import cyv.app.game.components.player.common.UnitRippleTurret;
import cyv.app.render.game.renders.UnitRenderer;
import cyv.app.util.MathUtils;

import static cyv.app.game.components.player.AbstractUnitObject.UNIT_SIZE;

public class RippleTurretRenderer extends UnitRenderer {
    private final TextureRegion mount;
    private final TextureRegion barrel;
    private final Texture aimTex;

    public RippleTurretRenderer(BubbleGame gameIn) {
        super(gameIn);
        this.aimTex = gameIn.getAssets().getTexture("gui_aim_highlight");
        TextureRegion[][] reg = gameIn.getAssets().getTextureMap("unit_ripple_turret");
        this.mount = reg[0][0];
        this.barrel = reg[0][1];
    }

    @Override
    public void render(SpriteBatch batch, BallObject b, float delta) {
        UnitRippleTurret turret = (UnitRippleTurret) b;

        float renderX = b.getLastX() * (1 - delta) + b.getX() * delta;
        float renderY = b.getLastY() * (1 - delta) + b.getY() * delta;
        float radius = b.getRadius();
        float size = radius * 2f;

        // draw mount first
        batch.draw(mount, renderX - radius, renderY - radius, size, size);

        // draw barrel
        int ticksSinceAttack = (int) (turret.getTimeLived() - turret.getTimeLastAttacked());
        int atkCd = turret.getAttackCooldown();
        if (ticksSinceAttack > atkCd) ticksSinceAttack = atkCd;
        // calculate barrel offset
        float rotation = MathUtils.lerpAngleDeg(b.getLastRotation(), b.getRotation(), delta);
        double rad = Math.toRadians(rotation);

        boolean facingLeft = rotation > 90f || rotation < -90f;
        float renderRotation = facingLeft ? rotation + 180f : rotation;

        // recoil offset (use real rotation for this)
        float barrelOffset = size * 0.1f * (float) Math.pow((float)(atkCd - ticksSinceAttack) / atkCd, 1);
        float boX = -barrelOffset * (float)Math.cos(rad);
        float boY = -barrelOffset * (float)Math.sin(rad);

        // flip by using negative scaleX
        float scaleX = facingLeft ? -1f : 1f;

        batch.draw(
            barrel,
            renderX - radius + boX,
            renderY - radius + boY + radius / 10,
            radius, radius,              // origin (center)
            size, size,                  // width/height
            scaleX, 1f,                  // flip horizontally if left
            renderRotation               // corrected rotation
        );
    }

    @Override
    public void renderHologram(SpriteBatch batch, Level levelIn, float renderX, float renderY) {
        float radius = UNIT_SIZE;
        float size = radius * 2f;
        batch.setColor(1, 1, 1, 0.5f);

        // aim
        HearthObject hearthObject = levelIn.getHearth();
        float rotation = (float) Math.toDegrees(
            Math.atan2(renderY - hearthObject.getY(), renderX - hearthObject.getX()));
        float range = UnitRippleTurret.ROTATION_RANGE;
        batch.draw(aimTex, renderX, renderY, 0, 0, UnitRippleTurret.SIGHT_RANGE, 16, 1f, 1f,
            rotation - range, 0, 0, aimTex.getWidth(), aimTex.getHeight(), false, false);
        batch.draw(aimTex, renderX, renderY, 0, 0, UnitRippleTurret.SIGHT_RANGE, 16, 1f, -1f,
            rotation + range, 0, 0, aimTex.getWidth(), aimTex.getHeight(), false, false);

        // then draw mount
        batch.draw(mount, renderX - radius, renderY - radius, size, size);
        // draw barrel
        boolean facingLeft = rotation > 90f || rotation < -90f;
        float renderRotation = facingLeft ? rotation + 180f : rotation;

        // flip by using negative scaleX
        float scaleX = facingLeft ? -1f : 1f;
        batch.draw(barrel, renderX - radius, renderY - radius + radius / 10, radius, radius,
            size, size, scaleX, 1f, renderRotation);

        batch.setColor(1, 1, 1, 1);
    }
}
