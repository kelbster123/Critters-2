package assignment5;
/* CRITTERS Critter1.java
 * EE422C Project 5 submission by
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
 * This Critter is an aggressive Critter that always runs during a time step
 * and always chooses to fight another Critter if possible. Its purpose is
 * to fight as many Critters as possible before dying.
 */
public class Critter1 extends Critter {
	
	private static int numFights = 0; // number of fights that Critter1s have attempted to fight

	@Override
	public void doTimeStep() {
		int dir = getRandomInt(8);
		String str = look(dir, true); // look() called from doTimeStep()
		if (str == null) {
			run(dir);
		}
	}

	@Override
	public boolean fight(String opponent) {
		numFights++;
		return true;
	}

	@Override
	public CritterShape viewShape() {
		return CritterShape.DIAMOND;
	}

	@Override
	public javafx.scene.paint.Color viewColor() { return javafx.scene.paint.Color.BLUE; }

	@Override
	public String toString () {
		return "1";
	}
	
	/**
     * This method prints out the specific statistics for this Critter.
     */
    public static String runStats(List<Critter> critters) {
        String s = Critter.runStats(critters);
        s += "\nCritter1s have attempted to fight a total of " + numFights + " fights in this world so far";
        return s;
    }
}
