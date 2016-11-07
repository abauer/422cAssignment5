/* CRITTERS Main.java
 * EE422C Project 5 submission by
 * Anthony Bauer
 * amb6869
 * 16480
 * Grant Uy
 * gau84
 * 16480
 * Slip days used: <0>
 * Fall 2016
 */
package assignment5;

import javafx.scene.paint.Color;

/**
 * GreedyCritter always fights and tries to move toward algae.
 */
public class GreedyCritter extends Critter {
    @Override
    public CritterShape viewShape() {
        return CritterShape.STAR;
    }
    @Override
    public Color viewColor() {
        return Color.CADETBLUE;
    }

    @Override
    public String toString() {
        return "O";
    }

    public boolean fight(String opponent) {
        return true;
    }

    @Override
    public void doTimeStep() {
        for (int i=0; i<16; i++) {
            String lookResult = look(i%8, i>=8);
            if (lookResult != null && lookResult.equals("@")) {
                if (i >= 8)
                    run(i%8);
                else
                    walk(i%8);
                return;
            }
        }
        // try to reproduce if not moving, otherwise just run in a random direction
        int direction = Critter.getRandomInt(8);
        if (getEnergy() > Params.start_energy) {
            reproduce(new GreedyCritter(), direction);
        } else {
            run(direction);
        }
    }
}
