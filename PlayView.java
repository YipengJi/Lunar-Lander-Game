import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.ArrayList;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.lang.Math.*;

// the actual game view
public class PlayView extends JPanel implements Observer {

    private GameModel model;
    private Rectangle2D world;
    private Rectangle2D pad;

    private Ship ship;
    private Rectangle2D shipplay;

    private Polygon terrain;
    private Rectangle worldbound;

    public PlayView(GameModel model) {

        this.model = model;
        model.addObserver(this);

        this.ship = model.ship;
        ship.addObserver(this);
        this.world = model.getWorldBounds();

        this.pad = model.pad;
        // needs to be focusable for keylistener
        setFocusable(true);

        // want the background to be black
        setBackground(Color.BLACK);
        setLayout(new FlowLayout());

        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_W:
                        ship.thrustUp();
                        break;
                    case KeyEvent.VK_A:
                        ship.thrustLeft();
                        break;
                    case KeyEvent.VK_S:
                        ship.thrustDown();
                        break;
                    case KeyEvent.VK_D:
                        ship.thrustRight();
                        break;
                    case KeyEvent.VK_SPACE:
                        if(ship.crash || ship.safeland){
                            ship.reset(ship.startPosition);
                            ship.ifcrash(false);
                            ship.ifsafeland(false);
                            ship.setPaused(false);
                        }
                        else if(ship.isPaused()){
                            ship.setPaused(false);
                        }
                        else {
                            ship.setPaused(true);
                        }
                        break;
                }
                repaint();
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  // antialiasing look nicer
                RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform M = g2.getTransform();

        double centrex = ship.getPosition().getX();
        double centrey = ship.getPosition().getY();

       /* Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int W = screenSize.width;
        int H = screenSize.height;
        //System.out.println("W & H  " + W + " " + H);*/
        //centrex = this.getWidth()/4;
        //centrey = this.getHeight()/4;
        //System.out.println("centre  " + centrex + " " + centrey);
        //System.out.println("screensize  " + W + " " + H);

        g2.translate (centrex, centrey);
        g2.scale(3.0,3.0);
        g2.translate (centrex*(-1), centrey*(-1));

        // world
        g2.setColor(Color.lightGray);
        if (ship.crash) {
            g2.setColor(Color.black);
        }
        else if (ship.safeland) {
            g2.setColor(Color.orange);
        }
        g2.draw(world);
        g2.fill(world);

        // terrain
        g2.setColor(Color.darkGray);
        terrain = new Polygon (model.terrainx, model.terrainy, 22);
        g2.drawPolygon(terrain);
        g2.fillPolygon(terrain);

        // pad
        g2.setColor(Color.RED);
        g2.draw(pad);
        g2.fill(pad);

        // ship
        shipplay = ship.getShape();
        g2.setColor(Color.blue);
        if (ship.crash) {
            Random rand = new Random();
            for (int i=1; i<=3; i++) {
                float r = rand.nextFloat();
                float gg = rand.nextFloat();
                float b = rand.nextFloat();
                Color randomColor = new Color(r, gg, b);
                //randomColor.brighter();
                g2.setColor(randomColor);
                Rectangle2D.Double rect = new Rectangle2D.Double (shipplay.getX()-i*5,
                        shipplay.getY()+10-i*5, 5,5);
                g2.draw(rect);
                g2.fill(rect);
            }
            for (int i=1; i<=3; i++) {
                float r = rand.nextFloat();
                float gg = rand.nextFloat();
                float b = rand.nextFloat();
                Color randomColor = new Color(r, gg, b);
                //randomColor.brighter();
                g2.setColor(randomColor);
                Rectangle2D.Double rect = new Rectangle2D.Double (shipplay.getX()+i*5,
                        shipplay.getY()+10-i*5, 5,5);
                g2.draw(rect);
                g2.fill(rect);
            }
            for (int i=1; i<=4; i++) {
                float r = rand.nextFloat();
                float gg = rand.nextFloat();
                float b = rand.nextFloat();
                Color randomColor = new Color(r, gg, b);
                //randomColor.brighter();
                g2.setColor(randomColor);
                Rectangle2D.Double rect = new Rectangle2D.Double (shipplay.getX(),
                        shipplay.getY()+10-i*5, 5,5);
                g2.draw(rect);
                g2.fill(rect);
            }

        } else {
            g2.draw(shipplay);
            g2.fill(shipplay);
        }

        // firework
        if (ship.safeland) {
            Random rand = new Random();
            Random random = new Random();
            for (int i = 1; i <=200; i++) {
                float r = rand.nextFloat();
                float gg = rand.nextFloat();
                float b = rand.nextFloat();
                Color randomColor = new Color(r, gg, b);
                randomColor.brighter();
                g2.setColor(randomColor);
                Rectangle2D.Double rect = new Rectangle2D.Double(random.nextInt(700),
                        random.nextInt(200), 5, 5);
                g2.drawOval(random.nextInt(700), random.nextInt(200), 5, 5);
                g2.draw(rect);
            }
            for (int i = 1; i <=40; i++) {
                float r = rand.nextFloat();
                float gg = rand.nextFloat();
                float b = rand.nextFloat();
                Color randomColor = new Color(r, gg, b);
                randomColor.brighter();
                g2.setColor(randomColor);
                g2.drawOval(0,0,20*i,20*i);
            }
        }

        g2.setTransform(M);

        if ((terrain.intersects(shipplay) || !world.contains(shipplay)) && !ship.isPaused()) {
            ship.ifcrash(true);
        }
        else if (pad.intersects(shipplay) && !ship.isPaused()) {
            if (ship.getSafeLandingSpeed() > ship.getSpeed()) {
                ship.ifsafeland(true);
            }
            else {
                ship.ifcrash(true);
            }

        }

        return;
    }

    @Override
    public void update(Observable o, Object arg) {
        repaint();

    }
}
