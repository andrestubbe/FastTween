package fasttween;

/**
 * Mathematical interpolation utilities.
 * 
 * <p>Core math functions for tweening values. Pure Java implementation
 * with zero JNI overhead - these calculations are so efficient that
 * native code would only add call overhead.
 * 
 * @author FastJava Team
 * @version 1.0.0
 */
public final class Interpolation {
    
    private Interpolation() {
        // Utility class
    }
    
    /**
     * Linear interpolation between two float values.
     * 
     * @param start Starting value
     * @param end Ending value
     * @param t Interpolation factor [0.0, 1.0]
     * @return Interpolated value
     */
    public static float lerp(float start, float end, float t) {
        return start + (end - start) * t;
    }
    
    /**
     * Linear interpolation between two double values.
     * 
     * @param start Starting value
     * @param end Ending value
     * @param t Interpolation factor [0.0, 1.0]
     * @return Interpolated value
     */
    public static double lerp(double start, double end, float t) {
        return start + (end - start) * t;
    }
    
    /**
     * Linear interpolation between two int values.
     * 
     * @param start Starting value
     * @param end Ending value
     * @param t Interpolation factor [0.0, 1.0]
     * @return Interpolated value (truncated)
     */
    public static int lerp(int start, int end, float t) {
        return (int) (start + (end - start) * t);
    }
    
    /**
     * Clamps a value to the range [0.0, 1.0].
     * 
     * @param value Value to clamp
     * @return Clamped value
     */
    public static float clamp01(float value) {
        if (value < 0) return 0;
        if (value > 1) return 1;
        return value;
    }
    
    /**
     * Smoothstep interpolation (3t² - 2t³).
     * Smooth acceleration and deceleration.
     * 
     * @param t Input value [0.0, 1.0]
     * @return Smoothed value
     */
    public static float smoothstep(float t) {
        return t * t * (3 - 2 * t);
    }
    
    /**
     * Smootherstep interpolation (6t⁵ - 15t⁴ + 10t³).
     * Even smoother acceleration and deceleration.
     * 
     * @param t Input value [0.0, 1.0]
     * @return Smoothed value
     */
    public static float smootherstep(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }
}
