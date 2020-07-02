package combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import vision.Offsets;

public class Assignment {


	private Set<Tile> ants;
	private int antsLosses;

	private Set<Tile> antsHills;
	private int antsHillsDestroyed;

	private Map<Integer, Set<Tile>> enemyAnts;
	private List<Integer> enemyLosses;

	private Map<Integer, Set<Tile>> enemyHills;
	private List<Integer> enemyHillsDestroyed;

	private Set<Tile> foodTile;
	private int antsFoodCollected;
	private List<Integer> enemyFoodCollected;

	private Set<Tile> antMoves;
	private Map<Integer, Set<Tile>> enemyMoves;

	private int currentTurn;

	private boolean isEnemyMoves;

	private Set<Assignment> child;


	Assignment(int turn,  Set<Tile> myAntSet, Set<Tile> antHills, Map<Integer, Set<Tile>> enemyAntSet, Map<Integer, Set<Tile>> enemyHills, Set<Tile> foodTiles, boolean enemyMoves) {
		this.currentTurn = turn;
		this.isEnemyMoves = enemyMoves;
		this.child = new TreeSet<Assignment>();

		this.ants = myAntSet;
		this.antsHills = antHills;
		this.antsLosses = 0;
		this.antsHillsDestroyed = 0;

		this.enemyAnts = enemyAntSet;
		this.enemyHills = enemyHills;
		this.enemyLosses.forEach(i -> i = 0);
		this.enemyHillsDestroyed.forEach(i -> i = 0);

		this.foodTile = foodTiles;
		this.antsFoodCollected = 0;
		this.enemyFoodCollected = new ArrayList<Integer>(enemyAnts.size());
		this.enemyFoodCollected.forEach(i -> i = 0);
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

	public Map<Integer, Set<Tile>> getOpponentAnts() {
		return enemyAnts;
	}

	public Map<Integer, Set<Tile>> getOpponentHills() {
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

	public List<Integer> getOpponentLosses_number() {
		return enemyLosses;
	}

	public List<Integer> getOpponentHillDestroyed_number() {
		return enemyHillsDestroyed;
	}

	public int getAntsHillDestroyed_number() {
		return antsHillsDestroyed;
	}

	public int getAntsFoodCollected_number() {
		return antsFoodCollected;
	}

	public List<Integer> getOpponentFoodCollected_number() {
		return enemyFoodCollected;
	}


	public void performMove(Set<Order> moves) {
		moves.parallelStream().forEach(move ->{ ants.remove(move.getTile()); ants.add(move.getTarget());});
	}

	public void resolveCombatAndFoodCollection() {

		istantKill();
		battle();
		hillRazing();
		foodResolution();

	}

	private HashMap<Tile, Integer> computeFocusAttack(Set<Tile> ants, Set<Tile> enemy){
		HashMap<Tile, Integer> focusAttack = new HashMap<Tile, Integer>();

		Offsets attack = new Offsets(Game.getAttackRadius());

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


		IntStream.range(1, enemyAnts.size()+1).parallel().forEachOrdered( i  -> { 
			Set<Tile> ienemySet = enemyAnts.get(i);

			enemy.clear();
			enemy.addAll(ants);
			if(i-1 > 1)
				IntStream.range(1, i-1).parallel().forEachOrdered( j -> enemy.addAll(enemyAnts.get(j)));
			if(i+1 < enemyAnts.size()+1)
				IntStream.range(i+1, enemyAnts.size()+1).parallel().forEachOrdered( j -> enemy.addAll(enemyAnts.get(j)));

			focusAttack.putAll(computeFocusAttack(ienemySet, enemy));
		});
		
		Set<Tile> deadAnts = new TreeSet<Tile>();

		ants.parallelStream().forEachOrdered(ant -> {
			int curAntFA = focusAttack.get(ant);
			
			
			Set<Tile> deadEnemyAnts = new TreeSet<Tile>();
			
			IntStream.range(1, enemyAnts.size()+1).parallel().forEachOrdered( i  -> { 
				int enemyLossesNumber = enemyLosses.get(i);
				Set<Tile> ienemySet = enemyAnts.get(i);
				
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
		IntStream.range(1, enemyAnts.size()).parallel().forEachOrdered(i -> { 
			Set<Tile> iremoveAnts = new TreeSet<Tile>();
			Set<Tile> ienemyAnts = enemyAnts.get(i);


			ienemyAnts.parallelStream().forEachOrdered(ienemy -> {
				IntStream.range(i+1, enemyAnts.size()+1).parallel().forEachOrdered(j -> { 
					if(enemyAnts.get(j).remove(ienemy)){ 
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
			IntStream.range(1, enemyAnts.size()).parallel().forEachOrdered(i -> { 
				if(enemyAnts.get(i).remove(ant)){ 
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
					if(enemyCount.get(i) == count) 
						enemyFoodCollected.set(i,enemyFoodCollected.get(i)+1); 
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
