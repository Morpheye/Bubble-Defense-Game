package cyv.app.contents;

import java.util.List;

public abstract class LevelGroup {
    private final List<LevelProvider> levels;
    public LevelGroup(List<LevelProvider> levels) {
        this.levels = levels;
        for (LevelProvider l : levels) l.setParent(getName());
    }

    public abstract String getName();

    public List<LevelProvider> getLevels() {
        return levels;
    }
}
