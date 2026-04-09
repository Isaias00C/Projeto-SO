package MyThreads;

import MySemaphores.SemProducerConsumer;

public class ChildThread extends Thread{
    public final String name;
    public Boolean comecou_com_bola = false;
    public int tempo_esperar;
    public int tempo_brincar;
    //public int qtd_de_bolas = 0;

    public ChildThread(String name, int tempo_brincar, int tempo_esperar) {
        super();
        this.name = name;
        this.tempo_brincar = tempo_brincar;
        this.tempo_esperar = tempo_esperar;
    }

    public ChildThread(String name, int tempo_brincar, int tempo_esperar, Boolean comecou_com_bola) {
        this.name = name;
        this.tempo_brincar = tempo_brincar;
        this.tempo_esperar = tempo_esperar;
        this.comecou_com_bola = comecou_com_bola;
    }

    @Override
    public void run() {
        while(true){
            if (comecou_com_bola){
                brinca(tempo_brincar);

                descansa(tempo_esperar);

                caminha_ate_cesto();

                try {
                    SemProducerConsumer.mutex.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                devolve_bola();
                SemProducerConsumer.mutex.release();
                SemProducerConsumer.full.release();

                volta_para_canto();

                comecou_com_bola = false;
            }else {
                caminha_ate_cesto();

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
                pega_bola();
                SemProducerConsumer.mutex.release();
                SemProducerConsumer.empty.release();

                volta_para_canto();

                brinca(tempo_brincar);

                descansa(tempo_esperar);

                caminha_ate_cesto();

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
                devolve_bola();
                SemProducerConsumer.mutex.release();
                SemProducerConsumer.full.release();
            }
        }
    }

    private void volta_para_canto() {
        System.out.println("Criança " + this.name + " voltou para o seu canto");
    }

    private void pega_bola() {
        System.out.println("Criança " + this.name + " pegou uma bola");
    }

    private void devolve_bola() {
        System.out.println("Criança " + this.name + "devolveu a bola");
    }

    private void descansa(int tempo_descansar) {
        System.out.println("Criança " + this.name + " esta descansando");
        esperarCpuBound(tempo_descansar);
    }

    private void caminha_ate_cesto() {
        System.out.println("Criança " + this.name + " esta caminhando ate o cesto");
    }

    private void brinca(int tempo_brincar) {
        System.out.println("Criança " + this.name + " esta brincando");
        esperarCpuBound(tempo_brincar);
    }

    public static void esperarCpuBound(int segundos) {
        long tempoLimite = System.currentTimeMillis() + (segundos * 1000);

        System.out.println("Iniciando espera CPU Bound por " + segundos + "s...");

        // O processador vai fritar aqui dentro verificando a condição sem parar
        while (System.currentTimeMillis() < tempoLimite) {
            // Não faz nada, apenas gasta ciclo de CPU verificando a hora
        }

        System.out.println("Tempo esgotado! CPU liberada.");
    }
}
