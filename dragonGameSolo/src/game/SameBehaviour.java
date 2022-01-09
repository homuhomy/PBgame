package game;

import java.io.Serializable;

/**
 * Same behaviour class.
 * Extended by Dragon and Tower
 *
 */
public abstract class SameBehaviour implements Serializable{
    private static final long serialVersionUID = 9134702883387401045L;

    protected int atkPoint;
    protected float critChance = 0.1f;
    protected float accuracy = 0.8f;

    /**
     * Displays the stats
     */
    public abstract void displayStats();


    //getters
    public float getCritChance() {
        return critChance;
    }

    public int getAtkPoint() {
        return atkPoint;
    }
}
