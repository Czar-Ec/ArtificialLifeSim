package als;

import java.util.Random;
import javafx.scene.image.Image;

/**
 * <h1>AnEntity</h1>
 * AnEntity super class which handles the information regarding the entities on the screen<br>
 * Subclasses include Food, Obstacle and Lifeform. The subclasses use the functions contained<br>
 * within this class, since they inherit everything from AnEntity
 * @author Czar Ian Echavez
 * @see Food
 * @see Obstacle
 * @see Lifeform
 */

public abstract class AnEntity {
	
	//entity main attributes
	protected String species;
	protected double energy;
	protected int hPos, vPos, uniqueID;
	protected AWorld world; //world the entity exists in
	
	//stores entity image
	protected Image img;
	
	//movement and behaviour
	protected double energyUse; //will be deducted against energy of entity
	protected int moveSpd, //number of coords the entity moves per frame
	moveCount, //used for when theres nothing nearby
	smellRange; //range of smellFood
	
	protected boolean hunting, //has the entity smelt food
	dead, //is the entity dead
	albino, //is the entity an albino organism
	poisonous; //used for food entities, will kill normal entities
	
	//traits the entity has
	protected traits t;
	
	//direction the entity is facing
	protected Direction d;
	
	/**
	 * <h1>traits</h1>
	 * eNum used to give the entities certain traits<br>
	 * These traits give the entities different behaviours
	 * @author Czar Echavez
	 *
	 */
	public enum traits
	{
		//values of traits
		fast_walker, //+1 move
		injured, //-1 move
		energy_efficient, //half energy use
		fatigued, //2x energy use
		hunter, //2x smell range
		blocked_nose, //half smell range
		student, //literally has no idea what it's doing, will move randomly
		agoraphobic, //will not move from where it spawned
		food, //entity is food
		obstacle, //entity is obstacle
		food_source, //entity is a food source
		den; //entity can spawn lifeforms
		
		//get random trait
		public static traits getTrait()
		{
			//random
			Random r = new Random();
			
			//return the random trait value
			//-2 because food and obstacle should only be assigned manually
			return values()[r.nextInt(values().length-4)];
		}
	}
	
	/**
	 * <h1>Direction</h1>
	 * Direction eNum used to get the entity to move a certain direction<br>
	 * Based on Richard Mitchell's Robot.java example
	 * @author Czar Echavez
	 * @see Richard Mitchell
	 */
	public enum Direction
	{
		//values of direction
		N, S, E, W, temp;
		public static Direction getRandom()
		{
			//get random value of direction
			Random r = new Random();
			
			//return the random, except for temp
			return values()[r.nextInt(values().length-1)];
		}
	}
	
	/**
	 * Empty constructor. will be used by subclasses
	 */
	AnEntity()
	{
		//left empty
	}
	
	/**
	 * Main constructor which is used to create entities
	 * @param _species
	 * @param _hPos
	 * @param _vPos
	 * @param _energy
	 * @param _uniqueID
	 * @param _world
	 */
	AnEntity(String _species, int _hPos, int _vPos, double _energy, int _uniqueID,
			AWorld _world)
	{
		//setting up values that were input
		setSpecies(_species);
		setHPos(_hPos);
		setVPos(_vPos);
		setEnergy(_energy);
		setUID(_uniqueID);
		setWorld(_world);
		
		//setting up base values [numeric vals]
		energyUse = 0.00025;
		moveSpd = 5;
		moveCount = 0;
		smellRange = 50;
		
		//setting up base values [booleans]
		hunting = false;
		dead = false;
		
		//randomised booleans
		albino = isAlbino();
		poisonous = isPoisonous();
		
		//get random traits
		t = traits.getTrait();
		
		//apply traits
		traitSetup(t, dead);
	}

	/**
	 * Setting entity species
	 * @param _species
	 */
	public void setSpecies(String _species)
	{
		species = _species;
	}
	
	/**
	 * Returns the entity species
	 * @return species
	 */
	public String getSpecies()
	{
		return species;
	}
	
	/**
	 * Setting x coordinate
	 * @param _hPos
	 */
	public void setHPos(int _hPos)
	{
		hPos = _hPos;
	}
	
	/**
	 * Getting x coordinate
	 * @return hPos
	 */
	public int getHPos()
	{
		return hPos;
	}
	
	/**
	 * Setting y coordinate
	 * @param _vPos
	 */
	public void setVPos(int _vPos)
	{
		vPos = _vPos;
	}
	
	/**
	 * Getting y coordinate
	 * @return vPos
	 */
	public int getVPos()
	{
		return vPos;
	}
	
	/**
	 * Setting the entity energy
	 * @param _energy
	 */
	public void setEnergy(double _energy)
	{
		energy = _energy;
	}
	
	/**
	 * Getting entity energy
	 * @return energy
	 */
	public double getEnergy()
	{
		return energy;
	}
	
	/**
	 * Setting uniqueID
	 * @param _uniqueID
	 */
	public void setUID(int _uniqueID)
	{
		uniqueID = _uniqueID;
	}
	
	/**
	 * Getting the uniqueID
	 * @return uniqueID
	 */
	public int getUID()
	{
		return uniqueID;
	}
	
	/**
	 * Setting the world (the entity exists in)
	 * @param _world
	 */
	public void setWorld(AWorld _world)
	{
		world = _world;
	}
	
	/**
	 * Getting the world
	 * @return world;
	 */
	public AWorld getWorld()
	{
		return world;
	}
	
	/**
	 * Getting the entity trait
	 * @return t the trait
	 */
	public traits seeTrait()
	{
		return t;
	}
	
	/**
	 * Set the entity trait
	 * @param _t input trait
	 */
	public void setTraits(traits _t)
	{
		t = _t;
	}
	
	/**
	 * Set the entity move direction
	 * @param _d
	 */
	public void setDirection(Direction _d)
	{
		d = _d;
	}
	
	/**
	 * Get the entity direction
	 * @return d
	 */
	public Direction seeDirection()
	{
		return d;
	}
	
	//smell range
	public int getSmellRange()
	{
		return smellRange;
	}
	
	/**
	 * Get the energy the entity uses every time they move/stay idle
	 * @return energyUse
	 */
	public double getEnergyUse()
	{
		return energyUse;
	}
	
	/**
	 * Function to randomise whether the entity should be albino or not
	 * @return true or false
	 */
	public boolean isAlbino()
	{
		Random r = new Random();
		
		int val = r.nextInt(1000) + 1;
		
		//10% chance of being albino
		if(val <= 100)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Set albino<br>
	 * If the entity needs to be set a specific albino
	 * @param _albino
	 */
	public void setAlbino(boolean _albino)
	{
		albino = _albino;
	}
	
	/**
	 * Function to randomise whether or not the entity should be poisonous
	 * @return true or false
	 */
	public boolean isPoisonous()
	{
		Random r = new Random();
		
		int val = r.nextInt(1000) + 1;
		
		//5% chance of being poisonous
		if(val <= 50)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Function to randomise whether or not the obstacle should be a spawner<br>
	 * i.e. food source or dens
	 * @return traits (.food_source or .den or .obstacle)
	 * @see traits
	 */
	public traits specialWall()
	{
		traits t;
		
		Random r = new Random();
		
		int val = r.nextInt(1000) + 1;
		
		//1% chance of being a food source
		if(val <= 10)
		{
			t = traits.food_source;
		}
		//10% chance of being a den
		else if(val > 10 && val <=110)
		{
			t = traits.den;
		}
		else
		{
			t = traits.obstacle;
		}
		
		return t;
	}
	
	/**
	 * Function to set the entity a specific value for poisonous
	 * @param _poisonous
	 */
	public void setPoison(boolean _poisonous)
	{
		poisonous = _poisonous;
	}
	
	/**
	 * Function to kill entity
	 */
	public void setDeath()
	{
		dead = true;
		
		//prints F to pay respects
		//System.out.println("F");
		
		traitSetup(t ,dead);
	}
	
	/**
	 * Function to set whether or not the entity is dead or alive<br>
	 * Simulation does not support entities being both alive and dead
	 * @param _dead
	 * @see Schrodinger
	 */
	public void setDead(boolean _dead)
	{
		dead = _dead;
	}
	
	/**
	 * Set the entity's hunting state
	 * @param _hunting
	 */
	public void setHunting(boolean _hunting)
	{
		hunting = _hunting;
	}
	
	/**
	 * Get the entity's hunting state
	 * @return hunting
	 */
	public boolean getHunting()
	{
		return hunting;
	}
	
	/**
	 * check entity state i.e. dead or not
	 * @return dead
	 */
	public boolean isKill()
	{
		return dead;
	}
	
	/**
	 * Function to check the entity's poisnous attribute
	 * @return poisonous
	 */
	public boolean poisonCheck()
	{
		return poisonous;
	}
	
	/**
	 * Function to set the entity#s moveCount
	 * @param move
	 */
	public void setMoveCount(int move)
	{
		moveCount = move;
	}
	
	/**
	 * Get the entity's moveCount
	 * @return moveCount
	 */
	public int getMoveCount()
	{
		return moveCount;
	}
	
	/**
	 * Get the entity's move speed
	 * @return moveSpd
	 */
	public int getMoveSpd()
	{
		return moveSpd;
	}
	
	/**
	 * check if entity is albino
	 * @return albino
	 */
	public boolean albinoCheck()
	{
		return albino;
	}

	/**
	 * Sets up the entity by looking at its trait and sets the appropriate image<b>
	 * The function also checks whether or not the entity is dead
	 * @param t
	 * @param dead
	 * @see Image
	 */
	public void traitSetup(traits t, boolean dead)
	{
		//LOAD ALBINO IMAGES
		Image aFastWalker = new Image("/img/albino/fast_walker.png");
		Image aInjured = new Image("/img/albino/injured.png");
		Image aEnergyEfficient = new Image("/img/albino/energy_efficient.png");
		Image aFatigued = new Image("/img/albino/fatigued.png");
		Image aHunter = new Image("/img/albino/hunter.png");
		Image aBlockedNose = new Image("/img/albino/blocked_nose.png");
		Image aStudent = new Image("/img/albino/student.png");
		Image aAgoraphobic = new Image("/img/albino/agoraphobic.png");
		
		//LOAD NORMAL IMAGES
		Image fastWalker = new Image("/img/normal/fast_walker.png");
		Image injured = new Image("/img/normal/injured.png");
		Image energyEfficient = new Image("/img/normal/energy_efficient.png");
		Image fatigued = new Image("/img/normal/fatigued.png");
		Image hunter = new Image("/img/normal/hunter.png");
		Image blockedNose = new Image("/img/normal/blocked_nose.png");
		Image student = new Image("/img/normal/student.png");
		Image agoraphobic = new Image("/img/normal/agoraphobic.png");		
		
		//LOAD FOOD AND OBSTACLE IMAGES
		Image food = new Image("/img/food.png");
		Image obstacle = new Image("/img/obstacle.png");
		
		//dead
		//set image to entity
		Image adeadImg = new Image("/img/albino/dead.png");
		Image deadImg = new Image("/img/normal/dead.png");

		
		Image den = new Image("/img/den.png");
		Image food_source = new Image("/img/food_source.png");
		
		
		//switch case to find the trait and apllies the related values
		//and images
		switch(t)
		{
			case fast_walker:
				moveSpd+=1; //+1 move	
				//if entity is albino
				if(albino){img = aFastWalker;}
				else{img = fastWalker;}
				break;
				
			case injured:
				moveSpd-=1; //-1 move
				
				if(albino){img = aInjured;}
				else{img = injured;}
				break;
				
			case energy_efficient:
				energyUse/=2; //half energy use
				
				if(albino){img = aEnergyEfficient;}
				else{img = energyEfficient;}
				break;
				
			case fatigued:
				energyUse*=2; //2x energy use
				
				if(albino){img = aFatigued;}
				else{img = fatigued;}
				break;
				
			case hunter:
				smellRange*=2; //2x smell range
				
				if(albino){img = aHunter;}
				else{img = hunter;}
				break;
				
			case blocked_nose:
				smellRange/=2; //half smell range
				
				if(albino){img = aBlockedNose;}
				else{img = blockedNose;}
				break;
				
			case student:
				//nothing applied, just this student will be VERY confused later
				
				if(albino){img = aStudent;}
				else{img = student;}
				
				smellRange = 0;
				break;
				
				/*
				 * below, agoraphobic and food & obstacle have been split instead of together because
				 * they have separate images 
				 */
				
			case agoraphobic:
				moveSpd = 0;
				
				if(albino){img = aAgoraphobic;}
				else{img = agoraphobic;}
				
				smellRange = 0;				
				break;
				
			case food:
				moveSpd = 0;
				//set image to food
				img = food;
				break;
				
			case obstacle:
				moveSpd = 0; //will not move
				//set image to obstacle
				img = obstacle;
				break;
				
			case food_source:
				moveSpd = 0;
				img = food_source;
				break;
				
			case den:
				moveSpd = 0;
				img = den;
				break;
			
			default:
				break;
				
			
		}
		
		if(dead)
			{
			 	//System.out.println(dead);	
				if(albino)
				{
					img = adeadImg;
					moveSpd = 0;
				}
				else
				{
					img = deadImg;
					moveSpd = 0;
				}
			}
	}
	
	/* no longer used
	public void toText()
	{
		System.out.println(
			"Species: \t" + species + "\n" + 
			"H Pos: \t" + hPos + "\t V Pos: \t" + vPos + "\n"
		);
	}
	*/
	
	/**
	 * return to screen the entity's properties
	 * @param world
	 * @return str the string which has the entity's details
	 */
	public String toString(AWorld world)
	{
		String str = "Species: " + species + "\t || H Pos: " + hPos + "\t || V Pos: " + vPos +
				"\t || Energy: " + energy + "\t || Unique ID: " + uniqueID + "\t || Trait: " + t + "\t || Dead: " + dead;
				
		return str;
	}
	
	/**
	 * checks if an entity already exists at the position
	 * @param x
	 * @param y
	 * @return 
	 */
	public boolean checkPos(int x, int y)
	{
		return x == hPos && y == vPos;
	}
	
	/**
	 * Draw entity image to screen
	 * @param gui
	 */
	public void displayEntity(GUI gui)
	{
		gui.showEntity(img, hPos, vPos);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		GUI.main(args);
	}

}
