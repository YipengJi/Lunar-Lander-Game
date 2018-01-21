import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.lang.reflect.Field;
import javax.swing.undo.*;
import javax.vecmath.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math.*;

public class GameModel extends Observable {
    // Undo manager
    private UndoManager undoManager;

    public int padx = 330;
    public int pady = 100;
    public int offsetx = 0;
    public int offsety = 0;

    public int[] terrainx = new int[22];
    public int[] terrainy = new int[22];

    public int index = 0;

    public boolean hitpad = false;
    public boolean hitcircle = false;

    public GameModel(int fps, int width, int height, int peaks) {

        undoManager = new UndoManager();

        //////
        Random random = new Random();
        for (int i=1; i<=20; i++) {
            //   terrainx[i] = random.nextInt(700);
            terrainx[i] = i*35;
            terrainy[i] = random.nextInt(80) + 100;
        }

        // 0 (0,0) 21 (700,0) 1 (0,y) 20 (700,y)
        terrainx[0] = 0;
        terrainx[1] = 0;
        terrainx[20] = 700;
        terrainx[21] = 700;
        terrainy[0] = 200;
        terrainy[21] = 200;

        pad = new Rectangle2D.Double(padx,pady,40,10);

        ship = new Ship(60, width/2, 50);

        worldBounds = new Rectangle2D.Double(0, 0, width, height);

        // anonymous class to monitor ship updates
        ship.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                setChangedAndNotify();
            }
        });
    }

    public int getIndex () { return index;}

    public int getPadx() { return padx;}
    public int getPady() { return pady;}

    public void setPad (int x, int y) {
        //    System.out.println("before Model: set pad to " + x + " " + y);

        // create undoable edit
        UndoableEdit undoableEdit = new AbstractUndoableEdit() {

            // capture variables for closure
            final int oldx = padx;
            final int newx = x;
            final int oldy = pady;
            final int newy = y;

            // Method that is called when we must redo the undone action
            public void redo() throws CannotRedoException {
                super.redo();
                padx = newx;
                pady = newy;
                //System.out.println("Model: redo newxy " + newx + " " + newy);
                pad.setRect(padx,pady,40,10);
                //System.out.println("Model: redo value to " + padx + " " + pady);
                setChangedAndNotify();
            }

            public void undo() throws CannotUndoException {
                super.undo();
                padx = oldx;
                pady = oldy;
                //System.out.println("Model: undo oldxy " + oldx + " " + oldy);
                //System.out.println("Model: undo value to " + padx + " " + pady);
                pad.setRect(padx,pady,40,10);
                setChangedAndNotify();
            }
        };

        // Add this undoable edit to the undo manager
        undoManager.addEdit(undoableEdit);

        // finally, set the value and notify views
        padx = x;
        pady = y;
        pad.setRect(padx,pady,40,10);
        //System.out.println("after Model: set pad to " + padx + " " + pady);
        setChangedAndNotify();

    }


    public void setTerrain(int tcy, int idx) {
        //  System.out.println("before Model: set tc to " + terrainy[idx]);

        // create undoable edit
        UndoableEdit undoableEdit = new AbstractUndoableEdit() {

            // capture variables for closure
            final int oldtc = terrainy[idx];
            final int newtc = tcy;

            // Method that is called when we must redo the undone action
            public void redo() throws CannotRedoException {
                super.redo();
                index = idx;
                terrainy[idx] = newtc;
                // System.out.println("Model: redo tc " + terrainy[idx]);
                setChangedAndNotify();
            }

            public void undo() throws CannotUndoException {
                super.undo();
                index = idx;
                terrainy[idx] = oldtc;
                // System.out.println("Model: undo tc " + terrainy[idx]);
                setChangedAndNotify();
            }
        };

        // Add this undoable edit to the undo manager
        undoManager.addEdit(undoableEdit);

        // finally, set the value and notify views
        terrainy[idx] = tcy;
        //  System.out.println("after Model: set tc to " + terrainy[idx]);
        setChangedAndNotify();

    }
    // World
    // - - - - - - - - - - -
    public final Rectangle2D getWorldBounds() {
        return worldBounds;
    }

    Rectangle2D.Double worldBounds;


    // Ship
    // - - - - - - - - - - -

    public Ship ship;

    // Pad
    // - - - - - - - - - - -
    public Rectangle2D.Double getPad() {
        return pad;
    }

    Rectangle2D.Double pad;

    // Observerable
    // - - - - - - - - - - -

    // helper function to do both
    void setChangedAndNotify() {
        setChanged();
        notifyObservers();
    }

    // undo and redo methods
    // - - - - - - - - - - - - - -

    public void undo() {
        if (canUndo())
            undoManager.undo();
    }

    public void redo() {
        if (canRedo())
            undoManager.redo();
    }

    public boolean canUndo() {
        return undoManager.canUndo();
    }

    public boolean canRedo() {
        return undoManager.canRedo();
    }


}



