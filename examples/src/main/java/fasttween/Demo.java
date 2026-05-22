package fasttween;

import fasttheme.FastTheme;
import fastdwm.FastDWM;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * FastTween v0.1.0 - "The Cinematic Swarm" (Game Loop Synced)
 */
public class Demo extends Canvas {
    
    private static final int SWARM_SIZE = 8;
    private final Actor[] swarm = new Actor[SWARM_SIZE];
    
    private static final Color BRAND_TEAL = new Color(0x15, 0x54, 0x5e);
    private static final Color BRAND_AQUA = new Color(0x53, 0xb6, 0xb1);
    
    private BufferedImage bakedBackground;
    private int frames = 0;
    private long lastFPSCheck = System.currentTimeMillis();
    private String currentFPS = "0";

    public Demo() {
        for (int i = 0; i < SWARM_SIZE; i++) swarm[i] = new Actor();
        setupMathChoreography();
    }

    private void precomputeBackground(int w, int h) {
        bakedBackground = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bakedBackground.createGraphics();
        GradientPaint gp = new GradientPaint(0, 0, BRAND_TEAL, 0, h, BRAND_TEAL.darker());
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);
        Random r = new Random();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        for (int y = 0; y < h; y += 4) {
            for (int x = 0; x < w; x += 4) {
                if (r.nextInt(100) < 5) {
                    g2.setColor(new Color(255, 255, 255, r.nextInt(10)));
                    g2.drawRect(x, y, 1, 1);
                }
            }
        }
        g2.dispose();
    }

    public void start() {
        createBufferStrategy(2);
        BufferStrategy bs = getBufferStrategy();
        new Thread(() -> {
            while (true) {
                // FastDWM.waitForVSync(); // UNLOCKED
                render(bs);
                updateFPS();
            }
        }, "Render-Loop").start();
    }

    private void render(BufferStrategy bs) {
        Graphics2D g2 = (Graphics2D) bs.getDrawGraphics();
        if (bakedBackground == null || bakedBackground.getWidth() != getWidth()) precomputeBackground(getWidth(), getHeight());
        
        g2.drawImage(bakedBackground, 0, 0, null);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int cx = getWidth() / 2 - 20;
        int cy = getHeight() / 2 - 20;
        for (Actor a : swarm) a.draw(g2, cx, cy);
        
        g2.dispose();
        bs.show();
    }

    private class Actor {
        float x, y, rotation, scale, alpha;
        void draw(Graphics2D g2, int centerX, int centerY) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1.0f, Math.max(0, alpha))));
            AffineTransform at = new AffineTransform();
            at.translate(centerX + x, centerY + y);
            at.rotate(Math.toRadians(rotation), 20, 20);
            at.scale(scale, scale);
            g2.setColor(BRAND_AQUA);
            g2.fillRoundRect(0, 0, 40, 40, 10, 10);
        }
    }

    private void setupMathChoreography() {
        for (int i = 0; i < SWARM_SIZE; i++) startActorCycle(i);
    }

    private void startActorCycle(int i) {
        final float angle = (float) (i * Math.PI * 2 / SWARM_SIZE);
        final float dist = 180f;
        FastTween.to(0, (float)Math.cos(angle) * dist, 1000).ease(Ease.ELASTIC_OUT).onUpdate(v -> swarm[i].x = v).start();
        FastTween.to(0, (float)Math.sin(angle) * dist, 1000).ease(Ease.ELASTIC_OUT).onUpdate(v -> swarm[i].y = v).start();
        FastTween.to(0, 1, 500).onUpdate(v -> swarm[i].alpha = v).start();
        FastTween.to(0.1f, 1.2f, 1000).ease(Ease.BACK_OUT).onUpdate(v -> swarm[i].scale = v)
            .onComplete(() -> {
                FastTween.to(0, 360, 2500).ease(Ease.CUBIC_IN_OUT).onUpdate(v -> {
                    double currentAngle = angle + Math.toRadians(v);
                    swarm[i].x = (float) Math.cos(currentAngle) * dist;
                    swarm[i].y = (float) Math.sin(currentAngle) * dist;
                    swarm[i].rotation = v * 2;
                }).onComplete(() -> {
                    FastTween.to(swarm[i].x, 0, 1000).ease(Ease.BOUNCE_OUT).onUpdate(v -> swarm[i].x = v).start();
                    FastTween.to(swarm[i].y, 0, 1000).ease(Ease.BOUNCE_OUT).onUpdate(v -> swarm[i].y = v).start();
                    FastTween.to(1.2f, 0, 1000).ease(Ease.QUAD_IN).onUpdate(v -> swarm[i].scale = v).start();
                    FastTween.to(1, 0, 1000).onUpdate(v -> swarm[i].alpha = v).onComplete(() -> startActorCycle(i)).start();
                }).start();
            }).start();
    }

    private void updateFPS() {
        frames++;
        long now = System.currentTimeMillis();
        if (now - lastFPSCheck >= 1000) {
            currentFPS = String.valueOf(frames);
            System.out.println("[FPS] FastTween: " + currentFPS);
            Window win = SwingUtilities.getWindowAncestor(this);
            if (win instanceof JFrame) ((JFrame) win).setTitle("FastTween Demo (FPS: " + currentFPS + ")");
            frames = 0;
            lastFPSCheck = now;
        }
    }

    private static Image createCircleIcon() {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BRAND_AQUA);
        g2.fillOval(4, 4, 24, 24);
        g2.dispose();
        return img;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("FastTween Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1184, 621);
        frame.setIconImage(createCircleIcon());
        Demo demo = new Demo();
        frame.add(demo);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Styling with delay to ensure DWM is ready
        Timer styleTimer = new Timer(50, e -> {
            long hwnd = FastTheme.getWindowHandle(frame);
            if (hwnd != 0) {
                FastTheme.setTitleBarDarkMode(hwnd, true);
                FastTheme.setTitleBarColor(hwnd, BRAND_TEAL.getRed(), BRAND_TEAL.getGreen(), BRAND_TEAL.getBlue());
            }
        });
        styleTimer.setRepeats(false);
        styleTimer.start();

        demo.start();
    }
}
