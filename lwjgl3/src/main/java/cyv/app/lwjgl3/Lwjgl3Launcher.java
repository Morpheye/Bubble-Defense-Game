package cyv.app.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import cyv.app.BubbleGame;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new BubbleGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration config =
            new Lwjgl3ApplicationConfiguration();

        config.setTitle("Bubble Game");
        config.useVsync(true);

        int width = 1280;
        int height = Math.round(width * 9f / 16f);

        config.setWindowedMode(width, height);
        config.setResizable(false); // optional but recommended

        return config;
    }
}
