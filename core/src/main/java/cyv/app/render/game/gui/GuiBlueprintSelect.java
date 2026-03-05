package cyv.app.render.game.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import cyv.app.contents.SaveManager;
import cyv.app.game.PlayerController;
import cyv.app.game.blueprints.AbstractBlueprint;
import cyv.app.game.blueprints.BlueprintRegistry;
import cyv.app.render.FontRenderer;
import cyv.app.render.ResourceManager;
import cyv.app.render.game.GameScreen;
import cyv.app.render.gui.Gui;
import cyv.app.render.gui.GuiButton;
import cyv.app.util.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class GuiBlueprintSelect extends Gui<GameScreen> {
    // constants
    private final int LIST_SIZE = 6; // for now, only 6 blueprints usable at once
    private final int GUI_X = 5;
    private final int GUI_Y = LIST_SIZE;

    private final GameScreen parent;
    private final List<AbstractBlueprint<?>> availableBlueprints;
    private final List<AbstractBlueprint<?>> selectedBlueprints = new ArrayList<>();
    private int page = 0; // current page
    private AbstractBlueprint<?> hoveredBlueprint = null;
    private boolean pauseButtonHovered = false;

    public GuiBlueprintSelect(GameScreen parent, ResourceManager manager, Viewport viewport) {
        super(parent, manager, viewport);
        this.parent = parent;

        availableBlueprints = new ArrayList<>();
        for (String bs : SaveManager.getInstance().getOwnedBlueprints()) {
            availableBlueprints.add(BlueprintRegistry.getBlueprint(bs));
        }

        getButtons().add(new GuiButton(manager, 640, 90, 200, 75, "Ready!", 25,
            () -> {
                parent.setPlayerController(new PlayerController(selectedBlueprints));
                parent.setGui(null);
            }));
    }

    @Override
    public void render(SpriteBatch batcher, FontRenderer fontRenderer, ResourceManager manager,
                       Viewport viewport, float delta, boolean isFocused) {
        // draw gray overlay
        final float SCREEN_WIDTH = viewport.getWorldWidth();
        final float SCREEN_HEIGHT = viewport.getWorldHeight();
        Texture pix = manager.PIXEL;
        batcher.setColor(0, 0, 0, 0.5f);
        batcher.draw(pix, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        batcher.setColor(1, 1, 1, 1);

        // draw pause button
        Texture pauseButtonTex = manager.getTexture("gui_pause_button");
        final float PAUSE_MARGIN = 10f;
        final float PAUSE_BUTTON_SIZE = 50f;
        float x = SCREEN_WIDTH - PAUSE_BUTTON_SIZE - PAUSE_MARGIN;
        float y = SCREEN_HEIGHT - PAUSE_BUTTON_SIZE - PAUSE_MARGIN;
        batcher.draw(pauseButtonTex, x, y, PAUSE_BUTTON_SIZE, PAUSE_BUTTON_SIZE);

        float inputUiX = getMouseX();
        float inputUiY =  getMouseY();
        pauseButtonHovered = false;
        boolean inBounds = inputUiX >= x && inputUiY >= y && inputUiX <= x + PAUSE_BUTTON_SIZE &&
            inputUiY <= y + PAUSE_BUTTON_SIZE;
        if (inBounds && isFocused) {
            batcher.setColor(0.2f, 0.8f, 1, 0.5f);
            batcher.draw(manager.PIXEL, x, y, PAUSE_BUTTON_SIZE, PAUSE_BUTTON_SIZE);
            batcher.setColor(1, 1, 1, 1);
            pauseButtonHovered = true;
        }


        // top text (TEMP)
        fontRenderer.setSize(60);
        fontRenderer.drawCenterBoth(batcher, "Select Blueprints", SCREEN_WIDTH / 2, SCREEN_HEIGHT * 9 / 10);

        // render current selected blueprints
        final float BLUEPRINT_X_MARGIN = 10f;
        final float BLUEPRINT_WIDTH = 120f;
        final float BLUEPRINT_HEIGHT = 60f;
        final float BLUEPRINT_GAP = 10f;
        final float SCREEN_CENTER_X = SCREEN_WIDTH / 2;
        final float SCREEN_CENTER_Y = SCREEN_HEIGHT / 2;
        Texture emptyTex = manager.getTexture("blueprint_unselected");
        Texture sTex = manager.getTexture("blueprint_selected");
        hoveredBlueprint = null;
        final float TOTAL_HEIGHT = LIST_SIZE * BLUEPRINT_HEIGHT + (LIST_SIZE - 1) * BLUEPRINT_GAP;
        for (int i = 0; i < LIST_SIZE; i++) {
            AbstractBlueprint<?> curr = null;
            Texture tex;
            if (selectedBlueprints.size() <= i) {
                tex = emptyTex;
            } else {
                curr = selectedBlueprints.get(i);
                tex = curr == null ? emptyTex : curr.getTexture();
            }

            float y_start = SCREEN_CENTER_Y + (TOTAL_HEIGHT / 2) + BLUEPRINT_GAP -
                (i + 1) * (BLUEPRINT_HEIGHT + BLUEPRINT_GAP);

            batcher.draw(tex, BLUEPRINT_X_MARGIN, y_start, BLUEPRINT_WIDTH, BLUEPRINT_HEIGHT);
            inBounds = MathUtils.inBounds(getMouseX(), getMouseY(),
                BLUEPRINT_X_MARGIN, BLUEPRINT_X_MARGIN + BLUEPRINT_WIDTH,
                y_start, y_start + BLUEPRINT_HEIGHT);
            if (inBounds) {
                final float S_WIDTH = BLUEPRINT_WIDTH * (float) 528 / 512;
                final float S_HEIGHT = BLUEPRINT_HEIGHT * (float) 272 / 256;
                float blueprintCenterX = BLUEPRINT_X_MARGIN + BLUEPRINT_WIDTH / 2;
                float blueprintCenterY = y_start + BLUEPRINT_HEIGHT / 2;
                batcher.setColor(1, 1, 1, 0.75f);
                batcher.draw(sTex, blueprintCenterX - S_WIDTH / 2f, blueprintCenterY - S_HEIGHT / 2f,
                    S_WIDTH, S_HEIGHT);
                batcher.setColor(1, 1, 1, 1);
                hoveredBlueprint = curr;
            }
        }

        // render actual selection box
        final float BOX_X = GUI_X * BLUEPRINT_WIDTH + (GUI_X + 1) * BLUEPRINT_GAP;
        final float BOX_Y = GUI_Y * BLUEPRINT_HEIGHT + (GUI_Y + 1) * BLUEPRINT_GAP;
        batcher.setColor(0, 0.1f, 0.15f, 1);
        batcher.draw(manager.PIXEL, SCREEN_CENTER_X - BOX_X / 2, SCREEN_CENTER_Y - BOX_Y / 2,
            BOX_X, BOX_Y);
        batcher.setColor(1, 1, 1, 1);

        int pageLimit = Math.max((int) Math.ceil((float) availableBlueprints.size() / (BOX_X * BOX_Y)), 1);
        if (page >= pageLimit) page = pageLimit - 1;
        for (int i = 0; i < GUI_X * GUI_Y; i++) {
            int index = i + page * (GUI_X * GUI_Y);
            if (index >= availableBlueprints.size()) break;
            AbstractBlueprint<?> bp = availableBlueprints.get(index);
            x = i % GUI_X;
            y = i / GUI_X;

            float left = SCREEN_CENTER_X - BOX_X / 2 + BLUEPRINT_GAP + x * (BLUEPRINT_WIDTH + BLUEPRINT_GAP);
            float bottom = SCREEN_CENTER_Y + BOX_Y / 2 - (1 + y) * (BLUEPRINT_HEIGHT + BLUEPRINT_GAP);

            if (selectedBlueprints.contains(bp)) batcher.setColor(1, 1, 1, 0.5f);
            batcher.draw(bp.getTexture(), left, bottom, BLUEPRINT_WIDTH, BLUEPRINT_HEIGHT);
            batcher.setColor(1, 1, 1, 1);

            if (MathUtils.inBounds(getMouseX(), getMouseY(),
                left, left + BLUEPRINT_WIDTH, bottom, bottom + BLUEPRINT_HEIGHT)) {
                final float S_WIDTH = BLUEPRINT_WIDTH * (float) 528 / 512;
                final float S_HEIGHT = BLUEPRINT_HEIGHT * (float) 272 / 256;
                float blueprintCenterX = left + BLUEPRINT_WIDTH / 2;
                float blueprintCenterY = bottom + BLUEPRINT_HEIGHT / 2;
                batcher.setColor(1, 1, 1, 0.75f);
                batcher.draw(sTex, blueprintCenterX - S_WIDTH / 2f, blueprintCenterY - S_HEIGHT / 2f,
                    S_WIDTH, S_HEIGHT);
                batcher.setColor(1, 1, 1, 1);
                hoveredBlueprint = bp;
            }
        }

        // draw buttons
        for (GuiButton button : getButtons()) {
            boolean hovered = isFocused && button.mouseOver(getMouseX(), getMouseY());
            button.render(batcher, fontRenderer, hovered);
        }
    }

    @Override
    public void onInputReleased() {
        // detect clicks on blueprints
        if (hoveredBlueprint != null) {
            if (selectedBlueprints.contains(hoveredBlueprint)) {
                selectedBlueprints.remove(hoveredBlueprint);
            } else if (selectedBlueprints.size() < LIST_SIZE) {
                selectedBlueprints.add(hoveredBlueprint);
            }
        }

        // pause button
        if (pauseButtonHovered) {
            getFrontendIn().setGui(new GuiPauseMenu(getFrontendIn(), getTextureManager(), getViewport()));
        }

        super.onInputReleased();
    }

    @Override
    public boolean isClosable() {
        return false;
    }

    @Override
    public void onClose() {
        parent.setPlayerController(new PlayerController(selectedBlueprints));
    }

    @Override
    public boolean pausesGame() {
        return true;
    }

}
