package cyv.app.game.components.projectile.common;

import cyv.app.game.Level;
import cyv.app.game.Team;
import cyv.app.game.components.BallObject;
import cyv.app.game.components.ILivingObject;
import cyv.app.game.components.projectile.Projectile;
import cyv.app.util.MathUtils;

import java.util.HashSet;
import java.util.Set;

import static cyv.app.game.Level.INSIGNIFICANT_F;

public class ProjectileRipple extends Projectile {
    private final float maxRange;
    private float lastRadius;
    private final Set<BallObject> collidees = new HashSet<>();

    // this should be a DamageProjectile but the collision detection is completely different,
    // so this will be a class of its own
    public ProjectileRipple(float x, float y, float r, Team team, float maxRange) {
        super("projectile_ripple", x, y, 1, r, 0, 0, 200);
        this.maxRange = maxRange;
        setTeam(team);
    }

    public int getDamage() {
        return 10;
    }

    public float getKnockback() {
        return 0.2f;
    }

    public float getLastRadius() {
        return lastRadius;
    }

    public float getMaxRange() {
        return maxRange;
    }

    @Override
    public void tick(Level levelIn) {
        // ripple projectile requires radius to be lerpable
        this.lastRadius = getRadius();
        super.tick(levelIn);

        // increase radius
        final float RATE_OF_EXPANSION = 10;
        setRadius(getRadius() + RATE_OF_EXPANSION);
        // invalidate once too big
        if (getRadius() >= maxRange) invalidate();

        // now, check if overlapping any valid targets
        for (BallObject b : levelIn.getBalls()) {
            if (!canCollide(b)) continue;
            float dx = b.getX() - getX();
            float dy = b.getY() - getY();
            float distSq = dx * dx + dy * dy;
            float range = getRadius() + b.getRadius();

            // check if within melee range AND within angle bound
            final float ANGLE_WIDTH = 45f;
            float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
            float delta = MathUtils.normalizeAngle(angle - getRotation());
            if (distSq <= range * range && Math.abs(delta) < ANGLE_WIDTH * 2) {
                // close enough, damage the target
                collide(b);
            }

            // stop the loop if the projectile invalidates (which will be true for non-piercing ones
            if (!isValid()) break;
        }
    }

    /**
     * Determines whether this projectile can actually hit the target
     * @param b Target
     * @return Can collide?
     */
    private boolean canCollide(BallObject b) {
        if (b.getTeam() == getTeam()) return false;
        if (collidees.contains(b)) return false;
        if (b instanceof ILivingObject) {
            ILivingObject l = (ILivingObject) b;
            return !l.isDead();
        }
        return true;
    }

    /**
     * Deals damage to the target.
     * @param b Target
     */
    private void collide(BallObject b) {
        // deal damage
        if (b instanceof ILivingObject) {
            ILivingObject l = (ILivingObject) b;
            int damage = getDamage();
            l.setHealth(l.getHealth() - damage);
        }

        // deal knockback
        float vx = getVx();
        float vy = getVy();
        float kb = getKnockback();
        float normal = (float) Math.hypot(vx, vy);
        if (normal > INSIGNIFICANT_F) {
            b.setVx(b.getVx() + vx / normal * kb);
            b.setVy(b.getVy() + vy / normal * kb);
        }

        // mark as collided
        collidees.add(b);
    }
}
