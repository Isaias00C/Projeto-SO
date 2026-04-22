import MyThreads.*;

public class Main {
    public static void main(String[] args) {
        Thread r1 = new Producer("b1");
        Thread w1 = new Consumer("c1");
        Thread w2 = new Consumer("c2");

        r1.start();
        w1.start();
        w2.start();

    }
}