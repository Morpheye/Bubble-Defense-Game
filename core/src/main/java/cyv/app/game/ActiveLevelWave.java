package cyv.app.game;

import cyv.app.game.components.enemy.AbstractEnemyObject;

import java.util.Set;

public class ActiveLevelWave {
    private final Set<AbstractEnemyObject> enemies;
    private final int startingHealth;

    private final float advanceThresholdOverride;
    private final int waveDelayOverride;

    private int enemyCount;
    private int cumulativeHealth;

    public ActiveLevelWave(Set<AbstractEnemyObject> enemies, int enemyCount, int cumulativeHealth,
                           float advanceThresholdOverride, int waveDelayOverride) {
        this.enemies = enemies;
        this.enemyCount = enemyCount;
        this.cumulativeHealth = cumulativeHealth;
        this.startingHealth = cumulativeHealth;

        this.advanceThresholdOverride = advanceThresholdOverride;
        this.waveDelayOverride = waveDelayOverride;

        for (AbstractEnemyObject e : enemies) e.setWave(this);
    }

    public Set<AbstractEnemyObject> getEnemies() {
        return enemies;
    }

    public int getEnemyCount() {
        return enemyCount;
    }

    public void setEnemyCount(int enemyCount) {
        this.enemyCount = enemyCount;
    }

    public void decrementCount() {
        this.enemyCount--;
    }

    public int getCumulativeHealth() {
        return cumulativeHealth;
    }

    public void setCumulativeHealth(int cumulativeHealth) {
        this.cumulativeHealth = cumulativeHealth;
    }

    public void changeCumulativeHealth(int amount) {
        this.cumulativeHealth += amount;
    }

    public int getStartingHealth() {
        return startingHealth;
    }

    public float getAdvanceThresholdOverride() {
        return advanceThresholdOverride;
    }

    public int getWaveDelayOverride() {
        return waveDelayOverride;
    }
}
