package ui;

import java.awt.*;
import java.awt.event.*;
import java.util.stream.IntStream;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

public class ContainerDeck<C extends Component> extends JPanel {
    private Box box;
    private JPanel canvas;
    private JFrame frame;
    ContainerDeck(JPanel canvas, JFrame frame) {
        super();
        box = new Box(BoxLayout.X_AXIS);
        super.add(box);
        box.add(Box.createHorizontalGlue());
        box.add(Box.createHorizontalGlue());

        this.canvas = canvas;
        this.frame = frame;
    }

    public void put(C component) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBorder(new EmptyBorder(4, 4, 4, 4));
        item.add(component);
        MouseMotionAdapter mouseMotionAdapter = new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point canvasPoint = frame.getContentPane().getLocationOnScreen();
                Point point = e.getLocationOnScreen();
                int width = item.getPreferredSize().width;
                int height = item.getPreferredSize().height;
                item.setBounds(
                    point.x - canvasPoint.x - width / 2,
                    point.y - canvasPoint.y - height / 2,
                    width,
                    height
                );
                canvas.repaint();
            }
        };
        MouseInputAdapter mouseInputAdapter = new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Container parent = item.getParent();
                if(parent != canvas) {
                    Point canvasPoint = frame.getContentPane().getLocationOnScreen();
                    Point point = item.getLocationOnScreen();
                    parent.remove(item);
                    parent.revalidate();
                    item.setBounds(
                        point.x - canvasPoint.x,
                        point.y - canvasPoint.y,
                        item.getPreferredSize().width,
                        item.getPreferredSize().height
                    );
                    canvas.add(item);
                }
                item.addMouseMotionListener(mouseMotionAdapter);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                item.removeMouseMotionListener(mouseMotionAdapter);
                Point dropPoint = getAbsoluteCenter(item);
                Component[] components = box.getComponents();
                int destination = IntStream.range(1, components.length - 1).filter(i -> {
                    Point componentPoint = getAbsoluteCenter(components[i]);
                    return componentPoint.x >= dropPoint.x;
                }).findFirst().orElse(components.length - 1);
                canvas.remove(item);
                box.add(item, destination);
                box.revalidate();
                box.repaint();
                canvas.repaint();
            }
        };
        item.addMouseListener(mouseInputAdapter);
        box.add(item, box.getComponentCount() - 1);
        box.revalidate();
        box.repaint();
    }

    public void clear() {
        box.removeAll();
        box.add(Box.createHorizontalGlue());
        box.add(Box.createHorizontalGlue());
        box.revalidate();
        box.repaint();
    }

    static Point getAbsoluteCenter(Component component) {
        Point topLeftPoint = component.getLocationOnScreen();
        return new Point(
            topLeftPoint.x + component.getWidth() / 2,
            topLeftPoint.y + component.getHeight() / 2
        );
    }
}
