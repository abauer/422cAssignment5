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
import java.util.ArrayList;
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
    static ArrayList<StatsWindow> statsWindows;
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

        statsWindows = new ArrayList<>();

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
        stage.setOnCloseRequest(event -> Platform.exit());

        border.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            double hsize = newValue.getHeight()/Params.world_height;
            double wsize = (newValue.getWidth()-324)/Params.world_width;
            BOXSIZE = hsize<wsize ? hsize-2 : wsize-2;
            border.setCenter(createGrid());
            Critter.displayWorld();
        });
	}

    public static void updateRunStats() {
        statsWindows.stream().forEach(StatsWindow::updateStats);
    }

    /*
     * Creates a grid for the center region
     */
    private static GridPane createGrid() {
        GridPane grid = new GridPane();
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
        grid.setAlignment(Pos.CENTER);
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

        Text seedLabel = new Text("Select a Seed:");
        seedLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(seedLabel,0,0);

        TextField seedField = new NumberField(""+Critter.getRandomInt(Integer.MAX_VALUE));
        Critter.setSeed(Integer.valueOf(seedField.getText()));
        seedField.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(seedField,1,0);

        Button setSeed = new Button("Set Seed");
        setSeed.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        setSeed.setOnAction(event -> {
            if (!animating) {
                Critter.setSeed(Integer.valueOf(seedField.getText()));
            }
        });
        grid.add(setSeed,1,1);
        GridPane.setHalignment(setSeed,HPos.RIGHT);

		Text addCritter = new Text("Add Critter of Type:");
        addCritter.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
		grid.add(addCritter,0,4);

        ComboBox<String> critterDropdown = new ComboBox(crits);
        critterDropdown.setValue(crits.get(0));
        grid.add(critterDropdown,1,4);

        Text amtLabel = new Text("Amount:");
        amtLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(amtLabel,0,5);

        TextField addField = new NumberField("1");
        addField.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(addField,1,5);

        Button addButton = new Button("Add Critters");
        addButton.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        addButton.setOnAction(event -> {
            if (!animating) {
                String critterType = critterDropdown.getValue();
                int num = (addField.getText().length() > 0) ? Integer.parseInt(addField.getText()) : 1;
                if (critterType != null)
                    makeCritters(critterType, num);
            }
        });
        grid.add(addButton,1,6);
        GridPane.setHalignment(addButton, HPos.RIGHT);

        Text stepText = new Text("Step World:");
        stepText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(stepText,0,9);

        TextField stepField = new NumberField("1");
        stepField.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(stepField,1,9);

        Button stepButton = new Button("Step");
        stepButton.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        stepButton.setOnAction(event -> {
            if (!animating) {
                int num = (stepField.getText().length() > 0) ? Integer.parseInt(stepField.getText()) : 1;
                runSteps(num);
            }
        });
        grid.add(stepButton,1,10);
        GridPane.setHalignment(stepButton, HPos.RIGHT);


        Text animateLabel = new Text("Animate World:");
        animateLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(animateLabel,0,13);

        Slider animSlider = new Slider(0,100,1);
        Text animSpeedLabel = new Text();
        BorderPane animBorderPane = new BorderPane();
        Button animButton = new Button("Start");

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
        grid.add(animSlider,0,14,2,1);

        animButton.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> runSteps(animationSpeed)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        animButton.setOnAction(event -> {
            if (!animating && animationSpeed > 0) {
                animButton.setText("Stop");
                animating = true;
                addButton.setDisable(true);
                stepButton.setDisable(true);
                setSeed.setDisable(true);
                timeline.play();
            } else {
                animButton.setText("Start");
                animating = false;
                addButton.setDisable(false);
                stepButton.setDisable(false);
                setSeed.setDisable(false);
                timeline.stop();
            }
        });
        animBorderPane.setRight(animButton);
        animSpeedLabel.setText("Speed: "+(int)animSlider.getValue()+" ");
        animBorderPane.setCenter(animSpeedLabel);
        grid.add(animBorderPane,1,13);
        GridPane.setHalignment(animBorderPane,HPos.RIGHT);

        Text runStatsLabel = new Text("Run Stats for Type:");
        runStatsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        grid.add(runStatsLabel,0,16);

        ComboBox<String> runStatsDropdown = new ComboBox(crits);
        runStatsDropdown.setValue(crits.get(0));
        grid.add(runStatsDropdown,1,16);

        Button seeStats = new Button("See Stats");
        seeStats.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        seeStats.setOnAction(event -> statsWindows.add(new StatsWindow(runStatsDropdown.getValue()).updateStats()));
        grid.add(seeStats,1,17);
        GridPane.setHalignment(seeStats, HPos.RIGHT);

        BorderPane quitBorderPane = new BorderPane();
        HBox quitHbox = new HBox();

        Button quitButton = new Button("QUIT");
        quitButton.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        quitButton.setTextFill(Color.FIREBRICK);
        quitButton.setOnAction(event -> Platform.exit());
        quitButton.setAlignment(Pos.CENTER);
        quitButton.setPadding(new Insets(10));

        Button clearWorldButton = new Button("Clear World");
        clearWorldButton.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        clearWorldButton.setOnAction(event -> {Critter.clearWorld(); Critter.displayWorld();});
        clearWorldButton.setAlignment(Pos.CENTER);
        clearWorldButton.setPadding(new Insets(10));

        quitHbox.getChildren().addAll(quitButton,clearWorldButton);
        quitHbox.setSpacing(10);
        quitHbox.setAlignment(Pos.CENTER);
        quitBorderPane.setCenter(quitHbox);
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

class StatsWindow{

    private String crit;
    private ByteArrayOutputStream baos;
    private Text stats;

    public StatsWindow(String critter){
        crit = critter;
        baos = new ByteArrayOutputStream();

        BorderPane bp = new BorderPane();

        Text critterName = new Text("Viewing stats for "+critter);
        critterName.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        bp.setTop(critterName);
        bp.setPadding(new Insets(10));

        stats = new Text("test");
        stats.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        bp.setBottom(stats);

        Stage stage = new Stage();
        stage.setOnCloseRequest(event -> Main.statsWindows.remove(this));
        Scene scene = new Scene(bp,470,80);
        stage.setScene(scene);
        stage.setTitle(critter+" Stats");
        stage.show();
    }

    public StatsWindow updateStats(){
        System.setOut(new PrintStream(baos));
        try {
            String critterPackage = Critter.class.getPackage().toString().split(" ")[1];
            Class.forName(critterPackage + "." + crit)
                    .getMethod("runStats", List.class)
                    .invoke(null, Critter.getInstances(crit));
        } catch (Exception e) {

        }
        String result = baos.toString();
        if(result.length()>73){
            result = result.substring(0,67) + "...";
        }
        stats.setText(result);
        baos.reset();
        return this;
    }
}

class NumberField extends TextField {

    public NumberField(String s){super(s);}

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
