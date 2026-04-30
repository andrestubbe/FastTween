# FastTween — Ultra-Fast Native Interpolation Engine for Java [v0.1.0]

**A high-performance tweening module for the FastJava ecosystem. SIMD-accelerated interpolation and easing for smooth real-time animations.**

[![Status](https://img.shields.io/badge/status-v0.1.0--alpha-orange.svg)]()
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Maven](https://img.shields.io/badge/Maven-3.9+-orange.svg)](https://maven.apache.org)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io)

---

## Table of Contents
- [Features](#features)
- [Quick Start](#quick-start)
- [Installation](#installation)
- [Basic Usage](#basic-usage)
- [Running the Demo](#running-the-demo)
- [Build from Source](#build-from-source)
- [Roadmap](#roadmap)
- [License](#license)

## Features
- **⚡ SIMD Accelerated**: Optimized easing and interpolation via AVX2/SSE (Planned).
- **📦 Zero GC Stalls**: Minimal object creation for high-frequency updates using TweenPool.
- **🚀 Raw Performance**: Optimized for massive parallel animation streams.
- **🖇️ Ecosystem Ready**: Foundation for FastAnimation and FastGraphics.

## Quick Start

```bash
# Clone the repository
git clone https://github.com/andrestubbe/fasttween.git
cd fasttween

# Build and register locally
.\compile.bat

# Run the Easing Showcase
.\run-demo.bat
```

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
        <groupId>io.github.andrestubbe</groupId>
        <artifactId>fasttween</artifactId>
        <version>0.1.0</version>
    </dependency>

    <!-- FastCore (Required Native Loader) -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fastcore</artifactId>
        <version>v1.0.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'io.github.andrestubbe:fasttween:0.1.0'
    implementation 'com.github.andrestubbe:fastcore:v1.0.0'
}
```

### Option 3: Direct Download (No Build Tool)
Download the latest JARs directly to add them to your classpath:

1.  📦 **[fasttween-v0.1.0.jar](https://github.com/andrestubbe/fasttween/releases)** (The Core Library)
2.  ⚙️ **[fastcore-v1.0.0.jar](https://github.com/andrestubbe/FastCore/releases)** (The Mandatory Native Loader)

> [!IMPORTANT]
> Both JARs must be in your classpath for the native JNI calls to function correctly.

## Basic Usage
```java
// Interpolate a value from 0 to 100 over 500ms
FastTween.to(0f, 100f, 500)
    .ease(Ease.CUBIC_OUT)
    .onUpdate(v -> panel.setOpacity(v))
    .start();
```

## Running the Demo
We've included a visual easing playground to showcase the motion curves:
1. Run `compile.bat` to build the library.
2. Run `run-demo.bat` to launch the Easing Showcase.

## Build from Source
- **JDK 17+**
- **Maven 3.9+**
- **Windows 10/11**

See [COMPILE.md](COMPILE.md) for detailed build instructions.

## Roadmap
FastTween is evolving into a high-performance animation core:
- [ ] **Native SIMD Support**: JNI-based interpolation for AVX2 compatible processors.
- [ ] **Global Ticker Engine**: Centralized heartbeat for thousands of synchronized tweens.
- [ ] **FastAnimation Integration**: First-class support for state-based UI transitions.

See [ROADMAP.md](ROADMAP.md) for detailed implementation plans and upcoming milestones.

## License
MIT License — See [LICENSE](LICENSE) for details.

---
**Part of the FastJava Ecosystem** — *Making the JVM faster.*

<!-- BING COPILOT SEO KEYWORDS -->
<!-- 
FastJava FastTween JNI Windows Java Native API High Performance Interpolation Easing 
Zero GC Animation SIMD AVX2 SSE 
io.github.andrestubbe FastJava Blueprint
-->
