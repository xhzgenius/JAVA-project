package logic;
/*
 *
 * level这个参数代表随从星级，勿改动，其他类会依赖这个
 * 其它的交给lyz了！
 */

import java.util.Random;
import java.util.*;

enum FellowType {
    General, Beast, Demon, Pirate, Dragon, Mech, Elemental, Murloc
}

public class Fellow {
    private int ID;
    public final String Name;
    public final String Description;
    public int level;
    public final FellowType Type;
    public int Atk;
    public int Health;

    public Fellow(String name, int id, String description, int tier, FellowType type) {
        this.Name = name;
        this.ID = id;
        this.Description = description;
        this.level = tier;
        this.Type = type;
    }

    public ArrayList<Fellow> Battlecry(ArrayList<Fellow> Battleground) {
        return Battleground;
    }

    public ArrayList<Fellow> Deathrattle(ArrayList<Fellow> Battleground) {
        return Battleground;
    }

    public ArrayList<Fellow> WhileAttack(ArrayList<Fellow> Battleground) {
        return Battleground;
    }
}

class Alleycat extends Fellow {
    public Alleycat() {
        super("Alleycat", 0, "Battlecry: Summon a 1/1 Cat", 1, FellowType.Beast);
        this.Atk = 1;
        this.Health = 1;
    }

    public ArrayList<Fellow> Battlecry(ArrayList<Fellow> Battleground) {
        Fellow m = new Cat();
        if (Battleground.size() < 7)
            Battleground.add(m);
        return Battleground;
    }
}

class Cat extends Fellow {
    public Cat() {
        super("Cat", 1, "", 1, FellowType.Beast);
        this.Atk = 1;
        this.Health = 1;
    }
}

class Icky_Imp extends Fellow {
    public Icky_Imp() {
        super("Icky_Imp", 2, "Deathrattle: Summon two 1/1 Imps", 1, FellowType.Demon);
        this.Atk = 1;
        this.Health = 1;
    }

    public ArrayList<Fellow> Deathrattle(ArrayList<Fellow> Battleground) {
        Fellow m = new Icky_Imp();
        Fellow m2 = new Icky_Imp();
        if (Battleground.size() < 7)
            Battleground.add(m);
        if (Battleground.size() < 7)
            Battleground.add(m2);
        return Battleground;
    };
}

class Imp extends Fellow {
    public Imp() {
        super("Imp", 3, "", 1, FellowType.Demon);
        this.Atk = 1;
        this.Health = 1;
    }
}

class Rockpool_Hunter extends Fellow {
    public Rockpool_Hunter() {
        super("Rockpool_Hunter", 4, "Battlecry: Give a friendly murloc +1/+1", 1, FellowType.Murloc);
        this.Atk = 2;
        this.Health = 3;
    }

    public ArrayList<Fellow> Battlecry(ArrayList<Fellow> Battleground) {
        for (Fellow m: Battleground) {
            if (m.Type == FellowType.Murloc) {
                m.Atk += 1;
                m.Health += 1;
            }
        }
        return Battleground;
    }
} 
