package game;

/**
 * Tower in the game
 *
 */
public class Tower extends SameBehaviour {
    private static final long serialVersionUID = 3627280085273899112L;

    /**
     * Constructor
     */
    public Tower() {
        atkPoint = 7;
        critChance = 0.2f;
        accuracy = 0.8f;
    }

    public void upAtkPoint() {
        atkPoint++;
    }

    public void upCritChance() {
        critChance += 0.05f;
        critChance = Math.min(0.5f, critChance);
    }

    public void upAccuracy() {
        accuracy += 0.04f;
        accuracy = Math.min(1, accuracy);
    }

    public void decreaseAtkPoint() {
        atkPoint--;
    }

    public void decreaseAccuracy(float amount) {
        accuracy -= amount;

    }

    @Override
    public void displayStats() {
        System.out.println("Tower's AttackPoint: " + atkPoint);
        System.out.println("Tower's Critical Chance: " + critChance);
        System.out.println("Tower's Accuracy: " + (accuracy * 100) + "%");
    }


}
