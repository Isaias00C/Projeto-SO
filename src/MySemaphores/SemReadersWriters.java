package MySemaphores;

import java.util.concurrent.Semaphore;

public class SemReadersWriters {
    public static  Semaphore db = new Semaphore(1);
    public static Semaphore mutex = new Semaphore(1);
    public static int rc = 0;

}
