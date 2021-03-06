package game;

/**
 * Dragon in the game.
 *
 */
public class Dragon extends SameBehaviour {
    private static final long serialVersionUID = 5290663642090638364L;

    private int level = 1;
    private int hp = 100;

    private int currentHp = hp;
    private int currentAtkPoint;

    /**
     * Constructor
     */
    public Dragon() {
        atkPoint = 7;
        recover();
    }

    /**
     * Recovers the dragon
     */
    public void recover() {
        currentHp = hp;
        currentAtkPoint = atkPoint;
        critChance = 0.2f;
        accuracy = 0.8f;
    }

    /**
     * Levels up the dragon
     */
    public void levelUp() {
        level++;
        hp += 15;
        atkPoint++;
        critChance += 0.02f;
    }

    @Override
    public void displayStats() {
        System.out.println("Dragon's Level: " + level);
        System.out.println("Dragon's HealthPoint: " + currentHp);
        System.out.println("Dragon's AttackPoint: " + atkPoint);
        System.out.println("Dragon's Critical Chance: " + critChance);
        System.out.println("Dragon's Accuracy: " + (accuracy * 100) + "%");
    }


    public void decreaseHp(int atkPoint) {
        currentHp -= atkPoint;

    }

    //getters
    public int getHp() {
        return hp;
    }

    public int getAtkPoint() {
        return currentAtkPoint;
    }



}
