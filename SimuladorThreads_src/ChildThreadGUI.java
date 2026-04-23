import MySemaphores.SemProducerConsumer;

import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

public class ChildThreadGUI extends Thread {
    public final String name;
    public Boolean comecou_com_bola;
    public int tempo_esperar;
    public int tempo_brincar;

    // Visual state
    private float x, y;
    private float targetX, targetY;
    private int state = 0; // 0=resting, 1=walking, 2=playing, 3=atBasket
    private boolean hasBall = false;
    private Color shirtColor;
    private CourtPanel courtPanel;
    private float walkSpeed = 2.5f;
    private static final Random RNG = new Random();

    // State string for label
    private String stateLabel = "esperando";

    // Corner position (assigned on creation)
    private float cornerX, cornerY;
    private float basketX, basketY;

    public ChildThreadGUI(String name, int tempo_brincar, int tempo_esperar, boolean comecou_com_bola, CourtPanel courtPanel) {
        this.name = name;
        this.tempo_brincar = tempo_brincar;
        this.tempo_esperar = tempo_esperar;
        this.comecou_com_bola = comecou_com_bola;
        this.courtPanel = courtPanel;
        this.hasBall = comecou_com_bola;
        this.shirtColor = new Color(RNG.nextInt(180) + 50, RNG.nextInt(80), RNG.nextInt(80) + 100);

        // Assign a random corner

        int side = RNG.nextInt(2);
        switch (side) {
            case 0: // Lado esquerdo
                cornerX = 0.05f + RNG.nextFloat() * 0.33f;  // 0.05 ~ 0.38
                cornerY = 0.10f + RNG.nextFloat() * 0.65f;  // 0.10 ~ 0.75
                break;
            case 1: // Lado direito
                cornerX = 0.62f + RNG.nextFloat() * 0.33f;  // 0.62 ~ 0.95
                cornerY = 0.10f + RNG.nextFloat() * 0.65f;  // 0.10 ~ 0.75
                break;
            //case 2: cornerX = RNG.nextFloat() ; cornerY = RNG.nextFloat()-0.25f ; break;
            //default: cornerX = RNG.nextFloat() ; cornerY = RNG.nextFloat()-0.25f ; break;
            //resultado = MINIMO + RNG.nextFloat() * (MAXIMO - MINIMO);
        }


        x = cornerX;
        y = cornerY;
        setDaemon(true);
    }

    public static void esperarCpuBound(int segundos) {
        long tempoLimite = System.currentTimeMillis() + (segundos * 1000L);
        while (System.currentTimeMillis() < tempoLimite) {
            
        }
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                if (comecou_com_bola || hasBall) {
                    // Has ball: play -> walk to basket -> deposit -> rest
                    stateLabel = "brincando ";
                    state = 2;
                    hasBall = true;
                    esperar(tempo_brincar);

                    // Walk to basket
                    stateLabel = "indo ao cesto";
                    state = 1;
                    walkToBasket();

                    try {
                        SemProducerConsumer.empty.acquire();
                    }catch(InterruptedException e){
                        throw new RuntimeException(e);
                    }
                    try {
                        SemProducerConsumer.mutex.acquire();
                    }catch (InterruptedException e){
                        throw new RuntimeException(e);
                    }
                    hasBall = false;
                    courtPanel.incrementBalls();
                    stateLabel = "devolveu bola";
                    SemProducerConsumer.mutex.release();
                    SemProducerConsumer.full.release();
                    // Walk back
                    walkToCorner();
                    stateLabel = "descansando";
                    state = 0;
                    esperar(tempo_esperar);
                    comecou_com_bola = false;
                } else {
                    // No ball: walk to basket -> wait for ball -> take -> play -> return
                    stateLabel = "indo ao cesto";
                    state = 1;
                    walkToBasket();
                    
                    stateLabel = "aguardando bola...";
                    try {
                        SemProducerConsumer.full.acquire();
                    } catch (InterruptedException e) { throw new RuntimeException(e); }
                    try {
                        SemProducerConsumer.mutex.acquire();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    hasBall = true;
                    courtPanel.decrementBalls();
                    stateLabel = "pegou bola!";
                    SemProducerConsumer.mutex.release();
                    SemProducerConsumer.empty.release();
                    walkToCorner();

                    stateLabel = "brincando";
                    state = 2;

                    esperar(tempo_brincar);

                    // Return ball
                    stateLabel = "indo ao cesto";
                    state = 1;
                    walkToBasket();


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
                    hasBall = false;
                    courtPanel.incrementBalls();
                    stateLabel = "devolveu bola";
                    SemProducerConsumer.mutex.release();
                    SemProducerConsumer.full.release();
                    walkToCorner();
                    stateLabel = "descansando";
                    state = 0;
                    esperar(tempo_esperar);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void walkToBasket() throws InterruptedException {
        // Basket is at center-ish
        basketX = 0.47f + RNG.nextFloat() * 0.06f;
        basketY = 0.5f + RNG.nextFloat() * 0.05f;

        moveTo(basketX, basketY);
        state=3;
        

    }
    


    private void walkToCorner() throws InterruptedException {
        moveTo(cornerX, cornerY);
    }

    private void moveTo(float tx, float ty) throws InterruptedException {
        targetX = tx;
        targetY = ty;
        while (Math.abs(x - targetX) > 0.01f || Math.abs(y - targetY) > 0.01f) {
            float dx = targetX - x;
            float dy = targetY - y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist < 0.008f) {
                x = targetX;
                y = targetY;
                break;
            }
            x += dx / dist * 0.008f;
            y += dy / dist * 0.008f;
            Thread.sleep(30);
        }
    }

    private void esperar(int segundos) throws InterruptedException {
        esperarCpuBound(segundos);
    }

    public void draw(Graphics2D g2, int w, int h, int courtTop) {
        int px = (int)(x * w);
        int py = courtTop + (int)(y * (h - courtTop));

        // Shadow
        g2.setColor(new Color(0, 0, 0, 60));
        g2.fillOval(px - 14, py + 40, 28, 8);

        Color corCamisa = shirtColor;

        if (state == 0) {
            float pulso = (float)(Math.sin(System.currentTimeMillis() / 400.0) * 0.5 + 0.5); // 0.0 ~ 1.0
            int r = (int)(shirtColor.getRed()   * pulso);
            int g = (int)(shirtColor.getGreen() * pulso);
            int b = (int)(shirtColor.getBlue()  * pulso);
            corCamisa = new Color(r, g, b);
        }
        // Body
        g2.setColor(corCamisa);
        g2.fillRoundRect(px - 10, py + 14, 20, 22, 6, 6);

        // Head
        g2.setColor(new Color(240, 190, 140));
        g2.fillOval(px - 10, py, 20, 20);

        // Hair
        g2.setColor(new Color(100, 60, 20));
        g2.fillArc(px - 10, py, 20, 14, 0, 180);

        // Eyes
        g2.setColor(new Color(40, 40, 40));
        g2.fillOval(px - 5, py + 7, 3, 3);
        g2.fillOval(px + 2, py + 7, 3, 3);

        // Legs
        //parado=0
        //andando=1
        //brincand=2
        g2.setColor(new Color(40, 40, 80));
        if (state == 1) {
            //andando
            long t = System.currentTimeMillis();

            int kick = (int)(Math.sin(t / 150.0) * 6);

            g2.setColor(Color.BLACK);
            g2.fillRoundRect(px - 9, py + 35, 8, 14 + kick, 4, 4);
            g2.fillRoundRect(px + 1, py + 35, 8, 14 - kick, 4, 4);

            if (hasBall) {
                int bx = px + 15;
                int by = (py + 10);
                g2.setColor(new Color(230, 100, 20));
                g2.fillOval(bx, by, 14, 14);
                g2.setColor(new Color(150, 50, 10));
                g2.setStroke(new BasicStroke(1f));
                g2.drawOval(bx, by, 14, 14);
                g2.drawLine(bx + 7, by, bx + 7, by + 14);
                g2.drawArc(bx - 3, by + 3, 20, 5, 0, 180);
            }
        }
        //quicando a bola
        if (state == 2) {
            long t = System.currentTimeMillis();
            int alturaQuique = 30;
            int bounce = (int)(Math.abs(Math.sin(t / 150.0)) * alturaQuique);
            
            g2.fillRoundRect(px - 9, py + 35, 8, 14, 4, 4);
            g2.fillRoundRect(px + 1, py + 35, 8, 14, 4, 4);

            if (hasBall) {
                int bx = px + 15;
                int by = (py + 10) + bounce;
                g2.setColor(new Color(230, 100, 20));
                g2.fillOval(bx, by, 14, 14);
                g2.setColor(new Color(150, 50, 10));
                g2.setStroke(new BasicStroke(1f));
                g2.drawOval(bx, by, 14, 14);
                g2.drawLine(bx + 7, by, bx + 7, by + 14);
                g2.drawArc(bx - 3, by + 3, 20, 5, 0, 180);
            }
        }

        if(state==0 || state == 3){
            g2.fillRoundRect(px - 9, py + 35, 8, 14, 4, 4);
            g2.fillRoundRect(px + 1, py + 35, 8, 14, 4, 4);
            
            if (hasBall) {
                int bx = px + 12;
                int by = py + 18;
                g2.setColor(new Color(230, 100, 20));
                g2.fillOval(bx, by, 14, 14);
                g2.setColor(new Color(150, 50, 10));
                g2.setStroke(new BasicStroke(1f));
                g2.drawOval(bx, by, 14, 14);
                g2.drawLine(bx + 7, by, bx + 7, by + 14);
                g2.drawArc(bx - 3, by + 3, 20, 5, 0, 180);
            }
    }


        // Name + state label
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        int nameW = fm.stringWidth(name);
        // Background pill
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRoundRect(px - nameW / 2 - 4, py - 22, nameW + 8, 14, 6, 6);
        g2.setColor(new Color(255, 230, 100));
        g2.drawString(name, px - nameW / 2, py - 11);

        // State label below
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        int slW = g2.getFontMetrics().stringWidth(stateLabel);
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(px - slW / 2 - 3, py + 52, slW + 6, 12, 4, 4);
        g2.setColor(new Color(180, 255, 180));
        g2.drawString(stateLabel, px - slW / 2, py + 62);
    }
}
