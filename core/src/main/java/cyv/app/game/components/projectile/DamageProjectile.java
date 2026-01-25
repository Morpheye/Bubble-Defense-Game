package cyv.app.game.components.projectile;

import cyv.app.game.Level;
import cyv.app.game.Team;
import cyv.app.game.components.BallObject;
import cyv.app.game.components.ILivingObject;

import static cyv.app.game.Level.INSIGNIFICANT_F;

public abstract class DamageProjectile extends Projectile {
    private Team team;

    public DamageProjectile(String id, float x, float y, float radius, float r, float vx, float vy,
                            int lifetime, Team team) {
        super(id, x, y, radius, r, vx, vy, lifetime);
        this.team = team;
    }

    /**
     * Gets the base damage of the projectile
     * @return Base damage
     */
    public int getDamage() {
        return 0;
    }

    /**
     * Gets the base knockback dealt by the projectile
     * @return Knockback (acceleration)
     */
    public float getKnockback() {
        return 0f;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public void tick(Level levelIn) {
        super.tick(levelIn);

        // now, check if overlapping any valid targets
        for (BallObject b : levelIn.getBalls()) {
            if (!canCollide(b)) continue;
            float dx = b.getX() - getX();
            float dy = b.getY() - getY();
            float distSq = dx * dx + dy * dy;
            float range = getRadius() + b.getRadius();

            // check if within melee range
            if (distSq <= range * range) {
                // close enough, damage the target
                collide(b);
                if (shouldInvalidateAfterCollision()) invalidate();
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
    }

    /**
     * Determines whether the projectile should be "exhausted" after a collision
     */
    private boolean shouldInvalidateAfterCollision() {
        return true;
    }
}
