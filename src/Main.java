import MyThreads.Barber;
import MyThreads.Costumer;
import MyThreads.Readers;
import MyThreads.Writers;

public class Main {
    public static void main(String[] args) {
        Thread r1 = new Barber("b1");
        Thread w1 = new Costumer("c1");
        Thread w2 = new Costumer("c2");

        r1.start();
        w1.start();
        w2.start();

    }
}