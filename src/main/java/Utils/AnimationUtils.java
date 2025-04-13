package Utils;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public class AnimationUtils {
    
    public static void fadeInUp(Node node) {
        node.setOpacity(0);
        node.setTranslateY(20);

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(node.opacityProperty(), 0),
                new KeyValue(node.translateYProperty(), 20)
            ),
            new KeyFrame(Duration.millis(500), 
                new KeyValue(node.opacityProperty(), 1),
                new KeyValue(node.translateYProperty(), 0)
            )
        );
        timeline.play();
    }

    public static void pulseAnimation(Node node) {
        ScaleTransition pulse = new ScaleTransition(Duration.millis(100), node);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.1);
        pulse.setToY(1.1);
        pulse.setCycleCount(2);
        pulse.setAutoReverse(true);
        pulse.play();
    }

    public static void slideIn(Node node) {
        node.setTranslateX(-50);
        node.setOpacity(0);

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.translateXProperty(), -50),
                new KeyValue(node.opacityProperty(), 0)
            ),
            new KeyFrame(Duration.millis(500),
                new KeyValue(node.translateXProperty(), 0),
                new KeyValue(node.opacityProperty(), 1)
            )
        );
        timeline.play();
    }

    public static void shakeAnimation(Node node) {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(node.translateXProperty(), 0)),
            new KeyFrame(Duration.millis(100), new KeyValue(node.translateXProperty(), -10)),
            new KeyFrame(Duration.millis(200), new KeyValue(node.translateXProperty(), 10)),
            new KeyFrame(Duration.millis(300), new KeyValue(node.translateXProperty(), -10)),
            new KeyFrame(Duration.millis(400), new KeyValue(node.translateXProperty(), 10)),
            new KeyFrame(Duration.millis(500), new KeyValue(node.translateXProperty(), 0))
        );
        timeline.play();
    }
}
