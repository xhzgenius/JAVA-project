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
        fellowList.add(new Nathrezim_Overseer());
        fellowList.add(new Sewer_Rat());
        fellowList.add(new Spawn_of_NZoth());
        fellowList.add(new Felfin_Navigator());
        fellowList.add(new Houndmaster());
        fellowList.add(new Rat_Pack());
        fellowList.add(new Screwjunk_Clunker());
        fellowList.add(new Southsea_Strongarm());
        fellowList.add(new Swolefin());
        fellowList.add(new Coldlight_Seer());
        fellowList.add(new Cave_Hydra());
        fellowList.add(new Mechano_Egg());
        fellowList.add(new Menagerie_Jug());
        fellowList.add(new Ring_Matron());
        fellowList.add(new Savannah_Highmane());
        fellowList.add(new Baby_Krush());
        fellowList.add(new King_Bagurgle());
        fellowList.add(new Leeroy_the_Reckless());
        fellowList.add(new Palescale_Crocolisk());
        fellowList.add(new Voidlord());
        fellowList.add(new Dread_Admiral_Eliza());
        fellowList.add(new Foe_Reaper_4000());
        fellowList.add(new Omega_Buster());
        fellowList.add(new Goldrinn_the_Great_Wolf());
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
