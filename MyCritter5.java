package assignment5;

import assignment5.Critter.TestCritter;

public class MyCritter5 extends TestCritter {
	
	boolean willFight;

	@Override
	public void doTimeStep() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean fight(String opponent) {

		if (opponent.equals("@"))
			return true;
		willFight = getRandomInt(1) == 1;
		return willFight;
	}

	@Override
	public CritterShape viewShape() {
		return null;
	}

	@Override
	public String toString () {
		return "5";
	}
}
