package cyv.app.game;

import cyv.app.render.game.GameScreen;

import java.util.function.Consumer;

public class ScheduledTask {
    public final int tick;
    public final Consumer<GameScreen> task;

    public ScheduledTask(int tick, Consumer<GameScreen> task) {
        this.tick = tick;
        this.task = task;
    }
}
