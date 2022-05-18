package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

import logic.*;

public class UIBase extends JPanel {
    Box boxTop;

    Box boxBottom;
    JLabel health;

    UIBase() {
        super();
        this.setLayout(new BorderLayout());

        boxTop = new Box(BoxLayout.X_AXIS);
        boxTop.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(boxTop, BorderLayout.NORTH);

        boxBottom = new Box(BoxLayout.X_AXIS);
        boxBottom.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(boxBottom, BorderLayout.SOUTH);
        health = new JLabel();

        boxBottom.add(health);
    }

    public void render(Game game) {
        setHealth(game);
    }

    public void register() {
        
    }

    public void setHealth(Game game) {
        this.health.setText(String.format("HP: %d", game.getHealth(game.SELF_PLAYER_ID)));
    }
    
}
