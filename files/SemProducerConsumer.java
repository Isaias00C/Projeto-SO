package MySemaphores;

import java.util.concurrent.Semaphore;

public class SemProducerConsumer {
    public static Semaphore mutex = new Semaphore(1);
    public static Semaphore full  = new Semaphore(0);  // bolas disponíveis para pegar
    public static Semaphore empty = new Semaphore(0);  // slots para devolver

    private static int qtd_cesta = 0;

    /** Deve ser chamado ANTES de criar qualquer thread */
    public static synchronized void inicializar(int capacidade) {
        qtd_cesta = capacidade;
        mutex  = new Semaphore(1);
        full   = new Semaphore(0);
        empty  = new Semaphore(capacidade);
    }

    public static synchronized int getQtd_cesta() {
        return qtd_cesta;
    }

    public static void setQtd_cesta(int qtd_cesta) {
        SemProducerConsumer.qtd_cesta = qtd_cesta;
    }
}
