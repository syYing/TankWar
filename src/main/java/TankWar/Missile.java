
package TankWar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import TankWar.Tank.Direction;

public class Missile {

    private static final int XSPEED = 20;
    private static final int YSPEED = 20;
    public static final int WIDTH = 5;
    public static final int HEIGHT = 5;
    private static final int POWER = 20;

    private int x;
    private int y;
    private Direction dir;
    private boolean live = true;
    private boolean good = true;

    private TankClient tc;

    public Missile(int x, int y, Direction dir) {
        this.x = x;
        this.y = y;
        this.dir = dir;
    }

    public Missile(int x, int y, Direction dir, boolean good, TankClient tc) {
        this(x, y, dir);
        this.good = good;
        this.tc = tc;
    }

    public void draw(Graphics g) {
        if(!live) return;

        Color c = g.getColor();
        if(good) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.BLUE);
        }
        g.fillOval(x, y, WIDTH, HEIGHT);
        g.setColor(c);

        move();
    }

    private void move() {
        if(dir == Direction.L) {
            x -= XSPEED;
        }
        else if(dir == Direction.LU) {
            x -= XSPEED;
            y -= YSPEED;
        }
        else if(dir == Direction.U) {
            y -= YSPEED;
        }
        else if(dir == Direction.RU) {
            x += XSPEED;
            y -= YSPEED;
        }
        else if(dir == Direction.R) {
            x += XSPEED;
        }
        else if(dir == Direction.RD) {
            x += XSPEED;
            y += YSPEED;
        }
        else if(dir == Direction.D) {
            y += YSPEED;
        }
        else if(dir == Direction.LD) {
            x -= XSPEED;
            y += YSPEED;
        }

        if(x < 0 || x > TankClient.GAME_WIDTH || y < 0 || y > TankClient.GAME_HEIGHT) {
            live = false;
        }
    }

    public void hitTank(Tank tk) {
        if(tk.isLive() && good != tk.isGood() && this.getRect().intersects(tk.getRect())) {
            this.live = false;

            if(tk.isGood()) {
                int HP = tk.getHP() - POWER;
                if(HP < 0) {
                    tk.setLive(false);
                }
                tk.setHP(HP);
            } else {
                tk.setLive(false);
            }

            Explode e = new Explode(x, y, tc);
            tc.getExplodes().add(e);
        }
    }

    public void hitTanks(List<Tank> tanks) {
        for (int i = 0; i < tanks.size(); i++) {
            hitTank(tanks.get(i));
        }
    }

    public void hitWall(Wall w) {
        if(live && getRect().intersects(w.getRect())) {
            live = false;
            return;
        }
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    /**
     * @return boolean return the live
     */
    public boolean isLive() {
        return live;
    }
}
