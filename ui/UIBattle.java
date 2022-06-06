package ui;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
import util.StreamUtil;

public class UIBattle extends UIBase {
    JLabel tempLabel;
    JLabel opponentLabel;

    BattleInfo battleInfo;
    BattleInfo.BattleHistory move;
    Integer opponentId = 0;

    ContainerTeam teamPlayer;
    ContainerTeam teamOpponent;

    HashMap<Integer, ContainerTeam> teams = new HashMap<>();

    public UIBattle(UIFrame frame) {
        super(frame);

        opponentLabel = new JLabel();
        boxTopMid.add(opponentLabel);

        teamOpponent = new ContainerTeam(canvas, frame);
        teamPlayer = new ContainerTeam(canvas, frame);
        boxCenter.add(teamOpponent);
        boxCenter.add(teamPlayer);

        tempLabel = new JLabel();
        tempLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        boxBottomRight.add(tempLabel);
    }

    @Override
    public void renderStatic(Game game) {
        if(this.battleInfo == null) {
            throw new Error("[UI] set GameInfo first!");
        }
        super.renderStatic(game);
        setTempLabel(game);
        setOpponentLabel(game);
    }

    @Override
    public void renderDynamic(Game game) {
        if(this.battleInfo == null) {
            throw new Error("[UI] set GameInfo first!");
        }
        super.renderDynamic(game);
        renderTeam();
    }

    @Override
    public void register(Game game) {
        super.register(game);
        registerTeam(game);
    }

    public void init(Game game) {
        battleInfo = game.getBattleInfo(game.SELF_PLAYER_ID);
        if(battleInfo.history.isEmpty()) throw new Error("[UI] history is empty!");
        teams.clear();
        move = battleInfo.history.get(0);
        teams.put(game.SELF_PLAYER_ID, teamPlayer);
        opponentId = game.SELF_PLAYER_ID == move.attackerID ? move.defenderID : move.attackerID;
        teams.put(opponentId, teamOpponent);
    }

    void setTempLabel(Game game) {
        BattleInfo info = game.getBattleInfo(game.SELF_PLAYER_ID);
        tempLabel.setText(String.format("D%d, R%d", info.damage, info.winner));
    }

    void setOpponentLabel(Game game) {
        opponentLabel.setText(String.format("%s (%d 级) 血量: %d", game.getName(opponentId), game.getLevel(opponentId), game.getHealth(opponentId)));
    }

    void renderTeam() {
        teams.get(move.attackerID).render(move.attackingSideFellows);
        teams.get(move.defenderID).render(move.defendingSideFellows);
    }

    void registerTeam(Game game) {
        teamOpponent.setOpponent(teamPlayer);
        teamPlayer.setOpponent(teamOpponent);
        teamOpponent.setPropagateRerender(() -> this.render(game));
        teamPlayer.setPropagateRerender(() -> this.render(game));
    }


    public void playback(Game game, int result) {
        Thread playback = new Thread(() -> {
            Runnable moves = () -> {
                System.out.println("[UI] battle playback over.");
                if (result == 0) {
                    if(battleInfo.damage == 0) {
                        System.out.println("[UI] battle result: draw, continue");
                        JOptionPane.showMessageDialog(null, "回合结束，平手", "回合结束", JOptionPane.INFORMATION_MESSAGE);
                    } else if(battleInfo.winner == game.SELF_PLAYER_ID) {
                        System.out.println("[UI] battle result: win, continue");
                        JOptionPane.showMessageDialog(null, String.format("回合结束，你胜了，造成 %d 点伤害", battleInfo.damage), "回合结束", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        System.out.println("[UI] battle result: lose, continue");
                        JOptionPane.showMessageDialog(null, String.format("回合结束，你败了，受到了 %d 点伤害", battleInfo.damage), "回合结束", JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                } else if (result == 1) {
                    System.out.println("[UI] battle result: win");
                    JOptionPane.showMessageDialog(null, "你赢了！", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    System.out.println("[UI] battle result: lose");
                    JOptionPane.showMessageDialog(null, "你输了！", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
                }
                then.run();
            };
            for(BattleInfo.BattleHistory h: battleInfo.history) {
                System.out.println(h.toString());
            }
            for(int i = battleInfo.history.size() - 1; i >= 0; --i) {
                final BattleInfo.BattleHistory history = battleInfo.history.get(i);
                final Runnable then = moves;
                moves = () -> {
                    this.move = history;
                    teams.get(history.attackerID).attack(history.attackerIndex, history.defenderIndex, then);
                };
            }
            final Runnable finalMoves = moves;
            SwingUtilities.invokeLater(() -> finalMoves.run());
        });
        
        playback.start();
        try {
            playback.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
