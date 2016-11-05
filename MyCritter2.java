package assignment4;

/*
 * This Critter is good for testing running away while fighting, and for testing 
 * movement in different directions.
 */
public class MyCritter2 extends MyCritter1 {
	
	private int myDir = 0;
	
	@Override
	public void doTimeStep () {
		walk(myDir);
		myDir = (myDir+1)%8; // change direction each walk call, CCW.
	}
	
	@Override
	/**
	 * Never fights, but always tries to run away.
	 */
	public boolean fight(String opp) {
		run(myDir);
		return false;
	}
	
	public String toString() {
		return "2";
	}

}
