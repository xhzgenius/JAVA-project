package ui;

import javax.swing.JFrame;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import logic.*;

public class UIFrame extends JFrame {
    JLayeredPane layeredPane = new JLayeredPane();
    JPanel canvas = new JPanel(null);
    ArrayList<UIBase> panels = new ArrayList<>();

    UIFrame() {
        canvas.setOpaque(false);
        layeredPane.add(canvas, JLayeredPane.DRAG_LAYER);

        this.add(layeredPane);
        this.setPreferredSize(new Dimension(1024, 768));
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    Dimension dim = UIFrame.this.getRootPane().getSize();
                    panels
                        .stream()
                        .forEach(panel -> {
                            panel.setSize(dim);
                            panel.revalidate();
                        });
                    canvas.setSize(dim);
                    UIFrame.this.repaint();
                });
                
            }

            @Override
            public void componentMoved(ComponentEvent e) {}

            @Override
            public void componentShown(ComponentEvent e) {}

            @Override
            public void componentHidden(ComponentEvent e) {}
        });
    }

    public void register(UIBase panel) {
        panels.add(panel);
        System.out.println(canvas == null);
        panel.putInFrame(this, canvas);
        layeredPane.add(panel, JLayeredPane.DEFAULT_LAYER);
    }

    public void toggle(JPanel toPanel) {
        panels.stream().forEach(panel -> {
            if (panel == toPanel) {
                panel.setVisible(true);
                panel.revalidate();
                panel.repaint();
            } else {
                panel.setVisible(false);
            }
        });
    }
}
