package ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import logic.*;

public class UIBase extends JPanel {
    Color mycolorBrown = new Color(94, 38, 18);
    Color mycolorBackground = new Color(210, 180, 140);
    Color mycolorTextBrown = new Color(47, 19, 9);
    Color mycolorTextLight = new Color(245, 222, 179);
    Color mycolorButton = new Color(139, 69, 19);
    Font myfontChinese = new Font("隶书", Font.BOLD, 18);
    Font myfontEnglish = new Font("Ink free", Font.BOLD, 15);

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

    Box boxLeft;

    JLabel health;

    Runnable then = () -> {};

    UIBase(UIFrame frame) {
        super();
        frame.register(this);

        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(1024, 768));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setBackground(mycolorBackground);

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
        health.setFont(myfontChinese);
        health.setForeground(mycolorTextBrown);
        boxBottomMid.add(health);

        boxLeft = new Box(BoxLayout.Y_AXIS);
        this.add(boxLeft, BorderLayout.WEST);

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
    }

    public final void putInFrame(JFrame frame, JPanel canvas) {
        this.frame = frame;
        this.canvas = canvas;
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
