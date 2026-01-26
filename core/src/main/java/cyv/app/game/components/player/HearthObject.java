package cyv.app.game.components.player;

import cyv.app.game.Level;
import cyv.app.game.components.GravityType;
import cyv.app.game.components.IAnchorObject;

import static cyv.app.game.Level.INSIGNIFICANT_F;

public class HearthObject extends AbstractUnitObject implements IAnchorObject {
    private final float spawnX;
    private final float spawnY;

    public HearthObject(float x, float y) {
        super("unit_hearth", x, y, 40, 10);
        this.spawnX = x;
        this.spawnY = y;
    }

    @Override
    public void doAcceleration(Level levelIn) {
        final float ANCHOR_FORCE = 0.5f;
        float dx = getSpawnX() - getX();
        float dy = getSpawnY() - getY();
        float dist = (float) Math.hypot(dx, dy);
        if (dist > INSIGNIFICANT_F) {
            addVx(dx / dist * Math.min(dist, ANCHOR_FORCE));
            addVy(dy / dist * Math.min(dist, ANCHOR_FORCE));
        }
    }

    @Override
    public int getMaxHealth() {
        return 100;
    }

    @Override
    public float getSpawnX() {
        return spawnX;
    }

    @Override
    public float getSpawnY() {
        return spawnY;
    }

}
