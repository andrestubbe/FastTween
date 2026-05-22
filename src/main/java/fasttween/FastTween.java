package fasttween;

/**
 * FastTween - Ultra-fast native Java tweening engine.
 * 
 * <p>Mathematical value interpolation with zero overhead.
 * No internal ticker; drive this via FastAnimation or manual loops.
 */
public final class FastTween {
    
    public static final String VERSION = "0.1.0";

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
    }
}
