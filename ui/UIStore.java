package ui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import logic.Game;
import logic.GameException;

public class UIStore extends UIBase {

    JButton upgrade;
    JButton refresh;
    JButton freeze;

    JLabel level;
    JLabel coin;

    Consumer<MouseEvent> funcUpgrade;

    UIStore() {
        super();
        
        upgrade = new JButton();
        refresh = new JButton("Refresh($1)");
        freeze = new JButton("Freeze($0)");
        level = new JLabel();
        this.boxTop.add(upgrade);
        this.boxTop.add(level);
        this.boxTop.add(refresh);
        this.boxTop.add(freeze);

        coin = new JLabel();
        this.boxBottom.add(coin);
    }

    @Override
    public void render(Game game) {
        super.render(game);
        setLevel(game);
        setCoin(game);
        setUpgrade(game);
    }

    @Override
    public void register() {
        super.register();
        upgrade.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                super.mouseClicked(e);
                funcUpgrade.accept(e);
            }
        });
    }

    public void setLevel(Game game) {
        level.setText(String.format("Store Level: %d", game.getLevel(game.SELF_PLAYER_ID)));
    }

    public void setCoin(Game game) {
        coin.setText(String.format("Coin: %d", game.getCoin(game.SELF_PLAYER_ID)));
    }

    public void setUpgrade(Game game) {
        upgrade.setText(String.format("Upgrade($%d)", game.getUpgradeFee(game.SELF_PLAYER_ID)));
        System.out.println(game.getUpgradeFee(game.SELF_PLAYER_ID));
        funcUpgrade = (MouseEvent event) -> { 
            try {
                game.upgrade(game.SELF_PLAYER_ID);
            } catch (GameException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        };
    }
}

class Test {
    public static void main(String[] args) {
        String[] a = {"Hi"};
        Game game = new Game(new ArrayList<>(Arrays.asList(a)));
        
        JFrame frame = new JFrame();
        UIStore uiStore = new UIStore();
        frame.add(uiStore);
        uiStore.render(game);
        uiStore.register();

        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}