package ui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import logic.Fellow;

public class ContainerDeck<C extends Component> extends JPanel {
    private Box box;
    private JPanel canvas;
    private JFrame frame;
    private ArrayList<DropTo<C>> dropToList;
    private Function<C, Boolean> submit;
    private Runnable propagateRerender;
    // private Boolean ordered;

    ContainerDeck(JPanel canvas, JFrame frame) {
        super();
        box = new Box(BoxLayout.X_AXIS);
        super.add(box);
        box.add(Box.createHorizontalGlue());
        box.add(Box.createHorizontalGlue());

        this.canvas = canvas;
        this.frame = frame;
        this.dropToList = new ArrayList<>();
        this.submit = (_component) -> true;
        this.propagateRerender = () -> {};
    }

    public ArrayList<DropTo<C>> getDropToList() {
        return dropToList;
    }

    public void setSubmit(Function<C, Boolean> submit) {
        this.submit = submit;
    }

    public void setPropagateRerender(Runnable propagateRerender) {
        this.propagateRerender = propagateRerender;
    }

    /**
     * 在 Deck 中最后的位置插入一个元素，且不向 Game 提交修改
     * 
     * @param component
     */
    public Boolean put(C component) {
        return put(component, false, false);
    }

    /**
     * 在 Deck 中插入一个元素
     * 
     * @param component 要插入的元素
     * @param positioned 根据当前位置决定插入的坐标
     * @param shouldSubmit 是否向 Game 提交修改
     */
    public Boolean put(C component, boolean positioned, boolean shouldSubmit) {
        if(shouldSubmit) {
            boolean ok = submit.apply(component);
            if(!ok) return false;
        }
        MouseMotionAdapter mouseMotionAdapter = new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
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
        };
        MouseInputAdapter mouseInputAdapter = new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
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
                component.addMouseMotionListener(mouseMotionAdapter);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                component.removeMouseMotionListener(mouseMotionAdapter);
                Rectangle itemHitbox = getAbsoluteBounds(component);
                Runnable putBack = () -> {
                    Component[] components = box.getComponents();
                    int destination = getInsertionIndex(component, components, 1, components.length - 1);
                    // box.add(component, destination);
                    // box.revalidate();
                    // box.repaint();
                    canvas.remove(component);
                };
                dropToList
                    .stream()
                    .filter(dropTo -> {
                        Component target = dropTo.getTarget();
                        Rectangle targetHitbox = getAbsoluteBounds(target);
                        return targetHitbox.intersects(itemHitbox);
                    })
                    .findFirst()
                    .ifPresentOrElse(
                        dropTo -> {
                            // item.remove(component);
                            Component target = dropTo.getTarget();
                            boolean ok = dropTo.dropTo(ContainerDeck.this, component, target);
                            if(ok) {
                                canvas.remove(component);
                            } else {
                                putBack.run();
                            }
                        },
                        putBack
                    );
                canvas.repaint();
                propagateRerender.run();
            }
        };
        component.addMouseListener(mouseInputAdapter);
        int destination;
        if(positioned) {
            destination = getInsertionIndex(component, box.getComponents(), 1, box.getComponentCount() - 1);
        } else {
            destination = box.getComponentCount() - 1;
        }

        box.add(component, destination);
        box.revalidate();
        box.repaint();
        return true;
    }

    public void clear() {
        box.removeAll();
        box.add(Box.createHorizontalGlue());
        box.add(Box.createHorizontalGlue());
        box.revalidate();
        box.repaint();
    }

    public Fellow[] getFellows() {
        Component[] components = box.getComponents();
        return Arrays.stream(components).filter(component -> ComponentCardFellow.class.isInstance(component)).map(card -> ((ComponentCardFellow)card).getFellow()).toArray(Fellow[]::new);
    }

    static Point getAbsoluteCenter(Component component) {
        Point topLeftPoint = component.getLocationOnScreen();
        return new Point(
            topLeftPoint.x + component.getWidth() / 2,
            topLeftPoint.y + component.getHeight() / 2
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

    static int getInsertionIndex(Component item, Component[] components, int startInclusive, int endExclusive) {
        Point dropPoint = getAbsoluteCenter(item);
        int destination = IntStream.range(startInclusive, endExclusive).filter(i -> {
            Point componentPoint = getAbsoluteCenter(components[i]);
            return componentPoint.x >= dropPoint.x;
        }).findFirst().orElse(endExclusive);
        return destination;
    }

    class DropToThis implements DropTo<C> {
        public Component getTarget() {
            return ContainerDeck.this;
        }

        public boolean dropTo(ContainerDeck<C> _containerDeck, C component, Component _target) {
            boolean ok = ContainerDeck.this.put(component, true, true);
            return ok;
        }
    }
}

interface DropTo<C extends Component> {
    Component getTarget();
    boolean dropTo(ContainerDeck<C> source, C component, Component target);
}
