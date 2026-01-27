package cyv.app.render.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import cyv.app.BubbleGame;
import cyv.app.game.Level;
import cyv.app.game.PlayerController;
import cyv.app.game.blueprints.AbstractBlueprint;
import cyv.app.game.components.BallObject;
import cyv.app.game.components.ILivingObject;
import cyv.app.game.components.enemy.AbstractEnemyObject;
import cyv.app.game.components.particle.Particle;
import cyv.app.game.components.player.AbstractUnitObject;
import cyv.app.game.components.projectile.Projectile;
import cyv.app.render.game.renders.ObjectRenderer;
import cyv.app.render.game.renders.UnitRenderer;

import java.util.List;

public class GameScreen implements Screen {
    public static final int TICK_LENGTH = 1000 / 20;

    private final BubbleGame game;
    private final OrthographicCamera gameCamera;
    private final Viewport gameViewport;
    private final OrthographicCamera uiCamera;
    private final Viewport uiViewport;

    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final float baseCapHeight;
    private final GlyphLayout layout;

    // game components
    private final Level level;
    private PlayerController controller;
    private long lastTickTime = 0;

    // input
    private float inputGameX = 0;
    private float inputGameY = 0;
    private float inputUiX = 0;
    private float inputUiY = 0;
    private boolean isInputDown = false;
    private boolean isInputJustPressed = false;
    private boolean isInputJustReleased = false;
    private boolean isOverlappingBlueprints = false;

    public GameScreen(BubbleGame game, Level level) {
        this.game = game;
        this.level = level;

        // set up camera
        float camWidth = level.getCameraScale();
        float aspect = 16.0f / 9;
        float camHeight = camWidth / aspect;

        gameCamera = new OrthographicCamera();
        gameCamera.setToOrtho(false, camWidth, camHeight);
        gameCamera.position.set(level.getCameraCenterX() * Level.TILE_SIZE,
            level.getCameraCenterY() * Level.TILE_SIZE, 0);
        gameViewport = new FitViewport(camWidth, camHeight, gameCamera);
        gameViewport.apply();

        uiCamera = new OrthographicCamera();
        uiViewport = new FitViewport(1280, 720, uiCamera);
        uiViewport.apply();

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        font = new BitmapFont(); // default font
        baseCapHeight = font.getData().capHeight;
        layout = new GlyphLayout();
        font.setUseIntegerPositions(true); // smoother scaling
    }

    public void setPlayerController(PlayerController controller) {
        this.controller = controller;
        level.setPlayerController(controller);
    }

    @Override
    public void render(float delta) {
        long now = System.currentTimeMillis();
        if (now - lastTickTime >= TICK_LENGTH) {
            lastTickTime = now;
            level.tick();
            controller.tick();
        }

        // detect mouse click
        // TODO: create a more robust input handling system

        // clear screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // render level and gui components
        handleInput();
        drawGame(now);
        drawGui(now);
        drawHologram(now);
        doLogic();
    }

    /**
     * Renders the components of the game (not the UI)
     * @param now current time in milliseconds
     */
    private void drawGame(long now) {
        // set camera to center on level
        gameCamera.position.set(level.getCameraCenterX(), level.getCameraCenterY(), 0);
        gameCamera.update();
        shapeRenderer.setProjectionMatrix(gameCamera.combined);

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
        batch.setProjectionMatrix(gameCamera.combined);
        batch.begin();

        // draw tiles
        TileRenderer.renderTiles(game, batch, level.getGrid());

        // draw balls
        drawBalls(now);
        drawProjectiles(now);
        drawParticles(now);

        batch.end();
    }

    private void drawBalls(long now) {
        float delta = (now - lastTickTime) / (float) TICK_LENGTH;
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
            } catch (ClassCastException e) {
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
                    batch.draw(game.getAssets().PIXEL, barX, barY, barWidth * healthRatio, barHeight);
                    batch.setColor(Color.WHITE);
                }
            }
        }

        batch.setColor(1, 1, 1, 1);
    }

    private void drawProjectiles(long now) {
        float delta = (now - lastTickTime) / (float) TICK_LENGTH;

        for (Projectile p : level.getProjectiles()) {
            ObjectRenderer<Projectile> renderer = RendererRegistry.getProjectileRenderer(p.getId());
            if (renderer != null) try {
                renderer.render(batch, p, delta);
            } catch (ClassCastException e) {
                Gdx.app.error("Renderer", "Invalid projectile type", e);
            }
        }

        batch.setColor(1, 1, 1, 1);
    }

    private void drawParticles(long now) {
        float delta = (now - lastTickTime) / (float) TICK_LENGTH;

        for (Particle p : level.getParticles()) {
            ObjectRenderer<Particle> renderer = RendererRegistry.getParticleRenderer(p.getId());
            if (renderer != null) try {
                renderer.render(batch, p, delta);
            } catch (ClassCastException e) {
                Gdx.app.error("Renderer", "Invalid particle type", e);
            }
        }

        batch.setColor(1, 1, 1, 1);
    }

    private void drawGui(long now) {
        uiCamera.update();
        batch.setProjectionMatrix(uiCamera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.begin();

        if (controller != null) drawPlayerController(now);

        batch.end();
    }

    private void drawPlayerController(long now) {
        // water indicator
        Texture wiTex = game.getAssets().getTexture("gui_water_indicator");
        Texture sTex = game.getAssets().getTexture("blueprint_selected");
        final float SCREEN_HEIGHT = uiViewport.getScreenHeight();
        // internal rendering constants
        float WIDTH = 150f;
        float HEIGHT = 50f;
        batch.draw(wiTex, BLUEPRINT_X_MARGIN, SCREEN_HEIGHT - BLUEPRINT_Y_MARGIN - HEIGHT, WIDTH, HEIGHT);

        // scale font and render water text
        float desiredTextHeight = HEIGHT * 0.4f;
        setFontSize(font, desiredTextHeight);
        String waterText = String.valueOf(controller.getWater());
        layout.setText(font, waterText);
        float textX = BLUEPRINT_X_MARGIN + WIDTH - 8f;
        float textY = BLUEPRINT_Y_MARGIN + HEIGHT / 2f + layout.height / 2f;
        font.draw(batch, layout, textX - layout.width, SCREEN_HEIGHT - textY + desiredTextHeight);

        // render blueprints
        List<AbstractBlueprint<?>> blueprints = controller.getBlueprints();
        for (int i = 0; i < blueprints.size(); i++) {
            AbstractBlueprint<?> blueprint = blueprints.get(i);
            boolean canUse = controller.getWater() >= blueprint.getCost() &&
                controller.getTicks() - controller.getTimeLastUsed(i) > blueprint.getCooldown();
            final float B_YOFFSET = BLUEPRINT_Y_MARGIN + HEIGHT + (i + 1) * BLUEPRINT_Y_GAP + i * BLUEPRINT_HEIGHT;
            batch.draw(blueprint.getTexture(), BLUEPRINT_X_MARGIN, SCREEN_HEIGHT - B_YOFFSET - BLUEPRINT_HEIGHT,
                BLUEPRINT_WIDTH, BLUEPRINT_HEIGHT);

            // detect selection
            float blueprintTop = SCREEN_HEIGHT - B_YOFFSET;
            float blueprintBottom = SCREEN_HEIGHT - B_YOFFSET - BLUEPRINT_HEIGHT;
            if (canUse && isInputJustPressed &&
                inputUiX >= BLUEPRINT_X_MARGIN && inputUiX <= BLUEPRINT_X_MARGIN + BLUEPRINT_WIDTH &&
                inputUiY >= blueprintBottom && inputUiY <= blueprintTop) {
                if (controller.getSelectedIndex() == i) controller.setSelectedIndex(-1);
                else controller.setSelectedIndex(i);
            }

            // draw selection / loading bars
            if (!canUse) {
                batch.setColor(0, 0, 0, 0.5f);
                batch.draw(game.getAssets().PIXEL, BLUEPRINT_X_MARGIN, SCREEN_HEIGHT - B_YOFFSET - BLUEPRINT_HEIGHT,
                    BLUEPRINT_WIDTH, BLUEPRINT_HEIGHT);
                float cooldown_ratio = (float) (controller.getTicks() - controller.getTimeLastUsed(i))
                    / blueprint.getCooldown();
                if (cooldown_ratio < 1)
                    batch.draw(game.getAssets().PIXEL, BLUEPRINT_X_MARGIN,
                        SCREEN_HEIGHT - B_YOFFSET - (BLUEPRINT_HEIGHT * (1 - cooldown_ratio)),
                        BLUEPRINT_WIDTH, BLUEPRINT_HEIGHT * (1 - cooldown_ratio));
                batch.setColor(Color.WHITE);
            } else if (i == controller.getSelectedIndex()) {
                final float S_WIDTH = BLUEPRINT_WIDTH * (float) 528 / 512;
                final float S_HEIGHT = BLUEPRINT_HEIGHT * (float) 272 / 256;
                float blueprintCenterX = BLUEPRINT_X_MARGIN + BLUEPRINT_WIDTH / 2f;
                float blueprintCenterY = SCREEN_HEIGHT - B_YOFFSET - BLUEPRINT_HEIGHT / 2f;
                batch.draw(sTex, blueprintCenterX - S_WIDTH / 2f, blueprintCenterY - S_HEIGHT / 2f,
                    S_WIDTH, S_HEIGHT);
            }

            // draw cost
            float blueprintY = SCREEN_HEIGHT - B_YOFFSET - BLUEPRINT_HEIGHT;
            desiredTextHeight = BLUEPRINT_HEIGHT * 0.35f;
            setFontSize(font, desiredTextHeight);
            String costText = String.valueOf(blueprint.getCost());
            layout.setText(font, costText);
            final float PADDING = 6f;
            textX = BLUEPRINT_X_MARGIN + BLUEPRINT_WIDTH - PADDING - layout.width;
            textY = blueprintY + PADDING + layout.height;
            font.draw(batch, layout, textX, textY);
        }
    }

    private void handleInput() {
        boolean currentlyDown;
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            currentlyDown = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
            float rawX = Gdx.input.getX();
            float rawY = Gdx.input.getY();
            Vector3 worldPos = new Vector3(rawX, rawY, 0);
            gameViewport.unproject(worldPos);
            inputGameX = worldPos.x;
            inputGameY = worldPos.y;
            inputUiX = rawX;
            inputUiY = uiViewport.getScreenHeight() - rawY;
        } else {
            currentlyDown = Gdx.input.isTouched();
            if (currentlyDown) {
                float rawX = Gdx.input.getX();
                float rawY = Gdx.input.getY();
                Vector3 worldPos = new Vector3(rawX, rawY, 0);
                gameViewport.unproject(worldPos);
                inputGameX = worldPos.x;
                inputGameY = worldPos.y;
                inputUiX = rawX;
                inputUiY = uiViewport.getScreenHeight() - rawY;
            }
        }
        // ---- UPDATE STATES ----
        isInputJustPressed = currentlyDown && !isInputDown; // compare with current frame's down
        isInputJustReleased = !currentlyDown && isInputDown; // NEW: detect release
        // store previous state for next frame
        isInputDown = currentlyDown;

        // blueprint overlap check
        isOverlappingBlueprints = false;
        final float SCREEN_HEIGHT = uiViewport.getScreenHeight();
        List<AbstractBlueprint<?>> blueprints = controller.getBlueprints();
        for (int i = 0; i < blueprints.size(); i++) {
            final float B_YOFFSET = BLUEPRINT_Y_MARGIN + 50f + (i + 1) * BLUEPRINT_Y_GAP + i * BLUEPRINT_HEIGHT; // 50 = water indicator height
            float blueprintTop = SCREEN_HEIGHT - B_YOFFSET;
            float blueprintBottom = SCREEN_HEIGHT - B_YOFFSET - BLUEPRINT_HEIGHT;
            float blueprintRight = BLUEPRINT_X_MARGIN + BLUEPRINT_WIDTH;

            if (inputUiX >= BLUEPRINT_X_MARGIN && inputUiX <= blueprintRight &&
                inputUiY >= blueprintBottom && inputUiY <= blueprintTop) {
                isOverlappingBlueprints = true;
                break; // no need to check further
            }
        }
    }

    private void drawHologram(long now) {
        // no draw if out of bounds
        if (controller == null) return;
        if (inputUiX < 0 || inputUiX > uiViewport.getScreenWidth() ||
            inputUiY < 0 || inputUiY > uiViewport.getScreenHeight()) return;
        if (level.isTileSolid(inputGameX, inputGameY)) return;

        gameViewport.apply();
        batch.setProjectionMatrix(gameCamera.combined);
        batch.begin();

        if (!isOverlappingBlueprints && isInputDown && controller.getSelectedIndex() != -1) {
            Texture pbTex = game.getAssets().getTexture("player_bubble_back");
            AbstractBlueprint<?> blueprint = controller.getBlueprints().get(controller.getSelectedIndex());
            UnitRenderer renderer = blueprint.getHologramRenderer();
            if (renderer != null) {
                float radius = AbstractUnitObject.UNIT_SIZE;
                batch.setColor(1, 1, 1, 0.5f);
                batch.draw(pbTex, inputGameX - radius, inputGameY - radius, radius * 2, radius * 2);
                batch.setColor(1, 1, 1, 1);
                renderer.renderHologram(batch, level, inputGameX, inputGameY);
            }
        }
        batch.end();
    }

    private void doLogic() {
        // if released and unit is selected, deploy it
        if (isInputJustReleased && controller.getSelectedIndex() != -1 && !isOverlappingBlueprints) {
            // don't deploy if out of bounds, or over a solid tile
            if (inputUiX < 0 || inputUiX > uiViewport.getScreenWidth() ||
                inputUiY < 0 || inputUiY > uiViewport.getScreenHeight()) return;
            if (level.isTileSolid(inputGameX, inputGameY)) return;

            AbstractBlueprint<?> blueprint = controller.getBlueprints().get(controller.getSelectedIndex());
            if (controller.getWater() >= blueprint.getCost()) {
                controller.use(); // water and cooldown handled there
                // place unit
                BallObject obj = blueprint.produce(inputGameX, inputGameY);
                level.spawnBall(obj);
            }
        }
    }

    private void setFontSize(BitmapFont font, float targetPixelHeight) {
        BitmapFont.BitmapFontData data = font.getData();
        float scale = targetPixelHeight / baseCapHeight;
        data.setScale(scale);
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        uiViewport.update(width, height, true);
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
        font.dispose();
    }

    private final float BLUEPRINT_X_MARGIN = 10f;
    private final float BLUEPRINT_Y_MARGIN = 10f;
    private final float BLUEPRINT_WIDTH = 120f;
    private final float BLUEPRINT_HEIGHT = 60f;
    private final float BLUEPRINT_Y_GAP = 10f;
}
