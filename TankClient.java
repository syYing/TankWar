
package TankWar;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import TankWar.Tank.Direction;

public class TankClient extends Frame{

    public static void main(String[] args) {
        TankClient tc = new TankClient();
        tc.launchFrame();
    }
    /* 游戏屏幕大小 */
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;

    private Tank tk = new Tank(50, 50, true, this); // 主坦克
    private List<Tank> enemyTanks = new ArrayList<>();
    private List<Missile> missiles = new ArrayList<>();
    private List<Explode> explodes = new ArrayList<>();
    private Wall w1 = new Wall(200, 200, 200, 10, this);
    private Wall w2 = new Wall(400, 500, 20, 100, this);
    private Blood b = new Blood();

    private Random r = new Random();
    private Image offScreenImage = null; // 缓存图像

    public void launchFrame() {
        this.setLocation(300, 400);
        this.setSize(GAME_WIDTH, GAME_HEIGHT);
        this.setTitle("TankWar");
        this.setBackground(Color.GRAY);
        // 为关闭窗口添加响应
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e)  {
                System.exit(0);
            }
        });
        this.setResizable(false);
        this.setVisible(true);
        
        new Thread(new PaintThread()).start();
        this.addKeyListener(new KeyMonitor());
    }

    @Override
    public void paint(Graphics g) {
        /**
         * 画出主坦克
         * 如果主坦克存活，则画出来；否则提示游戏结束
         */
        if(tk.isLive()) {
            tk.draw(g);
            tk.hitWall(w1);
            tk.hitWall(w2);
        } else {
            g.drawString("Game Over, press R to revive!", GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2);
        }
        /**
         * 画出敌方坦克
         * 如果敌方坦克数量为0，则产生随即数量的敌方坦克
         */
        if(enemyTanks.size() == 0) {
            produceTank();
        }
        for (int i = 0; i < enemyTanks.size(); i++) {
            Tank enemy = enemyTanks.get(i);
            // 坦克之间的体积碰撞
            enemy.acrossWithTanks(enemyTanks); 
            if(!enemy.isLive()) {
                enemyTanks.remove(enemy);
            } else {
                enemy.draw(g);
                // 坦克与墙之间的体积碰撞
                enemy.hitWall(w1);
                enemy.hitWall(w2);
            }
        }

        for (int i = 0; i < missiles.size(); i++) {
            Missile ms = missiles.get(i);
            if(ms.isLive()) {
                ms.draw(g);
                ms.hitTanks(enemyTanks);
                ms.hitTank(tk);
                ms.hitWall(w1);
                ms.hitWall(w2);
            } else {
                missiles.remove(ms);
            }
        }

        for (int i = 0; i < explodes.size(); i++) {
            Explode e = explodes.get(i);
            if(e.isLive()) {
                e.draw(g);
            } else {
                explodes.remove(e);
            }
        }

        w1.draw(g);
        w2.draw(g);

        b.draw(g);
        // 如果主坦克吃掉血块，则重新生成血块
        if(tk.eatBlood(b)) {
            b = new Blood();
        }

        tk.acrossWithTanks(enemyTanks);
    }

    @Override
    public void update(Graphics g) {
        if(offScreenImage == null) {
            offScreenImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);
        }
        // 重新定义一个画布
        Graphics screen = offScreenImage.getGraphics();
        Color c = screen.getColor();
        screen.setColor(Color.GRAY);
        screen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        screen.setColor(c);
        paint(screen);
        g.drawImage(offScreenImage, 0, 0, null);
    }

    private class PaintThread implements Runnable {
        @Override
        public void run() {
            
            while(true) {
                // 每50ms重画一次
                repaint();
                try {
                    Thread.sleep(50);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class KeyMonitor extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            tk.keyPressed(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            tk.keyReleased(e);
        }
    }

    /**
     * 产生敌方坦克
     */
    public void produceTank() {
        int num = r.nextInt(7) + 3;

        for (int i = 0; i < num; i++) {
            int x = (r.nextInt(10) + 1) * 80;
            int y = (r.nextInt(10) + 1) * 60;
            Direction[] dirs = Direction.values();
            Direction dir = dirs[r.nextInt(dirs.length)];
            Tank enemy = new Tank(x, y, false, dir, dir, this);
            enemyTanks.add(enemy);
        }
    }

    /**
     * @return List<Missile> return the missiles
     */
    public List<Missile> getMissiles() {
        return missiles;
    }

    /**
     * @param missiles the missiles to set
     */
    public void setMissiles(List<Missile> missiles) {
        this.missiles = missiles;
    }

    /**
     * @return List<Explode> return the explodes
     */
    public List<Explode> getExplodes() {
        return explodes;
    }

    /**
     * @param explodes the explodes to set
     */
    public void setExplodes(List<Explode> explodes) {
        this.explodes = explodes;
    }

    public Tank getTank() {
        return tk;
    }

    public void setTank(Tank tk) {
        this.tk = tk;
    }
}
