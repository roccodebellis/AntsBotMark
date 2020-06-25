package game;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import timing.Timing;
import vision.Offset;
import vision.Offsets;

public class Game {

	private static int rows;

	private static int cols;

	private final int turns;

	private final int viewRadius2;

	private final static int attackRadius2;

	private final int spawnRadius2;

	private final Offsets visionOffsets;

	private static Set<Tile> myHills;

	private static Set<Tile> enemyHills;

	private static Set<Tile> myAnts; 

	private static Set<Tile> enemyAnts;

	private Set<Tile> foods;

	private Set<Order> orders;

	private Set<Tile> unexplored;

	private static List<List<Tile>> map;

	/**
	 * Returns all orders sent so far.
	 * 
	 * @return all orders sent so far
	 */
	public Set<Order> getOrders() {
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
	public Game(long loadTime, long turnTime, int rows, int cols, int turns, int viewRadius2, int attackRadius2, int spawnRadius2) {
		Timing.setLoadTime(loadTime);//
		Timing.setTurnTime(turnTime);
		setRows(rows);
		setCols(cols);
		Timing.setMaxTurns(turns);
		this.viewRadius2 = viewRadius2;
		Game.attackRadius2 = attackRadius2;
		this.spawnRadius2 = spawnRadius2;

		myHills = new TreeSet<Tile>();
		enemyHills = new TreeSet<Tile>();
		myAnts = new TreeSet<Tile>();
		enemyAnts = new TreeSet<Tile>();
		foods = new TreeSet<Tile>();
		orders = new TreeSet<Order>();
		unexplored = new TreeSet<Tile>();

		map = initGameMap();

		visionOffsets = new Offsets((int) Math.sqrt(viewRadius2));// TODO passare slo viewRadius2
	}

	private static void setRows(int rows) {
		Game.rows = rows;
	}

	private static void setCols(int cols) {
		Game.cols = cols;
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

	private static Tile getTile(int row, int col) {
		return map.get(row).get(col);
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
	
	
	public static int getAttackRadius() {
		return attackRadius2;
	}

	public void clear() {
		clearMyAnts();
		clearEnemyAnts();
		clearMyHills();
		clearEnemyHills();
		clearFood();
		//clearDeadAnts(); //???
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
		foods.parallelStream().forEachOrdered(food -> food.removeFood());
		foods.clear();
	}

	/**
	 * Clears visible information

	public void clearVision() {
		map.parallelStream().forEachOrdered(row -> row.parallelStream().forEachOrdered(tile -> tile.setVisible(false)));

	}*/

	public void setWater(int row, int col) {
		Tile curTile = getTile(row, col);
		curTile.setTypeWater();

		unexplored.remove(curTile);
	}

	public void setAnt(int row, int col, int owner) {
		Tile curTile = getTile(row, col);
		curTile.setTypeLand();
		curTile.placeAnt(owner);

		unexplored.remove(curTile);

		if(owner == 0) {
			myAnts.add(curTile);
			//setVision(curTile); richiamato da Bot.afterUpdate() con setVision()
		} else
			enemyAnts.add(curTile);
	}

	/**
	 *  Updates game state information about food locations.
	 * @param row
	 * @param col
	 */
	public void setFood(int row, int col) {
		Tile curTile = getTile(row, col);
		curTile.setTypeLand();
		curTile.placeFood();

		foods.add(curTile);
	}

	public void setDead(int row, int col, int owner) {
		Tile curTile = getTile(row, col);
		curTile.removeAnt();

		if(owner == 0)
			myAnts.remove(curTile);
		else
			enemyAnts.remove(curTile);
	}

	public void setHills(int row, int col, int owner) {
		Tile curTile = getTile(row, col);
		curTile.setTypeHill(owner);

		if (owner == 0)
			myHills.add(curTile);
		else {
			enemyHills.add(curTile);
		}
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

	private Set<Tile> getTiles(Tile tile, Offsets offsets) {
		Set<Tile> inVisionOfThisTile = new TreeSet<Tile>();
		offsets.parallelStream().forEachOrdered(offset -> inVisionOfThisTile.add(getTile(tile, offset)));
		return inVisionOfThisTile;
	}

	/**
	 * EQUALS TO STATIC SEARCH
	 * Calculates visible information
	 */
	public void setVision(boolean visibile) {
		Set<Tile> inVision = new TreeSet<Tile>();
		myAnts.parallelStream().forEachOrdered(ant -> inVision.addAll(getTiles(ant,visionOffsets)));
		inVision.forEach(tile -> tile.setVisible(visibile));
	}
	
	/**
	 * Issues an order by sending it to the system output.
	 * 
	 * @param myAnt     map tile with my ant
	 * @param direction direction in which to move my ant
	 */
	public void issueOrder(Tile myAnt, Directions direction) {
		Order order = new Order(myAnt, direction);
		orders.add(order);
		//System.out.println(order); TODO
	}
	
	public static int getDistance(Tile t1, Tile t2) {
		return t1.getRowDelta(t2)%rows + t1.getColDelta(t2)%cols;
	}

	

}
