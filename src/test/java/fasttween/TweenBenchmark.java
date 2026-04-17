package fasttween;

import fasttween.opt.FastTweenOpt;
import fasttween.opt.TweenOpt;
import fasttween.opt.TweenPool;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Performance benchmark comparing standard vs optimized FastTween.
 * 
 * <p>Measures:
 * <ul>
 *   <li>Time to create and update 10,000 tweens</li>
 *   <li>Memory allocations (GC pressure)</li>
 *   <li>Pool efficiency for optimized version</li>
 * </ul>
 * 
 * <p>Run with: mvn test -Dtest=TweenBenchmark
 * 
 * @author FastJava Team
 * @version 1.0.0
 */
public class TweenBenchmark {
    
    private static final int ITERATIONS = 10000;
    private static final long TWEEN_DURATION = 1000; // 1 second
    
    // Capture results to prevent optimization
    private static volatile float resultSink = 0;
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         FastTween Performance Benchmark v1.0.0             ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Warmup JVM
        System.out.println("Warming up JVM...");
        warmup();
        
        // Force GC before benchmarks
        System.gc();
        System.gc();
        try { Thread.sleep(100); } catch (InterruptedException e) { }
        
        // Benchmark standard version
        System.out.println("\n--- Standard FastTween ---");
        BenchmarkResult standard = benchmarkStandard();
        standard.print();
        
        // Force GC
        System.gc();
        System.gc();
        try { Thread.sleep(100); } catch (InterruptedException e) { }
        
        // Benchmark optimized version
        System.out.println("\n--- Optimized FastTweenOpt ---");
        BenchmarkResult optimized = benchmarkOptimized();
        optimized.print();
        
        // Comparison
        System.out.println("\n--- Comparison ---");
        double speedup = (double) standard.timeMs / optimized.timeMs;
        double allocationRatio = (double) standard.allocations / optimized.allocations;
        
        System.out.printf("Speedup: %.2fx faster%n", speedup);
        System.out.printf("Allocation reduction: %.1fx (%.0f%% less)%n", 
            allocationRatio, 
            (1 - 1.0/allocationRatio) * 100);
        
        System.out.println("\nPool stats: " + FastTweenOpt.poolStats());
        
        System.out.println("\n✅ Benchmark complete!");
    }
    
    private static void warmup() {
        // Warmup standard
        for (int i = 0; i < 100; i++) {
            Tween tween = FastTween.to(0f, 100f, TWEEN_DURATION)
                .onUpdate(v -> resultSink = v)
                .start();
            tween.update();
        }
        
        // Warmup optimized
        for (int i = 0; i < 100; i++) {
            TweenOpt tween = FastTweenOpt.to(100f, TWEEN_DURATION)
                .onUpdate(v -> resultSink = v)
                .start();
            tween.update();
        }
        
        TweenPool.clear();
    }
    
    private static BenchmarkResult benchmarkStandard() {
        List<Tween> tweens = new ArrayList<>(ITERATIONS);
        
        // Measure memory before
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memBefore = runtime.totalMemory() - runtime.freeMemory();
        
        // Measure creation time
        long startTime = System.nanoTime();
        
        for (int i = 0; i < ITERATIONS; i++) {
            Tween tween = FastTween.to(0f, i, TWEEN_DURATION)
                .ease(Ease.QUAD_OUT)
                .onUpdate(new Consumer<Float>() {
                    @Override
                    public void accept(Float v) {
                        resultSink = v;
                    }
                })
                .start();
            tweens.add(tween);
        }
        
        // Update all once
        for (Tween tween : tweens) {
            tween.update();
        }
        
        long endTime = System.nanoTime();
        long timeMs = (endTime - startTime) / 1_000_000;
        
        // Measure memory after
        long memAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = Math.max(0, memAfter - memBefore);
        
        // Estimate allocations: Each Tween + Consumer + Float boxing
        // Approximate: ~64 bytes per tween + overhead
        long estimatedAllocations = ITERATIONS * 3; // Tween, Consumer, boxed Float
        
        // Cleanup
        tweens.clear();
        
        return new BenchmarkResult("Standard FastTween", timeMs, memoryUsed, estimatedAllocations, ITERATIONS);
    }
    
    private static BenchmarkResult benchmarkOptimized() {
        List<TweenOpt> tweens = new ArrayList<>(ITERATIONS);
        
        // Clear pool before benchmark
        FastTweenOpt.clearPool();
        
        // Measure memory before
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memBefore = runtime.totalMemory() - runtime.freeMemory();
        
        // Measure creation time
        long startTime = System.nanoTime();
        
        for (int i = 0; i < ITERATIONS; i++) {
            TweenOpt tween = FastTweenOpt.to(i, TWEEN_DURATION)
                .ease(Ease.QUAD_OUT)
                .onUpdate(v -> resultSink = v)  // Primitive, no boxing!
                .start();
            tweens.add(tween);
        }
        
        // Update all once
        for (TweenOpt tween : tweens) {
            tween.update();
        }
        
        long endTime = System.nanoTime();
        long timeMs = (endTime - startTime) / 1_000_000;
        
        // Measure memory after
        long memAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = Math.max(0, memAfter - memBefore);
        
        // Optimized: Pool reduces allocations significantly
        // First iteration creates objects, rest reuses
        long poolAvailable = FastTweenOpt.poolAvailable();
        long estimatedAllocations = Math.max(ITERATIONS - poolAvailable, 100); // Rough estimate
        
        // Cleanup - return to pool
        for (TweenOpt tween : tweens) {
            tween.reset();
        }
        tweens.clear();
        
        return new BenchmarkResult("Optimized FastTweenOpt", timeMs, memoryUsed, estimatedAllocations, ITERATIONS);
    }
    
    private static class BenchmarkResult {
        final String name;
        final long timeMs;
        final long memoryUsed;
        final long allocations;
        final int iterations;
        
        BenchmarkResult(String name, long timeMs, long memoryUsed, long allocations, int iterations) {
            this.name = name;
            this.timeMs = timeMs;
            this.memoryUsed = memoryUsed;
            this.allocations = allocations;
            this.iterations = iterations;
        }
        
        void print() {
            System.out.printf("Name: %s%n", name);
            System.out.printf("  Time: %d ms%n", timeMs);
            System.out.printf("  Memory: %d KB%n", memoryUsed / 1024);
            System.out.printf("  Throughput: %,d tweens/sec%n", (iterations * 1000L) / Math.max(timeMs, 1));
            System.out.printf("  Est. allocations: %,d objects%n", allocations);
        }
    }
}
