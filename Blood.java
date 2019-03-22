
package TankWar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

public class  Blood {

    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;

    private Random r = new Random();
    private int x = r.nextInt(750);
    private int y = r.nextInt(650);
    private boolean live = true;

    public void draw(Graphics g) {
        if(!live) return;
        Color c = g.getColor();
        g.setColor(Color.ORANGE);
        g.fillRect(x, y, WIDTH, HEIGHT);
        g.setColor(c);
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }
}
