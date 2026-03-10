package cyv.app.game.components.player;

import cyv.app.game.Level;
import cyv.app.game.Team;
import cyv.app.game.components.BallObject;
import cyv.app.game.components.ILivingObject;
import cyv.app.game.components.player.categories.IAnchorObject;
import cyv.app.game.components.player.categories.IShieldObject;

import java.util.ArrayList;
import java.util.TreeSet;

import static cyv.app.game.Level.INSIGNIFICANT_F;

/**
 * Represents any ball object that is a player unit or hearth.
 */
public abstract class AbstractUnitObject extends BallObject implements ILivingObject {
    public static final float UNIT_SIZE = 40f;
    private long timeLastDamaged = -10000;
    private int health = getMaxHealth();

    // physics and combat
    private BallObject lastAnchor = null;
    private final TreeSet<AbstractUnitObject> shields = new TreeSet<>((o1, o2) -> {
        // sort by health, descending order
        int cmp = Float.compare(o2.getHealth(), o1.getHealth());
        if (cmp != 0) return cmp;
        return System.identityHashCode(o1) - System.identityHashCode(o2);
    });

    public AbstractUnitObject(String id, float x, float y, float density) {
        super(id, x, y, UNIT_SIZE, density);
        setTeam(Team.PLAYER);
    }

    @Override
    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public abstract int getMaxHealth();

    public int getRegenerationDelay() {
        return 20 * 10;
    }

    public int getRegenerationInterval() {
        // at x, regenerate 1 health every x ticks.
        return 1;
    }

    /**
     * Gets the time last damaged, in ticks
     * @return Time last damaged
     */
    @Override
    public long getTimeLastDamaged() {
        return timeLastDamaged;
    }

    public BallObject getLastAnchor() {
        return lastAnchor;
    }

    protected void setLastAnchor(BallObject anchor) {
        this.lastAnchor = anchor;
    }

    public TreeSet<AbstractUnitObject> getShields() {
        return shields;
    }

    @Override
    public void doAcceleration(Level levelIn) {
        final float HOMING_FORCE = 1f;

        float anchorClosestDistSq = Float.POSITIVE_INFINITY;
        BallObject closestAnchor = null;

        for (BallObject obj : levelIn.getBalls()) {
            if (obj == this) continue;
            if (!(obj instanceof IAnchorObject)) continue;

            float dx = obj.getX() - getX();
            float dy = obj.getY() - getY();
            float distSq = dx * dx + dy * dy;

            if (distSq < anchorClosestDistSq && distSq > INSIGNIFICANT_F * INSIGNIFICANT_F) {
                anchorClosestDistSq = distSq;
                closestAnchor = obj;
            }
        }

        if (closestAnchor != null) {
            float dx = closestAnchor.getX() - getX();
            float dy = closestAnchor.getY() - getY();
            float dist = (float) Math.sqrt(anchorClosestDistSq);

            addVx(dx / dist * Math.min(dist, HOMING_FORCE));
            addVy(dy / dist * Math.min(dist, HOMING_FORCE));
        }

        lastAnchor = closestAnchor;
    }

    @Override
    public void finishTick() {
        // regenerate health if not damaged in a sufficient amount of time
        long timeSinceDamaged = getTimeLived() - getTimeLastDamaged();
        if (timeSinceDamaged >= getRegenerationDelay()) {
            int interval = getRegenerationInterval();
            if (interval < 1) interval = 1;

            // regenerate 1 health at a time
            if (getTimeLived() % interval == 0)
                setHealth(Math.min(getMaxHealth(), getHealth() + 1));
        }

        super.finishTick();
    }

    @Override
    public void damage(int amount) {
        // Shields should not redirect their own damage
        if (this instanceof IShieldObject) {
            ILivingObject.super.damage(amount);
            timeLastDamaged = getTimeLived();
            return;
        }

        int damageRemaining = amount;

        // Snapshot so each shield processes only once
        for (AbstractUnitObject shield : new ArrayList<>(shields)) {
            if (damageRemaining <= 0) break;

            // Remove already-dead shields
            if (shield.isDead()) {
                shields.remove(shield);
                continue;
            }

            IShieldObject cShield = (IShieldObject) shield;

            int redirectedDamage = cShield.redirectDamage(this, damageRemaining);
            damageRemaining -= redirectedDamage;

            if (shield.isDead()) {
                shields.remove(shield);
            }
        }

        // Apply leftover damage to this unit
        if (damageRemaining > 0) {
            ILivingObject.super.damage(damageRemaining);
        }

        timeLastDamaged = getTimeLived();
    }
}
