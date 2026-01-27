package cyv.app.game.components.enemy;

import cyv.app.game.components.enemy.common.BasicFireSpirit;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class EnemyGeneratorRegistry {
    private static final Map<String, BiFunction<Float, Float, AbstractEnemyObject>> generators = new HashMap<>();

    public static void registerGenerators() {
        generators.put("enemy_fire_spirit", BasicFireSpirit::new);
    }

    public static BiFunction<Float, Float, AbstractEnemyObject> getGenerator(String id) {
        return generators.get(id);
    }
}
