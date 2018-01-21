import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math.*;
import java.awt.Color;
import java.util.Observable;
import java.util.Observer;
import javax.vecmath.*;

import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;

// the editable view of the terrain and landing pad
public class EditView extends JPanel implements Observer {

    private GameModel model;

    public int padx = 330;
    public int pady = 100;
    public int pressx = 0;
    public int pressy = 0;

    public int idx = 0;

    public Rectangle2D world;

    public EditView(GameModel model) {
        this.model = model;
        model.addObserver(this);
        this.world = model.getWorldBounds();
        // want the background to be black
        setBackground(Color.BLACK);
        model.setPad(330, 100);

        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    padx = e.getX() - 20;
                    pady = e.getY() - 5;
                    model.setPad(padx, pady);
                }
            }
        });

        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                pressx = e.getX();
                pressy = e.getY();
                idx = hittest(pressx,pressy);
                if (model.pad.contains(e.getX(), e.getY())) {
                    model.hitpad = true;
                   // System.out.println ("press undox " + model.undox + " undoy " + model.undoy);
                }
                else if (idx != 0) {
                    model.hitcircle = true;
                }
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (!world.contains(e.getX(),e.getY())) { return; }
                int offsetx = e.getX() - pressx;
                int offsety = e.getY() - pressy;
                if (model.hitpad) {
                    padx += offsetx;
                    pady += offsety;
                    model.setPad(padx, pady);
                }
                else if (model.hitcircle) {
                    model.setTerrain(e.getY(),idx);
                }
                pressx = e.getX();
                pressy = e.getY();
            }
        });
        this.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e) {
                model.hitpad = false;
                model.hitcircle = false;
                //model.doubleclick = false;
                //System.out.println ("release undox " + model.undox + " undoy " + model.undoy);
            }
        });
    }
    // set hit circle and return the circle (terrainx/terrainy array index) that's hit
    public int hittest(int x, int y) {
        double dis;
        int x1,y1,index;
        for (int i=1 ; i<=20; i++) {
            x1 = model.terrainx[i]; // circle centre
            y1 = model.terrainy[i];
            index = i;
            dis = Math.sqrt((x-x1) * (x-x1) + (y-y1) * (y-y1));
            if (dis < 15) {
                // hitcircle = true;
                return index;
            }
        }
        return 0;
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  // antialiasing look nicer
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.lightGray);
        g2.draw(world);
        g2.fill(world);

        g2.setColor(Color.darkGray);
        g2.drawPolygon(model.terrainx, model.terrainy, 22);
        g2.fillPolygon(model.terrainx, model.terrainy, 22);

        g2.setColor(Color.gray);
        for (int i = 1; i <= 20; i++) {
            g2.drawOval(model.terrainx[i] - 15, model.terrainy[i] - 15, 30, 30); //circle top-left corner
        }

        g2.setColor(Color.RED);
        g2.draw(model.pad);
        g2.fill(model.pad);

        return;
    }
    @Override
    public void update(Observable o, Object arg) {
        padx = model.padx;
        pady = model.pady;
        repaint();
    }

}
