package cyv.app.game.components.particle.common;

import cyv.app.game.components.particle.Particle;

public class WaterParticle extends Particle {
    public WaterParticle(float x, float y) {
        super("particle_water", x, y, 25, 0, 0, 0, 10, 10, false);
    }

    @Override
    public void tick() {
        // accelerate
        setVy(getVy() + 0.5f);
        super.tick();
    }
}
