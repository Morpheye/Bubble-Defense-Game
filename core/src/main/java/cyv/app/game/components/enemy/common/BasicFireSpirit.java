package cyv.app.game.components.enemy.common;

import cyv.app.game.Level;
import cyv.app.game.components.BallObject;
import cyv.app.game.components.IAnchorObject;
import cyv.app.game.components.ILivingObject;
import cyv.app.game.components.enemy.AbstractEnemyObject;

import static cyv.app.game.Level.INSIGNIFICANT_F;

public class BasicFireSpirit extends AbstractEnemyObject {
    private BallObject target;

    public BasicFireSpirit(float x, float y) {
        super("enemy_fire_spirit", x, y, 40, 1);
    }

    /**
     * Gets the movement speed in acceleration per tick
     * @return Movement speed
     */
    public float getMovementSpeed() {
        return 0.2f;
    }

    /**
     * Gets the unit that this enemy is currently targeting
     * @return target
     */
    public BallObject getTarget() {
        return target;
    }

    public void setTarget(BallObject target) {
        this.target = target;
    }

    @Override
    public void onSpawn(Level l) {
        super.onSpawn(l);
        // by default, target the hearth first
        setTarget(l.getHearth());
    }

    @Override
    public void doAcceleration(Level levelIn) {
        // accelerate towards target if not already within 75% the melee distance
        if (target == null) return;
        if (target instanceof ILivingObject && ((ILivingObject) target).isDead()) return;
        float deltaX = target.getX() - getX();
        float deltaY = target.getY() - getY();
        float rawDist = (float) Math.hypot(deltaX, deltaY);
        if (rawDist < INSIGNIFICANT_F) return;

        if (rawDist - getRadius() - target.getRadius() > getAttackRange() * 0.75f) {
            // calculate normal, then accelerate in its direction

            float accel = getMovementSpeed();
            addVx(deltaX / rawDist * accel);
            addVy(deltaY / rawDist * accel);
        }
    }

    @Override
    public void doLogic(Level levelIn) {
        // Targeting logic: attempt to find the closest anchor and then set that as the target.
        // (if there are no extra anchors, this will always be the hearth)
        // However, if there is a unit within melee range, set that as the target instead.
        float closestDistSq = Float.POSITIVE_INFINITY;
        BallObject closest = null;

        for (BallObject obj : levelIn.getBalls()) {
            if (obj == this) continue;
            if (obj.getTeam() == getTeam()) continue;

            float dx = obj.getX() - getX();
            float dy = obj.getY() - getY();
            float distSq = dx * dx + dy * dy;
            float range = getAttackRange() + getRadius() + obj.getRadius();

            // check if within melee range
            if (distSq <= range * range && distSq < closestDistSq) {
                closestDistSq = distSq;
                closest = obj;
                continue;
            }

            if (!(obj instanceof IAnchorObject)) continue;
            if (distSq < closestDistSq && distSq > INSIGNIFICANT_F * INSIGNIFICANT_F) {
                closestDistSq = distSq;
                closest = obj;
            }
        }

        setTarget(closest);
        if (target == null) return;

        // now, melee attack the target if within distance and not on cooldown
        // TODO: introduce a more robust attack system
        float range = getAttackRange() + getRadius() + target.getRadius();
        if (getTimeLived() - getLastMeleeAttackTime() >= getAttackCooldown() &&
            closestDistSq <= range * range) {
            attack(levelIn, target);
        }
    }
}
