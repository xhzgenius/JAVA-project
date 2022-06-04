package ui;

import java.awt.*;
import javax.swing.*;

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

        this.name.setText(String.format("<html><body style=\"color: blue;\">%s</body></html>", fellow.Name));
        this.attack.setText(fellow.Atk + "");
        this.health.setText(fellow.Health + "");
        this.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLACK));
    }

    public Fellow getFellow() {
        return fellow;
    }
}
