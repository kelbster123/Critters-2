package assignment4;
/* CRITTERS Critter2.java
 * EE422C Project 4 submission by
 * Varun Prabhu
 * vp6793
 * 15465
 * Kelby Erickson
 * kde528
 * 15495
 * Spring 2018
 */

import java.util.List;

/**
 * This Critter is a crazy Critter that only moves in a circle.
 * That is, its direction cycles through the 8 possible directions in order.
 * Its purpose is to complete as many cycles as possible before dying.
 */
public class Critter2 extends Critter{
	
	private static int numCycles = 0; // number of cycles completed by Critter2s
	
	private int direction = 0; // current direction of Critter2
	
	@Override
	public void doTimeStep() {
		walk(direction);
		if (direction < 7) {
			direction++;
		} else {
			direction = 0;
			numCycles++;
		}
	}

	@Override
	public boolean fight(String opponent) {
		int dir = getRandomInt(8);
		String str = look(dir, true); // look() called from fight()
		if (str == null) {
			walk(dir);
		}
		return false;
	}

	@Override
	public String toString () {
		return "2";
	}
	
	/**
     * This method prints out the specific statistics for this Critter.
     */
	public static void runStats(List<Critter> critters) {
        Critter.runStats(critters);
        
        System.out.println("Critter2s have completed a total of " + numCycles + " cycles in this world so far");
    }
}
