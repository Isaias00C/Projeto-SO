package MyThreads;

import MySemaphores.SemReadersWriters;

public class Readers extends Thread{
    private String name;

    public Readers(String name) {
        super();
        this.name = name;
    }

    @Override
    public void run() {
        while(true) {


            // increase global flag
            try {
                SemReadersWriters.mutex.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            SemReadersWriters.rc += 1;
            if (SemReadersWriters.rc == 1) {
                try {
                    SemReadersWriters.db.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            SemReadersWriters.mutex.release();

            // init write process

            read();

            // decrease global flag
            try {
                SemReadersWriters.mutex.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            SemReadersWriters.rc -= 1;
            if (SemReadersWriters.rc == 0) SemReadersWriters.db.release();

            SemReadersWriters.mutex.release();


        }
    }

    private void read(){
        System.out.println(this.name + " is reading...");
    }
}
