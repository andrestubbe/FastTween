# Changelog

## [0.1.1] - 2026-08-21

### Added
- **Deterministic Stepping**: Added `Tween.update(float deltaMs)` enabling exact, frame-accurate offline value interpolation without querying system clock time.
- **Enhanced Reusability**: Optimized `reset()` and `start()` lifecycle for deterministic time loops.

---

## [0.1.0] - 2026-04-30

### Added
- Standardized v0.1.0 release for the FastJava ecosystem.
- Pure math foundation for interpolation (Float, Color, Vector).
- Simplified API: Removed global ticker to maintain zero-overhead principles.
- Full Blueprint alignment (POM, Batch, Readme).
