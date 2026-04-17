package fasttween;

/**
 * FastTween - Ultra-fast native Java tweening engine.
 * 
 * <p>Mathematical value interpolation with zero overhead.
 * No JNI, no scenegraph, just pure easing functions.
 * 
 * <p><b>Basic usage:</b>
 * <pre>
 * // Simple tween from 0 to 100 over 300ms
 * Tween tween = FastTween.to(0f, 100f, 300)
 *     .ease(Ease.CUBIC_OUT)
 *     .onUpdate(v -> System.out.println(v))
 *     .start();
 * 
 * // Custom easing via lambda
 * FastTween.to(0f, 1f, 500)
 *     .ease(t -> t * t * (3 - 2 * t))  // smoothstep
 *     .start();
 * </pre>
 * 
 * <p><b>Features:</b>
 * <ul>
 *   <li>8 essential easing functions</li>
 *   <li>Custom easing via {@link EaseFunction} lambdas</li>
 *   <li>Float, double, and int interpolation</li>
 *   <li>Zero allocation (reusable instances)</li>
 *   <li>Pure Java - no JNI overhead</li>
 * </ul>
 * 
 * @author FastJava Team
 * @version 1.0.0
 */
public final class FastTween {
    
    public static final String VERSION = "1.0.0";
    
    private FastTween() {
        // Utility class
    }
    
    /**
     * Creates a tween from a starting value to an ending value.
     * 
     * @param from Starting value
     * @param to Ending value
     * @param durationMs Duration in milliseconds
     * @return Configured tween instance (call .start() to begin)
     */
    public static Tween to(float from, float to, long durationMs) {
        return new Tween()
            .fromTo(from, to)
            .duration(durationMs);
    }
    
    /**
     * Creates a tween from 0 to the specified value.
     * 
     * @param to Ending value
     * @param durationMs Duration in milliseconds
     * @return Configured tween instance
     */
    public static Tween to(float to, long durationMs) {
        return to(0f, to, durationMs);
    }
    
    /**
     * Creates a tween from the current value to 0.
     * 
     * @param from Starting value
     * @param durationMs Duration in milliseconds
     * @return Configured tween instance
     */
    public static Tween from(float from, long durationMs) {
        return to(from, 0f, durationMs);
    }
    
    /**
     * Creates a tween with explicit from/to values.
     * 
     * @param from Starting value
     * @param to Ending value
     * @param durationMs Duration in milliseconds
     * @return Configured tween instance
     */
    public static Tween fromTo(float from, float to, long durationMs) {
        return to(from, to, durationMs);
    }
    
    /**
     * Main entry point for demo/testing.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println("FastTween v" + VERSION);
        System.out.println("Use examples/00-basic-usage/ for runnable demos");
    }
}
