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
