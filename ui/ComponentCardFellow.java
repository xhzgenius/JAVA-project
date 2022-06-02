package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import logic.Fellow;

public class ComponentCardFellow extends ComponentCard {
    Fellow fellow;

    JLabel attack;
    JLabel health;

    ComponentCardFellow(Fellow fellow) {
        super();
        this.fellow = fellow;

        attack = new JLabel();
        health = new JLabel();

        this.boxSouthLeft.add(attack);
        this.boxSouthRight.add(health);

        this.name.setText(String.format("<html><body style=\"color: blue;\">%s</body></html>", fellow.getName()));
        this.attack.setText(fellow.getAttack().toString());
        this.health.setText(fellow.getHealth().toString());
        this.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLACK));
    }
}
