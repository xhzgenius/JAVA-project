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
    private ArrayList<BattleInfo> battleInfoList; // 每名玩家上一轮的对战记录

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
        storesList.get(0).start(0); // ! 开始回合放在这里是不是不太好？
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

    /**
     * 给UI的函数，用于获取某一名玩家的上一次对战的记录。
     * @param playerID
     * @return
     */
    public BattleInfo getBattleInfo(int playerID){return battleInfoList.get(playerID);}

    /**
     * 给用户（UI和bot）的函数，用于购买随从，花费3铸币从商店购买以后置入自己的手牌。
     * @param playerID 玩家ID（玩家在Game中的下标）
     * @param f 商店里的一个随从
     * @param position 购买以后，随从要放在哪一位置，从左往右数，右边的随从位置顺延
     * @throws GameException 
     *      如果铸币不足3(ENROLL_NO_ENOUGH_COIN)，
     *      或者随从已满7个(BATTLEFIELD_FULL)，
     *      或者手牌满10张(INVENTORY_FULL)
     */
    public void enroll(int playerID, Fellow f) throws GameException
    {
        Store store = storesList.get(playerID);
        ArrayList<Fellow> inventory = inventoriesList.get(playerID);
        if(store.getCoin()<3) throw new GameException(GameException.GameExceptionType.ENROLL_NO_ENOUGH_COIN);
        if(inventory.size()>=10) throw new GameException(GameException.GameExceptionType.INVENTORY_FULL);
        store.enroll(f);
        inventory.add(f);
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
        store.refresh(true);
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
        if(battlefield.size()>=7) throw new GameException(GameException.GameExceptionType.BATTLEFIELD_FULL);
        inventory.remove(f);
        battlefield.add(position, f);
        f.Battlecry(battlefield);
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

    /**
     * 游戏主体运行的函数，包含回合的循环和终局的检测。游戏开始后就会调用run()函数。
     */
    private void run()
    {

    }

    /**
     * 两个玩家的随从对战
     */
    private void battle(int player1, int player2)
    {
        ArrayList<Fellow> battlefield1 = new ArrayList<Fellow>(), 
                          battlefield2 = new ArrayList<Fellow>();
        Iterator<Fellow> it1 = battlefieldsList.get(player1).iterator(), 
                         it2 = battlefieldsList.get(player2).iterator();
        while(it1.hasNext())
        {
            battlefield1.add(it1.next().clone());
        }
        while(it2.hasNext())
        {
            battlefield1.add(it2.next().clone());
        }
        // 深拷贝双方玩家战场上的随从。Fellow实现了深拷贝clone()方法。

        Random random = new Random(System.currentTimeMillis()); // 一次性随机生成器，用来随机选择攻击目标

        BattleInfo battleInfo = new BattleInfo(); // 本场战斗的信息（用于提供给UI）

        // 开始战斗
        int index1 = 0, index2 = 0; // 虽然炉石里面真正的攻击顺序比这个复杂，但不管了，先按下标攻击
        while(true)
        {
            battleInfo.addHistory(
                new BattleInfo.BattleHistory(
                    player1, player2, index1, index2, 
                    battlefield1, battlefield2)
                ); // 将攻击前双方的随从列表传给对战历史
            if(battlefield1.isEmpty() || battlefield2.isEmpty()) break; // 有一方没有随从了
            // 玩家1先攻击
            if(index1>=battlefield1.size()) index1 = 0;
            Fellow target = battlefield2.get(random.nextInt(battlefield2.size()));
            battlefield1.get(index1).attack(target);
            if(target.isDead())
            {
                target.Deathrattle(battlefield2, battlefield1); // 触发亡语
                battlefield2.remove(target); // 移除
            }

            battleInfo.addHistory(
                new BattleInfo.BattleHistory(
                    player2, player1, index2, index1, 
                    battlefield2, battlefield1)
                ); // 将攻击前双方的随从列表传给对战历史
            if(battlefield1.isEmpty() || battlefield2.isEmpty()) break; // 有一方没有随从了
            // 玩家2攻击
            if(index2>=battlefield2.size()) index2 = 0;
            target = battlefield1.get(random.nextInt(battlefield1.size()));
            battlefield2.get(index2).attack(target);
            if(target.isDead())
            {
                target.Deathrattle(battlefield1, battlefield2); // 触发亡语
                battlefield1.remove(target); // 移除
            }
        }
        
        // 战斗完毕
        
        if(battlefield1.isEmpty() && battlefield2.isEmpty())
        {
            // 不做任何事
        }
        else if(battlefield1.isEmpty()) // 玩家2赢了
        {
            int dmg = 0;
            for(Fellow f: battlefield2) // 将玩家2场上剩余随从的星级加起来，作为对玩家1造成的伤害
            {
                dmg += f.level;
            }
            battleInfo.winner = player2;
            battleInfo.damage = dmg;
        }
        else // 玩家1赢了
        {
            int dmg = 0;
            for(Fellow f: battlefield1) // 将玩家2场上剩余随从的星级加起来，作为对玩家1造成的伤害
            {
                dmg += f.level;
            }
            battleInfo.winner = player1;
            battleInfo.damage = dmg;
        }

        // 保存对战记录
        battleInfoList.set(player1, battleInfo);
        battleInfoList.set(player2, battleInfo);
    }

    
        
}

