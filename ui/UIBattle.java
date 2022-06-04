package ui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.MouseInputAdapter;

import animation.api.*;
import animation.api.swing.*;
import logic.*;
import logic.GameException.GameExceptionType;

public class UIBattle extends UIBase {
    JLabel tempLabel;
    public UIBattle(JFrame frame) {
        super(frame);

        tempLabel = new JLabel();
        tempLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        boxCenter.add(Box.createGlue());
        boxCenter.add(tempLabel);
        boxCenter.add(Box.createGlue());
    }

    @Override
    public void renderStatic(Game game) {
        super.renderStatic(game);
    }

    @Override
    public void register(Game game) {
        super.register(game);
    }

    void setTempLabel(Game game) {
    }

}

