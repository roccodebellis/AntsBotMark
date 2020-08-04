package game;

import java.util.Iterator;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import attackdefencehills.AttackDefenceHills;
//import attackdefensehills.AttackDefenseHills;
import combat.CombatSimulation;
import defaultpackage.Configuration;
import exploration.ExplorationAndMovement;
import gathering.FoodCollection;
import search.Node;
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
	 * TODO vd 405 416 {@link Offsets}
	 * 
	 */
	private static Vision view;

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
	 * Insieme contentente le {@link Tile tile} su cui sono posizionate le formiche
	 * dell'agente nel turno corrente.
	 */
	private static Set<Tile> myAnts;

	/**
	 * Insieme contentente le {@link Tile tile} su cui sono posizionate le formiche
	 * nemiche viste dalle {@link #myAnts formiche dell'agente} nel turno corrente.
	 */
	private static Set<Tile> enemyAnts;

	/**
	 * Formiche a cui e' stato assegnato un ordine.
	 */
	private static Set<Tile> orderlyAnts;

	/**
	 * Insieme contentente le {@link Tile tile} su cui e' posizionato il cibo nel
	 * turno corrente.
	 */
	private static Set<Tile> food;

	/**
	 * Insieme di {@link Orders ordini} assegnati ad una o piu' {@link #myAnts
	 * formiche} dell'agente.
	 */
	private static Set<Order> orders;

	/**
	 * 
	 */
	private static Set<Tile> ordersTarget;

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
	// private static Set<Tile> outOfSight;

	/**
	 * Mappa del gioco.
	 */
	private static List<List<Tile>> map;

	private static Set<Tile> setTiles;

	/**
	 * 
	 */
	// private static Set<Tile> borders;

	/**
	 * Returns all orders sent so far.
	 * 
	 * @return all orders sent so far
	 */
	private Set<Order> getOrders() {
		return orders;
	}

	public static Set<Tile> getOrdersTarget() {
		return ordersTarget;
	}

	private static int numberEnemy;

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
	public Game(int rows, int cols, int viewRadius2, int attackRadius2, int spawnRadius2) {

		setRows(rows);
		setCols(cols);

		Game.numberEnemy = 1;
		Game.viewRadius2 = viewRadius2;
		Game.attackRadius2 = attackRadius2;
		Game.spawnRadius2 = spawnRadius2;

		myHills = new HashSet<Tile>();
		enemyHills = new HashSet<Tile>();
		myAnts = new HashSet<Tile>();
		enemyAnts = new HashSet<Tile>();
		orderlyAnts = new TreeSet<Tile>();
		food = new HashSet<Tile>();
		orders = new HashSet<Order>();
		ordersTarget = new TreeSet<Tile>();
		unexplored = new TreeSet<Tile>();
		water = new HashSet<Tile>();
		// outOfSight = new TreeSet<Tile>(Tile.visionComparator());//TODO check if
		// comparator
		// borders = new TreeSet<Tile>();
		setTiles = new TreeSet<Tile>();
		map = initGameMap();

		view = new Vision(setTiles, getViewRadius2());
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
			for (int c = 0; c < cols; c++) {
				Tile t = new Tile(r,c);
				t.setSuitable(true);
				tempRow.add(t);
			}
			// if(r == 0 || r == rows-1 || c == 0 || c == cols-1)
			// borders.add(t);
			output.add(tempRow);
			// setTiles.addAll(tempRow.parallelStream().collect(Collectors.toCollection(()->new
			// TreeSet<Tile>(Tile.tileComparator()))));
			setTiles.addAll(tempRow);
			// unexplored.addAll(tempRow.parallelStream().collect(
			// Collectors.toCollection(() -> new TreeSet<Tile>(Tile.visionComparator()))));
			unexplored.addAll(tempRow);
		}

		// ADDED "INTERNAL" VICINI

		Iterator<List<Tile>> rows_It = output.iterator();

		List<Tile> UP_Row = rows_It.next();

		List<Tile> First_Row = UP_Row;

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

		List<Tile> DOWN_Row = null;

		while (rows_It.hasNext()) {
			UP_cols_It = UP_Row.iterator();

			DOWN_Row = rows_It.next();

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

			UP_Row = DOWN_Row;
		}

		Iterator<Tile> DOWN_cols_It = DOWN_Row.iterator();
		UP_cols_It = First_Row.iterator();

		while (DOWN_cols_It.hasNext() && UP_cols_It.hasNext()) {
			Tile DOWN_cur_Tile = DOWN_cols_It.next();
			Tile UP_cur_Tile = UP_cols_It.next();

			DOWN_cur_Tile.addNeighbour(Directions.SOUTH, UP_cur_Tile);
			UP_cur_Tile.addNeighbour(Directions.NORTH, DOWN_cur_Tile);
		}

		/*
		 * // ADD "EXTERNAL" NEIGHBOUR // Iterator<Tile> firstRowIt =
		 * output.getFirst().iterator(); // Iterator<Tile> lastRowIt =
		 * output.getLast().iterator(); Iterator<Tile> firstRowIt =
		 * output.get(0).iterator(); Iterator<Tile> lastRowIt =
		 * output.get(rows-1).iterator();
		 * 
		 * while (firstRowIt.hasNext() && lastRowIt.hasNext()) { Tile DOWN =
		 * lastRowIt.next(); Tile UP = firstRowIt.next();
		 * 
		 * UP.addNeighbour(Directions.NORTH, DOWN); DOWN.addNeighbour(Directions.SOUTH,
		 * UP); }
		 * 
		 */

		// setTiles.forEach(s -> System.out.println(s+" -> "+s.getNeighbour()));
		// output.forEach(l -> l.forEach(s -> System.out.println(s+" ->
		// "+s.getNeighbour())));
		return output;
	}

	public static List<List<Tile>> getMap() {
		return map;
	}

	public static Tile getTile(int row, int col) {
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

	/*
	 * public static Set<Tile> getBorders(){ return borders; }
	 */

	public static Set<Tile> getFoodTiles() {
		return food;
	}

	public static int getAttackRadius2() {
		return attackRadius2;
	}

	public static int getViewRadius2() {
		return viewRadius2;
	}

	public static Set<Tile> getWater() {
		return water;
	}

	static int getSpawnRadius() {
		return spawnRadius2;
	}

	public static Set<Tile> getOutOfSight() {
		return view.getOutOfSight();
	}

	public static int getNumberEnemy() {
		return numberEnemy;
	}

	public void clear() {
		clearMyAnts();
		clearEnemyAnts();
		clearMyHills();
		clearEnemyHills();
		clearFood();
		view.clearAllVision();// TODO
		orders.clear();
		ordersTarget.clear();
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

	public static void setVisible(Tile tile, boolean visible) {
		if (visible)
			unexplored.remove(tile);
		tile.setVisible(visible);
	}

	public void setWater(int row, int col) {
		Tile curTile = getTile(row, col);
		curTile.setTypeWater();

		water.add(curTile);

	}

	public void setAnt(int row, int col, int owner) {
		Tile curTile = getTile(row, col);

		curTile.placeAnt(owner);

		if (owner == 0) {
			myAnts.add(curTile);
			// setVision(curTile); richiamato da Bot.afterUpdate() con setVision()
		} else {
			enemyAnts.add(curTile);
			if (owner > numberEnemy)
				Game.numberEnemy = owner;
		}

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

		if (owner > numberEnemy)
			Game.numberEnemy = owner;

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

	/**
	 * NON RESTITUISCE LE TILE DI ACQUA
	 * 
	 * @param tile
	 * @param offsets
	 * @return
	 */
	public static Set<Tile> getTiles(Tile tile, Offsets offsets) {
		return offsets.parallelStream().map(offset -> getTile(tile, offset)).filter(t -> t.isAccessible())
				.collect(Collectors.toCollection(HashSet<Tile>::new));
	}

	private void setHillsToDefend() {
		if (Timing.getTurnNumber() == 1) {
			// aggiungere hill da difendere
			getMyHills().parallelStream().forEachOrdered(hill -> view.addHillToDefend(hill));
		} else {
			// rimuovi hills distrutti
			Set<Tile> hillsDown = new TreeSet<Tile>(view.getHillsToDefend());
			Set<Tile> myHillsTemp = new TreeSet<Tile>(getMyHills());
			if (hillsDown.size() > myHillsTemp.size()) {
				hillsDown.removeAll(myHillsTemp);
				hillsDown.parallelStream().forEachOrdered(hill -> view.removeHillToDefend(hill));
			} else if (hillsDown.size() < getMyHills().size()) {
				myHillsTemp.removeAll(hillsDown);
				myHillsTemp.parallelStream().forEachOrdered(hill -> view.addHillToDefend(hill));
			}
		}
	}

	private void generateCrazyNeighbours() {
		myAnts.parallelStream().forEachOrdered(ant -> {
			Map<Directions, Tile> reorderedNeigh = new HashMap<Directions, Tile>();
			ArrayList<Tile> neighAsList = new ArrayList<Tile>(ant.getNeighbour().values());
			Map<Tile, Directions> tempNeigh = new HashMap<Tile, Directions>();
			ant.getNeighbour().entrySet().parallelStream().forEachOrdered(e -> tempNeigh.put(e.getValue(), e.getKey()));
			while (neighAsList.size() > 0) {
				/*Random r = new Random(Timing.getCurTime());
				int val = (int) (r.nextInt() * (neighbour.size()));*/
				ThreadLocalRandom current = ThreadLocalRandom.current();
				//int val = (int) Math.random() * (neighbour.size());
				int val = current.nextInt((neighAsList.size()));
				reorderedNeigh.put(tempNeigh.get(neighAsList.get(val)), neighAsList.remove(val));
			}
			ant.crazyNeighbour(reorderedNeigh);
		});
	}

	/**
	 * EQUALS TO STATIC SEARCH Calculates visible information
	 */
	public void doVision() {

		view.setVision(myAnts);

		setHillsToDefend();

		generateCrazyNeighbours();

	}

	/**
	 * Issues an order by sending it to the system output.
	 * 
	 * @param myAnt     map tile with my ant
	 * @param direction direction in which to move my ant
	 */
	static public void issueOrder(Order order) {
		Tile o_ant = order.getOrigin();
		Directions o_dir = order.getDirection();

		Tile dest = o_ant.getNeighbour().get(o_dir);

		if (!ordersTarget.contains(dest)) {
			ordersTarget.add(dest);
			myAnts.remove(o_ant);
			orderlyAnts.add(o_ant);

			orders.add(order);
			if (!order.getDirection().equals(Directions.STAYSTILL))
				System.out.println(order);
		}
	}

	/**
	 * Per ogni @link Order ordine} assegnato alle formiche viene mandato l'ordine
	 * al System Output tramite {@link #issueOrder(Order)}.
	 * 
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
		int deltaRow = (difRow < 0) ? Math.abs(difRow) - rows : difRow;
		int deltaCol = (difCol < 0) ? Math.abs(difCol) - cols : difCol;

		if (deltaRow > 0 && deltaCol <= deltaRow && (-deltaCol > deltaRow || deltaCol > -deltaRow))
			output = Directions.NORTH;
		else if (deltaRow < 0 && deltaCol <= -deltaRow && (-deltaCol > deltaRow || deltaCol > -deltaRow))
			output = Directions.SOUTH;
		else if (deltaCol > 0)
			output = Directions.EAST;
		else
			output = Directions.WEST;
		return output;
	}

	public static Set<Tile> getOrderlyAnts() {
		return orderlyAnts;
	}

	public static Set<Tile> getMapTiles() {
		return setTiles;
	}

	public static Map<Node, Tile> getEnemyToAnt() {
		return view.getEnemyToAnt();
	}

	/**
	 * Considero le situazioni di battaglia in corso se ce ne e' almeno una in corso
	 * combatti
	 * 
	 */
	public void doCombat() {

		Map<Tile, Tile> ongoingBattlesSituation = getOngoingBattlesSituation();

		// System.out.println("battlesLeading: "+ongoingBattles);

		if (ongoingBattlesSituation.size() != 0) {
			/*
			 * try { if(ongoingBattlesSituation.size()>2) throw new NullPointerException();
			 */
			fight(ongoingBattlesSituation);
			/*
			 * }catch(NullPointerException e) { throw new
			 * NullPointerException("I'm in! - > " + ongoingBattlesSituation); }
			 */

		}
	}

	/**
	 * itero su tutti i nemici che sono in visione di una mia formica, prendo una
	 * coppia nemico-formica per volta se la mia formica non e' gia' coinvolta in
	 * un'altra battaglia, inserisco la battaglia corrente tra quelle in corso
	 * 
	 * @return
	 */
	private Map<Tile, Tile> getOngoingBattlesSituation() {

		Map<Node, Tile> enemyToAnt = getEnemyToAnt();
		Iterator<Entry<Node, Tile>> inCombatSituationItr = enemyToAnt.entrySet().iterator();

		Set<Tile> myAntsInCombatSituation = new TreeSet<Tile>();
		Map<Tile, Tile> ongoingBattles = new TreeMap<Tile, Tile>();

		while (inCombatSituationItr.hasNext()) {
			Entry<Node, Tile> currPairOfOpponents = inCombatSituationItr.next();
			Tile enemyAnt = currPairOfOpponents.getKey().getTile();
			Tile myAnt = currPairOfOpponents.getValue();

			// if(enemyTileAsNode.getHeuristicValue()) FIXME magari diminuire e non
			// utilizzare il raggio di visione, utilizzando la distanza
			if (!myAntsInCombatSituation.add(myAnt)) {
				Iterator<Tile> setEnemyIt = ongoingBattles.keySet().iterator();
				boolean notAddThisAnt = false;
				while (!notAddThisAnt && setEnemyIt.hasNext()) {
					Tile eA = setEnemyIt.next();
					if (Game.getDistance(myAnt, eA) < Configuration.getCombatModuleSearchRadius())
						notAddThisAnt = true;
				}
				if (notAddThisAnt)
					ongoingBattles.put(myAnt, enemyAnt);
			}

		}

		// myAntsInCombatSituation.parallelStream().forEachOrdered(ant ->
		// Game.myAnts.remove(ant));

		/*
		 * try { if(ongoingBattles.size()>0) throw new NullPointerException();
		 */
		return ongoingBattles;
		/*
		 * }catch(NullPointerException e) { throw new
		 * NullPointerException("I'm in! - > " + ongoingBattles + "\nEnemyToAnt - >"
		 * +enemyToAnt); }
		 */

	}

	/**
	 * Per ogni situazione di battaglia, assegno un tempo massimo per la sua
	 * esecuzione ed avvio una simulazione di battaglia; Per ogni battaglia
	 * 
	 * @param ongoingBattlesSituation
	 */
	private void fight(Map<Tile, Tile> ongoingBattlesSituation) {
		Set<CombatSimulation> battles = new HashSet<CombatSimulation>();
		long timeAssigned = Timing.getCombatTimeStime() / ongoingBattlesSituation.size();

		ongoingBattlesSituation.entrySet().parallelStream()
				.forEachOrdered(e -> battles.add(new CombatSimulation(e.getKey(), e.getValue(), timeAssigned)));

		battles.parallelStream().forEachOrdered(battle -> battle.combatResolution());
		try {
			if (battles.size() > 1)
				throw new NullPointerException();

		} catch (NullPointerException e) {
			throw new NullPointerException("I'm in! - > " + battles + "\n\t" + getEnemyToAnt());
		}
		Set<Order> movesToPerform = new HashSet<Order>();
		battles.parallelStream().forEachOrdered(battle -> movesToPerform.addAll(battle.getMoves()));
		Game.issueOrders(movesToPerform);
	}

	public void doFood() {
		if (getMyAnts().size() > 0)
			new FoodCollection(getFoodTiles(), getMyAnts());

	}

	public void doAttackHills() {
		AttackDefenceHills.attack();
	}

	public void doDefenceHills() {
		AttackDefenceHills.defence();
	}

	public void doExploration() {
		new ExplorationAndMovement();

	}

	public static void printMap() {
		Iterator<List<Tile>> rowIt = map.iterator();
		while (rowIt.hasNext()) {
			Iterator<Tile> colIt = rowIt.next().iterator();
			while (colIt.hasNext()) {
				System.out.print(!colIt.next().isAccessible() ? 'x' : 'o');
			}
			System.out.print('\n');
		}
	}

	public static void printMapVision() {
		Iterator<List<Tile>> rowIt = map.iterator();
		while (rowIt.hasNext()) {
			Iterator<Tile> colIt = rowIt.next().iterator();
			while (colIt.hasNext()) {
				System.out.print(colIt.next().getVisible());
			}
			System.out.print('\n');
		}
	}

	public static void printNeighbour() {
		Iterator<List<Tile>> rowIt = map.iterator();
		while (rowIt.hasNext()) {
			Iterator<Tile> colIt = rowIt.next().iterator();
			while (colIt.hasNext()) {
				Tile tile = colIt.next();
				System.out.println(tile + " ->  " + tile.getNeighbour());
			}

		}
	}

}
