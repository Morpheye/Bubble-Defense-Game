package cyv.app.game.components.player.common;

import cyv.app.game.components.player.AbstractTurret;
import cyv.app.game.components.projectile.Projectile;
import cyv.app.game.components.projectile.common.ProjectileDroplet;

import static cyv.app.game.Level.TILE_SIZE;

public class UnitDropletTurret extends AbstractTurret {
    public UnitDropletTurret(float x, float y) {
        super("unit_droplet_turret", x, y);
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
        // defaults to once per second
        return 20;
    }

    @Override
    public float getRotationRange() {
        // by default, can see in an arc of 30 degrees
        return 30f;
    }

    @Override
    public float getSightRange() {
        // sight range of 7 tiles
        return 7 * TILE_SIZE;
    }

    @Override
    public Projectile getProjectile() {
        float rotation = getRotation();
        final float PROJECTILE_SPEED = 10;
        float rad = (float) Math.toRadians(rotation);

        // slight offset forward
        float oX = (float) Math.cos(rad) * getRadius();
        float oY = (float) Math.sin(rad) * getRadius();

        // projectile speeds
        float sX = (float) Math.cos(rad) * PROJECTILE_SPEED;
        float sY = (float) Math.sin(rad) * PROJECTILE_SPEED;

        return new ProjectileDroplet(getX() + oX, getY() + oY, rotation, getTeam());
    }

}
