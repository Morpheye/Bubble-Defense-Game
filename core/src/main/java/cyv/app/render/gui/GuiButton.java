package cyv.app.render.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import cyv.app.render.FontRenderer;
import cyv.app.render.ResourceManager;

public class GuiButton {
    private final ResourceManager manager;
    private final float centerX;
    private final float centerY;
    private final float width;
    private final float height;
    private final String text;
    private final float textScale;
    private final Runnable function;
    private final Texture tex;
    private final Texture hoverTex;

    public GuiButton(ResourceManager manager, float centerX, float centerY, float width, float height,
                     String text, float textSize, Runnable function) {
        this.manager = manager;
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = width;
        this.height = height;
        this.text = text;
        this.textScale = textSize;
        this.function = function;

        if (width / height == 420.0f / 80.0f) {
            this.tex = manager.getTexture("gui_button_wide");
            this.hoverTex = manager.getTexture("gui_button_wide_hovered");
        } else if (width / height == 200.0f / 75.0f) {
            this.tex = manager.getTexture("gui_button_standard");
            this.hoverTex = manager.getTexture("gui_button_standard_hovered");
        } else {
            this.tex = null;
            this.hoverTex = null;
        }
    }

    /**
     * Render the button
     * @param batcher sprite batcher
     * @param mouseOver whether the button is hovered over
     */
    public void render(SpriteBatch batcher, FontRenderer fontRenderer, boolean mouseOver) {
        float halfW = width / 2f;
        float halfH = height / 2f;

        // Outer border
        if (this.tex == null || this.hoverTex == null) {
            Texture pix = manager.PIXEL;
            batcher.setColor(0f, mouseOver ? 0.3f : 0.15f, mouseOver ? 0.5f : 0.25f, 1f);
            batcher.draw(pix, centerX - halfW, centerY - halfH, width, height);
            batcher.setColor(0f, mouseOver ? 0.9f : 0.65f, mouseOver ? 1f : 0.8f, 1f);
            batcher.draw(pix, centerX - halfW + 4, centerY - halfH + 4, width - 8, height - 8);
        } else {
            batcher.draw(mouseOver ? this.hoverTex : this.tex, centerX - halfW, centerY - halfH, width, height);
        }

        int textSize = (int) textScale;
        fontRenderer.setSize(textSize);
        fontRenderer.drawCenter(batcher, text, (int) centerX, (int) (centerY + textSize / 2f));

        batcher.setColor(1, 1, 1, 1);
    }

    /**
     * Check if mouse is over button
     */
    public boolean mouseOver(float mouseX, float mouseY) {
        float halfW = width / 2f;
        float halfH = height / 2f;
        return mouseX >= centerX - halfW && mouseX <= centerX + halfW &&
            mouseY >= centerY - halfH && mouseY <= centerY + halfH;
    }

    public void run() {
        function.run();
    }

}
