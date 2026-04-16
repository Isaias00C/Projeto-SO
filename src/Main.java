import MySemaphore.SemProducerConsumer;
import MyThreads.ChildThread;

import java.util.Scanner;

public class Main {
    public static int capacidade_maxima;

    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("Capacidade maxima do cesto? ");
        capacidade_maxima = scanner.nextInt();
        SemProducerConsumer.empty.release(capacidade_maxima);

        while(true){
            System.out.println("inserir uma crianca? (1. sim / 2. nao)");
            int escolha = scanner.nextInt();
            if (escolha == 1) {
                criar_crianca();
            }else {
                System.out.println("tudo bem");
            }
        }
    }

    private static void criar_crianca() {
        System.out.println("noma da crianca? ");
        scanner.nextLine(); // aparentemente libera o buffer que ha entre o nextInt e o nextLine, sobra um '\n'
        String nome = scanner.nextLine();
        System.out.println("tempo de brincadeira? ");
        int tempo_brincadeira = scanner.nextInt();
        System.out.println("tempo de descanso? ");
        int tempo_descanso = scanner.nextInt();
        System.out.println("comeca com bola? (1. sim/ 2. nao)");
        int comeca_com = scanner.nextInt();
        if(comeca_com == 1) {
            Thread crianca = new ChildThread(nome, tempo_brincadeira, tempo_descanso, true);
            crianca.start();
        } else {
            Thread crianca = new ChildThread(nome, tempo_brincadeira, tempo_descanso);
            crianca.start();
        }
    }

    public Integer instanciar(){
        System.out.println("Digite o id do semaforo:");
        Integer id = scanner.nextInt();
        return id;
    }
}