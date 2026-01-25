package cyv.app.game.components.particle;

import cyv.app.game.Team;

public class Particle {
    private final String id;
    private Team team;
    private long timeLived = 0;

    private float x = 0;
    private float y = 0;
    private float radius = 0;
    private float rotation = 0;
    private float vx = 0;
    private float vy = 0;
    private boolean mirrored;

    private float lastX = 0;
    private float lastY = 0;
    private float lastRotation = 0;

    private final int lifetime;
    private final int fadeTime;

    public Particle(String id, float x, float y, float radius, float r, float vx, float vy,
                    int lifetime, int fadeTime, boolean mirrored) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.rotation = r;
        this.vx = vx;
        this.vy = vy;
        this.lifetime = lifetime;
        this.fadeTime = fadeTime;
        this.mirrored = mirrored;
    }

    public void tick() {
        setLastX(getX());
        setLastY(getY());
        setLastRotation(getRotation());

        // move
        setX(getX() + getVx());
        setY(getY() + getVy());

        this.timeLived++;
    }

    public String getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getVx() {
        return vx;
    }

    public void setVx(float vx) {
        this.vx = vx;
    }

    public float getVy() {
        return vy;
    }

    public void setVy(float vy) {
        this.vy = vy;
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

    public float getLastRotation() {
        return lastRotation;
    }

    public void setLastRotation(float lastRotation) {
        this.lastRotation = lastRotation;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public long getTimeLived() {
        return timeLived;
    }

    public int getLifetime() {
        return lifetime;
    }

    public int getFadeTime() {
        return fadeTime;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public boolean isMirrored() {
        return mirrored;
    }

    public void setMirrored(boolean mirrored) {
        this.mirrored = mirrored;
    }
}
