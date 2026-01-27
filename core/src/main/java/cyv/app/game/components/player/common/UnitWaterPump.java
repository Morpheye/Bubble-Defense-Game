package cyv.app.game.components.player.common;

import cyv.app.game.Level;
import cyv.app.game.components.particle.common.WaterParticle;
import cyv.app.game.components.player.AbstractUnitObject;

public class UnitWaterPump extends AbstractUnitObject {
    private int nextProductionTime = 20 * 5;

    public UnitWaterPump(float x, float y) {
        super("unit_water_pump", x, y, 1);
    }

    @Override
    public int getMaxHealth() {
        return 50;
    }

    @Override
    public void doLogic(Level levelIn) {
        super.doLogic(levelIn);
        if (getTimeLived() >= nextProductionTime) {
            // produce water
            // 2 water at a time, initial delay is 5 seconds, then produce water
            // every 10 to 15 seconds.
            levelIn.getController().addWater(2);
            levelIn.spawnParticle(new WaterParticle(getX(), getY(), 20));
            nextProductionTime += 20 * 10 + (int) (Math.random() * 20 * 6);
        }
    }
}
