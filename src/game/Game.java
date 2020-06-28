package game;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import timing.Timing;
import vision.Offset;
import vision.Offsets;
import vision.Vision;

/**
 * TODO EHHH IL MONDO
 * 
 * @author Debellis, Lorusso
 *
 */
public class Game {

	/**
	 * Numero di righe della mappa del gioco.
	 */
	private static int rows;

	/**
	 * Numero di colonne della mappa del gioco.
	 */
	private static int cols;

	/**
	 * Numero di turni totali del gioco.
	 */
	private static int turns;// TODO non usata!

	/**
	 * Raggio di visione delle formiche al quadrato.
	 */
	private static int viewRadius2;

	/**
	 * Raggio di attacco delle formiche al quadrato.
	 */
	private static int attackRadius2;

	/**
	 * Raggio di visione delle formiche per la raccolta del cibo al quadrato. 
	 */
	private static int spawnRadius2;

	/**
	 * TODO vd 405 416
	 * {@link Offsets} 
	 * 
	 */
	private final Offsets visionOffsets;

	/**
	 * Insieme contenente le {@link Tile tile} su cui sono posizionati i
	 * {@code formicai} dell'agente.
	 */
	private static Set<Tile> myHills;

	/**
	 * Insieme contenente le {@link Tile tile} su cui sono posizionati i
	 * {@code formicai} dei nemici.
	 */
	private static Set<Tile> enemyHills;

	/**
	 * Insieme contentente le {@link Tile tile} su cui sono posizionate
	 * le formiche dell'agente nel turno corrente.
	 */
	private static Set<Tile> myAnts;

	/**
	 * Insieme contentente le {@link Tile tile} su cui sono posizionate
	 * le formiche nemiche viste dalle {@link #myAnts formiche dell'agente} nel turno corrente.
	 */
	private static Set<Tile> enemyAnts;
	
	/**
	 * Formiche a cui e' stato assegnato un ordine.
	 */
	private static Set<Tile> orderlyAnts;

	/**
	 * Insieme contentente le {@link Tile tile} su cui e' posizionato il cibo nel turno corrente.
	 */
	private static Set<Tile> food;

	/**
	 * Insieme di {@link Orders ordini} assegnati ad una o piu' {@link #myAnts formiche} dell'agente.
	 */
	private static Set<Order> orders;

	/**
	 * Insieme contentente le {@link Tile tile} inesplorate.
	 */
	private static Set<Tile> unexplored;
	
	/**
	 * 
	 */
	private static Set<Tile> water;

	/**
	 * Tile che sono al di fuori della vista delle formiche.
	 */
	private static Set<Tile> outOfSight;

	/**
	 * Mappa del gioco.
	 */
	private static List<List<Tile>> map;
	
	/**
	 * 
	 */
	//private static Set<Tile> borders;

	/**
	 * Returns all orders sent so far.
	 * 
	 * @return all orders sent so far
	 */
	private Set<Order> getOrders() {
		return orders;
	}

	/**
	 * Creates new {@link Ants} object.
	 * 
	 * @param loadTime      timeout for initializing and setting up the bot on turn
	 *                      0
	 * @param turnTime      timeout for a single game turn, starting with turn 1
	 * @param rows          game map height
	 * @param cols          game map width
	 * @param turns         maximum number of turns the game will be played
	 * @param viewRadius2   squared view radius of each ant
	 * @param attackRadius2 squared attack radius of each ant
	 * @param spawnRadius2  squared spawn radius of each ant
	 */
	public Game(long loadTime, long turnTime, int rows, int cols, int turns, int viewRadius2, int attackRadius2,
			int spawnRadius2) {
		Timing.setLoadTime(loadTime);//
		Timing.setTurnTime(turnTime);
		setRows(rows);
		setCols(cols);
		Timing.setMaxTurns(turns);
		this.viewRadius2 = viewRadius2;
		this.attackRadius2 = attackRadius2;
		this.spawnRadius2 = spawnRadius2;

		myHills = new TreeSet<Tile>();
		enemyHills = new TreeSet<Tile>();
		myAnts = new TreeSet<Tile>();
		enemyAnts = new TreeSet<Tile>();
		orderlyAnts = new TreeSet<Tile>();
		food = new TreeSet<Tile>();
		orders = new TreeSet<Order>();
		unexplored = new TreeSet<Tile>();
		water = new TreeSet<Tile>();
		outOfSight = new TreeSet<Tile>();
		//borders = new TreeSet<Tile>();
		map = initGameMap();
		visionOffsets = new Offsets((int) Math.sqrt(viewRadius2));// FIXME passare slo viewRadius2
	}

	private static void setRows(int rows) {
		Game.rows = rows;
	}

	private static void setCols(int cols) {
		Game.cols = cols;
	}

	public static void setOutOfSight(Set<Tile> outOfSight) {
		Game.outOfSight = outOfSight;
	}
	
	/**
	 * inizializza l'intera mappa assegnando ad ogni tile la lista dei tile vicini
	 * viene assegnato ad ogni tile il valore land
	 */
	private List<List<Tile>> initGameMap() {
		List<List<Tile>> output = new ArrayList<>();

		for (int r = 0; r < rows; r++) {
			ArrayList<Tile> tempRow = new ArrayList<>();
			for (int c = 0; c < cols; c++) 
				tempRow.add(new Tile(r, c));
				//if(r == 0 || r == rows-1 || c == 0 || c == cols-1)
					//borders.add(t);
			output.add(tempRow);
			unexplored.addAll(tempRow);
		}

		// ADDED "INTERNAL" VICINI

		Iterator<List<Tile>> rows_It = output.iterator();

		List<Tile> UP_Row = rows_It.next();

		Iterator<Tile> UP_cols_It = UP_Row.iterator();
		Tile prevTile = UP_cols_It.next();

		Tile firstColTile = prevTile;

		while (UP_cols_It.hasNext()) {
			Tile curTile = UP_cols_It.next();
			prevTile.addNeighbour(Directions.EAST, curTile);
			curTile.addNeighbour(Directions.WEST, prevTile);
			prevTile = curTile;
		}

		prevTile.addNeighbour(Directions.EAST, firstColTile);
		firstColTile.addNeighbour(Directions.WEST, prevTile);

		while (rows_It.hasNext()) {
			UP_cols_It = UP_Row.iterator();

			List<Tile> DOWN_Row = rows_It.next();

			Iterator<Tile> DOWN_cols_It = DOWN_Row.iterator();

			Tile UP_prev_Tile = UP_cols_It.next();
			Tile DOWN_prev_Tile = DOWN_cols_It.next();

			firstColTile = DOWN_prev_Tile;

			DOWN_prev_Tile.addNeighbour(Directions.NORTH, UP_prev_Tile);
			UP_prev_Tile.addNeighbour(Directions.SOUTH, DOWN_prev_Tile);

			while (DOWN_cols_It.hasNext() && UP_cols_It.hasNext()) {

				Tile DOWN_cur_Tile = DOWN_cols_It.next();
				Tile UP_cur_Tile = UP_cols_It.next();

				DOWN_cur_Tile.addNeighbour(Directions.NORTH, UP_cur_Tile);
				UP_cur_Tile.addNeighbour(Directions.SOUTH, DOWN_cur_Tile);

				DOWN_prev_Tile.addNeighbour(Directions.EAST, DOWN_cur_Tile);
				DOWN_cur_Tile.addNeighbour(Directions.WEST, DOWN_prev_Tile);

				DOWN_prev_Tile = DOWN_cur_Tile;
				UP_prev_Tile = UP_cur_Tile;
			}

			DOWN_prev_Tile.addNeighbour(Directions.EAST, firstColTile);
			firstColTile.addNeighbour(Directions.WEST, DOWN_prev_Tile);

		}

		// ADD "EXTERNAL" NEIGHBOUR
		// Iterator<Tile> firstRowIt = output.getFirst().iterator();
		// Iterator<Tile> lastRowIt = output.getLast().iterator();
		Iterator<Tile> firstRowIt = output.get(0).iterator();
		Iterator<Tile> lastRowIt = output.get(rows).iterator();

		while (firstRowIt.hasNext() && lastRowIt.hasNext()) {
			Tile DOWN = lastRowIt.next();
			Tile UP = firstRowIt.next();

			UP.addNeighbour(Directions.NORTH, DOWN);
			DOWN.addNeighbour(Directions.SOUTH, UP);
		}

		return output;
	}
	
	public static List<List<Tile>> getMap(){
		return map;
	}

	private static Tile getTile(int row, int col) {
		return map.get(row).get(col);
	}
	
	public static int getRows() {
		return rows;
	}

	public static int getCols() {
		return cols;
	}

	public static Set<Tile> getMyAnts() {
		return myAnts;
	}

	public static Set<Tile> getEnemyAnts() {
		return enemyAnts;
	}

	public static Set<Tile> getEnemyHills() {
		return enemyHills;
	}

	public static Set<Tile> getMyHills() {
		return myHills;
	}

	public static Set<Tile> getUnexplored() {
		return unexplored;
	}
	
	/*public static Set<Tile> getBorders(){
		return borders;
	}*/
	
	public static Set<Tile> getFoodTiles(){
		return food;
	}
	
	public static int getAttackRadius() {
		return attackRadius2;
	}

	public static int getViewRadius() {
		return viewRadius2;
	}
	
	public static Set<Tile> getWater() {
		return water;
	}

	static int getSpawnRadius() {
		return spawnRadius2;
	}
	
	public static Set<Tile> getOutOfSight() {
		return outOfSight;
	}


	public void clear() {
		clearMyAnts();
		clearEnemyAnts();
		clearMyHills();
		clearEnemyHills();
		clearFood();
		Vision.clearAllVision();//TODO
		orders.clear();
	}

	/**
	 * Clears game state information about my ants locations.
	 */
	private void clearMyAnts() {
		myAnts.parallelStream().forEachOrdered(ant -> ant.removeAnt());
		myAnts.clear();
	}

	/**
	 * Clears game state information about enemy ants locations.
	 */
	private void clearEnemyAnts() {
		enemyAnts.parallelStream().forEachOrdered(ant -> ant.removeAnt());
		enemyAnts.clear();
	}

	/**
	 * Clears game state information about my hills locations.
	 */
	private void clearMyHills() {
		myHills.parallelStream().forEachOrdered(hill -> hill.setTypeLand());
		myHills.clear();
	}

	/**
	 * Clears game state information about enemy hills locations.
	 */
	private void clearEnemyHills() {
		enemyHills.parallelStream().forEachOrdered(hill -> hill.setTypeLand());
		enemyHills.clear();
	}

	/**
	 * Clears game state information about food locations.
	 */
	private void clearFood() {
		food.parallelStream().forEachOrdered(food -> food.removeFood());
		food.clear();
	}

	/**
	 * Clears visible information
	 * 
	 * public void clearVision() { map.parallelStream().forEachOrdered(row ->
	 * row.parallelStream().forEachOrdered(tile -> tile.setVisible(false)));
	 * 
	 * }
	 */

	public void setWater(int row, int col) {
		Tile curTile = getTile(row, col);
		curTile.setTypeWater();

		water.add(curTile);
		unexplored.remove(curTile);
	}

	public void setAnt(int row, int col, int owner) {
		Tile curTile = getTile(row, col);
		curTile.setTypeLand();
		curTile.placeAnt(owner);

		unexplored.remove(curTile);

		if (owner == 0) {
			myAnts.add(curTile);
			// setVision(curTile); richiamato da Bot.afterUpdate() con setVision()
		} else
			enemyAnts.add(curTile);
	}

	/**
	 * Updates game state information about food locations.
	 * 
	 * @param row
	 * @param col
	 */
	public void setFood(int row, int col) {
		Tile curTile = getTile(row, col);
		curTile.setTypeLand();
		curTile.placeFood();
		food.add(curTile);
		unexplored.remove(curTile);
	}

	public void setDead(int row, int col, int owner) {
		Tile curTile = getTile(row, col);
		curTile.removeAnt();

		if (owner == 0)
			myAnts.remove(curTile);
		else
			enemyAnts.remove(curTile);
	}

	public void setHills(int row, int col, int owner) {
		Tile curTile = getTile(row, col);
		curTile.setTypeHill(owner);

		if (owner == 0)
			myHills.add(curTile);
		else
			enemyHills.add(curTile);
			
		unexplored.remove(curTile);
	}

	/**
	 * Returns location with the specified offset from the specified location.
	 * 
	 * @param tile   location on the game map
	 * @param offset offset to look up
	 * 
	 * @return location with <code>offset</code> from <cod>tile</code>
	 */
	public static Tile getTile(Tile tile, Offset offset) {
		int row = (tile.getRow() + offset.getRow()) % rows;
		if (row < 0) {
			row += rows;
		}
		int col = (tile.getCol() + offset.getCol()) % cols;
		if (col < 0) {
			col += cols;
		}
		return getTile(row, col);
	}

	public static Set<Tile> getTiles(Tile tile, Offsets offsets) {
		Set<Tile> inVisionOfThisTile = new TreeSet<Tile>();
		offsets.parallelStream().forEachOrdered(offset -> inVisionOfThisTile.add(getTile(tile, offset)));
		return inVisionOfThisTile;
	}

	/**
	 * EQUALS TO STATIC SEARCH Calculates visible information
	 */
	public void setVision() {
		Vision.setVision();
	}

	/**
	 * Issues an order by sending it to the system output.
	 * 
	 * @param myAnt     map tile with my ant
	 * @param direction direction in which to move my ant
	 */
	static public void issueOrder(Order order) {
		Tile ant = order.getTile();
		myAnts.remove(ant);
		orderlyAnts.add(ant);
		
		orders.add(order);
		// System.out.println(order); FIXME
	}
	
	/**
	 * Per ogni @link Order ordine} assegnato alle formiche
	 * viene mandato l'ordine al System Output tramite {@link #issueOrder(Order)}.
	 * @param orders insieme di ordini da eseguire
	 */
	static public void issueOrders(Set<Order> orders) {
		orders.parallelStream().forEachOrdered(order -> issueOrder(order));
	}

	/**
	 * <p>
	 * Permette di calcolare la distanza tra due {@link Tile tile} per mezzo di una
	 * Euristica che non sovrastima la loro reale distanza.
	 * </p>
	 * <p>
	 * L'Euristica utilizzata e' una sorta di distanza di Manhattan modificata.
	 * </p>
	 * 
	 * @param t1 {@link Tile tile} di cui calcolare la distanza da {@code t2}
	 * @param t2 {@link Tile tile} di cui calcolare la distanza da {@code t1}
	 * @return distanza tra {@code t1} e {@code t2}
	 */
	public static int getDistance(Tile t1, Tile t2) {
		return t1.getDeltaRow(t2) % rows + t1.getDeltaCol(t2) % cols;
	}

	public static Directions getDirection(Tile t1, Tile t2) {
		Directions output = Directions.STAYSTILL;
		
		int difRow = t1.getRow() - t2.getRow();
		int difCol = t1.getCol() - t2.getCol();
		int deltaRow = (difRow<0) ? Math.abs(difRow)-rows : difRow;
		int deltaCol = (difCol<0) ? Math.abs(difCol)-cols : difCol;

		if(deltaRow>0 && deltaCol<=deltaRow && (-deltaCol> deltaRow || deltaCol > -deltaRow))
			output = Directions.NORTH;
		else if(deltaRow<0 && deltaCol<=-deltaRow && (-deltaCol> deltaRow || deltaCol > -deltaRow))
			output = Directions.SOUTH;
		else if(deltaCol>0)
			output = Directions.EAST;
		else
			output = Directions.WEST;
		return output;
	}

	public static Set<Tile> getOrderlyAnts() {
		return orderlyAnts;
	}

	public void clearAntsVision() {
		Vision.clearAntsVision();
		
	}
}
