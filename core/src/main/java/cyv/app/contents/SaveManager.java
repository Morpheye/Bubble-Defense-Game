package cyv.app.contents;

import cyv.app.game.blueprints.BlueprintRegistry;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class SaveManager {
    private static SaveManager instance = new SaveManager(Set.of("blueprint_droplet_turret",
        "blueprint_water_pump", "blueprint_ripple_turret"));

    public static SaveManager getInstance() {
        return instance;
    }

    private final Set<String> ownedBlueprints = new TreeSet<>(BlueprintRegistry::compare);

    private SaveManager(Set<String> ownedBlueprints) {
        this.ownedBlueprints.addAll(ownedBlueprints);
    }

    public Set<String> getOwnedBlueprints() {
        return ownedBlueprints;
    }
}
