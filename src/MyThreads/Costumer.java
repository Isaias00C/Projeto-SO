package MyThreads;

import MySemaphores.SemBarbershop;

public class Costumer extends Thread{
    private final String name;
    private volatile boolean isRunning = true;

    public Costumer(String name) {
        super();
        this.name = name;
    }

    @Override
    public void run() {
        while(isRunning){
            try {
                SemBarbershop.mutex.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(SemBarbershop.n_chairs == 0){
                SemBarbershop.mutex.release();
                balk();
                continue;
            }

            SemBarbershop.n_chairs -= 1;

            SemBarbershop.mutex.release();

            SemBarbershop.costumers.release();

            try {
                SemBarbershop.barber.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            get_hair_cut();

            try {
                SemBarbershop.mutex.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            SemBarbershop.n_chairs += 1;

            SemBarbershop.mutex.release();

        }
    }

    private void get_hair_cut(){
        System.out.println("cabelo do " + this.name +  " esta sendo cortado");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void balk(){
        System.out.println("arrives the barbershop");
        this.isRunning = false;

    }
}
