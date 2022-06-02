package logic;
/* 
 *
 * FellowPool类可以看作是一个随从池，用于在商店随机刷新随从。
 */

import java.util.*;

public class FellowPool
{
    public ArrayList<Fellow> fellowList = new ArrayList<Fellow>();
    public FellowPool(){
        fellowList.add(new Alleycat());
        fellowList.add(new IckyImp());
        fellowList.add(new RockpoolHunter());
        fellowList.add(new ImpulsiveTrickster());
        fellowList.add(new Scallywag());
        fellowList.add(new HarvestGolem());
        fellowList.add(new Leapfrogger());
        fellowList.add(new MetaltoothLeaper());
        fellowList.add(new MenagerieMug());
    }
    public void add(Fellow f)
    {
        fellowList.add(f);
    }
    public void remove(int index)
    {
        fellowList.remove(index);
    }
}
