package logic;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

enum FellowType {
    General, Beast, Demon, Pirate, Dragon, Mech, Elemental, Murloc
}

/*
 * class Fellow
 * Attack: int
 * Health: int
 * Type: FellowType
 */
abstract class Fellow implements Cloneable {
    public final int ID;
    public final String Name;
    public final String Description;
    public int level;
    public final FellowType Type;
    public int Atk;
    public int Health;
    public boolean isGolden = false; // 是否是金色（三合一得到的）随从

    public Fellow(String name, int id, String description, int tier, FellowType type) {
        this.Name = name;
        this.ID = id;
        this.Description = description;
        this.level = tier;
        this.Type = type;
    }

    public Fellow setStatus(int Atk, int Health, boolean isGolden) {
        this.Atk = Atk;
        this.Health = Health;
        this.isGolden = isGolden;
        return this;
    }

    @Override
    public Fellow clone() {
        try {
            return this.getClass()
                    .getDeclaredConstructor().newInstance()
                    .setStatus(Atk, Health, isGolden);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        // return new Fellow(Name, ID, Description, level, Type); // 这样构造的随从没有技能，不行
    }

    /** 创建一个新的金色随从，其属性值等于该随从+另一个同种随从。 */
    public Fellow newGoldenInstance(Fellow f1, Fellow f2) {
        try {
            return this.getClass()
                    .getDeclaredConstructor().newInstance()
                    .setStatus(f1.Atk + f2.Atk, f1.Health + f2.Health, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void attack(Fellow f) {
        f.Health -= this.Atk;
        this.Health -= f.Atk;
    }

    public void Battlecry(ArrayList<Fellow> Battleground) {
    }

    public void Deathrattle(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
    }

    public void WhileAttack(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
    }

    public void Attack(Fellow target) {
        this.Health -= target.Atk;
        target.Health -= this.Atk;
    }

    public boolean isDead() {
        return this.Health <= 0;
    }
}

/*
 * class Alleycat
 * Attack: 1
 * Health: 1
 * Type: Beast
 */
class Alleycat extends Fellow {
    public Alleycat() {
        super("Alleycat", 0, "Battlecry: Summon a 1/1 Cat", 1, FellowType.Beast);
        this.Atk = 1;
        this.Health = 1;
    }

    public void Battlecry(ArrayList<Fellow> Battleground) {
        Fellow m = new Cat();
        if (Battleground.size() < 7)
            Battleground.add(m);
    }
}

/*
 * class Cat
 * Attack: 1
 * Health: 1
 * Type: Beast
 */
class Cat extends Fellow {
    public Cat() {
        super("Cat", 1, "", 1, FellowType.Beast);
        this.Atk = 1;
        this.Health = 1;
    }
}

/*
 * class Icky_Imp
 * Attack: 1
 * Health: 1
 * Type: Demon
 */
class Icky_Imp extends Fellow {
    public Icky_Imp() {
        super("Icky_Imp", 2, "Deathrattle: Summon two 1/1 Imps", 1, FellowType.Demon);
        this.Atk = 1;
        this.Health = 1;
    }

    public void Deathrattle(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
        Fellow m = new Imp();
        Fellow m2 = new Imp();
        if (Battleground.size() < 7)
            Battleground.add(m);
        if (Battleground.size() < 7)
            Battleground.add(m2);
    };
}

/*
 * class Imp
 * Attack: 1
 * Health: 1
 * Type: Demon
 */
class Imp extends Fellow {
    public Imp() {
        super("Imp", 3, "", 1, FellowType.Demon);
        this.Atk = 1;
        this.Health = 1;
    }
}

/*
 * class Rockpool_Hunter
 * Attack: 2
 * Health: 3
 * Type: Murloc
 */
class Rockpool_Hunter extends Fellow {
    public Rockpool_Hunter() {
        super("Rockpool_Hunter", 4, "Battlecry: Give a friendly murloc +1/+1", 1, FellowType.Murloc);
        this.Atk = 2;
        this.Health = 3;
    }

    public void Battlecry(ArrayList<Fellow> Battleground) {
        for (Fellow m : Battleground) {
            if (m.Type == FellowType.Murloc) {
                m.Atk += 1;
                m.Health += 1;
                break;
            }
        }
    }
}

/*
 * class Implusive_Trickster
 * Attack: 2
 * Health: 2
 * Type: Demon
 */
class Implusive_Trickster extends Fellow {
    public Implusive_Trickster() {
        super("Implusive_Trickster", 5, "Deathrattle: Give another friendly minion +3 Health.", 1, FellowType.Demon);
        this.Atk = 2;
        this.Health = 2;
    }

    public void Deathrattle(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
        Random r = new Random();
        int i = r.nextInt(Battleground.size());
        Fellow m = Battleground.get(i);
        m.Health += 3;
    }
}

/*
 * class Scallywag
 * Attack: 3
 * Health: 1
 * Type: Pirate
 */
class Scallywag extends Fellow {
    public Scallywag() {
        super("Scallywag", 6, "Deathrattle: Summon a 1/1 Pirate. It attacks immediately.", 1, FellowType.Pirate);
        this.Atk = 3;
        this.Health = 1;
    }

    public void Deathrattle(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
        Fellow m = new Sky_Pirate();
        if (Battleground.size() < 7)
            Battleground.add(m);
    }
}

/*
 * class Sky_Pirate
 * Attack: 1
 * Health: 1
 * Type: Pirate
 */
class Sky_Pirate extends Fellow {
    public Sky_Pirate() {
        super("SkyPirate", 7, "", 1, FellowType.Pirate);
        this.Atk = 1;
        this.Health = 1;
    }
}

/*
 * class Harvest_Golem
 * Attack: 2
 * Health: 3
 * Type: Mech
 */
class Harvest_Golem extends Fellow {
    public Harvest_Golem() {
        super("HarvestGolem", 8, "Deathrattle: Summon a 2/1 Damaged Golem", 2, FellowType.Mech);
        this.Atk = 2;
        this.Health = 3;
    }

    public void Deathrattle(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
        Fellow m = new Damaged_Golem();
        if (Battleground.size() < 7)
            Battleground.add(m);
    }
}

/*
 * class Damaged_Golem
 * Attack: 2
 * Health: 1
 * Type: Mech
 */
class Damaged_Golem extends Fellow {
    public Damaged_Golem() {
        super("DamagedGolem", 9, "", 2, FellowType.Mech);
        this.Atk = 2;
        this.Health = 1;
    }
}

/*
 * class Leapfrogger
 * Attack: 3
 * Health: 3
 * Type: Beast
 */
class Leapfrogger extends Fellow {
    public Leapfrogger() {
        super("Leapfrogger", 10, "Deathrattle: Give a friendly Beast +3/+3.", 2, FellowType.Beast);
        this.Atk = 3;
        this.Health = 3;
    }

    public void Deathrattle(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
        for (Fellow m : Battleground) {
            if (m.Type == FellowType.Beast) {
                m.Atk += 3;
                m.Health += 3;
                break;
            }
        }
    }
}

/*
 * class Metaltooth_Leaper
 * Attack: 3
 * Health: 3
 * Type: Mech
 * Battlecry: Give your other Mechs +2 Attack.
 */
class Metaltooth_Leaper extends Fellow {
    public Metaltooth_Leaper() {
        super("MetaltoothLeaper", 11, "Battlecry: Give your other Mechs +2 Attack.", 2, FellowType.Mech);
        this.Atk = 3;
        this.Health = 3;
    }

    @Override
    public void Battlecry(ArrayList<Fellow> Battleground) {
        for (Fellow m : Battleground) {
            if (m.Type == FellowType.Mech) {
                m.Atk += 2;
            }
        }
    }
}

/*
 * class Menagerie_Mug
 * Attack: 2
 * Health: 2
 * Type: General
 * Deathrattle: Give 3 random friendly minions +1/+1.
 */
class Menagerie_Mug extends Fellow {
    public Menagerie_Mug() {
        super("MenagerieMug", 12, "Deathrattle: Give 3 friendly minions +1/+1.", 2, FellowType.General);
        this.Atk = 2;
        this.Health = 2;
    }

    public void Deathrattle(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
        Random r = new Random();
        for (int i = 0; i < 3; i++) {
            int j = r.nextInt(Battleground.size());
            Fellow m = Battleground.get(j);
            m.Atk += 1;
            m.Health += 1;
        }
    }
}

/*
 * class Nathrezim_Overseer
 * Attack: 2
 * Health: 4
 * Type: Demon
 * Battlecry: Give a friendly Demon +2/+2.
 */
class Nathrezim_Overseer extends Fellow {
    public Nathrezim_Overseer() {
        super("NathrezimOverseer", 13, "Battlecry: Give a friendly Demon +2/+2.", 2, FellowType.Demon);
        this.Atk = 2;
        this.Health = 4;
    }

    public void Battlecry(ArrayList<Fellow> Battleground) {
        for (Fellow m : Battleground) {
            if (m.Type == FellowType.Demon) {
                m.Atk += 2;
                m.Health += 2;
                break;
            }
        }
    }
}

/*
 * class Sewer_rat
 * Attack: 3
 * Health: 2
 * Type: Beast
 * Deathrattle: Summon a 2/4 Turtle.
 */
class Sewer_Rat extends Fellow {
    public Sewer_Rat() {
        super("SewerRat", 14, "Deathrattle: Summon a 2/4 Turtle.", 2, FellowType.Beast);
        this.Atk = 3;
        this.Health = 2;
    }

    public void Deathrattle(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
        Fellow m = new Half_Shell();
        if (Battleground.size() < 7)
            Battleground.add(m);
    }
}

/*
 * class Half_Shell
 * Attack: 2
 * Health: 4
 * Type: Beast
 */
class Half_Shell extends Fellow {
    public Half_Shell() {
        super("HalfShell", 15, "", 1, FellowType.Beast);
        this.Atk = 2;
        this.Health = 4;
    }
}

/*
 * class Spawn_of_NZoth
 * Attack: 2
 * Health: 2
 * Type: General
 * Deathrattle: Give your minions +1/+1.
 */
class Spawn_of_NZoth extends Fellow {
    public Spawn_of_NZoth() {
        super("SpawnofN'Zoth", 16, "Deathrattle: Give your minions +1/+1.", 2, FellowType.General);
        this.Atk = 2;
        this.Health = 2;
    }

    public void Deathrattle(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
        for (Fellow m : Battleground) {
            m.Atk += 1;
            m.Health += 1;
        }
    }
}

/*
 * class Felfin_Navigator
 * Attack: 4
 * Health: 4
 * Type: Murloc
 * Battlecry: Give your other Murlocs +1/+1.
 */
class Felfin_Navigator extends Fellow {
    public Felfin_Navigator() {
        super("FelfinNavigator", 17, "Battlecry: Give your other Murlocs +1/+1.", 3, FellowType.Murloc);
        this.Atk = 4;
        this.Health = 4;
    }

    public void Battlecry(ArrayList<Fellow> Battleground) {
        for (Fellow m : Battleground) {
            if (m.Type == FellowType.Murloc) {
                m.Atk += 1;
                m.Health += 1;
            }
        }
    }
}

/*
 * class Houndmaster
 * Attack: 4
 * Health: 3
 * Type: General
 * Battlecry: Give a friendly Beast +2/+3.
 */
class Houndmaster extends Fellow {
    public Houndmaster() {
        super("Houndmaster", 18, "Battlecry: Give a friendly Beast +2/+3.", 3, FellowType.General);
        this.Atk = 4;
        this.Health = 3;
    }

    public void Battlecry(ArrayList<Fellow> Battleground) {
        for (Fellow m : Battleground) {
            if (m.Type == FellowType.Beast) {
                m.Atk += 2;
                m.Health += 3;
                break;
            }
        }
    }
}

/*
 * class Rat_Pack
 * Attack: 2
 * Health: 2
 * Type: Beast
 * Deathrattle: Summon a number of 1/1 Rats equal to this minion's Attack.
 */
class Rat_Pack extends Fellow {
    public Rat_Pack() {
        super("RatPack", 19, "Deathrattle: Summon a number of 1/1 Rats equal to this minion's Attack.", 3,
                FellowType.Beast);
        this.Atk = 2;
        this.Health = 2;
    }

    public void Deathrattle(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
        for (int i = 0; i < this.Atk; i++) {
            Fellow m = new Rat();
            if (Battleground.size() < 7)
                Battleground.add(m);
        }
    }
}

/*
 * class Rat
 * Attack: 1
 * Health: 1
 * Type: Beast
 */
class Rat extends Fellow {
    public Rat() {
        super("Rat", 20, "", 1, FellowType.Beast);
        this.Atk = 1;
        this.Health = 1;
    }
}

/*
 * class Screwjunk_Clunker
 * Attack: 2
 * Health: 5
 * Type: Mech
 * Battlecry: Give a friendly Mech +2/+2.
 */
class Screwjunk_Clunker extends Fellow {
    public Screwjunk_Clunker() {
        super("ScrewjunkClunker", 21, "Battlecry: Give a friendly Mech +2/+2.", 3, FellowType.Mech);
        this.Atk = 2;
        this.Health = 5;
    }

    public void Battlecry(ArrayList<Fellow> Battleground) {
        for (Fellow m : Battleground) {
            if (m.Type == FellowType.Mech) {
                m.Atk += 2;
                m.Health += 2;
                break;
            }
        }
    }
}

/*
 * class Southsea_Strongarm
 * Attack: 4
 * Health: 3
 * Type: Pirate
 * Battlecry: Give your other Pirates +1/+1.
 */
class Southsea_Strongarm extends Fellow {
    public Southsea_Strongarm() {
        super("SouthseaStrongarm", 22, "Battlecry: Give your other Pirates +1/+1.", 3, FellowType.Pirate);
        this.Atk = 4;
        this.Health = 3;
    }

    public void Battlecry(ArrayList<Fellow> Battleground) {
        for (Fellow m : Battleground) {
            if (m.Type == FellowType.Pirate) {
                m.Atk += 1;
                m.Health += 1;
            }
        }
    }
}

/*
 * class Swolefin
 * Attack: 4
 * Health: 2
 * Type: Murloc
 * Battlecry: Gain +2/+1 for each other friendly Murloc.
 */
class Swolefin extends Fellow {
    public Swolefin() {
        super("Swolefin", 23, "Battlecry: Gain +2/+1 for each other friendly Murloc.", 3, FellowType.Murloc);
        this.Atk = 4;
        this.Health = 2;
    }

    public void Battlecry(ArrayList<Fellow> Battleground) {
        for (Fellow m : Battleground) {
            if (m.Type == FellowType.Murloc) {
                this.Atk += 2;
                this.Health += 1;
            }
        }
    }
}

/*
 * class Coldlight_Seer
 * Attack: 2
 * Health: 3
 * Type: Murloc
 * Battlecry: Give your other Murlocs +2 Health.
 */
class Coldlight_Seer extends Fellow {
    public Coldlight_Seer() {
        super("ColdlightSeer", 24, "Battlecry: Give your other Murlocs +2 Health.", 4, FellowType.Murloc);
        this.Atk = 2;
        this.Health = 3;
    }

    public void Battlecry(ArrayList<Fellow> Battleground) {
        for (Fellow m : Battleground) {
            if (m.Type == FellowType.Murloc) {
                m.Health += 2;
            }
        }
    }
}

/*
 * class Cave_Hydra
 * Attack: 2
 * Health: 4
 * Type: Beast
 * WhileAttack: Also deal damage to two random other enemy minions.
 */
class Cave_Hydra extends Fellow {
    public Cave_Hydra() {
        super("CaveHydra", 25, "While this is in your hand, deal damage to two random enemy minions.", 4,
                FellowType.Beast);
        this.Atk = 2;
        this.Health = 4;
    }

    public void WhileAttack(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
        Random r = new Random();
        int i = r.nextInt(EnemyBattleground.size());
        int j = r.nextInt(EnemyBattleground.size());
        while (i == j) {
            j = r.nextInt(EnemyBattleground.size());
        }
        EnemyBattleground.get(i).Health -= this.Atk;
        EnemyBattleground.get(j).Health -= this.Atk;
    }
}

/*
 * class Mechano_Egg
 * Attack: 0
 * Health: 5
 * Type: Mech
 * Deathrattle: Summon a 8/8 Robosaur.
 */
class Mechano_Egg extends Fellow {
    public Mechano_Egg() {
        super("MechanoEgg", 26, "Deathrattle: Summon a 8/8 Robosaur.", 4, FellowType.Mech);
        this.Atk = 0;
        this.Health = 5;
    }

    public void Deathrattle(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
        Fellow m = new Robosaur();
        if (Battleground.size() < 7)
            Battleground.add(m);
    }
}

/*
 * class Robosaur
 * Attack: 8
 * Health: 8
 * Type: Mech
 */
class Robosaur extends Fellow {
    public Robosaur() {
        super("Robosaur", 27, "", 1, FellowType.Mech);
        this.Atk = 8;
        this.Health = 8;
    }
}

/*
 * class Menagerie_Jug
 * Attack: 3
 * Health: 3
 * Type: General
 * Battlecry: Give 3 random friendly minions +2/+2.
 */
class Menagerie_Jug extends Fellow {
    public Menagerie_Jug() {
        super("MenagerieJug", 28, "Battlecry: Give 3 random friendly minions +2/+2.", 4, FellowType.General);
        this.Atk = 3;
        this.Health = 3;
    }

    public void Battlecry(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
        Random r = new Random();
        for (int i = 0; i < 3; i++) {
            int j = r.nextInt(Battleground.size());
            Fellow m = Battleground.get(j);
            m.Atk += 2;
            m.Health += 2;
        }
    }
}

/*
 * class Ring_Matron
 * Attack: 6
 * Health: 6
 * Type: Demon
 * Deathrattle: Summon two 3/2 Imps.
 */
class Ring_Matron extends Fellow {
    public Ring_Matron() {
        super("RingMatron", 29, "Deathrattle: Summon two 3/2 Imps.", 4, FellowType.Demon);
        this.Atk = 6;
        this.Health = 6;
    }

    public void Deathrattle(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
        Fellow m1 = new Fiery_Imp();
        Fellow m2 = new Fiery_Imp();
        if (Battleground.size() < 7) {
            Battleground.add(m1);
            Battleground.add(m2);
        }
    }
}

/*
 * class Fiery_Imp
 * Attack: 3
 * Health: 2
 * Type: Demon
 */
class Fiery_Imp extends Fellow {
    public Fiery_Imp() {
        super("FieryImp", 30, "", 1, FellowType.Demon);
        this.Atk = 3;
        this.Health = 2;
    }
}

/*
 * class Savannah_Highmane
 * Attack: 6
 * Health: 5
 * Type: Beast
 * Deathrattle: Summon two 2/2 Hyenas.
 */
class Savannah_Highmane extends Fellow {
    public Savannah_Highmane() {
        super("SavannahHighmane", 31, "Deathrattle: Summon two 2/2 Hyenas.", 4, FellowType.Beast);
        this.Atk = 6;
        this.Health = 5;
    }

    public void Deathrattle(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
        Fellow m1 = new Hyena();
        Fellow m2 = new Hyena();
        if (Battleground.size() < 7) {
            Battleground.add(m1);
            Battleground.add(m2);
        }
    }
}

/*
 * class Hyena
 * Attack: 2
 * Health: 2
 * Type: Beast
 */
class Hyena extends Fellow {
    public Hyena() {
        super("Hyena", 32, "", 1, FellowType.Beast);
        this.Atk = 2;
        this.Health = 2;
    }
}

/*
 * class Baby_Krush
 * Attack: 6
 * Health: 6
 * Type: Beast
 * WhileAttack: Summon a 8/8 Devilsaur and attack random enemy minion.
 */
class Baby_Krush extends Fellow {
    public Baby_Krush() {
        super("BabyKrush", 33, "While this is in your hand, summon a 8/8 Devilsaur and attack random enemy minion.", 5,
                FellowType.Beast);
        this.Atk = 6;
        this.Health = 6;
    }

    public void WhileAttack(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
        Fellow m = new Devilsaur();
        if (Battleground.size() < 7) {
            Battleground.add(m);
            Random r = new Random();
            int i = r.nextInt(EnemyBattleground.size());
            Battleground.get(Battleground.size() - 1).Attack(EnemyBattleground.get(i));
        }
    }
}

/*
 * class Devilsaur
 * Attack: 8
 * Health: 8
 * Type: Beast
 */
class Devilsaur extends Fellow {
    public Devilsaur() {
        super("Devilsaur", 34, "", 1, FellowType.Beast);
        this.Atk = 8;
        this.Health = 8;
    }
}

/*
 * class King_Bagurgle
 * Attack: 6
 * Health: 3
 * Type: Murloc
 * Battlecry and Deathrattle: Give your other friendly Murlocs +2/+2.
 */
class King_Bagurgle extends Fellow {
    public King_Bagurgle() {
        super("KingBagurgle", 35, "Battlecry and Deathrattle: Give your other friendly Murlocs +2/+2.", 5,
                FellowType.Murloc);
        this.Atk = 6;
        this.Health = 3;
    }

    public void Battlecry(ArrayList<Fellow> Battleground) {
        for (Fellow m : Battleground) {
            if (m.Type == FellowType.Murloc) {
                m.Atk += 2;
                m.Health += 2;
            }
        }
    }

    public void Deathrattle(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
        for (Fellow m : Battleground) {
            if (m.Type == FellowType.Murloc) {
                m.Atk += 2;
                m.Health += 2;
            }
        }
    }
}

/*
 * class Leeroy_the_Reckless
 * Attack: 6
 * Health: 2
 * Type: General
 * Deathrattle: Destroy a random enemy minion.
 */
class Leeroy_the_Reckless extends Fellow {
    public Leeroy_the_Reckless() {
        super("LeeroyTheReckless", 36, "Deathrattle: Destroy a random enemy minion.", 5, FellowType.General);
        this.Atk = 6;
        this.Health = 2;
    }

    public void Deathrattle(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
        Random r = new Random();
        int i = r.nextInt(EnemyBattleground.size());
        EnemyBattleground.remove(i);
    }
}

/*
 * class Palescale_Crocolisk
 * Attack: 4
 * Health: 5
 * Type: Beast
 * Deathrattle: Give another friendly Beasts +8/+8.
 */
class Palescale_Crocolisk extends Fellow {
    public Palescale_Crocolisk() {
        super("PalescaleCrocolisk", 37, "Deathrattle: Give another friendly Beasts +8/+8.", 5, FellowType.Beast);
        this.Atk = 4;
        this.Health = 5;
    }

    public void Deathrattle(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
        for (Fellow m : Battleground) {
            if (m.Type == FellowType.Beast) {
                m.Atk += 8;
                m.Health += 8;
            }
        }
    }
}

/*
 * class Voidlord
 * Attack: 3
 * Health: 9
 * Type: Demon
 * Deathrattle: Summon three 2/3 Voidwalkers.
 */
class Voidlord extends Fellow {
    public Voidlord() {
        super("Voidlord", 38, "Deathrattle: Summon three 2/3 Voidwalkers.", 5, FellowType.Demon);
        this.Atk = 3;
        this.Health = 9;
    }

    public void Deathrattle(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
        Fellow m1 = new Voidwalker();
        Fellow m2 = new Voidwalker();
        Fellow m3 = new Voidwalker();
        if (Battleground.size() < 7) {
            Battleground.add(m1);
            Battleground.add(m2);
            Battleground.add(m3);
        }
    }
}

/*
 * class Voidwalker
 * Attack: 2
 * Health: 3
 * Type: Demon
 */
class Voidwalker extends Fellow {
    public Voidwalker() {
        super("Voidwalker", 39, "", 1, FellowType.Demon);
        this.Atk = 2;
        this.Health = 3;
    }
}

/*
 * class Dread_Admiral_Eliza
 * Attack: 6
 * Health: 7
 * Type: Pirate
 * WhileAttack: Give all friendly minions +3/+2.
 */
class Dread_Admiral_Eliza extends Fellow {
    public Dread_Admiral_Eliza() {
        super("DreadAdmiralEliza", 40, "WhileAttack: Give all friendly minions +3/+2.", 6, FellowType.Pirate);
        this.Atk = 6;
        this.Health = 7;
    }

    public void WhileAttack(ArrayList<Fellow> Battleground) {
        for (Fellow m : Battleground) {
            m.Atk += 3;
            m.Health += 2;
        }
    }
}

/*
 * class Foe_Reaper_4000
 * Attack: 6
 * Health: 9
 * Type: Mech
 * WhileAttack: Also deal damage to two random other enemy minions.
 */
class Foe_Reaper_4000 extends Fellow {
    public Foe_Reaper_4000() {
        super("FoeReaper4000", 41, "WhileAttack: Also deal damage to two random other enemy minions.", 6,
                FellowType.Mech);
        this.Atk = 6;
        this.Health = 9;
    }

    public void WhileAttack(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
        Random r = new Random();
        int i = r.nextInt(EnemyBattleground.size());
        int j = r.nextInt(EnemyBattleground.size());
        while (i == j) {
            j = r.nextInt(EnemyBattleground.size());
        }
        EnemyBattleground.get(i).Health -= this.Atk;
        EnemyBattleground.get(j).Health -= this.Atk;
    }
}

/*
 * class Omega_Buster
 * Attack: 5
 * Health: 5
 * Type: Mech
 * Deathrattle: Summon five 1/1 Microbots. For each that doesn't fit, give your
 * Mechs +1/+1.
 */
class Omega_Buster extends Fellow {
    public Omega_Buster() {
        super("OmegaBuster", 42,
                "Deathrattle: Summon five 1/1 Microbots. For each that doesn't fit, give your Mechs +1/+1.", 6,
                FellowType.Mech);
        this.Atk = 5;
        this.Health = 5;
    }

    public void Deathrattle(ArrayList<Fellow> Battleground, ArrayList<Fellow> EnemyBattleground) {
        for (int i = 0; i < 5; i++) {
            if (Battleground.size() < 7) {
                Fellow m = new Microbot();
                Battleground.add(m);
            } else {
                for (Fellow m : Battleground) {
                    if (m.Type == FellowType.Mech) {
                        m.Atk += 1;
                        m.Health += 1;
                    }
                }
            }
        }
    }
}

/*
 * class Microbot
 * Attack: 1
 * Health: 1
 * Type: Mech
 */
class Microbot extends Fellow {
    public Microbot() {
        super("Microbot", 43, "", 1, FellowType.Mech);
        this.Atk = 1;
        this.Health = 1;
    }
}

/*
 * class Goldrinn_the_Great_Wolf
 * Attack: 4
 * Health: 4
 * Type: Beast
 * Deathrattle: Give your Beasts +5/+5.
 */
class Goldrinn_the_Great_Wolf extends Fellow {
    public Goldrinn_the_Great_Wolf() {
        super("GoldrinnTheGreatWolf", 44, "Deathrattle: Give your Beasts +5/+5.", 4, FellowType.Beast);
        this.Atk = 4;
        this.Health = 4;
    }

    public void Deathrattle(ArrayList<Fellow> Battleground) {
        for (Fellow m : Battleground) {
            if (m.Type == FellowType.Beast) {
                m.Atk += 5;
                m.Health += 5;
            }
        }
    }
}
