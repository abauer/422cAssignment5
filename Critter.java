/* CRITTERS Critter.java
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
package assignment5;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Critter {
	private static String myPackage;
	private	static List<Critter> population = new ArrayList<>();
	private static List<Critter> babies = new ArrayList<>();

	public enum CritterShape {
		CIRCLE,
		SQUARE,
		TRIANGLE,
		DIAMOND,
		STAR
	}

	/* the default color is white, which I hope makes critters invisible by default
	 * If you change the background color of your View component, then update the default
	 * color to be the same as you background
	 *
	 * critters must override at least one of the following three methods, it is not
	 * proper for critters to remain invisible in the view
	 *
	 * If a critter only overrides the outline color, then it will look like a non-filled
	 * shape, at least, that's the intent. You can edit these default methods however you
	 * need to, but please preserve that intent as you implement them.
	 */
    public javafx.scene.paint.Color viewColor() {
        return javafx.scene.paint.Color.WHITE;
    }

    public javafx.scene.paint.Color viewOutlineColor() { return viewColor(); }
    public javafx.scene.paint.Color viewFillColor() { return viewColor(); }
    public abstract CritterShape viewShape();

	// Gets the package name.  This assumes that Critter and its subclasses are all in the same package.
	static {
		myPackage = Critter.class.getPackage().toString().split(" ")[1];
	}
	
	private static java.util.Random rand = new java.util.Random();

    /**
     * Gets a random number from a (possibly) seeded generator.
     * @param max the exclusive upper bound
     * @return a random number in the range [0,max)
     */
	public static int getRandomInt(int max) {
		return rand.nextInt(max);
	}

    /**
     * Resets the random number generator, seeding it with a given value.
     * @param new_seed the new seed value
     */
	public static void setSeed(long new_seed) {
		rand = new java.util.Random(new_seed);
	}
	

    /**
     * Gets the string representation of a Critter.
     * @return a one-character long string that visually depicts your critter in the ASCII interface
     */
	public String toString() { return ""; }
	
	private int energy = 0;
	protected int getEnergy() { return energy; }
	
	private int x_coord;
	private int y_coord;

	private boolean hasMoved = false;

    /**
     * Internal method for moving a Critter a given number of tiles in a given direction
     * Only allows one movement per time step
     * @param direction integer in range [0,7] corresponding to a direction
     * @param distance number of squares to travel
     */
	private void moveInDirection(int direction, int distance) {
		if (hasMoved) return;
        hasMoved = true;
		// right
		if (direction == 7 || direction == 0 || direction == 1)
			x_coord = (x_coord+distance)%Params.world_width;
		// left
		else if (direction == 3 || direction == 4 || direction == 5)
			x_coord = (x_coord-distance)%Params.world_width;
		// up
		if (direction == 1 || direction == 2 || direction == 3)
			y_coord = (y_coord-distance)%Params.world_height;
		// down
		else if (direction == 5 || direction == 6 || direction == 7)
			y_coord = (y_coord+distance)%Params.world_height;
	}

    /**
     * Moves a Critter one tile in a given direction (at an energy cost)
     * @param direction integer in range [0,7] corresponding to a direction
     */
	protected final void walk(int direction) {
		energy -= Params.walk_energy_cost;
		moveInDirection(direction, 1);
	}

    /**
     * Moves a Critter two tiles in a given direction (at an energy cost)
     * @param direction integer in range [0,7] corresponding to a direction
     */
	protected final void run(int direction) {
		energy -= Params.run_energy_cost;
		moveInDirection(direction, 2);
	}

    /**
     * Initializes a new Critter one tile away from this Critter in a given direction
     * @param offspring newly-created Critter to initialize
     * @param direction integer in range [0,7] corresponding to a direction
     */
	protected final void reproduce(Critter offspring, int direction) {
		if (energy < Params.min_reproduce_energy) return;
		offspring.energy = energy/2; // round down
		energy -= energy/2; // round up
		// spawn offspring adjacent to parent
		offspring.x_coord = x_coord;
		offspring.y_coord = y_coord;
		offspring.moveInDirection(direction, 1);
		babies.add(offspring);
	}

    /**
     * Abstract method for Critters' custom behavior during each world time step
     */
	public abstract void doTimeStep();

    /**
     * Checks whether a Critter wants to fight, giving it an opportunity to flee
     * @param opponent string representation of the opposing Critter
     * @return whether or not the Critter wants to fight
     */
	public abstract boolean fight(String opponent);

	protected String look(int direction, boolean steps) {
        energy -= Params.look_energy_cost;
        int distance = steps ? 2 : 1;
        int x = x_coord;
        int y = y_coord;
        // right
        if (direction == 7 || direction == 0 || direction == 1)
            x = (x_coord+distance)%Params.world_width;
        // left
        else if (direction == 3 || direction == 4 || direction == 5)
            x = (x_coord-distance)%Params.world_width;
        // up
        if (direction == 1 || direction == 2 || direction == 3)
            y = (y_coord-distance)%Params.world_height;
        // down
        else if (direction == 5 || direction == 6 || direction == 7)
            y = (y_coord+distance)%Params.world_height;
        // return
        if (isCached) {
            Critter crit = cacheMap.get(hashCoords(x,y));
            return (crit != null) ? crit.toString() : null;
        } else {
            for (Critter c : population) {
                if (c.x_coord == x && c.y_coord == y)
                    return c.toString();
            }
            return null;
        }
    }
	
	/**
	 * create and initialize a Critter subclass.
	 * critter_class_name must be the unqualified name of a concrete subclass of Critter, if not,
	 * an InvalidCritterException must be thrown.
	 * (Java weirdness: Exception throwing does not work properly if the parameter has lower-case instead of
	 * upper. For example, if craig is supplied instead of Craig, an error is thrown instead of
	 * an Exception.)
	 * @param critter_class_name
	 * @throws InvalidCritterException
	 */
	public static void makeCritter(String critter_class_name) throws InvalidCritterException {
		try {
			Critter c = (Critter)Class.forName(myPackage+"."+critter_class_name).newInstance();
			c.energy = Params.start_energy;
			c.x_coord = getRandomInt(Params.world_width);
			c.y_coord = getRandomInt(Params.world_height);
			population.add(c);
		} catch(Exception e) {
			throw new InvalidCritterException("Could not find Critter of type "+critter_class_name);
		}
	}
	
	/**
	 * Gets a list of critters of a specific type.
	 * @param critter_class_name What kind of Critter is to be listed.  Unqualified class name.
	 * @return List of Critters.
	 * @throws InvalidCritterException
	 */
	public static List<Critter> getInstances(String critter_class_name) throws InvalidCritterException {
		try {
			Class c = Class.forName(myPackage+"."+critter_class_name);
			return population.stream().filter(c::isInstance).collect(Collectors.toList());
		} catch(Exception e) {
			throw new InvalidCritterException("Could not find Critter of type "+critter_class_name);
		}
	}
	
	/**
	 * Prints out how many Critters of each type there are on the board.
	 * @param critters List of Critters.
	 */
	public static void runStats(List<Critter> critters) {
		System.out.print("" + critters.size() + " critters as follows -- ");
		java.util.Map<String, Integer> critter_count = new java.util.HashMap<String, Integer>();
		for (Critter crit : critters) {
			String crit_string = crit.toString();
			Integer old_count = critter_count.get(crit_string);
			if (old_count == null) {
				critter_count.put(crit_string,  1);
			} else {
				critter_count.put(crit_string, old_count + 1);
			}
		}
		String prefix = "";
		for (String s : critter_count.keySet()) {
			System.out.print(prefix + s + ":" + critter_count.get(s));
			prefix = ", ";
		}
		System.out.println();		
	}
	
	/** the TestCritter class allows some critters to "cheat". If you want to
	 * create tests of your Critter model, you can create subclasses of this class
	 * and then use the setter functions contained here. 
	 *
	 */
	static abstract class TestCritter extends Critter {
		/**
		 * This method sets the energy of the Critter
		 * @param new_energy_value new energy value
		 */
		protected void setEnergy(int new_energy_value) {
			super.energy = new_energy_value;
		}

		/**
		 * This method sets the X coordinate of the Critter
		 * @param new_x_coord new x coordinate
		 */
		protected void setX_coord(int new_x_coord) {
			super.x_coord = new_x_coord;
		}

		/**
		 * Sets the Y coordinate of the Critter
		 * @param new_y_coord new y coordinate
		 */
		protected void setY_coord(int new_y_coord) {
			super.y_coord = new_y_coord;
		}

		/**
		 * Returns the X coordinate
		 * @return current x value of Critter
		 */
		protected int getX_coord() {
			return super.x_coord;
		}

		/**
		 * Returns the Y coordinate
		 * @return current y value of Critter
		 */
		protected int getY_coord() {
			return super.y_coord;
		}

		/**
		 * Returns the list of existing Critters
		 * @return the Critters alive at the start of this world time step
		 */
		protected static List<Critter> getPopulation() {
			return population;
		}
		
		/**
		 * Returns the newly created Critters
		 * @return the offspring created during this world time step
		 */
		protected static List<Critter> getBabies() {
			return babies;
		}
	}

	/**
	 * Clear the world of all critters, dead and alive
	 */
	public static void clearWorld() {
		population = new ArrayList<>();
		babies = new ArrayList<>();
	}

	/**
	 * Create a one to one hash given the X and Y coordinates
	 * @param x the x coordinate to hash
	 * @param y the y coordinate to hash
	 * @return the hashcode generated from x and y
	 */
	private static int hashCoords(int x, int y) {
		int w = Params.world_width;
		int h = Params.world_height;
		return (w>h) ? x+y*w : y+x*h;
	}

	/**
	 * Get the X value from the hashcode
	 * @param hash the hashcode containing the X value
	 * @return the X value hashed in the hashcode
	 */
	private static int unhashX(int hash) {
		int w = Params.world_width;
		int h = Params.world_height;
		return (w>h) ? hash%w : hash/h;
	}

	/**
	 * Get the Y value from the hashcode
	 * @param hash the hashcode containing the Y value
	 * @return the Y value hashed in the hashcode
	 */
	private static int unhashY(int hash) {
		int w = Params.world_width;
		int h = Params.world_height;
		return (w>h) ? hash/w : hash%h;
	}

	/**
	 * This method calculates the hash for Critter c and adds it to the ArrayList in the HashMap hash
	 * If no ArrayList exists at the hash a new one will be created
	 * @param c the Critter to add to the HashMap
	 * @param hash the HashMap the Critter should be added to
	 */
	private static void updateHash(Critter c, HashMap<Integer,LinkedList<Critter>> hash){
		int aHash = hashCoords(c.x_coord, c.y_coord);
		if (!hash.containsKey(aHash))
			hash.put(aHash,new LinkedList<>());
		hash.get(aHash).add(c);
	}

	private static HashMap<Integer,Critter> cacheMap;
    private static boolean isCached;
	/**
	 * This method performs the timestep for each Critter as follows
	 * Allow each Critter to perform individual timestep
	 * Determine locations of multiple Critters (where conflicts happen)
	 * Resolve conflicts such that each Critter can run before the fight, removing the loser
	 * Remove all Critters that are dead from world
	 * Add new Algae and babies to world
	 */
	public static void worldTimeStep() {
        population.forEach(c -> cacheMap.put(hashCoords(c.x_coord, c.y_coord), c));
		// do time step
        isCached = true;
		population.forEach(c -> {
			c.hasMoved = false;
			c.doTimeStep();
		});
        isCached = false;
		// pre-process locations
		Set<Integer> locations = new HashSet<>();
		HashMap<Integer,LinkedList<Critter>> crits = new HashMap<>();
		Set<Integer> collisions = new HashSet<>();
		for(Critter c : population) {	//identify collisions
			if(c.energy<=0) //Critter is dead, ignore it
				continue;
			int hash = hashCoords(c.x_coord, c.y_coord);
			// create list if necessary, then add critter
			if (!crits.containsKey(hash))
				crits.put(hash,new LinkedList<>());
			crits.get(hash).add(c);
			// check if seen, adding to collisions if necessary
			if (!locations.contains(hash))
				locations.add(hash);
			else
				collisions.add(hash);
		}
		// handle collisions
		for (Integer hash : collisions){
			LinkedList<Critter> result = crits.get(hash);
			int origx = unhashX(hash);
			int origy = unhashY(hash);
			while (result.size() > 1) {
				Critter A = result.poll();
				Critter B = result.poll();
				boolean aFlag = A.fight(B.toString());	//give critter option to move
				boolean bFlag = B.fight(A.toString());
				// check if either critter moved or died
				boolean aMoved = A.x_coord!=origx || A.y_coord!=origy;
				boolean aDied = A.energy <= 0;
				boolean bMoved = B.x_coord!=origx || B.y_coord!=origy;
				boolean bDied = B.energy <= 0;
				//determine if move was valid
				if (aMoved && crits.containsKey(hashCoords(A.x_coord,A.y_coord))){	//if space is already occupied, move back to conflict space
					A.x_coord = origx;
					A.y_coord = origy;
					aMoved = false;
				}
				if (bMoved && crits.containsKey(hashCoords(B.x_coord,B.y_coord))){	//if space is already occupied, move back to conflict space
					B.x_coord = origx;
					B.y_coord = origy;
					bMoved = false;
				}
				if(!(aMoved||aDied||bMoved||bDied)) {
					int aRoll = aFlag ? getRandomInt(A.getEnergy()+1) : 0;       //if we are still fighting, roll
					int bRoll = bFlag ? getRandomInt(B.getEnergy()+1) : 0;
                    if (aRoll >= bRoll) {    //A wins tiebreaker
						A.energy += B.energy / 2;
						B.energy = 0;
						bDied = true;
					} else {
						B.energy += A.energy / 2;
						A.energy = 0;
						aDied = true;
					}
				}
				if(!aDied)
					updateHash(A,crits);
				if(!bDied)
					updateHash(B,crits);
			}
		}
		// remove dead stuff
		Iterator<Critter> it = population.iterator();
		while (it.hasNext()) {
			Critter c = it.next();
			c.energy-=Params.rest_energy_cost;
			if (c.energy <= 0)
				it.remove();
		}
		// add new Algae
		for(int i = 0; i<Params.refresh_algae_count; i++) {
			try{
				makeCritter("Algae");
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		// handle reproduce
		population.addAll(babies);
		babies.clear();
	}

	/**
	 * This method prints the world borders and Critters in the world to the screen
	 * Typical use case is to be called from show command.
	 */
	public static void displayWorld() {
		// create +---+
		String border = "+"
				+ Collections.nCopies(Params.world_width,"-").stream().collect(Collectors.joining())
				+ "+";
		// upper border
		System.out.println(border);
		// pre-process all critters for simple lookups
		HashMap<Integer,String> critterIcons = new HashMap<>();
		for (Critter c : population)
			critterIcons.put(hashCoords(c.x_coord, c.y_coord), c.toString());
		// iterate over all locations
		for (int r=0; r<Params.world_height; r++) { //"y"
			System.out.print("|");
			for (int c=0; c<Params.world_width; c++) { //"x"
				int hash = hashCoords(c,r); // purposely "backwards"
				if (critterIcons.containsKey(hash)) {
					System.out.print(critterIcons.get(hash));
				} else {
					System.out.print(" ");
				}
			}
			System.out.print("|\n");
		}
		// lower border
		System.out.println(border);
	}
}
