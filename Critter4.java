/* CRITTERS Critter4.java
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
 * SquareCritters have a unique movement pattern: they always move in a square shape.
 * The square's side length is randomly determined on creation but will never change for a given Critter4.
 * SquareCritters are peaceful creatures--they only play fair and square--so they'll only "fight" Algae.
 */
public class Critter4 extends Critter {
    private int dir;
    private int sideLength;
    private int stepsLeft;

    public Critter4() {
        dir = 0;
        sideLength = Critter.getRandomInt(Params.world_width-1)+2; // 2 to world_width
        stepsLeft = sideLength-1; // number of steps necessary is always one less than the side length
    }

    @Override
    public String toString() { return "4"; }

    public boolean fight(String opponent) {
        return opponent.equals("@");
    }

    @Override
    public void doTimeStep() {
        walk(dir);
        // change direction if necessary
        if (--stepsLeft == 0) {
            dir = (dir+2)%8;
            stepsLeft = sideLength-1; // reset steps left
        }
        // 25% chance of reproducing at each timestep if it has plenty of energy
        if (Critter.getRandomInt(4) == 1 && getEnergy() > Params.min_reproduce_energy*2) {
            reproduce(new Critter4(), Critter.getRandomInt(8));
        }
    }
}
