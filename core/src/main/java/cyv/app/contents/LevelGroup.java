package cyv.app.contents;

import java.util.List;

public abstract class LevelGroup {
    private final List<LevelProvider> levels;
    public LevelGroup(List<LevelProvider> levels) {
        this.levels = levels;
    }

    public abstract String getName();

    public List<LevelProvider> getLevels() {
        return levels;
    }
}
