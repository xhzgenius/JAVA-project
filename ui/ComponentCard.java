package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class ComponentCard extends JPanel {
    JLabel name;
    
    Box boxSouth;
    Box boxSouthLeft;
    Box boxSouthRight;

    ComponentCard() {
        super();

        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(96, 128));
        this.setBorder(new EmptyBorder(4, 4, 4, 4));

        boxSouth = new Box(BoxLayout.X_AXIS);
        boxSouthLeft = new Box(BoxLayout.X_AXIS);
        boxSouthRight = new Box(BoxLayout.X_AXIS);
        boxSouthLeft.setBorder(new EmptyBorder(4, 4, 4, 4));
        boxSouthRight.setBorder(new EmptyBorder(4, 4, 4, 4));
        boxSouth.setAlignmentX(Component.CENTER_ALIGNMENT);
        boxSouthLeft.setAlignmentX(Component.LEFT_ALIGNMENT);
        boxSouthRight.setAlignmentX(Component.RIGHT_ALIGNMENT);
        this.add(boxSouth, BorderLayout.SOUTH);
        boxSouth.add(boxSouthLeft);
        boxSouth.add(Box.createGlue());
        boxSouth.add(boxSouthRight);

        name = new JLabel();
        this.add(name, BorderLayout.NORTH);
    }
}