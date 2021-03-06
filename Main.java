package assignment5;
/* CRITTERS Main.java
 * EE422C Project 5 submission by
 * Varun Prabhu
 * vp6793
 * 15465
 * Kelby Erickson
 * kde528
 * 15495
 * Spring 2018
 */

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.io.*;


/*
 * Usage: java <pkgname>.Main <input file> test
 * input file is optional.  If input file is specified, the word 'test' is optional.
 * May not use 'test' argument without specifying input file.
 */
public class Main extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }
	
    //static Scanner kb;	// scanner connected to keyboard input, or input file
    //private static String inputFile;	// input file, used instead of keyboard input if specified
    //static ByteArrayOutputStream testOutputString;	// if test specified, holds all console output
    private static String myPackage;	// package of Critter file.  Critter cannot be in default pkg.
    //private static boolean DEBUG = false; // Use it or not, as you wish!
    //static PrintStream old = System.out;	// if you want to restore output to console

    // Gets the package name.  The usage assumes that Critter and its subclasses are all in the same package.
    static {
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }
    
    static int numRows = Params.world_height; // number of rows in grid representing world
	static int numColumns = Params.world_width; // number of rows in grid representing world

	static int size = 450/Integer.max(numRows, numColumns); // length of one side of a single cell in the grid
	
	
	
    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("Critters"); // primary stage for GUI
        BorderPane border = new BorderPane(); // BorderPane used so we can have top, center, and bottom sections
        Scene scene = new Scene(border, 450, 450); // Scene containing BorderPane
        primaryStage.setScene(scene);
        
        GridPane grid = new GridPane(); // GridPane used to display world of Critters
        FlowPane control = new FlowPane(); // FlowPane containing buttons at top of BorderPane
        border.setTop(control);
        border.setCenter(grid);

        Label statLabel = new Label("Stats: "); // Label for stats control
        border.setBottom(statLabel);

        Button makeBtn = new Button("Make"); // Button to make Critters
        control.getChildren().add(makeBtn);
        
        // control for making Critters
        MenuButton critterMenu = new MenuButton("Critter to Make"); // MenuButton to select which Critter to make
        control.getChildren().add(critterMenu);
        HBox hb = new HBox();
        Label lb = new Label("Number to Make");
        TextField tf = new TextField(); // where user enters how many Critters to make
        tf.setMaxWidth(60);
        hb.getChildren().addAll(lb, tf);
        hb.setSpacing(5);
        control.getChildren().add(hb);

        Button statsButton = new Button("Stats"); // Button to display stats
        control.getChildren().add(statsButton);

        MenuButton stats = new MenuButton("Critter to Stats"); // MenuButton to select which Critter to display stats for
        control.getChildren().add(stats);


        Button step1 = new Button("Step 1"); // Button to take 1 time step and update display
        control.getChildren().add(step1);

        Button step10 = new Button("Step 10"); // Button to take 10 time steps and update display
        control.getChildren().add(step10);

        Button step100 = new Button("Step 100"); // Button to take 100 time steps and update display
        control.getChildren().add(step100);

        Button step1000 = new Button("Step 1000"); // Button to take 1000 time steps and update display
        control.getChildren().add(step1000);
        
        // animation controls
        HBox animHB = new HBox();
        Label animLB = new Label("Steps per Frame");
        TextField animTF = new TextField(); // where user enters how fast to perform animation
        animTF.setMaxWidth(60);
        animHB.getChildren().addAll(animLB, animTF);
        animHB.setSpacing(5);
        control.getChildren().add(animHB);
        
        // seed controls
        Button seedBtn = new Button("Set Seed"); // Button used to set the seed
        TextField seedTF = new TextField(); // where user enters new seed value
        seedTF.setMaxWidth(60);
        control.getChildren().addAll(seedBtn, seedTF);
        
        Button quit = new Button("quit"); // Button to quit application
        control.getChildren().add(quit);
        
        
        
        // fill MenuButtons with MenuItem for each included Critter
        File file = new File("./bin/" + myPackage); // Varun's path: ".\\out\\production\\assignment5\\"
        String[] critterClasses = file.list();
        for (String s : critterClasses) {
        	try {
				if (Class.forName(myPackage + ".Critter").isAssignableFrom(Class.forName(myPackage + "." + s.substring(0, s.length() - 6)))) {
					if (!s.equals("Critter.class") && !s.equals("Critter$TestCritter.class")) {
                        MenuItem critter = new MenuItem(s.substring(0, s.length() - 6));
                        critter.setOnAction(event -> {
                             critterMenu.setText(s.substring(0, s.length() - 6));
                        });
				        critterMenu.getItems().add(critter);

                        MenuItem statCrit = new MenuItem(s.substring(0, s.length() - 6));
                        statCrit.setOnAction(event -> {
                            stats.setText(s.substring(0, s.length() - 6));
                        });
                        stats.getItems().add(statCrit);
					}
					if (s.equals("Critter.class")) {
                        MenuItem statCrit = new MenuItem(s.substring(0, s.length() - 6));
                        statCrit.setOnAction(event -> {
                            stats.setText(s.substring(0, s.length() - 6));
                        });
                        stats.getItems().add(statCrit);
                    }
					
				}
			} catch (ClassNotFoundException e) {
				
			}
        }
        
        

		final ArrayList<Integer> num = new ArrayList<>(); // memory space used to transfer info between threads
		num.add(0);
        
        updateView(grid);
        primaryStage.show();
        
        
        
        // make Critters
        makeBtn.setOnAction(event -> {
            String critterToMake = critterMenu.getItems().get(0).getText();
            if(!critterMenu.getText().equals("Critter to Make")) {
                critterToMake = critterMenu.getText();
            }
            try{
            	int numToMake = 0;
            	if (tf.getText() != null) {
            		try {
            			numToMake = Integer.parseInt(tf.getText());
            		} catch (Exception e) {
            			
            		}
            	}
            	
                for (int count = 0; count < numToMake; count++) {
                	Critter.makeCritter(critterToMake);
                }
                updateView(grid);
            }
            catch(Exception e){
                System.out.println("Error");
            }

        });
        
        // display stats for a given Critter
        statsButton.setOnAction(event -> {
            String critterToStats = stats.getItems().get(0).getText();
            if(!stats.getText().equals("Critter to Stats")) {
                critterToStats = stats.getText();
            }
            try{
                List<Critter> listOfCritters = Critter.getInstances(critterToStats);
                Class<?> classType = Class.forName(myPackage + "." + critterToStats);
                String result = (String) classType.getMethod("runStats", List.class).invoke(null, listOfCritters);
                statLabel.setText("Stats: \n" + result);
            }
            catch(Exception e) {
                System.out.println("Error in Stats");
            }

        });
        
        // perform 1 step and update display
        step1.setOnAction(event -> {
            Critter.worldTimeStep();
            updateView(grid);
        });
        
        // perform 10 steps and update display; if animation speed greater than zero, perform animation
        step10.setOnAction(event -> {
			int animSpeed = 0;
			if (animTF.getText() != null) {
				try {
					animSpeed = Integer.parseInt(animTF.getText());
				} catch (Exception e) {

				}
			}

			if(animSpeed <= 0) {
				for(int i = 0; i < 10; i++) {
					Critter.worldTimeStep();
				}
				updateView(grid);
			}else {
				makeBtn.setDisable(true);
				critterMenu.setDisable(true);
				stats.setDisable(true);
				statsButton.setDisable(true);
				step1.setDisable(true);
				step10.setDisable(true);
				step100.setDisable(true);
				step1000.setDisable(true);
				seedBtn.setDisable(true);

				Timeline t = new Timeline();
				t.setCycleCount(10/animSpeed);
				num.set(0, animSpeed);
				KeyFrame update = new KeyFrame(Duration.seconds(0.2),
						new EventHandler<ActionEvent>() {

							public void handle(ActionEvent event) {
								for(int idx = 0; idx < num.get(0); idx++) {
									Critter.worldTimeStep();
								}
								updateView(grid);

								String critterToStats = "Critter";
								if(!stats.getText().equals("Critter to Stats")) {
									critterToStats = stats.getText();
								}
								try{
									List<Critter> listOfCritters = Critter.getInstances(critterToStats);
									Class<?> classType = Class.forName(myPackage + "." + critterToStats);
									String result = (String) classType.getMethod("runStats", List.class).invoke(null, listOfCritters);
									statLabel.setText("Stats: \n" + result);
								}
								catch(Exception e) {
									System.out.println("Error in Stats");
								}


							}
						});
				t.getKeyFrames().add(update);
				t.play();

				for(int i = 0; i < 10%animSpeed; i++) {
					Critter.worldTimeStep();
				}
				updateView(grid);

				makeBtn.setDisable(false);
				critterMenu.setDisable(false);
				stats.setDisable(false);
				statsButton.setDisable(false);
				step1.setDisable(false);
				step10.setDisable(false);
				step100.setDisable(false);
				step1000.setDisable(false);
				seedBtn.setDisable(false);

			}
        });
        
        // perform 100 steps and update display; if animation speed greater than zero, perform animation
        step100.setOnAction(event -> {
			int animSpeed = 0;
			if (animTF.getText() != null) {
				try {
					animSpeed = Integer.parseInt(animTF.getText());
				} catch (Exception e) {

				}
			}

			if(animSpeed <= 0) {
				for(int i = 0; i < 100; i++) {
					Critter.worldTimeStep();
				}
				updateView(grid);
			}else {
				makeBtn.setDisable(true);
				critterMenu.setDisable(true);
				stats.setDisable(true);
				statsButton.setDisable(true);
				step1.setDisable(true);
				step10.setDisable(true);
				step100.setDisable(true);
				step1000.setDisable(true);
				seedBtn.setDisable(true);

				Timeline t = new Timeline();
				t.setCycleCount(100/animSpeed);
				num.set(0, animSpeed);
				KeyFrame update = new KeyFrame(Duration.seconds(0.2),
						new EventHandler<ActionEvent>() {

							public void handle(ActionEvent event) {
								for(int idx = 0; idx < num.get(0); idx++) {
									Critter.worldTimeStep();
								}
								updateView(grid);


								String critterToStats = "Critter";
								if(!stats.getText().equals("Critter to Stats")) {
									critterToStats = stats.getText();
								}
								try{
									List<Critter> listOfCritters = Critter.getInstances(critterToStats);
									Class<?> classType = Class.forName(myPackage + "." + critterToStats);
									String result = (String) classType.getMethod("runStats", List.class).invoke(null, listOfCritters);
									statLabel.setText("Stats: \n" + result);
								}
								catch(Exception e) {
									System.out.println("Error in Stats");
								}

							}
						});
				t.getKeyFrames().add(update);
				t.play();

				for(int i = 0; i < 100%animSpeed; i++) {
					Critter.worldTimeStep();
				}
				updateView(grid);

				makeBtn.setDisable(false);
				critterMenu.setDisable(false);
				stats.setDisable(false);
				statsButton.setDisable(false);
				step1.setDisable(false);
				step10.setDisable(false);
				step100.setDisable(false);
				step1000.setDisable(false);
				seedBtn.setDisable(false);

			}
        });


        // perform 1000 steps and update display; if animation speed greater than zero, perform animation
        step1000.setOnAction(event -> {
        	int animSpeed = 0;
        	if (animTF.getText() != null) {
        		try {
        			animSpeed = Integer.parseInt(animTF.getText());
        		} catch (Exception e) {
        			
        		}
        	}

        	if(animSpeed <= 0) {
        		for(int i = 0; i < 1000; i++) {
        			Critter.worldTimeStep();
				}
				updateView(grid);
			}else {
				makeBtn.setDisable(true);
				critterMenu.setDisable(true);
				stats.setDisable(true);
				statsButton.setDisable(true);
				step1.setDisable(true);
				step10.setDisable(true);
				step100.setDisable(true);
				step1000.setDisable(true);
				seedBtn.setDisable(true);

				Timeline t = new Timeline();
				t.setCycleCount(1000/animSpeed);
				num.set(0, animSpeed);
				KeyFrame update = new KeyFrame(Duration.seconds(0.2),
						new EventHandler<ActionEvent>() {

							public void handle(ActionEvent event) {
								for(int idx = 0; idx < num.get(0); idx++) {
									Critter.worldTimeStep();
								}
								updateView(grid);

								String critterToStats = "Critter";
								if(!stats.getText().equals("Critter to Stats")) {
									critterToStats = stats.getText();
								}
								try{
									List<Critter> listOfCritters = Critter.getInstances(critterToStats);
									Class<?> classType = Class.forName(myPackage + "." + critterToStats);
									String result = (String) classType.getMethod("runStats", List.class).invoke(null, listOfCritters);
									statLabel.setText("Stats: \n" + result);
								}
								catch(Exception e) {
									System.out.println("Error in Stats");
								}
							}
						});
				t.getKeyFrames().add(update);
				t.play();

				for(int i = 0; i < 1000%animSpeed; i++) {
					Critter.worldTimeStep();
				}
				updateView(grid);

				makeBtn.setDisable(false);
				critterMenu.setDisable(false);
				stats.setDisable(false);
				statsButton.setDisable(false);
				step1.setDisable(false);
				step10.setDisable(false);
				step100.setDisable(false);
				step1000.setDisable(false);
				seedBtn.setDisable(false);

			}

        });
        
        // set seed
        seedBtn.setOnAction(event -> {
        	if (seedTF.getText() != null) {
        		try {
        			int newSeed = Integer.parseInt(seedTF.getText());
        			Critter.setSeed(newSeed);
        		} catch (Exception e) {
        			
        		}
        	}
        });
        
        // terminate application
        quit.setOnAction(event -> {
            System.exit(0);
        });

    }
    
    
    
    // Private Helper Methods
    
    /**
     * This method updates the display of the world on the given GridPane.
     * @param grid is the GridPane
     */
    private void updateView(GridPane grid) {
    	grid.getChildren().clear();
    	drawGridLines(grid);
    	
		List<Critter> world = Critter.displayWorld();
		for (Critter c : world) {
			Shape s = getShape(c.viewShape());
			s.setFill(c.viewFillColor());
			s.setStroke(c.viewOutlineColor());
			grid.add(s, c.getX(), c.getY());
		}
    }
    
    /**
     * This method draws grid lines on the given GridPane.
     * @param grid is the GridPane
     */
    private void drawGridLines(GridPane grid) {
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numColumns; col++) {
				Shape rect = new Rectangle(size, size);
				rect.setFill(null);
				rect.setStroke(Color.GRAY);
				grid.add(rect, col, row);
			}
		}
	}
    
    /**
     * This method returns a Shape that corresponds to the given CritterShape.
     * @param shape is the CritterShape indicating what Shape to make
     * @return Shape corresponding to given CritterShape
     */
    private Shape getShape(Critter.CritterShape shape) {
    	switch (shape) {
    		case CIRCLE:
    			return new Circle(size/2.0);
    		case SQUARE:
    			return new Rectangle(size, size);
    		case TRIANGLE: 
    			Polygon triangle = new Polygon();
    			triangle.getPoints().addAll(new Double[] {
    					size/2.0, 1.0,
    					1.0, size - 1.0,
    					size - 1.0, size - 1.0,
    			});
    			return triangle;
    		case DIAMOND:
    			Polygon diamond = new Polygon();
    			diamond.getPoints().addAll(new Double[] {
    					size/2.0, 1.0,
    					1.0, size/2.0,
    					size/2.0, size - 1.0,
    					size - 1.0, size/2.0,
    			});
    			return diamond;
    		case STAR:
    			Polygon star = new Polygon();
    			star.getPoints().addAll(new Double[] {
    					size/2.0, 2.0,
    					size/1.7, size/3.0,
    					size/1.2, size/3.0,
    					size/1.5, size*0.67,
    					size/1.3, size/1.2,
    					size/2.0, size*0.75,
    					1.0, size/1.2,
    					size*0.22, size*0.67,
    					1.0, size/3.0,
    					size*0.31, size/3.0,
    			});
    			return star;
    		default:
    			return new Circle(size/2.0);
    	}
    }
}
