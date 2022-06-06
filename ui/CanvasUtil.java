package ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CanvasUtil {
    static <C extends Component> void promote(JPanel canvas, JFrame frame, C component) {
        Container parent = component.getParent();
        if(parent != canvas) {
            Point canvasPoint = frame.getContentPane().getLocationOnScreen();
            Point point = component.getLocationOnScreen();
            parent.remove(component);
            parent.revalidate();
            component.setBounds(
                point.x - canvasPoint.x,
                point.y - canvasPoint.y,
                component.getPreferredSize().width,
                component.getPreferredSize().height
            );
            canvas.add(component);
        }
    }

    static <C extends Component> void drag(JPanel canvas, JFrame frame, C component, MouseEvent e) {
        Point canvasPoint = frame.getContentPane().getLocationOnScreen();
        Point point = e.getLocationOnScreen();
        int width = component.getPreferredSize().width;
        int height = component.getPreferredSize().height;
        component.setBounds(
            point.x - canvasPoint.x - width / 2,
            point.y - canvasPoint.y - height / 2,
            width,
            height
        );
        canvas.repaint();
    }

    static Point getAbsoluteCenter(Component component) {
        Point topLeftPoint = component.getLocationOnScreen();
        return new Point(
            topLeftPoint.x + component.getWidth() / 2,
            topLeftPoint.y + component.getHeight() / 2
        );
    }

    static Point getRelativeTopLeft(Component component, Component relativeTo) {
        Point topLeftPoint = component.getLocationOnScreen();
        Point relativeTopLeftPoint = relativeTo.getLocationOnScreen();
        return new Point(
            topLeftPoint.x - relativeTopLeftPoint.x,
            topLeftPoint.y - relativeTopLeftPoint.y
        );
    }

    static Rectangle getAbsoluteBounds(Component component) {
        Point topLeftPoint = component.getLocationOnScreen();
        return new Rectangle(
            topLeftPoint.x,
            topLeftPoint.y,
            component.getWidth(),
            component.getHeight()
        );
    }
}
