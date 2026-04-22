package MySemaphores;

import java.util.concurrent.Semaphore;

public class SemBarbershop {
    public static Semaphore barber = new Semaphore(0);
    public static Semaphore costumers = new Semaphore(0);
    public static Semaphore mutex = new Semaphore(1);
    public static int n_chairs = 10;
}
