package MyThreads;

import MySemaphore.SemProducerConsumer;

public class Consumer extends  Thread{
    private final String name;

    public Consumer(String name) {
        super();
        this.name = name;
    }

    @Override
    public void run() {
        while(true){
            try {
                SemProducerConsumer.full.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try {
                SemProducerConsumer.mutex.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            retirar_item();

            SemProducerConsumer.mutex.release();

            SemProducerConsumer.empty.release();
        }
    }

    private void retirar_item() {
        System.out.println(this.name + " retirando item");
    }
}
