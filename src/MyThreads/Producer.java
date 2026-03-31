package MyThreads;

import MySemaphores.SemProducerConsumer;

public class Producer extends Thread{
    private final String name;

    public Producer(String name) {
        super();
        this.name = name;
    }

    @Override
    public void run() {
        while(true){
            criar_item();

            try {
                SemProducerConsumer.empty.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try {
                SemProducerConsumer.mutex.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            adicionando_item();

            SemProducerConsumer.mutex.release();

            SemProducerConsumer.full.release();
        }
    }

    private void criar_item() {
        System.out.println(this.name + " Criando Item");
    }

    private void adicionando_item(){
        System.out.println(this.name + " adicionando item ao banco de dados");
    }


}
