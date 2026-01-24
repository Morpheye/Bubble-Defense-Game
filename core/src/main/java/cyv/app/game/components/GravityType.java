package cyv.app.game.components;

public enum GravityType {
    NONE, // No gravity
    STANDARD, // Unused, accelerates downwards
    HOMING, // Accelerates towards the nearest anchor
    ANCHOR // Remembers the spot it was spawned in and attempts to keep itself there
}
