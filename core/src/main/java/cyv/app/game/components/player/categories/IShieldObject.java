package cyv.app.game.components.player.categories;

import cyv.app.game.components.player.AbstractUnitObject;

public interface IShieldObject {
    /**
     * Get the ratio of damage which will be redirected
     * @return ratio from 0.0 to 1.0
     */
    float getRedirectionPercentage();

    float getRedirectionRadius();

    int redirectDamage(AbstractUnitObject fromObject, int incomingDamage);

}
