package cyv.app.util;

public class MathUtils {
    public static float normalizeAngle(float degrees) {
        degrees = degrees % 360f;
        if (degrees > 180f) degrees -= 360f;
        if (degrees < -180f) degrees += 360f;
        return degrees;
    }

    public static float lerpAngleDeg(float from, float to, float alpha) {
        float diff = ((to - from + 540f) % 360f) - 180f; // shortest signed difference
        return from + diff * alpha;
    }

    public static boolean inBounds(double x, double y, double left, double right,
                                   double bottom, double top) {
        return x >= left && x <= right && y >= bottom && y <= top;
    }
}
