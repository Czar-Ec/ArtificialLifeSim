package als;

/**
 * <h1>Obstacle</h1>
 * Obstacle is a subclass of AnEntity and handles the obstacles in the simulation.<br>
 * Edit: now also handles dens and food sources since obstacles can get a den or food source<br>
 * attribute
 * @author Czar Ian Echavez
 * @see AnEntity
 */
public class Obstacle extends AnEntity{

	/**
	 * Contructor to create new obstacles
	 * @param _hPos
	 * @param _vPos
	 * @param _uniqueID
	 * @param _world
	 */
	Obstacle(int _hPos, int _vPos, 
			int _uniqueID, AWorld _world)
	{
		setSpecies("obstacle");
		setHPos(_hPos);
		setVPos(_vPos);
		setEnergy(0);
		setUID(_uniqueID);
		setWorld(_world);
		
		//setting up base values [numeric vals]
		energyUse = 0;
		moveSpd = 0;
		moveCount = 0;
		smellRange = 0;
		
		//setting up base values [booleans]
		hunting = false;
		dead = false;
		
		//randomised booleans
		albino = false;
		poisonous = false;
		
		t = specialWall();
		
		traitSetup(t, dead);
	}
	
	/**
	 * Constructor to load obstacles from a file
	 * @param _hPos
	 * @param _vPos
	 * @param _uniqueID
	 * @param _world
	 * @param _t
	 */
	Obstacle(int _hPos, int _vPos, 
			int _uniqueID, AWorld _world, traits _t)
	{
		setSpecies("obstacle");
		setHPos(_hPos);
		setVPos(_vPos);
		setEnergy(0);
		setUID(_uniqueID);
		setWorld(_world);
		
		//setting up base values [numeric vals]
		energyUse = 0;
		moveSpd = 0;
		moveCount = 0;
		smellRange = 0;
		
		//setting up base values [booleans]
		hunting = false;
		dead = false;
		
		//randomised booleans
		albino = false;
		poisonous = false;
		
		t = _t;
		
		traitSetup(t, dead);
	}
	
}
