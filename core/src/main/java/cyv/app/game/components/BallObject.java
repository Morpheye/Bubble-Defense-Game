package cyv.app.game.components;

import cyv.app.game.Level;
import cyv.app.game.Team;

import java.util.concurrent.ThreadLocalRandom;

public abstract class BallObject {
    // identification
    private final String id;
    private Level levelIn;
    private long timeLived = 0;
    private Team team;
    private final int seed = ThreadLocalRandom.current().nextInt();

    // constants
    private float radius;
    private float density;

    // lerping
    private float lastX = 0;
    private float lastY = 0;
    private float lastRotation = 0;
    private float predictedX = 0;
    private float predictedY = 0;

    // location and physics
    private float x = 0;
    private float y = 0;
    private float rotation = 0;
    private float vx = 0;
    private float vy = 0;

    public BallObject(String id, float x, float y, float radius, float density) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.lastX = x;
        this.lastY = y;
        this.radius = radius;
        this.density = density;
    }

    /**
     * Gets the unique ID of the ball object
     * @return ID
     */
    public final String getId() {
        return id;
    }

    /**
     * Gets the amount of time this object has existed, in ticks.
     * @return Ticks lived
     */
    public final long getTimeLived() {
        return timeLived;
    }

    public final Level getLevelIn() {
        return levelIn;
    }

    public final void setLevelIn(Level l) {
        this.levelIn = l;
    }

    public final Team getTeam() {
        return this.team;
    }

    protected void setTeam(Team team) {
        this.team = team;
    }

    /**
     * Gets the random seed of this object
     * @return Seed
     */
    public final int getSeed() {
        return seed;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public float getMass() {
        // this is simple, just multiply density by radius squared
        // obviously not accurate to actual physics
        float radius = getRadius();
        return radius * radius * getDensity();
    }

    public void setPos(float x, float y) {
        setX(x);
        setY(y);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void addX(float dx) {
        this.x += dx;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void addY(float dy) {
        this.y += dy;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getLastRotation() {
        return lastRotation;
    }

    public void setLastRotation(float lastRotation) {
        this.lastRotation = lastRotation;
    }

    public float getVx() {
        return vx;
    }

    public void setVx(float vx) {
        this.vx = vx;
    }

    public void addVx(float dvx) {
        this.vx += dvx;
    }

    public float getVy() {
        return vy;
    }

    public void setVy(float vy) {
        this.vy = vy;
    }

    public void addVy(float dvy) {
        this.vy += dvy;
    }

    public float getLastX() {
        return lastX;
    }

    public void setLastX(float lastX) {
        this.lastX = lastX;
    }

    public float getLastY() {
        return lastY;
    }

    public void setLastY(float lastY) {
        this.lastY = lastY;
    }

    public float getPredictedX() {
        return predictedX;
    }

    public void setPredictedX(float x) {
        this.predictedX = x;
    }

    public float getPredictedY() {
        return predictedY;
    }

    public void setPredictedY(float y) {
        this.predictedY = y;
    }

    /**
     * Called once when the object is added to the level.
     */
    public void onSpawn(Level l) {

    }

    /**
     * Applies the normal acceleration each tick. This is different depending on
     * whether this is an anchor, normal unit, enemy pathfinding towards a unit, etc.
     */
    public void doAcceleration(Level levelIn) {

    }

    /**
     * Performs the functionality of the object. This includes attacking, producing resources, etc.
     * @param levelIn Level in
     */
    public void doLogic(Level levelIn) {

    }

    /**
     * Finishes ticking the object. (called at the end of every in-game tick)
     */
    public void finishTick() {
        timeLived++;
    }
}
