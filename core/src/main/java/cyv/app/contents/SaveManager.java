package cyv.app.contents;

import cyv.app.Skydouser;
import cyv.app.game.blueprints.BlueprintRegistry;

import java.util.*;

public class SaveManager {
    private static SaveManager instance = new SaveManager(Set.of("blueprint_droplet_turret"));

    public static SaveManager getInstance() {
        return instance;
    }

    private final Set<String> ownedBlueprints = new TreeSet<>(BlueprintRegistry::compare);
    private final Map<String, Integer> levelCompletions = new HashMap<>();
    private long coins = 0;

    private SaveManager(Set<String> ownedBlueprints) {
        this.ownedBlueprints.addAll(ownedBlueprints);
    }

    public Set<String> getOwnedBlueprints() {
        if (Skydouser.DEV) return BlueprintRegistry.getBlueprints();
        return ownedBlueprints;
    }

    public Map<String, Integer> getLevelCompletions() {
        return levelCompletions;
    }

    public long getCoins() {
        return coins;
    }

    /**
     * Gives the active save instance the provided reward
     */
    public static void handleReward(LevelReward reward) {
        instance.coins += reward.coins;
        instance.ownedBlueprints.addAll(Set.of(reward.blueprints));
    }

    /**
     * Increments completions for the given level
     * @param id Level id
     */
    public static void completeLevel(String id) {
        instance.levelCompletions.merge(id, 1, Integer::sum);
    }
}
