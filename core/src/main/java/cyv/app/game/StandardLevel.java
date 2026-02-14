package cyv.app.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import cyv.app.game.components.enemy.AbstractEnemyObject;
import cyv.app.game.components.enemy.EnemyGeneratorRegistry;
import cyv.app.game.components.player.HearthObject;

import java.util.*;

/**
 * Standard level with waves
 */
public abstract class StandardLevel extends Level {
    private static Json JSON = null;

    private final int spawnDelay;
    private final int waveDelay;
    private final float waveAdvanceThreshold; // ratio of health which needs to be depleted to advance

    private final List<LevelWave> waves;
    private ActiveLevelWave activeWave = null;
    private int currentWaveIndex = -1;
    private int nextWaveStartTime;

    public StandardLevel(int sizeX, int sizeY, HearthObject hearth, List<LevelWave> waves,
                         int spawnDelay, int waveDelay, float waveAdvanceThreshold) {
        super(sizeX, sizeY, hearth);
        this.waves = waves;
        this.spawnDelay = spawnDelay;
        this.waveDelay = waveDelay;
        this.waveAdvanceThreshold = waveAdvanceThreshold;
    }

    @Override
    public void start() {
        nextWaveStartTime = spawnDelay;
    }

    @Override
    public void tick() {
        super.tick();
        // new wave begins
        if (getTicks() == nextWaveStartTime && currentWaveIndex + 1 < waves.size()) {
            currentWaveIndex++;
            LevelWave newWave = waves.get(currentWaveIndex);
            activeWave = newWave.create();
            for (AbstractEnemyObject e : activeWave.getEnemies()) spawnBall(e);
        }

        // schedule new wave once health drops
        float waveAdvanceThreshold = (activeWave != null && activeWave.getAdvanceThresholdOverride() != -1f) ?
            activeWave.getAdvanceThresholdOverride() : this.waveAdvanceThreshold;

        if (nextWaveStartTime < getTicks() && activeWave != null &&
            currentWaveIndex + 1 < waves.size() &&
            (float) activeWave.getCumulativeHealth() / activeWave.getStartingHealth()
                <= (1 - waveAdvanceThreshold)) {
            int waveDelay = (activeWave != null && activeWave.getWaveDelayOverride() != -1) ?
                activeWave.getWaveDelayOverride() : this.waveDelay;
            nextWaveStartTime = getTicks() + waveDelay;
        }
    }

    @Override
    public boolean victoryConditionMet() {
        // victory is met if the currentWaveIndex >= waves.size() and no enemies remain
        return currentWaveIndex >= waves.size() && getEnemyCount() < 1;
    }

    public static StandardLevel parseLevel(String content) {
        if (JSON == null) {
            JSON = new Json();
            JSON.setIgnoreUnknownFields(true);
            JSON.setUsePrototypes(false);
        }

        SimpleLevel level = JSON.fromJson(SimpleLevel.class, content);
        List<LevelWave> waves = new ArrayList<>();
        for (SimpleLevel.RawLevelWave rawWave : level.waves) {
            Set<LevelWave.EnemyGenerator> generators = new HashSet<>();
            for (SimpleLevel.RawLevelWave.RawEnemyGenerator r : rawWave.generators) {
                generators.add(new LevelWave.EnemyGenerator(r.weight, r.cost,
                    EnemyGeneratorRegistry.getGenerator(r.id)));
            }
            // attempt pattern matching
            if (!rawWave.spawnTilePattern.isEmpty())
                rawWave.spawnTiles = level.generateSpawnTiles(rawWave.spawnTilePattern);
            LevelWave wave = new LevelWave(rawWave.costLimit, rawWave.spawnTiles, generators);
            wave.setWaveDelayOverride(rawWave.waveDelayOverride);
            wave.setAdvanceThresholdOverride(rawWave.advanceThresholdOverride);
            waves.add(wave);
        }

        return new StandardLevel(level.sizeX, level.sizeY,
            new HearthObject((level.sizeX * TILE_SIZE) / 2f, (level.sizeY * TILE_SIZE) / 2f),
            waves, level.spawnDelay, level.waveDelay, level.waveAdvanceThreshold) {
            public int waterGenerationDelay() {return level.waterGenerationDelay;}
            public int getStartingWater() {return level.startingWater;}
        };
    }

    static class SimpleLevel {
        int sizeX = 18;
        int sizeY = 11;
        int waterGenerationDelay = 50;
        int startingWater = 0;
        Array<RawLevelWave> waves;

        int spawnDelay = 20 * 10;
        int waveDelay = 20 * 2;
        float waveAdvanceThreshold = 0.5f;

        int[][] generateSpawnTiles(String pattern) {
            List<int[]> tiles = new ArrayList<>();
            switch (pattern) {
                case "FULL_BORDER": {
                    for (int i = 0; i < sizeX; i++) {
                        tiles.add(new int[] {i, 0});
                        tiles.add(new int[] {i, sizeY - 1});
                    }
                    for (int i = 1; i < sizeY - 1; i++) {
                        tiles.add(new int[] {0, i});
                        tiles.add(new int[] {sizeX - 1, i});
                    }
                }
                case "THIRDS": {
                    tiles.add(new int[] {sizeX - 1, sizeY / 2});
                    tiles.add(new int[] {sizeX / 4, 0});
                    tiles.add(new int[] {sizeX / 4, sizeY - 1});
                }
            }
            return tiles.toArray(new int[0][0]);
        }

        static class RawLevelWave {
            public Array<RawEnemyGenerator> generators;
            public int[][] spawnTiles = {};
            public String spawnTilePattern = "";
            public int costLimit;
            public boolean isHuge;

            // overrides
            public float advanceThresholdOverride = -1;
            public int waveDelayOverride = -1;

            static class RawEnemyGenerator {
                public int weight;
                public int cost;
                public String id;
            }
        }
    }
}
