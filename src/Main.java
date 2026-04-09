import MyThreads.ChildThread;

public class Main {
    public static void main(String[] args) {
        Thread r1 = new ChildThread("c1", 5, 1);
        Thread w1 = new ChildThread("c2",3, 6, true);
        Thread w2 = new ChildThread("c3", 10, 1);

        r1.start();
        w1.start();
        w2.start();

    }
}