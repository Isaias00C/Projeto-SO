package MyThreads;

import MySemaphores.SemProducerConsumer;
import javafx.application.Platform;

import java.util.function.Consumer;

public class ChildThread extends Thread {

    public final String name;
    public Boolean comecou_com_bola = false;
    public int tempo_esperar;
    public int tempo_brincar;

    // Callback para notificar a UI sobre mudanças de estado
    private Consumer<String> onStateChange;

    public ChildThread(String name, int tempo_brincar, int tempo_esperar) {
        super();
        this.name = name;
        this.tempo_brincar = tempo_brincar;
        this.tempo_esperar = tempo_esperar;
    }

    public ChildThread(String name, int tempo_brincar, int tempo_esperar, Boolean comecou_com_bola) {
        this.name = name;
        this.tempo_brincar = tempo_brincar;
        this.tempo_esperar = tempo_esperar;
        this.comecou_com_bola = comecou_com_bola;
    }

    public void setOnStateChange(Consumer<String> callback) {
        this.onStateChange = callback;
    }

    private void notifyState(String state) {
        if (onStateChange != null) {
            Platform.runLater(() -> onStateChange.accept(state));
        }
    }

    @Override
    public void run() {
        while (true) {
            if (comecou_com_bola) {
                notifyState("brincando");
                brinca(tempo_brincar);

                notifyState("caminhando_cesto");
                caminha_ate_cesto();

                try {
                    SemProducerConsumer.mutex.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                notifyState("devolvendo");
                devolve_bola();
                SemProducerConsumer.mutex.release();
                SemProducerConsumer.full.release();

                notifyState("voltando_canto");
                volta_para_canto();

                notifyState("descansando");
                descansa(tempo_esperar);

                comecou_com_bola = false;

            } else {
                notifyState("caminhando_cesto");
                caminha_ate_cesto();

                try {
                    SemProducerConsumer.full.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    SemProducerConsumer.mutex.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                notifyState("pegando");
                pega_bola();
                SemProducerConsumer.mutex.release();
                SemProducerConsumer.empty.release();

                notifyState("voltando_canto");
                volta_para_canto();

                notifyState("brincando");
                brinca(tempo_brincar);

                notifyState("caminhando_cesto");
                caminha_ate_cesto();

                try {
                    SemProducerConsumer.empty.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    SemProducerConsumer.mutex.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                notifyState("devolvendo");
                devolve_bola();
                SemProducerConsumer.mutex.release();
                SemProducerConsumer.full.release();

                notifyState("descansando");
                descansa(tempo_esperar);
            }
        }
    }

    private void volta_para_canto() {
        System.out.println("Criança " + this.name + " voltou para o seu canto");
    }

    private void pega_bola() {
        System.out.println("Criança " + this.name + " pegou uma bola");
    }

    private void devolve_bola() {
        System.out.println("Criança " + this.name + " devolveu a bola");
    }

    private void descansa(int tempo_descansar) {
        System.out.println("Criança " + this.name + " esta descansando");
        esperarSleep(tempo_descansar);
    }

    private void caminha_ate_cesto() {
        System.out.println("Criança " + this.name + " esta caminhando ate o cesto");
    }

    private void brinca(int tempo_brincar) {
        System.out.println("Criança " + this.name + " esta brincando");
        esperarSleep(tempo_brincar);
    }

    // Substituímos CPU-bound por sleep para não travar a UI
    public static void esperarSleep(int segundos) {
        try {
            Thread.sleep(segundos * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
