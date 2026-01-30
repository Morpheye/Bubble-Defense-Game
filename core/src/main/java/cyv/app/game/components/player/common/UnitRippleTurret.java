package cyv.app.game.components.player.common;

import cyv.app.game.components.player.AbstractTurret;
import cyv.app.game.components.projectile.Projectile;
import cyv.app.game.components.projectile.common.ProjectileDroplet;
import cyv.app.game.components.projectile.common.ProjectileRipple;

import static cyv.app.game.Level.TILE_SIZE;

public class UnitRippleTurret extends AbstractTurret {
    public static final float SIGHT_RANGE = 4f * TILE_SIZE;
    public static final float ROTATION_RANGE = 45f;

    public UnitRippleTurret(float x, float y) {
        super("unit_ripple_turret", x, y);
    }

    @Override
    public int getMaxHealth() {
        return 50;
    }

    /**
     * Gets the cooldown in ticks between attacks
     * @return Attack cooldown
     */
    @Override
    public int getAttackCooldown() {
        return 20;
    }

    @Override
    public float getRotationRange() {
        // by default, can see in an arc of 30 degrees
        return ROTATION_RANGE;
    }

    @Override
    public float getSightRange() {
        // sight range of 7 tiles
        return SIGHT_RANGE;
    }

    @Override
    public Projectile getProjectile() {
        float rotation = getRotation();
        float rad = (float) Math.toRadians(rotation);

        // slight offset forward
        float oX = (float) Math.cos(rad) * getRadius();
        float oY = (float) Math.sin(rad) * getRadius();

        return new ProjectileRipple(getX() + oX, getY() + oY, rotation, getTeam(), SIGHT_RANGE);
    }

}
