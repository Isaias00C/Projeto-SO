import java.util.concurrent.Semaphore;

import java.util.Scanner;

public class ExemploSemaforo {
    // Permite apenas 2 threads por vez
    public static final Semaphore mutex = new Semaphore(1);
    public static final Semaphore items = new Semaphore(0);
    public static final Semaphore spaces = new Semaphore(5);
    
    public static Integer buffer = 0;

    public static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        while (true) {
            System.out.println("Digite o qual semaforo voce quer instanciar:");
            Integer escolha = scanner.nextInt();
            if(escolha == 0){
                try {
                    Integer id = instanciar();
                    new Thread(new Produtor(mutex, items, spaces, id)).start();
                } catch (Exception e) {
                    System.out.println("Erro no loop: " + e.getMessage());
                    break; // Sai do loop se houver erro crítico
                }
            }else if(escolha == 1){
                try {
                    Integer id = instanciar();
                    new Thread(new Consumidor(mutex, items, spaces, id)).start();
                } catch (Exception e) {
                    System.out.println("Erro no loop: " + e.getMessage());
                    break; // Sai do loop se houver erro crítico
                }
            }
        }
        
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

     public static Integer instanciar(){
        System.out.println("Digite o id do semaforo:");
        Integer id = scanner.nextInt();
        return id;
     }

    static class Produtor implements Runnable {
        public Semaphore mutex;
        public Semaphore itens;
        public Semaphore spaces;
        public int id;

        public Produtor(Semaphore mutex, Semaphore itens, Semaphore buffersize, int id) {
            this.mutex = mutex;
            this.itens = itens;
            this.spaces = buffersize;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                System.out.println("Thread Produtor " + id + " aguardando...");
                spaces.acquire();
                mutex.acquire();
                System.out.println("Thread Produtor " + id + " entrou!");
                    esperarCpuBound(5);// Simula trabalho
                    buffer++;
                    System.out.println("buffer valor:" + buffer);
                mutex.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Thread Produtor " + id + " liberando...");
                itens.release();
            }
        }
    }

    static class Consumidor implements Runnable {
        public Semaphore mutex;
        public Semaphore itens;
        public Semaphore spaces;
        private int id;

        public Consumidor(Semaphore mutex, Semaphore itens, Semaphore spaces, int id) {
            this.mutex = mutex;
            this.itens = itens;
            this.spaces = spaces;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                System.out.println("Thread Consumidor " + id + " aguardando...");
                itens.acquire();
                mutex.acquire();
                    System.out.println("Thread Consumidor " + id + " entrou!");
                    esperarCpuBound(3); // Simula trabalho
                    buffer--;
                    System.out.println("buffer valor:" + buffer);
                mutex.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Thread Consumidor " + id + " liberando...");
                spaces.release();
                esperarCpuBound(3); // Simula trabalho
            }
        }
    }
}