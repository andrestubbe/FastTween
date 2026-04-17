package fasttween;

/**
 * Functional interface for custom easing functions.
 * 
 * <p>Allows users to define custom easing curves via lambda expressions:
 * <pre>
 * FastTween.to(0f, 100f, 300)
 *     .ease(t -> t * t * (3 - 2 * t))  // smoothstep
 *     .start();
 * </pre>
 * 
 * <p>The input t ranges from 0.0 (start) to 1.0 (end).
 * The output should also typically range from 0.0 to 1.0,
 * though values outside this range are valid for effects
 * like overshoot (Back) or bounce.
 * 
 * @author FastJava Team
 * @version 1.0.0
 */
@FunctionalInterface
public interface EaseFunction {
    
    /**
     * Applies the easing function to the input value.
     * 
     * @param t Input value in range [0.0, 1.0]
     * @return Eased value, typically in range [0.0, 1.0]
     */
    float apply(float t);
}
