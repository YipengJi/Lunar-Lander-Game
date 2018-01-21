/* A3 Enhancement
1. When the ship crashes (landing pad, terrains and world bounds), the world turns black
and the ship has a colourful explosion at the crashing position.
Explosion colour changes randomly when pressing any keys, resizing the window and motifing landingpad and terrains.

2. When the ship successfully landed on the landing pad, the world turns orange and fireworks rain from the top.
Colour of the firework can be changed randomly by any key or mouse events.
 */

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class LunarLander extends JPanel {

    LunarLander() {
        // create the model
        GameModel model = new GameModel(60, 700, 200, 20);

        JPanel playView = new PlayView(model);
        JPanel editView = new EditView(model);
        editView.setPreferredSize(new Dimension(700, 200));

        // layout the views
        setLayout(new BorderLayout());

        add(new MessageView(model), BorderLayout.NORTH);

        // nested Border layout for edit view
        JPanel editPanel = new JPanel();
        editPanel.setLayout(new BorderLayout());
        editPanel.add(new ToolBarView(model), BorderLayout.NORTH);
        editPanel.add(editView, BorderLayout.CENTER);
        add(editPanel, BorderLayout.SOUTH);

        // main playable view will be resizable
        add(playView, BorderLayout.CENTER);

        // for getting key events into PlayView
        playView.requestFocusInWindow();

    }

    public static void main(String[] args) {
        // create the window
        JFrame f = new JFrame("LunarLander"); // jframe is the app window
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(700, 600); // window size
        f.setContentPane(new LunarLander()); // add main panel to jframe
        f.setVisible(true); // show the window
    }
}
