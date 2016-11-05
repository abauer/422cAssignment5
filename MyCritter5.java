package assignment4;

import assignment4.Critter.TestCritter;

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
	public String toString () {
		return "5";
	}
}
