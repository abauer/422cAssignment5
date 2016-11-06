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
import javafx.geometry.HPos;
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

import java.io.*;
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
    public static final double BOXSIZE = 5;
    private static Text rs;
    private static String rsText;
    private static ByteArrayOutputStream baos;

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
        launch(Main.class, args);
	}

	@Override
	public void start(Stage stage) {
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

        createRunStatsWindow(ol);
	}

	public static void updateRunStats(){
        try {
            String critterPackage = Critter.class.getPackage().toString().split(" ")[1];
            Class.forName(critterPackage + "." + rsText)
                    .getMethod("runStats", List.class)
                    .invoke(null, Critter.getInstances(rsText));
        } catch (Exception e) {

        }
        rs.setText(baos.toString());
        baos.reset();
    }

	private void createRunStatsWindow(ObservableList<String> crits){
        BorderPane bp = new BorderPane();
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        Text selectCritter = new Text("Select Critter to see Stats:");
        selectCritter.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(selectCritter,0,0);

        ComboBox<String> rsSelect = new ComboBox(crits);
        grid.setHalignment(rsSelect, HPos.RIGHT);
        rsSelect.setOnAction(event -> {rsText = rsSelect.getValue(); updateRunStats();});
        grid.add(rsSelect,1,0);

        rs = new Text();
        rs.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(rs,0,1,2,1);

        bp.setCenter(grid);

        Stage runStats = new Stage();
        Scene s = new Scene(bp,470,80);
        runStats.setScene(s);
        runStats.setTitle("Critter Stats");
        runStats.show();
    }

    /*
     * Creates a grid for the center region
     */
    private static GridPane createGrid() {
        grid = new GridPane();
        gridPanes = new HashMap<>();
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setPadding(new Insets(10));

        for (int i = 0; i < Params.world_width; i++) {
            for (int j = 0; j < Params.world_height; j++) {
                StackPane sp = new StackPane();
                Shape s = new Rectangle(BOXSIZE,BOXSIZE); s.setFill(Color.WHITE); s.setStroke(Color.GRAY);
                sp.getChildren().addAll(s);
                gridPanes.put(hashCoords(i,j),sp);
                grid.add(sp, i, j);
            }
        }
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

		Text addCritter = new Text("Add Critter of Type:");
        addCritter.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
		grid.add(addCritter,0,0);

        ComboBox<String> dropdown = new ComboBox(crits);
        grid.add(dropdown,1,0);

        Text amt = new Text("Amount:");
        amt.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(amt,0,1);

        TextField addAmt = new NumberField();
        addAmt.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(addAmt,1,1);

        Button addCrit = new Button();
        addCrit.setText("Add Critters");
        addCrit.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        addCrit.setOnAction(event -> {
            String critterType = dropdown.getValue();
            int num = (addAmt.getText().length() > 0) ? Integer.parseInt(addAmt.getText()) : 1;
            if (critterType != null) {
                runCommand(String.format("make %s %d", dropdown.getValue(), num));
                runCommand("show");
            }
        });
        grid.add(addCrit,1,2);


        Text stepWrld = new Text("Step World:");
        stepWrld.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(stepWrld,0,5);

        TextField stepAmt = new NumberField();
        stepAmt.setOnAction(event -> {});   //add action here (update anim max also)
        stepAmt.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(stepAmt,1,5);

        Button step = new Button();
        step.setText("Step");
        step.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        step.setOnAction(event -> {
            int num = (stepAmt.getText().length() > 0) ? Integer.parseInt(stepAmt.getText()) : 1;
            runCommand(String.format("step %d", num));
            runCommand("show");
        });
        grid.add(step,1,6);


        Text animWrld = new Text("Animate World");
        animWrld.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(animWrld,0,9);

        Button animate = new Button();
        animate.setText("Animate");
        animate.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        animate.setOnAction(event -> {});   // add action here
        grid.add(animate,1,9);

        Slider anim = new Slider(1,10,1);
        anim.setShowTickLabels(true);
        anim.setShowTickMarks(true);
        anim.setSnapToTicks(false);
        anim.setMajorTickUnit((10-1)/4);
        anim.setMinorTickCount(1);
        grid.add(anim,0,10,2,1);

        BorderPane qbp = new BorderPane();

        Button quit = new Button();
        quit.setText("QUIT");
        quit.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        quit.setTextFill(Color.FIREBRICK);
        quit.setOnAction(event -> System.exit(0));   // add action here
        quit.setAlignment(Pos.CENTER);
        quit.setPadding(new Insets(10));

        qbp.setCenter(quit);
        qbp.setPadding(new Insets(10));
        border.setBottom(qbp);

		return border;
	}

    /**
     * Parses and runs an input command.
     * @param input the trimmed raw input
     * @return true if the command was a valid command (even if its parameters were invalid)
     */
    private static boolean runCommand(String input) {
        System.out.println("Running "+input);
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
            e.printStackTrace();
            System.out.println("error processing: "+input);
        }
        return true;
    }
}

class NumberField extends TextField {

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
}
