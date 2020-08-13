package attackdefencehills;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import game.Game;
import game.Tile;
import search.Node;
import search.Search;
import vision.Offsets;
import vision.Vision;

public class AttackDefenceHills {

	private static Logger LOGGER = Logger.getLogger( AttackDefenceHills.class.getName() );

	public static void defence() {
		int avaiableAnts = Game.getMyAnts().size();

		Map<Tile, TreeSet<Node>> enemiesForHills = new HashMap<Tile,TreeSet<Node>>();

		Set<Tile> defender = new TreeSet<Tile>();
		//TODO cancella eccezione
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

						Set<Tile> myAntsNearHill = new HashSet<Tile>(Game.getMyAnts());
						myAntsNearHill.addAll(Game.getOrderlyAnts());
						myAntsNearHill.retainAll(Game.getTiles(hill, new Offsets(Game.getAttackRadius2()*2)));

						if(myAntsNearHill.size()>2) {
							int minDist = enemies.first().getHeuristicValue(); 
							if(defIt.hasNext() && antsForHill >= 4 && minDist < Math.sqrt(Game.getViewRadius2()))//PARAMETER
								defender.add(defIt.next());
							if (defIt.hasNext() && antsForHill >= 3 && minDist < Math.sqrt(Game.getAttackRadius2())+3)
								defender.add(defIt.next());
							if (defIt.hasNext() && antsForHill >= 2 && minDist < Math.sqrt(Game.getAttackRadius2()))
								defender.add(defIt.next());
						}
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
						if(!defender.add(tileToDefend) && defIt.hasNext()) {
							assignedAnts++;
							if(!defender.add(defIt.next()) && defIt.hasNext())
								assignedAnts++;
							if(defender.add(defIt.next()))
								assignedAnts++;
						}
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

		if(!defender.isEmpty()) {
			Game.getMyHills().parallelStream().forEachOrdered(hill -> hill.setSuitableSuperSpecial(true));
			Search s = new Search(defender, Game.getMyAnts(), null, false, true, false);
			s.adaptiveSearch();
			Game.getMyHills().parallelStream().forEachOrdered(hill -> hill.setSuitableSuperSpecial(false));

			//Game.issueOrders(s.getOrders()); // FIXME controllare se sta cosa funziona, nel caso da l'ordine al
			// contrario

			Game.issueOrders(s.getOrders());//
		}

	}

	//TODO controllare la disponibilità di formiche con la quantità di formiche nemiche che circondano il nido
	public static void attack() {
		Set<Tile> enemyHills = Game.getEnemyHills();
		Set<Tile> myAnts = Game.getMyAnts();

		if(!enemyHills.isEmpty() && !myAnts.isEmpty()) {
			/* BFS reverse*/
			Search s = new Search(enemyHills, myAnts, null, false, false, true);
			s.adaptiveSearch();
			
			/* A* 
			//Search s = new Search(myAnts, enemyHills , null, true, false, false);
			
			s.EAStarSearch();
			*/
			Game.issueOrders(s.getOrders());
		}
	}

}
