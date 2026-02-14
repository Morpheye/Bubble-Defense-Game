package cyv.app.render.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import cyv.app.render.FontRenderer;
import cyv.app.render.TextureManager;

public class GuiButton {
    private final TextureManager manager;
    private final float centerX;
    private final float centerY;
    private final float scaleX;
    private final float scaleY;
    private final String text;
    private final float textScale;
    private final Runnable function;

    public GuiButton(TextureManager manager, float centerX, float centerY, float width, float height,
                     String text, float textSize, Runnable function) {
        this.manager = manager;
        this.centerX = centerX;
        this.centerY = centerY;
        this.scaleX = width;
        this.scaleY = height;
        this.text = text;
        this.textScale = textSize;
        this.function = function;
    }

    /**
     * Render the button
     * @param batcher sprite batcher
     * @param mouseOver whether the button is hovered over
     */
    public void render(SpriteBatch batcher, FontRenderer fontRenderer, boolean mouseOver) {
        Texture tex = manager.PIXEL;
        float halfW = scaleX / 2f;
        float halfH = scaleY / 2f;

        // Outer border
        batcher.setColor(0f, mouseOver ? 0.3f : 0.15f, mouseOver ? 0.5f : 0.25f, 1f);
        batcher.draw(tex, centerX - halfW, centerY - halfH, scaleX, scaleY);

        // Inner fill
        batcher.setColor(0f, mouseOver ? 0.9f : 0.65f, mouseOver ? 1f : 0.8f, 1f);
        batcher.draw(tex, centerX - halfW + 4, centerY - halfH + 4, scaleX - 8, scaleY - 8);

        int textSize = (int) textScale;
        fontRenderer.setSize(textSize);
        fontRenderer.drawCenter(batcher, text, (int) centerX, (int) (centerY + textSize / 2f));

        batcher.setColor(1, 1, 1, 1);
    }

    /**
     * Check if mouse is over button
     */
    public boolean mouseOver(float mouseX, float mouseY) {
        float halfW = scaleX / 2f;
        float halfH = scaleY / 2f;
        return mouseX >= centerX - halfW && mouseX <= centerX + halfW &&
            mouseY >= centerY - halfH && mouseY <= centerY + halfH;
    }

    public void run() {
        function.run();
    }

}
