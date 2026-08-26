package fasttween.benchmark;

import fasttween.Ease;
import fasttween.FastTween;
import fasttween.Interpolation;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class Benchmark {

    private float progress;

    @Setup
    public void setup() {
        progress = 0.5f;
    }

    @org.openjdk.jmh.annotations.Benchmark
    public float benchmarkEaseQuadInOut() {
        return Ease.QUAD_IN_OUT.apply(progress);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public float benchmarkEaseCubicInOut() {
        return Ease.CUBIC_IN_OUT.apply(progress);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public float benchmarkEaseBounceOut() {
        return Ease.BOUNCE_OUT.apply(progress);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public float benchmarkInterpolationLerp() {
        return Interpolation.lerp(10.0f, 100.0f, progress);
    }
}
