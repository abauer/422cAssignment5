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

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/*
 * Usage: java <pkgname>.Main <input file> test
 * input file is optional.  If input file is specified, the word 'test' is optional.
 * May not use 'test' argument without specifying input file.
 */
public class Main extends Application {

    private static Timeline timeline;
    private static int animationSpeed = 1;
    private static boolean animating = false;

    static Scanner kb;	// scanner connected to keyboard input, or input file
    private static String inputFile;	// input file, used instead of keyboard input if specified
    static ByteArrayOutputStream testOutputString;	// if test specified, holds all console output
    private static String myPackage;	// package of Critter file.  Critter cannot be in default pkg.
    private static boolean DEBUG = false; // Use it or not, as you wish!
    static PrintStream old = System.out;	// if you want to restore output to console
    public static double BOXSIZE = 7.5;
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
        grid.setAlignment(Pos.CENTER);
		Scene scene = new Scene(border);
		stage.setScene(scene);
		stage.setTitle("Critter World");
		stage.show();
        stage.setOnCloseRequest(event -> Platform.exit());

        createRunStatsWindow(ol);

        border.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            double hsize = newValue.getHeight()/Params.world_height;
            double wsize = (newValue.getWidth()-324)/Params.world_width;
            BOXSIZE = hsize<wsize ? hsize-2 : wsize-2;
            border.setCenter(createGrid());
            grid.setAlignment(Pos.CENTER);
            Critter.displayWorld();
        });
	}

	public static void updateRunStats(){
        try {
            String critterPackage = Critter.class.getPackage().toString().split(" ")[1];
            Class.forName(critterPackage + "." + rsText)
                    .getMethod("runStats", List.class)
                    .invoke(null, Critter.getInstances(rsText));
        } catch (Exception e) {

        }
        String stats = baos.toString();
        if(stats.length()>73){
            stats = stats.substring(0,67) + "...";
        }
        rs.setText(stats);
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
        rsSelect.setValue(crits.get(0));
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

        ComboBox<String> critterDropdown = new ComboBox(crits);
        critterDropdown.setValue(crits.get(0));
        grid.add(critterDropdown,1,0);

        Text amtLabel = new Text("Amount:");
        amtLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(amtLabel,0,1);

        TextField addField = new NumberField();
        addField.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        addField.setText("1");
        grid.add(addField,1,1);

        Button addButton = new Button();
        addButton.setText("Add Critters");
        addButton.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        addButton.setOnAction(event -> {
            if (!animating) {
                String critterType = critterDropdown.getValue();
                int num = (addField.getText().length() > 0) ? Integer.parseInt(addField.getText()) : 1;
                if (critterType != null)
                    makeCritters(critterType, num);
            }
        });
        grid.add(addButton,1,2);
        grid.setHalignment(addButton, HPos.RIGHT);

        Text stepText = new Text("Step World:");
        stepText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(stepText,0,5);

        TextField stepField = new NumberField();
        stepField.setOnAction(event -> {});   //add action here (update anim max also)
        stepField.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        stepField.setText("1");
        grid.add(stepField,1,5);

        Button stepButton = new Button();
        stepButton.setText("Step");
        stepButton.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        stepButton.setOnAction(event -> {
            if (!animating) {
                int num = (stepField.getText().length() > 0) ? Integer.parseInt(stepField.getText()) : 1;
                runSteps(num);
            }
        });
        grid.add(stepButton,1,6);
        grid.setHalignment(stepButton, HPos.RIGHT);


        Text animateLabel = new Text("Animate World");
        animateLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(animateLabel,0,9);

        Slider animSlider = new Slider(0,100,1);
        Text animSpeedLabel = new Text();
        BorderPane animBorderPane = new BorderPane();
        Button animButton = new Button();

        animSlider.setShowTickLabels(true);
        animSlider.setShowTickMarks(true);
        animSlider.setSnapToTicks(false);
        animSlider.setMajorTickUnit(25);
        animSlider.setMinorTickCount(5);
        animSlider.setOnMouseReleased(event -> {
            animSlider.setValue(Math.round(animSlider.getValue()));
            animButton.setDisable(animSlider.getValue() == 0);
            animationSpeed = (int)Math.round(animSlider.getValue());
            animSpeedLabel.setText("Speed: "+animationSpeed+" ");
        });
        grid.add(animSlider,0,10,2,1);

        animButton.setText("Start");
        animButton.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> runSteps(animationSpeed)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        animButton.setOnAction(event -> {
            if (!animating && animationSpeed > 0) {
                animButton.setText("Stop");
                animating = true;
                addButton.setDisable(true);
                stepButton.setDisable(true);
                timeline.play();
            } else {
                animButton.setText("Start");
                animating = false;
                addButton.setDisable(false);
                stepButton.setDisable(false);
                timeline.stop();
            }
        });   // add action here
        animBorderPane.setRight(animButton);
        animSpeedLabel.setText("Speed: "+(int)animSlider.getValue()+" ");
        animBorderPane.setCenter(animSpeedLabel);
        grid.add(animBorderPane,1,9);
        grid.setHalignment(animBorderPane,HPos.RIGHT);

        BorderPane quitBorderPane = new BorderPane();

        Button quitButton = new Button();
        quitButton.setText("QUIT");
        quitButton.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        quitButton.setTextFill(Color.FIREBRICK);
        quitButton.setOnAction(event -> Platform.exit());   // add action here
        quitButton.setAlignment(Pos.CENTER);
        quitButton.setPadding(new Insets(10));

        quitBorderPane.setCenter(quitButton);
        quitBorderPane.setPadding(new Insets(10));
        border.setBottom(quitBorderPane);

		return border;
	}

	private static void runSteps(int steps) {
        for (int i = 0; i < steps; i++)
            Critter.worldTimeStep();
        Critter.displayWorld();
    }

    private static void makeCritters(String type, int num) {
        try {
            for (int i = 0; i < num; i++)
                Critter.makeCritter(type);
        } catch (Exception e) {
            System.err.println("Invalid critter!");
        }
        Critter.displayWorld();
    }
}

class NumberField extends TextField {

    @Override public void replaceText(int start, int end, String text) {
        if (text.matches("\\d*")) {
            super.replaceText(start, end, text);
        }
    }

    @Override public void replaceSelection(String text) {
        if (text.matches("\\d*")) {
            super.replaceSelection(text);
        }
    }
}
