package logic;

import java.util.*;

public class Bot {
    private Game game; // 该bot能调用整个game对象的公共方法
    private int playerID;

    public Bot(Game game, int playerID) {
        this.game = game;
        this.playerID = playerID;
    }

    void act() throws GameException {
        // 阿巴阿巴
        int t = game.turn;
        int botCoin = Math.min(3 + t, 10);
        ArrayList<Fellow> botInventory = game.getInventory(playerID);
        while (botCoin >= 3 && botInventory.size() < 10) {
            ArrayList<Fellow> ShowFellow = game.getShowFellows(playerID);
            Fellow fm = ShowFellow.stream()
                    .max(Comparator.comparing(f->f.level))
                    .get();
            game.enroll(playerID, fm);
            // boolean result = mergeFellow(fm, battlefieldsList.get(playerID),
            // botInventory);
            // if(result==false) botInventory.add(fm);
        }
        ArrayList<Fellow> botList = game.getBattlefield(playerID);
        Random rand = new Random(System.currentTimeMillis());
        while (botList.size() < 7) {
            int randNumber1 = rand.nextInt(botInventory.size());
            Fellow f = botInventory.get(randNumber1);
            int randNumber2 = rand.nextInt(7) + 1;
            game.cast(playerID, f, randNumber2);
        }
    }
}
