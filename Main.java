/* CRITTERS Main.java
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
package assignment4; // cannot be in default package
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.io.*;
import java.util.ServiceLoader;


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
     * @param args args can be empty.  If not empty, provide two parameters -- the first is a file name, 
     * and the second is test (for test output, where all output to be directed to a String), or nothing.
     */
    public static void main(String[] args) {
        Iterator it = ServiceLoader.loadInstalled(Critter.class).iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
        System.out.println("Done");
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
        /* Write your code below. */

        while (true) {
            System.out.print("critters>");
            String input = kb.nextLine().trim();
            // special-cased since we can't use System.exit()
            if (input.equals("quit"))
                break;
            if (!runCommand(input))
                System.out.println("invalid command: "+input);
        }
        
        /* Write your code above */
        System.out.flush();

    }

    /**
     * Parses and runs an input command.
     * @param input the trimmed raw input
     * @return true if the command was a valid command (even if its parameters were invalid)
     */
    private static boolean runCommand(String input) {
        String[] tokens = input.split("\\s+");
        try {
            if (tokens[0].equals("quit")){
                throw new IllegalArgumentException();
            } else if (tokens[0].equals("show")) {
                if (tokens.length > 1)
                    throw new IllegalArgumentException();
                Critter.displayWorld();
            } else if (tokens[0].equals("step")) {
                if (tokens.length == 1) {
                    Critter.worldTimeStep();
                } else if (tokens.length == 2) {
                    int steps = Integer.parseInt(tokens[1]);
                    for (int i = 0; i < steps; i++)
                        Critter.worldTimeStep();
                } else {
                    throw new IllegalArgumentException();
                }
            } else if (tokens[0].equals("seed")) {
                if (tokens.length != 2)
                    throw new IllegalArgumentException();
                int seed = Integer.parseInt(tokens[1]);
                Critter.setSeed(seed);
            } else if (tokens[0].equals("make")) {
                if (tokens.length == 2) {
                    Critter.makeCritter(tokens[1]);
                } else if (tokens.length == 3) {
                    int num = Integer.parseInt(tokens[2]);
                    for (int i = 0; i < num; i++)
                        Critter.makeCritter(tokens[1]);
                } else {
                    throw new IllegalArgumentException();
                }
            } else if (tokens[0].equals("stats")) {
                if (tokens.length != 2)
                    throw new IllegalArgumentException();
                String critterPackage = Critter.class.getPackage().toString().split(" ")[1];
                Class.forName(critterPackage + "." + tokens[1])
                        .getMethod("runStats", List.class)
                        .invoke(null, Critter.getInstances(tokens[1]));
            } else {
                return false;   //not a valid command
            }
        } catch (Exception e) {
            System.out.println("error processing: "+input);
        }
        return true;
    }
}
