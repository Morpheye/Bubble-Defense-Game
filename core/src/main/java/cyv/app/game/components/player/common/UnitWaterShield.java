package cyv.app.game.components.player.common;

import cyv.app.game.Level;
import cyv.app.game.components.BallObject;
import cyv.app.game.components.player.AbstractUnitObject;
import cyv.app.game.components.player.categories.IShieldObject;

public class UnitWaterShield extends AbstractUnitObject implements IShieldObject {
    public UnitWaterShield(float x, float y) {
        super("unit_water_shield", x, y, 1);
    }

    @Override
    public int getMaxHealth() {
        return 500;
    }

    @Override
    public float getRedirectionPercentage() {
        return 1.0f;
    }

    @Override
    public float getRedirectionRadius() {
        return 100;
    }

    @Override
    public int getRegenerationDelay() {
        return 20 * 20; // regeneration starts 2x slower than normal
    }

    @Override
    public int getRegenerationInterval() {
        return 10; // regenerate health 10x slower, fully regenerate in 500 ticks = 25 seconds
    }

    @Override
    public int redirectDamage(AbstractUnitObject fromObject, int incomingDamage) {
        int healthRemaining = getHealth();
        int redirectedDamage = (int) (incomingDamage * getRedirectionPercentage());
        int damageTaken = Math.min(healthRemaining, redirectedDamage);
        this.damage(damageTaken);

        return damageTaken;
    }

    @Override
    public void doLogic(Level levelIn) {
        super.doLogic(levelIn);

        int health = getHealth();
        if (health <= 0) {
            for (BallObject obj : levelIn.getBalls()) {
                if (obj instanceof AbstractUnitObject) {
                    ((AbstractUnitObject) obj).getShields().remove(this);
                }
            }
            return;
        }

        float sx = getX();
        float sy = getY();
        float radiusSq = getRedirectionRadius() * getRedirectionRadius();

        for (BallObject obj : levelIn.getBalls()) {
            if (obj == this) continue;
            if (obj instanceof IShieldObject) continue;
            if (!(obj instanceof AbstractUnitObject)) continue;

            AbstractUnitObject o = (AbstractUnitObject) obj;
            float dx = sx - o.getX();
            float dy = sy - o.getY();
            float distSq = dx * dx + dy * dy;

            if (distSq <= radiusSq) {
                o.getShields().add(this);
            } else {
                o.getShields().remove(this);
            }
        }
    }
}
