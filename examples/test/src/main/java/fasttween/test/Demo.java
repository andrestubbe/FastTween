package fasttween.test;

import fastmath.FastMathPure;
import fasttheme.FastTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

/**
 * ⚡ FastTween + FastMath Continuous Cyclic Live Benchmark (50,000 Particles).
 * 
 * Cycles continuously:
 * - PHASE 1: Standard JVM Math (Math.sin & Math.pow) — 50,000 Particles (Blue)
 * - PHASE 2: FastMath Pure (Taylor Polynomials) — 50,000 Particles (Green)
 * - REPEAT: Updates rolling benchmark telemetry, rolling average speedup, and cycle count in real-time.
 */
public class Demo extends JPanel {

    private static final int WIDTH = 1173;
    private static final int HEIGHT = 610;
    private static final int PARTICLE_COUNT = 50_000;
    private static final int FRAMES_PER_CYCLE = 240; // ~3 seconds per phase

    // Direct pixel buffer for blazing fast software rendering
    private final BufferedImage screenImage;
    private final int[] pixels;

    // Particle state arrays
    private final float[] startX = new float[PARTICLE_COUNT];
    private final float[] startY = new float[PARTICLE_COUNT];
    private final float[] targetX = new float[PARTICLE_COUNT];
    private final float[] targetY = new float[PARTICLE_COUNT];
    private final float[] phase = new float[PARTICLE_COUNT];
    private final float[] freq = new float[PARTICLE_COUNT];

    // Benchmark state machine
    private enum Phase { STANDARD_MATH, FASTMATH_PURE }
    private Phase currentPhase = Phase.STANDARD_MATH;
    private int currentFrameInPhase = 0;
    private int completedCycles = 0;

    // Rolling Benchmark metrics
    private long currentPhaseNanos = 0;
    private double lastStandardAvgMs = 0;
    private double lastFastMathAvgMs = 0;
    private double liveFrameMs = 0;
    private double rollingSpeedup = 0;

    // Telemetry
    private int fps = 0;
    private int frameCounter = 0;
    private long lastFpsUpdate = System.currentTimeMillis();
    private float globalTime = 0;

    public Demo() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        screenImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) screenImage.getRaster().getDataBuffer()).getData();

        initParticles();

        new Timer(8, e -> {
            updateAndBenchmark();
            repaint();
        }).start();
    }

    private void initParticles() {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            startX[i] = 60 + (float) (Math.random() * (WIDTH - 120));
            startY[i] = 130 + (float) (Math.random() * (HEIGHT - 270));

            targetX[i] = 60 + (float) (Math.random() * (WIDTH - 120));
            targetY[i] = 130 + (float) (Math.random() * (HEIGHT - 270));

            phase[i] = (float) (Math.random() * Math.PI * 2);
            freq[i] = 1.0f + (float) (Math.random() * 3.5f);
        }
    }

    private void updateAndBenchmark() {
        long now = System.currentTimeMillis();
        frameCounter++;
        if (now - lastFpsUpdate >= 1000) {
            fps = frameCounter;
            frameCounter = 0;
            lastFpsUpdate = now;
        }

        globalTime += 0.02f;
        float progress = (float) ((Math.sin(globalTime) + 1.0) * 0.5);

        // Dark canvas background
        Arrays.fill(pixels, 0xFF0D0F17);

        currentFrameInPhase++;

        if (currentPhase == Phase.STANDARD_MATH) {
            long t0 = System.nanoTime();
            runStandardMathBatch(progress, 0xFF5A96FF); // Blue
            long elapsed = System.nanoTime() - t0;
            currentPhaseNanos += elapsed;
            liveFrameMs = elapsed / 1_000_000.0;

            if (currentFrameInPhase >= FRAMES_PER_CYCLE) {
                lastStandardAvgMs = (currentPhaseNanos / (double) FRAMES_PER_CYCLE) / 1_000_000.0;
                currentPhaseNanos = 0;
                currentFrameInPhase = 0;
                currentPhase = Phase.FASTMATH_PURE; // Switch to FastMath
            }
        } else {
            long t0 = System.nanoTime();
            runFastMathBatch(progress, 0xFF32DC8C); // Green
            long elapsed = System.nanoTime() - t0;
            currentPhaseNanos += elapsed;
            liveFrameMs = elapsed / 1_000_000.0;

            if (currentFrameInPhase >= FRAMES_PER_CYCLE) {
                lastFastMathAvgMs = (currentPhaseNanos / (double) FRAMES_PER_CYCLE) / 1_000_000.0;
                currentPhaseNanos = 0;
                currentFrameInPhase = 0;
                completedCycles++;
                if (lastFastMathAvgMs > 0) {
                    rollingSpeedup = lastStandardAvgMs / lastFastMathAvgMs;
                }
                currentPhase = Phase.STANDARD_MATH; // Loop back to Standard Math
            }
        }
    }

    private void runStandardMathBatch(float progress, int color) {
        float c4 = (float) ((2 * Math.PI) / 3);
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            float t = progress;
            float eased = (float) (Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75f) * c4 * freq[i] + phase[i]) + 1);

            int px = (int) (startX[i] + (targetX[i] - startX[i]) * eased);
            int py = (int) (startY[i] + (targetY[i] - startY[i]) * eased);

            if (px >= 10 && px < WIDTH - 10 && py >= 110 && py < HEIGHT - 110) {
                pixels[py * WIDTH + px] = color;
            }
        }
    }

    private void runFastMathBatch(float progress, int color) {
        float c4 = (float) ((2 * Math.PI) / 3);
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            float t = progress;
            float sinFast = (float) FastMathPure.sinFast((t * 10 - 0.75f) * c4 * freq[i] + phase[i]);
            float powFast = (float) Math.pow(2, -10 * t);
            float eased = powFast * sinFast + 1.0f;

            int px = (int) (startX[i] + (targetX[i] - startX[i]) * eased);
            int py = (int) (startY[i] + (targetY[i] - startY[i]) * eased);

            if (px >= 10 && px < WIDTH - 10 && py >= 110 && py < HEIGHT - 110) {
                pixels[py * WIDTH + px] = color;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(screenImage, 0, 0, null);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Header Title
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g2.drawString("⚡ FastTween — Continuous 50,000 Particle Live Math Benchmark", 30, 38);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g2.setColor(new Color(170, 175, 195));
        g2.drawString(String.format("FPS: %d  |  Batch: 50,000 Tweens  |  Live Frame Math: %.2f ms  |  Cycles Completed: %d", fps, liveFrameMs, completedCycles), 30, 60);

        // Benchmark Status Card (Top Right)
        g2.setColor(new Color(25, 28, 38, 240));
        g2.fillRoundRect(WIDTH - 420, 18, 390, 85, 10, 10);
        g2.setColor(new Color(55, 60, 80));
        g2.drawRoundRect(WIDTH - 420, 18, 390, 85, 10, 10);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        if (currentPhase == Phase.STANDARD_MATH) {
            g2.setColor(new Color(90, 150, 255));
            g2.drawString(String.format("🔵 Testing: Standard Java Math (%d/%d)", currentFrameInPhase, FRAMES_PER_CYCLE), WIDTH - 405, 42);
        } else {
            g2.setColor(new Color(50, 220, 140));
            g2.drawString(String.format("🟢 Testing: FastMath Pure (%d/%d)", currentFrameInPhase, FRAMES_PER_CYCLE), WIDTH - 405, 42);
        }

        // Live progress bar in top card
        int barW = 360;
        int barProgress = (int) (barW * (currentFrameInPhase / (double) FRAMES_PER_CYCLE));
        g2.setColor(new Color(40, 45, 60));
        g2.fillRect(WIDTH - 405, 55, barW, 6);
        g2.setColor(currentPhase == Phase.STANDARD_MATH ? new Color(90, 150, 255) : new Color(50, 220, 140));
        g2.fillRect(WIDTH - 405, 55, barProgress, 6);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(new Color(190, 195, 210));
        g2.drawString(String.format("Live Frame Evaluation: %.2f ms (%.0f ops/sec)", liveFrameMs, liveFrameMs > 0 ? (50000 / (liveFrameMs / 1000.0)) : 0), WIDTH - 405, 85);

        // Persistent Rolling Results Card (Bottom)
        drawRollingScoreCard(g2);
    }

    private void drawRollingScoreCard(Graphics2D g2) {
        g2.setColor(new Color(18, 22, 32, 245));
        g2.fillRoundRect(30, HEIGHT - 105, WIDTH - 60, 85, 12, 12);
        g2.setColor(new Color(50, 220, 140));
        g2.drawRoundRect(30, HEIGHT - 105, WIDTH - 60, 85, 12, 12);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        String speedupText = rollingSpeedup > 0 ? String.format("FastMath is %.2fx faster in rolling tests", rollingSpeedup) : "Collecting first cycle measurements...";
        g2.drawString("🏁 Continuous Benchmark Metrics — " + speedupText, 50, HEIGHT - 75);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g2.setColor(new Color(180, 190, 215));

        double stdScore = lastStandardAvgMs > 0 ? lastStandardAvgMs : (currentPhase == Phase.STANDARD_MATH ? liveFrameMs : 0);
        double fastScore = lastFastMathAvgMs > 0 ? lastFastMathAvgMs : (currentPhase == Phase.FASTMATH_PURE ? liveFrameMs : 0);

        g2.drawString(String.format("• Standard Math (Math.sin & Math.pow): %7.2f ms / frame  |  %9.0f ops/sec", stdScore, stdScore > 0 ? (50000 / (stdScore / 1000.0)) : 0), 50, HEIGHT - 52);
        g2.drawString(String.format("• FastMath Pure (Taylor Polynomials):    %7.2f ms / frame  |  %9.0f ops/sec  (Zero GC Allocation)", fastScore, fastScore > 0 ? (50000 / (fastScore / 1000.0)) : 0), 50, HEIGHT - 30);
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
            JFrame frame = new JFrame("FastTween — Continuous 50,000 Particle Live Math Benchmark");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setIconImage(createRoundIcon());
            frame.add(new Demo());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.addNotify();
            try {
                long hwnd = FastTheme.getWindowHandle(frame);
                FastTheme.setTitleBarDarkMode(hwnd, true);
                FastTheme.setTitleBarColor(hwnd, 13, 15, 23);
                FastTheme.setTitleBarTextColor(hwnd, 255, 255, 255);
            } catch (Exception ignored) {
            }
            frame.setVisible(true);
        });
    }
}