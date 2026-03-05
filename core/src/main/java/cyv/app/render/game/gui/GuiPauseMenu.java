package cyv.app.render.game.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import cyv.app.render.FontRenderer;
import cyv.app.render.ResourceManager;
import cyv.app.render.game.GameScreen;
import cyv.app.render.gui.Gui;
import cyv.app.render.gui.GuiButton;

import java.util.List;

public class GuiPauseMenu extends Gui<GameScreen> {
    public GuiPauseMenu(GameScreen parent, ResourceManager manager, Viewport viewport) {
        super(parent, manager, viewport);

        List<GuiButton> buttons = getButtons();
        buttons.add(new GuiButton(manager, 640, 480, 420, 80,
            "Back to Game", 30, () -> parent.setGui(null)));
        buttons.add(new GuiButton(manager, 640, 360, 420, 80,
            "Restart", 30, parent::restartLevel));
        buttons.add(new GuiButton(manager, 640, 240, 420, 80,
            "Exit Level", 30, parent::exitToMenu));
    }

    @Override
    public void render(SpriteBatch batcher, FontRenderer fontRenderer,
                       ResourceManager manager, Viewport viewport, float delta, boolean isFocused) {
        Gui<GameScreen> subGui = getSubGui();
        if (subGui != null) subGui.render(batcher, fontRenderer, manager, viewport, delta, false);

        // draw gray overlay
        final float SCREEN_WIDTH = viewport.getWorldWidth();
        final float SCREEN_HEIGHT = viewport.getWorldHeight();
        Texture pix = manager.PIXEL;
        batcher.setColor(0, 0, 0, getSubGui() != null ? 0.9f : 0.5f);
        batcher.draw(pix, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        batcher.setColor(1, 1, 1, 1);

        // draw buttons
        for (GuiButton button : getButtons()) {
            boolean hovered = isFocused && button.mouseOver(getMouseX(), getMouseY());
            button.render(batcher, fontRenderer, hovered);
        }
    }

    @Override
    public boolean acceptsSubGuis() {
        return true;
    }

    @Override
    public boolean pausesGame() {
        return true;
    }
}
