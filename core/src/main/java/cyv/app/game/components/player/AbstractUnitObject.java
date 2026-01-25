package cyv.app.game.components.player;

import cyv.app.game.Level;
import cyv.app.game.Team;
import cyv.app.game.components.BallObject;
import cyv.app.game.components.IAnchorObject;
import cyv.app.game.components.ILivingObject;

import java.util.Set;

import static cyv.app.game.Level.INSIGNIFICANT_F;

/**
 * Represents any ball object that is a player unit or hearth.
 */
public abstract class AbstractUnitObject extends BallObject implements ILivingObject {
    private long timeLastDamaged = -10000;
    private int health = getMaxHealth();
    private BallObject lastAnchor = null;

    public AbstractUnitObject(String id, float x, float y, float radius, float density) {
        super(id, x, y, radius, density);
        setTeam(Team.PLAYER);
    }

    @Override
    public void setHealth(int health) {
        // TODO: move timeLastDamaged logic to someplace else ts is stupid
        if (health < this.health) timeLastDamaged = getTimeLived();
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

    public int getRegenerationAmount() {
        // regeneration rate bottoms out at 1 health per tick (20 hp/sec)
        // For every 100 health the object has, it regenerates 1 extra health per tick

        // Ex: for the standard unit with 50 health, it fully regenerates in 2.5 seconds.
        // For a tanky unit with 2000 health, it fully regenerates in 4.76 seconds.
        return 1 + getMaxHealth() / 100;
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

    @Override
    public void doAcceleration(Level levelIn) {
        final float HOMING_FORCE = 1f;

        float closestDistSq = Float.POSITIVE_INFINITY;
        BallObject closest = null;

        for (BallObject obj : levelIn.getBalls()) {
            if (obj == this) continue;
            if (!(obj instanceof IAnchorObject)) continue;

            float dx = obj.getX() - getX();
            float dy = obj.getY() - getY();
            float distSq = dx * dx + dy * dy;

            if (distSq < closestDistSq && distSq > INSIGNIFICANT_F * INSIGNIFICANT_F) {
                closestDistSq = distSq;
                closest = obj;
            }
        }

        if (closest != null) {
            float dx = closest.getX() - getX();
            float dy = closest.getY() - getY();
            float dist = (float) Math.sqrt(closestDistSq);

            addVx(dx / dist * Math.min(dist, HOMING_FORCE));
            addVy(dy / dist * Math.min(dist, HOMING_FORCE));
        }

        lastAnchor = closest;
    }

    @Override
    public void finishTick() {
        // regenerate health if not damaged in a sufficient amount of time
        long timeSinceDamaged = getTimeLived() - getTimeLastDamaged();
        if (timeSinceDamaged >= getRegenerationDelay()) {
            setHealth(Math.min(getMaxHealth(), getHealth() + getRegenerationAmount()));
        }

        super.finishTick();
    }
}
