package ui;

import javafx.animation.FillTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Cesto visual no centro da quadra.
 * Mostra quantas bolas estão disponíveis e a capacidade máxima.
 */
public class BasketSprite extends Pane {

    private final int capacity;
    private int currentBalls;

    private final Text countText;
    private final Rectangle basketBody;
    private final FillTransition flashAnim;

    public BasketSprite(int capacity, double x, double y) {
        this.capacity     = capacity;
        this.currentBalls = 0;

        setLayoutX(x);
        setLayoutY(y);

        // === Cesto (trapézio simulado com polígono) ===
        Polygon trapTop = new Polygon(
                0.0, 0.0,
                60.0, 0.0,
                50.0, 10.0,
                10.0, 10.0
        );
        trapTop.setFill(Color.web("#5C3317"));
        trapTop.setStroke(Color.web("#3E1F00"));
        trapTop.setStrokeWidth(2);

        basketBody = new Rectangle(8, 10, 44, 50);
        basketBody.setArcWidth(4);
        basketBody.setArcHeight(4);
        basketBody.setFill(Color.web("#8B4513"));
        basketBody.setStroke(Color.web("#5C3317"));
        basketBody.setStrokeWidth(2);

        // Linhas do cesto (textura)
        for (int i = 1; i < 4; i++) {
            Line hLine = new Line(8, 10 + i * 12, 52, 10 + i * 12);
            hLine.setStroke(Color.web("#5C3317"));
            hLine.setStrokeWidth(1);
            getChildren().add(hLine);
        }

        // Aro superior
        Ellipse rim = new Ellipse(30, 10, 28, 6);
        rim.setFill(Color.TRANSPARENT);
        rim.setStroke(Color.web("#CD853F"));
        rim.setStrokeWidth(2.5);

        // === Contador de bolas ===
        countText = new Text("0/" + capacity);
        countText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        countText.setFill(Color.WHITE);
        countText.setStroke(Color.BLACK);
        countText.setStrokeWidth(0.8);
        countText.setX(12);
        countText.setY(42);

        // Legenda
        Text label = new Text("bolas");
        label.setFont(Font.font("Arial", 9));
        label.setFill(Color.web("#FFD700"));
        label.setX(18);
        label.setY(54);

        getChildren().addAll(basketBody, trapTop, rim, countText, label);

        // Flash ao adicionar/remover bola
        flashAnim = new FillTransition(Duration.millis(300), basketBody);
        flashAnim.setAutoReverse(true);
        flashAnim.setCycleCount(2);
    }

    // ------------------------------------------------------------------ API

    public void addBall() {
        if (currentBalls < capacity) {
            currentBalls++;
            updateDisplay();
            flash(Color.web("#FF6B35"), Color.web("#8B4513"));
        }
    }

    public void removeBall() {
        if (currentBalls > 0) {
            currentBalls--;
            updateDisplay();
            flash(Color.web("#4CAF50"), Color.web("#8B4513"));
        }
    }

    public int getCurrentBalls() { return currentBalls; }

    // ------------------------------------------------------------------ private

    private void updateDisplay() {
        countText.setText(currentBalls + "/" + capacity);
        // Cor muda conforme lotação
        if (currentBalls == 0) {
            countText.setFill(Color.web("#FF4444"));
        } else if (currentBalls == capacity) {
            countText.setFill(Color.web("#44FF44"));
        } else {
            countText.setFill(Color.WHITE);
        }
    }

    private void flash(Color from, Color to) {
        flashAnim.stop();
        flashAnim.setFromValue(from);
        flashAnim.setToValue(to);
        flashAnim.play();
        ScaleTransition st = new ScaleTransition(Duration.millis(150), this);
        st.setByX(0.15); st.setByY(0.15);
        st.setAutoReverse(true); st.setCycleCount(2);
        st.play();
    }
}
