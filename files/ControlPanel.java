package ui;

import MySemaphores.SemProducerConsumer;
import MyThreads.ChildThread;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.function.BiConsumer;

/**
 * Painel de controle superior — replica o design do Figma.
 */
public class ControlPanel extends HBox {

    // Campos da criança
    private final TextField tfNome         = styledField("Insira um valor", 110);
    private final TextField tfBrincadeira  = styledField("Insira um valor", 90);
    private final TextField tfDescanso     = styledField("Insira um valor", 90);
    private final ComboBox<String> cbBola  = new ComboBox<>();

    // Campo do cesto
    private final TextField tfCesto        = styledField("Insira um valor", 90);

    // Botões
    private final Button btnInstanciar = styledButton("Instanciar", "#4A90D9");
    private final Button btnCriar      = styledButton("Criar",      "#27AE60");
    private final Button btnDestruir   = styledButton("Destruir",   "#E74C3C");

    // Callback: (ChildThread criada, boolean instanciarApenas)
    private BiConsumer<ChildThread, Boolean> onChildAction;
    private Runnable onDestruir;

    // Estado de instância pendente
    private ChildThread pendingChild = null;
    private boolean cestoConfigurado = false;

    // Log de ações
    private final TextArea logArea = new TextArea();

    public ControlPanel() {
        setStyle("-fx-background-color: #2C3E50; -fx-border-color: #1ABC9C; -fx-border-width: 0 0 3 0;");
        setPadding(new Insets(12, 20, 12, 20));
        setSpacing(30);
        setAlignment(Pos.CENTER_LEFT);
        setPrefHeight(200);

        // ---- Secção "parâmetros da criança" ----
        VBox childSection = new VBox(8);
        childSection.setAlignment(Pos.TOP_LEFT);

        Text childTitle = new Text("Insira os parâmetros da criança:");
        childTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        childTitle.setFill(Color.WHITE);

        GridPane childGrid = new GridPane();
        childGrid.setHgap(10);
        childGrid.setVgap(8);

        childGrid.add(label("Nome:"),         0, 0);
        childGrid.add(tfNome,                 1, 0);
        childGrid.add(label("tempo de brincadeira:"), 2, 0);
        childGrid.add(tfBrincadeira,          3, 0);

        childGrid.add(label("Bola:"),         0, 1);
        cbBola.getItems().addAll("sim", "não");
        cbBola.setValue("não");
        cbBola.setStyle("-fx-background-color: white; -fx-font-size: 11px; -fx-pref-width: 80px;");
        childGrid.add(cbBola,                 1, 1);
        childGrid.add(label("tempo de descanso:"), 2, 1);
        childGrid.add(tfDescanso,             3, 1);

        childSection.getChildren().addAll(childTitle, childGrid);

        // ---- Botões centrais ----
        VBox btnBox = new VBox(10);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.getChildren().addAll(btnInstanciar, new HBox(10, btnCriar, btnDestruir));
        ((HBox) btnBox.getChildren().get(1)).setAlignment(Pos.CENTER);

        // ---- Secção "tamanho do cesto" ----
        VBox cestoSection = new VBox(8);
        cestoSection.setAlignment(Pos.TOP_RIGHT);

        HBox cestoRow = new HBox(10);
        cestoRow.setAlignment(Pos.CENTER_RIGHT);
        Text cestoTitle = new Text("Insira tamanho do cesto:");
        cestoTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        cestoTitle.setFill(Color.WHITE);
        cestoRow.getChildren().addAll(cestoTitle, tfCesto);

        // Log
        logArea.setPrefSize(280, 110);
        logArea.setEditable(false);
        logArea.setStyle("-fx-control-inner-background: #1A252F; -fx-text-fill: #2ECC71; -fx-font-family: monospace; -fx-font-size: 10px;");
        logArea.setWrapText(true);

        cestoSection.getChildren().addAll(cestoRow, logArea);

        // ---- Layout final ----
        HBox.setHgrow(btnBox, Priority.ALWAYS);
        getChildren().addAll(childSection, btnBox, cestoSection);

        setupActions();
    }

    // ------------------------------------------------------------------ Ações

    private void setupActions() {

        btnInstanciar.setOnAction(e -> {
            try {
                String nome = tfNome.getText().trim();
                if (nome.isEmpty()) { showAlert("Nome não pode ser vazio!"); return; }

                int tb = Integer.parseInt(tfBrincadeira.getText().trim());
                int td = Integer.parseInt(tfDescanso.getText().trim());
                boolean comBola = "sim".equals(cbBola.getValue());

                pendingChild = new ChildThread(nome, tb, td, comBola);
                log("✔ Instanciada: " + nome + (comBola ? " [com bola]" : ""));
                btnCriar.setDisable(false);
            } catch (NumberFormatException ex) {
                showAlert("Tempos devem ser números inteiros.");
            }
        });

        btnCriar.setOnAction(e -> {
            // Verificar se cesto foi configurado
            if (!cestoConfigurado) {
                try {
                    int cap = Integer.parseInt(tfCesto.getText().trim());
                    if (cap <= 0) { showAlert("Capacidade deve ser > 0"); return; }
                    SemProducerConsumer.inicializar(cap);
                    cestoConfigurado = true;
                    log("🧺 Cesto criado: cap=" + cap);
                } catch (NumberFormatException ex) {
                    showAlert("Insira o tamanho do cesto primeiro!");
                    return;
                }
            }

            if (pendingChild == null) {
                showAlert("Instancie uma criança primeiro!");
                return;
            }

            if (onChildAction != null) {
                onChildAction.accept(pendingChild, false);
            }
            log("🚀 Criada: " + pendingChild.name);
            pendingChild = null;
            btnCriar.setDisable(true);
        });

        btnDestruir.setOnAction(e -> {
            if (onDestruir != null) onDestruir.run();
            log("💥 Destruir solicitado");
        });

        btnCriar.setDisable(true);
    }

    // ------------------------------------------------------------------ API pública

    public void setOnChildAction(BiConsumer<ChildThread, Boolean> cb) { this.onChildAction = cb; }
    public void setOnDestruir(Runnable r)                              { this.onDestruir = r; }

    public void log(String msg) {
        logArea.appendText(msg + "\n");
        logArea.setScrollTop(Double.MAX_VALUE);
    }

    // ------------------------------------------------------------------ helpers

    private static TextField styledField(String prompt, double width) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(width);
        tf.setStyle("-fx-background-color: white; -fx-border-radius: 6; -fx-background-radius: 6;");
        return tf;
    }

    private static Button styledButton(String text, String hex) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        btn.setPrefWidth(120);
        btn.setPrefHeight(38);
        btn.setStyle(
            "-fx-background-color: " + hex + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: derive(" + hex + ", -20%);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: " + hex + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        return btn;
    }

    private static Label label(String txt) {
        Label l = new Label(txt);
        l.setStyle("-fx-text-fill: #BDC3C7; -fx-font-size: 11px;");
        return l;
    }

    private static void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
