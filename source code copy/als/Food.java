package als;

/**
 * <h1>Food</h1>
 * 
 * Food is a subclass of AnEntity.<br>
 * This subclass handles entities that are food
 * @author Czar Ian Echavez
 * @see AnEntity
 */
public class Food extends AnEntity{
	
	/**
	 * Main constructor to create new food entities
	 * @param _hPos
	 * @param _vPos
	 * @param _uniqueID
	 * @param _world
	 */
	Food(int _hPos, int _vPos, 
			int _uniqueID, AWorld _world)
	{
		setSpecies("food");
		setHPos(_hPos);
		setVPos(_vPos);
		setEnergy(5);
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
		poisonous = isPoisonous();
		
		t = traits.food;
		
		traitSetup(t, dead);
	}
	
	/**
	 * Auxiliary constructor, used to load entities with species name food from file
	 * @param _hPos
	 * @param _vPos
	 * @param _uniqueID
	 * @param _world
	 * @param _poisonous
	 */
	Food(int _hPos, int _vPos, int _uniqueID, AWorld _world,
			boolean _poisonous) 
	{
		//setup attributes
		setSpecies("food");
		setHPos(_hPos);
		setVPos(_vPos);
		setEnergy(5);
		setUID(_uniqueID);
		setWorld(_world);
		
		//setting up base values [numeric vals]
		energyUse = 0.025;
		moveSpd = 5;
		moveCount = 0;
		smellRange = 0;
		
		//setting up base values [booleans]
		hunting = false;
		dead = false;
		
		//randomised booleans
		albino = false;
		poisonous = _poisonous;
		
		t = traits.food;
		
		traitSetup(t, dead);
	}

}
