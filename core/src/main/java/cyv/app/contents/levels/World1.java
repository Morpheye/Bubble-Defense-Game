package cyv.app.contents.levels;

import cyv.app.contents.LevelGroup;
import cyv.app.contents.LevelProvider;

import java.util.Arrays;

public class World1 extends LevelGroup {
    public World1() {
        super(Arrays.asList(
            new LevelProvider("levels/level_1_1.json"),
            new LevelProvider("levels/level_1_2.json")
        ));
    }

    @Override
    public String getName() {
        return "World 1";
    }
}
