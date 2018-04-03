package assignment5;
/* CRITTERS Critter3.java
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
 * This Critter is focused on reproduction.
 * Each turn it is alive, it reproduces if possible during its doTimeStep.
 * Additionally, if it gets into an encounter, it will not want to fight and will reproduce twice instead of wanting to fight.
 * Each critter will not last too long because it reproduces each turn.
 * The ultimate goal of this Critter is to produce a lot of offspring before it dies off.
 */
public class Critter3 extends Critter {

    private static int attemptedReproduces = 0; // number of times Critter3s have attempted reproduction

    private int dir = 0; // current direction of Critter3

    @Override
    public void doTimeStep() {
        Critter baby = new Critter3();
        reproduce(baby, dir);
        attemptedReproduces++;
        dir = (dir+1)%8;
    }

    @Override
    public boolean fight(String opponent) {
        run(dir);
        Critter baby = new Critter3();
        reproduce(baby, dir);
        Critter baby2 = new Critter3();
        reproduce(baby2, (dir+1)%8);
        attemptedReproduces++;
        attemptedReproduces++;
        return false;
    }

    @Override
    public CritterShape viewShape() {
        return null;
    }

    @Override
    public String toString () {
        return "3";
    }
    
    /**
     * This method prints out the specific statistics for this Critter.
     */
    public static void runStats(List<Critter> critters) {
        Critter.runStats(critters);
        
        System.out.println("Critter3s have attempted to reproduce a total of " + attemptedReproduces + " times in this world so far");
    }
}
