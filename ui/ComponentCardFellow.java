package ui;

import java.awt.*;
import javax.swing.*;

import logic.Fellow;

public class ComponentCardFellow extends ComponentCard {
    Color mycolorBrown = new Color(94, 38, 18);
    Color mycolorBackground = new Color(210, 180, 140);
    Color mycolorBorder = new Color(163, 148, 128);
    Color mycolorGolden = new Color(255, 215, 0);
    Font myfontChinese = new Font("隶书", Font.BOLD, 12);
    Font myfontNumber = new Font("Ink free", Font.BOLD, 18);
    Font myfontEnglish = new Font("Ink free", Font.BOLD, 14);
    Font myfontStars = new Font("", Font.BOLD, 7);

    Fellow fellow;

    JLabel attack;
    JLabel level;
    JLabel health;

    ComponentCardFellow(Fellow fellow) {
        super();
        this.fellow = fellow;

        attack = new JLabel();
        attack.setFont(myfontNumber);
        attack.setForeground(mycolorBrown);
        level = new JLabel();
        level.setFont(myfontStars);
        level.setForeground(mycolorBrown);
        health = new JLabel();
        health.setFont(myfontNumber);
        health.setForeground(mycolorBrown);

        this.boxSouthLeft.add(attack);
        this.boxSouthMid.add(level);
        this.boxSouthRight.add(health);

        this.name.setFont(myfontEnglish);
        this.name.setForeground(mycolorBrown);
        this.name.setText(String.format("<html><body><span style=\"white-space: normal; text-align: center;\">%s<br><span style=\"font-size: 0.75em\">%s<br>%s</span></span></body></html>", fellow.Name, fellow.Type, fellow.Description));
        this.attack.setText(fellow.Atk + "");
        this.health.setText(fellow.Health + "");
        String stars = "";
        for(int i = 0;i<fellow.level;i++) stars += "\u2606";
        this.level.setText(stars);
        if(fellow.isGolden) {
            this.setBorder(BorderFactory.createLineBorder(mycolorGolden, 4));
        } else {
            this.setBorder(BorderFactory.createLineBorder(mycolorBorder, 4));
        }
        this.setBackground(mycolorBackground);
    }

    public Fellow getFellow() {
        return fellow;
    }

}
