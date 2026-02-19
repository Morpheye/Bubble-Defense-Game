package cyv.app.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import cyv.app.BubbleGame;
import cyv.app.render.gui.Gui;

public abstract class AbstractScreen implements Screen {
    protected final BubbleGame game;
    protected final TextureManager manager;

    protected final SpriteBatch batch;
    protected final ShapeRenderer shapeRenderer;
    protected final FontRenderer fontRenderer;

    protected boolean isValid = true;

    // gui
    protected Gui<?> gui = null;

    public AbstractScreen(BubbleGame game) {
        this.game = game;
        this.manager = game.getAssets();

        batch = game.getBatcher();
        shapeRenderer = game.getShapeRenderer();
        fontRenderer = new FontRenderer(new BitmapFont());
    }

    public void setGui(Gui<?> newGui) {
        if (newGui == null) {
            // if there's no current gui, do nothing
            if (gui == null) return;

            // simply close existing gui
            gui.onClose();
            // if it contains a sub gui, open it
            Gui<?> subGui = gui.getSubGui();
            if (subGui != null) {
                gui = subGui;
                subGui.onOpen();
            } else gui = null;
        } else if (gui == null) {
            // a new gui is opened but there's no current gui
            gui = newGui;
            newGui.onOpen();
        } else {
            // a new gui is opened and there's an old gui
            if (newGui.acceptsSubGuis()) {
                // if the new gui accepts sub guis, don't close the old one
                newGui.setSubGui(gui);
            } else {
                // close the old gui
                gui.onClose();
            }
            gui = newGui;
            newGui.onOpen();
        }
    }

    @Override
    public void dispose() {
        fontRenderer.dispose();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
    }

    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
}
