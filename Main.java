package assignment4;
/* CRITTERS Main.java
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
import java.util.Scanner;
import java.io.*;


/*
 * Usage: java <pkgname>.Main <input file> test
 * input file is optional.  If input file is specified, the word 'test' is optional.
 * May not use 'test' argument without specifying input file.
 */
public class Main {
	
    static Scanner kb;	// scanner connected to keyboard input, or input file
    private static String inputFile;	// input file, used instead of keyboard input if specified
    static ByteArrayOutputStream testOutputString;	// if test specified, holds all console output
    private static String myPackage;	// package of Critter file.  Critter cannot be in default pkg.
    private static boolean DEBUG = false; // Use it or not, as you wish!
    static PrintStream old = System.out;	// if you want to restore output to console


    // Gets the package name.  The usage assumes that Critter and its subclasses are all in the same package.
    static {
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }

    /**
     * Main method.
     * @param args args can be empty. If not empty, provide two parameters -- the first is a file name, 
     * and the second is test (for test output, where all output to be directed to a String), or nothing.
     */
    public static void main(String[] args) {     	
        if (args.length != 0) {
            try {
                inputFile = args[0];
                kb = new Scanner(new File(inputFile));			
            } catch (FileNotFoundException e) {
                System.out.println("USAGE: java Main OR java Main <input file> <test output>");
                e.printStackTrace();
            } catch (NullPointerException e) {
                System.out.println("USAGE: java Main OR java Main <input file>  <test output>");
            }
            if (args.length >= 2) {
                if (args[1].equals("test")) { // if the word "test" is the second argument to java
                    // Create a stream to hold the output
                    testOutputString = new ByteArrayOutputStream();
                    PrintStream ps = new PrintStream(testOutputString);
                    // Save the old System.out.
                    old = System.out;
                    // Tell Java to use the special stream; all console output will be redirected here from now
                    System.setOut(ps);
                }
            }
        } else { // if no arguments to main
            kb = new Scanner(System.in); // use keyboard and console
        }

        /* Do not alter the code above for your submission. */
        boolean running = true;
              
        while(running) { // while user has not entered "quit"
           System.out.print("critters>");
           String cmd = kb.nextLine();
           Scanner command = new Scanner(cmd);
           command.useDelimiter("\\s+");
           String s = command.next();
           try {
               switch(s){ // perform operation that corresponds to user input
                   case "quit":
                       if (command.hasNext()) {
                           throw new Exception();
                       }
                       Critter.clearWorld();
                       running = false;
                       break;

                   case "show":
                       if (command.hasNext()) {
                           throw new Exception();
                       }
                       Critter.displayWorld();
                       break;

                   case "step":
                       int count = 1;
                       if (command.hasNextInt()) {
                           count = command.nextInt();
                       }
                       if (command.hasNext()) {
                           throw new Exception();
                       }
                       for (int i = 0; i < count; i++) {
                           Critter.worldTimeStep();
                       }
                       break;

                   case "seed":
                       if (command.hasNextInt()) {
                           int seed = command.nextInt();
                           if(command.hasNext()) {
                               throw new Exception();
                           }
                           Critter.setSeed(seed);
                       }
                       break;

                   case "make":
                	   if (!command.hasNext()) {
                		   throw new Exception();
                	   }
                       String critter = command.next();
                       
                       int crittersToMake = 1;
                       if(command.hasNextInt()) {
                           crittersToMake = command.nextInt();
                       }
                       if(command.hasNext()) {
                           throw new Exception();
                       }

                       for(int numCreated = 0; numCreated < crittersToMake; numCreated++) {
                            Critter.makeCritter(critter);
                       }
                       break;

                   case "stats":
                	   if (!command.hasNext()) {
                		   throw new Exception();
                	   }
                	   String c = command.next();
                	   if(command.hasNext()) {
                           throw new Exception();
                       }
                	   List<Critter> listOfCritters = Critter.getInstances(c);                	   
                	   Class<?> classType = Class.forName(myPackage + "." + c);
                	   classType.getMethod("runStats", List.class).invoke(null, listOfCritters);
                	   break;

                   default:
                       System.out.println("invalid command: " + cmd);
               }
           }
           catch (Exception | NoClassDefFoundError e) { // catch any exceptions and the error that results from using the lowercase version of a valid Critter name
               System.out.println("error processing: " + cmd);
           }

        }

        System.out.flush();
    }
}
