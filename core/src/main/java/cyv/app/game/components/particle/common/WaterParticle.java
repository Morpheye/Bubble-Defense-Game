package cyv.app.game.components.particle.common;

import cyv.app.game.components.particle.Particle;

public class WaterParticle extends Particle {
    public WaterParticle(float x, float y, float radius) {
        super("particle_water", x, y, radius, 0, 0, 0, 10, 10, false);
    }

    @Override
    public void tick() {
        // accelerate
        setVy(getVy() + 0.5f);
        super.tick();
    }
}
