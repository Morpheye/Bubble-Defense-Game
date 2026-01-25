package cyv.app.render.game;

import cyv.app.BubbleGame;
import cyv.app.game.components.BallObject;
import cyv.app.game.components.particle.Particle;
import cyv.app.game.components.projectile.Projectile;
import cyv.app.render.game.renders.ObjectRenderer;
import cyv.app.render.game.renders.particle.AttackParticleRenderer;
import cyv.app.render.game.renders.projectile.DropletProjectileRenderer;
import cyv.app.render.game.renders.unit.HearthRenderer;

import java.util.HashMap;
import java.util.Map;

public class RendererRegistry {
    private static final Map<String, ObjectRenderer<BallObject>> ballRenderers = new HashMap<>();
    private static final Map<String, ObjectRenderer<Particle>> particleRenders = new HashMap<>();
    private static final Map<String, ObjectRenderer<Projectile>> projectileRenders = new HashMap<>();

    public static void registerRenders(BubbleGame game) {
        ballRenderers.clear();
        particleRenders.clear();
        projectileRenders.clear();

        registerUnits(game);
        registerEnemies(game);
        registerParticles(game);
        registerProjectiles(game);
    }

    private static void registerUnits(BubbleGame game) {
        ballRenderers.put("hearth", new HearthRenderer(game));
    }

    private static void registerEnemies(BubbleGame game) {

    }

    private static void registerParticles(BubbleGame game) {
        particleRenders.put("particle_attack", new AttackParticleRenderer(game));
    }

    private static void registerProjectiles(BubbleGame game) {
        projectileRenders.put("projectile_droplet", new DropletProjectileRenderer(game));
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
