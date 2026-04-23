import java.util.concurrent.Semaphore;

import java.util.Scanner;

public class ExemploSemaforo {
    // Permite apenas 2 threads por vez
    public static final Semaphore mutex = new Semaphore(1);
    public static final Semaphore items = new Semaphore(0);
    public static final Semaphore spaces = new Semaphore(2);
    
    public static Integer buffer = 0;

    public static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        while (true) {
            try {
                System.out.println("Digite 0 para começar sem bola e 1 para começar com bola: ");
                Boolean bola = scanner.nextBoolean();
                System.out.println("Digite o id da crianca: ");
                Integer id = instanciar();
                new Thread(new crianca(mutex, items, spaces, id, bola)).start();
            } catch (Exception e) {
                System.out.println("Erro no loop: " + e.getMessage());
                break; // Sai do loop se houver erro crítico
            }
        }
    }
    
    public static Integer instanciar(){
        System.out.println("Digite o id do semaforo:");
        Integer id = scanner.nextInt();
        return id;
    }
    
    public static void esperarCpuBound(int segundos) {
        long tempoLimite = System.currentTimeMillis() + (segundos * 1000);
        
        System.out.println("Iniciando espera CPU Bound por " + segundos + "s...");
        
        // O processador vai fritar aqui dentro verificando a condição sem parar
        while (System.currentTimeMillis() < tempoLimite) {
            // Não faz nada, apenas gasta ciclo de CPU verificando a hora
        }
        
        System.out.println("Tempo esgotado! CPU liberada.");
    }

    static class crianca implements Runnable {
        public Semaphore mutex;
        public Semaphore itens;
        public Semaphore spaces;
        public int id;
        public boolean bola;

        public crianca(Semaphore mutex, Semaphore itens, Semaphore buffersize, int id, boolean bola) {
            this.mutex = mutex;
            this.itens = itens;
            this.spaces = buffersize;
            this.id = id;
            this.bola = bola;
        }

        @Override
        public void run() {
            try {
                System.out.println("Thread Produtor " + id + " aguardando...");
                spaces.acquire();
                mutex.acquire();
                System.out.println("Thread Produtor " + id + " entrou!");
                    buffer++;
                mutex.release();
                esperarCpuBound(60);// Simula trabalho
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Thread Produtor " + id + " liberando...");
                System.out.println("buffer valor:" + buffer);
                itens.release();
            }
        }
    }
}