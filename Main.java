import java.util.LinkedList;
import java.util.concurrent.Semaphore;

class Buffer {
    LinkedList<Integer> lista = new LinkedList<>();
    Semaphore vazio;
    Semaphore cheio = new Semaphore(1); 
    Semaphore mutex = new Semaphore(1);
    int n = 10;

    public Buffer(int capacidade) {
        this.vazio = new Semaphore(capacidade);
    }
}
//hehe

class Crianca implements Runnable {
    private Buffer buffer;
    private String nome;

    public Crianca(Buffer buffer, String nome) {
        this.buffer = buffer;
        this.nome = nome; // Guarda o nome "udson"
    }

    @Override
    public void run() {
        try {
            while (true) {
                /// trocar pro tirar()
                buffer.cheio.acquire();
                buffer.mutex.acquire();
                
                buffer.n = buffer.n - 1;
                System.out.println(this.nome + " : tirou 1. Valor de n: " + buffer.n);
                
                buffer.mutex.release();
                buffer.vazio.release();
                /// trocar por tirar()

                Thread.sleep(1000); ///trocar por cpubound baseado no tempo da criança

                ///trocar por devolver()
                buffer.vazio.acquire();
                buffer.mutex.acquire();
                
                buffer.n = buffer.n + 1;  
                System.out.println(this.nome + " : devolveu 1. Valor de n: " + buffer.n);
                
                buffer.cheio.release();
                buffer.mutex.release();    
                ///trocar por devolver()

                Thread.sleep(1000); 
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Buffer buffer = new Buffer(5);
        Thread Crianca1 = new Thread(new Crianca(buffer, "udson"));//adciniorar o tempo por crainaça + metodo para adicionar criancas sem ser pelo codgio
        Thread Crianca2 = new Thread(new Crianca(buffer, "isaias"));
        Crianca1.setPriority(10);/// eu moggando isaias
		Crianca2.setPriority(1);
        Crianca1.start();
        Crianca2.start();
        
    }
}