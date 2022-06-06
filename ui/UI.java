package ui;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.WindowConstants;

import java.awt.event.*;
import java.awt.*;

import logic.*;


public class UI {
    public static void main(String[] players) {
        Game game = new Game(new ArrayList<>(Arrays.asList(players)));
        
        UIFrame frame = new UIFrame();
        UIStore uiStore = new UIStore(frame);
        UIBattle uiBattle = new UIBattle(frame);
        uiStore.then(() -> {
            int result = game.endTurn();
            uiBattle.then(() -> {
                if(result != 0) {
                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                    return;
                }
                uiStore.render(game);
                frame.toggle(uiStore);
            });
            uiBattle.init(game);
            uiBattle.register(game);
            uiBattle.render(game);
            frame.toggle(uiBattle);
            uiBattle.playback(game, result);
        });
        uiStore.register(game);
        uiStore.render(game);

        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.toggle(uiStore);
    }
}
