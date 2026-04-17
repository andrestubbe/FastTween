package fasttween.opt;

/**
 * Optimized factory for creating pooled, zero-allocation tweens.
 * 
 * <p>Use this instead of {@link fasttween.FastTween} when GC pressure matters:
 * <ul>
 *   <li>Object pooling via {@link TweenPool}</li>
 *   <li>Primitive callbacks via {@link FloatConsumer}</li>
 *   <li>Auto-return to pool on completion</li>
 * </ul>
 * 
 * <p><b>Example usage:</b>
 * <pre>
 * // Create and start (automatically pooled)
 * TweenOpt tween = FastTweenOpt.to(0f, 100f, 300)
 *     .ease(Ease.CUBIC_OUT)
 *     .onUpdate(v -> position.x = v)  // v is primitive!
 *     .start();
 * 
 * // Tween automatically returns to pool when complete
 * </pre>
 * 
 * @author FastJava Team
 * @version 1.0.0
 */
public final class FastTweenOpt {
    
    public static final String VERSION = "1.0.0-opt";
    
    private FastTweenOpt() {
        // Utility class
    }
    
    /**
     * Creates a tween from 0 to endValue.
     * 
     * @param endValue Ending value
     * @param durationMs Duration in milliseconds
     * @return Configurable TweenOpt from pool
     */
    public static TweenOpt to(float endValue, long durationMs) {
        return TweenPool.acquire()
            .fromTo(0f, endValue)
            .duration(durationMs);
    }
    
    /**
     * Creates a tween from startValue to endValue.
     * 
     * @param startValue Starting value
     * @param endValue Ending value
     * @param durationMs Duration in milliseconds
     * @return Configurable TweenOpt from pool
     */
    public static TweenOpt fromTo(float startValue, float endValue, long durationMs) {
        return TweenPool.acquire()
            .fromTo(startValue, endValue)
            .duration(durationMs);
    }
    
    /**
     * Creates a tween from startValue to 0.
     * 
     * @param startValue Starting value
     * @param durationMs Duration in milliseconds
     * @return Configurable TweenOpt from pool
     */
    public static TweenOpt from(float startValue, long durationMs) {
        return TweenPool.acquire()
            .fromTo(startValue, 0f)
            .duration(durationMs);
    }
    
    /**
     * Returns pool statistics for current thread.
     * 
     * @return Statistics string
     */
    public static String poolStats() {
        return TweenPool.getStats();
    }
    
    /**
     * Clears the tween pool for current thread.
     * Use for testing or memory cleanup.
     */
    public static void clearPool() {
        TweenPool.clear();
    }
    
    /**
     * Returns number of available tweens in pool.
     * 
     * @return Available count
     */
    public static int poolAvailable() {
        return TweenPool.available();
    }
    
    /**
     * Main for quick test.
     * 
     * @param args Command line args
     */
    public static void main(String[] args) {
        System.out.println("FastTweenOpt v" + VERSION);
        System.out.println("Use TweenBenchmark for performance testing");
    }
}
