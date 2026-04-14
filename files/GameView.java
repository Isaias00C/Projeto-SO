package ui;

import MySemaphores.SemProducerConsumer;
import MyThreads.ChildThread;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.*;

/**
 * GameView monta o layout completo:
 *   - ControlPanel (topo)
 *   - CourtPane  (quadra de basquete animada)
 *
 * Gerencia os sprites das crianças e o cesto central.
 */
public class GameView {

    private final VBox root;
    private final ControlPanel controlPanel;
    private final CourtPane courtPane;
    private final Pane courtLayer;   // layer transparente sobre a quadra para sprites
    private BasketSprite basket;

    // Mapeamento thread -> sprite
    private final Map<String, ChildSprite> sprites = new LinkedHashMap<>();
    private final List<ChildThread> threads = new ArrayList<>();

    // Posições dos cantos disponíveis na quadra para novas crianças
    private static final double[][] CORNERS = {
        {80,  350}, {150, 400}, {900, 350}, {980, 400},
        {80,  250}, {200, 300}, {850, 250}, {1050,300},
        {300, 420}, {700, 420}, {500, 430}, {400, 380}
    };
    private int cornerIndex = 0;

    // Centro do cesto na tela
    private static final double BASKET_X = CourtPane.WIDTH / 2 - 30;
    private static final double BASKET_Y = 230;

    public GameView() {
        courtPane  = new CourtPane();
        courtLayer = new Pane();
        courtLayer.setPrefSize(CourtPane.WIDTH, CourtPane.HEIGHT);
        courtLayer.setPickOnBounds(false);

        // Stack: quadra embaixo, sprites em cima
        Pane courtStack = new Pane(courtPane, courtLayer);
        courtStack.setPrefSize(CourtPane.WIDTH, CourtPane.HEIGHT);

        controlPanel = new ControlPanel();
        controlPanel.setPrefHeight(200);

        root = new VBox(controlPanel, courtStack);

        // Conecta o painel de controle ao gerenciador de threads
        controlPanel.setOnChildAction((child, instantiateOnly) -> addChild(child));
        controlPanel.setOnDestruir(this::destroyAll);
    }

    public VBox getRoot() { return root; }

    // ------------------------------------------------------------------ API

    /**
     * Adiciona uma criança: cria o sprite, configura o callback de estado
     * e inicia a thread.
     */
    public void addChild(ChildThread child) {
        // Cria o cesto na primeira criança se ainda não existir
        if (basket == null) {
            int cap = SemProducerConsumer.getQtd_cesta();
            if (cap <= 0) cap = 5; // default safety
            basket = new BasketSprite(cap, BASKET_X, BASKET_Y);
            Platform.runLater(() -> courtLayer.getChildren().add(basket));
        }

        // Seleciona canto
        double[] corner = CORNERS[cornerIndex % CORNERS.length];
        cornerIndex++;

        ChildSprite sprite = new ChildSprite(
                child.name,
                corner[0], corner[1],
                child.comecou_com_bola
        );

        sprites.put(child.name, sprite);
        Platform.runLater(() -> courtLayer.getChildren().add(sprite));

        // Callback de estado: atualiza o sprite e o cesto
        child.setOnStateChange(state -> updateSprite(child.name, state));

        threads.add(child);
        child.setDaemon(true);
        child.start();

        controlPanel.log("👶 " + child.name + " entrou na quadra");
    }

    public void destroyAll() {
        threads.forEach(Thread::interrupt);
        threads.clear();
        sprites.clear();
        Platform.runLater(() -> {
            courtLayer.getChildren().clear();
            basket = null;
            cornerIndex = 0;
        });
        controlPanel.log("🧹 Todas as threads destruídas");
    }

    // ------------------------------------------------------------------ private

    private void updateSprite(String name, String state) {
        ChildSprite sprite = sprites.get(name);
        if (sprite == null) return;

        switch (state) {
            case "brincando" -> {
                sprite.setState(ChildSprite.State.PLAYING);
                sprite.setHasBall(true);
                sprite.moveTo(sprite.getHomeX(), sprite.getHomeY(), null);
            }
            case "caminhando_cesto" -> {
                sprite.setState(ChildSprite.State.WALKING);
                sprite.moveTo(BASKET_X - 40 + Math.random() * 40, BASKET_Y + 60, null);
            }
            case "pegando" -> {
                sprite.setState(ChildSprite.State.AT_BASKET);
                sprite.setHasBall(true);
                if (basket != null) basket.removeBall();
            }
            case "devolvendo" -> {
                sprite.setState(ChildSprite.State.AT_BASKET);
                sprite.setHasBall(false);
                if (basket != null) basket.addBall();
            }
            case "voltando_canto" -> {
                sprite.setState(ChildSprite.State.WALKING);
                sprite.moveTo(sprite.getHomeX(), sprite.getHomeY(), null);
            }
            case "descansando" -> {
                sprite.setState(ChildSprite.State.WAITING);
                sprite.moveTo(sprite.getHomeX(), sprite.getHomeY(), null);
            }
        }
    }
}
