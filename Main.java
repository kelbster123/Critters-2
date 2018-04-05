package assignment5;
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

import javafx.application.Application;
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
    
    static int numRows = Params.world_height;
	static int numColumns = Params.world_width;
	static int size = 30;
    
    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("Critters");
        BorderPane border = new BorderPane();
        Scene scene = new Scene(border, 1000, 1000);
        primaryStage.setScene(scene);
        
        GridPane grid = new GridPane();
        FlowPane control = new FlowPane();
        border.setTop(control);
        border.setCenter(grid);

        Label statLabel = new Label("Stats: ");
        border.setBottom(statLabel);

        Button makeBtn = new Button("Make");
        control.getChildren().add(makeBtn);


        
        MenuButton critterMenu = new MenuButton("Critter to Make");
        control.getChildren().add(critterMenu);
        
        
        HBox hb = new HBox();
        Label lb = new Label("Number to Make");
        TextField tf = new TextField();
        tf.setMaxWidth(60);
        hb.getChildren().addAll(lb, tf);
        hb.setSpacing(5);
        control.getChildren().add(hb);
        

        Button statsButton = new Button("Stats");
        control.getChildren().add(statsButton);

        MenuButton stats = new MenuButton("Critter to Stats");
        control.getChildren().add(stats);


        Button step1 = new Button("Step 1");
        control.getChildren().add(step1);

        Button step10 = new Button("Step 10");
        control.getChildren().add(step10);

        Button step100 = new Button("Step 100");
        control.getChildren().add(step100);

        Button step1000 = new Button("Step 1000");
        control.getChildren().add(step1000);
        
        
        // animation controls
        HBox animHB = new HBox();
        Label animLB = new Label("Steps per Frame");
        TextField animTF = new TextField();
        animTF.setMaxWidth(60);
        animHB.getChildren().addAll(animLB, animTF);
        animHB.setSpacing(5);
        control.getChildren().add(animHB);
        
        // seed controls
        Button seedBtn = new Button("Set Seed");
        TextField seedTF = new TextField();
        seedTF.setMaxWidth(60);
        control.getChildren().addAll(seedBtn, seedTF);
        

        Button quit = new Button("quit");
        control.getChildren().add(quit);



        // .\\out\\production\\assignment5\\

        File file = new File("./bin/" + myPackage);
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

       // critterMenu.setText(critterMenu.getItems().get(0).getText());
        //stats.setText(stats.getItems().get(0).getText());




        
        
        updateView(grid);
        primaryStage.show();


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





        step1.setOnAction(event -> {
            Critter.worldTimeStep();
            updateView(grid);
        });

        step10.setOnAction(event -> {
        	int animSpeed = 0;
        	if (animTF.getText() != null) {
        		try {
        			animSpeed = Integer.parseInt(animTF.getText());
        		} catch (Exception e) {
        			
        		}
        	}
        	
        	makeBtn.setDisable(true);
        	critterMenu.setDisable(true);
        	stats.setDisable(true);
        	statsButton.setDisable(true);
        	step1.setDisable(true);
        	step10.setDisable(true);
        	step100.setDisable(true);
        	step1000.setDisable(true);
        	seedBtn.setDisable(true);
        	
            for (int i = 0; i < 10; i++) {
                Critter.worldTimeStep();
                if((animSpeed != 0) && (i % animSpeed == 0)) {
                	updateView(grid);
                	try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						
					}
                }
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
        });

        step100.setOnAction(event -> {
        	int animSpeed = 0;
        	if (animTF.getText() != null) {
        		try {
        			animSpeed = Integer.parseInt(animTF.getText());
        		} catch (Exception e) {
        			
        		}
        	}
        	
        	makeBtn.setDisable(true);
        	critterMenu.setDisable(true);
        	stats.setDisable(true);
        	statsButton.setDisable(true);
        	step1.setDisable(true);
        	step10.setDisable(true);
        	step100.setDisable(true);
        	step1000.setDisable(true);
        	seedBtn.setDisable(true);
        	
            for (int i = 0; i < 100; i++) {
                Critter.worldTimeStep();
                if((animSpeed != 0) && (i % animSpeed == 0)) {
                	updateView(grid);
                	try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						
					}
                }
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
        });

        step1000.setOnAction(event -> {
        	int animSpeed = 0;
        	if (animTF.getText() != null) {
        		try {
        			animSpeed = Integer.parseInt(animTF.getText());
        		} catch (Exception e) {
        			
        		}
        	}
        	
        	makeBtn.setDisable(true);
        	critterMenu.setDisable(true);
        	stats.setDisable(true);
        	statsButton.setDisable(true);
        	step1.setDisable(true);
        	step10.setDisable(true);
        	step100.setDisable(true);
        	step1000.setDisable(true);
        	seedBtn.setDisable(true);
        	
            for (int i = 0; i < 1000; i++) {
                Critter.worldTimeStep();
                if((animSpeed != 0) && (i % animSpeed == 0)) {
                	updateView(grid);
                	try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						
					}
                }
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
        });
        
        
        seedBtn.setOnAction(event -> {
        	if (seedTF.getText() != null) {
        		try {
        			int newSeed = Integer.parseInt(seedTF.getText());
        			Critter.setSeed(newSeed);
        		} catch (Exception e) {
        			
        		}
        	}
        });
        

        quit.setOnAction(event -> {
            System.exit(0);
        });

    }


    
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
    
    private void drawGridLines(GridPane grid) {
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numColumns; col++) {
				Shape rect = new Rectangle(size, size);
				rect.setFill(null);
				rect.setStroke(Color.GRAY);
				grid.add(rect, row, col);
			}
		}
	}
    
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
    					size - 2.0, size/3.0,
    					size/1.5, size*0.67,
    					size - 3.0, size - 3.0,
    					size/2.0, size*0.75,
    					1.0, size - 1.0,
    					size*0.22, size*0.67,
    					1.0, size/3.0,
    					size*0.31, size/3.0,
    			});
    			return star;
    		default:
    			return new Circle(size/2.0);
    	}
    }


    /**
     * Main method.
     * @param args args can be empty. If not empty, provide two parameters -- the first is a file name, 
     * and the second is test (for test output, where all output to be directed to a String), or nothing.
     */
//    public static void main(String[] args) {
//        if (args.length != 0) {
//            try {
//                inputFile = args[0];
//                kb = new Scanner(new File(inputFile));
//            } catch (FileNotFoundException e) {
//                System.out.println("USAGE: java Main OR java Main <input file> <test output>");
//                e.printStackTrace();
//            } catch (NullPointerException e) {
//                System.out.println("USAGE: java Main OR java Main <input file>  <test output>");
//            }
//            if (args.length >= 2) {
//                if (args[1].equals("test")) { // if the word "test" is the second argument to java
//                    // Create a stream to hold the output
//                    testOutputString = new ByteArrayOutputStream();
//                    PrintStream ps = new PrintStream(testOutputString);
//                    // Save the old System.out.
//                    old = System.out;
//                    // Tell Java to use the special stream; all console output will be redirected here from now
//                    System.setOut(ps);
//                }
//            }
//        } else { // if no arguments to main
//            kb = new Scanner(System.in); // use keyboard and console
//        }
//
//        /* Do not alter the code above for your submission. */
//        boolean running = true;
//
//        while(running) { // while user has not entered "quit"
//           System.out.print("critters>");
//           String cmd = kb.nextLine();
//           Scanner command = new Scanner(cmd);
//           command.useDelimiter("\\s+");
//           String s = command.next();
//           try {
//               switch(s){ // perform operation that corresponds to user input
//                   case "quit":
//                       if (command.hasNext()) {
//                           throw new Exception();
//                       }
//                       Critter.clearWorld();
//                       running = false;
//                       break;
//
//                   case "show":
//                       if (command.hasNext()) {
//                           throw new Exception();
//                       }
//                       Critter.displayWorld();
//                       break;
//
//                   case "step":
//                       int count = 1;
//                       if (command.hasNextInt()) {
//                           count = command.nextInt();
//                       }
//                       if (command.hasNext()) {
//                           throw new Exception();
//                       }
//                       for (int i = 0; i < count; i++) {
//                           Critter.worldTimeStep();
//                       }
//                       break;
//
//                   case "seed":
//                       if (command.hasNextInt()) {
//                           int seed = command.nextInt();
//                           if(command.hasNext()) {
//                               throw new Exception();
//                           }
//                           Critter.setSeed(seed);
//                       }
//                       break;
//
//                   case "make":
//                	   if (!command.hasNext()) {
//                		   throw new Exception();
//                	   }
//                       String critter = command.next();
//
//                       int crittersToMake = 1;
//                       if(command.hasNextInt()) {
//                           crittersToMake = command.nextInt();
//                       }
//                       if(command.hasNext()) {
//                           throw new Exception();
//                       }
//
//                       for(int numCreated = 0; numCreated < crittersToMake; numCreated++) {
//                            Critter.makeCritter(critter);
//                       }
//                       break;
//
//                   case "stats":
//                	   if (!command.hasNext()) {
//                		   throw new Exception();
//                	   }
//                	   String c = command.next();
//                	   if(command.hasNext()) {
//                           throw new Exception();
//                       }
//                	   List<Critter> listOfCritters = Critter.getInstances(c);
//                	   Class<?> classType = Class.forName(myPackage + "." + c);
//                	   classType.getMethod("runStats", List.class).invoke(null, listOfCritters);
//                	   break;
//
//                   default:
//                       System.out.println("invalid command: " + cmd);
//               }
//           }
//           catch (Exception | NoClassDefFoundError e) { // catch any exceptions and the error that results from using the lowercase version of a valid Critter name
//               System.out.println("error processing: " + cmd);
//           }
//
//        }
//
//        System.out.flush();
//    }
}
