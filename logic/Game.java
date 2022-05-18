package logic;
import java.util.*;

/**
 *
 * Created by XHZ
 * 游戏本体类，一局游戏只有一个Game对象
 * 目前采用的是Store的Single Mode
 */
public class Game
{
    private final int maxPlayer; // 最大玩家数
    private ArrayList<Store> storesList; // 每名玩家对应一个Store
    private ArrayList<ArrayList<Fellow>> inventoriesList; // 每名玩家的手牌
    private ArrayList<ArrayList<Fellow>> battlefieldsList; // 每名玩家战场上的生物
    private ArrayList<Integer> healthList; // 每名玩家的剩余生命值
    private ArrayList<ArrayList<BattleHistory>> histories; // 每名玩家上一轮的对战记录

    /**  人类玩家的 ID */
    public int SELF_PLAYER_ID = 0; 

    // ============================================================================================
    /**
     * Game初始化函数
     * @param namesList 所有玩家名字的列表
     * 
     */
    public Game(ArrayList<String> namesList)
    {
        maxPlayer = namesList.size();
        storesList = new ArrayList<>();
        inventoriesList = new ArrayList<>();
        battlefieldsList = new ArrayList<>();
        healthList = new ArrayList<>();
        for(int i = 0;i<maxPlayer;i++)
        {
            storesList.add(
                new Store(i, namesList.get(i), 1, 
                          // Cost to upgrade store
                          new ArrayList<Integer>(Arrays.asList(5, 6, 7, 8, 9, 10)), 
                          // Max numbers of Fellows
                          new ArrayList<Integer>(Arrays.asList(3, 4, 4, 5, 5, 6))
                )
            );
            inventoriesList.add(new ArrayList<Fellow>());
            battlefieldsList.add(new ArrayList<Fellow>());
            healthList.add(Integer.valueOf(40));
        }
    }

    // ============================================================================================
    /**
     * 以下函数均为给用户（UI和bot）的函数。
     * 是玩家的UI窗口和bot可以调用的函数，用于进行操作。
     * 这些函数包含异常处理机制，保证完成的操作一定是合法的。（不合法的操作会抛出GameException）
     * 包括：观看商店信息、手牌信息、自己场上随从信息、自己剩余生命值、某次对战的过程回放；
     *      从商店里购买、贩卖、冻结、刷新、升级；
     *      从手中放随从；
     *      改变某个随从在自己场上的位置；
     *      结束当前回合的购买。
     */
    
    /**
     * 给用户（UI和bot）的函数，用于访问商店信息。
     * @param playerID 玩家ID（玩家在Game中的下标）
     */
    public String getName(int playerID){return storesList.get(playerID).getName();}
    public int getCoin(int playerID){return storesList.get(playerID).getCoin();}
    public int getLevel(int playerID){return storesList.get(playerID).getLevel();}
    public ArrayList<Fellow> getShowFellows(int playerID){return storesList.get(playerID).getShowFellows();}
    public ArrayList<Fellow> getFrozenFellows(int playerID){return storesList.get(playerID).getFrozenFellows();}
    public int getUpgradeFee(int playerID){return storesList.get(playerID).getUpgradeFee();}

    /**
     * 给用户（UI和bot）的函数，用于访问手牌（三合一获得的，且未放到战场上的随从）的信息。
     * @param playerID 玩家ID（玩家在Game中的下标）
     * @return 玩家手牌中的随从列表
     */
    public ArrayList<Fellow> getInventory(int playerID){return inventoriesList.get(playerID);}

    /**
     * 给用户（UI和bot）的函数，用于访问手牌（三合一获得的，且未放到战场上的随从）的信息。
     * @param playerID 玩家ID（玩家在Game中的下标）
     * @return 玩家当前战场上的随从列表
     */
    public ArrayList<Fellow> getBattlefield(int playerID){return battlefieldsList.get(playerID);}

    /**
     * 给用户（UI和bot）的函数，用于获取玩家剩余生命值。
     * @param playerID
     * @return 玩家当前剩余生命值
     */
    public int getHealth(int playerID){return healthList.get(playerID);}

    public ArrayList<BattleHistory> getBattleHistory(int playerID){return histories.get(playerID);}
    /**
     * 给用户（UI和bot）的函数，用于购买随从，花费3铸币从商店购买以后置入自己的战场。
     * @param playerID 玩家ID（玩家在Game中的下标）
     * @param f 商店里的一个随从
     * @param position 购买以后，随从要放在哪一位置，从左往右数，右边的随从位置顺延
     * @throws GameException 如果铸币不足3(ENROLL_NO_ENOUGH_COIN)，或者随从已满7个(ENROLL_TOO_MUCH_FELLOW)
     */
    public void enroll(int playerID, Fellow f, int position) throws GameException
    {
        Store store = storesList.get(playerID);
        ArrayList<Fellow> battlefield = battlefieldsList.get(playerID);
        if(store.getCoin()<3) throw new GameException(GameException.GameExceptionType.ENROLL_NO_ENOUGH_COIN);
        if(battlefield.size()>=7) throw new GameException(GameException.GameExceptionType.ENROLL_TOO_MUCH_FELLOW);
        store.enroll(f);
        battlefield.add(position, f);
    }

    /**
     * 给用户（UI和bot）的函数，用于卖出随从，获得1铸币并将其从战场上移除。
     * @param playerID
     * @param f
     * @throws GameException
     */
    public void sell(int playerID, Fellow f) throws GameException
    {
        Store store = storesList.get(playerID);
        ArrayList<Fellow> battlefield = battlefieldsList.get(playerID);
        if(battlefield.contains(f)==false) throw new GameException(GameException.GameExceptionType.SELL_FELLOW_NOT_FOUND);
        store.sell(f);
        battlefield.remove(f);
    }

    /**
     * 给用户（UI和bot）的函数，用于冻结商店里的所有随从。
     * @param playerID
     */
    public void freezeAll(int playerID)
    {
        Store store = storesList.get(playerID);
        store.freeze(store.getShowFellows());
    }

    /**
     * 给用户（UI和bot）的函数，用于刷新商店里的所有随从，并花费1铸币。
     * @param playerID
     * @throws GameException
     */
    public void refresh(int playerID) throws GameException
    {
        Store store = storesList.get(playerID);
        if(store.getCoin()<1) throw new GameException(GameException.GameExceptionType.REFRESH_NO_ENOUGH_COIN);
        store.refresh();
    }

    /**
     * 给用户（UI和bot）的函数，用于给商店升级，花费一定数额的金币。
     * @param playerID
     * @throws GameException
     */
    public void upgrade(int playerID) throws GameException
    {
        Store store = storesList.get(playerID);
        if(store.getCoin()<store.getUpgradeFee()) throw new GameException(GameException.GameExceptionType.UPGRADE_NO_ENOUGH_COIN);
        store.upgrade();
    }

    /**
     * 给用户（UI和bot）的函数，用于从手里施放一个随从到战场上。
     * @param playerID
     * @param f 将要施放的随从
     * @param position 放下去以后在场上的位置
     * @throws GameException
     */
    public void cast(int playerID, Fellow f, int position) throws GameException
    {
        ArrayList<Fellow> inventory = inventoriesList.get(playerID);
        ArrayList<Fellow> battlefield = battlefieldsList.get(playerID);
        if(inventory.contains(f)==false) throw new GameException(GameException.GameExceptionType.CAST_FELLOW_NOT_FOUND);
        inventory.remove(f);
        battlefield.add(position, f);
    }

    /**
     * 给用户（UI和bot）的函数，用于将场上的一个随从放到新的位置，新的位置右边的随从顺延一位。
     * @param playerID
     * @param oldPosition 要改变位置的随从的原始位置
     * @param newPosition 要改变位置的随从的新位置
     */
    public void changePosition(int playerID, int oldPosition, int newPosition)
    {
        ArrayList<Fellow> battlefield = battlefieldsList.get(playerID);
        Fellow f = battlefield.remove(oldPosition);
        battlefield.add(newPosition, f);
    }

    public void endTurn(int playerID)
    {
        
    }

    // ============================================================================================
    // 以上是用户可调用函数

    // 以下是游戏内核函数

}