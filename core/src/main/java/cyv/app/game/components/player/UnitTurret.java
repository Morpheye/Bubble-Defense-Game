package cyv.app.game.components.player;

import cyv.app.game.Level;
import cyv.app.game.components.BallObject;
import cyv.app.game.components.projectile.Projectile;
import cyv.app.game.components.projectile.ProjectileDroplet;
import cyv.app.util.MathUtils;

import static cyv.app.game.Level.INSIGNIFICANT_F;

public class UnitTurret extends AbstractUnitObject {
    private long timeLastAttacked;

    public UnitTurret(float x, float y) {
        super("turret", x, y, 40, 1);
    }

    @Override
    public int getMaxHealth() {
        return 50;
    }

    /**
     * Gets the cooldown in ticks between attacks
     * @return Attack cooldown
     */
    public int getAttackCooldown() {
        // defaults to once per second
        return 20;
    }

    public final long getTimeLastAttacked() {
        return timeLastAttacked;
    }

    public float getRotationRange() {
        // by default, can see in an arc of 30 degrees
        return 30f;
    }

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
        // fire projectile in direction facing
        float rotation = getRotation();
        final float PROJECTILE_SPEED = 10;
        float rad = (float) Math.toRadians(rotation);

        // slight offset forward
        float oX = (float) Math.cos(rad) * getRadius();
        float oY = (float) Math.sin(rad) * getRadius();

        // projectile speeds
        float sX = (float) Math.cos(rad) * PROJECTILE_SPEED;
        float sY = (float) Math.sin(rad) * PROJECTILE_SPEED;

        Projectile proj = new ProjectileDroplet(getX() + oX, getY() + oY, rotation, getTeam());
        levelIn.spawnProjectile(proj);

        // update time last attacked
        timeLastAttacked = getTimeLived();
    }

}
