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

package assignment4;

/**
 * NinjaCritters are special because they don't show up on the map; their String representation is a space.
 * However, NinjaCritters will always fight when encountered.
 * Because ninjas are always trying to climb the social ladder, NinjaCritters only move in upward directions.
 */
public class Critter3 extends Critter {
    @Override
    public String toString() {
        return "3"; //intended to be a " " (space
    }

    public boolean fight(String not_used) {
        return true;
    }

    @Override
    public void doTimeStep() {
		// only moves upward, but somewhat infrequently
        if (Critter.getRandomInt(3) == 0)
            walk(Critter.getRandomInt(3)+1);
        // generally doesn't reproduce unless it has a surplus of energy
        if (getEnergy() > Params.start_energy)
            reproduce(new Critter3(), 6);
    }
}
