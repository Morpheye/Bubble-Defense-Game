package cyv.app.game;

import com.badlogic.gdx.math.MathUtils;
import cyv.app.game.components.BallObject;
import cyv.app.game.components.ILivingObject;
import cyv.app.game.components.particle.Particle;
import cyv.app.game.components.player.HearthObject;
import cyv.app.game.components.projectile.Projectile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Level {
    // important constants
    public static final float TILE_SIZE = 100f;
    public static final float INSIGNIFICANT_F = 1e-6f;

    // level bounds
    private final int sizeX;
    private final int sizeY;

    // objects and tile grid
    private final int[][] grid;
    private final Set<BallObject> balls = new HashSet<>();
    private final HearthObject hearth;
    private final Set<Projectile> projectiles = new HashSet<>();
    private final Set<Particle> particles = new HashSet<>();

    // front-end info
    private float camera_center_x;
    private float camera_center_y;
    private float camera_scale; // corresponds to the width of the camera

    public Level(int sizeX, int sizeY, HearthObject hearth) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        // temporary camera constants
        this.camera_center_x = TILE_SIZE * sizeX / 2;
        this.camera_center_y = TILE_SIZE * sizeY / 2;
        this.camera_scale = 1600;

        // objects
        this.hearth = hearth;
        this.balls.add(hearth);
        this.grid = new int[sizeX][sizeY];

    }

    /**
     * Starts the level
     */
    public void start() {
        // TODO: start the waves and events
    }

    public HearthObject getHearth() {
        return this.hearth;
    }

    /**
     * Ticks the physics and combat of the level
     */
    public void tick() {
        // constants
        final float DRAG = 0.9f;
        final float COLLISION_PUSH_FORCE = 0.1f;
        final int COLLISION_RESOLUTION_ATTEMPTS = 5;

        // Max distance a ball is allowed to move per sub-step
        final float MAX_STEP_DISTANCE = TILE_SIZE * 0.25f;

        // ------------------------------------------------------------------
        // Step 1: apply forces + drag (ONCE per tick)
        // ------------------------------------------------------------------
        for (BallObject b : balls) {
            // First: remember last position
            b.setLastX(b.getX());
            b.setLastY(b.getY());
            b.setLastRotation(b.getRotation());

            // Second: each ballObject has its own movement
            b.doAcceleration(this);

            // Finally, apply drag.
            b.setVx(b.getVx() * DRAG);
            b.setVy(b.getVy() * DRAG);
        }

        // ------------------------------------------------------------------
        // Step 2: compute sub-step count based on max velocity
        // ------------------------------------------------------------------
        float maxSpeed = 0f;
        for (BallObject b : balls) {
            float speed = (float) Math.hypot(b.getVx(), b.getVy());
            maxSpeed = Math.max(maxSpeed, speed);
        }

        int subSteps = Math.max(1, (int) Math.ceil(maxSpeed / MAX_STEP_DISTANCE));

        // ------------------------------------------------------------------
        // Step 3: perform sub-steps
        // ------------------------------------------------------------------
        for (int step = 0; step < subSteps; step++) {
            // 3a: predict partial movement
            for (BallObject b : balls) {
                b.setPredictedX(b.getX() + b.getVx() / subSteps);
                b.setPredictedY(b.getY() + b.getVy() / subSteps);
            }

            List<BallObject> list = new ArrayList<>(balls);

            // 3b: collision resolution loop
            for (int attempt = 0; attempt < COLLISION_RESOLUTION_ATTEMPTS; attempt++) {

                // ---------- Ball ↔ Ball ----------
                for (int i = 0; i < list.size(); i++) {
                    for (int j = i + 1; j < list.size(); j++) {
                        BallObject b1 = list.get(i);
                        BallObject b2 = list.get(j);

                        float dx = b2.getPredictedX() - b1.getPredictedX();
                        float dy = b2.getPredictedY() - b1.getPredictedY();
                        float dist = (float) Math.hypot(dx, dy);
                        float overlap = (b1.getRadius() + b2.getRadius()) - dist;

                        if (overlap > 0f) {
                            if (dist < INSIGNIFICANT_F) {
                                dx = 1f;
                                dy = 0f;
                                dist = 1f;
                            }

                            float nx = dx / dist;
                            float ny = dy / dist;

                            float totalMass = b1.getMass() + b2.getMass();

                            float push = overlap * COLLISION_PUSH_FORCE;
                            if (b1.getTeam() == Team.ENEMY && b2.getTeam() == Team.ENEMY) push *= 0.075f;

                            b1.setPredictedX(b1.getPredictedX() - nx * push * b2.getMass() / totalMass);
                            b1.setPredictedY(b1.getPredictedY() - ny * push * b2.getMass() / totalMass);
                            b2.setPredictedX(b2.getPredictedX() + nx * push * b1.getMass() / totalMass);
                            b2.setPredictedY(b2.getPredictedY() + ny * push * b1.getMass() / totalMass);
                        }
                    }
                }

                // ---------- Ball <-> Tile (Option B) ----------
                for (BallObject b : balls) {
                    float r = b.getRadius();
                    float cx = b.getPredictedX();
                    float cy = b.getPredictedY();

                    float maxPenetration = 0f;
                    float pushX = 0f;
                    float pushY = 0f;

                    int minTileX = (int) Math.floor((cx - r) / TILE_SIZE);
                    int maxTileX = (int) Math.floor((cx + r) / TILE_SIZE);
                    int minTileY = (int) Math.floor((cy - r) / TILE_SIZE);
                    int maxTileY = (int) Math.floor((cy + r) / TILE_SIZE);

                    for (int tx = minTileX; tx <= maxTileX; tx++) {
                        for (int ty = minTileY; ty <= maxTileY; ty++) {
                            if (tx < 0 || ty < 0 || tx >= sizeX || ty >= sizeY) continue;
                            if (grid[tx][ty] != 1) continue;

                            float tileMinX = tx * TILE_SIZE;
                            float tileMaxX = tileMinX + TILE_SIZE;
                            float tileMinY = ty * TILE_SIZE;
                            float tileMaxY = tileMinY + TILE_SIZE;

                            float closestX = MathUtils.clamp(cx, tileMinX, tileMaxX);
                            float closestY = MathUtils.clamp(cy, tileMinY, tileMaxY);

                            float dx = cx - closestX;
                            float dy = cy - closestY;
                            float distSq = dx * dx + dy * dy;

                            if (distSq < r * r) {
                                float dist = (float) Math.sqrt(distSq);
                                if (dist < INSIGNIFICANT_F) continue;

                                float penetration = r - dist;
                                if (penetration > maxPenetration) {
                                    maxPenetration = penetration;
                                    pushX = dx / dist * penetration;
                                    pushY = dy / dist * penetration;
                                }
                            }
                        }
                    }

                    b.setPredictedX(cx + pushX);
                    b.setPredictedY(cy + pushY);
                }
            }

            // 3c: commit sub-step movement
            for (BallObject b : balls) {
                b.setPos(b.getPredictedX(), b.getPredictedY());
            }
        }

        // ------------------------------------------------------------------
        // Step 4: recompute velocities from actual movement
        // ------------------------------------------------------------------
        for (BallObject b : balls) {
            b.setVx(b.getX() - b.getLastX());
            b.setVy(b.getY() - b.getLastY());
        }

        // ------------------------------------------------------------------
        // Step 5: tick individual objects, projectiles, and particles
        // ------------------------------------------------------------------
        for (BallObject b : balls) {
            b.doLogic(this);
        }

        projectiles.forEach(p -> p.tick(this));
        projectiles.removeIf(p -> !p.isValid() || p.getTimeLived() > p.getLifetime());

        particles.forEach(Particle::tick);
        particles.removeIf(p -> p.getTimeLived() > p.getLifetime());

        // ------------------------------------------------------------------
        // Step 6: clean up (remove dead units and enemies)
        // ------------------------------------------------------------------
        for (BallObject b : balls) {
            b.finishTick();
        }

        balls.removeIf(b -> {
            if (!(b instanceof ILivingObject)) return false;
            ILivingObject l = (ILivingObject) b;
            return l.isDead();
        });
    }


    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public int[][] getGrid() {
        return grid;
    }

    public Set<BallObject> getBalls() {
        return balls;
    }

    /**
     * Adds a new ball to the level
     * @param obj Ball object
     */
    public void spawnBall(BallObject obj) {
        balls.add(obj);
        obj.onSpawn(this);
    }

    public Set<Projectile> getProjectiles() {
        return projectiles;
    }

    /**
     * Adds a new projectile to the level
     * @param proj Projectile
     */
    public void spawnProjectile(Projectile proj) {
        projectiles.add(proj);
    }

    public Set<Particle> getParticles() {
        return particles;
    }

    /**
     * Adds a new particle to the level
     * @param p Particle
     */
    public void spawnParticle(Particle p) {
        particles.add(p);
    }

    public float getCameraCenterX() {
        return camera_center_x;
    }

    public void setCameraCenterX(float x) {
        this.camera_center_x = x;
    }

    public float getCameraCenterY() {
        return camera_center_y;
    }

    public void setCameraCenterY(float y) {
        this.camera_center_y = y;
    }

    public float getCameraScale() {
        return camera_scale;
    }

    public void setCameraScale(float scale) {
        this.camera_scale = scale;
    }
}
