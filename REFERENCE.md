# FastTween Reference & Vocabulary

## 1. Core Vocabulary

*   **Tween (`FastTweenOpt`)**: The core mathematical container representing an interpolation over time. It holds the start value, end value, duration, and the current progress.
*   **Easing (`Ease` Enum)**: The mathematical curve applied to the interpolation. It defines how the value accelerates and decelerates (e.g., Linear, Quad, Elastic, Bounce).
*   **TweenPool**: The internal pre-allocated object pool. When `FastTween.to()` is called, an idle tween is fetched from this pool rather than allocating a new object on the JVM heap.
*   **Zero-Allocation Hotpath**: The critical code path executed during the rendering loop where zero objects are instantiated, completely bypassing the Garbage Collector.

## 2. API Quick Reference

### `FastTween` (Main Factory)
*   `FastTween.to(float start, float end, long durationMs)`: Fetches a pooled tween and configures it to interpolate between the given values over the specified duration.

### `Tween` (The Interpolator)
*   `ease(Ease easing)`: Applies a specific easing curve to the interpolation (e.g., `Ease.CUBIC_IN_OUT`). Default is `LINEAR`.
*   `onUpdate(Consumer<Float> callback)`: Attaches a lambda callback that is triggered every time the tween's value changes.
*   `onComplete(Runnable callback)`: Attaches a callback triggered exactly once when the tween finishes.
*   `start()`: Marks the tween as active and records the start time.
*   `update()`: The core tick method. Calculates the delta time, applies the easing math, fires the `onUpdate` callback, and returns `true` if still running or `false` if completed. Once completed, it automatically recycles itself back to the `TweenPool`.

## 3. Supported Easing Functions
*   `LINEAR`
*   `QUAD_IN`, `QUAD_OUT`, `QUAD_IN_OUT`
*   `CUBIC_IN`, `CUBIC_OUT`, `CUBIC_IN_OUT`
*   `QUART_IN`, `QUART_OUT`, `QUART_IN_OUT`
*   `QUINT_IN`, `QUINT_OUT`, `QUINT_IN_OUT`
*   `SINE_IN`, `SINE_OUT`, `SINE_IN_OUT`
*   `EXPO_IN`, `EXPO_OUT`, `EXPO_IN_OUT`
*   `CIRC_IN`, `CIRC_OUT`, `CIRC_IN_OUT`
*   `BACK_IN`, `BACK_OUT`, `BACK_IN_OUT`
*   `ELASTIC_IN`, `ELASTIC_OUT`, `ELASTIC_IN_OUT`
*   `BOUNCE_IN`, `BOUNCE_OUT`, `BOUNCE_IN_OUT`

---
**Part of the FastJava Ecosystem** — *Making the JVM faster.*

Made with ❤️ by Andre Stubbe