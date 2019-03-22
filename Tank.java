
package TankWar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tank {

    private static final int WIDTH = 30;
    private static final int HEIGHT = 30;
    private static final int XSPEED = 5;
    private static final int YSPEED = 5;

    private int x, y;
    private int oldX, oldY;
    private boolean bL, bU, bR, bD;
    public enum Direction { L, LU, U, RU, R, RD, D, LD, STOP };
    private Direction dir = Direction.STOP;
    private Direction gunDir = Direction.D;
    private boolean good;
    private boolean live = true;
    private int HP = 100;

    private Random r = new Random();
    private int step = r.nextInt(7) + 3;

    private TankClient tc;

    public Tank(int x, int y, boolean good) {
        this.x = x;
        this.y = y;
        this.good = good;
    }

    public Tank(int x, int y, boolean good, TankClient tc) {
        this(x, y, good);
        this.tc = tc;
    }

    public Tank(int x, int y, boolean good, Direction dir, TankClient tc) {
        this(x, y, good);
        this.dir = dir;
        this.tc = tc;
    }

    public Tank(int x, int y, boolean good, Direction dir, Direction gunDir, TankClient tc) {
        this(x, y, good);
        this.dir = dir;
        this.gunDir = gunDir;
        this.tc = tc;
    }

    public void draw(Graphics g) {
        if(!live) return;

        if(!good) {
            if(step == 0) {
                Direction[] dirs = Direction.values();
                dir = dirs[r.nextInt(dirs.length)];
                gunDir = dir;
                step = r.nextInt(7) + 3;
            }
        }

        Color c = g.getColor();
        if(good) {
            g.setColor(Color.RED);
            bloodBar(g);
        } else {
            g.setColor(Color.BLUE);
        }
        g.fillOval(x, y, WIDTH, HEIGHT);
        g.setColor(c);
        drawGun(g);

        move();
        step--;

        if(!good && dir != Direction.STOP && r.nextInt(40) > 38) {
            tc.getMissiles().add(fire(gunDir));
        }
    }

    private void drawGun(Graphics g) {
        int centerX = x + WIDTH / 2;
        int centerY = y + HEIGHT / 2;

        if(gunDir == Direction.L) {
            g.drawLine(centerX, centerY, x, y + HEIGHT / 2);
        } else if(gunDir == Direction.LU){
            g.drawLine(centerX, centerY, x, y );
        } else if(gunDir == Direction.U){
            g.drawLine(centerX, centerY, x+ WIDTH/2, y );
        } else if(gunDir == Direction.RU){
            g.drawLine(centerX, centerY, x + WIDTH, y );
        } else if(gunDir == Direction.R){
            g.drawLine(centerX, centerY, x+ WIDTH, y + HEIGHT/2);
        } else if(gunDir == Direction.RD){
            g.drawLine(centerX, centerY, x+ WIDTH, y + HEIGHT);
        } else if(gunDir == Direction.D){
            g.drawLine(centerX, centerY, x+ WIDTH/2, y + HEIGHT);
        } else if(gunDir == Direction.LD){
            g.drawLine(centerX, centerY, x, y + HEIGHT);
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch(key) {
        case KeyEvent.VK_LEFT:
            bL = true;
            break;
        case KeyEvent.VK_UP:
            bU = true;
            break;
        case KeyEvent.VK_RIGHT:
            bR = true;
            break;
        case KeyEvent.VK_DOWN:
            bD = true;
            break;
        }

        moveDirection();
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch(key) {
        case KeyEvent.VK_R:
            produceMainTank();
            break;
        case 65:
            tc.getMissiles().add(fire(gunDir));
            break;
        case KeyEvent.VK_S:
            tc.getMissiles().addAll(superFire());
            break;
        case KeyEvent.VK_LEFT:
            bL = false;
            break;
        case KeyEvent.VK_UP:
            bU = false;
            break;
        case KeyEvent.VK_RIGHT:
            bR = false;
            break;
        case KeyEvent.VK_DOWN:
            bD = false;
            break;
        }
    }

    private void moveDirection() {
        if(bL && !bU && !bR && !bD) {
            dir = Direction.L;
        }
        else if(bL && bU && !bR && !bD) {
            dir = Direction.LU;
        }
        else if(!bL && bU && !bR && !bD) {
            dir = Direction.U;
        }
        else if(!bL && bU && bR && !bD) {
            dir = Direction.RU;
        }
        else if(!bL && !bU && bR && !bD) {
            dir = Direction.R;
        }
        else if(!bL && !bU && bR && bD) {
            dir = Direction.RD;
        }
        else if(!bL && !bU && !bR && bD) {
            dir = Direction.D;
        }
        else if(bL && !bU && !bR && bD) {
            dir = Direction.LD;
        }
        else{
            dir = Direction.STOP;
        }

        if(dir != Direction.STOP) {
            gunDir = dir;
        }
    }

    private void move() {
        oldX = x;
        oldY = y;

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

        dealTankBorder();
    }

    private void dealTankBorder() {
        if(x < 0) {
            x = 0;
        } else if(x > TankClient.GAME_WIDTH - WIDTH) {
            x = TankClient.GAME_WIDTH - WIDTH;
        }

        if(y < 0) {
            y = 0;
        } else if(y > TankClient.GAME_HEIGHT - HEIGHT) {
            y = TankClient.GAME_HEIGHT - HEIGHT;
        }
    }

    public Missile fire(Direction gunDir) {
        int x = this.x + this.WIDTH / 2 - Missile.WIDTH / 2;
        int y = this.y + this.HEIGHT / 2 - Missile.HEIGHT / 2;
        Missile ms = new Missile(x, y, gunDir, good, tc);
        return ms;
    }

    public List<Missile> superFire() {
        List<Missile> missiles = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            if(dir != Direction.STOP) {
                missiles.add(fire(dir));
            }
        }
        return missiles;
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    private void produceMainTank() {
        if(!tc.getTank().isLive()) {
            int x = r.nextInt(400) + 400;
            int y = r.nextInt(300) + 300;
            Tank newTank = new Tank(x, y, true, Direction.STOP, tc);
            tc.setTank(newTank);
        }
    }

    public void hitWall(Wall w) {
        if(live && getRect().intersects(w.getRect())) {
            x = oldX;
            y = oldY;
            return;
        }
    }

    private void bloodBar(Graphics g) {
        Color c = g.getColor();
        g.setColor(Color.GREEN);
        g.drawRect(x, y - 10, WIDTH, 5);
        int w = WIDTH * HP / 100;
        g.fillRect(x, y - 10, w, 5);
        g.setColor(c);
    }

    public boolean eatBlood(Blood b) {
        if(live && good && getRect().intersects(b.getRect())) {
            b.setLive(false);
            HP = 100;
            return true;
        } else {
            return false;
        }
    }

    public void acrossWithTank(Tank tk) {
        if(live && tk.isLive() && getRect().intersects(tk.getRect())) {
            if(good != tk.isGood()) {
                live = false;
                tk.setLive(false);
                Explode e = new Explode(x, y, tc);
                tc.getExplodes().add(e);
            } else {
                x = oldX;
                y = oldY;
                tk.setX(tk.getOldX());
                tk.setY(tk.getOldY());
            }
        }
    }

    public void acrossWithTanks(List<Tank> tanks) {
        for (Tank tk : tanks) {
            if(this != tk) {
                acrossWithTank(tk);
            }
        }
    }

        /**
     * @return int return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return int return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y) {
        this.y = y;
    }

    public int getOldX() {
        return oldX;
    }

    public int getOldY() {
        return oldY;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public boolean isGood() {
        return good;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }
}
