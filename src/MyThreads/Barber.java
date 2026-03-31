package MyThreads;

import MySemaphores.SemBarbershop;

public class Barber extends Thread{
    private final String name;
    public Barber(String name) {
        super();
        this.name = name;
    }

    @Override
    public void run() {
        while(true){
            try {
                SemBarbershop.costumers.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            SemBarbershop.barber.release();

            cut_hair();

        }
    }

    private void cut_hair(){
        System.out.println(name + " esta cortando o cabelo do cliente");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
