package logic;
import java.util.*;
/**
 * 对战的过程回放。记录了某一回合战斗之前，双方随从的信息。
 */
public class BattleHistory {
    public final int turn;
    public final int attackerID; // playerID of the attacking side
    public final int defenderID; // playerID of the attacked side
    public final int attackerIndex; // attacking Fellow's position (from left to right)
    public final int defenderIndex; // attacked Fellow's position (from left to right)
    public final ArrayList<Fellow> attackingSideFellows; // attacking player's Fellows
    public final ArrayList<Fellow> defendingSideFellows; // attacked player's Fellows
    public BattleHistory(int turn, int attackerID, int defenderID, 
        int attackerIndex, int defenderIndex, 
        ArrayList<Fellow> attackingSideFellows, ArrayList<Fellow> defendingSideFellows)
    {
        this.turn = turn;
        this.attackerID = attackerID;
        this.defenderID = defenderID;
        this.attackerIndex = attackerIndex;
        this.defenderIndex = defenderIndex;
        this.attackingSideFellows = attackingSideFellows;
        this.defendingSideFellows = defendingSideFellows;
    }
}
