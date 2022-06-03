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

public class UIStore extends UIBase {

    JButton upgrade;
    JButton refresh;
    JButton freeze;
    JButton battle;

    JLabel level;
    JLabel coin;

    Consumer<AWTEvent> funcUpgrade;
    Consumer<AWTEvent> funcRefresh;
    Consumer<AWTEvent> funcFreeze;
    Consumer<AWTEvent> funcBattle;

    ContainerDeck<ComponentCardFellow> showFellowDeck;

    UIStore(JFrame frame) {
        super(frame);
        
        upgrade = new JButton();
        refresh = new JButton("刷新($1)");
        freeze = new JButton("冻结($0)");
        battle = new JButton("开始!");
        level = new JLabel();

        this.boxTopLeft.add(upgrade);
        this.boxTopMid.add(level);
        this.boxTopRight.add(refresh);
        this.boxTopRight.add(Box.createHorizontalStrut(4));
        this.boxTopRight.add(freeze);

        coin = new JLabel();
        
        this.boxBottomMid.add(Box.createHorizontalStrut(4));
        this.boxBottomMid.add(coin);

        // this.boxCenter.add(new JLabel("测试"));
        // this.boxCenter.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        showFellowDeck = new ContainerDeck<>(canvas, frame);
        this.boxCenter.add(Box.createVerticalGlue());
        this.boxCenter.add(showFellowDeck);
        this.boxCenter.add(Box.createVerticalGlue());
        this.boxCenter.add(battle);
        this.boxCenter.add(Box.createVerticalGlue());
    }

    @Override
    public void render(Game game) {
        super.render(game);
        synchronized(game) {
            setLevel(game);
            setCoin(game);
            setUpgrade(game);
            setRefresh(game);
            setFreeze(game);
            setBattle(game);
            renderShowFellows(game);
        }
    }

    @Override
    public void register() {
        super.register();
        upgrade.addActionListener(e -> funcUpgrade.accept(e));
        refresh.addActionListener(e -> funcRefresh.accept(e));
        freeze.addActionListener(e -> funcFreeze.accept(e));
        battle.addActionListener(e -> funcBattle.accept(e));
    }

    public void setLevel(Game game) {
        level.setText(String.format("商店等级: %d", game.getLevel(game.SELF_PLAYER_ID)));
    }

    public void setCoin(Game game) {
        coin.setText(String.format("铸币: %d", game.getCoin(game.SELF_PLAYER_ID)));
    }

    public void setUpgrade(Game game) {
        upgrade.setText(String.format("升级($%d)", game.getUpgradeFee(game.SELF_PLAYER_ID)));
        funcUpgrade = (AWTEvent event) -> {
            try {
                synchronized(game) {
                    game.upgrade(game.SELF_PLAYER_ID);
                }
            } catch (GameException e) {
                if (e.type == GameExceptionType.UPGRADE_NO_ENOUGH_COIN) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
                else e.printStackTrace();
            } finally {
                render(game);
            }
        };
    }

    public void setRefresh(Game game) {
        funcRefresh = (AWTEvent event) -> { 
            try {
                synchronized(game) {
                    game.refresh(game.SELF_PLAYER_ID);
                }
            } catch (GameException e) {
                if (e.type == GameExceptionType.REFRESH_NO_ENOUGH_COIN) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
                else e.printStackTrace();
            } finally {
                render(game);
            }
        };
    }

    public void setFreeze(Game game) {
        funcFreeze = (AWTEvent event) -> {
            synchronized(game) {
                game.freezeAll(game.SELF_PLAYER_ID);
            }
            render(game);
        };
    }

    public void setBattle(Game game) {
        funcBattle = (AWTEvent event) -> {
            synchronized(game) {
                System.out.println("[UI] Store done!");
                game.gameHolder.release();
            }
            // render(game);
        };
    }

    public void renderShowFellows(Game game) {
        showFellowDeck.clear();
        ArrayList<Fellow> fellows = game.getShowFellows(game.SELF_PLAYER_ID);
        fellows.stream().map(fellow -> new ComponentCardFellow(fellow)).collect(Collectors.toList()).forEach(showFellowDeck::put);
    }
}

class Test {
    public static void main(String[] args) {
        String[] a = {"Hi"};
        Game game = new Game(new ArrayList<>(Arrays.asList(a)));
        
        JFrame frame = new JFrame();
        UIStore uiStore = new UIStore(frame);
        uiStore.register();
        uiStore.render(game);

        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        
        Thread gameLoop = new Thread(() -> {
            game.run();
        });
        Thread uiLoop = new Thread(() -> {
            game.uiHolder.hold();
            System.out.println("[DEBUG] UI released.");
            uiStore.render(game);
        });

        gameLoop.start();
        uiLoop.start();

        try {
            gameLoop.join();
            uiLoop.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}