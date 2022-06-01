package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EmptyBorder;

import logic.*;

public class UIBase extends JPanel {
    Box boxTop;
    Box boxTopLeft;
    Box boxTopMid;
    Box boxTopRight;

    Box boxBottom;
    Box boxBottomLeft;
    Box boxBottomMid;
    Box boxBottomRight;

    JLabel health;

    UIBase() {
        super();
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(1024, 768));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        boxTop = new Box(BoxLayout.X_AXIS);
        boxTopLeft = new Box(BoxLayout.X_AXIS);
        boxTopMid = new Box(BoxLayout.X_AXIS);
        boxTopRight = new Box(BoxLayout.X_AXIS);
        boxTop.setAlignmentX(Component.CENTER_ALIGNMENT);
        boxTopLeft.setAlignmentX(Component.LEFT_ALIGNMENT);
        boxTopMid.setAlignmentX(Component.CENTER_ALIGNMENT);
        boxTopRight.setAlignmentX(Component.RIGHT_ALIGNMENT);
        this.add(boxTop, BorderLayout.NORTH);
        boxTop.add(boxTopLeft);
        boxTop.add(Box.createGlue());
        boxTop.add(boxTopMid);
        boxTop.add(Box.createGlue());
        boxTop.add(boxTopRight);

        boxBottom = new Box(BoxLayout.X_AXIS);
        boxBottomLeft = new Box(BoxLayout.X_AXIS);
        boxBottomMid = new Box(BoxLayout.X_AXIS);
        boxBottomRight = new Box(BoxLayout.X_AXIS);
        boxBottom.setAlignmentX(Component.CENTER_ALIGNMENT);
        boxBottomLeft.setAlignmentX(Component.LEFT_ALIGNMENT);
        boxBottomMid.setAlignmentX(Component.CENTER_ALIGNMENT);
        boxBottomRight.setAlignmentX(Component.RIGHT_ALIGNMENT);
        this.add(boxBottom, BorderLayout.SOUTH);
        boxBottom.add(boxBottomLeft);
        boxBottom.add(Box.createGlue());
        boxBottom.add(boxBottomMid);
        boxBottom.add(Box.createGlue());
        boxBottom.add(boxBottomRight);

        health = new JLabel();
        boxBottomMid.add(health);
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
