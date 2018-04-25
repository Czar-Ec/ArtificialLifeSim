package als;

/**
 * <h1>Lifeform</h1>
 * Lifeform is a subclass of AnEntity and handles the lifeforms in the simulation
 * @author Czar Ian Echavez
 * @see AnEntity
 *
 */
public class Lifeform extends AnEntity {

	/**
	 * constructor to make new lifeforms
	 * @param _species
	 * @param _hPos
	 * @param _vPos
	 * @param _energy
	 * @param _uniqueID
	 * @param _world
	 */
	Lifeform(String _species, int _hPos, int _vPos, double _energy, int _uniqueID, AWorld _world) 
	{
		//setup attributes
		setSpecies(_species);
		setHPos(_hPos);
		setVPos(_vPos);
		setEnergy(_energy);
		setUID(_uniqueID);
		setWorld(_world);
		
		//setting up base values [numeric vals]
		energyUse = 0.0025;
		moveSpd = 2;
		moveCount = 0;
		smellRange = 150;
		
		//setting up base values [booleans]
		hunting = false;
		dead = false;
		
		//randomised booleans
		albino = isAlbino();
		poisonous = false;
		
		t = traits.getTrait();
		d = Direction.temp;
		
		traitSetup(t, dead);
	}
	
	/**
	 * constructor to LOAD lifeforms from saves
	 * @param _species
	 * @param _hPos
	 * @param _vPos
	 * @param _energy
	 * @param _uniqueID
	 * @param _world
	 * @param _dead
	 * @param _albino
	 * @param _t
	 */
	Lifeform(String _species, int _hPos, int _vPos, double _energy, int _uniqueID, AWorld _world, 
			boolean _dead ,boolean _albino, traits _t) 
	{
		//setup attributes
		setSpecies(_species);
		setHPos(_hPos);
		setVPos(_vPos);
		setEnergy(_energy);
		setUID(_uniqueID);
		setWorld(_world);
		
		//setting up base values [numeric vals]
		energyUse = 0.0025;
		moveSpd = 2;
		moveCount = 0;
		smellRange = 150;
		
		//setting up base values [booleans]
		hunting = false;
		dead = _dead;
		
		//randomised booleans
		albino = _albino;
		poisonous = false;
		
		t = _t;
		//just temp direction
		d = Direction.temp;
		
		traitSetup(t, dead);
	}

}
