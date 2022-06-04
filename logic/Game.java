package logic;
import java.io.IOException;
import java.nio.channels.Pipe;
import java.util.*;

import logic.GameException.GameExceptionType;
import util.Holder;

/**
 *
 * Created by XHZ
 * 游戏本体类，一局游戏只有一个Game对象
 * 目前采用的是Store的Single Mode
 */
public class Game
{
    private final int maxPlayer; // 最大玩家数
    private final ArrayList<String> namesList;
    private ArrayList<Store> storesList = new ArrayList<>(); // 每名玩家对应一个Store
    private ArrayList<ArrayList<Fellow>> inventoriesList = new ArrayList<>(); // 每名玩家的手牌
    private ArrayList<ArrayList<Fellow>> battlefieldsList = new ArrayList<>(); // 每名玩家战场上的生物
    private ArrayList<Integer> healthList = new ArrayList<>(); // 每名玩家的剩余生命值
    private ArrayList<BattleInfo> battleInfoList = new ArrayList<>(); // 每名玩家上一轮的对战记录
    private ArrayList<Integer> remainPlayers = new ArrayList<>(); // 剩余存活玩家的ID
    private ArrayList<Bot> bots = new ArrayList<>(); // Bots
    int turn = 0; // 回合数
    int lastEliminatedPlayerID = 0; // 上一个被淘汰的玩家ID，用于在当前人数为奇数时凑人数。

    /**  人类玩家的 ID */
    public int SELF_PLAYER_ID = 0; 

    /** UI 被 Game 阻塞，Game release，UI hold */
    public Holder uiHolder;

    /** Game 被 UI 阻塞，Game hold，UI release */
    public Holder gameHolder;

    // ============================================================================================
    /**
     * Game初始化函数
     * @param namesList 所有玩家名字的列表
     * 
     */
    public Game(ArrayList<String> namesList)
    {
        this.namesList = namesList;
        maxPlayer = this.namesList.size();
        if(maxPlayer%2!=0) System.out.println("[Game] 警告：玩家数为奇数，不合法。");
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
            battleInfoList.add(new BattleInfo());
            remainPlayers.add(Integer.valueOf(i));
            bots.add(new Bot(this, i)); // 会有一个冗余的0号bot，但我们不会调用它。
        }
        
        // uiHolder = new Holder();
        // gameHolder = new Holder();
        startStore();
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
     * 给用户（UI和bot）的函数，用于访问手牌（购买的，以及三合一获得的，且未放到战场上的随从）的信息。
     * @param playerID 玩家ID（玩家在Game中的下标）
     * @return 玩家手牌中的随从列表
     */
    public ArrayList<Fellow> getInventory(int playerID){return inventoriesList.get(playerID);}

    /**
     * 给用户（UI和bot）的函数，用于访问战场上随从的信息。
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
        boolean result = mergeFellow(f, battlefieldsList.get(playerID), inventory); // 自动进行三合一(if able)
        if(result==false) inventory.add(f); // 未三合一
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

    /**
     * 给用户（UI和bot）的函数，用于将一个随从放到场上。它可能在手牌里或是已经在场上。
     * @param playerID
     * @param f 将要施放的随从
     * @param position 放下去以后在场上的位置
     * @throws GameException
     */
    public void changePositionOrCast(int playerID, Fellow f, int position) throws GameException {
        ArrayList<Fellow> battlefield = battlefieldsList.get(playerID);
        int positionOnBattlefield = battlefield.indexOf(f);
        if(positionOnBattlefield == -1) {
            cast(playerID, f, position);
        } else {
            changePosition(playerID, positionOnBattlefield, position);
        }
    }


    // ============================================================================================
    // 以上是用户可调用函数

    // 以下是游戏内核函数

    /** 三合一（进入手牌之前检测。由游戏自动进行。）
     * @return 当成功三合一，返回true，否则返回false
     */
    private boolean mergeFellow(Fellow newlyBoughtFellow, ArrayList<Fellow> battlefield, ArrayList<Fellow> inventory)
    {
        Fellow f1 = null, f2 = null;
        for(Fellow f: battlefield)
        {
            if(f.ID==newlyBoughtFellow.ID && f.isGolden==false)
            {
                if(f1==null) f1 = f;
                else if(f2==null) f2 = f;
                else break;
            }
        }
        for(Fellow f: inventory)
        {
            if(f.ID==newlyBoughtFellow.ID && f.isGolden==false)
            {
                if(f1==null) f1 = f;
                else if(f2==null) f2 = f;
                else break;
            }
        }
        if(f1==null || f2==null) return false;
        else
        {
            battlefield.remove(f1);
            inventory.remove(f1);
            battlefield.remove(f2);
            inventory.remove(f2);
            inventory.add(newlyBoughtFellow.newGoldenInstance(f1, f2));
            return true;
        }
    }


    // ! 弃用
    /** UI和内核的消息传递对象。UI可以访问该对象，修改其信息，并且notify该对象。 */
    // public class UIOperation
    // {
    //     public Game game;
    //     public String operation;
    //     public int playerID, arg1, arg2;
    //     public Fellow argFellow1, argFellow2;
    //     /** 如果读取到的操作不合法，则设置Exception对象，UI notify之后应当检查操作合法性。 */
    //     public UIOperation(Game game){this.game = game;}
    //     public GameException exception = null;
    //     /**
    //      * 读取该对象的成员变量，来对游戏进行操作。
    //      * @return 0代表操作结束后不进入战斗阶段，1代表操作结束后进入战斗阶段。
    //      */
    //     public int operate()
    //     {
    //         try
    //         {
    //             switch(operation)
    //             {
    //                 case "enroll":
    //                 game.enroll(playerID, argFellow1);
    //                 break;
    //                 case "sell":
    //                 game.sell(playerID, argFellow1);
    //                 break;
    //                 case "freezeAll":
    //                 game.freezeAll(playerID);
    //                 break;
    //                 case "refresh":
    //                 game.refresh(playerID);
    //                 break;
    //                 case "upgrade":
    //                 game.upgrade(playerID);
    //                 break;
    //                 case "cast":
    //                 game.cast(playerID, argFellow1, arg1);
    //                 break;
    //                 case "changePosition":
    //                 game.changePosition(playerID, arg1, arg2);
    //                 break;
    //                 case "endTurn":
    //                 return 1; // 代表进入战斗阶段
    //             }
    //         } catch (GameException e)
    //         {
    //             this.exception = e;
    //         }
    //         return 0; // 代表不进入战斗阶段
    //     }
    // }
    // public UIOperation operation = new UIOperation(this); // 此Game对象的唯一operation对象
    
    static final int GAME_NOT_END = 0, GAME_END_WIN = 1, GAME_END_LOSE = -1;
    /**
     * 判断整盘游戏是否结束，若未结束返回0，结束且胜利返回1，结束且失败返回-1.
     * @return 整盘游戏是否结束，若未结束返回0，结束且胜利返回1，结束且失败返回-1.
     * Game的一些final int: GAME_NOT_END = 0, GAME_END_WIN = 1, GAME_END_LOSE = -1;
     */
    private int judgeGameEnd()
    {
        if(remainPlayers.size()>=2) return GAME_NOT_END;
        if(remainPlayers.contains(SELF_PLAYER_ID)) // 玩家赢了
        {
            return GAME_END_WIN;
        }
        else return GAME_END_LOSE; // 玩家输了
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

    private void startStore()
    {
        // bot操作
        for(int i = 0;i<maxPlayer;i++)
        {
            storesList.get(i).start(turn);
            if(i==SELF_PLAYER_ID) continue;
            try
            {
                bots.get(i).act();
            } catch(GameException e)
            {
                // 忽略Bot的错误操作
            }
            
        }
        // 玩家操作 （已弃用，交给UI来调用，无需在此处调用。
        // uiHolder.release(); // 解锁 UI，使其渲染
        // gameHolder.hold(); // 锁定 Game，等待玩家操作结束
        // System.out.println("[GAME] Store done. Start battle.");
        // ! 弃用
        /* while(true)
        {
            try
            {
                synchronized(this.operation)
                {
                    this.operation.wait(); // 等待UI的输入
                }
            } catch (InterruptedException e)
            {
                int result = this.operation.operate();
                if(result==1) return;
            }
        } */
    }

    /** UI点击“结束回合”后，Game进行的操作。操作结束后，是玩家的商店阶段。
     * @return 整盘游戏是否结束，若未结束返回0，结束且胜利返回1，结束且失败返回-1.
     * Game的一些final int: GAME_NOT_END = 0, GAME_END_WIN = 1, GAME_END_LOSE = -1;
     */
    public int endTurn()
    {
        // 将剩余玩家随机两两分组进行战斗。如果剩余玩家有奇数个，则将最后被淘汰的玩家加入其中
        ArrayList<Integer> battlePlayers = new ArrayList<Integer>(remainPlayers);
        if(battlePlayers.size()%2==1) battlePlayers.add(lastEliminatedPlayerID);
        Random random = new Random(System.currentTimeMillis());
        while(battlePlayers.size()>=2)
        {
            Integer player1 = battlePlayers.get(random.nextInt(battlePlayers.size()));
            battlePlayers.remove(player1);
            Integer player2 = battlePlayers.get(random.nextInt(battlePlayers.size()));
            battlePlayers.remove(player2);
            battle(player1, player2);
            System.out.printf("[Game] 对战双方: %s(id: %d) vs %s(id: %d)\n", 
                              namesList.get(player1), player1, 
                              namesList.get(player2), player2);
        }

        // 战斗结束，更新存活的玩家列表
        ArrayList<Integer> newRemainPlayers = new ArrayList<Integer>();
        for(Integer id: remainPlayers)
        {
            if(healthList.get(id)>0) newRemainPlayers.add(id); // 该玩家存活
            else lastEliminatedPlayerID = id; // 该玩家被淘汰
        }
        remainPlayers = newRemainPlayers;
        turn += 1;

        int result = judgeGameEnd();
        if(result==GAME_NOT_END) startStore();
        return result;
    }
        
}

