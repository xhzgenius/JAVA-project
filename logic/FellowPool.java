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
        fellowList.add(new Icky_Imp());
        fellowList.add(new Rockpool_Hunter());
        fellowList.add(new Implusive_Trickster());
        fellowList.add(new Scallywag());
        fellowList.add(new Harvest_Golem());
        fellowList.add(new Leapfrogger());
        fellowList.add(new Metaltooth_Leaper());
        fellowList.add(new Menagerie_Mug());
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
