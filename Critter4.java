package assignment5;
/* CRITTERS Critter4.java
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
 * This Critter is a dichotomous critter. Each alternating time step, it exhibits different behavior.
 * One time step it walks during its doTimeStep and it fights if encountered.
 * The next time step, it is lazy and does not move during its doTimeStep and it runs away from any encounter.
 * This continues in an alternating fashion.
 */
public class Critter4 extends Critter {

    private static int numRuns = 0; // number of times Critter4s have run
    private static int numFights = 0; // number of times Critter4s have fought
    private static int numWalk = 0; // number of times Critter4s have walked
    private static int numLazy = 0; // number of times Critter4s have stayed in place

    private boolean moveThisTurn = true; // indicates which of the two behaviors the Critter4 will exhibit
    
    @Override
    public void doTimeStep() {
        if(moveThisTurn) {
            walk(getRandomInt(8));
            numWalk++;
        } else{
            numLazy++;
        }
        moveThisTurn = !moveThisTurn;
    }
    
    @Override
    public boolean fight(String opponent) {
        if(moveThisTurn) {
            run(getRandomInt(8));
            numRuns++;
            return(false);
        } else {
            numFights++;
            return true;
        }
    }

    @Override
    public CritterShape viewShape() {
        return null;
    }

    @Override
    public String toString () {
        return "4";
    }
    
    /**
     * This method prints out the specific statistics for this Critter.
     */
    public static String runStats(List<Critter> critters) {
        String s = Critter.runStats(critters);
        s += "\nCritter4s have moved during their doTimeStep a total of " + numWalk + " times in this world so far";
        s += "\nCritter4s have been lazy and not moved a total of " + numLazy + " times in this world so far";
        s += "\nCritter4s have ran from fights a total of " + numRuns + " times in this world so far";
        s += "\nCritter4s have decided to fight a total of " + numFights + " times in this world so far";
        return s;

    }
}
