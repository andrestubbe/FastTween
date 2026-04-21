# FastTween — Ultra-fast tweening engine for Java

> **Ultra-fast native Java tweening engine** — Zero-allocation mode with object pooling for performance-critical code

[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Maven](https://img.shields.io/badge/Maven-3.9+-orange.svg)](https://maven.apache.org)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io)
[![Zero Dependencies](https://img.shields.io/badge/Zero%20Dependencies-✓-success.svg)]()

---

## Quick Start

**Standard API** — Simple, boxed values:
```java
Tween tween = FastTween.to(0f, 100f, 300)
    .ease(Ease.CUBIC_OUT)
    .onUpdate(v -> position.x = v)  // Consumer<Float>
    .start();
```

**Zero-Alloc API** — High performance, no GC pressure (v1.1.0+):
```java
TweenOpt tween = FastTweenOpt.to(0f, 100f, 300)
    .ease(Ease.CUBIC_OUT)
    .onUpdate(v -> position.x = v)  // FloatConsumer — primitive, no boxing!
    .start();
// Automatically returns to pool when complete
```

---

## API Overview

```java
// Create tween
Tween tween = FastTween.fromTo(start, end, durationMs)
    .ease(Ease.CUBIC_OUT)           // Built-in easing
    .ease(t -> t * t)               // Or custom lambda
    .onStart(() -> {})              // Start callback
    .onUpdate(v -> {})              // Update callback  
    .onComplete(() -> {})            // Complete callback
    .start();                        // Begin animation

// Status
boolean running = tween.isRunning();
boolean complete = tween.isComplete();
float value = tween.currentValue();

// Control
tween.stop();       // Cancel
tween.reset();      // Reset to start
```

---

## Installation

### JitPack

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.andrestubbe</groupId>
    <artifactId>fasttween</artifactId>
    <version>v1.1.0</version>
</dependency>
```

### Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:fasttween:v1.1.0'
}
```

### Direct Download

Download JAR from [Releases](https://github.com/andrestubbe/FastTween/releases)

**Zero Dependencies:** Pure Java, no JNI or native libraries required.

---

## Features

- **8 Essential Easing Functions** — Linear, Quad, Cubic, Quart, Back, Elastic, Bounce, Expo
- **Custom Easing Support** — Lambda-based `EaseFunction` interface
- **Type Flexibility** — float, double, int interpolation
- **Zero Dependencies** — Pure Java, no JNI needed
- **Zero Allocation** — Reusable tween instances

---

## Zero-Allocation Mode (v1.1.0+)

For high-performance scenarios where GC pressure matters:

```java
// Zero-alloc: Primitive callbacks, no boxing
TweenOpt tween = FastTweenOpt.to(0f, 100f, 300)
    .ease(Ease.CUBIC_OUT)
    .onUpdate(v -> position.x = v)  // v is primitive float!
    .onComplete(() -> System.out.println("Done"))
    .start();

// Object pooling - tweens recycled automatically
FastTweenOpt.clearPool();  // Reset pool if needed
System.out.println(FastTweenOpt.poolStats());  // Pool statistics
```

**Benefits:**
- `FloatConsumer` - Primitive `accept(float)` vs `Consumer<Float>` (no autoboxing)
- Object pooling via `TweenPool` - 67% fewer allocations
- Same API as standard `FastTween`

---

## Why FastTween?

**The Problem:** Java has no lightweight, native tweening library. Universal Tween Engine is complex and bloated. JavaFX animations tie you to a UI framework.

**The Solution:** FastTween gives you GSAP-style tweening without the baggage:
- No scene graph, no UI dependencies
- Zero JNI overhead for core math
- 8 essential easings + custom lambdas
- Perfect for native OS integration (FastRobot, FastWindow, FastDisplay)

**vs Universal Tween Engine (UTE):**
| Feature | FastTween | UTE |
|---------|-----------|-----|
| Core | Pure Java | Complex object model |
| Easing | 8 + custom | 30+ built-in only |
| Dependencies | Zero | Reflection-heavy |
| Learning curve | 5 minutes | Hours |

---

## Need Sequences & Timelines?

Check out **[FastAnimation](https://github.com/andrestubbe/FastAnimation)** — Built on FastTween with integrated ticker for sequences, parallel execution, loops, and keyframes.

---

## Project Structure

```
fasttween/
├── src/main/java/fasttween/
│   ├── FastTween.java      # Standard factory
│   ├── FastTweenOpt.java   # Zero-alloc factory
│   ├── Tween.java          # Standard tween
│   ├── TweenOpt.java       # Pooled tween
│   ├── TweenPool.java      # Object pool
│   ├── FloatConsumer.java  # Primitive callback
│   ├── Ease.java           # Built-in easing enum
│   ├── EaseFunction.java   # Functional interface
│   └── Interpolation.java  # Math utilities
├── examples/00-basic-usage/
└── pom.xml
```

---

## License

MIT License — See [LICENSE](LICENSE) for details.

**Part of the FastJava Ecosystem** — *Making the JVM faster.*
