package cyv.app.render.game.gui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import cyv.app.render.FontRenderer;
import cyv.app.render.TextureManager;
import cyv.app.render.game.GameScreen;

import java.util.ArrayList;
import java.util.List;

public abstract class Gui {
    private final GameScreen parent;
    private final TextureManager manager;
    private float lastPressedX;
    private float lastPressedY;
    private float mouseX;
    private float mouseY;
    private final List<GuiButton> buttons;

    public Gui(GameScreen parent, TextureManager manager) {
        this.parent = parent;
        this.manager = manager;
        this.buttons = new ArrayList<>();
    }

    protected List<GuiButton> getButtons() {
        return buttons;
    }

    protected GameScreen getFrontendIn() {
        return parent;
    }

    protected TextureManager getTextureManager() {
        return manager;
    }

    public void updateMousePos(float x, float y, boolean justPressed) {
        this.mouseX = x;
        this.mouseY = y;
        if (justPressed) {
            this.lastPressedX = x;
            this.lastPressedY = y;
        }
    }

    public abstract void render(SpriteBatch batcher, FontRenderer fontRenderer,
                                TextureManager manager, Viewport viewport, float delta);

    public void onInputReleased() {
        for (GuiButton button : getButtons()) {
            if (button.mouseOver(mouseX, mouseY) && button.mouseOver(lastPressedX, lastPressedY)) {
                button.run();
                return;
            }
        }
    }

    public void onOpen() {

    }

    public void onClose() {

    }

    /**
     * Whether the gui stops the game from ticking
     */
    public boolean pausesGame() {
        return false;
    }

    /**
     * Whether the gui prevents the player from interacting with the game
     */
    public boolean blocksInput() {
        return pausesGame();
    }

    public float getLastPressedX() {
        return lastPressedX;
    }

    public float getLastPressedY() {
        return lastPressedY;
    }

    public float getMouseX() {
        return mouseX;
    }

    public float getMouseY() {
        return mouseY;
    }
}
