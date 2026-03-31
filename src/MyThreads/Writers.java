package MyThreads;

import MySemaphores.SemReadersWriters;

public class Writers extends Thread{
    private String name;
    public Writers(String name) {
        super();
        this.name = name;
    }

    @Override
    public void run() {
        while (true) {
            try {
                SemReadersWriters.db.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            write();
            SemReadersWriters.db.release();

        }


    }

    private void write(){
        System.out.println(this.name + " is writing...");

    }
}
