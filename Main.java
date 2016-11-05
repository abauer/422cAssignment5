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
package assignment5; // cannot be in default package

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/*
 * Usage: java <pkgname>.Main <input file> test
 * input file is optional.  If input file is specified, the word 'test' is optional.
 * May not use 'test' argument without specifying input file.
 */
public class Main extends Application {

    static Scanner kb;	// scanner connected to keyboard input, or input file
    private static String inputFile;	// input file, used instead of keyboard input if specified
    static ByteArrayOutputStream testOutputString;	// if test specified, holds all console output
    private static String myPackage;	// package of Critter file.  Critter cannot be in default pkg.
    private static boolean DEBUG = false; // Use it or not, as you wish!
    static PrintStream old = System.out;	// if you want to restore output to console

    static GridPane grid;
    static HashMap<Integer,StackPane> gridPanes;

    // Gets the package name.  The usage assumes that Critter and its subclasses are all in the same package.
    static {
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }

    /**
     * Main method.
     * @param args args can be empty.  If not empty, provide two parameters -- the first is a file name, 
     * and the second is test (for test output, where all output to be directed to a String), or nothing.
     */
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
        File[] pkgFiles = new File("./src/assignment5").listFiles();
        if (pkgFiles == null) {
            System.err.println("Something's wrong with the package structure...");
            System.exit(1);
        }
        List<String> critClasses = Stream.of(pkgFiles)
            .map(File::getName) // convert to names
            .filter(s -> s.endsWith(".java")) // get class files
            .map(s -> s.substring(0, s.length() - 5)) // strip extension
            .filter(s -> { // save only subclasses of critter
                try {
                    return !s.equals("Critter") && Critter.class.isAssignableFrom(Class.forName(myPackage+"."+s));
                } catch (Exception e) {
                    return false;
                }
            })
            .collect(Collectors.toList());
        System.out.println(critClasses);
        launch(Main.class, args);
	}

	@Override
	public void start(Stage stage) {
// Use a border pane as the root for scene
		BorderPane border = new BorderPane();

        File[] pkgFiles = new File("./src/assignment5").listFiles();
        if (pkgFiles == null) {
            System.err.println("Something's wrong with the package structure...");
            System.exit(1);
        }
        List<String> critClasses = Stream.of(pkgFiles)
                .map(File::getName) // convert to names
                .filter(s -> s.endsWith(".java")) // get class files
                .map(s -> s.substring(0, s.length() - 5)) // strip extension
                .filter(s -> { // save only subclasses of critter
                    try {
                        return !s.equals("Critter") && Critter.class.isAssignableFrom(Class.forName(myPackage+"."+s));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

        ObservableList<String> ol = FXCollections.observableArrayList(critClasses);

		border.setLeft(addVBox(ol));
        border.setCenter(createGrid());
		Scene scene = new Scene(border);
		stage.setScene(scene);
		stage.setTitle("Critter World");
		stage.show();
	}

    /*
     * Creates a grid for the center region
     */
    public static GridPane createGrid() {
        grid = new GridPane();
        gridPanes = new HashMap<>();
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setPadding(new Insets(10, 10, 10, 10));

        for (int i = 0; i < Params.world_width; i++) {
            for (int j = 0; j < Params.world_height; j++) {
                StackPane sp = new StackPane();
                Shape s = new Rectangle(5,5); s.setFill(Color.WHITE); s.setStroke(Color.WHITE);
                sp.getChildren().addAll(s);
                gridPanes.put(hashCoords(i,j),sp);
                grid.add(sp, i, j);
            }
        }
        grid.setGridLinesVisible(true);
        return grid;
    }

    private static int hashCoords(int x, int y) {
        int w = Params.world_width;
        int h = Params.world_height;
        return (w>h) ? x+y*w : y+x*h;
    }

	/*
     * Creates a VBox with a list of links for the left region
     */
	private Node addVBox(ObservableList<String> crits) {
        BorderPane border = new BorderPane();
		GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(5);
        border.setCenter(grid);
		grid.setPadding(new Insets(10)); // Set all sides to 10
		//vbox.setSpacing(8);              // Gap between nodes

		Text addCritter = new Text("Add Critter of Type:");
        addCritter.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
		grid.add(addCritter,0,0);

        grid.add(new ComboBox(crits),1,0);

        Text amt = new Text("Amount:");
        amt.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(amt,2,0);

        TextField addAmt = new TextField() {
            @Override public void replaceText(int start, int end, String text) {
                if (text.matches("[0-9]*")) {
                    super.replaceText(start, end, text);
                }
            }

            @Override public void replaceSelection(String text) {
                if (text.matches("[0-9]*")) {
                    super.replaceSelection(text);
                }
            }
        };
        addAmt.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(addAmt,3,0);

        Button addCrit = new Button();
        addCrit.setText("Add Critters");
        addCrit.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        addCrit.setOnAction(event -> {});   // add action here
        grid.add(addCrit,3,1);


        Text stepWrld = new Text("Step World");
        stepWrld.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(stepWrld,0,3);

        Slider anim; //declared here so stepAmt can modify, used later

        TextField stepAmt = new TextField() {
            @Override public void replaceText(int start, int end, String text) {
                if (text.matches("[0-9]*")) {
                    super.replaceText(start, end, text);
                }
            }

            @Override public void replaceSelection(String text) {
                if (text.matches("[0-9]*")) {
                    super.replaceSelection(text);
                }
            }
        };
        stepAmt.setOnAction(event -> {});   //add action here (update anim max also)
        stepAmt.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(stepAmt,1,3);

        Button step = new Button();
        step.setText("Step");
        step.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        step.setOnAction(event -> {});   // add action here
        grid.add(step,3,4);

        Text animWrld = new Text("Animate World");
        animWrld.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(animWrld,0,6);

        Button animate = new Button();
        animate.setText("Animate");
        animate.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        animate.setOnAction(event -> {});   // add action here
        grid.add(animate,3,7);

        anim = new Slider(1,10,1);
        anim.setShowTickLabels(true);
        anim.setShowTickMarks(true);
        anim.setSnapToTicks(false);
        anim.setMajorTickUnit((10-1)/4);
        anim.setMinorTickCount(1);
        grid.add(anim,1,6,3,1);

        Button quit = new Button();
        quit.setText("QUIT");
        quit.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        quit.setTextFill(Color.FIREBRICK);
        quit.setOnAction(event -> System.exit(0));   // add action here
        border.setBottom(quit);

		return border;
	}

	/*
     * Uses a stack pane to create a help icon and adds it to the right side of an HBox
     *
     * @param hb HBox to add the stack to
     */
	private void addStackPane(HBox hb) {

		StackPane stack = new StackPane();
		Rectangle helpIcon = new Rectangle(30.0, 25.0);
		helpIcon.setFill(new LinearGradient(0,0,0,1, true, CycleMethod.NO_CYCLE,
				new Stop[]{
						new Stop(0, Color.web("#4977A3")),
						new Stop(0.5, Color.web("#B0C6DA")),
						new Stop(1,Color.web("#9CB6CF")),}));
		helpIcon.setStroke(Color.web("#D0E6FA"));
		helpIcon.setArcHeight(3.5);
		helpIcon.setArcWidth(3.5);

		Text helpText = new Text("?");
		helpText.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
		helpText.setFill(Color.WHITE);
		helpText.setStroke(Color.web("#7080A0"));

		stack.getChildren().addAll(helpIcon, helpText);
		stack.setAlignment(Pos.CENTER_RIGHT);
		// Add offset to right for question mark to compensate for RIGHT
		// alignment of all nodes
		StackPane.setMargin(helpText, new Insets(0, 10, 0, 0));

		hb.getChildren().add(stack);
		HBox.setHgrow(stack, Priority.ALWAYS);

	}

	public static void unused(String[] args){
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
