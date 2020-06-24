import java.util.HashMap;
import java.util.Map;

/**
 * <p>Rappresenta e gestisce una {@code Tile}.<br>
 * Una {@code Tile} corrisponde ad una singola area nella mappa, caratterizzata da un paio
 * di coordinate ([{@link #row riga}, {@link #col colonna}]) su cui puo' camminare - o meno -
 * una formica.</p>
 * <p></p>
 * che puo' essere di
 * diversi {@link #type tipi}, in base al corrispondente {@link TileTypes}:<ul>
 * UNEXPLORED,
 * LAND,
 * WATER,
 * HILL
 * </ul>
 * 
 *  //TODO
 *  <br></p>
 * @author Debellis, Lorusso
 *
 */
public class Tile {
	/**
	 * 
	 */
	private Integer row;

	/**
	 * 
	 */
	private Integer col;

	/**
	 * 
	 */
	private TileTypes type;

	/**
	 * 
	 */
	private Map<Directions, Tile> neighbourTiles;

	/**
	 * 
	 */
	private boolean visible;

	/**
	 * indica se la tile corrente e' occupata da una formica
	 */
	private boolean occupiedByAnt;

	/**
	 * se occupied ha valore true, corrisponde all'ID della formica che occupa la
	 * tile altrimenti se false e TYPE e' una HILL indica il proprietario dell'HILL
	 */
	private Integer idOwner;// vale sia per il proprietario dell'hill o il proprietario della formica
	// stazionante

	/**
	 * se occupied ha valore true e id owner = 0 available indica se e' possibile
	 * assegnare un ordine ad una formica
	 */
	private boolean antIsAvailable;

	/**
	 * puo' contenere il cibo se il tile type e' LAND e non e' occupato da una
	 * formica
	 */
	private boolean containsFood;

	/**
	 * 
	 * @param row
	 * @param col
	 */
	public Tile(int row, int col) {
		this.row = row;
		this.col = col;
		type = TileTypes.UNEXPLORED;
		neighbourTiles = new HashMap<>();
		visible = false;
		occupiedByAnt = false;
		idOwner = null;
		antIsAvailable = false;
		containsFood = false;
	}

	/**
	 * 
	 * @return
	 */
	int getRow() {
		return row;
	}

	/**
	 * 
	 * @return
	 */
	int getCol() {
		return col;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * 
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isOccupiedByAnt() {
		return occupiedByAnt;
	}

	/**
	 * prima di chiamarla chiamare isOccupiedByAnt() e getOwner()
	 * 
	 * @return
	 * @throws TileTypeException
	 */
	public boolean isIdle() throws TileTypeException {//TODO da gestire
		if (occupiedByAnt && idOwner == 0)
			return antIsAvailable;
		else
			throw new TileTypeException("Pensavi fosse un tua formica e/o pensavi fosse occupata");
	}

	/**
	 * 
	 * @return
	 */
	public TileTypes getType() {
		return type;
	}

	/**
	 * 
	 */
	public void setTypeWater() {
		type = TileTypes.WATER;
		neighbourTiles.forEach((dir, t) -> t.removeNeighbour(dir.opponent()));
		neighbourTiles = null;
		visible = true;
		occupiedByAnt = false;
		idOwner = null;
		antIsAvailable = false;
		containsFood = false;
	}

	/**
	 * 
	 * @param idOwner
	 */
	public void setTypeHill(Integer idOwner) {
		type = TileTypes.HILL;
		// visible = true; fatto in set vision
		occupiedByAnt = false;
		idOwner = idOwner;
		antIsAvailable = false;
		containsFood = false;
	}

	/**
	 * 
	 */
	public void setTypeLand() {
		type = TileTypes.LAND;
		// visible = true; fatto in set vision
		occupiedByAnt = false;
		idOwner = null;
		antIsAvailable = false;
		containsFood = false;
	}

	/**
	 * 
	 * @param newIdOwner
	 */
	public void placeAnt(Integer newIdOwner) {
		if (type == TileTypes.HILL && idOwner != newIdOwner)
			type = TileTypes.LAND;
		// else e' un Hill e sopra ci va una sua formica

		// visible = true; fatto da set vision!
		occupiedByAnt = true;
		idOwner = newIdOwner;
		antIsAvailable = true;
	}

	/**
	 * 
	 */
	public void removeAnt() {
		// visible = false; fatto da clea vision!
		occupiedByAnt = false;

		idOwner = null; // TODO non te ne frega nulla tanto perdi tutti gli hill
		antIsAvailable = false;
	}

	/**
	 * 
	 */
	public void placeFood() {
		containsFood = true;
	}

	/**
	 * 
	 */
	public void removeFood() {
		containsFood = false;
	}

	/**
	 * 
	 * @param cardinal
	 * @param tile
	 */
	public void addNeighbour(Directions cardinal, Tile tile) {
		neighbourTiles.put(cardinal, tile);
	}

	/**
	 * 
	 * @param cardinal
	 */
	private void removeNeighbour(Directions cardinal) {
		neighbourTiles.remove(cardinal);
	}

	/**
	 * 
	 * @return
	 */
	public Map<Directions, Tile> getNeighbour() {
		return neighbourTiles;
	}

	/**
	 * 
	 * @return
	 * @throws TileTypeException
	 */
	public int getOwner() throws TileTypeException {
		if (occupiedByAnt || type.equals(TileTypes.HILL))
			return idOwner;
		else
			throw new TileTypeException("Pensavi fosse un HILL invece era " + type);
	}

	/**
	 * 
	 * @param t2
	 * @return
	 */
	public int getRowDelta(Tile t2) {
		return Math.abs(getRow() - t2.getRow());
	}

	/**
	 * 
	 * @param t2
	 * @return
	 */
	public int getColDelta(Tile t2) {
		return Math.abs(getCol() - t2.getCol());
	}



	public boolean isSuitable() {
		return (occupiedByAnt || (type.equals(TileTypes.HILL) && idOwner==0 )) ? false : true;
		//FIXME se è una formica nemica ????????
		//viene utilizzata da BFS
	}
	
	@Override
	public int hashCode(){
		return row * 50000 + col ;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tile other = (Tile) obj;
		if (col == null) {
			if (other.col != null)
				return false;
		} else if (!col.equals(other.col))
			return false;
		if (row == null) {
			if (other.row != null)
				return false;
		} else if (!row.equals(other.row))
			return false;
		return true;
	}

}
