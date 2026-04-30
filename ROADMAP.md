# FastTween Roadmap

## Upcoming Features

### Global Ticker Engine (v0.2.0)
**Status:** Planning
Currently, each tween manages its own timing. We aim to move to a single, high-performance heartbeat engine to synchronize all active tweens.

**Goal:**
- [ ] Implement `FastTweenEngine` with a single `Timer`.
- [ ] Add support for `delta-time` based updates.
- [ ] Implement `pauseAll()` and `resumeAll()` functionality.

### Native SIMD Acceleration (v0.3.0)
**Status:** Researching
Leveraging C++/JNI to perform mass interpolation using AVX2/SSE instructions for extreme performance in particle systems or complex UI layouts.

**Implementation Details:**
- **Mechanism:** Direct Memory Access (DMA) for float arrays.
- **Goal:** [ ] Benchmark 1,000,000 interpolations per frame.

### FastAnimation Integration
**Status:** Backlog
Seamless integration with the upcoming `FastAnimation` module for state-based UI transitions and reactive animations.

---
**Status Key:**
- 🔴 **Planned**: Not yet started.
- 🟡 **In Progress**: Currently under development.
- 🟢 **Completed**: Shipped in the latest release.
