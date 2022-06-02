package logic;
import java.util.*;

enum FellowType {
    General, Beast, Demon, Pirate, Dragon, Mech, Elemental, Murloc
}

public class Fellow implements Cloneable{
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

    @Override
    public Fellow clone()
    {
        return new Fellow(Name, ID, Description, level, Type);
    }

    public bool isDead()
    {
        return Health<=0;
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

/*
    class Alleycat
    Attack: 1
    Health: 1
    Type: Beast
*/
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

/*
    class Cat
    Attack: 1
    Health: 1
    Type: Beast
*/
class Cat extends Fellow {
    public Cat() {
        super("Cat", 1, "", 1, FellowType.Beast);
        this.Atk = 1;
        this.Health = 1;
    }
}

/*
    class Icky_Imp
    Attack: 1
    Health: 1
    Type: Demon
*/
class Icky_Imp extends Fellow {
    public Icky_Imp() {
        super("Icky_Imp", 2, "Deathrattle: Summon two 1/1 Imps", 1, FellowType.Demon);
        this.Atk = 1;
        this.Health = 1;
    }

    public ArrayList<Fellow> Deathrattle(ArrayList<Fellow> Battleground) {
        Fellow m = new Imp();
        Fellow m2 = new Imp();
        if (Battleground.size() < 7)
            Battleground.add(m);
        if (Battleground.size() < 7)
            Battleground.add(m2);
        return Battleground;
    };
}

/*
    class Imp
    Attack: 1
    Health: 1
    Type: Demon
*/
class Imp extends Fellow {
    public Imp() {
        super("Imp", 3, "", 1, FellowType.Demon);
        this.Atk = 1;
        this.Health = 1;
    }
}

/*
    class Rockpool_Hunter
    Attack: 2
    Health: 3
    Type: Murloc
*/
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
                break;
            }
        }
        return Battleground;
    }
} 

/*
    class Implusive_Trickster
    Attack: 2
    Health: 2
    Type: Demon
*/
class Implusive_Trickster extends Fellow {
    public Implusive_Trickster() {
        super("Implusive_Trickster", 5, "Deathrattle: Give this minion's health to another friendly minion.", 1, FellowType.Demon);
        this.Atk = 2;
        this.Health = 2;
    }

    public ArrayList<Fellow> Deathrattle(ArrayList<Fellow> Battleground) {
        Random r = new Random();
        int i = r.nextInt(Battleground.size());
        Fellow m = Battleground.get(i);
        if (m.Type == FellowType.Demon) {
            m.Health += this.Health;
        }
        return Battleground;
    }
}

/*
    class Scallywag
    Attack: 3
    Health: 1
    Type: Pirate
*/
class Scallywag extends Fellow {
    public Scallywag() {
        super("Scallywag", 6, "Deathrattle: Summon a 1/1 Pirate. It attacks immediately.", 1, FellowType.Pirate);
        this.Atk = 3;
        this.Health = 1;
    }

    public ArrayList<Fellow> Deathrattle(ArrayList<Fellow> Battleground) {
        Fellow m = new Sky_Pirate();
        if (Battleground.size() < 7)
            Battleground.add(m);
        return Battleground;
    }
}

/*
    class Sky_Pirate
    Attack: 1
    Health: 1
    Type: Pirate
*/
class Sky_Pirate extends Fellow {
    public Sky_Pirate() {
        super("SkyPirate", 7, "", 1, FellowType.Pirate);
        this.Atk = 1;
        this.Health = 1;
    }
}

/*
    class Harvest_Golem
    Attack: 2
    Health: 3
    Type: Mech
*/
class Harvest_Golem extends Fellow {
    public Harvest_Golem() {
        super("HarvestGolem", 8, "Deathrattle: Summon a 2/1 Damaged Golem", 2, FellowType.Mech);
        this.Atk = 2;
        this.Health = 3;
    }

    public ArrayList<Fellow> Deathrattle(ArrayList<Fellow> Battleground) {
        Fellow m = new Damaged_Golem();
        if (Battleground.size() < 7)
            Battleground.add(m);
        return Battleground;
    }
}

/*
    class Damaged_Golem
    Attack: 2
    Health: 1
    Type: Mech
*/
class Damaged_Golem extends Fellow {
    public Damaged_Golem() {
        super("DamagedGolem", 9, "", 2, FellowType.Mech);
        this.Atk = 2;
        this.Health = 1;
    }
}

/*
    class Leapfrogger
    Attack: 3
    Health: 3
    Type: Beast
*/
class Leapfrogger extends Fellow {
    public Leapfrogger() {
        super("Leapfrogger", 10, "Deathrattle: Give a friendly Beast +3/+3.", 2, FellowType.Beast);
        this.Atk = 3;
        this.Health = 3;
    }

    public ArrayList<Fellow> Deathrattle(ArrayList<Fellow> Battleground) {
        for (Fellow m: Battleground) {
            if (m.Type == FellowType.Beast) {
                m.Atk += 3;
                m.Health += 3;
                break;
            }
        }
        return Battleground;
    }
}

/*
    class Metaltooth_Leaper
    Attack: 3
    Health: 3
    Type: Mech
    Battlecry: Give your other Mechs +2 Attack.
*/
class Metaltooth_Leaper extends Fellow {
    public Metaltooth_Leaper() {
        super("Metaltooth_Leaper", 11, "Battlecry: Give your other Mechs +2 Attack.", 2, FellowType.Mech);
        this.Atk = 3;
        this.Health = 3;
    }

    @Override
    public ArrayList<Fellow> Battlecry(ArrayList<Fellow> Battleground) {
        for (Fellow m: Battleground) {
            if (m.Type == FellowType.Mech) {
                m.Atk += 2;
            }
        }
        return Battleground;
    }
}

/*
    class Menagerie_Mug
    Attack: 2
    Health: 2
    Type: General
    Deathrattle: Give 3 friendly minions +1/+1.
*/
class Menagerie_Mug extends Fellow {
    public Menagerie_Mug() {
        super("MenagerieMug", 12, "Deathrattle: Give 3 friendly minions +1/+1.", 2, FellowType.General);
        this.Atk = 2;
        this.Health = 2;
    }

    public ArrayList<Fellow> Deathrattle(ArrayList<Fellow> Battleground) {
        Random r = new Random();
        for (int i = 0; i < 3; i++) {
            int j = r.nextInt(Battleground.size());
            Fellow m = Battleground.get(j);
            m.Atk += 1;
            m.Health += 1;
        }
        return Battleground;
    }
}
