/* CRITTERS Critter1.java
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
 * Critter1
 * The Critter1 will always move forward
 * Upon entering a fight the Critter1 will preserve half its energy in the form of an offspring
 */
public class Critter1 extends Critter {

    @Override
    public String toString() { return "1"; }

    private int dir;

    public Critter1() {
        dir = Critter.getRandomInt(8);
    }

    public boolean fight(String not_used) {
        reproduce(new Critter1(), Critter.getRandomInt(8));
        return true;
    }

    @Override
    public void doTimeStep() {
        walk(dir);
        if (getEnergy() > Params.start_energy+1) {
            reproduce(new Critter1(), Critter.getRandomInt(8));
        }
    }
}
