import MySemaphores.SemProducerConsumer;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ControlPanel extends JPanel {
    private JTextField tfNome, tfBrincadeira, tfDescanso, tfTamanho;
    private JComboBox<String> cbBola;
    private CourtPanel courtPanel;
    private List<Thread> threads = new ArrayList<>();

    private static final Color BG_COLOR = new Color(30, 40, 70);
    private static final Color PANEL_BG = new Color(20, 28, 55, 220);
    private static final Color ACCENT = new Color(255, 165, 0);
    private static final Color ACCENT2 = new Color(220, 80, 50);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color FIELD_BG = new Color(255, 255, 255, 230);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public ControlPanel(CourtPanel courtPanel) {
        this.courtPanel = courtPanel;
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new MatteBorder(0, 0, 3, 0, ACCENT));

        JPanel innerPanel = new JPanel(new GridBagLayout());
        innerPanel.setBackground(BG_COLOR);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // ---- Left section: Parâmetros da crianca ----
        JPanel leftSection = new JPanel(new GridBagLayout());
        leftSection.setOpaque(false);
        leftSection.setBorder(createSectionBorder("Insira os parametros da crianca:"));

        GridBagConstraints lc = new GridBagConstraints();
        lc.insets = new Insets(5, 8, 5, 8);
        lc.anchor = GridBagConstraints.WEST;

        // Row 1: Nome | Tempo de brincadeira
        lc.gridx = 0; lc.gridy = 0;
        leftSection.add(makeLabel("Nome:"), lc);
        lc.gridx = 1;
        tfNome = makeField(120);
        leftSection.add(tfNome, lc);
        lc.gridx = 2;
        leftSection.add(makeLabel("Tempo de brincadeira:"), lc);
        lc.gridx = 3;
        tfBrincadeira = makeField(80);
        leftSection.add(tfBrincadeira, lc);

        // Row 2: Bola | Tempo de descanso
        lc.gridx = 0; lc.gridy = 1;
        leftSection.add(makeLabel("Bola:"), lc);
        lc.gridx = 1;
        cbBola = new JComboBox<>(new String[]{"Nao", "Sim"});
        cbBola.setFont(FIELD_FONT);
        cbBola.setPreferredSize(new Dimension(120, 28));
        leftSection.add(cbBola, lc);
        lc.gridx = 2;
        leftSection.add(makeLabel("Tempo de descanso:"), lc);
        lc.gridx = 3;
        tfDescanso = makeField(80);
        leftSection.add(tfDescanso, lc);

        // ---- Center: Buttons ----
        JPanel centerSection = new JPanel(new GridLayout(3, 1, 0, 8));
        centerSection.setOpaque(false);
        centerSection.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton btnInstanciar = makeButton("Criar Cesto", new Color(60, 100, 200));
        JButton btnCriar = makeButton("Criar Crianca", new Color(50, 160, 70));
        JButton btnDestruir = makeButton("Destruir", new Color(200, 50, 50));

        centerSection.add(btnInstanciar);
        centerSection.add(btnCriar);
        centerSection.add(btnDestruir);

        // ---- Right section: Tamanho do cesto ----
        JPanel rightSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        rightSection.setOpaque(false);
        rightSection.setBorder(createSectionBorder("Insira tamanho do cesto:"));
        tfTamanho = makeField(100);
        rightSection.add(tfTamanho);

//        JButton btnSetCesto = makeButton("OK", ACCENT2);
//        btnSetCesto.setPreferredSize(new Dimension(60, 28));
//        rightSection.add(btnSetCesto);

        // ---- Assemble ----
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.6;
        innerPanel.add(leftSection, gbc);
        gbc.gridx = 1; gbc.weightx = 0.2;
        innerPanel.add(centerSection, gbc);
        gbc.gridx = 2; gbc.weightx = 0.2;
        innerPanel.add(rightSection, gbc);

        add(innerPanel, BorderLayout.CENTER);

        // ---- Log panel ----
        JPanel logWrapper = new JPanel(new BorderLayout());
        logWrapper.setBackground(new Color(10, 15, 35));
        logWrapper.setBorder(new MatteBorder(2, 0, 0, 0, ACCENT));
        JLabel logLabel = new JLabel(" Log: aguardando acoes...");
        logLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logLabel.setForeground(new Color(180, 220, 180));
        logWrapper.add(logLabel, BorderLayout.WEST);
        add(logWrapper, BorderLayout.SOUTH);

        // ---- Listeners ----
        btnInstanciar.addActionListener(e -> {
            String val = tfTamanho.getText().trim();
            if (!val.isEmpty()) {
                try {
                    int cap = Integer.parseInt(val);
                    SemProducerConsumer.setQtd_cesta(cap);
                    // Reinitialize semaphores with new capacity
                    SemProducerConsumer.reset(cap);
                    courtPanel.setCapacidade(cap);
                    logLabel.setText(" Log: Cesto instanciado com capacidade " + cap);
                } catch (NumberFormatException ex) {
                    logLabel.setText(" Log: Erro - tamanho do cesto inválido");
                }
            } else {
                logLabel.setText(" Log: Digite o tamanho do cesto primeiro");
            }
        });

//        btnSetCesto.addActionListener(e -> btnInstanciar.doClick());

        btnCriar.addActionListener(e -> {
            String nome = tfNome.getText().trim();
            String strBrincar = tfBrincadeira.getText().trim();
            String strDescanso = tfDescanso.getText().trim();
            String strCesta = tfTamanho.getText().trim();

            if (nome.isEmpty() || strBrincar.isEmpty() || strDescanso.isEmpty() || strCesta.isEmpty() ) {
                logLabel.setText(" Log: Preencha todos os campos da crianca");
                return;
            }
            try {
                int tBrincar = Integer.parseInt(strBrincar);
                int tDescanso = Integer.parseInt(strDescanso);
                boolean comBola = cbBola.getSelectedIndex() == 1;

                ChildThreadGUI crianca = new ChildThreadGUI(nome, tBrincar, tDescanso, comBola, courtPanel);
                threads.add(crianca);
                crianca.start();
                courtPanel.addChild(crianca);
                logLabel.setText(" Log: Crianca '" + nome + "' criada " + (comBola ? "(com bola)" : "(sem bola)"));
                tfNome.setText(""); tfBrincadeira.setText(""); tfDescanso.setText("");
            } catch (NumberFormatException ex) {
                logLabel.setText(" Log: Erro - tempos devem ser números inteiros");
            }
        });

        btnDestruir.addActionListener(e -> {
            for (Thread t : threads) t.interrupt();
            threads.clear();
            courtPanel.clearChildren();
            SemProducerConsumer.reset(0);
            logLabel.setText(" Log: Todas as threads destruídas");
        });
    }

    private TitledBorder createSectionBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT, 2, true), title,
            TitledBorder.LEFT, TitledBorder.TOP, TITLE_FONT, ACCENT
        );
        return border;
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(LABEL_FONT);
        lbl.setForeground(TEXT_COLOR);
        return lbl;
    }

    private JTextField makeField(int width) {
        JTextField tf = new JTextField();
        tf.setFont(FIELD_FONT);
        tf.setPreferredSize(new Dimension(width, 28));
        tf.setBackground(FIELD_BG);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 1),
            BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        return tf;
    }

    private JButton makeButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(color.brighter());
                } else {
                    g2.setColor(color);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 32));
        btn.setForeground(Color.WHITE);
        return btn;
    }
}
