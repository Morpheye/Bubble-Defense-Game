package cyv.app.game.components.player.common;

import cyv.app.game.Level;
import cyv.app.game.components.player.categories.IWaterProducer;
import cyv.app.game.components.particle.common.WaterParticle;
import cyv.app.game.components.player.AbstractUnitObject;

public class UnitWaterPump extends AbstractUnitObject implements IWaterProducer {
    private long nextProductionTime = getStartingProductionTime();

    public UnitWaterPump(float x, float y) {
        super("unit_water_pump", x, y, 1);
    }

    @Override
    public int getMaxHealth() {
        return 50;
    }

    // specific to water producers

    @Override
    public long getNextProductionTime() {
        return nextProductionTime;
    }

    @Override
    public int getMinProductionTime() {
        return 20 * 10;
    }

    @Override
    public int getMaxProductionTime() {
        return 20 * 15;
    }

    @Override
    public int getStartingProductionTime() {
        return 20 * 5;
    }

    @Override
    public void doLogic(Level levelIn) {
        super.doLogic(levelIn);
        if (getTimeLived() >= nextProductionTime) {
            // produce water
            levelIn.getController().addWater(2);
            levelIn.spawnParticle(new WaterParticle(getX(), getY(), 20));
            int min = getMinProductionTime();
            int max = getMaxProductionTime();
            nextProductionTime = getTimeLived() + min + (int) (Math.random() * (max - min + 1));
        }
    }
}
