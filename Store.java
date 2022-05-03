import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.*;
//  继承自随从池(default: )    
/**
 * 回合费用
初始回合玩家获得3个铸币，每回合结束时清空铸币，下一回合重新获得上一回合数+1的铸币，最多获得十个。 [2] 
招揽随从
每当玩家进入酒馆时，鲍勃会向玩家展示一排可供招揽的随从。当然，玩家在招揽随从时必须先支付铸币，然后他们才会为玩家效力。招揽随从需花费3个铸币。
战场一位玩家最多站7位随从。 [2] 
刷新面板
场上的随从玩家一个都不喜欢，玩家可以花费1个铸币，刷新可供招揽的随从。 [2] 
出售随从
如果玩家需要腾出场地空间，或者有哪个随从在阵容中力不从心，玩家可以将该随从出售，并获得1枚铸币。 [2] 
冻结面板
理想的随从就在眼前，但玩家却囊中羞涩。玩家可以免费冻结当前的随从面板，下次造访酒馆时，玩家依然会看到这些随从。 [2] 
升级酒馆
玩家的酒馆等级决定了鲍勃提供的随从数量和强度，以及玩家在战斗阶段对敌方生命值造成的基础伤害。玩家的酒馆等级越高，玩家就能获得更强力的随从，
玩家造成的伤害也越高。每当玩家提升酒馆等级，后续升级的花费都会提高。如果玩家选择不进行升级，则当玩家再次返回酒馆时，提升酒馆等级的花费会减少1枚铸币。 [2] 
打造三连(待定)
招揽三个相同的随从会自动将他们融合成一个金色的随从，属性和效果都将显著提升。三连会保留融合之前所获得的全部增益效果。
当玩家成功实现了三连招揽，并且将融合之后的随从放入战场后，玩家还能获得一张奖励卡牌，使玩家免费发现一个更高酒馆等级的随从。 [2] 


* Assumption FellowPool
    fellowList 
        [attribute] ArrayList;
    Fellow
        + level 用来评价的强度
        
*/             
public class Store extends FellowPool
{
    
    /****   personal mode  ****/
    private final int splayerID;       // 玩家id
    private final String splayName;    // 玩家姓名（可不用）
    private int splayerCoin;     // 玩家金币
    private int splayerLevel;
    private ArrayList<Fellow> sFrozenFellow;
                                // 展示的随从
    private ArrayList<Fellow> showFellow;
                                // 冻结随从
    private boolean sUpgraded;  // 是否升级
    private int sUpgradeFee;    // 升级开销
    private String mode;        // 商店使用模式 share 多人共用 single 单人单用
    private ArrayList<Player>Accounts;
                                // share模式下的多玩家管理
    /****   必要的条件    ****/
    private static ArrayList<Integer> upgradeCost;
                                // 升级开销
    private static ArrayList<Integer> fellowGradeNum;
                                // 等级化随从数量
    public int turnID;          // 游戏回合
    private final int maxFellowN=5;     
                                // 展示最大数量

    /*****  share mode *******/
    static class Player{
        int playerID;           // 玩家id
        String playName;        // 玩家姓名
        int playerCoin;         // 玩家金币
        ArrayList<Fellow> frozenFellow;
                                // 冻结的随从
        ArrayList<Fellow> showFellow;
                                // 展示的随从
        int playerLevel;        // 玩家等级
        int upgradeFee;         // 升级开销
        boolean upgraded;       // 是否升级
        /**
         * @brief Player 初始化
         * @param Id    玩家ID
         * @param name  玩家姓名（可置空）
         * @param coin  玩家金币
         * @param level 玩家等级
         */
        public Player(int Id,String Name,int coin,int level){
            this.playerID=Id;
            this.playName=Name;
            this.playerCoin=coin;
            this.playerLevel=level;
            this.upgradeFee=Store.upgradeCost.get(level);
            this.upgraded = false;
        }

        public void start(int turnID){
            this.playerCoin = 3+turnID<=10?3+turnID:10;
        }

        public void upgrade(){
            this.playerCoin-=this.upgradeFee;
            this.playerLevel+=1;
            this.upgradeFee=Store.upgradeCost.get(this.playerLevel);
        }

        public void add(){
            this.playerCoin+=1;
        }

        /**
         * 买或正常扣除、清空都在此处完成
         */
        public void sub(int amount){
            this.playerCoin-=amount;
        }
        
        public int getCoin(){
            return this.playerCoin;
        }

        public int getLevel(){
            return this.playerLevel;
        } 

        // 默认fellow基类实现了深拷贝
        /**
         * @brief frozen 更新冻结列表
         * @param frozen 冻结列表
         */
        public void frozen(ArrayList<Fellow> frozen){
            for(Fellow f:frozen){
                this.frozenFellow.add(f.clone());
            }
            return;
        }

        public ArrayList<Fellow> getFrozen(){
            return this.frozenFellow;
        }

        public boolean isFrozen(){
            return this.frozenFellow.isEmpty();
        }

        public end(){
            this.playerCoin=0;
            this.frozenFellow.clear();
            if(!this.upgraded)this.upgradeFee=Math.max(this.upgradeFee-1,0);
            else this.upgraded=true;
        }

    }
    /*******   商店初始化 ******/
    public Store(){}
    /**
     * @brief store 接受参数列表初始化
     * @param args -- PlayerMsg 
     *            
     */
    public Store(ArrayList<PlayerMsg>players, int maxSize,ArrayList<Integer>cost,ArrayList<Integer>gradeNum){
        super();
        for(PlayerMsg player:players){
            this.Accounts.add(new Player(player.Id, player.name, player.coin, player.level));
        }
        this.maxFellowN = maxSize;
        this.mode = "share";
        Store.fellowGradeNum= gradeNum.clone();
        Store.upgradeCost=cost.clone();
    }

    /**
     * @brief store 给定参数初始化
     * @param Id    玩家ID
     * @param name  玩家姓名（可置空）
     * @param coin  玩家金币
     * @param level 玩家等级
     */
    public Store(int Id, String name, int coin, int level, int maxSize,ArrayList<Integer>cost,ArrayList<Integer>gradeNum){
        super();
        this.turnID=0;
        this.splayerID=Id;
        this.splayName=name;
        this.splayerCoin=coin;
        this.splayerLevel=level;
        this.maxFellowN = maxSize;
        this.mode = "single";
        Store.upgradeCost=cost.clone();
        Store.fellowGradeNum = gradeNum.clone();
    }
    /*******   功能函数  *******/
    
    /**
     * @brief start 每次访问商店需要首先访问
     * @param turnID 回合数
     */
    public void start(int turnID){
        this.splayerCoin=Math.min(3+Store.upgradeCost.get(this.splayerLevel),10);
        if(this.sUpgraded){
            this.maxFellowN=Store.fellowGradeNum.get(this.splayerLevel);
            this.sUpgradeFee=Store.upgradeCost.get(this.splayerLevel);
            this.sUpgraded=false;
        }
        else{
            this.sUpgradeFee-=1;
        }
    }

    /**
     * 
     * @param fli initial Fellow List
     * @param mode whether repeatable generate Fellow list 
     */
    public ArrayList<Fellow> refresh(int mode){
        if(this.splayerCoin==0)return this.showFellow;
        this.splayerCoin-=1;
        if(!this.showFellow.isEmpty())this.showFellow.clear();
        return this.getSelectableFellowList(mode);
    }

    /**
     * @brief freeze 冻结随从
     * @param selected 选中冻结的fellow
     */
    public void freeze(ArrayList<Fellow> selected){
        if(this.sFrozenFellow.size()+selected.size()<=this.maxFellowN){
            for(Fellow f : selected){
                this.sFrozenFellow.add(f);
            }
            selected.clear();
        }
        return;
    }

    /**
     * @brief upgrade 升级酒馆
     */
    public void upgrade(){
        this.splayerCoin -= this.sUpgradeFee;
        this.sUpgraded=true;
        this.splayerLevel++;
    }

    /**
     * 
     * @param saleFellow 待售Fellow
     */
    public void sale(ArrayList<Fello>saleFellow){
        this.splayerCoin+=saleFellow.size();
        saleFellow.clear();
    }

    /**
     * 
     * @param f selected Fellow
     */
    public void enroll(Fellow f){
        if(this.showFellow.contains(f)&&this.splayerCoin>=3){
            this.splayerCoin-=3;
            this.showFellow.remove(f);
        }
        return;
    }

    /**
     * @brief
     * @param mode 是否随机生成不重复
     * @return 可选择随从列表
     */
    public ArrayList<Fellow> getSelectableFellowList(int mode){
        // 判定是否冻结，冻结加入新展示队列
        if(!this.sFrozenFellow.isEmpty()){
            for(Fellow f:this.sFrozenFellow)
            this.showFellow.add(f);
            this.sFrozenFellow.clear();
        }
        ArrayList<Integer> okList;
        for(int i=0;i<this.fellowList.size();++i){
            if(this.fellowList.get(i).level<=this.splayerLevel)okList.add(i);
        }
        Random rdm = new Random();
        int size =okList.size();
        int maxSize = this.maxFellowN - this.showFellow.size();
        // 可能重复的卡组
        if(mode==0){
            for(int i=0;i<maxSize;++i){
            int idx = rdm.nextInt(size);
            this.showFellow.add(this.fellowList.get(okList.get(idx)));
            }
        }
        else{
            for(int i=0;i<maxSize;++i){
                int idx = rdm.nextInt(size);
                while(okList.contains(idx)){
                    idx = rdm.nextInt(size);
                }
                this.showFellow.add(this.fellowList.get(okList.get(idx)));
            }
        }
        return this.showFellow;
    }
}
