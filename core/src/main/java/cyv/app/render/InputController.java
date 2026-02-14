package cyv.app.render;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.function.Function;

public class InputController {
    private final Function<Float, Float> xFunc;
    private final Function<Float, Float> yFunc;

    private float x = 0;
    private float y = 0;
    private float lastClickX = 0;
    private float lastClickY = 0;
    private boolean isInputDown = false;
    private boolean isInputJustPressed = false;
    private boolean isInputJustReleased = false;

    public InputController(Function<Float, Float> xFunc, Function<Float, Float> yFunc) {
        this.xFunc = xFunc;
        this.yFunc = yFunc;
    }

    public void update() {
        boolean currentlyDown;
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            currentlyDown = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
            this.x = xFunc.apply((float) Gdx.input.getX());
            this.y = yFunc.apply((float) Gdx.input.getY());
        } else {
            currentlyDown = Gdx.input.isTouched();
            if (currentlyDown) {
                this.x = xFunc.apply((float) Gdx.input.getX());
                this.y = yFunc.apply((float) Gdx.input.getY());
            }
        }

        // update states
        isInputJustPressed = currentlyDown && !isInputDown;
        isInputJustReleased = !currentlyDown && isInputDown;
        isInputDown = currentlyDown;

        if (isInputJustPressed) {
            this.lastClickX = this.x;
            this.lastClickY = this.y;
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getLastClickX() {
        return lastClickX;
    }

    public float getLastClickY() {
        return lastClickY;
    }

    public boolean isInputDown() {
        return isInputDown;
    }

    public boolean isInputJustPressed() {
        return isInputJustPressed;
    }

    public boolean isInputJustReleased() {
        return isInputJustReleased;
    }
}
