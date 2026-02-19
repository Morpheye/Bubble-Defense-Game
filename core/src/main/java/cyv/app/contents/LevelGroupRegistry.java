package cyv.app.contents;

import cyv.app.contents.levels.World1;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LevelGroupRegistry {
    private static final Map<String, LevelGroup> worlds = new HashMap<>();

    static {
        // register worlds here
        Set<LevelGroup> groups = Set.of(new World1());

        for (LevelGroup group : groups) {
            worlds.put(group.getName(), group);
        }
    }

    public static LevelGroup getWorld(String name) {
        return worlds.get(name);
    }

    public static LevelGroup getDefaultWorld() {
        return getWorld("World 1");
    }
}
