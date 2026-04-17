package fasttween.opt;

/**
 * Primitive consumer for float values to avoid autoboxing.
 * 
 * <p>This interface is equivalent to {@link java.util.function.Consumer}&lt;Float&gt;
 * but uses primitive {@code float} instead of boxed {@link Float}, eliminating
 * memory allocation from autoboxing.
 * 
 * <p>Use this in hot paths where GC pressure matters:
 * <pre>
 * tween.onUpdate(v -> {
 *     // v is primitive float, no Float object created
 *     position.x = v;
 * });
 * </pre>
 * 
 * @author FastJava Team
 * @version 1.0.0
 */
@FunctionalInterface
public interface FloatConsumer {
    
    /**
     * Performs this operation on the given value.
     * 
     * @param value the input value (primitive, no boxing)
     */
    void accept(float value);
}
