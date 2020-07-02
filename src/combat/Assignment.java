package combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import game.Game;
import game.Order;
import game.Tile;
import timing.Timing;

public class Assignment {


	Set<Tile> ants;
	int antsLosses;

	Set<Tile> antsHills;
	int antsHillsDestroyed;

	Map<Integer, Set<Tile>> enemyAnts;
	List<Integer> enemyLosses;

	Map<Integer, Set<Tile>> enemyHills;
	List<Integer> enemyHillsDestroyed;

	Set<Tile> foodTile;
	int antsFoodCollected;
	List<Integer> enemyFoodCollected;

	Set<Tile> antMoves;
	Map<Integer, Set<Tile>> enemyMoves;

	int currentTurn;

	boolean isEnemyMoves;

	Set<Assignment> child;


	Assignment(int turn,  Set<Tile> myAntSet, Set<Tile> antHills, Map<Integer, Set<Tile>> enemyAntSet, Map<Integer, Set<Tile>> enemyHills, Set<Tile> foodTiles, boolean enemyMoves) {
		this.currentTurn = turn;
		this.isEnemyMoves = enemyMoves;
		this.child = new TreeSet<Assignment>();

		this.ants = myAntSet;
		this.antsHills = antHills;
		antsLosses = 0;
		antsHillsDestroyed = 0;

		this.enemyAnts = enemyAntSet;
		this.enemyHills = enemyHills;
		enemyLosses = 0;
		enemyHillsDestroyed = 0;

		this.foodTile = foodTiles;
		antsFoodCollected = 0;
		enemyFoodCollected = new ArrayList<Integer>(enemyAnts.size());
		enemyFoodCollected.forEach(i -> i = 0);
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
		return enemyAnts;
	}

	public Set<Tile> getOpponentHills() {
		return enemyHills;
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
		return enemyLosses;
	}

	public int getOpponentHillDestroyed_number() {
		return enemyHillsDestroyed;
	}

	public int getAntsHillDestroyed_number() {
		return antsHillsDestroyed;
	}

	public int getAntsFoodCollected_number() {
		return antsFoodCollected;
	}

	public int getOpponentFoodCollected_number() {
		return enemyFoodCollected;
	}


	public void performMove(Set<Order> moves) {
		moves.parallelStream().forEach(move ->{ ants.remove(move.getTile()); ants.add(move.getTarget());});
	}

	public void resolveCombatAndFoodCollection() {

		istantKill();
		//combattimento
		hillRazing();
		foodResolution();

	}



	private void istantKill(){

		//formiche tue si suicidano tra di loro

		
		//formiche nemiche si suicidano tra di loro

		//formiche nostre e formiche nemiche si suicidono
		Set<Tile> antsKilled = new TreeSet<Tile>();
		ants.parallelStream().forEachOrdered(ant -> {
			if(enemyAnts.remove(ant)) {
				antsKilled.add(ant);
				antsLosses++;
				enemyLosses++;
			}
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
			Set<Tile> curEnemyHills = enemyHills.get(i);
			curEnemyHills.parallelStream().forEachOrdered(hill -> {
				if(ants.contains(hill)) {
					enemyHillsDestroyed.set(i+1, enemyHillsDestroyed.get(i+1)+1);
					hillDestroyed.add(hill);
				}
			});
			enemyHills.get(i).removeAll(hillDestroyed);	
		});
	}

	/**
	 * NB: è scritta sapendo che spawn radius == 1, il modo corretto sarebbe usare una static search
	 */
	private void foodResolution() {
		Set<Tile> foodGathered = new TreeSet<Tile>();

		foodTile.parallelStream().forEachOrdered(food -> {
			int antCount = 0;
			List<Integer> enemyCount = new ArrayList<Integer>(enemyAnts.size());
			enemyCount.forEach(i -> i = 0);

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
					if(enemyCount.get(i) == count) {
						enemyFoodCollected.set(i,enemyFoodCollected.get(i)); 
						break;
					}
				});

			if(antCount>0 || count>0)
				foodGathered.add(food);
		});

		foodTile.removeAll(foodGathered);
	}



	public void addChild(Assignment childState) {
		// TODO Auto-generated method stub

	}

}
