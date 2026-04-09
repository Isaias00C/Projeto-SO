import MyThreads.ChildThread;

import java.util.Scanner;

public class Main {
    public int capacidade_maxima;

    public Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Thread r1 = new ChildThread("c1", 5, 1);
        Thread w1 = new ChildThread("c2",3, 6, true);
        Thread w2 = new ChildThread("c3", 10, 1);

        r1.start();
        w1.start();
        w2.start();
    }

    public Integer instanciar(){
        System.out.println("Digite o id do semaforo:");
        Integer id = scanner.nextInt();
        return id;
    }
}