package als;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

import als.AnEntity.Direction;

//import javax.swing.JList;
//import javax.swing.JOptionPane;

import als.AnEntity.traits;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * <h1>AWorld</h1>
 * AWorld is a class that handles the world that simulation takes place in and<br>
 * also stores the entities needed by the simulation in its entity ArrayList.<br>
 * AWorld's x and y size is used to determine the screen size as well
 * @author Czar Ian Echavez
 * @see AnEntity
 * @see GUI
 */
public class AWorld {
	
	//attributes
	private int maxEnt, //max entity in the world
	worldX, worldY, //world size
	entityCount; //counts the number of entities
	
	//arraylist to store the entities
	ArrayList<AnEntity> entityList = new ArrayList<AnEntity>();
	
	//rand
	Random r;

	/**
	 * Constructor for the world.
	 * Takes input: the world height and width
	 * The world then initiales an ArrayList for the entities
	 * 
	 * @param _worldX	world width
	 * @param _worldY	world height
	 * @see 			ArrayList
	 */
	AWorld(int _worldX, int _worldY)
	{
		//set attributes
		setX(_worldX);
		setY(_worldY);
		setMaxEnt(_worldX, _worldY);
		
		//entity array
		entityList = new ArrayList<AnEntity>();
	}
	
	/**
	 * Sets the width of the world
	 * @param _worldX 	the world's width
	 */
	public void setX(int _worldX)
	{
		worldX = _worldX;
	}
	
	/**
	 * Returns the width of the world
	 * @return worldX	the world's width
	 */
	public int getX()
	{
		return worldX;
	}
	
	/**
	 * Sets the height of the world
	 * @param _worldY	the world's height
	 */
	public void setY(int _worldY)
	{
		worldY = _worldY;
	}
	
	/**
	 * Returns the height of the world
	 * @return worldY	the world's height
	 */
	public int getY()
	{
		return worldY;
	}
	
	/**
	 * Calculates the maximum amount of entities
	 * If either temporary values become 0, it gets reset to 1.
	 * This is to prevent any world from having a maximum entity
	 * of 0
	 * @param _worldX	world width
	 * @param _worldY	world height
	 */
	public void setMaxEnt(int _worldX, int _worldY)
	{
		int temp1 = _worldX / 100;
		int temp2 = _worldY / 100;
		
		//prevents 0 errors
		if(temp1 <= 0)
		{
			temp1 = 1;
		}
		
		if(temp2 <= 0)
		{
			temp2 = 1;
		}
		
		maxEnt = (temp1 * temp2) * 10;
	}
	
	/**
	 * Returns the maximum entities allowed in the world
	 * @return  maxEnt	maximum entities allowed in the world
	 */
	public int getMaxEnt()
	{
		return maxEnt;
	}
	
	/**
	 * Returns the current number of entities in the world
	 * @return entityList.size()	size of the list is the number of entities in the world
	 * @see .size()
	 */
	public int getEntityCount()
	{
		return entityList.size();
	}
	
	/**
	 * Function to add new entities into the world.<br>
	 * Adds in the entity via their name, food and obstacles can be made<br>
	 * by setting the input name as "food" and "obstacle" respectively
	 * 
	 * @param _species	takes in species name
	 */
	public void addEntity(String _species)
	{
		//rand function to find random x and y value
		r = new Random();
		
		//checks to make sure the maxEntities is not exceeded
		if (entityList.size() < maxEnt)
		{
			//temporary variables
			int _hPos, _vPos;
			
			//do while loop will keep looping until a valid position is found
			//if getEntityAt function returns a positive value, valid position is found
			
			//prevents infinite loops
			int count = 0;
			
			do
			{
				/*the equation for the range:
				 * r.nextInt(max - min + 1) + min
				 */
				 
				_hPos = r.nextInt(worldX - 30);
				
				_vPos = r.nextInt((worldY  - 100)) + 70;
				
				count++;
				
			}while(getEntityAt(_hPos, _vPos) >= 0 && count < 1000);
			
			//if the species is food, make a new food entity
			if(_species.equals("food") && count < 1000)
			{
				//makes the food entity using the Food subclass
				Food food = new Food(_hPos, _vPos, entityCount, this);
				
				//add to the list
				entityList.add(food);
				
				//increment the number of entities
				entityCount++;
			}
			
			//if the species is obstacle, make an obstacle entity
			else if(_species.equals("obstacle") && count < 1000)
			{
				//makes the obstacle entity using the Obstacle subclass
				Obstacle obstacle = new Obstacle(_hPos, _vPos, entityCount, this);
				
				//add to the list
				entityList.add(obstacle);
				
				//increment the number of entities
				entityCount++;
			}
			
			//everything else that isnt food or obstacle
			else if(count < 1000)
			{
				//makes the lifeform using the Lifeform subclass
				Lifeform species = new Lifeform(_species, _hPos, _vPos, 5, entityCount, this);
				
				//add to the list
				entityList.add(species);
				
				//increment the number of entities
				entityCount++;
			}
			
		}
		
		else
		{
			//message to notify user that the max entities is exceeded
			Alert error = new Alert(AlertType.ERROR);
			error.setHeaderText("Maximum entities allowed exceeded");
			error.setContentText("Max Entities reached: " + maxEnt);
			error.showAndWait();
		}
	}
	
	/**
	 * Function to check if there is an entity in a 30 x 30 pixel region<br>
	 * around the input coordinates
	 * 
	 * @param _hPos	entity x coordinate
	 * @param _vPos	entity y coordinate
	 * @return var	either -1 or a value that is 0 or larger
	 */
	public int getEntityAt(int _hPos, int _vPos)
	{
		int var = -1;
		
		//check through all the entities in the entityList array
		for(int i = 0; i < entityList.size(); i++)
		{
			//check if the entity is null, prevents crashes
			if(entityList.get(i) != null)
			{
				//nested loop to check a region around the hPos and vPos
				for(int x = _hPos - 30; x < _hPos + 30; x++)
				{
					for(int y = _vPos - 30; y < _vPos + 30; y++)
					{
						if(entityList.get(i).checkPos(x, y))
						{
							var = i;
						}
					}
				}
			}
		}
		
		return var;
	}
	
	/**
	 * Display all entities to screen. Loops through all entities<br>
	 * and then calls displayEntity which draws the entity
	 * @param gui	GUI class
	 * @see displayEntity()
	 */
	public void showAllEntities(GUI gui)
	{
		for(int i = 0; i < entityList.size(); i++)
		{
			entityList.get(i).displayEntity(gui);
		}
	}
	
	/**
	 * Lists all non food and obstacle entities<br>
	 * The non food and obstacle entities get added into a separate temporary observableArrayList<br>
	 * which is then displayed to another window
	 * 
	 * @see ListView
	 * @see ObservableArrayList
	 */
	public void listAllEntities()
	{
		//arraylist to store all the strings to be returned
		ArrayList<String> str = new ArrayList<String>();
		
		//loop to get the details of the entities
		for(int i = 0; i < entityList.size(); i++)
		{
			//skip if the entity is null
			if(entityList.get(i) == null)
				continue;
			
			//only prints lifeforms
			if(!entityList.get(i).getSpecies().equals("food") && !entityList.get(i).getSpecies().equals("obstacle"))
			{
				str.add(entityList.get(i).toString(this));
			}
		}
		
		//convert array list to normal array
		String ent[] = new String[entityList.size()];
		ent = str.toArray(ent);

		//observes the arraylist str
		ObservableList<String> strList = FXCollections.observableArrayList(str);
		//listView
		ListView<String> list = new ListView<String>(strList);
		
		Alert msg = new Alert(AlertType.INFORMATION);
		msg.setHeaderText("Entity List");
		
		//check if there are actually entities
		if(ent.length > 0)
		{
			//pane to show the listView
            StackPane listWindow = new StackPane();
            listWindow.getChildren().add(list);
            
            //scene
            Scene listViewScene = new Scene(listWindow, 750, 400);
            
            //stage
            Stage listViewStage = new Stage();
            listViewStage.setTitle("Entity List");
            listViewStage.setScene(listViewScene);
            
            //show to screen
            listViewStage.getIcons().add(new Image("/img/czarec.png"));
            listViewStage.show();
		}
		else
		{
			msg.setContentText("No entities exist in the world");
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	//SIMULATION FUNCTIONS
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//booleans used for direction checks
	boolean nAvailable;
	boolean sAvailable;
	boolean eAvailable;
	boolean wAvailable;
	
	/**
	 * The main part of the program, simulates the world and the entities' actions<p>
	 * Simulates:<br>
	 * - entities grabbing food that they (their image) can reach/touch<br>
	 * - eating adding energy to the entity<br>
	 * - food with a poisonous attribute set to true kills entities<br>
	 * - "avoiding" obstacles (entities can still go through obstacles<br>
	 * but clearly struggles<br>
	 * - moving, using direction enums<br>
	 * - using energy, when either moving or just existing (prevents immortality) <br>
	 * - checks to see if the entity should be killed i.e. energy < 0<br>
	 * - dens and food source traited entities spawn entities and food respectively<br>
	 * 
	 * @param time	the animation timer is used to determine when dens and obstacles
	 * should spawn entities
	 */
	public void simulate(Double time)
	{
		//order of simulation
		//grabFood() - check if there is food nearby
		//	¬eatFood() - eat food if it is nearby
		//	¬ 	//¬ add energy to entity
		//	¬poisoned() - was that food poisonous?
		
		//avoid() - avoid obstacles and other entities that isnt food
		
		//	¬move(moveSpd) - move towards the food unless it is agoraphobic or a student
		//	¬useEnergy(energy) - entity will use up energy
		
		//checkEnergy(energy) - check if entity still has energy
		//	¬setDeath() - entity dies if it has no energy left
		//use the time as a value to respawn food
		//same principle with the dens
		
		//obstacle direction availability
		nAvailable = true;
		sAvailable = true;
		eAvailable = true;
		wAvailable = true;
		
		//loop through all entities
		for(int i = 0; i < entityList.size(); i++)
		{
			//skip if entity is null
			if(entityList.get(i).equals(null))
			{continue;}
			
			//entity
			AnEntity entity = entityList.get(i);
			
			//get this entity's name
			String species = entity.getSpecies();
			boolean dead = entity.isKill();
			
			//if not food or obstacle and not dead
			if(!(species.equals("food") || species.equals("obstacle") || dead))
			{
				
				//all lifeform entities grab food from sorroundings
				grabFood(entity);
				
				//check for student traits
				//students only move randomly
				traits student = traits.student;
				if(entity.seeTrait().equals(student))
				{
					//avoid obstacles
					avoid(entity);
					
					//get random direction and move towards it
					randDir(entity);
					move(entity);
				}
				
				//check for agoraphobic traits and student traits
				//agoraphobics do not move, students randomly move
				traits agoraphobic = traits.agoraphobic;
				if(!entity.seeTrait().equals(agoraphobic) && !entity.seeTrait().equals(student))
				{
					//smell food, if no food then
					//random moving, students can only do random moving but still avoids obstacles
					
					avoid(entity);
					
					//use the hunting value from entity
					//hunting = findFood(entity);
					
					//if the entity does not see food, get random direction every 10 steps
					if((entity.getMoveCount() % 10 == 0))
					{
						//random dir
						randDir(entity);
					}
								
					move(entity);
				}
				
				//use energy regardless of move or not
				useEnergy(entity, "idle");
				
				//check to kill entity or not
				checkEnergy(entity);
			}
			
			traits foodSource = traits.food_source;
			//spawn food around a food source obstacle
			if(entity.seeTrait().equals(foodSource) && (time%20 < 0.05))
			{
				dropFood(entity);
			}
			
			traits den = traits.den;
			//spawn entities around a den obstacle
			if(entity.seeTrait().equals(den) && (time%30 < 0.05))
			{
				spawnLife(entity);
			}
			
			
		}
		//System.out.println((time%15 < 0.05) + " " + time);
	}
	
	/**
	 * Function to determine the food source behaviour i.e.<br>
	 * to spawn food where and when
	 * @param entity entity is passed to determine locations of food spawn
	 */
	public void dropFood(AnEntity entity)
	{
		//check if there is room in the world
		if(entityList.size() < maxEnt)
		{
			//random
			Random r = new Random();
			
			//stores the entity's planned pos
			int hPos;
			int vPos;
			
			//get the food source's pos
			int spawnerX = entity.getHPos();
			int spawnerY = entity.getVPos();
			
			//count to ensure it doesnt loop forever
			int count = 0;
			
			do
			{
				//get random pos relative to spawner
				hPos = r.nextInt(90) + (spawnerX - 40);
				
				vPos = r.nextInt(90) + (spawnerY - 40);
				
				//increment count
				count++;
				
				//System.out.println(count);
				
				//keep looping until pos is acceptable, no entity around the position AND
				//count is less than 1000
			}while((getEntityAt(hPos, vPos) >= 0 && count < 1000) && acceptablePos(hPos, vPos, entity));
			
			if(count < 1000)
			{
				//add the food
				Food food = new Food(hPos, vPos, entityCount, this);
				
				//add to ent list
				entityList.add(food);
				
				//increment the number of entities
				entityCount++;
			}
		}
	}
	
	/**
	 * Function to determine the den behaviour i.e.<br>
	 * to spawn entities where and when
	 * 
	 * @param entity used to determine where to spawn entities
	 */
	public void spawnLife(AnEntity entity)
	{
		//check if there is room in the world
				if(entityList.size() < maxEnt)
				{
					//random
					Random r = new Random();
					
					//stores the entity's planned pos
					int hPos;
					int vPos;
					
					//get the den's pos
					int spawnerX = entity.getHPos();
					int spawnerY = entity.getVPos();
					
					//count to ensure it doesnt loop forever
					int count = 0;
					
					do
					{
						//get random pos relative to spawner
						hPos = r.nextInt(90) + (spawnerX - 40);
						
						vPos = r.nextInt(90) + (spawnerY - 40);
						
						//increment count
						count++;
						
						//System.out.println(count);
						
						//keep looping until pos is acceptable, no entity around the position AND
						//count is less than 1000
					}while((getEntityAt(hPos, vPos) >= 0 && count < 1000) && acceptablePos(hPos, vPos, entity));
					
					if(count < 1000)
					{
						//add the new entity
						Lifeform spawn = new Lifeform("spawn", hPos, vPos, 5, entityCount, this);
						
						//add to ent list
						entityList.add(spawn);
						
						//increment the number of entities
						entityCount++;
					}
				}
	}
	
	/**
	 * Function to check if the random values of x and y are valid i.e. in range of the<br>
	 * spawner, but isn't the same coordinate as the spawner.<br>
	 * Default return is true because we'd want the loop to do another run if it is not acceptable
	 * 
	 * @param x	randomised x value
	 * @param y randomised y value
	 * @param entity	the spawner
	 * @return var either true or false
	 */
	public boolean acceptablePos(int x, int y, AnEntity entity)
	{
		boolean var = true;
		
		int entX = entity.getHPos();
		int entY = entity.getVPos();
		
		//define an area around the den/food_source
		if(
				((x >= entX - 60 && x < entX - 30) || (x > entX + 30 && x <= entX + 60)) &&
				((y >= entY - 60 && y < entY - 30) || (y > entY + 30 && y <= entY + 60))
				)
		{
			var = false;
			
		}
		//System.out.println(var);
		return var;
	}
	
	/**
	 * Entity looks around to see if it can grab food<br>
	 * Function checks the region within its 30 x 30 area (size of image)
	 * 
	 * @param entity 
	 */
	public void grabFood(AnEntity entity)
	{
		//get current entity's coordinates
		int x = entity.getHPos();
		int y = entity.getVPos();
		
		//setting up min and max area checks
		int minX = x - 25;
		if (minX < 0)
		{
			minX = 0;
		}
		
		int maxX = x + 25;
		if(maxX > worldX)
		{
			maxX = worldX;
		}
		
		int minY = y - 25;
		if(minY < 0)
		{
			minY = 0;
		}
		
		int maxY = y + 25;
		if(maxY > worldY)
		{
			maxY = worldY;
		}
		
		//ensures entity doesnt eat everything
		boolean hasEaten = false;
		
		//check for food
		for(int i = minX; i < maxX; i++)
		{
			for(int j = minY; j < maxY; j++)
			{
				//exit loop if entity has eaten
				if(hasEaten)
				{
					j = maxY;
					continue;
				}
				
				//check around the entity if it can grab food
				hasEaten = eat(entity, i, j);
			}
			
			//exit loop
			if(hasEaten)
			{
				i = maxX;
				continue;
			}
		}
		
	}
	
	/**
	 * Function to eat the food, should the entity find one around it
	 * @param entity
	 * @param x current x coordinate being looked at
	 * @param y current y coordinate being looked at
	 * @return either true or false, true if there was food and the entity ate it
	 */
	public boolean eat(AnEntity entity, int x, int y)
	{
		//boolean to be returned
		boolean var = false;
		
		//loop through all the entities
		for(int i = 0; i < entityList.size(); i++)
		{
			//check if the entity is null/empty
			if(!entityList.get(i).equals(null))
			{
				//check the position if the entity exists there
				if(entityList.get(i).checkPos(x, y))
				{
					//check if the entity being scanned is food
					if(entityList.get(i).getSpecies().equals("food"))
					{
						var = true;
						//System.out.println(entityList.get(i).poisonCheck());
						//check if the food was poisonous
						if(entityList.get(i).poisonCheck())
						{
							//R.I.P.
							entity.setDeath();
						}
						//if not poisonous
						else
						{
							//add energy
							double energy = entity.getEnergy();
							entity.setEnergy(energy + 5);
							//no longer hunting
							entity.setHunting(false);
						}
						
						//remove the food
						entityList.remove(i);
					}
				}
			}
		}
		
		return var;
	}
	
	/**
	 * Function that helps the entity determine the direction to go and check if there<br>
	 * is an obstacle in the way
	 * @param entity
	 */
	public void avoid(AnEntity entity)
	{
		//get current entity's coordinates
		int x = entity.getHPos();
		int y = entity.getVPos();
		
		//setting up min and max area checks
		int minX = x - 30;
		if (minX < 0)
		{
			minX = 0;
		}
		
		int maxX = x + 30;
		if(maxX > worldX)
		{
			maxX = worldX;
		}
		
		int minY = y - 30;
		if(minY < 0)
		{
			minY = 0;
		}
		
		int maxY = y + 30;
		if(maxY > worldY)
		{
			maxY = worldY;
		}
		
		for(int i = minX; i < maxX; i++)
		{
			for(int j = minY; j < maxY; j++)
			{
				//check the entire region
				wall(entity, i, j);
			}
		}
	}
	
	/**
	 * Function to check if there is an obstacle and set which direction<br>
	 * the entity shouldn't go
	 * @param entity
	 * @param _x
	 * @param _y
	 */
	public void wall(AnEntity entity, int _x, int _y)
	{
		
		int entY = entity.getVPos();
		int entX = entity.getHPos();
		
		for(int i = 0; i < entityList.size(); i++)
		{
			if(!entityList.get(i).equals(null))
			{
				if(entityList.get(i).checkPos(_x, _y))
				{
					if(entityList.get(i).getSpecies().equals("obstacle"))
					{
						
						if((entY < _y + 30) && (entY > _y))
						{
							nAvailable = false;
							eAvailable = false;
							wAvailable = false;
						}
						if((entY > _y - 30) && (entY < _y))
						{
							sAvailable = false;
							eAvailable = false;
							wAvailable = false;
						}
						if((entX > _x - 30) && (entX < _x))
						{
							eAvailable = false;
							nAvailable = false;
							sAvailable = false;
						}
						if((entX < _x + 30) && (entX > _x))
						{
							wAvailable = false;
							nAvailable = false;
							sAvailable = false;
						}
						
						
						randDir(entity);
					}
				}
			}
		}
	}
	
	/**
	 * Function to give the entity a random direction , but the direction must be valid<br>
	 * i.e. moves the entity away from the obstacle
	 * @param entity
	 */
	public void randDir(AnEntity entity)
	{
		//prevents infinite loops
		int count = 0;
		
		boolean loop = true;
		
		do
		{
			loop = isRouteNotAllowed(entity);
			
			//get random dir until valid
			entity.setDirection(Direction.getRandom());
			
			if(count > 1000)
			{
				loop = false;
			}
			
			count++;
			
			//System.out.println(isRouteNotAllowed(entity));
			
		}while(loop);
	}
	
	/**
	 * Check if the direction received is valid
	 * @param entity
	 * @return var true or false, of the route is valid
	 */
	public boolean isRouteNotAllowed(AnEntity entity)
	{
		boolean var = false;
		
		//get entity direction
		Direction check = entity.seeDirection();
		
		//if the route isnt available and the direction is towards it, return true
		if(!nAvailable && (check.equals(Direction.N)))
		{
			var = true;
		}
		if(!sAvailable && (check.equals(Direction.S)))
		{
			var = true;
		}
		if(!eAvailable && (check.equals(Direction.E)))
		{
			var = true;
		}
		if(!wAvailable && (check.equals(Direction.W)))
		{
			var = true;
		}
		
		return var;
	}

	/**
	 * Function that uses up an entity's energy depending on the activity<br>
	 * the entity happens to be doing
	 * @param entity
	 * @param activity	String input, which can either be "move" or "idle"
	 */
	public void useEnergy(AnEntity entity, String activity)
	{
		//2 ways of using energy, being alive just uses 0.05
		//moving will depend on the animal trait
		double energy = entity.getEnergy();
		double energyUse = entity.getEnergyUse();
		traits tired = traits.fatigued;
		traits fit = traits.energy_efficient;
		
		//moving used energyUse
		if(activity.equals("move"))
		{
			//double energy use if fatigued
			if(entity.seeTrait().equals(tired))
			{
				entity.setEnergy(energy - (energyUse * 2)) ;
			}
			//half energy use if energy_efficient
			else if(entity.seeTrait().equals(fit))
			{
				entity.setEnergy(energy - (energyUse / 2));
			}
			//normal energy use
			else
			{
				entity.setEnergy(energy - energyUse);
			}
		}
		//being idle only uses 1/5 of energy use
		//even if entity moved it will still use this energy for being alive
		else
		{
			//double energy use if fatigued
			if(entity.seeTrait().equals(tired))
			{
				entity.setEnergy(energy - ((energyUse * 2) / 5)) ;
			}
			//half energy use if energy_efficient
			else if(entity.seeTrait().equals(fit))
			{
				entity.setEnergy(energy - ((energyUse / 2) / 5));
			}
			//normal energy use
			else
			{
				entity.setEnergy(energy - (energyUse / 5));
			}
		}
		
	}
	
	/**
	 * Function to check if the entity should be killed or not.<br>
	 * Checks if entity energy < 0
	 * @param entity
	 */
	public void checkEnergy(AnEntity entity)
	{
		//check if energy is less than 0, if it is  entity will die
		if(entity.getEnergy() <= 0)
		{
			entity.setDeath();
		}		
	}
	
	//////////
	//MOVEMENT
	//////////
	
	/**
	 * Function that moves the entity
	 * @param entity
	 */
	public void move(AnEntity entity)
	{
		//check if on the edge of screen
		//increment entity moveCount
		//move towards direction
		
		//get entity values
		int x = entity.getHPos();
		int y = entity.getVPos();
		int moveSpd = entity.getMoveSpd();
		Direction entD = entity.seeDirection();
		
		switch(entD)
		{
			//move north	
			case N:
				//make sure the entity doesn't walk off screen
				if(y - moveSpd > 60)
				{
					entity.setVPos(y - moveSpd);
					//use energy, because it moved
					useEnergy(entity, "move");
				}
				//keeps the entity in the world
				else
				{
					//set the entity at the border and get a random direction 
					//and randomise the direction every 10 moves
					entity.setVPos(60);
					randDir(entity);
				}
				break;
				
			//move south
			case S:
				if(y + moveSpd < worldY - 30)
				{
					entity.setVPos(y + moveSpd);
					useEnergy(entity, "move");
				}
				else
				{
					entity.setVPos(worldY-30);
					randDir(entity);
				}
				break;
			
			//move east
			case E:
				if(x + moveSpd < worldX - 30)
				{
					entity.setHPos(x + moveSpd);
					useEnergy(entity, "move");
				}
				else
				{
					entity.setHPos(worldX-30);
					randDir(entity);
				}
				break;
				
			//move west
			case W:
				if(x - moveSpd > 0)
				{
					entity.setHPos(x - moveSpd);
					useEnergy(entity, "move");
				}
				else
				{
					entity.setHPos(0);
					randDir(entity);
				}
				break;
				
			default:
				
				break;				
		}

		int moveCount = entity.getMoveCount();
		entity.setMoveCount(moveCount+1);
		
	}
	
	//END OF SIMULATION FUNCTIONS
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Function to save the world and its entities to a file
	 * @param worldList current world
	 * @param worldNum current world's number
	 * @param hasFilename boolean, so the function knows to save the file with different names
	 * @param fileName can be null; the name for the file
	 * @see FileWriter
	 */
	public void saveFile(
			ArrayList<AWorld> worldList, //save the world
			int worldNum, //get the world number
			boolean hasFilename, //check to see if the save file has been set a name
			String fileName //filename for the save
			)
	{
		//file io function
		FileWriter writer = null;
		
		//checking hasFilename
		if(hasFilename)
		{
			try {
				writer = new FileWriter(fileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			//true means append
			try {
				writer = new FileWriter("sav/Simulation" + worldNum + ".sim", true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//saves the world data
		int worldX = worldList.get(worldNum).getX();
		int worldY = worldList.get(worldNum).getY();
		int maxEnt = worldList.get(worldNum).getMaxEnt();
		
		//save the world's size
		try {
			writer.write(worldX + "," + worldY + "," + maxEnt + "," + 0 + "," + 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Alert error = new Alert(AlertType.ERROR);
			error.setTitle("Error");
			error.setHeaderText("Program cannot start");
			error.showAndWait();
			System.exit(0);
		}
				
		//saving the entities
		for(int i = 0; i < entityList.size(); i++)
		{
			//line separator so each file is separated by a line
			try {
				writer.write(System.getProperty("line.separator"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//check if entity is null
			if(!entityList.get(i).equals(null))
			{
				//get the entity's values
				String _species = entityList.get(i).getSpecies();
				int _hPos = entityList.get(i).getHPos();
				int _vPos = entityList.get(i).getVPos();
				double _energy = entityList.get(i).getEnergy();
				int _uniqueID = entityList.get(i).getUID();
				boolean _dead = entityList.get(i).isKill();
				boolean _poisonous = entityList.get(i).poisonCheck();
				boolean _albino = entityList.get(i).albinoCheck();
				traits _t = entityList.get(i).seeTrait();
				
				//write to file
				try {
					writer.write(_species + "," + _hPos + "," + _vPos + "," + 
							_energy + "," + _uniqueID + "," + _dead + "," + 
							_poisonous + "," + _albino + "," + _t + "\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		//finish writing to file
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Function to load entities from a file
	 * 
	 * @param _species name of the entity
	 * @param _hPos entity x coordinate
	 * @param _vPos entity y coordinate
	 * @param _energy entity energy
	 * @param _uniqueID entity unique ID
	 * @param _dead attribute to see if the entity is dead
	 * @param _poisonous attribute to see if entity is poisonous
	 * @param _albino attribute to see if entity is albino
	 * @param _t entity's trait
	 */
	public void loadEntity(String _species, int _hPos, int _vPos, double _energy,
			int _uniqueID, boolean _dead, boolean _poisonous, boolean _albino, traits _t)
	{
		//check what entity is being loaded
		if(_species.equals("food"))
		{
			Food food = new Food(_hPos, _vPos, _uniqueID, this, _poisonous);
			
			entityList.add(food);
		}
		
		else if(_species.equals("obstacle"))
		{
			Obstacle obstacle = new Obstacle(_hPos, _vPos, _uniqueID, this, _t);
			
			entityList.add(obstacle);
		}
		
		else
		{
			Lifeform species = new Lifeform(_species, _hPos, _vPos, _energy, _uniqueID, 
			this, _dead , _albino, _t);
			
			entityList.add(species);
		}
		
		//increment entity count
		entityCount++;
	}
	
	/**
	 * Function to remove entity from the simulation<br>
	 * Called when entity eats food, or user removes latest entity
	 */
	public void removeEntity()
	{	
		//make sure that there are entities to delete in the first place
		if(entityList.size() > 0)
		{
			//set up alert message
			Alert remove = new Alert(AlertType.CONFIRMATION);
			remove.setTitle("Remove entity");
			remove.setHeaderText("Remove latest entity?: ");
			remove.setContentText(entityList.get(entityList.size()-1).toString(this));
			//buttons
			ButtonType yes = new ButtonType("Yes");
			ButtonType no = new ButtonType("No");
			
			
			//bind butons
			remove.getButtonTypes().setAll(yes, no);
			
			//listener
			Optional<ButtonType> result = remove.showAndWait();			
			
			//if the user says yes, delete the entity in the end of the arraylist
			if(result.get() == yes)
			{
				entityList.remove(entityCount-1);
				entityCount--;
			}
			else
			{
				//nothing
			}
		}
		else
		{
			Alert error = new Alert(AlertType.ERROR);
			error.setHeaderText("No entities to remove");
			error.showAndWait();
		}
	}
	
	/**
	 * Get the size of the entity List
	 * @return
	 */
	public int getEntSize()
	{
		return entityList.size();
	}
	
	/**
	 * Function to edit the last entity in the ArrayList
	 * @param species entity name
	 * @param hPos entity x coordinate
	 * @param vPos entity y coordinate
	 * @param energy entity's energy
	 * @param dead attribute to show if entity is dead
	 * @param poisonous attribute to see if entity is poisonous
	 * @param albino attribute to see if entity is albino
	 * @param t entity trait
	 * @param entNum entity uniqueID
	 */
	public void editEntity(String species, int hPos, int vPos, double energy,
			boolean dead, boolean poisonous, boolean albino, traits t, int entNum)
	{
		//get entity
		AnEntity entity = entityList.get(entNum);
		
		//change ent values
		entity.setSpecies(species);
		entity.setHPos(hPos);
		entity.setVPos(vPos);
		entity.setEnergy(energy);
		entity.setDead(dead);
		entity.setPoison(poisonous);
		entity.setAlbino(albino);
		entity.setTraits(t);
		
		//update ent image
		entity.traitSetup(t, dead);
	}
	
	/**
	 * Function to return the required entity.<br>
	 * Used to get an entity to be modified from thr GUI
	 * @param i
	 * @return entity
	 */
	public AnEntity getEntity(int i)
	{
		AnEntity entity = entityList.get(i);
		return entity;
	}
	
	/**
	 * Function to clear the non moving entities off the edge of the world<br>
	 * These entities would be dead things, agoraphobics and food & obstacles
	 */
	public void clearEnt()
	{
		//check all entities
		for(int i = 0; i < entityList.size(); i++)
		{
			if(!entityList.get(i).equals(null))
			{
				AnEntity ent = entityList.get(i);
				if(ent.getVPos() > worldY || ent.getHPos() > worldX)
				{
					if(ent.getSpecies().equals("food") || ent.getSpecies().equals("obstacle") ||
						ent.seeTrait().equals(traits.agoraphobic) || ent.isKill())
					{
						entityList.remove(i);
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		GUI.main(args);
	}

}
