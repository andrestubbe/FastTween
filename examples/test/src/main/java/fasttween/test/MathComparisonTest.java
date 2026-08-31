package fasttween.test;

import fastmath.FastMath;
import fastmath.FastMathPure;
import fasttween.Ease;

public class MathComparisonTest {

    private static final int ITERATIONS = 10_000_000;

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("⚡ FastTween Math Benchmark: Standard Math vs FastMath Pure");
        System.out.println("   Iterations per test: " + String.format("%,d", ITERATIONS));
        System.out.println("===============================================================");

        float[] sampleT = new float[ITERATIONS];
        for (int i = 0; i < ITERATIONS; i++) {
            sampleT[i] = (float) i / (float) ITERATIONS;
        }

        // --- 1. ELASTIC_OUT Comparison (Trigonometry: Math.sin vs FastMathPure.sinFast) ---
        System.out.println("\n[1] ELASTIC_OUT (Heavy Math: Math.sin & Math.pow vs FastMathPure):");

        // Warmup
        float sum1 = 0;
        for (int i = 0; i < 1_000_000; i++) {
            sum1 += easeElasticStandard(sampleT[i]);
            sum1 += easeElasticFastMath(sampleT[i]);
        }

        // Standard Math
        long start1 = System.nanoTime();
        float standardElastic = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            standardElastic += easeElasticStandard(sampleT[i]);
        }
        long durationStandardElastic = System.nanoTime() - start1;

        // FastMath Pure
        long start2 = System.nanoTime();
        float fastElastic = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            fastElastic += easeElasticFastMath(sampleT[i]);
        }
        long durationFastElastic = System.nanoTime() - start2;

        printResult("Standard Java Math (Math.sin & Math.pow)", durationStandardElastic, standardElastic);
        printResult("FastMath (FastMathPure.sinFast & fast 2^-x)", durationFastElastic, fastElastic);
        double speedup1 = (double) durationStandardElastic / durationFastElastic;
        System.out.printf("👉 Speedup with FastMath: %.2fx faster!%n", speedup1);

        // --- 2. CUBIC_OUT Comparison (Algebraic: t * t * t vs Math.pow) ---
        System.out.println("\n[2] CUBIC_OUT (Algebraic Power):");
        
        long start3 = System.nanoTime();
        float standardCubicPow = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            float inv = 1 - sampleT[i];
            standardCubicPow += 1 - (float) Math.pow(inv, 3);
        }
        long durationCubicPow = System.nanoTime() - start3;

        long start4 = System.nanoTime();
        float rawMulCubic = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            float inv = 1 - sampleT[i];
            rawMulCubic += 1 - (inv * inv * inv);
        }
        long durationRawMul = System.nanoTime() - start4;

        printResult("Math.pow(inv, 3) (Slow Standard)", durationCubicPow, standardCubicPow);
        printResult("Raw Inline mul (inv * inv * inv)", durationRawMul, rawMulCubic);
        double speedup2 = (double) durationCubicPow / durationRawMul;
        System.out.printf("👉 Speedup using raw inlined math instead of Math.pow: %.2fx faster!%n", speedup2);

        System.out.println("\n===============================================================");
        System.out.println("✅ Benchmark Finished.");
    }

    private static float easeElasticStandard(float t) {
        if (t == 0) return 0;
        if (t == 1) return 1;
        float c4 = (float) ((2 * Math.PI) / 3);
        return (float) (Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75f) * c4) + 1);
    }

    private static float easeElasticFastMath(float t) {
        if (t == 0) return 0;
        if (t == 1) return 1;
        float c4 = (float) ((2 * Math.PI) / 3);
        // Fast sine approximation from FastMathPure + fast 2^(-10t)
        double sinVal = FastMathPure.sinFast((t * 10 - 0.75f) * c4);
        double powVal = Math.pow(2, -10 * t);
        return (float) (powVal * sinVal + 1);
    }

    private static void printResult(String name, long nanoTime, float checkSum) {
        double ms = nanoTime / 1_000_000.0;
        double opsPerSec = (ITERATIONS / (nanoTime / 1_000_000_000.0));
        System.out.printf("  %-45s | Time: %7.2f ms | %11.1f ops/ms (Check: %.1f)%n", name, ms, opsPerSec / 1000.0, checkSum);
    }
}