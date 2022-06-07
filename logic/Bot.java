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
        int botCoin = game.getCoin(playerID);
        if(botCoin>=game.getUpgradeFee(playerID) && game.getLevel(playerID)<6) game.upgrade(playerID);
        ArrayList<Fellow> botInventory = game.getInventory(playerID);
        while (botCoin >= 3 && botInventory.size() < 10) {
            ArrayList<Fellow> ShowFellow = game.getShowFellows(playerID);
            Fellow fm = ShowFellow.stream()
                    .max(Comparator.comparing(f -> f.level))
                    .get();
            game.enroll(playerID, fm);
            botCoin = game.getCoin(playerID);
            // boolean result = mergeFellow(fm, battlefieldsList.get(playerID),
            // botInventory);
            // if(result==false) botInventory.add(fm);
        }
        ArrayList<Fellow> botList = game.getBattlefield(playerID);
        Random rand = new Random(System.currentTimeMillis());
        while (botInventory.size() > 0) {
            if(botList.size() >= 7)
            {
                Fellow fm = botList.stream()
                    .min(Comparator.comparing(f -> f.level))
                    .get();
                game.sell(playerID, fm); // 场上满了就卖随从
            }
            int randNumber1 = rand.nextInt(botInventory.size());
            Fellow f = botInventory.get(randNumber1);
            // int randNumber2 = rand.nextInt(botList.size()) + 1;
            int randNumber2 = 0;
            game.cast(playerID, f, randNumber2);
        }
    }
}
