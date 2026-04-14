package MySemaphores;

import java.util.concurrent.Semaphore;

public class SemProducerConsumer {
    public static Semaphore mutex = new Semaphore(1);
    public static Semaphore full = new Semaphore(0);
    public static Semaphore empty = new Semaphore(0);
    private static int qtd_cesta;

    public static void setQtd_cesta(int qtd_cesta) {
        SemProducerConsumer.qtd_cesta = qtd_cesta;
    }

    /**
     * Reset all semaphores for a new simulation.
     * @param capacidade the basket capacity (number of balls it starts with)
     */
    public static synchronized void reset(int capacidade) {
        mutex = new Semaphore(1);
        full = new Semaphore(0);  // basket starts full
        empty = new Semaphore(capacidade);
        qtd_cesta = capacidade;
    }
}
