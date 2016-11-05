package assignment4;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import assignment4.Critter.TestCritter;
import java.awt.Point;
public class JunitTestMyCritterSample1 {

	private static final boolean DEBUG = false;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		TestCritter.clearWorld();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	/**
	 * 1.
	 * Use MakeCritter to create a Critter, and makes sure walk works for 1 step.
	 * @throws InvalidCritterException
	 */
	public void testWalk() throws InvalidCritterException {
		Critter.makeCritter("MyCritter1");
		MyCritter1 m1 = (MyCritter1) Critter.TestCritter.getPopulation().get(0);
		int x1a = m1.getX_coord(); int  y1a = m1.getY_coord();
		m1.doTimeStep();
		int x1b = m1.getX_coord(); int  y1b = m1.getY_coord();
		assertTrue((x1b - x1a == 1) || (x1b + Params.world_width - x1a) == 1);
		assertTrue(Math.abs(y1b - y1a) == 0);
	}
	
	@Test
	/**
	 * 2.
	 * Should move only once even if walk is called twice in one turn. Width and Height >= 3.
	 * This test does not use MakeCritter.
	 */
	public void testWalkTwiceInOneTurn() {
		MyCritter2 m2 = new MyCritter2();
		int x, y;
		x = 2; y = 2; // Start position
		m2.setX_coord(x);
		m2.setY_coord(y);
		m2.walk(1);
		assertEquals(m2.getX_coord(), x+1);
		assertEquals(m2.getY_coord(), y-1);
		m2.walk(1);
		assertEquals(m2.getX_coord(), x+1);
		assertEquals(m2.getY_coord(), y-1);
	}
	
	//@Test
	/**
	 * 3.
	 * num Critters created at 1 location.  Call WorldTimeStep once.  Ensure that all but
	 * 1 Critter is dead at the end of 1 step.
	 * This test uses methods that student solutions may not have.  It illustrates 
	 */
	/*public void testFightsWithoutRun() {
		int x = 0; int y = 0;
		Point p = new Point(x, y);
		int num = 10;
		for (int i = 0; i < num; i++) {
			MyCritter5 c = new MyCritter5();
			c.init();							// initialize critter.
			c.setX_coord(x);
			c.setY_coord(y);
			TestCritter.getPopulation().add(c);
			c.addToMap();
		}
		assertEquals(num, TestCritter.getPop().get(p).size(), num);
		if (DEBUG) System.out.println(TestCritter.getPop());
		Critter.worldTimeStep();
		if (DEBUG) {
			System.out.println(TestCritter.getPop());	
			Critter.displayWorld();
		}
		assertEquals(1, TestCritter.getPop().get(p).size());
	}*/
}
