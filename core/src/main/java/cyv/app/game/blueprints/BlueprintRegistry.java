package cyv.app.game.blueprints;

import cyv.app.Skydouser;
import cyv.app.game.blueprints.contents.BlueprintDropletTurret;
import cyv.app.game.blueprints.contents.BlueprintRippleTurret;
import cyv.app.game.blueprints.contents.BlueprintWaterPump;

import java.util.*;

public class BlueprintRegistry {
    private static final Map<String, Integer> order = new HashMap<>();
    private static final LinkedHashMap<String, AbstractBlueprint<?>> registry = new LinkedHashMap<>();

    public static void registerBlueprints(Skydouser game) {
        registry.put("blueprint_droplet_turret", new BlueprintDropletTurret(game));
        registry.put("blueprint_water_pump", new BlueprintWaterPump(game));
        registry.put("blueprint_ripple_turret", new BlueprintRippleTurret(game));

        int i = 0;
        for (String key : registry.keySet()) {
            order.put(key, i++);
        }
    }

    public static AbstractBlueprint<?> getBlueprint(String name) {
        return registry.get(name);
    }

    public static int compare(String a, String b) {
        Integer i1 = order.get(a);
        Integer i2 = order.get(b);
        if (i1 == null || i2 == null) {
            throw new IllegalArgumentException("String doesn't exist.");
        }
        return Integer.compare(i1, i2);
    }
}
