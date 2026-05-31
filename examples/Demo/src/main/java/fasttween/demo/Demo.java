package fasttween.demo;

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

public class Demo extends JPanel {

    private static final int WIDTH = 1173;
    private static final int HEIGHT = 610;
    private static final int CIRCLE_SIZE = 70;
    private static final Ellipse2D ellipse2D = new Ellipse2D.Float();

    private static final Ease[] EASINGS = {
            Ease.LINEAR,
            Ease.QUAD_OUT,
            Ease.CUBIC_IN_OUT,
            Ease.QUART_OUT,
            Ease.BACK_OUT,
            Ease.ELASTIC_OUT,
            Ease.BOUNCE_OUT
    };

    private final float[] yPositions;
    private final List<Tween> activeTweens;

    // State machine for continuous loop
    private boolean movingDown = true;
    private long pauseUntil = 0;

    // FPS tracking
    private int frames = 0;
    private long lastFpsTime = 0;

    public Demo() {
        setBackground(new Color(0, 0, 0));
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        yPositions = new float[EASINGS.length];
        activeTweens = new ArrayList<>();

        // Initial setup
        for (int i = 0; i < yPositions.length; i++) {
            yPositions[i] = 100;
        }

        // Auto-start after 1 second
        pauseUntil = System.currentTimeMillis() + 1000;

        new Timer(8, e -> {
            long now = System.currentTimeMillis();

            // FPS Counter
            frames++;
            if (now - lastFpsTime >= 1000) {
                int fps = frames;
                frames = 0;
                lastFpsTime = now;
                Window win = SwingUtilities.getWindowAncestor(this);
                if (win instanceof JFrame) {
                    ((JFrame) win).setTitle("FastTween Demo - Fps: " + fps);
                }
            }

            // If we are waiting for a pause, do nothing but repaint
            if (now < pauseUntil) {
                repaint();
                return;
            }

            boolean anyRunning = false;
            for (Tween t : activeTweens) {
                if (t.update()) {
                    anyRunning = true;
                }
            }

            // If animation finished, schedule a pause and flip direction
            if (!anyRunning && !activeTweens.isEmpty()) {
                activeTweens.clear();
                movingDown = !movingDown;
                pauseUntil = now + 1000; // 1 second pause
            }
            // If no active tweens and we passed the pause time, start next wave
            else if (activeTweens.isEmpty()) {
                float startY = movingDown ? 100f : HEIGHT - 100f;
                float endY = movingDown ? HEIGHT - 100f : 100f;

                for (int i = 0; i < EASINGS.length; i++) {
                    final int index = i;
                    Tween t = FastTween.to(startY, endY, 2500)
                            .ease(EASINGS[i])
                            .onUpdate(val -> yPositions[index] = val)
                            .start();
                    activeTweens.add(t);
                }
            }

            repaint();
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);

        int cols = EASINGS.length;
        // Float spacing for pixel-perfect distribution
        float spacing = (float) WIDTH / (cols + 1);

        for (int i = 0; i < cols; i++) {
            // (i + 1) so we have equal padding on left and right edge
            float xCenter = Math.round((i + 1) * spacing);
            float y = yPositions[i];
            ellipse2D.setFrame(xCenter - (CIRCLE_SIZE / 2), y - (CIRCLE_SIZE / 2), CIRCLE_SIZE, CIRCLE_SIZE);
            g2d.fill(ellipse2D);
        }
    }

    private static BufferedImage createRoundIcon() {
        BufferedImage icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillOval(4, 4, 56, 56);
        g.dispose();
        return icon;
    }

    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.awt.noerasebackground", "true");

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("FastTween Demo - Fps: ...");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setIconImage(createRoundIcon());
            frame.add(new Demo());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.addNotify();
            try {
                long hwnd = FastTheme.getWindowHandle(frame);
                FastTheme.setTitleBarDarkMode(hwnd, true);
                FastTheme.setTitleBarColor(hwnd, 0, 0, 0);
                FastTheme.setTitleBarTextColor(hwnd, 255, 255, 255);
                FastTheme.setWindowTransparency(hwnd, 224);
            } catch (Exception e) {
                System.err.println("FastTheme not available: " + e.getMessage());
            }
            frame.setVisible(true);
        });
    }
}
