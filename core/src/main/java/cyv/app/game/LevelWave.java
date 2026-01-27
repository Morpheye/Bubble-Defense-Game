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
        int totalWeight = 0;
        for (EnemyGenerator g : enemies) totalWeight += g.weight;

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
            // Stop if we can't afford anything
            if (chosen == null || chosen.cost > costRemaining) break;
            AbstractEnemyObject enemy = chosen.generate(this);
            set.add(enemy);
            count++;
            health += enemy.getHealth();
            costRemaining -= chosen.cost;
        }

        return new ActiveLevelWave(set, count, health);
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

        public AbstractEnemyObject generate(LevelWave wave) {
            int[] tile = wave.spawnTiles[(int) (Math.random() * wave.spawnTiles.length)];
            float x = (tile[0] + 0.5f) * TILE_SIZE;
            float y = (tile[1] + 0.5f) * TILE_SIZE;
            return generator.apply(x, y);
        }
    }
}
