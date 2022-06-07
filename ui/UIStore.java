package ui;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
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

    Box showFellowBox;
    Box inventoryBox;
    Box battlefieldBox;

    ContainerDeck<ComponentCardFellow> showFellowDeck;
    ContainerDeck<ComponentCardFellow> inventoryDeck;
    ContainerDeck<ComponentCardFellow> battlefieldDeck;

    Boolean frozen = false;

    UIStore(UIFrame frame) {
        super(frame);
        
        upgrade = new JButton();
        upgrade.setFont(myfontChinese);
        upgrade.setBackground(mycolorButton);
        upgrade.setForeground(mycolorTextLight);
        refresh = new JButton("刷新($1)");
        refresh.setFont(myfontChinese);
        refresh.setBackground(mycolorButton);
        refresh.setForeground(mycolorTextLight);
        freeze = new JButton("冻结($0)");
        freeze.setFont(myfontChinese);
        freeze.setBackground(mycolorButton);
        freeze.setForeground(mycolorTextLight);
        battle = new JButton("开始!");
        battle.setFont(myfontChinese);
        battle.setBackground(mycolorButton);
        battle.setForeground(mycolorTextLight);
        level = new JLabel();
        level.setFont(myfontChinese);
        level.setForeground(mycolorTextBrown);

        this.boxTopLeft.add(upgrade);
        this.boxTopMid.add(level);
        this.boxTopRight.add(refresh);
        this.boxTopRight.add(Box.createHorizontalStrut(4));
        this.boxTopRight.add(freeze);

        coin = new JLabel();
        coin.setFont(myfontChinese);
        coin.setForeground(mycolorTextBrown);
        
        this.boxBottomMid.add(Box.createHorizontalStrut(4));
        this.boxBottomMid.add(coin);

        // this.boxCenter.add(new JLabel("测试"));
        // this.boxCenter.setBorder(BorderFactory.createLineBorder(mycolor));

        JLabel storeLabel = new JLabel("商店");
        storeLabel.setFont(myfontChinese);
        storeLabel.setForeground(mycolorTextBrown);
        showFellowDeck = new ContainerDeck<>(canvas, frame);
        showFellowBox = new Box(BoxLayout.Y_AXIS);
        showFellowBox.setPreferredSize(new Dimension(1024, 160));
        showFellowBox.setMinimumSize(new Dimension(0, 160));
        showFellowBox.setAlignmentY(Component.CENTER_ALIGNMENT);
        showFellowBox.add(storeLabel);
        showFellowBox.add(showFellowDeck);
        showFellowBox.setBorder(BorderFactory.createMatteBorder(4, 4, 2, 4, mycolorBrown));

        battlefieldDeck = new ContainerDeck<>(canvas, frame);
        battlefieldDeck.ordered = true;

        JLabel battlefieldLabel = new JLabel("战场");
        battlefieldLabel.setFont(myfontChinese);
        battlefieldLabel.setForeground(mycolorTextBrown);
        battlefieldBox = new Box(BoxLayout.Y_AXIS);
        battlefieldBox.setPreferredSize(new Dimension(1024, 160));
        battlefieldBox.setMinimumSize(new Dimension(0, 160));
        battlefieldBox.setAlignmentY(Component.CENTER_ALIGNMENT);
        battlefieldBox.add(battlefieldLabel);
        battlefieldBox.add(battlefieldDeck);
        battlefieldBox.setBorder(BorderFactory.createMatteBorder(2, 4, 2, 4, mycolorBrown));

        JLabel inventoryLabel = new JLabel("手牌");
        inventoryLabel.setFont(myfontChinese);
        inventoryLabel.setForeground(mycolorTextBrown);
        inventoryDeck = new ContainerDeck<>(canvas, frame);
        inventoryBox = new Box(BoxLayout.Y_AXIS);
        inventoryBox.setPreferredSize(new Dimension(1024, 160));
        inventoryBox.setMinimumSize(new Dimension(0, 160));
        inventoryBox.setAlignmentY(Component.CENTER_ALIGNMENT);
        inventoryBox.add(inventoryLabel);
        inventoryBox.add(inventoryDeck);
        inventoryBox.setBorder(BorderFactory.createMatteBorder(2, 4, 4, 4, mycolorBrown));

        this.boxCenter.add(Box.createVerticalGlue());
        this.boxCenter.add(showFellowBox);
        this.boxCenter.add(battlefieldBox);
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
            renderBattlefield(game);
        }
    }

    @Override
    public void renderStatic(Game game) {
        super.renderStatic(game);
        synchronized(game) {
            setLevel(game);
            setCoin(game);
            setUpgrade(game);
            
            setFrozen(game);
            
            setHealthBar(game);
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
        registerBattlefield(game);
    }

    void setLevel(Game game) {
        level.setText(String.format("商店等级: %d", game.getLevel(game.SELF_PLAYER_ID)));
    }

    void setCoin(Game game) {
        coin.setText(String.format("铸币: %d", game.getCoin(game.SELF_PLAYER_ID)));
    }

    void setUpgrade(Game game) {
        if(game.getLevel(game.SELF_PLAYER_ID) == 6) {
            upgrade.setEnabled(false);
            upgrade.setText("满级");
        } else {
            upgrade.setEnabled(true);
            upgrade.setText(String.format("升级($%d)", game.getUpgradeFee(game.SELF_PLAYER_ID)));
        }
    }

    void setFrozen(Game game) {
        if (game.getFrozenFellows(game.SELF_PLAYER_ID).isEmpty()) {
            showFellowBox.setBorder(BorderFactory.createLineBorder(mycolorBrown, 5));
        } else {
            showFellowBox.setBorder(BorderFactory.createLineBorder(new Color(176, 213, 223), 5));
        }
    }

    public void setHealthBar(Game game) {
        boxLeft.removeAll();
        game.getRemainPlayers().stream().sorted((id1, id2) -> {
            return game.getHealth(id2) - game.getHealth(id1);
        }).forEach(id -> {
            JLabel tmpLabel = new JLabel(String.format("%s{%d}", game.getName(id), game.getHealth(id)));
            tmpLabel.setFont(myfontEnglish);
            tmpLabel.setForeground(mycolorTextBrown);
            boxLeft.add(tmpLabel);
        });
    }

    void registerUpgrade(Game game) {
        funcUpgrade = (AWTEvent event) -> {
            try {
                synchronized(game) {
                    game.upgrade(game.SELF_PLAYER_ID);
                }
            } catch (GameException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
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
            then.run();
        };
    }

    public void registerShowFellows(Game game) {
        showFellowDeck.getDropToList().add(inventoryDeck.new DropToThis());
        showFellowDeck.setPropagateRerender(() -> this.render(game));
        showFellowDeck.setSubmit((card, _index) -> {
            synchronized(game) {
                try {
                    game.sell(game.SELF_PLAYER_ID, card.getFellow());
                    return true;
                } catch (GameException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    return false;
                } finally {
                    render(game);
                }
            }
        });
    }

    public void registerInventory(Game game) {
        inventoryDeck.getDropToList().add(battlefieldDeck.new DropToThis());
        inventoryDeck.setPropagateRerender(() -> this.render(game));
        inventoryDeck.setSubmit((card, _index) -> {
            synchronized(game) {
                try {
                    game.enroll(game.SELF_PLAYER_ID, card.getFellow());
                    return true;
                } catch (GameException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    return false;
                } finally {
                    render(game);
                }
            }
        });
    }

    public void registerBattlefield(Game game) {
        battlefieldDeck.getDropToList().add(showFellowDeck.new DropToThis());
        battlefieldDeck.setPropagateRerender(() -> this.render(game));
        battlefieldDeck.setSubmit((card, index) -> {
            synchronized(game) {
                try {
                    if(index == ContainerDeck.SUBMIT_DEFAULT_INDEX) index = game.getBattlefield(game.SELF_PLAYER_ID).size();
                    index -= 1;
                    game.changePositionOrCast(game.SELF_PLAYER_ID, card.getFellow(), index);
                    return true;
                } catch (GameException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
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
            fellows.stream().map(fellow -> new ComponentCardFellow(fellow)).collect(Collectors.toList()).forEach(showFellowDeck::append);
        }
        showFellowDeck.revalidate();
    }

    public void renderInventory(Game game) {
        inventoryDeck.clear();
        synchronized(game) {
            ArrayList<Fellow> fellows = game.getInventory(game.SELF_PLAYER_ID);
            fellows.stream().map(fellow -> new ComponentCardFellow(fellow)).collect(Collectors.toList()).forEach(inventoryDeck::append);
        }
        showFellowDeck.revalidate();
    }

    public void renderBattlefield(Game game) {
        battlefieldDeck.clear();
        synchronized(game) {
            ArrayList<Fellow> fellows = game.getBattlefield(game.SELF_PLAYER_ID);
            fellows.stream().map(fellow -> new ComponentCardFellow(fellow)).collect(Collectors.toList()).forEach(battlefieldDeck::append);
        }
        showFellowDeck.revalidate();
    }
}
