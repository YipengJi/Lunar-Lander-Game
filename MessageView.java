import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;
import java.text.DecimalFormat;

public class MessageView extends JPanel implements Observer {

    private GameModel model;
    private Ship ship;

    // status messages for game
    JLabel fuel = new JLabel("fuel");
    JLabel speed = new JLabel("speed");
    JLabel message = new JLabel("message");

    public MessageView(GameModel model) {

        this.model = model;
        this.ship = model.ship;
        model.addObserver(this);

        // want the background to be black
        setBackground(Color.BLACK);

        setLayout(new FlowLayout(FlowLayout.LEFT));

        add(fuel);
        add(speed);
        add(message);

        fuel.setText("Fuel: " + ship.getFuel());
        speed.setText("Speed: " + ship.getSpeed());
        message.setText("");

        for (Component c: this.getComponents()) {
            c.setForeground(Color.WHITE);
            c.setPreferredSize(new Dimension(100, 20));
        }
        speed.setForeground(Color.green);
    }


    @Override
    public void update(Observable o, Object arg) {
        fuel.setText("Fuel: " + ship.getFuel());
        DecimalFormat df = new DecimalFormat("0.00");
        String d = df.format(ship.getSpeed());
        speed.setText("Speed: " + d);
        if (ship.getFuel() < 10) {
            fuel.setForeground(Color.red);
        } else {
            fuel.setForeground(Color.white);
        }
        if (ship.getSpeed() <= ship.getSafeLandingSpeed()) {
            speed.setForeground(Color.green);
        } else {
            speed.setForeground(Color.white);
        }

        if (ship.crash) {
            message.setText("CRASH");
        }
        else if (ship.safeland){
            message.setText("LANDED!");
        }
        else if (ship.isPaused() && (ship.getSpeed() != 0)) {
            message.setText("(Paused)");
        }
        else {
            message.setText("");
        }
    }
}