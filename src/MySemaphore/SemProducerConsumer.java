package MySemaphore;

import java.util.concurrent.Semaphore;

public class SemProducerConsumer {
    public static Semaphore mutex = new Semaphore(1);
    public static Semaphore full = new Semaphore(0);
    public static Semaphore empty = new Semaphore(0);
}
