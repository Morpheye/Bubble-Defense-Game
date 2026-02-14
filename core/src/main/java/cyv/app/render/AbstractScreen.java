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
    protected Gui gui = null;

    public AbstractScreen(BubbleGame game) {
        this.game = game;
        this.manager = game.getAssets();

        batch = game.getBatcher();
        shapeRenderer = game.getShapeRenderer();
        fontRenderer = new FontRenderer(new BitmapFont());
    }

    public void setGui(Gui newGui) {
        if (gui != null) gui.onClose();
        gui = newGui;
        if (newGui != null) newGui.onOpen();
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
