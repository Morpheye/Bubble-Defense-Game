package cyv.app.render.game.renders;

import cyv.app.Skydouser;
import cyv.app.game.components.BallObject;
import cyv.app.game.components.particle.Particle;
import cyv.app.game.components.projectile.Projectile;
import cyv.app.render.game.renders.particle.AttackParticleRenderer;
import cyv.app.render.game.renders.projectile.DropletProjectileRenderer;
import cyv.app.render.game.renders.particle.WaterParticleRenderer;
import cyv.app.render.game.renders.projectile.RippleProjectileRenderer;
import cyv.app.render.game.renders.unit.*;

import java.util.HashMap;
import java.util.Map;

public class RendererRegistry {
    private static final Map<String, ObjectRenderer<BallObject>> ballRenderers = new HashMap<>();
    private static final Map<String, ObjectRenderer<Particle>> particleRenders = new HashMap<>();
    private static final Map<String, ObjectRenderer<Projectile>> projectileRenders = new HashMap<>();

    public static void registerRenders(Skydouser game) {
        ballRenderers.clear();
        particleRenders.clear();
        projectileRenders.clear();

        registerUnits(game);
        registerEnemies(game);
        registerParticles(game);
        registerProjectiles(game);
    }

    private static void registerUnits(Skydouser game) {
        ballRenderers.put("unit_hearth", new HearthRenderer(game));

        ballRenderers.put("unit_droplet_turret", new DropletTurretRenderer(game));
        ballRenderers.put("unit_water_pump", new WaterPumpRenderer(game));
        ballRenderers.put("unit_ripple_turret", new RippleTurretRenderer(game));
        ballRenderers.put("unit_water_shield", new WaterShieldRenderer(game));
    }

    private static void registerEnemies(Skydouser game) {

    }

    private static void registerParticles(Skydouser game) {
        particleRenders.put("particle_attack", new AttackParticleRenderer(game));
        particleRenders.put("particle_water", new WaterParticleRenderer(game));
    }

    private static void registerProjectiles(Skydouser game) {
        projectileRenders.put("projectile_droplet", new DropletProjectileRenderer(game));
        projectileRenders.put("projectile_ripple", new RippleProjectileRenderer(game));
    }

    public static ObjectRenderer<BallObject> getBallRenderer(String id) {
        return ballRenderers.get(id);
    }

    public static ObjectRenderer<Particle> getParticleRenderer(String id) {
        return particleRenders.get(id);
    }

    public static ObjectRenderer<Projectile> getProjectileRenderer(String id) {
        return projectileRenders.get(id);
    }

}
