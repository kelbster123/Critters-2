package assignment5;
/* CRITTERS Critter.java
 * EE422C Project 5 submission by
 * Varun Prabhu
 * vp6793
 * 15465
 * Kelby Erickson
 * kde528
 * 15495
 * Spring 2018
 */

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/* see the PDF for descriptions of the methods and fields in this class
 * you may add fields, methods or inner classes to Critter ONLY if you make your additions private
 * no new public, protected or default-package code or data can be added to Critter
 */

public abstract class Critter {
	/* NEW FOR PROJECT 5 */
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
	
	private static String myPackage;
	private	static List<Critter> population = new ArrayList<Critter>();
	private static List<Critter> babies = new ArrayList<Critter>();

	// Gets the package name.  This assumes that Critter and its subclasses are all in the same package.
	static {
		myPackage = Critter.class.getPackage().toString().split(" ")[1];
	}
	
	private static List<Critter> lookPopulation = new ArrayList<Critter>(); // population that look() uses
	
	/**
	 * This method looks for another Critter in the spot that is the
	 * given number of steps away from this Critter in the given direction
	 * @param direction is the direction to look in
	 * @param steps is false to look 1 step and true to look 2 steps
	 * @return null if no Critter in location, otherwise String representation of looked at Critter
	 */
	protected final String look(int direction, boolean steps) {
		energy -= Params.look_energy_cost;
		
		int xValue = x_coord;
		int yValue = y_coord;
		
		int numSteps;
		if (steps) {
			numSteps = 1;
		} else {
			numSteps = 0;
		}

		for(int count = 0; count < numSteps; count++) {
			walkHelper(direction);
		}

		for (Critter c : lookPopulation) { // used to separate look stage and step stage
			if (!c.equals(this)) {
				if((c.x_coord == x_coord) && (c.y_coord == y_coord)) {
					x_coord = xValue;
					y_coord = yValue;
					return c.toString();
				}
			}
		}
		x_coord = xValue;
		y_coord = yValue;
		return null;
	}
	
	/* rest is unchanged from Project 4 */
	
	
	private static java.util.Random rand = new java.util.Random();
	public static int getRandomInt(int max) {
		return rand.nextInt(max);
	}
	
	/**
	 * This method sets the seed for the random number generator.
	 * @param new_seed is the seed
	 */
	public static void setSeed(long new_seed) {
		rand = new java.util.Random(new_seed);
	}
	
	
	/* a one-character long string that visually depicts your critter in the ASCII interface */
	public String toString() { return ""; }
	
	private int energy = 0;
	protected int getEnergy() { return energy; }
	
	private int x_coord;
	private int y_coord;
	
	private int moveCount = 0;
	private static boolean doingEcounters = false;
	
	
	/**
	 * This method performs a single walk in the given direction.
	 * @param direction is the direction to walk
	 */
	protected final void walk(int direction) {
		if (moveCount == 0) {
			if((!doingEcounters) || (checkOkToMove(direction, 1))) {
				walkHelper(direction);
			}
		}
		energy -= Params.walk_energy_cost;
		moveCount++;
	}

	/**
	 * This method performs a single run in the given direction.
	 * @param direction is the direction to run
	 */
	protected final void run(int direction) {
		if(moveCount == 0) {
			if((!doingEcounters) || (checkOkToMove(direction, 2))) {
				walkHelper(direction);
				walkHelper(direction);
			}
		}
		energy -= Params.run_energy_cost;
		moveCount++;
	}
	
	/**
	 * This method checks whether the Critter can move without encountering another Critter.
	 * @param direction is the direction the Critter wants to move in
	 * @param steps is the number of spaces by which the Critter wants to move
	 * @return true if the Critter can move without encountering another Critter, false otherwise
	 */
	private boolean checkOkToMove(int direction, int steps) {
		int xValue = x_coord;
		int yValue = y_coord;

		for(int count = 0; count < steps; count++) {
			walkHelper(direction);
		}

		for (Critter c : population) {
			if (!c.equals(this)) {
				if((c.x_coord == x_coord) && (c.y_coord == y_coord)) {
					x_coord = xValue;
					y_coord = yValue;
					return false;
				}
			}
		}
		x_coord = xValue;
		y_coord = yValue;
		return true;
	}
	
	/**
	 * This method performs the reproduction process for a Critter.
	 * @param offspring is the baby Critter being produced
	 * @param direction indicates where the baby will be placed relative to the parent
	 */
	protected final void reproduce(Critter offspring, int direction) {
		if (energy >= Params.min_reproduce_energy) {
			offspring.energy = energy/2; // split energy between offspring and parent
			energy = (int)Math.ceil(energy/2.0);
			offspring.x_coord = x_coord; // set offspring coordinates to parent coordinates
			offspring.y_coord = y_coord;
			offspring.energy += Params.walk_energy_cost; // give offspring energy for a free walk
			offspring.walk(direction); // walk offspring so it is adjacent to parent
			babies.add(offspring);
		}
	}

	public abstract void doTimeStep();
	public abstract boolean fight(String opponent);
	
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
			Class<?> c = Class.forName(myPackage + "." + critter_class_name);
			Critter newCritter = (Critter) c.newInstance();
			newCritter.energy = Params.start_energy;
			newCritter.x_coord = getRandomInt(Params.world_width);
			newCritter.y_coord = getRandomInt(Params.world_height);
			population.add(newCritter);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoClassDefFoundError e) {
			throw new InvalidCritterException(critter_class_name);
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
			Class<?> critterClass = Class.forName(myPackage + "." + critter_class_name);
			List<Critter> result = new ArrayList<Critter>();
			for(Critter c : population) {
				if (critterClass.isInstance(c)) {
					result.add(c);
				}
			}
			return result;
		} catch (ClassNotFoundException e) {
			throw new InvalidCritterException(critter_class_name);
		}
	}
	
	/**
	 * Prints out how many Critters of each type there are on the board.
	 * @param critters List of Critters.
	 */
	public static String runStats(List<Critter> critters) {
		String result = "" + critters.size() + " critters as follows -- ";


		java.util.Map<String, Integer> critter_count = new java.util.HashMap<String, Integer>();
		for (Critter crit : critters) {
			String crit_string = crit.toString();
			Integer old_count = critter_count.get(crit_string);
			if (old_count == null) {
				critter_count.put(crit_string,  1);
			} else {
				critter_count.put(crit_string, old_count.intValue() + 1);
			}
		}
		String prefix = "";
		for (String s : critter_count.keySet()) {
			result += prefix + s + ":" + critter_count.get(s);

			prefix = ", ";
		}
		return result;
	}
	
	/* the TestCritter class allows some critters to "cheat". If you want to 
	 * create tests of your Critter model, you can create subclasses of this class
	 * and then use the setter functions contained here. 
	 * 
	 * NOTE: you must make sure that the setter functions work with your implementation
	 * of Critter. That means, if you're recording the positions of your critters
	 * using some sort of external grid or some other data structure in addition
	 * to the x_coord and y_coord functions, then you MUST update these setter functions
	 * so that they correctly update your grid/data structure.
	 */
	static abstract class TestCritter extends Critter {
		protected void setEnergy(int new_energy_value) {
			super.energy = new_energy_value;
		}
		
		protected void setX_coord(int new_x_coord) {
			super.x_coord = new_x_coord;
		}
		
		protected void setY_coord(int new_y_coord) {
			super.y_coord = new_y_coord;
		}
		
		protected int getX_coord() {
			return super.x_coord;
		}
		
		protected int getY_coord() {
			return super.y_coord;
		}
		

		/*
		 * This method getPopulation has to be modified by you if you are not using the population
		 * ArrayList that has been provided in the starter code.  In any case, it has to be
		 * implemented for grading tests to work.
		 */
		protected static List<Critter> getPopulation() {
			return population;
		}
		
		/*
		 * This method getBabies has to be modified by you if you are not using the babies
		 * ArrayList that has been provided in the starter code.  In any case, it has to be
		 * implemented for grading tests to work.  Babies should be added to the general population 
		 * at either the beginning OR the end of every timestep.
		 */
		protected static List<Critter> getBabies() {
			return babies;
		}
	}

	/**
	 * Clear the world of all critters, dead and alive
	 */
	public static void clearWorld() {
		// Complete this method.
	}


	/**
	 * This method performs one time step for each Critter in the Critter collection.
	 */
	public static void worldTimeStep() {
		lookPopulation = new ArrayList<Critter>();
		for (Critter c : population) {
			try {
				Critter copy = (Critter) c.getClass().newInstance();
				copy.energy = Params.start_energy;
				copy.x_coord = c.x_coord;
				copy.y_coord = c.y_coord;
				lookPopulation.add(copy);
			} catch (InstantiationException | IllegalAccessException e) { // incorrect user input will never cause these exceptions
				System.out.println("Copying Error");
			}
		}
		
		for (Critter c : population) {
			c.doTimeStep();
		}
		
		lookPopulation = population;
		 
		// remove dead Critters
		for (int idx = population.size() - 1; idx >= 0; idx--) {
			if (population.get(idx).energy <= 0) { // if Critter is out of energy, remove it from the collection
				population.remove(idx);
			}
		}

		doingEcounters = true;

        Critter[][] world = new Critter[Params.world_height][Params.world_width]; // 2D array of chars representing the world
        for (int row = 0; row < world.length; row++) {
            for (int col = 0; col < world[row].length; col++) {
                world[row][col] = null; // initialize world with null critters
            }
        }
        
        for (Critter c : population) { // check for collisions between Critters
            if (world[c.y_coord][c.x_coord] == null) {
                world[c.y_coord][c.x_coord] = c;
            } else {
                Critter otherCritter = world[c.y_coord][c.x_coord];
                boolean fightC = c.fight(otherCritter.toString());
                boolean fightOther = otherCritter.fight(c.toString());
                if ((c.x_coord == otherCritter.x_coord) && (c.y_coord == otherCritter.y_coord)) { // if neither Critter ran away
                    if ((c.energy > 0) && (otherCritter.energy > 0)) {
                        //we reach here only if both Critters are in the same place and both alive
                        int cValue = 0;
                        int otherValue = 0;
                        if (fightC) {
                            cValue = getRandomInt(c.energy);
                        }
                        if (fightOther) {
                            otherValue = getRandomInt(otherCritter.energy);
                        }
                        
                        if (cValue >= otherValue) { // if Critter c wins fight or if tie
                            c.energy += (otherCritter.energy)/2;
                            otherCritter.energy = 0;
                           // population.remove(otherCritter);
                            world[c.y_coord][c.x_coord] = c;
                        } else { // other Critter wins
                        	otherCritter.energy += c.energy/2;
                        	c.energy = 0;
                        //	population.remove(c);
                        }
                    } else if (c.energy > 0) { // if other Critter dead but Critter c alive
                    	world[c.y_coord][c.x_coord] = c;
                    	//population.remove(otherCritter);
                    } else if ((c.energy <= 0) && (otherCritter.energy <= 0)) { // if both Critters dead
                    	world[c.y_coord][c.x_coord] = null;
                    //	population.remove(c);
                    //	population.remove(otherCritter);
                    } else { // if Critter c dead but other Critter alive
                    //	population.remove(c);
                    }
                }
            }
        }

        doingEcounters = false;

        // remove dead Critters
		for (int idx = population.size() - 1; idx >= 0; idx--) {
			population.get(idx).energy -= Params.rest_energy_cost;
			if (population.get(idx).energy <= 0) { // if Critter is out of energy, remove it from the collection
				population.remove(idx);
			}
		}
		
		population.addAll(babies); // put offspring into world
		babies.clear();
		
		for (int count = 0; count < Params.refresh_algae_count; count++) {
			Critter newCritter = new Algae();
			newCritter.energy = Params.start_energy;
			newCritter.x_coord = getRandomInt(Params.world_width);
			newCritter.y_coord = getRandomInt(Params.world_height);
			population.add(newCritter);
		}

		for (Critter c : population) {
			c.moveCount = 0;
		}
	}
	
	public int getX() {
		return x_coord;
	}
	
	public int getY() {
		return y_coord;
	}
	
	/**
	 * This method returns the population ArrayList so that the view can be updated.
	 */
	public static List<Critter> displayWorld() {
		return population;

	}
	
	/**
	 * Performs walk without altering energy.
	 * @param direction is the direction in which the Critter should walk
	 */
	private void walkHelper(int direction) {
		switch (direction) {
			case 0: x_coord = x_coord + 1;
					break;
			case 1: x_coord = x_coord + 1;
					y_coord = y_coord + 1;
					break;
			case 2: y_coord = y_coord + 1;
					break;
			case 3: x_coord = x_coord - 1;
					y_coord = y_coord + 1;
					break;
			case 4: x_coord = x_coord - 1;
					break;
			case 5: x_coord = x_coord - 1;
					y_coord = y_coord - 1;
					break;
			case 6: y_coord = y_coord - 1;
					break;
			case 7: x_coord = x_coord + 1;
					y_coord = y_coord - 1;
					break;
		}
	
		if (x_coord >= Params.world_width) {
			x_coord = 0;
		} else if (x_coord < 0) {
			x_coord = Params.world_width - 1;
		}
		
		if (y_coord >= Params.world_height) {
			y_coord = 0;
		} else if (y_coord < 0) {
			y_coord = Params.world_height - 1;
		}
	}



}
