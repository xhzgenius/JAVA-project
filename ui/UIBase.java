package ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import logic.*;

public class UIBase extends JPanel {
    JFrame frame;
    JPanel canvas;

    Box boxTop;
    Box boxTopLeft;
    Box boxTopMid;
    Box boxTopRight;

    Box boxCenter;

    Box boxBottom;
    Box boxBottomLeft;
    Box boxBottomMid;
    Box boxBottomRight;

    JLabel health;

    Runnable then = () -> {};

    UIBase(JFrame frame) {
        super();
        this.frame = frame;

        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(1024, 768));
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

        Box containerVert = new Box(BoxLayout.Y_AXIS);
        Box containerHoriz = new Box(BoxLayout.X_AXIS);
        boxCenter = new Box(BoxLayout.Y_AXIS);

        containerVert.add(Box.createGlue());
        containerHoriz.add(Box.createGlue());
        containerHoriz.add(boxCenter);
        containerHoriz.add(Box.createGlue());
        containerVert.add(containerHoriz);
        containerVert.add(Box.createGlue());

        this.add(containerVert, BorderLayout.CENTER);

        JLayeredPane layeredPane = new JLayeredPane();

        JPanel thisPanel = this;
        canvas = new JPanel(null);
        canvas.setOpaque(false);
        layeredPane.add(thisPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(canvas, JLayeredPane.DRAG_LAYER);
        frame.add(layeredPane);
        frame.setPreferredSize(new Dimension(1024, 768));
        frame.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    Dimension dim = frame.getRootPane().getSize();
                    thisPanel.setSize(dim);
                    thisPanel.revalidate();
                    canvas.setSize(dim);
                    frame.repaint();
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

    /**
     * render 方法用于从当前的 Game 中获取当前的游戏状态，并将其显示在界面上
     * 
     * 这个方法可以多次调用
     * 
     * @param game Game 对象
     */
    public final void render(Game game) {
        renderStatic(game);
        renderDynamic(game);
    }

    /**
     * renderStatic 方法用于从当前的 Game 中获取状态并更新带拖拽功能的组件
     * 
     * @param game
     */
    public void renderDynamic(Game game) {
    }

    /**
     * renderStatic 方法用于从当前的 Game 中获取状态并更新普通的组件
     * 
     * @param game
     */
    public void renderStatic(Game game) {
        synchronized(game) {
            setHealth(game);
        }
    }

    /**
     * register 方法用于注册该对象的事件监听器
     * 
     * 这个方法仅可调用一次
     */
    public void register(Game game) {
        
    }

    public void setHealth(Game game) {
        this.health.setText(String.format("血量: %d", game.getHealth(game.SELF_PLAYER_ID)));
    }

    public void then(Runnable then) {
        this.then = then;
    }
    
}
