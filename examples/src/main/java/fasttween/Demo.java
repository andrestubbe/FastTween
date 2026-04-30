package fasttween;

import fasttween.Ease;
import fasttween.FastTween;
import fasttween.Tween;

/**
 * Demo showcasing FastTween functionality.
 * Standardized for the FastJava Blueprint.
 */
public class Demo {
    
    public static void main(String[] args) throws InterruptedException {
        printBanner();
        
        demoBasicTween();
        demoEasingTypes();
        demoCustomEasing();
        
        System.out.println("\n✅ Demo complete!");
    }
    
    private static void demoBasicTween() throws InterruptedException {
        System.out.println("\n--- Basic Tween (0 -> 100 in 500ms) ---");
        
        Tween tween = FastTween.to(0f, 100f, 500)
            .ease(Ease.LINEAR)
            .onUpdate(v -> System.out.printf("Value: %.2f%n", v))
            .onComplete(() -> System.out.println("Complete!"))
            .start();
        
        while (tween.isRunning()) {
            tween.update();
            Thread.sleep(16); // ~60fps
        }
    }
    
    private static void demoEasingTypes() throws InterruptedException {
        System.out.println("\n--- Easing Comparison (300ms each) ---");
        
        Ease[] eases = {Ease.LINEAR, Ease.QUAD_OUT, Ease.CUBIC_OUT, Ease.EXPO_OUT};
        
        for (Ease ease : eases) {
            System.out.printf("%n%s: ", ease.name());
            
            StringBuilder values = new StringBuilder();
            Tween tween = FastTween.to(0f, 50f, 300)
                .ease(ease)
                .onUpdate(v -> values.append(String.format("%.0f ", v)))
                .start();
            
            while (tween.isRunning()) {
                tween.update();
                Thread.sleep(16);
            }
            
            System.out.println(values);
        }
    }
    
    private static void demoCustomEasing() throws InterruptedException {
        System.out.println("\n--- Custom Easing (Smoothstep) ---");
        
        Tween tween = FastTween.to(0f, 100f, 400)
            .ease(t -> t * t * (3 - 2 * t))  // smoothstep
            .onUpdate(v -> System.out.printf("Custom: %.2f%n", v))
            .start();
        
        while (tween.isRunning()) {
            tween.update();
            Thread.sleep(16);
        }
    }
    
    private static void printBanner() {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║         FastTween Demo v0.1.0            ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }
}
