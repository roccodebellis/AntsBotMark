import java.util.HashMap;
import java.util.Map;

public class Tile  {

	private int row;
	private int col;

	/**
	 * 
	 */
	private TileTypes type;

	private Map<Directions, Tile> adjacentTiles;

	private boolean visible;

	/**
	 * indica se la tile corrente è occupata da una formica
	 */
	private boolean occupiedByAnt;

	/**
	 * se occupied ha valore true, corrisponde all'ID della formica che occupa la tile
	 * altrimenti se false e TYPE è una HILL indica il proprietario dell'HILL
	 */
	private Integer idOwner;//vale sia per il proprietario dell'hill o il proprietario della formica stazionante

	/**
	 * se occupied ha valore true e id owner = 0
	 * available indica se è possibile assegnare un ordine ad una formica
	 */
	private boolean antIsAvailable;
	
	/**
	 * può contenere il cibo se il tile type è LAND e 
	 * non è occupato da una formica 
	 */
	private boolean containsFood;

	public Tile(int row, int col) {
		this.row = row;
		this.col = col;
		type = TileTypes.UNEXPLORED;
		adjacentTiles = new HashMap<>();
		visible = false;
		occupiedByAnt = false;
		idOwner = null;
		antIsAvailable = false;
		containsFood = false;
		
	}

	int getRow() {
		return row;
	}

	int getCol() {
		return col;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isOccupiedByAnt() {
		return occupiedByAnt;
	}

	/**
	 * prima di chiamarla chiamare isOccupiedByAnt() e getOwner() 
	 * @return
	 * @throws TileTypeException
	 */
	public boolean isIdle() throws TileTypeException {
		if(occupiedByAnt && idOwner==0)
			return antIsAvailable;
		else 
			throw new TileTypeException("Pensavi fosse un tua formica e/o pensavi fosse occupata");
	}

	public TileTypes getType() {
		return type;
	}

	public void setTypeWater() {
		type = TileTypes.WATER;
		adjacentTiles.forEach((dir,t) -> t.removeAdjacent(dir.opponent()));
		adjacentTiles = null;
		visible = true;
		occupiedByAnt = false;
		idOwner = null;
		antIsAvailable = false;
		containsFood = false;
	}

	public void setTypeHill(Integer idOwner) {
		type = TileTypes.HILL;
		//visible = true; fatto in set vision
		occupiedByAnt = false;
		idOwner = idOwner;
		antIsAvailable = false;
		containsFood = false;
	}

	public void setTypeLand() {
		type = TileTypes.LAND;
		//visible = true; fatto in set vision
		occupiedByAnt = false;
		idOwner = null;
		antIsAvailable = false;
		containsFood = false;
	}

	public void placeAnt(Integer newIdOwner) {
		if(type == TileTypes.HILL && idOwner != newIdOwner)
			type = TileTypes.LAND;
		//else è un Hill e sopra ci va una sua formica 


		//visible = true; fatto da set vision! 
		occupiedByAnt = true;
		idOwner = newIdOwner;
		antIsAvailable = true;
	}

	public void removeAnt() {
		//visible = false; fatto da clea vision! 
		occupiedByAnt = false;
		
		idOwner = null; //TODO non te ne frega nulla tanto perdi tutti gli hill
		antIsAvailable = false;
	}

	public void placeFood() {
		containsFood = true;
	}
	
	public void removeFood() {
		containsFood = false;
	}
	
	public void addAdjacent(Directions cardinal, Tile tile) {
		adjacentTiles.put(cardinal,tile);
	}

	private void removeAdjacent(Directions cardinal) {
		adjacentTiles.remove(cardinal);
	}

	public Map<Directions, Tile> getAdjacentNodes() {
		return adjacentTiles;
	}

	public int getOwner() throws TileTypeException {
		if(occupiedByAnt || type.equals(TileTypes.HILL))
			return idOwner;
		else 
			throw new TileTypeException("Pensavi fosse un HILL invece era "+ type);
	}
	
	public int getRowDelta(Tile t2) {
		return Math.abs(getRow() - t2.getRow());
	}

	public int getColDelta(Tile t2) {
		return Math.abs(getCol() - t2.getCol());
	}
	
}
