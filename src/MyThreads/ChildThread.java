package MyThreads;

public class ChildThread extends Thread{
    public final String name;
    public Boolean comecou_com_bola = false;

    public ChildThread(String name) {
        super();
        this.name = name;
    }

    public ChildThread(String name, Boolean comecou_com_bola) {
        this.name = name;
        this.comecou_com_bola = comecou_com_bola;
    }

    @Override
    public void run() {
        while(true){
            if (comecou_com_bola){
                brinca();

                descansa();

                caminha_ate_cesto();

                devolve_bola();

                comecou_com_bola = false;
            }else {
                caminha_ate_cesto();

                pega_bola();

                volta_para_canto();

                brinca();

                descansa();

                caminha_ate_cesto();

                devolve_bola();
            }
        }
    }

    private void volta_para_canto() {
    }

    private void pega_bola() {
    }

    private void devolve_bola() {
    }

    private void descansa() {
    }

    private void caminha_ate_cesto() {
    }

    private void brinca() {
    }
}
