# The Philosophy of FastTween

> [!IMPORTANT]
> **"Zero Objects. Zero Garbage. Pure Mathematical Precision."**

FastTween is built on the principle that the JVM is an incredibly fast computational engine, but memory allocation during the rendering loop destroys visual fluidity.

## Core Tenets

1.  **Zero-Allocation Hotpath**
    In games or high-performance UIs, allocating new `Tween` objects 120 times per second triggers inevitable Garbage Collection (GC) pauses. FastTween solves this by aggressively utilizing Object Pooling (`TweenPool`). When you animate, no memory is dynamically allocated.

2.  **Primitive-First Execution**
    Autoboxing primitives (e.g., converting `float` to `Float`) is a silent performance killer in Java. FastTween's core engine relies exclusively on raw primitive arrays and primitive data types to ensure peak CPU cache utilization.

3.  **Agnostic Independence**
    FastTween is deliberately decoupled from any rendering framework. It doesn't care if you are using Swing, JavaFX, LibGDX, or a headless server. It only processes mathematical time-deltas.

4.  **Blueprint Consistency**
    As part of the **FastJava** ecosystem, FastTween adheres to a standardized architecture:
    *   **Stand-Alone Math Engine**: Independent and modular.
    *   **Unified Ecosystem**: Designed to be orchestrated by `FastAnimation`.
    *   **Premium Quality**: Built for zero-latency UI transitions and smooth graphics.

---
**❤️ FastTween — Powering the next generation of Native Java.**
