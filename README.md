# FastTween

> **Ultra-fast native Java tweening engine** — Mathematical value interpolation with zero overhead

[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Maven](https://img.shields.io/badge/Maven-3.9+-orange.svg)](https://maven.apache.org)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io)

---

## Quick Start

```java
// Basic tween from 0 to 100 over 300ms
FastTween tween = FastTween.to(0f, 100f, 300)
    .ease(Ease.CUBIC_OUT)
    .onUpdate(value -> System.out.println(value))
    .start();

// Custom easing via lambda
FastTween.to(0f, 1f, 500)
    .ease(t -> t * t * (3 - 2 * t))  // smoothstep
    .start();
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
    <version>v1.0.0</version>
</dependency>
```

### Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:fasttween:v1.0.0'
}
```

---

## Features

- **8 Essential Easing Functions** — Linear, Quad, Cubic, Quart, Back, Elastic, Bounce, Expo
- **Custom Easing Support** — Lambda-based `EaseFunction` interface
- **Type Flexibility** — float, double, int interpolation
- **Zero Dependencies** — Pure Java, no JNI needed
- **Zero Allocation** — Reusable tween instances

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
│   ├── FastTween.java      # Static factory
│   ├── Ease.java           # Built-in easing enum
│   ├── EaseFunction.java   # Functional interface
│   ├── Tween.java          # Tween instance
│   └── Interpolation.java  # Math utilities
├── examples/00-basic-usage/
└── pom.xml
```

---

## License

MIT License — See [LICENSE](LICENSE) for details.

**Part of the FastJava Ecosystem** — *Making the JVM faster.*
