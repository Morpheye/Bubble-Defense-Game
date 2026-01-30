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
import cyv.app.render.FontRenderer;
import cyv.app.render.TextureManager;
import cyv.app.render.game.gui.Gui;
import cyv.app.render.game.gui.contents.GuiPauseMenu;
import cyv.app.render.game.renders.ObjectRenderer;
import cyv.app.render.game.renders.RendererRegistry;
import cyv.app.render.game.renders.UnitRenderer;

import java.util.List;

public class GameScreen implements Screen {
    public static final int TICK_LENGTH = 1000 / 20;

    private final BubbleGame game;
    private final TextureManager manager;
    private final OrthographicCamera gameCamera;
    private final Viewport gameViewport;
    private final OrthographicCamera uiCamera;
    private final Viewport uiViewport;

    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final FontRenderer fontRenderer;

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

    // gui
    private Gui gui = null;

    public GameScreen(BubbleGame game, Level level) {
        this.game = game;
        this.manager = game.getAssets();
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
        fontRenderer = new FontRenderer(new BitmapFont());
    }

    public void setPlayerController(PlayerController controller) {
        this.controller = controller;
        level.setPlayerController(controller);
    }

    @Override
    public void render(float delta) {
        long now = System.currentTimeMillis();
        if (now - lastTickTime >= TICK_LENGTH && (gui == null || !gui.pausesGame())) {
            lastTickTime = now;
            level.tick();
            controller.tick();
        }
        float d = Math.min(1, (now - lastTickTime) / (float) TICK_LENGTH);

        // clear screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // render level and gui components
        handleInput();
        drawGame(d);
        drawIngameGui(d);
        drawHologram(d);
        drawExternalGui(d);
        doLogic();
    }

    /**
     * Renders the components of the game (not the UI)
     */
    private void drawGame(float delta) {
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
        drawBalls(delta);
        drawProjectiles(delta);
        drawParticles(delta);

        batch.end();
    }

    private void drawBalls(float delta) {
        Texture pbTex = manager.getTexture("player_bubble_back");
        Texture ebTex = manager.getTexture("enemy_bubble_back");

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
                    batch.draw(manager.PIXEL, barX, barY, barWidth * healthRatio, barHeight);
                    batch.setColor(Color.WHITE);
                }
            }
        }

        batch.setColor(1, 1, 1, 1);
    }

    private void drawProjectiles(float delta) {
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

    private void drawParticles(float delta) {
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

    private void drawIngameGui(float delta) {
        uiCamera.update();
        batch.setProjectionMatrix(uiCamera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.begin();

        if (controller != null) drawPlayerController(delta);
        // draw pause button
        if (gui == null || !gui.pausesGame()) {
            Texture pauseButtonTex = manager.getTexture("gui_pause_button");
            float x = uiViewport.getScreenWidth() - PAUSE_BUTTON_SIZE - PAUSE_MARGIN;
            float y = uiViewport.getScreenHeight() - PAUSE_BUTTON_SIZE - PAUSE_MARGIN;
            batch.draw(pauseButtonTex, x, y, PAUSE_BUTTON_SIZE, PAUSE_BUTTON_SIZE);

            boolean inBounds = inputUiX >= x && inputUiY >= y && inputUiX <= x + PAUSE_BUTTON_SIZE &&
                inputUiY <= y + PAUSE_BUTTON_SIZE;
            if ((gui == null || !gui.blocksInput()) && inBounds) {
                batch.setColor(0.2f, 0.8f, 1, 0.5f);
                batch.draw(manager.PIXEL, x, y, PAUSE_BUTTON_SIZE, PAUSE_BUTTON_SIZE);
                batch.setColor(1, 1, 1, 1);
            }
        }

        batch.end();
    }

    private void drawPlayerController(float delta) {
        // water indicator
        Texture wiTex = manager.getTexture("gui_water_indicator");
        Texture sTex = manager.getTexture("blueprint_selected");
        final float SCREEN_HEIGHT = uiViewport.getScreenHeight();
        // internal rendering constants
        float WIDTH = 150f;
        float HEIGHT = 50f;
        batch.draw(wiTex, BLUEPRINT_X_MARGIN, SCREEN_HEIGHT - BLUEPRINT_Y_MARGIN - HEIGHT, WIDTH, HEIGHT);

        // scale font and render water text
        float desiredTextHeight = HEIGHT * 0.4f;
        String waterText = String.valueOf(controller.getWater());
        fontRenderer.setSize(desiredTextHeight);
        float textX = BLUEPRINT_X_MARGIN + WIDTH - 12f;
        float textY = SCREEN_HEIGHT - (BLUEPRINT_Y_MARGIN + HEIGHT / 2f);
        fontRenderer.drawRight(batch, waterText, textX, textY + desiredTextHeight / 2f);

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
            boolean withinBounds = inputUiX >= BLUEPRINT_X_MARGIN &&
                inputUiX <= BLUEPRINT_X_MARGIN + BLUEPRINT_WIDTH &&
                inputUiY >= blueprintBottom && inputUiY <= blueprintTop;
            if ((gui == null || !gui.blocksInput()) && canUse && isInputJustPressed &&
                inputUiX >= BLUEPRINT_X_MARGIN && withinBounds) {
                if (controller.getSelectedIndex() == i) controller.setSelectedIndex(-1);
                else controller.setSelectedIndex(i);
            }

            // draw selection / loading bars
            if (!canUse) {
                batch.setColor(0, 0, 0, 0.5f);
                batch.draw(manager.PIXEL, BLUEPRINT_X_MARGIN, SCREEN_HEIGHT - B_YOFFSET - BLUEPRINT_HEIGHT,
                    BLUEPRINT_WIDTH, BLUEPRINT_HEIGHT);
                float cooldown_ratio = (float) (controller.getTicks() - controller.getTimeLastUsed(i))
                    / blueprint.getCooldown();
                if (cooldown_ratio < 1)
                    batch.draw(manager.PIXEL, BLUEPRINT_X_MARGIN,
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
            } else if (withinBounds && (gui == null || !gui.blocksInput())) {
                final float S_WIDTH = BLUEPRINT_WIDTH * (float) 528 / 512;
                final float S_HEIGHT = BLUEPRINT_HEIGHT * (float) 272 / 256;
                float blueprintCenterX = BLUEPRINT_X_MARGIN + BLUEPRINT_WIDTH / 2f;
                float blueprintCenterY = SCREEN_HEIGHT - B_YOFFSET - BLUEPRINT_HEIGHT / 2f;
                batch.setColor(1, 1, 1, 0.5f);
                batch.draw(sTex, blueprintCenterX - S_WIDTH / 2f, blueprintCenterY - S_HEIGHT / 2f,
                    S_WIDTH, S_HEIGHT);
                batch.setColor(1, 1, 1, 1);
            }

            // draw cost
            float blueprintY = SCREEN_HEIGHT - B_YOFFSET - BLUEPRINT_HEIGHT;
            desiredTextHeight = BLUEPRINT_HEIGHT * 0.35f;
            String costText = String.valueOf(blueprint.getCost());
            final float PADDING = 6f;
            fontRenderer.setSize(desiredTextHeight);
            textX = BLUEPRINT_X_MARGIN + BLUEPRINT_WIDTH - PADDING;
            textY = blueprintY + PADDING + desiredTextHeight;
            fontRenderer.drawRight(batch, costText, textX, textY);
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

        // update states
        isInputJustPressed = currentlyDown && !isInputDown;
        isInputJustReleased = !currentlyDown && isInputDown;
        // store previous state for next frame
        isInputDown = currentlyDown;

        // update gui
        if (gui != null) {
            gui.updateMousePos(inputUiX, inputUiY, isInputJustPressed);
            if (isInputJustReleased) gui.onInputReleased();
        }

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

    private void drawHologram(float delta) {
        // no draw if out of bounds
        if (controller == null) return;
        if (inputUiX < 0 || inputUiX > uiViewport.getScreenWidth() ||
            inputUiY < 0 || inputUiY > uiViewport.getScreenHeight()) return;
        if (level.isTileSolid(inputGameX, inputGameY)) return;

        gameViewport.apply();
        batch.setProjectionMatrix(gameCamera.combined);
        batch.begin();

        if (!isOverlappingBlueprints && isInputDown && controller.getSelectedIndex() != -1) {
            Texture pbTex = manager.getTexture("player_bubble_back");
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

    private void drawExternalGui(float delta) {
        if (gui == null) return;
        uiViewport.apply();
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        gui.render(batch, fontRenderer, manager, uiViewport, delta);

        batch.end();
    }

    private void doLogic() {
        boolean checkGameInput = gui == null || !gui.blocksInput();

        // if released and unit is selected, deploy it
        if (checkGameInput && isInputJustReleased && controller.getSelectedIndex() != -1 &&
            !isOverlappingBlueprints) {
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

        // pause logic
        float x = uiViewport.getScreenWidth() - PAUSE_BUTTON_SIZE - PAUSE_MARGIN;
        float y = uiViewport.getScreenHeight() - PAUSE_BUTTON_SIZE - PAUSE_MARGIN;
        boolean inBounds = inputUiX >= x && inputUiY >= y && inputUiX <= x + PAUSE_BUTTON_SIZE &&
            inputUiY <= y + PAUSE_BUTTON_SIZE;
        boolean pauseKeyPressed = Gdx.input.isKeyJustPressed(Input.Keys.P);
        if (pauseKeyPressed || (gui == null && inBounds && isInputJustPressed)) {
            if (gui != null) setGui(null);
            else {
                controller.setSelectedIndex(-1);
                setGui(new GuiPauseMenu(this, manager));
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    public void setGui(Gui newGui) {
        if (gui != null) gui.onClose();
        gui = newGui;
        if (newGui != null) newGui.onOpen();
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
        fontRenderer.dispose();
    }

    private final float BLUEPRINT_X_MARGIN = 10f;
    private final float BLUEPRINT_Y_MARGIN = 10f;
    private final float BLUEPRINT_WIDTH = 120f;
    private final float BLUEPRINT_HEIGHT = 60f;
    private final float BLUEPRINT_Y_GAP = 10f;
    private final float PAUSE_MARGIN = 10f;
    private final float PAUSE_BUTTON_SIZE = 50f;
}
