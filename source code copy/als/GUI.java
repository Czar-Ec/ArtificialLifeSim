package als;

import java.awt.Desktop;
//import java.io.BufferedReader;
import java.io.File;
//import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

//import javax.swing.JFrame;
//import javax.swing.JOptionPane;

import als.AnEntity.traits;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
//import javafx.scene.control.Dialog;
//import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;

/*
 * source on javafx Alert
 * http://code.makery.ch/blog/javafx-dialogs-official/
 */

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * <h1>GUI</h1>
 * GUI handles the display of the entities to the screen as well as the windows<br>
 * where users can input data, see outputs and possible errors that may break the program
 * @author Czar Ian Echavez
 * @see AnEntity
 * @see AWorld
 *
 */
public class GUI extends Application{

	//window sizes
	int windowX = 1366;
	int windowY = 768;
	
	//simulation variables
	boolean toggle = true; //toggle for whether the simulation shows the image or not
	boolean runSim = false; //var for whether or not to run the simulation
	
	//setting up the world
	int worldNum = 0; //increments so multiple world are handled
	ArrayList<AWorld> worldList = new ArrayList<AWorld>();
	
	//variable that stores the current file loaded
	//preset to "autosave" on load so it loads the last sim
	String currentFile = "autosave";
	
	//setting up the window
	Group rt = new Group();
	Canvas canvas = new Canvas(windowX, windowY);
	GraphicsContext gc;
	
	//border pane and panes
	BorderPane bp = new BorderPane();
	Pane center = new Pane();
	
	Scene scene = new Scene(bp);
	
	/////////////////////////////////////////////////////////////////////////////
	//Functions
	/////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Function to create a new world from what the user inputs via the new configuration<br>
	 * @param _input the string input the user provides
	 * @see addEntity
	 */
	public void fromText(String _input)
	{
		//split the input with commas
		String splitString[] = _input.split(",");
		
		//get the world x and y size and parse to ints
		int worldX = Integer.parseInt(splitString[0]);
		int worldY = Integer.parseInt(splitString[1]);
		
		//create a new world
		AWorld world = new AWorld(worldX, worldY);
		
		//add world to worldList
		worldList.add(world);
		
		//get the max ent
		int maxEnt = Integer.parseInt(splitString[2]);
		
		//System.out.println(maxEnt);
		
		//calculate the percentage of food
		double food_percent = Double.parseDouble(splitString[3]);
		double calcF = (food_percent / 100) * maxEnt;
		int calcFood = (int) calcF;
		
		//System.out.println(calcFood);
		
		//create the food
		String input_species;
		for (int f = 0; f < calcFood; f++)
		{	
			input_species = "food";
			worldList.get(worldNum).addEntity(input_species);
		}
		
		//calculate percentage of obstacles
		double ob_percent = Double.parseDouble(splitString[4]);
		double calcO = (ob_percent / 100) * maxEnt;
		int calcOb = (int) calcO;
		
		//System.out.println(calcOb);
		
		//create the obstacles
		for(int o = 0; o < calcOb; o++)
		{
			input_species = "obstacle";
			worldList.get(worldNum).addEntity(input_species);
		}
		
		//start adding the entities to the world
		//starting from 4, since the previous were used for creating the world
		for (int i = 5; i < splitString.length; i++)
		{
			//string to be passed as the species name
			input_species = splitString[i];
			//increment i so the next value being looked at is amount of species to make
			i++;
			
			//find out how many of species to make
			int entNum = Integer.parseInt(splitString[i]);
			//loop to make the species an entNum number of times
			for (int j = 0; j < entNum; j++)
			{
				worldList.get(worldNum).addEntity(input_species);
			}
			
		}
	}
	
	/**
	 * Function to load files from the file name passed to the function
	 * @param loadFile
	 * @see fromText
	 * @see toLoadEnt
	 */
	public void loadFile(String loadFile)
	{
		try
		{
			//new world
			worldNum++;
			
			//store the strings
			ArrayList<String> loadedData = new ArrayList<String>();
			
			//get filepath
			File file = new File("sav/"+loadFile+".sim");
			
			//open a scanner
			Scanner scan = new Scanner(file);
			
			//while there is a next line
			while(scan.hasNextLine())
			{
				//add to the arraylist
				loadedData.add(scan.nextLine());
			}
			
			//close the scanner
			scan.close();
			
			//System.out.println(loadedData.get(0));
			
			//set up the world
			fromText(loadedData.get(0));
			
			for (int i = 1; i < loadedData.size(); i++)
			{				
				if(!loadedData.get(i).isEmpty())
				{	
					//System.out.println("LOADED: " + loadedData.get(i));
					//System.out.println("debug loadedData");
					toLoadEnt(loadedData.get(i));
				}
			}
			
		}
		catch(IOException e)
		{
			//notify user that the file could not be loaded
			Alert error = new Alert(AlertType.ERROR);
			error.setContentText("File " + loadFile + " could not be loaded");
			error.showAndWait();
		}
	}
	
	/**
	 * Function to load the entities individually
	 * @param loaded
	 */
	public void toLoadEnt(String loaded)
	{
		//System.out.println(loaded);
		
		//split the input values
		String loadEnt[] = loaded.split(",");		
		
		worldList.get(worldNum).loadEntity(
				loadEnt[0], //species
				Integer.parseInt(loadEnt[1]), //hPos
				Integer.parseInt(loadEnt[2]), //vPos
				Double.parseDouble(loadEnt[3]), //energy
				Integer.parseInt(loadEnt[4]), //uniqueID
				Boolean.parseBoolean(loadEnt[5]), //dead
				Boolean.parseBoolean(loadEnt[6]), //poisonous
				Boolean.parseBoolean(loadEnt[7]), //albino
				traits.valueOf(loadEnt[8]) //convert the string to enum type
				);
	}
	
	/**
	 * Function to save the current simulation as a user-named file
	 * @throws Exception
	 * @see save
	 */
	public void saveAs() throws Exception
	{
		//variable to hold whether or not there is a file with the name already
		boolean existing = false;
		
		while(!existing)
		{
			//prompt user for save file name
			TextInputDialog in = new TextInputDialog();
			in.setTitle("Save As");
			in.setHeaderText("Save simulation");
			in.setContentText("Save simulation as: ");
			
			//listener
			Optional<String> result = in.showAndWait();
			
			//get file name from user
			String fileName = null;
			
			//check if empty
			if(result.isPresent())
			{
				fileName = result.get();
				fileName = "sav/" + fileName + ".sim"; //change format to be a valid file path
			}
			else
			{
				//exit the loop
				existing = true;
				continue;
			}
			
			//get file path
			File exists = new File(fileName);
			
			//check if file exists
			//if exists, save the file
			if(!exists.isFile())
			{
				//call saveFile via the world class
				worldList.get(worldNum).saveFile(worldList, worldNum, true, fileName);
				
				//exit the loop
				existing = true;
			}
			else
			{
				//pop up to notify user file already exists
				Alert overwrite = new Alert(AlertType.CONFIRMATION);
				overwrite.setTitle("File already exists");
				overwrite.setHeaderText(fileName + " already exists");
				overwrite.setContentText("Overwrite " + fileName + "?");
				
				//buttons
				ButtonType overwriteFile = new ButtonType("Overwrite");
				ButtonType no = new ButtonType("No");
				
				//add buttons to gui
				overwrite.getButtonTypes().setAll(overwriteFile, no);
				
				//event listener
				Optional<ButtonType> result1 = overwrite.showAndWait();
				
				//if user chose yes
				if(result1.get() == overwriteFile)
				{
					//delete the file
					exists.delete();
					
					//recreate the new file
					worldList.get(worldNum).saveFile(worldList, worldNum, true, fileName);
					
					//exit the loop
					existing = true;
				}
				
				//if user chose no
				else if (result1.get() == no)
				{
					//exit the loop
					existing = true;
				}
			}
		}
	}
	
	/**
	 * Method to setup the simulations on the first load.<br>
	 * This is needed since it creates the folder the saved files<br>
	 * load from.
	 */
	public void firstSetup()
	{
		String savFolder = new File("sav").getAbsolutePath();
		File sav = new File(savFolder);
		try
		{
			if(!sav.exists())
			{
				boolean create = sav.mkdir();
				if(create)
				{
					Alert msg = new Alert(AlertType.INFORMATION);
					msg.setHeaderText("New save folder created");
					msg.setContentText("Save folder created at: " + sav);
					msg.showAndWait();
					
					//make a new autosave file
					worldList.get(worldNum).saveFile(worldList, worldNum, true, "sav/autosave.sim");
				}
			}
		} catch(Exception e)
		{
			Alert error = new Alert(AlertType.ERROR);
			error.setContentText("Could not create save folder");
			error.showAndWait();
		}
	}
	
	/**
	 * Function to change the world and display size
	 */
	public void configEdit()
	{
		Stage editC = new Stage();
		editC.setTitle("Edit Configuration");
		
		//grid for the form layout
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(20);
		grid.setVgap(20);
		
		//text field and labels for the fields
		Label X = new Label("World X Size [Integer]: ");
		grid.add(X, 0, 1);
		TextField _X = new TextField("" + windowX);
		grid.add(_X, 1, 1);
		
		Label Y = new Label("World Y Size [Integer]: ");
		grid.add(Y, 0, 2);
		TextField _Y = new TextField("" + windowY);
		grid.add(_Y, 1, 2);
		
		//button inputs
		Button edit = new Button("Edit");
		HBox box = new HBox(10);
		box.setAlignment(Pos.BOTTOM_RIGHT);
		box.getChildren().add(edit);
		grid.add(box, 1, 3);
		
		
		edit.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent t)
			{
				if(!(
					_X.getText().trim().isEmpty() ||
					_Y.getText().trim().isEmpty() ))
				{
					if(numCheck(_X.getText().trim()) &&
						numCheck(_Y.getText().trim()))
					{
						//set new world sizes
						worldList.get(worldNum).setX(Integer.parseInt(_X.getText()));
						worldList.get(worldNum).setY(Integer.parseInt(_Y.getText()));
						
						//change window size
						windowX = worldList.get(worldNum).getX();
						windowY = worldList.get(worldNum).getY();
						
						bp.setPrefSize(windowX, windowY);
						canvas.setHeight(windowY);
						canvas.setWidth(windowX);
						
						worldList.get(worldNum).clearEnt();
						
						editC.close();
					}
				}
				else
				{
					Alert error = new Alert(AlertType.ERROR);
					error.setHeaderText("Input error");
					error.setContentText("Please fill all fields properly");
					error.showAndWait();
				}
			}
		});
		
		//scene
		Scene form_scene = new Scene(grid, 350, 150);
						
		//show the window
		editC.getIcons().add(new Image("/img/czarec.png"));
		editC.setScene(form_scene);
		editC.show();
		
	}
	
	/**
	 * Function used to check if the string value to be checked can be parsed<br>
	 * into an integer
	 * @param num (String)
	 * @return true or false
	 */
	public boolean numCheck(String num)
	{
		try
		{
			Integer.parseInt(num);
		}
		catch(NumberFormatException nfe)
		{
			Alert error = new Alert(AlertType.ERROR);
			error.setHeaderText("Input cannot be parsed");
			error.setContentText("Input cannot be parsed into an integer");
			error.showAndWait();
			return false;
		}
		return true;
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//FUNCTION TO START THE PROGRAM
	/////////////////////////////////////////////////////////////////////////////
	/**
	 * Method that begins the application
	 */
	@Override
	public void start(Stage stage) throws IOException {
		// TODO Auto-generated method stub
		
		//the 2 zeroes are placeholders for the percentages of food and obstacles
		String worldSetup = windowX + "," + windowY + "," + 0 + "," + 0 + "," + 0;
		fromText(worldSetup);
		
		firstSetup();
		
		//load last save [autosave]
		File auto = new File("sav/autosave.sim");
		
		//check if there is an autosave file
		if(auto.isFile())
		{
			//load the autosave file
			loadFile("autosave");
			
			windowX = worldList.get(worldNum).getX();
			windowY = worldList.get(worldNum).getY();
		}
		else
		{
			//create a new world that is the size of the window
			AWorld world = new AWorld(windowX, windowY);
			//add the world tot the worldList
			worldList.add(world);
		}
		
		/***************************************************
		 * SETUP OF MENU
		 **************************************************/
		
		//menubar
		MenuBar menuBar = new MenuBar();
		
		//file menu
		Menu file = new Menu("File");
			//file sub menu
			MenuItem newConfig = new MenuItem("New configuation");
			MenuItem openConfig = new MenuItem("Open configuration");
			MenuItem save = new MenuItem("Save");
			MenuItem saveAs = new MenuItem("Save As");
			MenuItem exitProgram = new MenuItem("Exit");
			
			//add menu items to the file menu
			file.getItems().addAll(newConfig, openConfig, save, saveAs, exitProgram);
		
		//view menu
		Menu view = new Menu("View");
			//view sub menu
			MenuItem dispC = new MenuItem("Display configuration");
			MenuItem editC = new MenuItem("Edit configuration");
			MenuItem lifeformInfo = new MenuItem("Display lifeform info");
			MenuItem mapInfo = new MenuItem("Display map information");
			
			//add menu items to the view menu
			view.getItems().addAll(dispC, editC, lifeformInfo, mapInfo);
		
		//edit menu
		Menu edit = new Menu("Edit");
			//edit sub menu
			MenuItem mod = new MenuItem("Modify current lifeform");
			MenuItem remove = new MenuItem("Remove current lifeform");
			MenuItem add = new MenuItem("Add new lifeform");
			
			//add menu items to the edit menu
			edit.getItems().addAll(mod, remove, add);
			
		//sim sub menu
		Menu sim  = new Menu("Simulation");
			//sim sub menu
			MenuItem run = new MenuItem("Run");
			MenuItem pause = new MenuItem("Pause");
			MenuItem reset = new MenuItem("Reset");
			MenuItem toggleDisp = new MenuItem("Toggle Display");
			
			//add menu items to sim menu
			sim.getItems().addAll(run, pause, reset, toggleDisp);
			
		//help sub menu
		Menu help = new Menu("Help");
			//help sub menu
			MenuItem appInfo = new MenuItem("Display application information");
			MenuItem authInfo = new MenuItem("Display author information");
			
			//add menu items to help menu
			help.getItems().addAll(appInfo, authInfo);
			
		/************************************************************
		 * EVENT HANDLERS
		 ************************************************************/
		
		//new config event
		newConfig.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{	
				/////////////////////////////////////////////////////////////////////////////////////
				
				//create a new window for the form
				Stage worldCreation = new Stage();
				worldCreation.setTitle("Create a new world");
				
				//grid for the form layout
				GridPane grid = new GridPane();
				grid.setAlignment(Pos.CENTER);
				grid.setHgap(20);
				grid.setVgap(20);
				
				//text field and labels for the fields
				Label X = new Label("World X Size [Integer]: ");
				grid.add(X, 0, 1);
				TextField _X = new TextField("1000");
				grid.add(_X, 1, 1);
				
				Label Y = new Label("World Y Size [Integer]: ");
				grid.add(Y, 0, 2);
				TextField _Y = new TextField("500");
				grid.add(_Y, 1, 2);
				
				Label food = new Label("Percentage of Food [Integer]: ");
				grid.add(food, 0, 3);
				TextField _food = new TextField();
				grid.add(_food, 1, 3);
				
				Label obstacle = new Label("Percentage of Obstacles [Integer]: ");
				grid.add(obstacle, 0, 4);
				TextField _obstacle = new TextField();
				grid.add(_obstacle, 1, 4);
				
				Label ent = new Label("Entities [separated by commas ONLY]: \n" + 
				"Entered in format: ent1,ent1Amount, ... ent(String),ent(Integer)Amount");
				grid.add(ent, 0, 6);
				TextField entIn = new TextField();
				grid.add(entIn, 0, 7);
				
				//button inputs
				Button create = new Button("Create");
				HBox box = new HBox(10);
				box.setAlignment(Pos.BOTTOM_RIGHT);
				box.getChildren().add(create);
				grid.add(box, 1, 8);
				
				create.setOnAction(new EventHandler<ActionEvent>()
						{
							@Override
							public void handle(ActionEvent t)
							{
								//check if any fields are empty
								if(!(
										_X.getText().trim().isEmpty() || 
										_Y.getText().trim().isEmpty() ||
										_food.getText().trim().isEmpty() ||
										_obstacle.getText().trim().isEmpty()
										))
								{
									//get all values from the text fields
									String worldX = _X.getText().replace(",", "").trim();
									String worldY = _Y.getText().replace(",", "").trim();
									
									int temp1 = Integer.parseInt(worldX);
									int temp2 = Integer.parseInt(worldY);
									
									//setting min and max sizes
									if(!(temp1 > 1367 || temp1 <= 249 ||
										 temp2 > 769 || temp2 <= 249))
									{									
										//calculating maxEnt
										//prevents 0 errors
										
										temp1/=100;
										temp2/=100;
										
										if(temp1 <= 0)
										{
											temp1 = 1;
										}
										
										if(temp2 <= 0)
										{
											temp2 = 1;
										}
										
										
										
										int maxEnt = (temp1 * temp2) * 10;
										
										//convert maxEnt to String
										String strMax = Integer.toString(maxEnt);
										
										String food_percent = _food.getText().replace(",", "").trim();
										String obstacle_percent = _obstacle.getText().replace(",", "").trim();
										String ents = entIn.getText();
										
										String in = worldX + "," + worldY + "," + strMax + "," + food_percent + "," + obstacle_percent 
												+ "," + ents;
										
										//check the nums if they are parse-able
										if(numCheck(food_percent) && numCheck(obstacle_percent))
										{
											//A WHOLE NEW WORLLDDDDD
											worldNum++;
											//pass input to fromText
											fromText(in);
										}
										else
										{
											Alert error = new Alert(AlertType.ERROR);
											error.setTitle("Input invalid");
											error.setContentText("Food percent or Obstacle percent is invalid");
											error.showAndWait();
										}
									}
									else
									{
										Alert error = new Alert(AlertType.ERROR);
										error.setTitle("World size invalid");
										error.setHeaderText("World size not valid");
										error.setContentText("Maximum X Size:\t1366\n" + 
															"Maximum Y Size:\t768\n" + 
															"Minimum X and Y size:\t250\n");
										error.showAndWait();
									}
								}
								else
								{
									Alert error = new Alert(AlertType.ERROR);
									error.setTitle("Empty field detected");
									error.setHeaderText("Please fill all fields properly");
									//show error message
									error.showAndWait();
								}
							}
						});
				
				//scene
				Scene form_scene = new Scene(grid, 600, 400);
								
				//show the window
				worldCreation.getIcons().add(new Image("/img/czarec.png"));
				worldCreation.setScene(form_scene);
				worldCreation.show();
				
			}
		});
		
		//open configuration
		openConfig.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				/*
				 * Show the file in the sav folder
				 * basically:
				 * >cd sav
				 * >ls
				 */
				
				//get filepath
				File saves = new File("sav");
				
				//array to store filenames
				File[] fileList = saves.listFiles();
				
				//arraylist to store objects
				ArrayList<String> options = new ArrayList<String>();
				
				int i = 0;
				
				for (File list : fileList)
				{
					if(list.isFile())
					{
						/*
						 * format to make sure that when the value is passed to loadFile
						 * it wont crash.
						*/
						String strFile = fileList[i].toString();
						
						//removes "sav/"
						String format = strFile.replaceAll("sav\\\\", "");
						//removes ".sim"
						String format2 = format.replaceAll(".sim", "");
						
						//turn file into object
						options.add(format2);
						i++;
					}
				}
				
				//drop down box menu
				//option.get(0) is default value
				ChoiceDialog<String> load = new ChoiceDialog<>(options.get(0), options);
				load.setTitle("Load File");
				load.setHeaderText("Load saved simulations");
				load.setContentText("Choose file: ");
				
				//listener
				Optional<String> result = load.showAndWait();
				
				//if the user did not press cancel
				if(result.isPresent())
				{
					//close window
					stage.close();
					
					//get result
					loadFile(result.get());
					
					Alert a = new Alert(AlertType.INFORMATION);
					a.setContentText("File [" + result.get() + "] loaded");
					a.showAndWait();
					
					//current file is now the opened config
					currentFile = result.get();
				}
				
				windowX = worldList.get(worldNum).getX();
				windowY = worldList.get(worldNum).getY();
				
				bp.setPrefSize(windowX, windowY);
				canvas.setHeight(windowY);
				canvas.setWidth(windowX);
				
				stage.setScene(scene);
				stage.setResizable(true);
				stage.show();
			}
		});
		
		//save
		save.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				try {
					save();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		//save as
		saveAs.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event) 
			{
				try {
					saveAs();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		});
		
		//display current entity
		dispC.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent event)
				{
					//redraw the world
					//mostly going to be used when toggle is off
					drawWorld();

					Alert msg = new Alert(AlertType.INFORMATION);
					msg.setHeaderText("World updated");
					msg.setContentText("Display has been updated");
					msg.showAndWait();
				}
			}
		);
		
		//display lifeform info
		lifeformInfo.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				worldList.get(worldNum).listAllEntities();
			}
		}
		);
		
		//edit current world
		editC.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent event)
				{
					configEdit();
				}
			}
		);
		
		//display map info
		mapInfo.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent event)
				{
					//System.out.println("fml");
					
					//get world values
					int worldX = worldList.get(worldNum).getX();
					int worldY = worldList.get(worldNum).getY();
					int worldEntities = worldList.get(worldNum).getEntityCount();
					int maxEntities = worldList.get(worldNum).getMaxEnt();
					
					//output as message box
					Alert map = new Alert(AlertType.INFORMATION);
					map.setTitle(currentFile);
					map.setHeaderText(currentFile + " statistics");
					map.setContentText(
							"World X Size: " + worldX + "\n" +
							"World Y Size: " + worldY + "\n" + 
							"Number of entities: " + worldEntities + "\n" + 
							"Maximum entities: " + maxEntities + "\n");
					map.showAndWait();
				}
			}
		);
		
		//modify current lifeform
		mod.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				int checkEntSize = (worldList.get(worldNum).getEntityCount()-1);
				
				if(checkEntSize >= 0)
				{
					AnEntity entity = worldList.get(worldNum).getEntity(checkEntSize);
					
					//get all relevant data
					String species = entity.getSpecies();
					String vPos = Integer.toString(entity.getVPos());
					String hPos = Integer.toString(entity.getHPos());
					String energy = Double.toString(entity.getEnergy());
					boolean dead = entity.isKill();
					boolean poisonous = entity.poisonCheck();
					boolean albino = entity.albinoCheck();
					traits t = entity.seeTrait();
					
					//create a new window for the form
					Stage entityMod = new Stage();
					entityMod.setTitle("Edit entity");
					
					//grid for the form layout
					GridPane grid = new GridPane();
					grid.setAlignment(Pos.CENTER);
					grid.setHgap(20);
					grid.setVgap(5);
					
					//text field and labels for the fields
					Label spec = new Label("Species: ");
					grid.add(spec, 0, 1);
					TextField _spec = new TextField(species);
					grid.add(_spec, 1, 1);
					
					Label x = new Label("H Pos: ");
					grid.add(x, 0, 2);
					TextField _x = new TextField(hPos);
					grid.add(_x, 1, 2);
					
					Label y = new Label("V Pos: ");
					grid.add(y, 0, 3);
					TextField _y = new TextField(vPos);
					grid.add(_y, 1, 3);
					
					Label Energy = new Label("Energy: ");
					grid.add(Energy, 0, 4);
					TextField _energy = new TextField(energy);
					grid.add(_energy, 1, 4);
					
					ObservableList<Boolean> bool = FXCollections.observableArrayList(
							true, false);
					
					Label death = new Label("Dead: ");
					grid.add(death, 0, 5);
					final ComboBox<Boolean> _death = new ComboBox<Boolean>(bool);
					//set default value, which is what the entity is currently
					_death.setValue(dead);
					grid.add(_death, 1, 5);
					
					Label poison = new Label("Poisonous: ");
					grid.add(poison, 0, 6);
					final ComboBox<Boolean> _poison = new ComboBox<Boolean>(bool);
					_poison.setValue(poisonous);
					grid.add(_poison, 1, 6);
					
					Label isAlbino = new Label("Albino: ");
					grid.add(isAlbino, 0, 7);
					ComboBox<Boolean> _isAlbino = new ComboBox<Boolean>(bool);
					_isAlbino.setValue(albino);
					grid.add(_isAlbino, 1, 7);
					
					Label editTrait = new Label("Trait: ");
					grid.add(editTrait, 0, 8);
					
					//Options for the combo box
					ObservableList<traits> options = FXCollections.observableArrayList(
							traits.fast_walker, //+1 move
							traits.injured, //-1 move
							traits.energy_efficient, //half energy use
							traits.fatigued, //2x energy use
							traits.hunter, //2x smell range
							traits.blocked_nose, //half smell range
							traits.student, //literally has no idea what it's doing, will move randomly
							traits.agoraphobic, //will not move from where it spawned
							traits.food, //entity is food
							traits.obstacle, //entity is obstacle
							traits.food_source,
							traits.den);
					
					final ComboBox<traits> traitBox = new ComboBox<traits>(options);
					traitBox.setValue(t);
					grid.add(traitBox, 1, 8);
					
					//button inputs
					Button edit = new Button("Edit");
					HBox box = new HBox(10);
					box.setAlignment(Pos.BOTTOM_RIGHT);
					box.getChildren().add(edit);
					grid.add(box, 1, 9);
					
					edit.setOnAction(new EventHandler<ActionEvent>()
						{
							@Override
							public void handle(ActionEvent event)
							{
								if(!(
									_spec.getText().trim().isEmpty() ||
									_x.getText().trim().isEmpty() ||
									_y.getText().trim().isEmpty() ||
									_energy.getText().trim().isEmpty() ||
									!(numCheck(_x.getText().trim()) &&
									numCheck(_y.getText().trim()))
									))
								{
									String species = _spec.getText();
									int hPos = Integer.parseInt(_x.getText().trim());
									int vPos = Integer.parseInt(_y.getText().trim());
									double energy = Double.parseDouble(_energy.getText());
									boolean dead = _death.getValue();
									boolean poisonous = _poison.getValue();
									boolean albino = _isAlbino.getValue();
									traits traits = traitBox.getValue();
									
									//send data to edit the entity
									worldList.get(worldNum).editEntity(species, hPos, vPos, energy, dead, poisonous, albino, traits, checkEntSize);
									//close the window
									entityMod.close();
								}
								else
								{
									Alert error = new Alert(AlertType.ERROR);
									error.setTitle("Empty field / Incorrect format detected");
									error.setHeaderText("Please fill all fields properly");
									//show error message
									error.showAndWait();
								}
							}
						});
					
					//scene
					Scene scene = new Scene(grid, 400, 300);
					//show window
					entityMod.getIcons().add(new Image("/img/czarec.png"));
					entityMod.setScene(scene);
					entityMod.show();
				}
				else
				{
					Alert error = new Alert(AlertType.ERROR);
					error.setHeaderText("No entities in the world");
					error.showAndWait();
				}
			}
		}
		);
		
		//remove current lifeform
		remove.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				worldList.get(worldNum).removeEntity();
			}
		}
		);
		
		//add new lifeform
		add.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				TextInputDialog in = new TextInputDialog();
				in.setTitle("Add new entity");
				in.setHeaderText("Enter entity name");
				in.setContentText("Entity Name: ");
				
				//listener
				Optional<String> result = in.showAndWait();
				
				if(result.isPresent())	
				{
					String input = result.get().replace(",", "").trim();
					worldList.get(worldNum).addEntity(input);
				}
			}
		}
		);
		
		//run simulation
		run.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				runSim = true;
			}
		}
		);
		
		//pause sim
		pause.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				runSim = false;
			}
		}
		);
		
		//reset sim
		reset.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				//reset works by reloading the current file
				loadFile(currentFile);
			}
		}
		);
		
		//toggle display
		toggleDisp.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				if(toggle == true)
				{
					toggle = false;
				}
				
				else if(toggle == false)
				{
					toggle = true;
				}
			}
		}
		);
		
		//open app info
		appInfo.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				try
				{
//					//get file path
//					File appInfo = new File("/manuals/appInfo.pdf");
					//open the document
					Desktop.getDesktop().browse(new URI("http://czarec.weebly.com/uploads/6/3/6/9/6369114/appinfo.pdf"));
				}
				catch (URISyntaxException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//error message
					Alert fourOfour = new Alert(AlertType.ERROR);
					fourOfour.setHeaderText("404: File not found");
					fourOfour.setContentText("Application's manual is missing");
					fourOfour.showAndWait();
				}
			}
		});
		
		//open author info
		authInfo.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				try
				{
//					//get file path
//					File authInfo = new File("/manuals/authInfo.pdf");
					//open document
					Desktop.getDesktop().browse(new URI("http://czarec.weebly.com/contact.html"));
				}
				catch (IOException | URISyntaxException ex)
				{
					//error message
					Alert fourOfour = new Alert(AlertType.ERROR);
					fourOfour.setHeaderText("404: File not found");
					fourOfour.setContentText("My info is gone D:");
					fourOfour.showAndWait();
				}
			}
		});
		
		//exit events
		/******************************************************
		 * THIS ONE IS FOR WHEN THE USER EXITS VIA THE MENU BAR
		 ******************************************************/
		exitProgram.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				//check if an autosave already exists
				File closing = new File("sav/autosave.sim");
				
				//if there is already an autosave
				if(closing.isFile())
				{
					//delete the autosave file
					closing.delete();
				}
				
				//make a new autosave file
				worldList.get(worldNum).saveFile(worldList, worldNum, true, "sav/autosave.sim");
				
				//"SHUT. DOWN. EVERYTHING."
				//				- Madagascar, Pandemic II
				Platform.exit();
				System.exit(0);
			}
		});
		
		//if screen is closed
		/******************************************************
		 * THIS ONE IS FOR WHEN THE USER EXITS VIA THE X BUTTON
		 ******************************************************/
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) 
            {
            	//check if an autosave already exists
            	File closing = new File("sav/autosave.sim");
            	
            	//if there is already an autosave
				if(closing.isFile())
				{
					//delete the autosave file
					closing.delete();
				}
				
				//make a new autosave file
				worldList.get(worldNum).saveFile(worldList, worldNum, true, "sav/autosave.sim");
				
				//"SHUT. DOWN. EVERYTHING."
				//				- Madagascar, Pandemic II
            	Platform.exit();
                System.exit(0);
            }
		});
		
		/*****************************************************
		 * TOOLBAR
		 ****************************************************/
		//play button	
		Button playButton = new Button();
		Image playImg = new Image("/img/icons/start.png", 20, 20, false, false);
		playButton.setGraphic(new ImageView(playImg));
		
		//pause button
		Button pauseButton = new Button();
		Image pauseImg = new Image("/img/icons/pause.png", 20, 20, false, false);
		pauseButton.setGraphic(new ImageView(pauseImg));
		
		//restart button
		Button resetButton = new Button();
		Image resetImg = new Image("/img/icons/restart.png", 20, 20, false, false);
		resetButton.setGraphic(new ImageView(resetImg));
		
		//handler for play
		playButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				runSim = true;
			}
		});
		
		//handler for pause
		pauseButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				runSim = false;
			}
		});
		
		//handler for reset
		resetButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				loadFile(currentFile);
			}
		});
		
		/*****************************************************
		 * SIMULATION
		 ****************************************************/
		final long time = System.nanoTime();
		
		new AnimationTimer()
		{
			public void handle(long time2)
			{
				double times = (time2 - time) / 1000000000.0;
				//System.out.println(times);
				
				//check if the sim is to be run
				if(runSim)
				{
					worldList.get(worldNum).simulate(times);
				}
				
				if(toggle)
				{
					drawWorld();
				}
			}
		}.start();
		
		/*****************************************************
		 * SCREEN SETUP
		 ****************************************************/
		stage.setTitle("Artificial Life Simulator");
		
		gc = canvas.getGraphicsContext2D();
		
		bp.setCenter(center);
		bp.getChildren().add(canvas);
		bp.setPrefSize(windowX, windowY);
		
		ToolBar t = new ToolBar();
		t.getItems().addAll(playButton, pauseButton, resetButton);
		
		//add all menus to menubar
		menuBar.getMenus().addAll(file, view, edit, sim, help);
		
		//holds both menu bar and toolbar
		VBox top = new VBox();
		top.getChildren().add(menuBar);
		top.getChildren().add(t);
		
		//show vbox
		bp.setTop(top);
		
		
			
		//show to the screen
		stage.getIcons().add(new Image("/img/czarec.png"));
		stage.setScene(scene);
		stage.setResizable(true);
		stage.show();
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//FUNCTIONS TO CONTROL OTHER PARTS OF THE PROGRAM
	/////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Function to save the current simulation as an application generated filename
	 * @throws Exception
	 * @see saveAs
	 */
	public void save() throws Exception
	{
		//confirm to save
		Alert save = new Alert(AlertType.CONFIRMATION);
		save.setTitle("Save file");
		save.setHeaderText("Save simulation?");
		save.setContentText("Save simulation: Simulation" + worldNum + "?");
		
		ButtonType yes = new ButtonType("Yes");
		ButtonType no = new ButtonType("No");
		ButtonType savAs = new ButtonType("Save As");
		
		save.getButtonTypes().setAll(yes, no);
		
		//input listener
		Optional<ButtonType> result = save.showAndWait();
		
		if(result.get() == yes)
		{
			//check if file exists
			String existingFile = "sav/Simulation" + worldNum + ".sim";
			File exists = new File(existingFile);
			
			//if doesnt exist
			if(!exists.isFile())
			{
				worldList.get(worldNum).saveFile(worldList, worldNum, false, null);
			}
			//if file exists
			else
			{
				//asks for another input, i.e. if the user wants to overwrite or save as another name
				Alert save1 = new Alert(AlertType.CONFIRMATION);
				save1.setTitle("WARNING");
				save1.setHeaderText("Overwrite file?");
				save1.setContentText("Simulation" + worldNum + " already exists");
				
				save1.getButtonTypes().setAll(yes, no, savAs);
				
				//listener
				result = save1.showAndWait();
				
				//if yes
				if(result.get() == yes)
				{
					//delete file
					exists.delete();
					//remake file
					worldList.get(worldNum).saveFile(worldList, worldNum, false, null);
				}
				
				//if saveAs
				else if(result.get() == savAs)
				{
					saveAs();
				}
				
				else if(result.get() == no)
				{
					//nothing
				}
				
			}
		}
		else if(result.get() == no)
		{
			//nothing
		}
		
	}
	
	/**
	 * Function to individually draw entities into the canvas
	 * @param img
	 * @param hPos
	 * @param vPos
	 */
	public void showEntity(Image img, int hPos, int vPos)
	{
		//draw the image to the screem
		gc.drawImage(img, hPos, vPos, 30, 30);
	}
	
	/**
	 * Function to draw the world to the canvas as well as all the entities<br>
	 * @see showAllEntities
	 */
	public void drawWorld()
	{
		//clears the screen everytime its animated
		gc.clearRect(0, 0, windowX, windowY);
		gc.setFill(Color.FORESTGREEN);
		gc.fillRect(0, 0, windowX, windowY);
		//draw entities to screen
		worldList.get(worldNum).showAllEntities(this);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//run the program
		Application.launch(args);
	}

	

}
