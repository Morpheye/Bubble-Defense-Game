package cyv.app.render.gui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import cyv.app.render.AbstractScreen;
import cyv.app.render.FontRenderer;
import cyv.app.render.TextureManager;
import cyv.app.render.game.GameScreen;

import java.util.ArrayList;
import java.util.List;

public abstract class Gui<T extends AbstractScreen> {
    private final T parent;
    private final TextureManager manager;
    private float lastPressedX;
    private float lastPressedY;
    private float mouseX;
    private float mouseY;
    private final List<GuiButton> buttons;

    private Gui<T> subGui;

    public Gui(T parent, TextureManager manager) {
        this.parent = parent;
        this.manager = manager;
        this.buttons = new ArrayList<>();
    }

    protected final List<GuiButton> getButtons() {
        return buttons;
    }

    protected final T getFrontendIn() {
        return parent;
    }

    protected final TextureManager getTextureManager() {
        return manager;
    }

    public final void updateMousePos(float x, float y, boolean justPressed) {
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

    public boolean acceptsSubGuis() {
        return false;
    }

    public final Gui<T> getSubGui() {
        return subGui;
    }

    public final void setSubGui(Gui<?> subGui) {
        if (!acceptsSubGuis())
            throw new UnsupportedOperationException("This GUI does not support sub GUIs.");
        this.subGui = (Gui<T>) subGui;
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
