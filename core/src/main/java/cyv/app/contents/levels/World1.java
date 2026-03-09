package cyv.app.contents.levels;

import cyv.app.contents.LevelGroup;
import cyv.app.contents.LevelProvider;

import java.util.Arrays;

public class World1 extends LevelGroup {
    public World1() {
        super(Arrays.asList(
            new LevelProvider("world1/level1", "levels/level_1_1.json", "Level 1"),
            new LevelProvider("world1/level2", "levels/level_1_2.json", "Level 2"),
            new LevelProvider("world1/level3", "levels/level_1_3.json", "Level 3")
        ));
    }

    @Override
    public String getName() {
        return "World 1";
    }
}
