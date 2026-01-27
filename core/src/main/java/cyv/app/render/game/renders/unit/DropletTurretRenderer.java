package cyv.app.render.game.renders.unit;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import cyv.app.BubbleGame;
import cyv.app.game.Level;
import cyv.app.game.components.BallObject;
import cyv.app.game.components.player.HearthObject;
import cyv.app.game.components.player.common.UnitDropletTurret;
import cyv.app.render.game.renders.ObjectRenderer;
import cyv.app.render.game.renders.UnitRenderer;
import cyv.app.util.MathUtils;

public class DropletTurretRenderer extends UnitRenderer {
    private final TextureRegion mount;
    private final TextureRegion barrel;

    public DropletTurretRenderer(BubbleGame gameIn) {
        super(gameIn);
        TextureRegion[][] reg = getGameIn().getAssets().getTextureMap("unit_droplet_turret");
        this.mount = reg[0][0];
        this.barrel = reg[0][1];
    }

    @Override
    public void render(SpriteBatch batch, BallObject b, float delta) {
        UnitDropletTurret turret = (UnitDropletTurret) b;

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
        float barrelOffset = size * 0.1f * (float) Math.pow((float)(atkCd - ticksSinceAttack) / atkCd, 3);
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
        float radius = 40;
        float size = radius * 2f;
        batch.setColor(1, 1, 1, 0.5f);

        // draw mount first
        batch.draw(mount, renderX - radius, renderY - radius, size, size);
        // draw barrel
        HearthObject hearthObject = levelIn.getHearth();
        float rotation = (float) Math.toDegrees(
            Math.atan2(renderY - hearthObject.getY(), renderX - hearthObject.getX()));
        boolean facingLeft = rotation > 90f || rotation < -90f;
        float renderRotation = facingLeft ? rotation + 180f : rotation;

        // flip by using negative scaleX
        float scaleX = facingLeft ? -1f : 1f;
        batch.draw(barrel, renderX - radius, renderY - radius + radius / 10, radius, radius,
            size, size, scaleX, 1f, renderRotation);

        batch.setColor(1, 1, 1, 1);
    }
}
