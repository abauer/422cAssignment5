/* CRITTERS Critter3.java
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
 * TimidCritter avoids all interactions with other critters, except with algae.
 * TimidCritter invokes the look method to check to make sure it's not walking onto another critter.
 * It will only fight with algae, won't flee, and never reproduces.
 */
public class TimidCritter extends Critter {
    @Override
    public CritterShape viewShape() {
        return CritterShape.STAR;
    }
    @Override
    public javafx.scene.paint.Color viewColor() {
        return Color.CHOCOLATE;
    }

    @Override
    public String toString() {
        return "T";
    }

    public boolean fight(String opponent) {
        return opponent.equals("@");
    }

    @Override
    public void doTimeStep() {
        int direction;
        String lookResult;
        do {
            direction = Critter.getRandomInt(8);
            lookResult = look(direction, false);
        } while (lookResult != null && !lookResult.equals("@"));
        walk(direction);
    }
}
