package cyv.app.render.levelSelect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import cyv.app.BubbleGame;
import cyv.app.contents.LevelGroup;
import cyv.app.contents.LevelGroupRegistry;
import cyv.app.contents.LevelProvider;
import cyv.app.render.AbstractScreen;
import cyv.app.render.InputController;
import cyv.app.render.game.GameScreen;
import cyv.app.render.levelSelect.gui.GuiLevelInfo;
import cyv.app.util.MathUtils;

public class LevelSelectScreen extends AbstractScreen {
    private LevelGroup levelGroup;
    private final OrthographicCamera uiCamera;
    private final Viewport uiViewport;

    // input
    private final InputController inputController;

    public LevelSelectScreen(BubbleGame game, LevelGroup levelGroup) {
        super(game);
        this.levelGroup = levelGroup;

        if (levelGroup == null) {
            this.levelGroup = LevelGroupRegistry.getDefaultWorld();
        }

        uiCamera = new OrthographicCamera();
        uiViewport = new FitViewport(1280, 720, uiCamera);
        uiViewport.apply();

        this.inputController = new InputController(x -> x, y -> uiViewport.getScreenHeight() - y);
    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();
        draw();

        if (!isValid) this.dispose();
    }

    private void draw() {
        uiCamera.update();
        batch.setProjectionMatrix(uiCamera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.begin();

        final float SCREEN_WIDTH = uiViewport.getWorldWidth();
        final float SCREEN_HEIGHT = uiViewport.getWorldHeight();

        // back
        final float PANEL_SIZE = 640;
        final float PANEL_LEFT = SCREEN_WIDTH / 2 - PANEL_SIZE / 2;
        final float PANEL_BOTTOM = SCREEN_HEIGHT / 2 - PANEL_SIZE / 2;
        Texture pix = manager.PIXEL;
        batch.setColor(0, 0.25f, 0.3f, 1);
        batch.draw(pix, PANEL_LEFT, PANEL_BOTTOM, PANEL_SIZE, PANEL_SIZE);
        batch.setColor(1, 1, 1, 1);

        // individual levels
        final float LEVEL_SIZE = 104;
        final float MARGIN = 20;

        int i = 0;
        LevelProvider provider = null;
        LevelDrawLoop: for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                if (i >= levelGroup.getLevels().size()) break LevelDrawLoop;
                LevelProvider level = levelGroup.getLevels().get(i);
                i++;

                float clickX = inputController.getLastClickX();
                float clickY = inputController.getLastClickY();
                float cX = inputController.getX();
                float cY = inputController.getY();
                float drawX = PANEL_LEFT + MARGIN + (LEVEL_SIZE + MARGIN) * x;
                float drawY = PANEL_BOTTOM + MARGIN + (LEVEL_SIZE + MARGIN) * (4 - y);

                boolean guiBlocks = gui != null && gui.blocksInput();
                if (!guiBlocks && MathUtils.inBounds(cX, cY, drawX, drawX + LEVEL_SIZE, drawY,
                    drawY + LEVEL_SIZE)) {
                    batch.setColor(0.1f, 0.85f, 0.95f, 1);
                    if (MathUtils.inBounds(clickX, clickY, drawX, drawX + LEVEL_SIZE, drawY,
                        drawY + LEVEL_SIZE) && inputController.isInputJustReleased()) {
                        provider = level;
                    }
                } else batch.setColor(0, 0.7f, 0.85f, 1);

                batch.draw(pix, drawX, drawY, LEVEL_SIZE, LEVEL_SIZE);
                batch.setColor(1, 1, 1, 1);
            }
        }

        if (provider != null) {
            setGui(new GuiLevelInfo(this, manager, provider));
        }

        if (gui != null) gui.render(batch, fontRenderer, manager, uiViewport, 0);

        batch.end();
    }

    private void handleInput() {
        // update gui
        inputController.update();
        if (gui != null) {
            gui.updateMousePos(inputController.getX(), inputController.getY(),
                inputController.isInputJustPressed());
            if (inputController.isInputJustReleased()) gui.onInputReleased();
        }

        int i = 0;
    }

    /**
     * Called when a level is to be played (from the level gui)
     * @param provider Provider
     */
    public void playlevel(LevelProvider provider) {
        game.beginLevel(provider, levelGroup);
        this.isValid = false;
    }

    @Override
    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }
}
