import javax.swing.Timer;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * FastTween - Ultra-fast native Java tweening engine.
 * 
 * <p>Mathematical value interpolation with zero overhead.
 * Automatically managed by a Global Ticker.
 */
public final class FastTween {
    
    public static final String VERSION = "0.1.0";
    
    // The Global Ticker Engine
    private static final int FPS = 60;
    private static final CopyOnWriteArrayList<Tween> ACTIVE_TWEENS = new CopyOnWriteArrayList<>();
    private static final Timer TICKER = new Timer(1000 / FPS, e -> tick());

    private FastTween() {
        // Utility class
    }

    private static void tick() {
        if (ACTIVE_TWEENS.isEmpty()) {
            TICKER.stop();
            return;
        }

        for (Tween tween : ACTIVE_TWEENS) {
            if (!tween.update()) {
                ACTIVE_TWEENS.remove(tween);
            }
        }
    }

    /**
     * Registers a tween with the global ticker engine.
     * @param tween The tween to start tracking
     */
    static void register(Tween tween) {
        if (!ACTIVE_TWEENS.contains(tween)) {
            ACTIVE_TWEENS.add(tween);
            if (!TICKER.isRunning()) {
                TICKER.start();
            }
        }
    }

    /**
     * Unregisters a tween from the global ticker.
     * @param tween The tween to stop tracking
     */
    static void unregister(Tween tween) {
        ACTIVE_TWEENS.remove(tween);
        if (ACTIVE_TWEENS.isEmpty()) {
            TICKER.stop();
        }
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
