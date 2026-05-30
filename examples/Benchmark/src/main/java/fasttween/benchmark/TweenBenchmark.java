package fasttween.benchmark;

import fasttween.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
@State(Scope.Thread)
public class TweenBenchmark {

    private Tween tween;
    private TweenOpt tweenOpt;

    @Setup(Level.Iteration)
    public void setup() {
        tween = FastTween
                .to(0f, 100f, 1000)
                .ease(Ease.CUBIC_OUT)
                .onUpdate(v -> {});

        tweenOpt = FastTweenOpt
                .to(100f, 1000)
                .ease(Ease.CUBIC_OUT)
                .onUpdate(v -> {});
    }

    @Benchmark
    public Tween createTweenStandard() {
        return FastTween
                .to(0f, 100f, 1000)
                .ease(Ease.CUBIC_OUT)
                .onUpdate(v -> {});
    }

    @Benchmark
    public TweenOpt createTweenOpt() {
        return FastTweenOpt
                .to(100f, 1000)
                .ease(Ease.CUBIC_OUT)
                .onUpdate(v -> {});
    }

    @Benchmark
    public void updateTweenStandard(Blackhole bh) {
        tween.start();
        for (int i = 0; i < 1000; i++) {
            bh.consume(tween.update());
        }
    }

    @Benchmark
    public void updateTweenOpt(Blackhole bh) {
        tweenOpt.start();
        for (int i = 0; i < 1000; i++) {
            bh.consume(tweenOpt.update());
        }
    }

    @Benchmark
    public float rawInterpolationLerp() {
        return Interpolation.lerp(0f, 100f, 0.5f);
    }

    @Benchmark
    public float rawEasingCubicOut() {
        return Ease.CUBIC_OUT.apply(0.5f);
    }
}
