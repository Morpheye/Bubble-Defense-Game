package cyv.app.game.blueprints;

import com.badlogic.gdx.graphics.Texture;
import cyv.app.Skydouser;
import cyv.app.game.components.player.AbstractUnitObject;
import cyv.app.render.game.renders.RendererRegistry;
import cyv.app.render.game.renders.UnitRenderer;

import static cyv.app.Skydouser.DEV;

/**
 * Represents an abstract factory that can be added to the player controller to allow
 * the player to deploy units to the board.
 * @param <T>
 */
public abstract class AbstractBlueprint<T extends AbstractUnitObject> {
    private final Skydouser game;
    private final String name;
    private UnitRenderer cachedUnitRenderer;

    public AbstractBlueprint(Skydouser game, String name) {
        this.game = game;
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    /**
     * Gets the cost to deploy the unit
     * @return Cost (in water)
     */
    public abstract int getCost();

    /**
     * Gets the cooldown after placing the unit before another one is ready to be placed
     * @return Cooldown (in ticks)
     */
    public abstract int getCooldown();

    /**
     * Gets whether the blueprint is off cooldown at the start of the level
     * @return Ready on start
     */
    public boolean readyOnStart() {
        return DEV;
    }

    public abstract Texture getTexture();

    /**
     * Creates the object associated with the factory.
     * @return New instance of object
     */
    public abstract T produce(float x, float y);

    protected Skydouser getGame() {
        return game;
    }

    public abstract String getHologramRendererName();

    public UnitRenderer getHologramRenderer() {
        if (cachedUnitRenderer != null) return cachedUnitRenderer;
        cachedUnitRenderer = (UnitRenderer) RendererRegistry.getBallRenderer(getHologramRendererName());
        return cachedUnitRenderer;
    }
}
