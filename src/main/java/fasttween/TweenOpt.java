package fasttween;

/**
 * Optimized tween implementation with zero-allocation goals.
 * 
 * <p>Key optimizations over standard Tween:
 * <ul>
 *   <li>{@link FloatConsumer} instead of {@code Consumer<Float>} - no autoboxing</li>
 *   <li>Object pooling via {@link TweenPool} - reuse instead of GC</li>
 *   <li>Direct field access - minimal overhead</li>
 *   <li>No lambda capturing in hot paths</li>
 * </ul>
 * 
 * <p>Use with {@link FastTweenOpt} factory for automatic pool management:
 * <pre>
 * TweenOpt tween = FastTweenOpt.to(0f, 100f, 300)
 *     .ease(Ease.CUBIC_OUT)
 *     .onUpdate(v -> position.x = v)
 *     .start();
 * </pre>
 * 
 * @author FastJava Team
 * @version 1.0.0
 */
public final class TweenOpt {
    
    // Core tween data (all primitives, no objects)
    private float startValue;
    private float endValue;
    private float currentValue;
    private long durationMs;
    private long startTime;
    
    // State flags (packed for memory efficiency)
    private byte state; // 0=idle, 1=running, 2=complete
    private static final byte STATE_IDLE = 0;
    private static final byte STATE_RUNNING = 1;
    private static final byte STATE_COMPLETE = 2;
    
    // Easing (single object reference)
    private EaseFunction easeFunction;
    
    // Callbacks (primitive functional interfaces)
    private FloatConsumer onUpdate;
    private Runnable onComplete;
    private Runnable onStart;
    
    // Pooled flag
    private boolean pooled = true;
    
    TweenOpt() {
        this.easeFunction = Ease.LINEAR;
        reset();
    }
    
    /**
     * Sets start and end values.
     */
    public TweenOpt fromTo(float start, float end) {
        this.startValue = start;
        this.endValue = end;
        return this;
    }
    
    /**
     * Sets duration in milliseconds.
     */
    public TweenOpt duration(long durationMs) {
        this.durationMs = Math.max(0, durationMs);
        return this;
    }
    
    /**
     * Sets easing function.
     */
    public TweenOpt ease(EaseFunction ease) {
        this.easeFunction = ease != null ? ease : Ease.LINEAR;
        return this;
    }
    
    /**
     * Sets update callback with primitive consumer (no boxing).
     */
    public TweenOpt onUpdate(FloatConsumer callback) {
        this.onUpdate = callback;
        return this;
    }
    
    /**
     * Sets complete callback.
     */
    public TweenOpt onComplete(Runnable callback) {
        this.onComplete = callback;
        return this;
    }
    
    /**
     * Sets start callback.
     */
    public TweenOpt onStart(Runnable callback) {
        this.onStart = callback;
        return this;
    }
    
    /**
     * Starts the tween. Triggers onStart if set.
     */
    public TweenOpt start() {
        this.startTime = System.currentTimeMillis();
        this.state = STATE_RUNNING;
        
        if (onStart != null) {
            onStart.run();
        }
        
        update(); // Immediate first update
        return this;
    }
    
    /**
     * Stops the tween immediately.
     */
    public TweenOpt stop() {
        state = STATE_IDLE;
        return this;
    }
    
    /**
     * Resets tween to initial state and returns to pool if pooled.
     */
    public TweenOpt reset() {
        state = STATE_IDLE;
        startValue = 0;
        endValue = 0;
        currentValue = 0;
        durationMs = 0;
        easeFunction = Ease.LINEAR;
        onUpdate = null;
        onComplete = null;
        onStart = null;
        return this;
    }
    
    /**
     * Updates tween state. Call regularly from game loop or ticker.
     * 
     * @return true if still running, false if complete or stopped
     */
    public boolean update() {
        if (state != STATE_RUNNING) {
            return false;
        }
        
        long elapsed = System.currentTimeMillis() - startTime;
        float t = Interpolation.clamp01((float) elapsed / durationMs);
        float easedT = easeFunction.apply(t);
        currentValue = Interpolation.lerp(startValue, endValue, easedT);
        
        // Invoke callback without boxing
        if (onUpdate != null) {
            onUpdate.accept(currentValue);
        }
        
        if (t >= 1.0f) {
            state = STATE_COMPLETE;
            
            if (onComplete != null) {
                onComplete.run();
            }
            
            // Auto-return to pool
            if (pooled) {
                TweenPool.release(this);
            }
            
            return false;
        }
        
        return true;
    }
    
    /**
     * Gets current value without updating.
     */
    public float currentValue() {
        if (state == STATE_IDLE) {
            return startValue;
        }
        
        long elapsed = System.currentTimeMillis() - startTime;
        float t = Interpolation.clamp01((float) elapsed / durationMs);
        float easedT = easeFunction.apply(t);
        return Interpolation.lerp(startValue, endValue, easedT);
    }
    
    /**
     * Checks if running.
     */
    public boolean isRunning() {
        return state == STATE_RUNNING;
    }
    
    /**
     * Checks if complete.
     */
    public boolean isComplete() {
        return state == STATE_COMPLETE;
    }
    
    /**
     * Checks if idle.
     */
    public boolean isIdle() {
        return state == STATE_IDLE;
    }
    
    /**
     * Gets duration.
     */
    public long getDuration() {
        return durationMs;
    }
    
    /**
     * Sets whether this tween should return to pool on complete.
     */
    public TweenOpt setPooled(boolean pooled) {
        this.pooled = pooled;
        return this;
    }
}
