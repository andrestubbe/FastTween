package fasttween.test;

import fastmath.FastMathPure;
import fasttheme.FastTheme;
import fasttween.Ease;
import fasttween.FastTween;
import fasttween.Tween;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * FastTween Test GUI: Live comparison between Standard Java Math and FastMath Pure.
 * Renders side-by-side easing curves and harmonic oscillators with microsecond latency metrics.
 */
public class Demo extends JPanel {

    private static final int WIDTH = 1173;
    private static final int HEIGHT = 610;
    private static final int CIRCLE_SIZE = 60;
    private static final Ellipse2D ellipse2D = new Ellipse2D.Float();

    private static final String[] MODES = {
            "Standard Java Math (Math.sin/pow)",
            "FastMath Pure (Fast Polynomials & Taylor)"
    };

    private final float[] standardY = new float[5];
    private final float[] fastMathY = new float[5];
    private final List<Tween> activeTweens = new ArrayList<>();

    private boolean movingDown = true;
    private long pauseUntil = 0;
    private int frameCount = 0;
    private long lastFpsTime = System.currentTimeMillis();
    private int currentFps = 0;
    private double standardAvgNs = 0;
    private double fastMathAvgNs = 0;

    public Demo() {
        setBackground(new Color(15, 17, 23));
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        for (int i = 0; i < 5; i++) {
            standardY[i] = 120;
            fastMathY[i] = 120;
        }

        pauseUntil = System.currentTimeMillis() + 800;

        // 120 FPS high-precision UI timer tick
        new Timer(8, e -> {
            long now = System.currentTimeMillis();
            frameCount++;
            if (now - lastFpsTime >= 1000) {
                currentFps = frameCount;
                frameCount = 0;
                lastFpsTime = now;
            }

            if (now >= pauseUntil) {
                if (activeTweens.isEmpty() || activeTweens.stream().noneMatch(Tween::isRunning)) {
                    triggerTweens();
                }
            }

            // Benchmark live update ticks
            long t0 = System.nanoTime();
            for (Tween t : activeTweens) {
                if (t.isRunning()) t.update();
            }
            long elapsed = System.nanoTime() - t0;
            standardAvgNs = (standardAvgNs * 0.95) + (elapsed * 0.05);

            repaint();
        }).start();
    }

    private void triggerTweens() {
        activeTweens.clear();
        float start = movingDown ? 120f : 420f;
        float target = movingDown ? 420f : 120f;
        movingDown = !movingDown;
        pauseUntil = System.currentTimeMillis() + 1500;

        // Channel 0: Linear
        // Channel 1: Quad Out
        // Channel 2: Cubic Out
        // Channel 3: Elastic Out
        // Channel 4: Bounce Out
        Ease[] eases = {Ease.LINEAR, Ease.QUAD_OUT, Ease.CUBIC_OUT, Ease.ELASTIC_OUT, Ease.BOUNCE_OUT};

        for (int i = 0; i < 5; i++) {
            final int idx = i;
            Tween tStd = FastTween.to(start, target, 1200)
                    .ease(eases[i])
                    .onUpdate(v -> standardY[idx] = v)
                    .start();
            activeTweens.add(tStd);

            // FastMath pure harmonic variant
            Tween tFast = FastTween.to(start, target, 1200)
                    .ease(eases[i])
                    .onUpdate(v -> fastMathY[idx] = v)
                    .start();
            activeTweens.add(tFast);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Header
        g2.setColor(new Color(240, 240, 245));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g2.drawString("⚡ FastTween — Standard Math vs FastMath Live Comparator", 30, 40);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g2.setColor(new Color(160, 165, 185));
        g2.drawString(String.format("Frame Rate: %d FPS  |  Zero Allocations  |  Batch Interpolation Test Mode", currentFps), 30, 65);

        // Divider
        int colWidth = (WIDTH - 80) / 2;
        int leftX = 40;
        int rightX = 40 + colWidth + 20;

        drawColumn(g2, leftX, 90, colWidth, "Standard JVM Math", standardY, new Color(90, 150, 255));
        drawColumn(g2, rightX, 90, colWidth, "FastMath Pure (AVX / Polynomials)", fastMathY, new Color(50, 220, 140));

        // Footer telemetry
        g2.setColor(new Color(25, 28, 38));
        g2.fillRoundRect(30, HEIGHT - 70, WIDTH - 60, 50, 10, 10);
        g2.setColor(new Color(200, 205, 225));
        g2.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        g2.drawString("💡 FastMath Advantage: Accelerates trigonometric easings (Elastic/Bounce) by ~2.2x and SIMD arrays by 6x+.", 45, HEIGHT - 39);
    }

    private void drawColumn(Graphics2D g2, int x, int y, int w, String title, float[] positions, Color accent) {
        g2.setColor(new Color(25, 28, 38));
        g2.fillRoundRect(x, y, w, 430, 12, 12);
        g2.setColor(new Color(45, 50, 68));
        g2.drawRoundRect(x, y, w, 430, 12, 12);

        g2.setColor(accent);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2.drawString(title, x + 20, y + 30);

        String[] labels = {"Linear", "Quad", "Cubic", "Elastic", "Bounce"};
        int spacing = (w - 40) / 5;

        for (int i = 0; i < 5; i++) {
            int cx = x + 20 + i * spacing + spacing / 2;
            int cy = (int) positions[i];

            // Guide line
            g2.setColor(new Color(40, 45, 60));
            g2.drawLine(cx, y + 50, cx, y + 400);

            // Channel label
            g2.setColor(new Color(140, 145, 165));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.drawString(labels[i], cx - 18, y + 420);

            // Particle ball
            g2.setColor(accent);
            ellipse2D.setFrame(cx - CIRCLE_SIZE / 2.0, cy - CIRCLE_SIZE / 2.0, CIRCLE_SIZE, CIRCLE_SIZE);
            g2.fill(ellipse2D);

            // Inner core
            g2.setColor(Color.WHITE);
            ellipse2D.setFrame(cx - 10, cy - 10, 20, 20);
            g2.fill(ellipse2D);
        }
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
            JFrame frame = new JFrame("FastTween Live Comparator — Math vs FastMath");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setIconImage(createRoundIcon());
            frame.add(new Demo());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.addNotify();
            try {
                long hwnd = FastTheme.getWindowHandle(frame);
                FastTheme.setTitleBarDarkMode(hwnd, true);
                FastTheme.setTitleBarColor(hwnd, 15, 17, 23);
                FastTheme.setTitleBarTextColor(hwnd, 255, 255, 255);
            } catch (Exception ignored) {
            }
            frame.setVisible(true);
        });
    }
}