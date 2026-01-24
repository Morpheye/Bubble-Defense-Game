package cyv.app.game;

import cyv.app.game.blueprints.AbstractBlueprint;

import java.util.List;

/**
 * Class containing information about what the player has brought into a level.
 */
public class PlayerController {
    private final List<AbstractBlueprint<?>> blueprints;
    private int selectedIndex = -1;

    public PlayerController(List<AbstractBlueprint<?>> blueprints) {
        // TODO: add additional parameters for what tools and perks the player has unlocked
        this.blueprints = blueprints;
    }

    /**
     * Gets the blueprints that the player has selected
     * @return Blueprints
     */
    public List<AbstractBlueprint<?>> getBlueprints() {
        return this.blueprints;
    }

    /**
     * Gets the selected blueprint's index. -1 means nothing is selected.
     * @return Index
     */
    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    /**
     * Sets the selected blueprint's index. -1 means nothing is selected.
     * @param index New index
     */
    public void setSelectedIndex(int index) {
        if (index < 0 || index >= blueprints.size()) index = -1;
        this.selectedIndex = index;
    }
}
