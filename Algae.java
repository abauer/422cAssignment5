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
/*
 * Do not change this file.
 */
import assignment5.Critter.TestCritter;

public class Algae extends TestCritter {

	@Override
	public CritterShape viewShape() { return CritterShape.CIRCLE; }
 	public javafx.scene.paint.Color viewColor() { return javafx.scene.paint.Color.GREEN; }

	public String toString() { return "@"; }
	
	public boolean fight(String not_used) { return false; }
	
	public void doTimeStep() {
		setEnergy(getEnergy() + Params.photosynthesis_energy_amount);
	}
}
