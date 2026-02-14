package cyv.app.render.levelSelect.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import cyv.app.contents.LevelProvider;
import cyv.app.render.FontRenderer;
import cyv.app.render.TextureManager;
import cyv.app.render.game.GameScreen;
import cyv.app.render.gui.Gui;
import cyv.app.render.gui.GuiButton;
import cyv.app.render.levelSelect.LevelSelectScreen;

import java.util.List;

public class GuiLevelInfo extends Gui<LevelSelectScreen> {
    private final LevelProvider provider;

    public GuiLevelInfo(LevelSelectScreen parent, TextureManager manager, LevelProvider provider) {
        super(parent, manager);
        final float WIDTH = 1280;
        final float HEIGHT = 720;
        List<GuiButton> buttons = getButtons();
        buttons.add(new GuiButton(manager, WIDTH / 2, HEIGHT / 3, WIDTH / 3, HEIGHT / 9,
            "Play", HEIGHT / 27,
            () -> parent.playlevel(provider)));

        this.provider = provider;
    }

    @Override
    public void render(SpriteBatch batcher, FontRenderer fontRenderer,
                       TextureManager manager, Viewport viewport, float delta) {
        // draw gray overlay
        final float SCREEN_WIDTH = viewport.getScreenWidth();
        final float SCREEN_HEIGHT = viewport.getScreenHeight();
        Texture pix = manager.PIXEL;
        batcher.setColor(0, 0, 0, 0.5f);
        batcher.draw(pix, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        batcher.setColor(1, 1, 1, 1);

        // draw buttons
        for (GuiButton button : getButtons()) {
            boolean hovered = button.mouseOver(getMouseX(), getMouseY());
            button.render(batcher, fontRenderer, hovered);
        }
    }

    @Override
    public boolean pausesGame() {
        return true;
    }
}
