package cyv.app.game.components.player;

import cyv.app.game.Level;
import cyv.app.game.components.BallObject;
import cyv.app.game.components.IAnchorObject;
import cyv.app.game.components.projectile.Projectile;
import cyv.app.util.MathUtils;

import static cyv.app.game.Level.INSIGNIFICANT_F;

public abstract class AbstractTurret extends AbstractUnitObject {
    private long timeLastAttacked = -10000;

    public AbstractTurret(String id, float x, float y) {
        super(id, x, y, 1);
    }

    @Override
    public void onSpawn(Level l) {
        super.onSpawn(l);

        // find anchor immediately and rotate accordingly
        float closestDistSq = Float.POSITIVE_INFINITY;
        BallObject closest = null;
        float lastDx = 1;
        float lastDy = 0;

        for (BallObject obj : l.getBalls()) {
            if (obj == this) continue;
            if (!(obj instanceof IAnchorObject)) continue;

            float dx = obj.getX() - getX();
            float dy = obj.getY() - getY();
            float distSq = dx * dx + dy * dy;

            if (distSq < closestDistSq && distSq > INSIGNIFICANT_F * INSIGNIFICANT_F) {
                closestDistSq = distSq;
                closest = obj;
                lastDx = dx;
                lastDy = dy;
            }
        }

        setRotation(MathUtils.normalizeAngle((float) Math.toDegrees(Math.atan2(-lastDy, -lastDx))));
        setLastRotation(getRotation());
        setLastAnchor(closest);

    }

    /**
     * Gets the cooldown in ticks between attacks
     * @return Attack cooldown
     */
    public abstract int getAttackCooldown();

    public final long getTimeLastAttacked() {
        return timeLastAttacked;
    }

    public abstract float getRotationRange();

    @Override
    public void doLogic(Level levelIn) {
        // check the angle of this turret relative to its anchor
        BallObject anchor = getLastAnchor();
        float baseAngle = 0;
        float rotationRange = getRotationRange();

        if (anchor != null) {
            float dx = getX() - anchor.getX();
            float dy = getY() - anchor.getY();
            baseAngle = (float) Math.toDegrees(Math.atan2(dy, dx));
        } else {
            rotationRange = 180f; // can freely rotate because no anchor
        }

        BallObject target = null;
        float closestDistSq = Float.POSITIVE_INFINITY;
        float targetAngle = 0f;

        for (BallObject b : levelIn.getBalls()) {
            if (b.getTeam() == getTeam()) continue;

            float dx = b.getX() - getX();
            float dy = b.getY() - getY();

            float distSq = dx * dx + dy * dy;
            if (distSq < INSIGNIFICANT_F * INSIGNIFICANT_F) continue;

            float angle = (float) Math.toDegrees(Math.atan2(dy, dx));

            // smallest signed angular difference
            float delta = MathUtils.normalizeAngle(angle - baseAngle);

            // outside rotational arc
            if (Math.abs(delta) > rotationRange * 2) continue;

            if (distSq < closestDistSq) {
                closestDistSq = distSq;
                target = b;
                targetAngle = angle;
            }
        }

        if (target != null) {
            setRotation(targetAngle);

            if (getTimeLived() - getTimeLastAttacked() > getAttackCooldown()) {
                attack(levelIn);
            }
        }

    }

    private void attack(Level levelIn) {
        levelIn.spawnProjectile(getProjectile());
        // update time last attacked
        timeLastAttacked = getTimeLived();
    }

    public abstract Projectile getProjectile();

}
