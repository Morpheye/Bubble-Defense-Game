package cyv.app.render;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FontRenderer {
    private final BitmapFont font;
    private final GlyphLayout layout;
    private final float baseCapHeight;

    public FontRenderer(BitmapFont font) {
        this.font = font;
        this.layout = new GlyphLayout();
        this.baseCapHeight = font.getData().capHeight;
        font.setUseIntegerPositions(true);
    }

    /**
     * Sets the font height in world/UI units.
     */
    public void setSize(float targetHeight) {
        float scale = targetHeight / baseCapHeight;
        font.getData().setScale(scale);
    }

    /**
     * Draw text left-aligned at (x, y)
     */
    public void drawLeft(SpriteBatch batch, String text, float x, float y) {
        layout.setText(font, text);
        font.draw(batch, layout, x, y);
    }

    /**
     * Draw text right-aligned at (x, y)
     */
    public void drawRight(SpriteBatch batch, String text, float x, float y) {
        layout.setText(font, text);
        font.draw(batch, layout, x - layout.width, y);
    }

    /**
     * Draw text centered horizontally at (x, y)
     */
    public void drawCenter(SpriteBatch batch, String text, float x, float y) {
        layout.setText(font, text);
        font.draw(batch, layout, x - layout.width / 2f, y);
    }

    /**
     * Draw text centered both horizontally and vertically around (x, y)
     */
    public void drawCenterBoth(SpriteBatch batch, String text, float x, float y) {
        layout.setText(font, text);
        font.draw(batch, layout,
            x - layout.width / 2f,
            y + layout.height / 2f);
    }

    public float getTextWidth(String text) {
        layout.setText(font, text);
        return layout.width;
    }

    public float getTextHeight(String text) {
        layout.setText(font, text);
        return layout.height;
    }

    public BitmapFont getFont() {
        return font;
    }

    public void dispose() {
        font.dispose();
    }
}
