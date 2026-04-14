import MyThreads.ChildThread;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CourtPanel extends JPanel {
    private int capacidade = 0;
    private CopyOnWriteArrayList<ChildThreadGUI> children = new CopyOnWriteArrayList<>();
    private int ballsInBasket = 0;

    // Colors matching the basketball court theme
    private static final Color COURT_FLOOR = new Color(205, 133, 63);
    private static final Color COURT_LINES = new Color(255, 255, 255, 180);
    private static final Color WALL_COLOR = new Color(40, 80, 100);
    private static final Color BLEACHERS = new Color(60, 100, 120);
    private static final Color ACCENT_RED = new Color(200, 50, 40);
    private static final Color ACCENT_BLUE = new Color(30, 60, 150);

    private Timer repaintTimer;

    public CourtPanel() {
        setBackground(WALL_COLOR);
        repaintTimer = new Timer(50, e -> repaint());
        repaintTimer.start();
    }

    public void setCapacidade(int cap) {
        this.capacidade = cap;
        ballsInBasket = cap; // start with basket full
        repaint();
    }

    public void addChild(ChildThreadGUI child) {
        children.add(child);
    }

    public void clearChildren() {
        children.clear();
        ballsInBasket = 0;
        capacidade = 0;
        repaint();
    }

    public synchronized void incrementBalls() {
        if (ballsInBasket < capacidade) ballsInBasket++;
        repaint();
    }

    public synchronized void decrementBalls() {
        if (ballsInBasket > 0) ballsInBasket--;
        repaint();
    }

    public int getBallsInBasket() { return ballsInBasket; }
    public int getCapacidade() { return capacidade; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        drawBackground(g2, w, h);
        drawBleachers(g2, w, h);
        drawCourt(g2, w, h);
        drawBasketGoals(g2, w, h);
        drawScoreboard(g2, w, h);
        drawBasket(g2, w, h);
        drawChildren(g2, w, h);
        drawBallsInfo(g2, w, h);
    }

    private void drawBackground(Graphics2D g2, int w, int h) {
        // Dark teal wall background
        GradientPaint wallGrad = new GradientPaint(0, 0, new Color(25, 55, 80), 0, h * 0.45f, new Color(40, 80, 100));
        g2.setPaint(wallGrad);
        g2.fillRect(0, 0, w, (int)(h * 0.45));

        // Ceiling lights
        for (int i = 0; i < 6; i++) {
            int lx = (int)(w * (0.1 + i * 0.16));
            g2.setColor(new Color(255, 240, 180, 60));
            g2.fillOval(lx - 30, -10, 60, 40);
            g2.setColor(new Color(255, 240, 180, 200));
            g2.fillOval(lx - 8, 5, 16, 16);
        }
    }

    private void drawBleachers(Graphics2D g2, int w, int h) {
        int courtTop = (int)(h * 0.45);
        // Bleacher rows behind court
        for (int row = 0; row < 5; row++) {
            int y = (int)(h * 0.12) + row * 22;
            g2.setColor(new Color(50 + row * 8, 85 + row * 5, 105 + row * 5));
            g2.fillRect(0, y, w, 22);
            // Seats
            for (int seat = 0; seat < w / 30; seat++) {
                int sx = seat * 30 + 5;
                g2.setColor(new Color(70 + row * 5, 110 + row * 5, 135 + row * 5));
                g2.fillRoundRect(sx, y + 3, 22, 16, 4, 4);
            }
        }
    }

    private void drawCourt(Graphics2D g2, int w, int h) {
        int courtTop = (int)(h * 0.45);
        int courtH = h - courtTop;

        // Floor
        GradientPaint floorGrad = new GradientPaint(0, courtTop, new Color(220, 150, 70), 0, h, new Color(185, 110, 45));
        g2.setPaint(floorGrad);
        g2.fillRect(0, courtTop, w, courtH);

        // Court lines
        g2.setColor(COURT_LINES);
        g2.setStroke(new BasicStroke(2f));

        // Center line
        g2.drawLine(w / 2, courtTop, w / 2, h);
        // Center circle
        int cx = w / 2, cy = courtTop + courtH / 2;
        g2.drawOval(cx - 60, cy - 60, 120, 120);
        // 3-point arcs
        g2.drawArc(40, courtTop + 30, 220, courtH - 60, -70, 140);
        g2.drawArc(w - 260, courtTop + 30, 220, courtH - 60, 110, 140);
        // Paint boxes
        g2.drawRect(0, courtTop + courtH / 2 - 80, 150, 160);
        g2.drawRect(w - 150, courtTop + courtH / 2 - 80, 150, 160);

        // Banner bottom
        g2.setColor(ACCENT_RED);
        g2.fillRect(w / 4, courtTop - 8, w / 2, 22);
        g2.setColor(ACCENT_BLUE);
        g2.fillRect(w / 4 - 4, courtTop - 8, 4, 22);
        g2.fillRect(w * 3 / 4, courtTop - 8, 4, 22);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Impact", Font.BOLD, 14));
        String bannerText = "  BASKETBALL  FORTALEZA  BASKETBALL  ";
        g2.drawString(bannerText, w / 4 + 10, courtTop + 9);
    }

    private void drawBasketGoals(Graphics2D g2, int w, int h) {
        int courtTop = (int)(h * 0.45);
        // Left hoop
        drawHoop(g2, 110, courtTop + (h - courtTop) / 2 - 40);
        // Right hoop
        drawHoop(g2, w - 110, courtTop + (h - courtTop) / 2 - 40);
    }

    private void drawHoop(Graphics2D g2, int cx, int cy) {
        // Backboard
        g2.setColor(new Color(240, 240, 240));
        g2.fillRect(cx - 30, cy - 50, 60, 45);
        g2.setColor(new Color(200, 50, 40));
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(cx - 30, cy - 50, 60, 45);
        g2.drawRect(cx - 15, cy - 38, 30, 22);

        // Pole
        g2.setColor(new Color(180, 180, 180));
        g2.setStroke(new BasicStroke(5));
        g2.drawLine(cx, cy - 5, cx, cy + 80);

        // Rim
        g2.setColor(new Color(255, 100, 20));
        g2.setStroke(new BasicStroke(4));
        g2.drawOval(cx - 22, cy - 5, 44, 12);

        // Net (simplified)
        g2.setColor(new Color(255, 255, 255, 180));
        g2.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < 5; i++) {
            g2.drawLine(cx - 18 + i * 9, cy + 6, cx - 12 + i * 7, cy + 30);
        }
        g2.drawLine(cx - 12, cy + 30, cx + 12, cy + 30);

        // Fortaleza logo placeholder
        g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
        g2.setColor(new Color(255, 200, 0));
        g2.drawString("FORTALEZA", cx - 22, cy - 55);
    }

    private void drawScoreboard(Graphics2D g2, int w, int h) {
        int sbW = 180, sbH = 90;
        int sbX = w / 2 - sbW / 2;
        int sbY = 15;

        // Board
        g2.setColor(new Color(20, 20, 20));
        g2.fillRoundRect(sbX, sbY, sbW, sbH, 12, 12);
        g2.setColor(ACCENT_RED);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(sbX, sbY, sbW, sbH, 12, 12);

        // Title
        g2.setColor(ACCENT_RED);
        g2.setFont(new Font("Impact", Font.BOLD, 14));
        g2.drawString("BASKETBALL", sbX + 20, sbY + 20);

        // Score display: balls in basket / capacity
        g2.setColor(new Color(255, 200, 50));
        g2.setFont(new Font("Courier New", Font.BOLD, 28));
        String score = String.format("%02d : %02d", ballsInBasket, capacidade);
        g2.drawString(score, sbX + 22, sbY + 60);

        // Labels
        g2.setColor(new Color(180, 180, 180));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        g2.drawString("CESTO    MAXIMO", sbX + 18, sbY + 78);
    }

    private void drawBasket(Graphics2D g2, int w, int h) {
        int courtTop = (int)(h * 0.45);
        int bx = w / 2 - 35;
        int by = courtTop + (h - courtTop) / 2 - 20;

        // Basket/bin
        g2.setColor(new Color(140, 70, 30));
        int[] bxPts = {bx, bx + 70, bx + 55, bx + 15};
        int[] byPts = {by, by, by + 70, by + 70};
        g2.fillPolygon(bxPts, byPts, 4);

        // Rim
        g2.setColor(new Color(100, 50, 20));
        g2.setStroke(new BasicStroke(4));
        g2.drawLine(bx, by, bx + 70, by);

        // Balls inside (show up to 5 visually)
        int show = Math.min(ballsInBasket, 5);
        for (int i = 0; i < show; i++) {
            int bx2 = bx + 12 + (i % 3) * 16;
            int by2 = by + 45 - (i / 3) * 15;
            drawBall(g2, bx2, by2, 12);
        }

        // Label
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.drawString("Cesto (" + ballsInBasket + "/" + capacidade + ")", bx - 5, by + 85);
    }

    private void drawBall(Graphics2D g2, int x, int y, int r) {
        // Orange ball
        g2.setColor(new Color(230, 100, 20));
        g2.fillOval(x, y, r * 2, r * 2);
        g2.setColor(new Color(180, 60, 10));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(x, y, r * 2, r * 2);
        // Lines on ball
        g2.drawLine(x + r, y, x + r, y + r * 2);
        g2.drawArc(x - r / 2, y + r / 3, r * 3, r, 0, 180);
        g2.drawArc(x - r / 2, y + r, r * 3, r, 180, 180);
    }

    private void drawChildren(Graphics2D g2, int w, int h) {
        int courtTop = (int)(h * 0.45);
        for (ChildThreadGUI child : children) {
            child.draw(g2, w, h, courtTop);
        }
    }

    private void drawBallsInfo(Graphics2D g2, int w, int h) {
        // Bottom info bar
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRect(0, h - 28, w, 28);
        g2.setColor(new Color(255, 200, 100));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.drawString("Threads ativas: " + children.size() + "   |   Bolas no cesto: " + ballsInBasket + "/" + capacidade, 16, h - 10);
    }
}
