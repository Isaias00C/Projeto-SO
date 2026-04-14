package ui;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Quadra de basquete desenhada inteiramente com JavaFX shapes.
 * Não requer nenhuma imagem externa.
 */
public class CourtPane extends Pane {

    public static final double WIDTH  = 1200;
    public static final double HEIGHT = 500;

    public CourtPane() {
        setPrefSize(WIDTH, HEIGHT);
        draw();
    }

    private void draw() {
        // === Fundo (arquibancadas) ===
        Rectangle bg = new Rectangle(0, 0, WIDTH, HEIGHT);
        bg.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#1a3a5c")),
                new Stop(0.5, Color.web("#0d2137")),
                new Stop(1, Color.web("#061020"))
        ));
        getChildren().add(bg);

        // Arquibancadas (fileiras de hexágonos simulados com retângulos arredondados)
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 30; col++) {
                Rectangle seat = new Rectangle(col * 42 - 8, row * 22 + 5, 38, 18);
                seat.setArcWidth(8); seat.setArcHeight(8);
                double shade = 0.15 + row * 0.04 + (col % 3) * 0.02;
                seat.setFill(Color.rgb(30, 70, 120, shade));
                seat.setStroke(Color.rgb(10, 40, 80, 0.3));
                seat.setStrokeWidth(0.5);
                getChildren().add(seat);
            }
        }

        // === Placar central (decorativo) ===
        Rectangle scoreboard = new Rectangle(WIDTH / 2 - 80, 10, 160, 90);
        scoreboard.setArcWidth(12); scoreboard.setArcHeight(12);
        scoreboard.setFill(Color.web("#0a1628"));
        scoreboard.setStroke(Color.web("#FF6B35"));
        scoreboard.setStrokeWidth(3);
        getChildren().add(scoreboard);

        Text scoreTitle = new Text("BASKETBALL");
        scoreTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        scoreTitle.setFill(Color.web("#FF6B35"));
        scoreTitle.setX(WIDTH / 2 - 52);
        scoreTitle.setY(32);
        getChildren().add(scoreTitle);

        // === Piso da quadra ===
        Rectangle floor = new Rectangle(0, 120, WIDTH, 380);
        floor.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#C8860A")),
                new Stop(0.5, Color.web("#D4942B")),
                new Stop(1, Color.web("#A06800"))
        ));
        getChildren().add(floor);

        // Tábuas do piso
        for (int i = 0; i < 20; i++) {
            Line plank = new Line(0, 120 + i * 19, WIDTH, 120 + i * 19);
            plank.setStroke(Color.rgb(100, 60, 0, 0.3));
            plank.setStrokeWidth(1);
            getChildren().add(plank);
        }

        // === Linhas da quadra ===
        // Borda
        Rectangle courtBorder = new Rectangle(60, 130, WIDTH - 120, 350);
        courtBorder.setFill(Color.TRANSPARENT);
        courtBorder.setStroke(Color.WHITE);
        courtBorder.setStrokeWidth(2.5);
        getChildren().add(courtBorder);

        // Linha central
        Line centerLine = new Line(WIDTH / 2, 130, WIDTH / 2, 480);
        centerLine.setStroke(Color.WHITE);
        centerLine.setStrokeWidth(2.5);
        getChildren().add(centerLine);

        // Círculo central
        Circle centerCircle = new Circle(WIDTH / 2, 305, 60);
        centerCircle.setFill(Color.TRANSPARENT);
        centerCircle.setStroke(Color.WHITE);
        centerCircle.setStrokeWidth(2.5);
        getChildren().add(centerCircle);

        // Área do garrafão - esquerda
        Rectangle paintL = new Rectangle(60, 205, 140, 200);
        paintL.setFill(Color.rgb(180, 100, 10, 0.3));
        paintL.setStroke(Color.WHITE);
        paintL.setStrokeWidth(2);
        getChildren().add(paintL);

        // Área do garrafão - direita
        Rectangle paintR = new Rectangle(WIDTH - 200, 205, 140, 200);
        paintR.setFill(Color.rgb(180, 100, 10, 0.3));
        paintR.setStroke(Color.WHITE);
        paintR.setStrokeWidth(2);
        getChildren().add(paintR);

        // Arco de 3 pontos - esquerda
        Arc arc3L = new Arc(60, 305, 200, 150, -90, 180);
        arc3L.setType(ArcType.OPEN);
        arc3L.setFill(Color.TRANSPARENT);
        arc3L.setStroke(Color.WHITE);
        arc3L.setStrokeWidth(2);
        getChildren().add(arc3L);

        // Arco de 3 pontos - direita
        Arc arc3R = new Arc(WIDTH - 60, 305, 200, 150, 90, 180);
        arc3R.setType(ArcType.OPEN);
        arc3R.setFill(Color.TRANSPARENT);
        arc3R.setStroke(Color.WHITE);
        arc3R.setStrokeWidth(2);
        getChildren().add(arc3R);

        // === Tabelas e aros ===
        drawBasketGoal(this, 40, 240, true);
        drawBasketGoal(this, WIDTH - 40, 240, false);

        // === Banner inferior ===
        Rectangle banner = new Rectangle(0, 460, WIDTH, 40);
        banner.setFill(Color.web("#CC2200"));
        getChildren().add(banner);

        Text bannerText = new Text("⚽  PRODUCER - CONSUMER  ⚽");
        bannerText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        bannerText.setFill(Color.WHITE);
        bannerText.setX(WIDTH / 2 - 120);
        bannerText.setY(485);
        getChildren().add(bannerText);
    }

    private void drawBasketGoal(Pane pane, double x, double y, boolean facingRight) {
        double dir = facingRight ? 1 : -1;

        // Poste
        Rectangle pole = new Rectangle(x - 4, y, 8, 200);
        pole.setFill(Color.web("#C0C0C0"));
        pole.setStroke(Color.web("#888888"));
        pole.setStrokeWidth(1);

        // Tabela
        Rectangle board = new Rectangle(x - 35 * dir - (facingRight ? 0 : -70), y + 10, 70, 50);
        board.setFill(Color.web("#FFFFFF"));
        board.setStroke(Color.web("#CC2200"));
        board.setStrokeWidth(3);

        // Quadrado vermelho no centro da tabela
        Rectangle square = new Rectangle(x - 15 * dir - (facingRight ? 0 : -30), y + 23, 30, 24);
        square.setFill(Color.TRANSPARENT);
        square.setStroke(Color.web("#CC2200"));
        square.setStrokeWidth(2);

        // Aro
        Ellipse rim = new Ellipse(x + 35 * dir, y + 60, 20, 5);
        rim.setFill(Color.TRANSPARENT);
        rim.setStroke(Color.web("#FF6B00"));
        rim.setStrokeWidth(3);

        // Rede (linhas verticais)
        for (int i = -3; i <= 3; i++) {
            Line net = new Line(
                    x + 35 * dir + i * 4.5, y + 64,
                    x + 35 * dir + i * 3, y + 90
            );
            net.setStroke(Color.WHITE);
            net.setStrokeWidth(1);
            pane.getChildren().add(net);
        }

        pane.getChildren().addAll(pole, board, square, rim);
    }
}
