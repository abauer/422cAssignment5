package assignment5;

import java.util.*;

public class MyCritter1 extends Critter.TestCritter {

	@Override
	public void doTimeStep() {
		walk(0);
	}

	@Override
	public boolean fight(String opponent) {
		if (getEnergy() > 10) return true;
		return false;
	}

	@Override
	public CritterShape viewShape() {
		return null;
	}

	public String toString() {
		return "1";
	}
	
	public void test (List<Critter> l) {
		
	}
}
