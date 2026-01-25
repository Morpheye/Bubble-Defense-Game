package cyv.app.util;

public class MathUtils {
    public static float normalizeAngle(float degrees) {
        degrees = degrees % 360f;
        if (degrees > 180f) degrees -= 360f;
        if (degrees < -180f) degrees += 360f;
        return degrees;
    }
}
