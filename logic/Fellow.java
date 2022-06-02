package logic;
import java.util.*;

enum FellowType {
    General, Beast, Demon, Pirate, Dragon, Mech, Elemental, Murloc
}

public class Fellow {
    final String name;
    final String description;
    int level;
    final FellowType type;
    int attack;
    int health;

    public String getName() {
        return name;
    }

    public Integer getAttack() {
        return attack;
    }

    public Integer getHealth() {
        return health;
    }

    public String getDescription() {
        return description;
    }

    public Integer getLevel() {
        return level;
    }

    public FellowType getType() {
        return type;
    }


    public Fellow(String name, String description, int tier, FellowType type) {
        this.name = name;
        this.description = description;
        this.level = tier;
        this.type = type;
    }

    public ArrayList<Fellow> battleCry(ArrayList<Fellow> Battleground) {
        return Battleground;
    }

    public ArrayList<Fellow> deathRattle(ArrayList<Fellow> Battleground) {
        return Battleground;
    }

    public ArrayList<Fellow> whileAttack(ArrayList<Fellow> Battleground) {
        return Battleground;
    }
}

/**
    Attack: 1
    Health: 1
    Type: Beast
*/
class Alleycat extends Fellow {
    public Alleycat() {
        super("Alleycat", "Battle Cry: Summon a 1/1 Cat", 1, FellowType.Beast);
        this.attack = 1;
        this.health = 1;
    }

    public ArrayList<Fellow> battleCry(ArrayList<Fellow> Battleground) {
        Fellow m = new Cat();
        if (Battleground.size() < 7)
            Battleground.add(m);
        return Battleground;
    }
}

/**
    Attack: 1
    Health: 1
    Type: Beast
*/
class Cat extends Fellow {
    public Cat() {
        super("Cat", "", 1, FellowType.Beast);
        this.attack = 1;
        this.health = 1;
    }
}

/**
    Attack: 1
    Health: 1
    Type: Demon
*/
class IckyImp extends Fellow {
    public IckyImp() {
        super("IckyImp", "Death Rattle: Summon two 1/1 Imps", 1, FellowType.Demon);
        this.attack = 1;
        this.health = 1;
    }

    public ArrayList<Fellow> deathRattle(ArrayList<Fellow> Battleground) {
        Fellow m = new Imp();
        Fellow m2 = new Imp();
        if (Battleground.size() < 7)
            Battleground.add(m);
        if (Battleground.size() < 7)
            Battleground.add(m2);
        return Battleground;
    };
}

/**
    Attack: 1
    Health: 1
    Type: Demon
*/
class Imp extends Fellow {
    public Imp() {
        super("Imp", "", 1, FellowType.Demon);
        this.attack = 1;
        this.health = 1;
    }
}

/** 
    Attack: 2
    Health: 3
    Type: Murloc
*/
class RockpoolHunter extends Fellow {
    public RockpoolHunter() {
        super("RockpoolHunter", "Battle Cry: Give a friendly Murloc +1/+1", 1, FellowType.Murloc);
        this.attack = 2;
        this.health = 3;
    }

    public ArrayList<Fellow> battleCry(ArrayList<Fellow> Battleground) {
        for (Fellow m: Battleground) {
            if (m.type == FellowType.Murloc) {
                m.attack += 1;
                m.health += 1;
                break;
            }
        }
        return Battleground;
    }
} 

/**
    Attack: 2
    Health: 2
    Type: Demon
*/
class ImpulsiveTrickster extends Fellow {
    public ImpulsiveTrickster() {
        super("ImpulsiveTrickster", "Death Rattle: Give this minion's health to another friendly minion.", 1, FellowType.Demon);
        this.attack = 2;
        this.health = 2;
    }

    public ArrayList<Fellow> deathRattle(ArrayList<Fellow> Battleground) {
        Random r = new Random();
        int i = r.nextInt(Battleground.size());
        Fellow m = Battleground.get(i);
        if (m.type == FellowType.Demon) {
            m.health += this.health;
        }
        return Battleground;
    }
}

/**
    Attack: 3
    Health: 1
    Type: Pirate
*/
class Scallywag extends Fellow {
    public Scallywag() {
        super("Scallywag", "Death Rattle: Summon a 1/1 Pirate. It attacks immediately.", 1, FellowType.Pirate);
        this.attack = 3;
        this.health = 1;
    }

    public ArrayList<Fellow> deathRattle(ArrayList<Fellow> Battleground) {
        Fellow m = new SkyPirate();
        if (Battleground.size() < 7)
            Battleground.add(m);
        return Battleground;
    }
}

/**
    Attack: 1
    Health: 1
    Type: Pirate
*/
class SkyPirate extends Fellow {
    public SkyPirate() {
        super("SkyPirate", "", 1, FellowType.Pirate);
        this.attack = 1;
        this.health = 1;
    }
}

/**
    Attack: 2
    Health: 3
    Type: Mech
*/
class HarvestGolem extends Fellow {
    public HarvestGolem() {
        super("HarvestGolem", "Death Rattle: Summon a 2/1 Damaged Golem", 2, FellowType.Mech);
        this.attack = 2;
        this.health = 3;
    }

    public ArrayList<Fellow> deathRattle(ArrayList<Fellow> Battleground) {
        Fellow m = new DamagedGolem();
        if (Battleground.size() < 7)
            Battleground.add(m);
        return Battleground;
    }
}

/**
    Attack: 2
    Health: 1
    Type: Mech
*/
class DamagedGolem extends Fellow {
    public DamagedGolem() {
        super("DamagedGolem", "", 2, FellowType.Mech);
        this.attack = 2;
        this.health = 1;
    }
}

/**
    Attack: 3
    Health: 3
    Type: Beast
*/
class Leapfrogger extends Fellow {
    public Leapfrogger() {
        super("Leapfrogger", "Death Rattle: Give a friendly Beast +3/+3.", 2, FellowType.Beast);
        this.attack = 3;
        this.health = 3;
    }

    public ArrayList<Fellow> deathRattle(ArrayList<Fellow> Battleground) {
        for (Fellow m: Battleground) {
            if (m.type == FellowType.Beast) {
                m.attack += 3;
                m.health += 3;
                break;
            }
        }
        return Battleground;
    }
}

/**
    Attack: 3
    Health: 3
    Type: Mech
    battleCry: Give your other Mechs +2 Attack.
*/
class MetaltoothLeaper extends Fellow {
    public MetaltoothLeaper() {
        super("MetaltoothLeaper", "Battle Cry: Give your other Mechs +2 Attack.", 2, FellowType.Mech);
        this.attack = 3;
        this.health = 3;
    }

    @Override
    public ArrayList<Fellow> battleCry(ArrayList<Fellow> Battleground) {
        for (Fellow m: Battleground) {
            if (m.type == FellowType.Mech) {
                m.attack += 2;
            }
        }
        return Battleground;
    }
}

/**
    Attack: 2
    Health: 2
    Type: General
    deathRattle: Give 3 friendly minions +1/+1.
*/
class MenagerieMug extends Fellow {
    public MenagerieMug() {
        super("MenagerieMug", "Death Rattle: Give 3 friendly minions +1/+1.", 2, FellowType.General);
        this.attack = 2;
        this.health = 2;
    }

    public ArrayList<Fellow> deathRattle(ArrayList<Fellow> Battleground) {
        Random r = new Random();
        for (int i = 0; i < 3; i++) {
            int j = r.nextInt(Battleground.size());
            Fellow m = Battleground.get(j);
            m.attack += 1;
            m.health += 1;
        }
        return Battleground;
    }
}
