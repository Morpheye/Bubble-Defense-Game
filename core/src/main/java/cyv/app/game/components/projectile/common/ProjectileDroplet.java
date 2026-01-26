package cyv.app.game.components.projectile.common;

import cyv.app.game.Team;
import cyv.app.game.components.projectile.DamageProjectile;

public class ProjectileDroplet extends DamageProjectile {
    private static final float SPEED = 25;

    public ProjectileDroplet(float x, float y, float r, Team team) {
        // lasts for 10 seconds.
        super("projectile_droplet", x, y, 10, r,
            (float) Math.cos(Math.toRadians(r)) * SPEED,
            (float) Math.sin(Math.toRadians(r)) * SPEED,
            200, team);
    }

    @Override
    public int getDamage() {
        return 10;
    }

    @Override
    public float getKnockback() {
        return 0.2f;
    }
}
