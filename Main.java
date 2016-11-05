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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.ServiceLoader;


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
		launch(Main.class, args);
	}

	@Override
	public void start(Stage stage) {


// Use a border pane as the root for scene
		BorderPane border = new BorderPane();

		HBox hbox = addHBox();
		border.setTop(hbox);
		border.setLeft(addVBox());

// Add a stack to the HBox in the top region
		addStackPane(hbox);

// To see only the grid in the center, uncomment the following statement
// comment out the setCenter() call farther down
        border.setCenter(addGridPane());

// Choose either a TilePane or FlowPane for right region and comment out the
// one you aren't using
//		border.setRight(addFlowPane());
//        border.setRight(addTilePane());

// To see only the grid in the center, comment out the following statement
// If both setCenter() calls are executed, the anchor pane from the second
// call replaces the grid from the first call
//		border.setCenter(addAnchorPane(addGridPane()));

		Scene scene = new Scene(border);
		stage.setScene(scene);
		stage.setTitle("Layout Sample");
		stage.show();
	}

/*
 * Creates an HBox with two buttons for the top region
 */

	private HBox addHBox() {

		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10);   // Gap between nodes
		hbox.setStyle("-fx-background-color: #336699;");

		Button buttonCurrent = new Button("Current");
		buttonCurrent.setPrefSize(100, 20);

		Button buttonProjected = new Button("Projected");
		buttonProjected.setPrefSize(100, 20);

		hbox.getChildren().addAll(buttonCurrent, buttonProjected);

		return hbox;
	}

	/*
     * Creates a VBox with a list of links for the left region
     */
	private VBox addVBox() {

		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10)); // Set all sides to 10
		vbox.setSpacing(8);              // Gap between nodes

		Text title = new Text("Data");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		vbox.getChildren().add(title);

		Hyperlink options[] = new Hyperlink[] {
				new Hyperlink("Sales"),
				new Hyperlink("Marketing"),
				new Hyperlink("Distribution"),
				new Hyperlink("Costs")};

		for (int i=0; i<4; i++) {
			// Add offset to left side to indent from title
			VBox.setMargin(options[i], new Insets(0, 0, 0, 8));
			vbox.getChildren().add(options[i]);
		}

		return vbox;
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

	/*
     * Creates a grid for the center region with four columns and three rows
     */
	private GridPane addGridPane() {

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));

        // Category in column 2, row 1
        Text category = new Text("Sales:");
        category.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        grid.add(category, 1, 0);

        // Title in column 3, row 1
        Text chartTitle = new Text("Current Year");
        chartTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        grid.add(chartTitle, 2, 0);

        // Subtitle in columns 2-3, row 2
        Text chartSubtitle = new Text("Goods and Services");
        grid.add(chartSubtitle, 1, 1, 2, 1);

        // Left label in column 1 (bottom), row 3
        Text goodsPercent = new Text("Goods\n80%");
        GridPane.setValignment(goodsPercent, VPos.BOTTOM);
        grid.add(goodsPercent, 0, 2);

        // Right label in column 4 (top), row 3
        Text servicesPercent = new Text("Services\n20%");
        GridPane.setValignment(servicesPercent, VPos.TOP);
        grid.add(servicesPercent, 3, 2);

        grid.setGridLinesVisible(true);
        return grid;
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
