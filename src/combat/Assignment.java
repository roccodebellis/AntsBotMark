package combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import defaultpackage.Configuration;
import game.Game;
import game.Order;
import game.Tile;
import timing.Timing;
import vision.Offsets;

public class Assignment implements Comparable<Assignment>{


	private Set<Tile> ants;
	private int antsLosses;

	private Set<Tile> antsHills;
	private int antsHillsDestroyed;

	private Map<Integer, Set<Tile>> enemyAnts;
	private List<Integer> enemyLosses;

	private Map<Integer, Set<Tile>> enemyHills;
	private List<Integer> enemyHillsDestroyed;

	private Set<Tile> foodTiles;
	private int antsFoodCollected;
	private List<Integer> enemyFoodCollected;

	private int currentTurn;

	private boolean isEnemyMoves;

	private TreeSet<Assignment> child;

	private Set<Order> antsMove;

	private double value;


	Assignment(int turn,  Set<Tile> myAntSet, Set<Tile> antHills, Map<Integer, Set<Tile>> enemyAntSet, Map<Integer, Set<Tile>> enemyHills, Set<Tile> foodTiles, boolean enemyMoves) {
		this.currentTurn = turn;
		this.isEnemyMoves = enemyMoves;
		this.child = new TreeSet<Assignment>(Collections.reverseOrder());

		this.ants = myAntSet;
		this.antsHills = antHills;
		this.antsLosses = 0;
		this.antsHillsDestroyed = 0;

		this.enemyAnts = enemyAntSet;
		this.enemyHills = enemyHills;
		this.enemyLosses = new ArrayList<Integer>(enemyAnts.size());
		IntStream.range(0, enemyAnts.size()).parallel().forEachOrdered(i -> enemyLosses.add(0));
		
		this.enemyHillsDestroyed = new ArrayList<Integer>(enemyAnts.size());
		IntStream.range(0, enemyAnts.size()).parallel().forEachOrdered(i -> enemyHillsDestroyed.add(0));

		this.foodTiles = foodTiles;
		this.antsFoodCollected = 0;
		this.enemyFoodCollected = new ArrayList<Integer>(enemyAnts.size());
		IntStream.range(0, enemyAnts.size()).parallel().forEachOrdered(i -> enemyFoodCollected.add(0));


		antsMove = new TreeSet<Order>();
	}


	public boolean isEnemyMove() {
		return isEnemyMoves;
	}

	public Set<Tile> getAnts() {
		return ants;
	}

	public int getAnts_number() {
		return ants.size();
	}

	public Set<Tile> getOpponentAnts() {
		Set<Tile> enemy = new HashSet<Tile>();
		enemyAnts.values().forEach( enemySet -> enemy.addAll(enemySet));
		return enemy;
	}

	public Set<Tile> getOpponentHills() {
		Set<Tile> curEnemyHills = new HashSet<Tile>();
		enemyHills.values().forEach( enemySet -> curEnemyHills.addAll(enemySet));
		return curEnemyHills;
	}

	public int getOpponentAnts_number() {
		return enemyAnts.size();
	}

	public int getTurnsLeft() {
		return Timing.getTurnLeft(currentTurn);
	}

	public int getAntsLosses_number() {
		return antsLosses;
	}

	public int getOpponentLosses_number() {
		return enemyLosses.parallelStream().mapToInt(Integer::intValue).sum();
	}

	public int getOpponentHillDestroyed_number() {
		return enemyHillsDestroyed.parallelStream().mapToInt(Integer::intValue).sum();
	}

	public int getAntsHillDestroyed_number() {
		return antsHillsDestroyed;
	}

	public int getAntsFoodCollected_number() {
		return antsFoodCollected;
	}

	public int getOpponentFoodCollected_number() {
		return enemyFoodCollected.parallelStream().mapToInt(Integer::intValue).sum();
	}


	public Assignment performMove(Set<Order> moves) {
		Set<Tile> newAnts = new TreeSet<Tile>();
		newAnts.addAll(ants);
		Map<Integer, Set<Tile>> newEnemyAnts = new TreeMap<Integer, Set<Tile>>(enemyAnts);

		if(isEnemyMoves)
			moves.parallelStream().forEach(move ->{ 
				IntStream.range(0, newEnemyAnts.size()).parallel().forEachOrdered( i  -> { 
					if(newEnemyAnts.get(i+1).remove(move.getOrigin()))
						newEnemyAnts.get(i+1).add(move.getOrderedTile());
				});
			});
		else {
			moves.parallelStream().forEach(move ->{ newAnts.remove(move.getOrigin()); newAnts.add(move.getOrderedTile());});
			antsMove = moves;
		}
		return new Assignment(currentTurn+1,  newAnts, antsHills, newEnemyAnts, enemyHills, foodTiles, isEnemyMoves ? false : true);

	}

	public void resolveCombatAndFoodCollection() {

		istantKill();
		battle();
		hillRazing();
		foodResolution();

	}

	private HashMap<Tile, Integer> computeFocusAttack(Set<Tile> ants, Set<Tile> enemy){
		HashMap<Tile, Integer> focusAttack = new HashMap<Tile, Integer>();

		Offsets attack = new Offsets(Game.getAttackRadius2());

		ants.parallelStream().forEachOrdered( ant -> {
			Set<Tile> shape = Game.getTiles(ant, attack);
			focusAttack.put(ant, (int) shape.parallelStream().filter(t -> enemy.contains(t)).count());
		});

		return focusAttack;
	}

	private void battle() {
		HashMap<Tile, Integer> focusAttack= new HashMap<Tile, Integer>();


		Set<Tile> enemy = new HashSet<Tile>();
		enemyAnts.values().forEach( enemySet -> enemy.addAll(enemySet));

		focusAttack.putAll(computeFocusAttack(ants, enemy));


		IntStream.range(0, enemyAnts.size()).parallel().forEachOrdered( i  -> { 
			Set<Tile> ienemySet = enemyAnts.get(i+1);

			enemy.clear();
			enemy.addAll(ants);
			if(i > 0)
				IntStream.range(0, i+1).parallel().forEachOrdered( j -> enemy.addAll(enemyAnts.get(j+1)));//FIXME
			if(i+2 < enemyAnts.size())
				IntStream.range(i+2, enemyAnts.size()).parallel().forEachOrdered( j -> enemy.addAll(enemyAnts.get(j+1)));

			//System.out.println("* ienemySet"+ ienemySet);
			//System.out.println("* enemy"+ enemy);

			focusAttack.putAll(computeFocusAttack(ienemySet, enemy));
		});

		Set<Tile> deadAnts = new TreeSet<Tile>();

		ants.parallelStream().forEachOrdered(ant -> {
			int curAntFA = focusAttack.get(ant);


			Set<Tile> deadEnemyAnts = new TreeSet<Tile>();

			IntStream.range(0, enemyAnts.size()).parallel().forEachOrdered( i  -> { 
				int enemyLossesNumber = enemyLosses.get(i);
				Set<Tile> ienemySet = enemyAnts.get(i+1);

				deadEnemyAnts.clear();

				ienemySet.parallelStream().forEachOrdered(enemyAnt -> {
					int curEnemyFA = focusAttack.get(enemyAnt);

					if(curAntFA > curEnemyFA) {
						antsLosses++;
						deadAnts.add(ant);
					} else if (curAntFA < curEnemyFA) {
						deadEnemyAnts.add(enemyAnt);
						enemyLosses.set(i, enemyLossesNumber+1);
					} else {
						antsLosses++;
						deadAnts.add(ant);
						deadEnemyAnts.add(enemyAnt);
						enemyLosses.set(i, enemyLossesNumber+1);
					}

				});
				ienemySet.removeAll(deadEnemyAnts);
			});
		});

	}


	private void istantKill(){

		//formiche tue/di ogni nemico si suicidano tra di loro


		//formiche nemiche si suicidano tra di loro
		IntStream.range(0, enemyAnts.size()).parallel().forEachOrdered(i -> { 
			Set<Tile> iremoveAnts = new TreeSet<Tile>();
			Set<Tile> ienemyAnts = enemyAnts.get(i+1);


			ienemyAnts.parallelStream().forEachOrdered(ienemy -> {
				IntStream.range(i+1, enemyAnts.size()).parallel().forEachOrdered(j -> { 
					if(enemyAnts.get(j+1).remove(ienemy)){ 
						if(!iremoveAnts.contains(ienemy)) {
							iremoveAnts.add(ienemy);
							enemyLosses.set(i,enemyLosses.get(i)+1); 
						}
						enemyLosses.set(j,enemyLosses.get(j)+1); 
					}
				});
			});
			ienemyAnts.removeAll(iremoveAnts);
		});

		//formiche nostre e formiche nemiche si suicidono
		Set<Tile> antsKilled = new TreeSet<Tile>();
		ants.parallelStream().forEachOrdered(ant -> {
			IntStream.range(0, enemyAnts.size()).parallel().forEachOrdered(i -> { 
				if(enemyAnts.get(i+1).remove(ant)){ 
					if(!antsKilled.contains(ant)) {
						antsKilled.add(ant);
						antsLosses++;
					}
					enemyLosses.set(i,enemyLosses.get(i)+1); 
				}
			});
		});
		ants.removeAll(antsKilled);
	}

	private void hillRazing() { //TODO fare una sola funzione
		//FIXME TENERE IN CONSIDERAZIONE L'AVER MANGIATO CIBO NEL TURNO PRECEDENTE
		Set<Tile> hillDestroyed = new TreeSet<Tile>();
		antsHills.parallelStream().forEachOrdered(hill -> {
			IntStream.range(0, enemyAnts.size()).parallel().forEachOrdered(i -> { 
				if(enemyAnts.get(i+1).contains(hill)) {
					antsHillsDestroyed++;
					hillDestroyed.add(hill);
				}
			});
		});
		antsHills.removeAll(hillDestroyed);	


		IntStream.range(0, enemyAnts.size()).parallel().forEachOrdered(i -> {
			hillDestroyed.clear();
			Set<Tile> curEnemyHills = enemyHills.get(i+1);
			curEnemyHills.parallelStream().forEachOrdered(hill -> {
				if(ants.contains(hill)) {
					enemyHillsDestroyed.set(i, enemyHillsDestroyed.get(i)+1);
					hillDestroyed.add(hill);
				}
			});
			enemyHills.get(i+1).removeAll(hillDestroyed);	
		});
	}

	/**
	 * NB: è scritta sapendo che spawn radius == 1, il modo corretto sarebbe usare una static search
	 */
	private void foodResolution() {
		Set<Tile> foodGathered = new TreeSet<Tile>();

		foodTiles.parallelStream().forEachOrdered(food -> {
			int antCount = 0;
			List<Integer> enemyCount = new ArrayList<Integer>(enemyAnts.size());
			IntStream.range(0, enemyAnts.size()).parallel().forEachOrdered(i -> enemyCount.add(0));

			Set<Tile> neighbours = food.getNeighbour().entrySet().parallelStream().map(nE -> nE.getValue()).collect(Collectors.toSet());
			Iterator<Tile> neItr = neighbours.iterator();
			while(neItr.hasNext()) {
				Tile neighbour = neItr.next();
				if(ants.contains(neighbour))
					antCount++;
				else enemyAnts.entrySet().parallelStream().forEachOrdered(e -> {
					if(e.getValue().contains(neighbour))
						enemyCount.set(e.getKey()-1, enemyCount.get(e.getKey()-1) +1);
				});

			}

			int count = enemyCount.parallelStream().mapToInt(Integer::intValue).sum();


			if(antCount>0 && count==0) 
				antsFoodCollected++;
			else if(antCount==0 && count>0)
				IntStream.range(0, enemyCount.size()).parallel().forEachOrdered(i -> { 
					if(enemyCount.get(i) == count) 
						enemyFoodCollected.set(i,enemyFoodCollected.get(i)+1); 
				});

			if(antCount>0 || count>0)
				foodGathered.add(food);
		});

		foodTiles.removeAll(foodGathered);
	}



	public void addChild(Assignment childState) {
		child.add(childState);
	}


	public Set<Order> getFirstChild() {
		return child.first().getMoves();
	}


	private Set<Order> getMoves() {
		return antsMove;
	}

	long GetExtensionEstimate() {
		return (long) (ants.size() + enemyAnts.entrySet().parallelStream().mapToInt(eASet -> eASet.getValue().size()).sum()) * Configuration.getMilSecUsedForEachAntsInCS();
	}
	
	/**
	 * 
	 * FIXME aggiungere quando veine effettuata la traduzione che un punteggio di 1 eè assegnato se un nido
	 * nemico viene distruto
	 */
	double evaluate() {
		Double AntsMultiplier = 1.1D;
		Double OpponentMultiplier = 1.0D;

		double value;

		//TODO MassRatioThreshold impostare mass radio in base al numero di formiche
		if (getAnts_number() > 3) //FIXME MassRatioThreshold
			OpponentMultiplier *= Math.max(1,Math.pow((getAnts_number()+1)/(getOpponentAnts_number()+1),2));

		//TODO crescita logaritmica col passare dei turni a partire da una certa soglia
		if(getTurnsLeft()<50) 
			OpponentMultiplier *= 1.5D;

		value = OpponentMultiplier * getOpponentLosses_number() - AntsMultiplier  * getAntsLosses_number();

		if(getAntsLosses_number() == getAnts_number())
			value -= 0.5;
		else if (getOpponentLosses_number() == getOpponentAnts_number())
			value += 0.4;

		//TODO RISCRIVERE FUNZIONE 
		//considerando un pareggio 
		//considerando in caso di pareggio se il numero di formiche uccise da me è maggiore 
		//del numero di formiche perse

		value += getOpponentHillDestroyed_number();
		value -= getAntsHillDestroyed_number() * 5;

		value += getAntsFoodCollected_number() /2;
		value -= getOpponentFoodCollected_number();

		this.value = value;
		return value;
	}


	@Override
	public int compareTo(Assignment o) {
		return Double.compare(value, o.value);
	}


	@Override
	public String toString() {
		return "Assignment [ants=" + ants + ", enemyAnts=" + enemyAnts + ", value=" + value + "]";
	}


}
