package ui;

import java.awt.*;
import javax.swing.*;

import logic.Fellow;

public class ComponentCardFellow extends ComponentCard {
    Fellow fellow;

    JLabel attack;
    JLabel level;
    JLabel health;

    ComponentCardFellow(Fellow fellow) {
        super();
        this.fellow = fellow;

        attack = new JLabel();
        level = new JLabel();
        health = new JLabel();

        this.boxSouthLeft.add(attack);
        this.boxSouthMid.add(level);
        this.boxSouthRight.add(health);

        this.name.setText(fellow.Name);
        this.attack.setText(fellow.Atk + "A");
        this.health.setText(fellow.Health + "HP");
        this.level.setText(String.format("[%d]", fellow.level));
        if(fellow.isGolden) {
            this.setBorder(BorderFactory.createLineBorder(new Color(246, 196, 48), 4));
        } else {
            this.setBorder(BorderFactory.createLineBorder(new Color(22, 119, 179), 4));
        }
    }

    public Fellow getFellow() {
        return fellow;
    }

}
