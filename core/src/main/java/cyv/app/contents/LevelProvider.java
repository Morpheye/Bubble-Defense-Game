package cyv.app.contents;

import com.badlogic.gdx.Gdx;
import cyv.app.game.Level;
import cyv.app.game.StandardLevel;

/**
 * Represents an object which can produce a singular level
 */
public class LevelProvider {
    private final String id;
    private final String path;
    private final String name;
    private String parent = null;

    public LevelProvider(String id, String path, String name) {
        this.id = id;
        this.path = path;
        this.name = name;
    }

    /**
     * Path of the standard level provided
     * @return Level path
     */
    public String getLevelPath() {
        return this.path;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public final Level produce() {
        return StandardLevel.parseLevel(Gdx.files.internal(getLevelPath()).readString("UTF-8"));
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
