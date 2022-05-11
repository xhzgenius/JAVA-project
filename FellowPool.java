/* 
 *
 * FellowPool类可以看作是一个随从池，用于在商店随机刷新随从。
 */

import java.util.*;

public class FellowPool
{
    public ArrayList<Fellow> fellowList = new ArrayList<Fellow>();
    public FellowPool(){}
    public void add(Fellow f)
    {
        fellowList.add(f);
    }
    public void remove(int index)
    {
        fellowList.remove(index);
    }
}
