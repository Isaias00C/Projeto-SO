import MyThreads.ChildThread;
import MySemaphores.SemProducerConsumer;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Simulador de Threads - Crianças e Bolas");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1440, 760);
            frame.setMinimumSize(new Dimension(1000, 600));
            frame.setLayout(new BorderLayout());

            CourtPanel courtPanel = new CourtPanel();
            ControlPanel controlPanel = new ControlPanel(courtPanel);

            frame.add(controlPanel, BorderLayout.NORTH);
            frame.add(courtPanel, BorderLayout.CENTER);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
