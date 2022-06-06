package logic;
import java.util.*;

/**
 * 给UI传递信息的接口。
 * 一场对战的过程。记录了一场战斗所有的信息，包括每一次攻击之前双方随从的信息，战斗的胜者，胜者对败者造成的伤害。
 */
public class BattleInfo {
    public ArrayList<BattleHistory> history; // 其中每一个元素都是一次攻击前的双方随从信息
    public int winner; // 胜者玩家的ID
    public int damage; // 胜者玩家对败者玩家造成的伤害

    public BattleInfo()
    {
        history = new ArrayList<BattleHistory>();
        winner = -1;
        damage = 0;
    }

    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer("BattleInfo: \n");
        for(BattleHistory h: history)
        {
            result.append(h.toString());
            result.append("\n");
        }
        result.append("Winner: "+winner+"\n");
        result.append("Damage: "+damage+"\n");
        return result.toString();

    }

    /** BattleHistory会对战场进行深拷贝，请放心传入引用值。 */
    public void addHistory(BattleHistory h)
    {
        history.add(h);
    }

    /**
     * 记录了某一次攻击之前，双方随从的信息。只是战斗的一帧。
     * 一场战斗的总信息在另一个类BattleInfo里面，里面有个BattleHistory的ArrayList和一些其它信息。
     * 只需传入战场的引用即可，构造函数会自动进行深拷贝。
     */
    static public class BattleHistory {
        
        public final int attackerID; // playerID of the attacking side
        public final int defenderID; // playerID of the attacked side
        public final int attackerIndex; // attacking Fellow's position (from left to right)
        public final int defenderIndex; // attacked Fellow's position (from left to right)
        public final ArrayList<Fellow> attackingSideFellows; // attacking player's Fellows
        public final ArrayList<Fellow> defendingSideFellows; // attacked player's Fellows

        public BattleHistory(int attackerID, int defenderID, 
            int attackerIndex, int defenderIndex, 
            ArrayList<Fellow> attackingSideFellows, ArrayList<Fellow> defendingSideFellows)
        {
            this.attackerID = attackerID;
            this.defenderID = defenderID;
            this.attackerIndex = attackerIndex;
            this.defenderIndex = defenderIndex;
            this.attackingSideFellows = deepcopy(attackingSideFellows);
            this.defendingSideFellows = deepcopy(defendingSideFellows);
            System.out.println("[Game] Create a history:");
            System.out.println(this.attackingSideFellows.toString());
            System.out.println(this.defendingSideFellows.toString());
        }

        static private ArrayList<Fellow> deepcopy(ArrayList<Fellow> battlefield) // 用于深拷贝一个战场（及上面的随从）
        {
            ArrayList<Fellow> copy = new ArrayList<Fellow>();
            Iterator<Fellow> it = battlefield.iterator();
            while(it.hasNext())
            {
                copy.add(it.next().clone());
            }
            return copy;
        }
        
        @Override
        public String toString()
        {
            StringBuffer result = new StringBuffer();
            if(this.attackerIndex>=0)
            {
                result.append("attacker: "+this.attackerID+"'s "+this.attackingSideFellows.get(this.attackerIndex).Name);
            }
            if(this.defenderIndex>=0)
            {
                result.append("defender: "+this.defenderID+"'s "+this.defendingSideFellows.get(this.defenderIndex).Name);
            }
            result.append("\nattackingSideFellows: \n");
            for(Fellow f: attackingSideFellows)
            {
                result.append(f.Name+"("+f.Atk+"/"+f.Health+") ");
            }
            result.append("\ndefendingSideFellows: \n");
            for(Fellow f: defendingSideFellows)
            {
                result.append(f.Name+"("+f.Atk+"/"+f.Health+") ");
            }
            result.append("\n");
            return result.toString();
        }
    }
}
