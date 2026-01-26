package cyv.app.game.blueprints;

import cyv.app.game.blueprints.contents.BlueprintDropletTurret;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlueprintRegistry {
    private static final Map<String, AbstractBlueprint<?>> registry = new HashMap<>();
    private static final Set<String> ownedBlueprints = new HashSet<>();
    // TODO: implement ownedBlueprints

    public static void registerBlueprints() {
        registry.put("unit_droplet_turret", new BlueprintDropletTurret());
    }

    public static AbstractBlueprint<?> getBlueprint(String name) {
        return registry.get(name);
    }
}
