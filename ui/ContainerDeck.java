package ui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import logic.Fellow;

public class ContainerDeck<C extends Component> extends JPanel {
    Color mycolorBrown = new Color(94, 38, 18);
    Color mycolorBackground = new Color(210, 180, 140);
    Font myfontChinese = new Font("隶书", Font.BOLD, 18);
    Font myfontEnglish = new Font("Ink free", Font.BOLD, 15);

    private Box box;
    private JPanel canvas;
    private JFrame frame;

    /** 该 Deck 中卡片可以拖拽并放置到的其他目标 */
    private ArrayList<DropTo<C>> dropToList;

    /** submit(card, index) -> bool 将 card 放置在该 Deck 中 index 位置，返回放置成功与否 */
    private BiFunction<C, Integer, Boolean> submit;

    /** 告知 submit 函数 index 位置不重要的常量 */
    public static final Integer SUBMIT_DEFAULT_INDEX = -1;

    /** 触发当前 UI 重渲染的钩子 */
    private Runnable propagateRerender;

    /** 该 Deck 中卡片的顺序是否有意义 */
    public Boolean ordered = false;

    ContainerDeck(JPanel canvas, JFrame frame) {
        super();
        box = new Box(BoxLayout.X_AXIS);
        super.add(box);
        box.add(Box.createHorizontalGlue());
        box.add(Box.createHorizontalGlue());

        this.canvas = canvas;
        this.frame = frame;
        this.dropToList = new ArrayList<>();
        this.submit = (_component, _index) -> true;
        this.propagateRerender = () -> {};
        this.setBackground(mycolorBackground);
    }

    public ArrayList<DropTo<C>> getDropToList() {
        return dropToList;
    }

    public void setSubmit(BiFunction<C, Integer, Boolean> submit) {
        this.submit = submit;
    }

    public void setPropagateRerender(Runnable propagateRerender) {
        this.propagateRerender = propagateRerender;
    }

    /**
     * 在 Deck 中最后的位置插入一个元素，且不向 Game 提交修改
     * 
     * 只用于渲染
     * 
     * @param component
     */
    public Boolean append(C component) {
        put(component, false, false);
        box.add(component, box.getComponentCount() - 1);
        return true;
    }

    /**
     * 在 Deck 中插入一个元素
     * 
     * @param component 要插入的元素
     * @param positioned 需要根据当前在 UI 中的位置确定插入的坐标（说明不是在 render 中调用的，而是玩家操作引发的）
     * @param shouldSubmit 是否向 Game 提交修改
     */
    public Boolean put(C component, boolean positioned, boolean shouldSubmit) {
        if(shouldSubmit) {
            int destination;
            if(positioned) {
                destination = getInsertionIndex(component, box.getComponents(), 1, box.getComponentCount() - 1);
            } else {
                destination = SUBMIT_DEFAULT_INDEX;
            }
            boolean ok = submit.apply(component, destination);
            if(!ok) return false;
        }
        MouseMotionAdapter mouseMotionAdapter = new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                CanvasUtil.drag(canvas, frame, component, e);
            }
        };
        MouseInputAdapter mouseInputAdapter = new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                CanvasUtil.promote(canvas, frame, component);
                component.addMouseMotionListener(mouseMotionAdapter);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                component.removeMouseMotionListener(mouseMotionAdapter);
                Rectangle itemHitbox = CanvasUtil.getAbsoluteBounds(component);
                Runnable putBack = () -> {
                    if(ordered) {
                        Component[] components = box.getComponents();
                        int destination = getInsertionIndex(component, components, 1, components.length - 1);
                        submit.apply(component, destination);
                    }
                    // box.revalidate();
                    // box.repaint();
                    canvas.remove(component);
                };
                dropToList
                    .stream()
                    .filter(dropTo -> {
                        Component target = dropTo.getTarget();
                        Rectangle targetHitbox = CanvasUtil.getAbsoluteBounds(target);
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
        return true;
    }

    public void clear() {
        box.removeAll();
        box.add(Box.createHorizontalGlue());
        box.add(Box.createHorizontalGlue());
    }

    public Fellow[] getFellows() {
        Component[] components = box.getComponents();
        return Arrays.stream(components).filter(component -> ComponentCardFellow.class.isInstance(component)).map(card -> ((ComponentCardFellow)card).getFellow()).toArray(Fellow[]::new);
    }

    static int getInsertionIndex(Component item, Component[] components, int startInclusive, int endExclusive) {
        Point dropPoint = CanvasUtil.getAbsoluteCenter(item);
        int destination = IntStream.range(startInclusive, endExclusive).filter(i -> {
            Point componentPoint = CanvasUtil.getAbsoluteCenter(components[i]);
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
