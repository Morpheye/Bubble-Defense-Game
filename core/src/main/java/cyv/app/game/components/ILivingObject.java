package cyv.app.game.components;

/**
 * Living objects observe the following properties:
 * - They have a health, and a max health. When the health drops to zero, they die.
 * - They may or may not regenerate health over time.
 * - They can be damaged.
 */
public interface ILivingObject {
    public void setHealth(int health);

    public int getHealth();

    public int getMaxHealth();

    public long getTimeLived();

    public long getTimeLastDamaged();

    // helper methods

    /**
     * Checks if the enemy is dead. Most of the time this is just if the enemy has 0 or less health.
     * In rare cases (such as bosses) this may be false even if health is 0.
     * @return
     */
    public default boolean isDead() {
        return getHealth() <= 0;
    }
}
