package cyv.app.game;

import com.badlogic.gdx.math.MathUtils;
import cyv.app.game.components.enemy.AbstractEnemyObject;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

import static cyv.app.game.Level.TILE_SIZE;

/**
 * Represents a single wave of enemies
 */
public class LevelWave {
    private final Set<EnemyGenerator> enemies;
    private final int[][] spawnTiles;
    private final int costLimit;

    private float advanceThresholdOverride = -1; // amount of this wave's health depletion needed
    private int waveDelayOverride = -1; // the time needed to wait after this wave is done

    public LevelWave(int costlimit, int[][] spawnTiles, Set<EnemyGenerator> enemies) {
        this.enemies = enemies;
        this.spawnTiles = spawnTiles;
        this.costLimit = costlimit;
    }

    public ActiveLevelWave create() {
        Set<AbstractEnemyObject> set = new HashSet<>();
        int count = 0;
        int health = 0;

        int costRemaining = costLimit;

        // --- Prepare weighted enemy selection ---
        int totalWeight = 0;
        for (EnemyGenerator g : enemies) totalWeight += g.weight;

        // --- Prepare evenly distributed spawn order ---
        int[][] shuffledTiles = spawnTiles.clone();
        shuffleArray(shuffledTiles);  // custom shuffle
        int tileIndex = 0;

        while (costRemaining > 0) {
            EnemyGenerator chosen = null;
            int random = MathUtils.random(totalWeight - 1);
            int cumulative = 0;

            for (EnemyGenerator g : enemies) {
                cumulative += g.weight;
                if (random < cumulative) {
                    chosen = g;
                    break;
                }
            }

            if (chosen == null || chosen.cost > costRemaining) break;

            // --- Round-robin tile selection ---
            int[] tile = shuffledTiles[tileIndex];
            tileIndex++;

            if (tileIndex >= shuffledTiles.length) {
                shuffleArray(shuffledTiles); // reshuffle for next cycle
                tileIndex = 0;
            }

            float x = (tile[0] + 0.5f) * TILE_SIZE;
            float y = (tile[1] + 0.5f) * TILE_SIZE;

            AbstractEnemyObject enemy = chosen.generator.apply(x, y);
            set.add(enemy);

            count++;
            health += enemy.getHealth();
            costRemaining -= chosen.cost;
        }

        return new ActiveLevelWave(set, count, health, advanceThresholdOverride, waveDelayOverride);
    }

    private static void shuffleArray(int[][] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = MathUtils.random(i);
            int[] temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public float getAdvanceThresholdOverride() {
        return advanceThresholdOverride;
    }

    public void setAdvanceThresholdOverride(float advanceThresholdOverride) {
        this.advanceThresholdOverride = advanceThresholdOverride;
    }

    public int getWaveDelayOverride() {
        return waveDelayOverride;
    }

    public void setWaveDelayOverride(int waveDelayOverride) {
        this.waveDelayOverride = waveDelayOverride;
    }

    public static class EnemyGenerator {
        public final int weight;
        public final int cost;
        private final BiFunction<Float, Float, AbstractEnemyObject> generator;
        EnemyGenerator(int weight, int cost, BiFunction<Float, Float, AbstractEnemyObject> generator) {
            this.weight = weight;
            this.cost = cost;
            this.generator = generator;
        }
    }
}
