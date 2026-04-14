package ui;

import javafx.animation.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Sprite visual de uma criança na quadra.
 * Desenhado em JavaFX shapes — sem imagens externas.
 */
public class ChildSprite extends Pane {

    private static final double SPRITE_W = 36;
    private static final double SPRITE_H = 60;

    public enum State { IDLE, WALKING, PLAYING, WAITING, AT_BASKET }

    private final String childName;
    private State currentState = State.IDLE;
    private boolean hasBall;

    // Partes do boneco
    private final Circle head;
    private final Rectangle body;
    private final Rectangle legL, legR;
    private final Circle ball;
    private final Text nameTag;

    // Animação de "bounce"
    private final TranslateTransition bounce;
    // Cor do "status" brilhando no body
    private final FillTransition bodyColor;

    // Posição base na quadra (canto designado)
    private double homeX, homeY;

    public ChildSprite(String name, double x, double y, boolean startWithBall) {
        this.childName = name;
        this.hasBall   = startWithBall;
        this.homeX = x;
        this.homeY = y;

        setPrefSize(SPRITE_W + 20, SPRITE_H + 30);
        setLayoutX(x);
        setLayoutY(y);

        // === Cabeça ===
        head = new Circle(SPRITE_W / 2.0, 10, 10, Color.web("#F4A460"));
        head.setStroke(Color.web("#8B4513"));
        head.setStrokeWidth(1.5);

        // Olhos
        Circle eyeL = new Circle(SPRITE_W / 2.0 - 3.5, 8, 2, Color.WHITE);
        Circle eyeR = new Circle(SPRITE_W / 2.0 + 3.5, 8, 2, Color.WHITE);
        Circle pupilL = new Circle(SPRITE_W / 2.0 - 3.5, 8.5, 1, Color.BLACK);
        Circle pupilR = new Circle(SPRITE_W / 2.0 + 3.5, 8.5, 1, Color.BLACK);

        // Sorriso
        Arc smile = new Arc(SPRITE_W / 2.0, 11, 4, 3, 200, 140);
        smile.setType(ArcType.OPEN);
        smile.setStroke(Color.web("#8B4513"));
        smile.setStrokeWidth(1.2);
        smile.setFill(Color.TRANSPARENT);

        // === Cabelo ===
        Arc hair = new Arc(SPRITE_W / 2.0, 6, 10, 7, 0, 180);
        hair.setType(ArcType.ROUND);
        hair.setFill(Color.web("#5C3317"));

        // === Corpo (camiseta laranja) ===
        body = new Rectangle(SPRITE_W / 2.0 - 9, 20, 18, 20);
        body.setArcWidth(4);
        body.setArcHeight(4);
        body.setFill(Color.web("#FF6B35"));
        body.setStroke(Color.web("#CC4400"));
        body.setStrokeWidth(1);

        // === Pernas ===
        legL = new Rectangle(SPRITE_W / 2.0 - 9, 40, 7, 14);
        legL.setArcWidth(3);
        legL.setArcHeight(3);
        legL.setFill(Color.web("#1A1A2E"));

        legR = new Rectangle(SPRITE_W / 2.0 + 2, 40, 7, 14);
        legR.setArcWidth(3);
        legR.setArcHeight(3);
        legR.setFill(Color.web("#1A1A2E"));

        // Tênis
        Rectangle shoeL = new Rectangle(SPRITE_W / 2.0 - 11, 52, 10, 5);
        shoeL.setArcWidth(3); shoeL.setArcHeight(3);
        shoeL.setFill(Color.WHITE);
        Rectangle shoeR = new Rectangle(SPRITE_W / 2.0 + 1, 52, 10, 5);
        shoeR.setArcWidth(3); shoeR.setArcHeight(3);
        shoeR.setFill(Color.WHITE);

        // === Bola ===
        ball = new Circle(SPRITE_W / 2.0 + 14, 38, 7, Color.web("#FF8C00"));
        ball.setStroke(Color.web("#8B4513"));
        ball.setStrokeWidth(1.5);
        // Linhas da bola
        Line ballLine1 = new Line(SPRITE_W / 2.0 + 7, 38, SPRITE_W / 2.0 + 21, 38);
        ballLine1.setStroke(Color.web("#8B4513")); ballLine1.setStrokeWidth(1);
        Line ballLine2 = new Line(SPRITE_W / 2.0 + 14, 31, SPRITE_W / 2.0 + 14, 45);
        ballLine2.setStroke(Color.web("#8B4513")); ballLine2.setStrokeWidth(1);
        ball.setVisible(hasBall);
        ballLine1.visibleProperty().bind(ball.visibleProperty());
        ballLine2.visibleProperty().bind(ball.visibleProperty());

        // === Tag do nome ===
        nameTag = new Text(childName);
        nameTag.setFont(Font.font("Arial", FontWeight.BOLD, 9));
        nameTag.setFill(Color.WHITE);
        nameTag.setStroke(Color.BLACK);
        nameTag.setStrokeWidth(0.5);
        nameTag.setX(SPRITE_W / 2.0 - childName.length() * 3);
        nameTag.setY(72);

        getChildren().addAll(
                hair, head, eyeL, eyeR, pupilL, pupilR, smile,
                body, legL, legR, shoeL, shoeR,
                ball, ballLine1, ballLine2,
                nameTag
        );

        // === Animação bounce padrão ===
        bounce = new TranslateTransition(Duration.millis(400), this);
        bounce.setByY(-6);
        bounce.setAutoReverse(true);
        bounce.setCycleCount(Animation.INDEFINITE);

        // Transição de cor do corpo conforme estado
        bodyColor = new FillTransition(Duration.millis(600), body);
        bodyColor.setAutoReverse(true);
        bodyColor.setCycleCount(Animation.INDEFINITE);
    }

    // ------------------------------------------------------------------ API

    public String getChildName() { return childName; }
    public State getState()       { return currentState; }
    public double getHomeX()      { return homeX; }
    public double getHomeY()      { return homeY; }

    public void setState(State state) {
        currentState = state;
        applyStateVisuals(state);
    }

    public void setHasBall(boolean has) {
        hasBall = has;
        ball.setVisible(has);
    }

    public void moveTo(double x, double y, Runnable onFinished) {
        TranslateTransition move = new TranslateTransition(Duration.millis(800), this);
        double dx = x - (getLayoutX() + getTranslateX());
        double dy = y - (getLayoutY() + getTranslateY());
        move.setByX(dx);
        move.setByY(dy);
        if (onFinished != null) move.setOnFinished(e -> onFinished.run());
        move.play();
    }

    // ------------------------------------------------------------------ private

    private void applyStateVisuals(State state) {
        bounce.stop();
        bodyColor.stop();

        switch (state) {
            case WALKING -> {
                body.setFill(Color.web("#FF6B35"));
                bounce.setByY(-4);
                bounce.setDuration(Duration.millis(250));
                bounce.play();
            }
            case PLAYING -> {
                body.setFill(Color.web("#FF6B35"));
                bodyColor.setFromValue(Color.web("#FF6B35"));
                bodyColor.setToValue(Color.web("#FFB347"));
                bodyColor.play();
                bounce.setByY(-8);
                bounce.setDuration(Duration.millis(350));
                bounce.play();
            }
            case WAITING -> {
                body.setFill(Color.web("#888888"));
                bodyColor.setFromValue(Color.web("#888888"));
                bodyColor.setToValue(Color.web("#AAAAAA"));
                bodyColor.play();
            }
            case AT_BASKET -> {
                body.setFill(Color.web("#4CAF50"));
                bounce.setByY(-3);
                bounce.setDuration(Duration.millis(500));
                bounce.play();
            }
            default -> body.setFill(Color.web("#FF6B35"));
        }
    }
}
