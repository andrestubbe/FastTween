package fasttween;

/**
 * Built-in easing functions for FastTween.
 * 
 * <p>Curated selection of 8 essential easing functions covering
 * 99% of animation needs:
 * <ul>
 *   <li><b>Linear</b> - Constant velocity for technical movements</li>
 *   <li><b>Quad</b> - Soft, predictable standard ease for UI</li>
 *   <li><b>Cubic</b> - Smoother, premium-feeling ease</li>
 *   <li><b>Quart</b> - Snappy, fast movements</li>
 *   <li><b>Back</b> - Overshoot for UI bounce without physics</li>
 *   <li><b>Elastic</b> - Rubber effect for showcase demos</li>
 *   <li><b>Bounce</b> - Physical ground bounce for playful UI</li>
 *   <li><b>Expo</b> - Extreme fast start, gentle landing</li>
 * </ul>
 * 
 * @author FastJava Team
 * @version 1.0.0
 */
public enum Ease implements EaseFunction {
    
    /** Constant velocity. No acceleration. */
    LINEAR(t -> t),
    
    /** Accelerating from zero velocity. */
    QUAD_IN(t -> t * t),
    
    /** Decelerating to zero velocity. */
    QUAD_OUT(t -> {
        return 1 - (1 - t) * (1 - t);
    }),
    
    /** Acceleration until halfway, then deceleration. */
    QUAD_IN_OUT(t -> {
        return t < 0.5f ? 2 * t * t : 1 - (float)Math.pow(-2 * t + 2, 2) / 2;
    }),
    
    /** Accelerating from zero velocity (smoother). */
    CUBIC_IN(t -> t * t * t),
    
    /** Decelerating to zero velocity (smoother). */
    CUBIC_OUT(t -> {
        return 1 - (float)Math.pow(1 - t, 3);
    }),
    
    /** Acceleration until halfway, then deceleration (smoother). */
    CUBIC_IN_OUT(t -> {
        return t < 0.5f ? 4 * t * t * t : 1 - (float)Math.pow(-2 * t + 2, 3) / 2;
    }),
    
    /** Fast deceleration for snappy movements. */
    QUART_OUT(t -> {
        return 1 - (float)Math.pow(1 - t, 4);
    }),
    
    /** Overshoots slightly then settles back. */
    BACK_OUT(t -> {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        return 1 + c3 * (float)Math.pow(t - 1, 3) + c1 * (float)Math.pow(t - 1, 2);
    }),
    
    /** Elastic snap effect. */
    ELASTIC_OUT(t -> {
        if (t == 0) return 0;
        if (t == 1) return 1;
        float c4 = (float) ((2 * Math.PI) / 3);
        return (float) (Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75f) * c4) + 1);
    }),
    
    /** Bouncing effect like a ball. */
    BOUNCE_OUT(t -> {
        float n1 = 7.5625f;
        float d1 = 2.75f;
        
        if (t < 1 / d1) {
            return n1 * t * t;
        } else if (t < 2 / d1) {
            return n1 * (t -= 1.5f / d1) * t + 0.75f;
        } else if (t < 2.5 / d1) {
            return n1 * (t -= 2.25f / d1) * t + 0.9375f;
        } else {
            return n1 * (t -= 2.625f / d1) * t + 0.984375f;
        }
    }),
    
    /** Extreme fast start, gentle landing. */
    EXPO_OUT(t -> {
        return t == 1 ? 1 : 1 - (float)Math.pow(2, -10 * t);
    });
    
    private final EaseFunction function;
    
    Ease(EaseFunction function) {
        this.function = function;
    }
    
    @Override
    public float apply(float t) {
        return function.apply(t);
    }
}
