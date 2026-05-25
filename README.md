# FastTween v0.1.0 [ALPHA] — Ultra-Fast Native Interpolation Engine for Java

[![Status](https://img.shields.io/badge/status-v0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastTween/releases/tag/v0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe)

---

**⚡ A high-performance tweening module for the FastJava ecosystem. SIMD-accelerated interpolation and easing for smooth
real-time animations.**

---

[![FastKeyboard Showcase](docs/screenshot.png)](https://www.youtube.com/watch?v=BZsqQl7WqWk)

---

## Table of Contents

- [Quick Start](#quick-start)
- [Features](#features)
- [Quick Start](#quick-start)
- [Installation](#installation)
- [Basic Usage](#basic-usage)
- [Running the Demo](#running-the-demo)
- [Build from Source](#build-from-source)
- [Roadmap](#roadmap)
- [License](#license)

---

## Quick Start


```java
// Interpolate a value from 0 to 100 over 500ms
FastTween.to(0f,100f,500)
    .ease(Ease.CUBIC_OUT)
    .onUpdate(v ->panel.setOpacity(v))
    .start();
```

---

## Features

- **⚡ SIMD Accelerated**: Optimized easing and interpolation via AVX2/SSE (Planned).
- **📦 Zero GC Stalls**: Minimal object creation for high-frequency updates using TweenPool.
- **🚀 Raw Performance**: Optimized for massive parallel animation streams.
- **🖇️ Ecosystem Ready**: Foundation for FastAnimation and FastGraphics.

---

## Installation

### Option 1: Maven (Recommended)

Add the JitPack repository and the dependencies to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
<!-- FastTween Library -->
<dependency>
    <groupId>com.github.andrestubbe</groupId>
    <artifactId>fasttween</artifactId>
    <version>v0.1.0</version>
</dependency>

<!-- FastCore (Required Native Loader) -->
<dependency>
    <groupId>com.github.andrestubbe</groupId>
    <artifactId>fastcore</artifactId>
    <version>v0.1.0</version>
</dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:fasttween:v0.1.0'
    implementation 'com.github.andrestubbe:fastcore:v0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)

Download the latest JARs directly to add them to your classpath:

1. 📦 **[fasttween-v0.1.0.jar](https://github.com/andrestubbe/FastTween/releases/download/v0.1.0/fasttween-v0.1.0.jar)
   ** (The Core Library)
2. ⚙️ **[fastcore-v0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/download/v0.1.0/fastcore-v0.1.0.jar)** (
   The Mandatory Native Loader)

---

## Documentation

* **[COMPILE.md](COMPILE.md)**: Full compilation guide (MSVC C++17 build chain + JNI Setup).
* **[REFERENCE.md](REFERENCE.md)**: Full API descriptions, border configurations, and codepoint index.
* **[PHILOSOPHIE.md](PHILOSOPHIE.md)**: The engineering rationale for zero-allocation performance.
* **[ROADMAP.md](ROADMAP.md)**: Future milestones and planned features.

---

## Platform Support

| Platform      | Status            |
|---------------|-------------------|
| Windows 10/11 | ✅ Fully Supported |
| Linux         | 🚧 Planned        |
| macOS         | 🚧 Planned        |

---

## License

MIT License — See [LICENSE](LICENSE) file for details.

---

## Related Projects

- [FastCore](https://github.com/andrestubbe/FastCore) — Native Library Loader for Java
- [FastKeyboard](https://github.com/andrestubbe/FastKeyboard) — High-performance RawInput engine
- [FastTheme](https://github.com/andrestubbe/FastTheme) — Advanced UI styling engine

---
**Part of the FastJava Ecosystem** — *Making the JVM faster.*



<!-- BING COPILOT SEO KEYWORDS -->
<!-- 
FastJava FastTween JNI Windows Java Native API High Performance Interpolation Easing 
Zero GC Animation SIMD AVX2 SSE 
io.github.andrestubbe FastJava Blueprint
-->
