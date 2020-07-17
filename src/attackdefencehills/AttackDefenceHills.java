package attackdefencehills;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import game.Directions;
import game.Game;
import game.Order;
import game.Tile;
import search.Node;
import search.Search;
import vision.Offsets;
import vision.Vision;

public class AttackDefenceHills {

	public AttackDefenceHills() {
		//defence();
		//attack();
	}

	// c'e' qualcosa che non va
	// entrano in difesa anche se non ci sono formiche nemiche in vista
	// da controllare
	public static void defence() {
		int avaiableAnts = Game.getMyAnts().size();

		Map<Tile, TreeSet<Node>> enemiesForHills = new HashMap<Tile,TreeSet<Node>>();

		Set<Tile> defender = new TreeSet<Tile>();

		try {
			Game.getMyHills().forEach(curHill -> {
				Set<Tile> tempSet = new TreeSet<Tile>();
				tempSet.add(curHill);
				Search enSearch = new Search(tempSet, Game.getEnemyAnts(), Game.getViewRadius2()+8, false, false, false);
				tempSet.clear();
				tempSet = enSearch.adaptiveSearch();

				enemiesForHills.put(curHill, tempSet.parallelStream().map(e -> new Node(e, curHill)).collect(Collectors.toCollection(TreeSet<Node>::new)));
			});

		}catch(Exception e) {
			throw new NullPointerException("ciao");
		}

		if (enemiesForHills.size() != 0) {
			double antsForHill = avaiableAnts / (enemiesForHills.size());

			if (antsForHill >= 1) {
				enemiesForHills.entrySet().parallelStream().forEachOrdered(enemiesForHill -> {

					Tile hill = enemiesForHill.getKey();
					TreeSet<Node> enemies = enemiesForHill.getValue();
					if(enemies.size()>0) {
						Set<Tile> defendHillTile = Vision.getHillDefenceTargets(hill);

						Iterator<Tile> defIt = defendHillTile.iterator();
						if(defIt.hasNext())
							defender.add(defIt.next());

						int minDist = enemies.first().getHeuristicValue();
						if(defIt.hasNext() && antsForHill >= 4 && minDist < Math.sqrt(Game.getViewRadius2()))//PARAMETER
							defender.add(defIt.next());
						if (defIt.hasNext() && antsForHill >= 3 && minDist < Math.sqrt(Game.getAttackRadius2())+3)
							defender.add(defIt.next());
						if (defIt.hasNext() && antsForHill >= 2 && minDist < Math.sqrt(Game.getAttackRadius2()))
							defender.add(defIt.next());

					}
				});
			} else if (avaiableAnts > 0) {	
				TreeSet<Node> enemiesNearHills = new TreeSet<Node>();
				enemiesForHills.values().forEach(enemiesNearHills::addAll);
				
				Iterator<Node> enemiesNearHillsIt = enemiesNearHills.iterator();
				
				int assignedAnts = 0;
				while(enemiesNearHillsIt.hasNext() && assignedAnts<avaiableAnts) {
					Tile hill = enemiesNearHillsIt.next().getTarget();
					Set<Tile> defendHillTile = Vision.getHillDefenceTargets(hill);
					
					Iterator<Tile> defIt = defendHillTile.iterator();
					if(defIt.hasNext()) {
						Tile tileToDefend = defIt.next();
						if(!defender.add(tileToDefend) && defIt.hasNext())
							if(!defender.add(defIt.next()) && defIt.hasNext())
								defender.add(defIt.next());
					}
				}
				
			}

			/*
			 * Directions sentinel = Directions.random(); Tile hill =
			 * myHills.iterator().next(); //difendi almeno un nido
			 * defender.add(Game.getTile(hill,sentinel.getDiagonal()));
			 * 
			 * if(antsForHill>1) //TODO non abbiamo cercato la formica vicina
			 * defender.add(Game.getTile(hill,sentinel.getOpponent().getDiagonal()));
			 */

		}
		//Set<Order> withoutHill = null;
		if(!defender.isEmpty()) {
			Search s = new Search(defender, Game.getMyAnts(), null, false, true, false);
			s.adaptiveSearch();


			//Game.issueOrders(s.getOrders()); // FIXME controllare se sta cosa funziona, nel caso da l'ordine al
			// contrario

			//withoutHill = doNotStepOnMyHills(s.getOrders()); //TODO
			Game.issueOrders(s.getOrders());//
			//Game.issueOrders(withoutHill);
		}

		/*
		System.out.println("*enemiesForHills* "+enemiesForHills);
		Game.getMyHills().forEach(hill -> System.out.println("*offset*"+Vision.getHillDefenceTargets(hill)));
		System.out.println("*defender* "+defender);
		System.out.println("*order* "+withoutHill);
		System.out.println("*enemy*"+ Game.getEnemyAnts());
		 */




	}

	//TODO controllare la disponibilità di formiche con la quantità di formiche nemiche che circondano il nido
	public static void attack() {
		// questa ricerca e' giusta
		Search s = new Search(Game.getEnemyHills(), Game.getMyAnts(), null, false, false, true);
		s.adaptiveSearch();
		//Set<Order> withoutHill = doNotStepOnMyHills(s.getOrders());
		//Game.issueOrders(withoutHill);
		Game.issueOrders(s.getOrders());
	}

	//TODO magari da rimuovere
	private Set<Order> doNotStepOnMyHills(Set<Order> orders) {
		Set<Order> withoutHill = new HashSet<Order>();
		orders.parallelStream().forEachOrdered(o -> {
			if (!Game.getMyHills().contains(o.getOrderedTile())) {
				withoutHill.add(o);
			}
		});

		return withoutHill;
	}

	/*
	 * private void defense(Set<Tile> myAnts, Set<Tile> myHills, Set<Tile> enemy) {
	 * int avaiableAnts = myAnts.size();
	 * 
	 * double antsForHill = avaiableAnts/(myHills.size()+1);
	 * 
	 * Set<Tile> defender = new TreeSet<Tile>();
	 * 
	 * if(antsForHill>0) { Game.getMyHills().parallelStream().forEachOrdered(hill ->
	 * { Directions sentinel = Directions.random();
	 * defender.add(Game.getTile(hill,sentinel.getDiagonal()));
	 * 
	 * //ricerca la formica nemica più vicina Iterator<Tile> enemyItr =
	 * enemy.iterator();
	 * 
	 * if(enemyItr.hasNext()) { Tile minTarget = enemyItr.next(); int minDist =
	 * Game.getDistance(hill,minTarget);
	 * 
	 * while (enemyItr.hasNext()) { Tile next = enemyItr.next(); int nextDist =
	 * Game.getDistance(hill,next); if (nextDist < minDist) { minTarget = next;
	 * minDist = nextDist; } } if(antsForHill>1 && minDist < Game.getViewRadius())
	 * defender.add(Game.getTile(hill,sentinel.getOpponent().getDiagonal()));
	 * if(antsForHill>2 && minDist < Game.getAttackRadius()+3)
	 * defender.add(Game.getTile(hill,sentinel.getNext().getDiagonal()));
	 * if(antsForHill>3 && minDist < Game.getAttackRadius())
	 * defender.add(Game.getTile(hill,sentinel.getOpponent().getNext().getDiagonal()
	 * )); }
	 * 
	 * });
	 * 
	 * }else if(avaiableAnts > 0 ) { Directions sentinel = Directions.random(); Tile
	 * hill = myHills.iterator().next(); //difendi almeno un nido
	 * defender.add(Game.getTile(hill,sentinel.getDiagonal()));
	 * 
	 * if(antsForHill>1) //TODO non abbiamo cercato la formica vicina
	 * defender.add(Game.getTile(hill,sentinel.getOpponent().getDiagonal())); }
	 * 
	 * //Search s = new Search(defender, Game.getMyAnts(), null, false, true,
	 * false); Search s = new Search(defender, Game.getMyAnts(), null, false, false,
	 * true); s.adaptiveSearch(); Game.issueOrders(s.getOrders()); //FIXME
	 * controllare se sta cosa funziona, nel caso da l'ordine al contrario }
	 * 
	 * private void attack(Set<Tile> myAnts, Set<Tile> enemyHills) { //questa
	 * ricerca è giusta Search s = new Search(Game.getEnemyHills(),
	 * Game.getMyAnts(), null, false, false, true); s.adaptiveSearch();
	 * Game.issueOrders(s.getOrders());
	 * 
	 * }
	 */

}
