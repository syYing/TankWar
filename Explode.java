
package TankWar;

import java.awt.Color;
import java.awt.Graphics;

public class Explode {

    private int x;
    private int y;
    private int[] diameter = {6, 20, 40, 60, 20, 7};
    private int step = 0;
    private boolean live = true;

    private TankClient tc;

    public Explode(int x, int y, TankClient tc) {
        this.x = x;
        this.y = y;
        this.tc = tc;
    }

    public void draw(Graphics g) {
        if(!live) return;

        if(step >= diameter.length) {
            live = false;
            step = 0;
            return;
        }
        Color c = g.getColor();
        g.setColor(Color.YELLOW);
        g.fillOval(x, y, diameter[step], diameter[step]);
        g.setColor(c);
        step++;
    }

    public boolean isLive() {
        return live;
    }
}
