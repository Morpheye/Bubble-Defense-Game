package cyv.app.render.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import cyv.app.BubbleGame;
import cyv.app.game.Level;
import cyv.app.game.components.BallObject;
import cyv.app.game.components.ILivingObject;
import cyv.app.game.components.enemy.AbstractEnemyObject;
import cyv.app.game.components.enemy.BasicFireSpirit;
import cyv.app.game.components.particle.Particle;
import cyv.app.game.components.player.AbstractUnitObject;
import cyv.app.game.components.player.UnitTurret;
import cyv.app.game.components.projectile.Projectile;
import cyv.app.render.game.renders.ObjectRenderer;

public class GameScreen implements Screen {
    public static final int TICK_LENGTH = 1000 / 20;

    private final BubbleGame game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;

    // game components
    // TODO: add PlayerController
    private final Level level;
    private long lastTickTime = 0;

    public GameScreen(BubbleGame game, Level level) {
        this.game = game;
        this.level = level;

        // set up camera
        float camWidth = level.getCameraScale();
        float aspect = 16.0f / 9;
        float camHeight = camWidth / aspect;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, camWidth, camHeight);
        camera.position.set(level.getCameraCenterX() * Level.TILE_SIZE,
            level.getCameraCenterY() * Level.TILE_SIZE, 0);

        viewport = new FitViewport(camWidth, camHeight, camera);
        viewport.apply();

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        long now = System.currentTimeMillis();
        if (now - lastTickTime >= TICK_LENGTH) {
            lastTickTime = now;
            level.tick();
        }

        // detect mouse click
        // TODO: create a more robust input handling system
        if (Gdx.input.justTouched()) {
            Vector3 screenPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(screenPos); // screen -> world

            if (Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.LEFT))
                level.spawnBall(new UnitTurret(screenPos.x, screenPos.y));
            else if (Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.RIGHT))
                level.spawnBall(new BasicFireSpirit(screenPos.x, screenPos.y));
        }

        // clear screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // render level components
        renderGame(now);

    }

    /**
     * Renders the components of the game (not the UI)
     * @param now current time in milliseconds
     */
    private void renderGame(long now) {
        // set camera to center on level
        camera.position.set(level.getCameraCenterX(), level.getCameraCenterY(), 0);
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // draw background
        float centerX = level.getCameraCenterX();
        float centerY = level.getCameraCenterY();
        float skySize = level.getCameraScale();

        shapeRenderer.setColor(0.8f, 1f, 1f, 1f);
        shapeRenderer.rect(centerX - skySize / 2f, centerY - skySize / 2f, skySize, skySize);
        shapeRenderer.end();

        // prepare batcher
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // draw tiles
        TileRenderer.renderTiles(game, batch, level.getGrid());

        // draw balls
        renderBalls(now);

        // TODO: draw projectiles
        renderProjectiles(now);

        // draw particles
        renderParticles(now);

        batch.end();
    }

    private void renderBalls(long now) {
        float delta = (now - lastTickTime) / (float) TICK_LENGTH;

        Texture pixel = game.getAssets().getTexture("pixel");
        Texture pbTex = game.getAssets().getTexture("player_bubble_back");
        Texture ebTex = game.getAssets().getTexture("enemy_bubble_back");

        for (BallObject b : level.getBalls()) {
            float renderX = b.getLastX() * (1 - delta) + b.getX() * delta;
            float renderY = b.getLastY() * (1 - delta) + b.getY() * delta;
            float radius = b.getRadius();
            float size = radius * 2f;

            // draw bubble back
            if (b instanceof AbstractUnitObject)
                batch.draw(pbTex, renderX - radius, renderY - radius, size, size);
            else if (b instanceof AbstractEnemyObject)
                batch.draw(ebTex, renderX - radius, renderY - radius, size, size);

            // render inside entity
            ObjectRenderer<BallObject> renderer = RendererRegistry.getBallRenderer(b.getId());
            if (renderer != null) try {
                renderer.render(batch, b, delta);
            } catch (IllegalArgumentException e) {
                Gdx.app.error("Renderer", "Invalid ball type", e);
            }

            // render healthbar
            if (b instanceof ILivingObject) {
                ILivingObject l = (ILivingObject) b;
                int hp = l.getHealth();
                int maxHp = l.getMaxHealth();
                if (hp < maxHp) {
                    float barWidth = radius * 2f;
                    float barHeight = 4f; // arbitrary height
                    float barX = renderX - radius;
                    float barY = renderY + radius + 4f; // slightly above the ball
                    float healthRatio = Math.max(0f, Math.min(1f, (float) hp / maxHp));
                    Color healthColor = new Color();
                    healthColor.set(1f - healthRatio, healthRatio, 0f, 0.5f);
                    batch.setColor(healthColor);
                    batch.draw(pixel, barX, barY, barWidth * healthRatio, barHeight);
                    batch.setColor(Color.WHITE);
                }
            }
        }

        batch.setColor(1, 1, 1, 1);
    }

    private void renderProjectiles(long now) {
        float delta = (now - lastTickTime) / (float) TICK_LENGTH;

        for (Projectile p : level.getProjectiles()) {
            ObjectRenderer<Projectile> renderer = RendererRegistry.getProjectileRenderer(p.getId());
            if (renderer != null) try {
                renderer.render(batch, p, delta);
            } catch (IllegalArgumentException e) {
                Gdx.app.error("Renderer", "Invalid projectile type", e);
            }
        }

        batch.setColor(1, 1, 1, 1);
    }

    private void renderParticles(long now) {
        float delta = (now - lastTickTime) / (float) TICK_LENGTH;

        for (Particle p : level.getParticles()) {
            ObjectRenderer<Particle> renderer = RendererRegistry.getParticleRenderer(p.getId());
            if (renderer != null) try {
                renderer.render(batch, p, delta);
            } catch (IllegalArgumentException e) {
                Gdx.app.error("Renderer", "Invalid particle type", e);
            }
        }

        batch.setColor(1, 1, 1, 1);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
    }

    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
    }
}
