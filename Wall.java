
package TankWar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Wall {

    private int x;
    private int y;
    private int width;
    private int height;

    private TankClient tc;

    public Wall(int x, int y, int width, int height, TankClient tc) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.tc = tc;
    }

    public void draw(Graphics g) {
        Color c = g.getColor();
        g.setColor(Color.BLACK);
        g.fillRect(x, y, width, height);
        g.setColor(c);
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }
}
