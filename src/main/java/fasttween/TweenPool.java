package fasttween;

import java.util.ArrayDeque;

/**
 * Object pool for TweenOpt instances to minimize GC pressure.
 * 
 * <p>Maintains a pool of reusable TweenOpt objects. When a tween completes,
 * it returns itself to the pool instead of being garbage collected.
 * 
 * <p>Thread-safety: This pool uses ThreadLocal to avoid synchronization overhead.
 * Each thread has its own pool, making allocation lock-free.
 * 
 * @author FastJava Team
 * @version 1.0.0
 */
public class TweenPool {
    
    private static final int DEFAULT_CAPACITY = 256;
    private static final int MAX_CAPACITY = 1024;
    
    // ThreadLocal pool - each thread has its own to avoid synchronization
    private static final ThreadLocal<ArrayDeque<TweenOpt>> POOL = 
        ThreadLocal.withInitial(() -> new ArrayDeque<>(DEFAULT_CAPACITY));
    
    // Statistics for monitoring
    private static volatile long acquisitions = 0;
    private static volatile long creations = 0;
    private static volatile long returns = 0;
    
    private TweenPool() {
        // Utility class
    }
    
    /**
     * Acquires a TweenOpt from the pool, creating new if pool is empty.
     * 
     * @return A TweenOpt instance (either recycled or new)
     */
    public static TweenOpt acquire() {
        ArrayDeque<TweenOpt> pool = POOL.get();
        TweenOpt tween = pool.poll();
        
        if (tween == null) {
            tween = new TweenOpt();
            creations++;
        }
        
        acquisitions++;
        tween.reset(); // Ensure clean state
        return tween;
    }
    
    /**
     * Returns a TweenOpt to the pool for reuse.
     * 
     * @param tween The tween to recycle
     */
    public static void release(TweenOpt tween) {
        if (tween == null) return;
        
        ArrayDeque<TweenOpt> pool = POOL.get();
        
        // Don't exceed max capacity to prevent memory bloat
        if (pool.size() < MAX_CAPACITY) {
            pool.offer(tween);
            returns++;
        }
        // If pool is full, let GC collect it
    }
    
    /**
     * Returns statistics about pool usage.
     * 
     * @return Formatted statistics string
     */
    public static String getStats() {
        ArrayDeque<TweenOpt> pool = POOL.get();
        return String.format(
            "Pool[thread=%d, available=%d, acquired=%d, created=%d, returned=%d]",
            Thread.currentThread().threadId(),
            pool.size(),
            acquisitions,
            creations,
            returns
        );
    }
    
    /**
     * Clears the pool for current thread.
     */
    public static void clear() {
        POOL.get().clear();
    }
    
    /**
     * Returns number of available tweens in current thread's pool.
     * 
     * @return Available count
     */
    public static int available() {
        return POOL.get().size();
    }
}
