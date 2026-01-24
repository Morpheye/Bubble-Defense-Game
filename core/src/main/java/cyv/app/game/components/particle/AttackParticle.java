package cyv.app.game.components.particle;

public class AttackParticle extends Particle {
    public AttackParticle(float x, float y, float radius, float r) {
        super("particle_attack", x, y, radius, r, 0, 0, 6, 5, Math.random() > 0.5);
    }
}
