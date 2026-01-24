package cyv.app.game.components.enemy;

import cyv.app.game.Level;
import cyv.app.game.Team;
import cyv.app.game.components.BallObject;
import cyv.app.game.components.GravityType;
import cyv.app.game.components.ILivingObject;
import cyv.app.game.components.particle.AttackParticle;

/**
 * Represents any ball object that is an enemy.
 */
public abstract class AbstractEnemyObject extends BallObject implements ILivingObject {
    private long timeLastDamaged = -10000;
    private long timeLastMeleeAttacked = -10000;
    private int health = getMaxHealth();

    public AbstractEnemyObject(String id, float x, float y, float radius, float density) {
        super(id, x, y, radius, density);
        setTeam(Team.ENEMY);
    }

    /**
     * Gets the base damage dealt per melee attack, before extra effects.
     * @return Base melee damage
     */
    public int getBaseMeleeDamage() {
        return 10;
    }

    /**
     * Gets the final damage dealt per melee attack, after extra effects.
     * @return Final melee damage
     */
    public int getFinalMeleeDamage() {
        // for now, effects aren't implemented.
        return getBaseMeleeDamage();
    }

    /**
     * Cooldown in ticks in between melee attacks
     * @return Attack cooldown in ticks
     */
    public long getAttackCooldown() {
        // defaults to 15 (about 0.75 seconds)
        return 15;
    }

    /**
     * Gets the time this enemy last attacked, in ticks
     * @return Last attack time
     */
    public long getLastMeleeAttackTime() {
        return this.timeLastMeleeAttacked;
    }

    /**
     * Fires off an attack. As of right now, this has no logic and only updates attack time.
     */
    protected void attack(Level levelIn, BallObject target) {
        this.timeLastMeleeAttacked = getTimeLived();
        if (!(target instanceof ILivingObject)) return;

        // damage target
        ILivingObject lt = (ILivingObject) target;
        lt.setHealth(lt.getHealth() - getFinalMeleeDamage());

        // create particle
        double direction = Math.atan2(target.getY() - getY(), target.getX() - getX());
        float offset = getAttackRange();
        final float RANDOM_WIDTH = 50;
        float random = (RANDOM_WIDTH / 2) - (float) Math.random() * RANDOM_WIDTH;

        AttackParticle particle = new AttackParticle(
            getX() + offset * (float) Math.cos(direction),
            getY() + offset * (float) Math.sin(direction),
            getRadius(),
            (float) (direction * 180 / Math.PI) + random
        );
        levelIn.spawnParticle(particle);

    }

    /**
     * Range of which a valid target has to be for this enemy to attack
     * @return Attack range
     */
    public float getAttackRange() {
        // intended calculation:
        // rawDist = Math.hypot(target.x - this.x, target.y - this.y);
        // doAttack = (rawDist - target.radius - this.radius) < getAttackRange()
        return 50f;
    }

    // normal living object overrides below

    @Override
    public void setHealth(int health) {
        if (health < this.health) timeLastDamaged = getTimeLived();
        this.health = health;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public int getMaxHealth() {
        return 50;
    }

    /**
     * Gets the time last damaged, in ticks
     * @return Time last damaged
     */
    @Override
    public long getTimeLastDamaged() {
        return timeLastDamaged;
    }
}
