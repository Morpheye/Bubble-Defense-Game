package cyv.app.game;

import cyv.app.game.blueprints.AbstractBlueprint;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class containing information about what the player has brought into a level.
 */
public class PlayerController {
    private int ticks = 0;
    private int water = 0;
    private final List<AbstractBlueprint<?>> blueprints;
    private int selectedIndex = -1;
    private final int[] timesLastUsed;

    public PlayerController(List<AbstractBlueprint<?>> blueprints) {
        this.blueprints = blueprints;
        this.timesLastUsed = new int[blueprints.size()];
        for (int i = 0; i < blueprints.size(); i++) {
            timesLastUsed[i] = blueprints.get(i).readyOnStart() ? -10000 : 0;
        }
    }

    /**
     * Ticks the playerController (necessary for cooldowns)
     */
    public void tick() {
        ticks++;
    }

    public int getWater() {
        return water;
    }

    public void setWater(int amount) {
        this.water = amount;
    }

    public void addWater(int amount) {
        this.water += amount;
    }

    public int getTicks() {
        return ticks;
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
     * Gets the time the blueprint was last used
     * @param index Index of blueprint
     * @return Time last used in ticks
     */
    public int getTimeLastUsed(int index) {
        return timesLastUsed[index];
    }

    /**
     * Uses the blueprint which is currently selected, putting it on cooldown.
     */
    public void use() {
        if (selectedIndex != -1) {
            timesLastUsed[selectedIndex] = getTicks();
            addWater(-blueprints.get(selectedIndex).getCost());
        }
        setSelectedIndex(-1);
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
