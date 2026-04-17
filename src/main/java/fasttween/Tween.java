package fasttween;

import java.util.function.Consumer;

/**
 * A single tween instance that interpolates a value over time.
 * 
 * <p>Tween objects are reusable. Call {@link #reset()} to restart
 * a tween with the same parameters, or adjust parameters and call
 * {@link #start()} again.
 * 
 * <p>Use {@link FastTween} factory methods to create instances:
 * <pre>
 * Tween tween = FastTween.to(0f, 100f, 300).ease(Ease.CUBIC_OUT).start();
 * </pre>
 * 
 * @author FastJava Team
 * @version 1.0.0
 */
public class Tween {
    
    private float startValue;
    private float endValue;
    private long durationMs;
    private EaseFunction easeFunction;
    private Consumer<Float> onUpdate;
    private Runnable onComplete;
    
    private long startTime;
    private boolean isRunning;
    private boolean isComplete;
    
    Tween() {
        this.easeFunction = Ease.LINEAR;
    }
    
    /**
     * Sets the start and end values for this tween.
     * 
     * @param start Starting value
     * @param end Ending value
     * @return This tween for chaining
     */
    public Tween fromTo(float start, float end) {
        this.startValue = start;
        this.endValue = end;
        return this;
    }
    
    /**
     * Sets the duration of this tween.
     * 
     * @param durationMs Duration in milliseconds
     * @return This tween for chaining
     */
    public Tween duration(long durationMs) {
        this.durationMs = Math.max(0, durationMs);
        return this;
    }
    
    /**
     * Sets the easing function for this tween.
     * 
     * @param ease Easing function
     * @return This tween for chaining
     */
    public Tween ease(EaseFunction ease) {
        this.easeFunction = ease != null ? ease : Ease.LINEAR;
        return this;
    }
    
    /**
     * Sets a callback to be invoked on each update.
     * 
     * @param callback Consumer receiving the current value
     * @return This tween for chaining
     */
    public Tween onUpdate(Consumer<Float> callback) {
        this.onUpdate = callback;
        return this;
    }
    
    /**
     * Sets a callback to be invoked when the tween completes.
     * 
     * @param callback Runnable invoked on completion
     * @return This tween for chaining
     */
    public Tween onComplete(Runnable callback) {
        this.onComplete = callback;
        return this;
    }
    
    /**
     * Starts or restarts this tween from the beginning.
     * 
     * @return This tween for chaining
     */
    public Tween start() {
        this.startTime = System.currentTimeMillis();
        this.isRunning = true;
        this.isComplete = false;
        update();
        return this;
    }
    
    /**
     * Stops this tween immediately.
     * 
     * @return This tween for chaining
     */
    public Tween stop() {
        this.isRunning = false;
        return this;
    }
    
    /**
     * Resets this tween to its initial state without starting it.
     * 
     * @return This tween for chaining
     */
    public Tween reset() {
        this.isRunning = false;
        this.isComplete = false;
        return this;
    }
    
    /**
     * Updates this tween. Should be called regularly (e.g., each frame).
     * 
     * @return true if the tween is still running, false if complete
     */
    public boolean update() {
        if (!isRunning) {
            return false;
        }
        
        long elapsed = System.currentTimeMillis() - startTime;
        float t = Interpolation.clamp01((float) elapsed / durationMs);
        float easedT = easeFunction.apply(t);
        float currentValue = Interpolation.lerp(startValue, endValue, easedT);
        
        if (onUpdate != null) {
            onUpdate.accept(currentValue);
        }
        
        if (t >= 1.0f) {
            isRunning = false;
            isComplete = true;
            if (onComplete != null) {
                onComplete.run();
            }
            return false;
        }
        
        return true;
    }
    
    /**
     * Returns the current interpolated value without updating.
     * 
     * @return Current value
     */
    public float currentValue() {
        if (!isRunning && !isComplete) {
            return startValue;
        }
        
        long elapsed = System.currentTimeMillis() - startTime;
        float t = Interpolation.clamp01((float) elapsed / durationMs);
        float easedT = easeFunction.apply(t);
        return Interpolation.lerp(startValue, endValue, easedT);
    }
    
    /**
     * Checks if this tween is currently running.
     * 
     * @return true if running
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * Checks if this tween has completed.
     * 
     * @return true if complete
     */
    public boolean isComplete() {
        return isComplete;
    }
    
    /**
     * Returns the start value.
     * 
     * @return Start value
     */
    public float getStartValue() {
        return startValue;
    }
    
    /**
     * Returns the end value.
     * 
     * @return End value
     */
    public float getEndValue() {
        return endValue;
    }
    
    /**
     * Returns the duration in milliseconds.
     * 
     * @return Duration
     */
    public long getDuration() {
        return durationMs;
    }
}
