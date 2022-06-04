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
    ContainerDeck<ComponentCardFellow> inventoryDeck;

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
        Box showFellowBox = new Box(BoxLayout.Y_AXIS);
        showFellowBox.setAlignmentY(Component.CENTER_ALIGNMENT);
        showFellowBox.add(new JLabel("商店"));
        showFellowBox.add(showFellowDeck);
        showFellowBox.setBorder(BorderFactory.createLineBorder(Color.BLUE));

        inventoryDeck = new ContainerDeck<>(canvas, frame);
        Box inventoryBox = new Box(BoxLayout.Y_AXIS);
        inventoryBox.setAlignmentY(Component.CENTER_ALIGNMENT);
        inventoryBox.add(new JLabel("手牌"));
        inventoryBox.add(inventoryDeck);
        inventoryBox.setBorder(BorderFactory.createLineBorder(Color.GREEN));

        this.boxCenter.add(Box.createVerticalGlue());
        this.boxCenter.add(showFellowBox);
        this.boxCenter.add(inventoryBox);
        this.boxCenter.add(battle);
        this.boxCenter.add(Box.createVerticalGlue());
    }

    @Override
    public void renderDynamic(Game game) {
        super.renderDynamic(game);
        synchronized(game) {
            renderShowFellows(game);
            renderInventory(game);
        }
    }

    @Override
    public void renderStatic(Game game) {
        super.renderStatic(game);
        synchronized(game) {
            setLevel(game);
            setCoin(game);
            setUpgrade(game);
        }
    }

    @Override
    public void register(Game game) {
        super.register(game);
        registerUpgrade(game);
        registerRefresh(game);
        registerFreeze(game);
        registerBattle(game);
        upgrade.addActionListener(e -> funcUpgrade.accept(e));
        refresh.addActionListener(e -> funcRefresh.accept(e));
        freeze.addActionListener(e -> funcFreeze.accept(e));
        battle.addActionListener(e -> funcBattle.accept(e));

        registerShowFellows(game);
        registerInventory(game);
    }

    public void setLevel(Game game) {
        level.setText(String.format("商店等级: %d", game.getLevel(game.SELF_PLAYER_ID)));
    }

    public void setCoin(Game game) {
        coin.setText(String.format("铸币: %d", game.getCoin(game.SELF_PLAYER_ID)));
    }

    public void setUpgrade(Game game) {
        upgrade.setText(String.format("升级($%d)", game.getUpgradeFee(game.SELF_PLAYER_ID)));
    }

    void registerUpgrade(Game game) {
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

    void registerRefresh(Game game) {
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

    void registerFreeze(Game game) {
        funcFreeze = (AWTEvent event) -> {
            synchronized(game) {
                game.freezeAll(game.SELF_PLAYER_ID);
            }
            render(game);
        };
    }

    void registerBattle(Game game) {
        funcBattle = (AWTEvent event) -> {
            synchronized(game) {
                // System.out.println("[UI] Store done!");
                // game.gameHolder.release();
            }
            // render(game);
        };
    }

    public void registerShowFellows(Game game) {
        showFellowDeck.getDropToList().add(inventoryDeck.new DropToThis());
        showFellowDeck.setPropagateRerender(() -> this.render(game));
    }

    public void registerInventory(Game game) {
        inventoryDeck.setPropagateRerender(() -> this.render(game));
        inventoryDeck.setSubmit((card) -> {
            synchronized(game) {
                try {
                    game.enroll(game.SELF_PLAYER_ID, card.getFellow());
                    return true;
                } catch (GameException e) {
                    if (e.type == GameExceptionType.ENROLL_NO_ENOUGH_COIN) {
                        JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                    else e.printStackTrace();
                    return false;
                } finally {
                    render(game);
                }
            }
        });
    }

    public void renderShowFellows(Game game) {
        showFellowDeck.clear();
        synchronized(game) {
            ArrayList<Fellow> fellows = game.getShowFellows(game.SELF_PLAYER_ID);
            fellows.stream().map(fellow -> new ComponentCardFellow(fellow)).collect(Collectors.toList()).forEach(showFellowDeck::put);
        }
        // showFellowDeck.
    }

    public void renderInventory(Game game) {
        inventoryDeck.clear();
        synchronized(game) {
            ArrayList<Fellow> fellows = game.getInventory(game.SELF_PLAYER_ID);
            fellows.stream().map(fellow -> new ComponentCardFellow(fellow)).collect(Collectors.toList()).forEach(inventoryDeck::put);
        }
    }
}

class Test {
    public static void main(String[] args) {
        String[] a = {"Hi"};
        Game game = new Game(new ArrayList<>(Arrays.asList(a)));
        
        JFrame frame = new JFrame();
        UIStore uiStore = new UIStore(frame);
        uiStore.register(game);
        uiStore.render(game);

        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
