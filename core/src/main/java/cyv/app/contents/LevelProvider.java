package cyv.app.contents;

import com.badlogic.gdx.Gdx;
import cyv.app.game.Level;
import cyv.app.game.StandardLevel;

/**
 * Represents an object which can produce a singular level
 */
public class LevelProvider {
    private final String path;

    public LevelProvider(String path) {
        this.path = path;
    }

    /**
     * Path of the standard level provided
     * @return Level path
     */
    public String getLevelPath() {
        return this.path;
    }

    public final Level produce() {
        return StandardLevel.parseLevel(Gdx.files.internal(getLevelPath()).readString("UTF-8"));
    }
}
