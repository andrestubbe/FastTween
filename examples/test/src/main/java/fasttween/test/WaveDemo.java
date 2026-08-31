package fasttween.test;

import fastdwm.FastDWM;
import fasttheme.FastTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

/**
 * ⚡ FastTween + FastMath: 3D Holographic Kinetic Wave Live Benchmark (102,400 Nodes @ 120 FPS).
 * 
 * Features:
 * - Proper 3D Z-Buffer (fixes backface through-rendering / sorting artifacts).
 * - Chunky Glow Quads (2x2 to 5x5 pixels) for rich, solid volume and zero eye strain.
 * - Real-time continuous benchmark switching (Standard Math vs FastMath Pure).
 * - Interactive Controls: [M/TAB] Engine Toggle, [1/2/3] Wave Shape, [SPACE] Shockwave.
 */
public class WaveDemo extends Canvas {

    private static final int WIDTH = 1173;
    private static final int HEIGHT = 610;
    private static final int PIXEL_COUNT = WIDTH * HEIGHT;
    private static final int GRID_SIZE = 320;
    private static final int NODE_COUNT = GRID_SIZE * GRID_SIZE; // 102,400 Nodes
    private static final int FRAMES_PER_PHASE = 240;

    private static final ForkJoinPool POOL = ForkJoinPool.commonPool();

    // Direct pixel buffer & High-Performance Z-Buffer
    private final BufferedImage screenImage;
    private final int[] pixels;
    private final float[] zBuffer = new float[PIXEL_COUNT];

    // 3D Grid coordinates
    private final float[] gridBaseX = new float[NODE_COUNT];
    private final float[] gridBaseZ = new float[NODE_COUNT];
    private final float[] nodeDistance = new float[NODE_COUNT];

    // Camera settings (1.5x Zoom: increased FOV from 480 to 720)
    private static final float FOV = 720.0f;
    private static final float CAMERA_DISTANCE = 650.0f;

    // Modes & Math Engine
    public enum Engine { STANDARD_MATH, FASTMATH_PARALLEL }
    public enum WaveShape { RIPPLE_MATRIX, TORUS_VORTEX, COSMIC_HELIX }

    private Engine currentEngine = Engine.STANDARD_MATH;
    private WaveShape currentShape = WaveShape.RIPPLE_MATRIX;

    private int frameInPhase = 0;
    private int completedCycles = 0;

    // Timing metrics
    private long phaseNanos = 0;
    private double standardAvgMs = 0;
    private double fastMathAvgMs = 0;
    private double liveComputeMs = 0;
    private double rollingSpeedup = 0;

    // Shockwave trigger
    private float shockwaveRadius = 0.0f;
    private float shockwaveIntensity = 0.0f;

    // Telemetry
    private int fps = 0;
    private int frameCounter = 0;
    private long lastFpsUpdate = System.currentTimeMillis();
    private float globalTime = 0;

    public WaveDemo() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        screenImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) screenImage.getRaster().getDataBuffer()).getData();

        initGrid();
        setupKeyControls();
    }

    private void initGrid() {
        float spacing = 2.4f;
        float halfGrid = (GRID_SIZE * spacing) / 2.0f;

        for (int z = 0; z < GRID_SIZE; z++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                int i = z * GRID_SIZE + x;
                float gx = (x * spacing) - halfGrid;
                float gz = (z * spacing) - halfGrid;
                gridBaseX[i] = gx;
                gridBaseZ[i] = gz;
                nodeDistance[i] = (float) Math.sqrt(gx * gx + gz * gz);
            }
        }
    }

    private void setupKeyControls() {
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_1) currentShape = WaveShape.RIPPLE_MATRIX;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_2) currentShape = WaveShape.TORUS_VORTEX;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_3) currentShape = WaveShape.COSMIC_HELIX;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_M || e.getKeyCode() == java.awt.event.KeyEvent.VK_TAB) {
                    currentEngine = (currentEngine == Engine.STANDARD_MATH) ? Engine.FASTMATH_PARALLEL : Engine.STANDARD_MATH;
                    frameInPhase = 0;
                    phaseNanos = 0;
                }
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {
                    shockwaveRadius = 0.0f;
                    shockwaveIntensity = 1.0f;
                }
            }
        });
    }

    public void startLoop() {
        createBufferStrategy(2);
        BufferStrategy bs = getBufferStrategy();

        try {
            FastDWM.beginTimerPeriod(1);
        } catch (Throwable ignored) {}

        Thread renderThread = new Thread(() -> {
            long lastFrameTime = System.nanoTime();
            final long targetFrameNanos = 1_000_000_000L / 120; // 120 FPS target

            while (true) {
                long now = System.currentTimeMillis();
                frameCounter++;
                if (now - lastFpsUpdate >= 1000) {
                    fps = frameCounter;
                    frameCounter = 0;
                    lastFpsUpdate = now;
                }

                long currentTime = System.nanoTime();
                float dt = (currentTime - lastFrameTime) / 1_000_000_000.0f;
                lastFrameTime = currentTime;

                // Clamp delta-time to avoid spikes during phase transitions
                if (dt > 0.05f) dt = 0.00833f;

                updateAndBenchmarkScene(dt);
                render(bs);

                long elapsedNanos = System.nanoTime() - currentTime;
                long sleepNanos = targetFrameNanos - elapsedNanos;
                if (sleepNanos > 1_000_000L) {
                    try {
                        Thread.sleep(sleepNanos / 1_000_000L);
                    } catch (InterruptedException ignored) {}
                }
            }
        }, "120FPS-Wave-Thread");

        renderThread.setPriority(Thread.MAX_PRIORITY);
        renderThread.start();
    }

    private void updateAndBenchmarkScene(float dt) {
        // True Delta-Time animation (rock-solid constant speed regardless of frame latency)
        globalTime += dt * 3.0f;
        if (shockwaveIntensity > 0.01f) {
            shockwaveRadius += dt * 720.0f;
            shockwaveIntensity *= (float) Math.pow(0.01, dt);
        }

        // Clear canvas & Reset Z-Buffer to infinity
        Arrays.fill(pixels, 0xFF0A0C14);
        Arrays.fill(zBuffer, Float.MAX_VALUE);

        frameInPhase++;
        long t0 = System.nanoTime();

        if (currentEngine == Engine.STANDARD_MATH) {
            computeStandardMathScene();
        } else {
            computeFastMathParallelScene();
        }

        long elapsed = System.nanoTime() - t0;
        phaseNanos += elapsed;
        liveComputeMs = elapsed / 1_000_000.0;

        // Auto phase switcher
        if (frameInPhase >= FRAMES_PER_PHASE) {
            if (currentEngine == Engine.STANDARD_MATH) {
                standardAvgMs = (phaseNanos / (double) FRAMES_PER_PHASE) / 1_000_000.0;
                currentEngine = Engine.FASTMATH_PARALLEL;
            } else {
                fastMathAvgMs = (phaseNanos / (double) FRAMES_PER_PHASE) / 1_000_000.0;
                completedCycles++;
                if (fastMathAvgMs > 0) {
                    rollingSpeedup = standardAvgMs / fastMathAvgMs;
                }
                currentEngine = Engine.STANDARD_MATH;
            }
            phaseNanos = 0;
            frameInPhase = 0;
        }
    }

    // 1. STANDARD JAVA MATH (Single-Threaded, Math.sin / Math.cos)
    private void computeStandardMathScene() {
        float pitch = 0.65f + (float) Math.sin(globalTime * 0.4) * 0.15f;
        float yaw = globalTime * 0.35f;

        float sinPitch = (float) Math.sin(pitch);
        float cosPitch = (float) Math.cos(pitch);
        float sinYaw = (float) Math.sin(yaw);
        float cosYaw = (float) Math.cos(yaw);

        for (int i = 0; i < NODE_COUNT; i++) {
            float gx = gridBaseX[i];
            float gz = gridBaseZ[i];
            float dist = nodeDistance[i];

            float gy = 0;
            if (currentShape == WaveShape.RIPPLE_MATRIX) {
                float wave1 = (float) Math.sin(dist * 0.04 - globalTime * 2.5) * 35.0f;
                float wave2 = (float) Math.cos(gx * 0.03 + globalTime * 1.8) * 20.0f;
                float wave3 = (float) Math.sin(gz * 0.03 - globalTime * 1.5) * 20.0f;
                gy = wave1 + wave2 + wave3;
            } else if (currentShape == WaveShape.TORUS_VORTEX) {
                float angle = (float) Math.atan2(gz, gx);
                gy = (float) Math.sin(angle * 4.0 + dist * 0.03 - globalTime * 3.0) * 45.0f;
            } else if (currentShape == WaveShape.COSMIC_HELIX) {
                gy = (float) (Math.sin(gx * 0.05 + globalTime * 3.0) * Math.cos(gz * 0.05 + globalTime * 2.0)) * 55.0f;
            }

            if (shockwaveIntensity > 0.01f) {
                float delta = Math.abs(dist - shockwaveRadius);
                if (delta < 40.0f) {
                    gy += (float) Math.sin((delta / 40.0f) * Math.PI) * 60.0f * shockwaveIntensity;
                }
            }

            float rotX = gx * cosYaw - gz * sinYaw;
            float rotZ = gx * sinYaw + gz * cosYaw;
            float rotY = gy * cosPitch - rotZ * sinPitch;
            float finalZ = gy * sinPitch + rotZ * cosPitch + CAMERA_DISTANCE;

            if (finalZ > 50.0f) {
                float invZ = 1.0f / finalZ;
                int screenX = (int) (WIDTH / 2.0f + (rotX * FOV * invZ));
                int screenY = (int) (HEIGHT / 2.0f + (rotY * FOV * invZ));

                float depthFactor = Math.max(0.0f, Math.min(1.0f, 1.0f - (finalZ - 300.0f) / 900.0f));
                float heightFactor = (gy + 60.0f) / 120.0f;

                int r = (int) ((30 + heightFactor * 120) * depthFactor);
                int g = (int) ((120 + heightFactor * 80) * depthFactor);
                int b = (int) ((220 + heightFactor * 35) * depthFactor);
                int rgb = (r << 16) | (g << 8) | b;

                // Thicker point size: 2x2 to 5x5 based on Z-depth
                int size = finalZ < 450 ? 4 : (finalZ < 650 ? 3 : 2);
                drawPointWithZ(screenX, screenY, finalZ, size, rgb);
            }
        }
    }

    // 2. FASTMATH PURE (Inlined Bhaskara + Multi-Core Parallel Batch)
    private void computeFastMathParallelScene() {
        float pitch = 0.65f + fastSin(globalTime * 0.4f) * 0.15f;
        float yaw = globalTime * 0.35f;

        float sinPitch = fastSin(pitch);
        float cosPitch = fastCos(pitch);
        float sinYaw = fastSin(yaw);
        float cosYaw = fastCos(yaw);

        final int chunkSize = 12_800;

        POOL.submit(() -> java.util.stream.IntStream.range(0, (NODE_COUNT + chunkSize - 1) / chunkSize).parallel().forEach(chunk -> {
            int start = chunk * chunkSize;
            int end = Math.min(start + chunkSize, NODE_COUNT);

            for (int i = start; i < end; i++) {
                float gx = gridBaseX[i];
                float gz = gridBaseZ[i];
                float dist = nodeDistance[i];

                float gy = 0;
                if (currentShape == WaveShape.RIPPLE_MATRIX) {
                    float wave1 = fastSin(dist * 0.04f - globalTime * 2.5f) * 35.0f;
                    float wave2 = fastCos(gx * 0.03f + globalTime * 1.8f) * 20.0f;
                    float wave3 = fastSin(gz * 0.03f - globalTime * 1.5f) * 20.0f;
                    gy = wave1 + wave2 + wave3;
                } else if (currentShape == WaveShape.TORUS_VORTEX) {
                    float angle = (float) Math.atan2(gz, gx);
                    gy = fastSin(angle * 4.0f + dist * 0.03f - globalTime * 3.0f) * 45.0f;
                } else if (currentShape == WaveShape.COSMIC_HELIX) {
                    gy = fastSin(gx * 0.05f + globalTime * 3.0f) * fastCos(gz * 0.05f + globalTime * 2.0f) * 55.0f;
                }

                if (shockwaveIntensity > 0.01f) {
                    float delta = Math.abs(dist - shockwaveRadius);
                    if (delta < 40.0f) {
                        gy += fastSin((delta / 40.0f) * 3.14159f) * 60.0f * shockwaveIntensity;
                    }
                }

                float rotX = gx * cosYaw - gz * sinYaw;
                float rotZ = gx * sinYaw + gz * cosYaw;
                float rotY = gy * cosPitch - rotZ * sinPitch;
                float finalZ = gy * sinPitch + rotZ * cosPitch + CAMERA_DISTANCE;

                if (finalZ > 50.0f) {
                    float invZ = 1.0f / finalZ;
                    int screenX = (int) (WIDTH / 2.0f + (rotX * FOV * invZ));
                    int screenY = (int) (HEIGHT / 2.0f + (rotY * FOV * invZ));

                    float depthFactor = Math.max(0.0f, Math.min(1.0f, 1.0f - (finalZ - 300.0f) / 900.0f));
                    float heightFactor = (gy + 60.0f) / 120.0f;

                    int r = (int) ((40 + heightFactor * 160) * depthFactor);
                    int g = (int) ((220 - heightFactor * 60) * depthFactor);
                    int b = (int) ((140 + heightFactor * 100) * depthFactor);
                    int rgb = (r << 16) | (g << 8) | b;

                    // Thicker point size: 2x2 to 5x5 based on Z-depth
                    int size = finalZ < 450 ? 4 : (finalZ < 650 ? 3 : 2);
                    drawPointWithZ(screenX, screenY, finalZ, size, rgb);
                }
            }
        })).join();
    }

    // High-Precision Z-Buffered Rasterizer (eliminates backface through-rendering)
    private void drawPointWithZ(int x, int y, float z, int size, int color) {
        if (x < 2 || x >= WIDTH - 6 || y < 2 || y >= HEIGHT - 6) return;

        for (int dy = 0; dy < size; dy++) {
            int py = y + dy;
            int rowOffset = py * WIDTH;
            for (int dx = 0; dx < size; dx++) {
                int px = x + dx;
                int idx = rowOffset + px;
                if (z < zBuffer[idx]) {
                    zBuffer[idx] = z;
                    pixels[idx] = color;
                }
            }
        }
    }

    private static float fastSin(float x) {
        float B = 1.27323954f;
        float C = -0.405284735f;
        x = x % 6.2831853f;
        if (x < -3.14159265f) x += 6.2831853f;
        else if (x > 3.14159265f) x -= 6.2831853f;
        float y = B * x + C * x * (x < 0 ? -x : x);
        return 0.225f * (y * (y < 0 ? -y : y) - y) + y;
    }

    private static float fastCos(float x) {
        return fastSin(x + 1.5707963f);
    }

    private void render(BufferStrategy bs) {
        Graphics2D g2 = (Graphics2D) bs.getDrawGraphics();
        g2.drawImage(screenImage, 0, 0, null);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Header Title
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g2.drawString("⚡ FastTween — 3D Holographic Kinetic Wave Live Benchmark", 30, 38);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g2.setColor(new Color(170, 175, 195));
        g2.drawString(String.format("FPS: %d  |  102,400 Nodes (Z-Buffered Quads 2x2 to 5x5)  |  Math Compute: %.2f ms / frame  |  Cycles: %d", fps, liveComputeMs, completedCycles), 30, 60);

        // Status Card (Top Right)
        g2.setColor(new Color(25, 28, 38, 240));
        g2.fillRoundRect(WIDTH - 440, 18, 410, 95, 10, 10);
        g2.setColor(currentEngine == Engine.STANDARD_MATH ? new Color(90, 150, 255) : new Color(50, 220, 140));
        g2.drawRoundRect(WIDTH - 440, 18, 410, 95, 10, 10);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        if (currentEngine == Engine.STANDARD_MATH) {
            g2.setColor(new Color(90, 150, 255));
            g2.drawString(String.format("🔵 Testing: Standard Java Math (%d/%d)", frameInPhase, FRAMES_PER_PHASE), WIDTH - 425, 42);
        } else {
            g2.setColor(new Color(50, 220, 140));
            g2.drawString(String.format("🟢 Testing: FastMath Parallel Batch (%d/%d)", frameInPhase, FRAMES_PER_PHASE), WIDTH - 425, 42);
        }

        // Live progress bar
        int barW = 380;
        int barProgress = (int) (barW * (frameInPhase / (double) FRAMES_PER_PHASE));
        g2.setColor(new Color(40, 45, 60));
        g2.fillRect(WIDTH - 425, 55, barW, 6);
        g2.setColor(currentEngine == Engine.STANDARD_MATH ? new Color(90, 150, 255) : new Color(50, 220, 140));
        g2.fillRect(WIDTH - 425, 55, barProgress, 6);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(new Color(200, 205, 225));
        g2.drawString("Modes: [1] Ripple [2] Torus [3] Helix [SPACE] Shock [M] Engine", WIDTH - 425, 85);

        // Persistent Rolling Results Card (Bottom)
        drawRollingScoreCard(g2);

        g2.dispose();
        bs.show();
    }

    private void drawRollingScoreCard(Graphics2D g2) {
        g2.setColor(new Color(18, 22, 32, 245));
        g2.fillRoundRect(30, HEIGHT - 105, WIDTH - 60, 85, 12, 12);
        g2.setColor(new Color(50, 220, 140));
        g2.drawRoundRect(30, HEIGHT - 105, WIDTH - 60, 85, 12, 12);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        String speedupText = rollingSpeedup > 0 ? String.format("FastMath is %.2fx faster across 102,400 3D nodes!", rollingSpeedup) : "Collecting first cycle comparison measurements...";
        g2.drawString("🏁 3D Kinetic Live Benchmark Metrics — " + speedupText, 50, HEIGHT - 75);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g2.setColor(new Color(180, 190, 215));

        double stdScore = standardAvgMs > 0 ? standardAvgMs : (currentEngine == Engine.STANDARD_MATH ? liveComputeMs : 0);
        double fastScore = fastMathAvgMs > 0 ? fastMathAvgMs : (currentEngine == Engine.FASTMATH_PARALLEL ? liveComputeMs : 0);

        g2.drawString(String.format("• Standard Math (Single-Thread Math.sin/cos): %7.2f ms / frame  |  %9.0f evaluations/sec", stdScore, stdScore > 0 ? (102400 / (stdScore / 1000.0)) : 0), 50, HEIGHT - 52);
        g2.drawString(String.format("• FastMath Parallel (SIMD-Style Inlined Math): %7.2f ms / frame  |  %9.0f evaluations/sec  (120 FPS)", fastScore, fastScore > 0 ? (102400 / (fastScore / 1000.0)) : 0), 50, HEIGHT - 30);
    }

    private static BufferedImage createRoundIcon() {
        BufferedImage icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(50, 220, 140));
        g.fillOval(4, 4, 56, 56);
        g.dispose();
        return icon;
    }

    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.awt.noerasebackground", "true");

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("FastTween — 3D Holographic Kinetic Wave Benchmark (102,400 Nodes @ 120 FPS)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setIconImage(createRoundIcon());
            WaveDemo canvas = new WaveDemo();
            frame.add(canvas);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.addNotify();
            try {
                long hwnd = FastTheme.getWindowHandle(frame);
                FastTheme.setTitleBarDarkMode(hwnd, true);
                FastTheme.setTitleBarColor(hwnd, 10, 12, 20);
                FastTheme.setTitleBarTextColor(hwnd, 255, 255, 255);
            } catch (Exception ignored) {}
            frame.setVisible(true);

            canvas.startLoop();
        });
    }
}