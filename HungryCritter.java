/* CRITTERS HungryCritter.java
 * EE422C Project 4 submission by
 * Anthony Bauer
 * amb6869
 * 16480
 * Grant Uy
 * gau84
 * 61480
 * Slip days used: <0>
 * Fall 2016
 */

package assignment5;

import javafx.scene.paint.Color;

/**
 * HungryCritter
 * The HungryCritter will move in a random default direction
 * Upon entering a fight with nonAlgae the Hungry Critter will look for an empty spot and move to it
 */
public class HungryCritter extends Critter {

    @Override
    public CritterShape viewShape() {
        return CritterShape.TRIANGLE;
    }

    @Override
    public Color viewOutlineColor() {
        return Color.BLACK;
    }

    @Override
    public Color viewFillColor() {
        return Color.FIREBRICK;
    }

    @Override
    public String toString() { return "H"; }

    private int dir;

    public HungryCritter() {
        dir = Critter.getRandomInt(8);
    }

    public boolean fight(String opponent) {
        if(opponent.equals("@"))
            return true;
        for (int dir = 0; dir < 8; dir++) {
            if(this.look(dir, false) == null) {
                walk(dir);
                return false;
            }
        }
        for (int dir = 0; dir < 8; dir++) {
            if(this.look(dir, true) == null) {
                run(dir);
                return false;
            }
        }
        return false;
    }

    @Override
    public void doTimeStep() {
        walk(dir);
    }
}
