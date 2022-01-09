package game;

import java.io.Serializable;

/**
 * Wall in the game
 *
 */
public class Wall implements Serializable{
    private static final long serialVersionUID = 3374495944795862612L;

    private int hp = 100;
    private float blockPercent = 0.1f;

    public void upHp() {
        hp += 75;
    }

    public void upBlockPercent() {
        blockPercent += 0.05f;
        blockPercent = Math.min(0.5f, blockPercent);
    }

    public void decreaseHp(int atkPoint) {
        hp -= atkPoint;
    }

    public double getBlockPercent() {
        return blockPercent;
    }

    public int getHp() {
        return hp;
    }

    public void displayStats() {
        System.out.println("Wall's HealthPoint: " + hp);
        System.out.println("Wall's Block: " + (blockPercent * 100) + "%");

    }


}
