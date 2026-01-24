package cyv.app.game.blueprints;

import cyv.app.game.components.player.AbstractUnitObject;

/**
 * Represents an abstract factory that can be added to the player controller to allow
 * the player to deploy units to the board.
 * @param <T>
 */
public abstract class AbstractBlueprint<T extends AbstractUnitObject> {
    public AbstractBlueprint() {

    }

    /**
     * Gets the cost to deploy the unit
     * @return Cost (in water)
     */
    public abstract int getCost();

    /**
     * Creates the object associated with the factory.
     * @return New instance of object
     */
    public abstract T produce();
}
