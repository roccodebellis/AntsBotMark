package attackdefensehills;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import game.Directions;
import game.Game;
import game.Order;
import game.Tile;
import search.Search;

public class AttackDefenseHills {

	public AttackDefenseHills(Set<Tile> myAnts, Set<Tile> myHills, Set<Tile> enemy, Set<Tile> enemyHills) {
		defense(myAnts, myHills, enemy);
		attack(myAnts, enemyHills);

	}

	public static void defense(Set<Tile> myAnts, Set<Tile> myHills, Set<Tile> enemy) {
		int avaiableAnts = myAnts.size();

		if (myHills.size() != 0) {
			double antsForHill = avaiableAnts / (myHills.size());// sembra dare prestazioni migliori senza +1

			Set<Tile> defender = new TreeSet<Tile>();

			if (antsForHill > 0) {
				Game.getMyHills().parallelStream().forEachOrdered(hill -> {
					Directions sentinel = Directions.random();
					defender.add(Game.getTile(hill, sentinel.getDiagonal()));

					// ricerca la formica nemica piu' vicina
					Iterator<Tile> enemyItr = enemy.iterator();

					if (enemyItr.hasNext()) {
						Tile minTarget = enemyItr.next();
						int minDist = Game.getDistance(hill, minTarget);

						while (enemyItr.hasNext()) {
							Tile next = enemyItr.next();
							int nextDist = Game.getDistance(hill, next);
							if (nextDist < minDist) {
								minTarget = next;
								minDist = nextDist;
							}
						}
						//>5
						if (antsForHill > 3 && minDist < Game.getViewRadius() - 20)// era troppo alto viewradius,
																					// richiamava
																					// troppe formiche
							defender.add(Game.getTile(hill, sentinel.getOpponent().getDiagonal()));
						if (antsForHill > 2 && minDist < Game.getAttackRadius() + 3)
							defender.add(Game.getTile(hill, sentinel.getNext().getDiagonal()));
						if (antsForHill > 1 && minDist < Game.getAttackRadius())
							defender.add(Game.getTile(hill, sentinel.getOpponent().getNext().getDiagonal()));
						// anche cosi' buone prestazioni
						/*
						 * if(antsForHill>5 && minDist < Game.getViewRadius()-37)//era troppo alto
						 * viewradius, richiamava troppe formiche
						 * defender.add(Game.getTile(hill,sentinel.getOpponent().getDiagonal()));
						 * if(antsForHill>2 && minDist < Game.getAttackRadius()+5)
						 * defender.add(Game.getTile(hill,sentinel.getNext().getDiagonal()));
						 * if(antsForHill>1 && minDist < Game.getAttackRadius()+1)
						 * defender.add(Game.getTile(hill,sentinel.getOpponent().getNext().getDiagonal()
						 * ));
						 */
					}

				});

			} else if (avaiableAnts > 0) {

				Boolean flag = true;
				Tile hill;
				do {
					Iterator<Tile> it = myHills.iterator();
					if (it.hasNext()) {
						hill = it.next();
						Tile target;
						Directions dir = Directions.random();
						int i = 4;
						do {
							target = Game.getTile(hill, dir.getDiagonal());
							if (Game.getOrdersTarget().contains(target)) {
								dir = Directions.random();
								flag = false;
							}
							i--;
						} while (i != 0);

						if (flag == false && it.hasNext())
							continue;
						else if (flag == true) {
							defender.add(Game.getTile(hill, dir.getDiagonal()));
							// difendi almeno un nido
							if (antsForHill > 1) // TODO non abbiamo cercato la formica vicina
								defender.add(Game.getTile(hill, dir.getOpponent().getDiagonal()));
						}
					}
				} while (flag);
			}
			/*
			 * Directions sentinel = Directions.random(); Tile hill =
			 * myHills.iterator().next(); //difendi almeno un nido
			 * defender.add(Game.getTile(hill,sentinel.getDiagonal()));
			 * 
			 * if(antsForHill>1) //TODO non abbiamo cercato la formica vicina
			 * defender.add(Game.getTile(hill,sentinel.getOpponent().getDiagonal()));
			 */
		

			Search s = new Search(defender, Game.getMyAnts(), null, false, true, false);// da valutare, mandiamo solo una
																					// formica
			// dunque one_target_per_source oppure piu' formiche? prestazioni non sembrano
			// tanto diverse
			// Search s = new Search(defender, Game.getMyAnts(), null, false, false, true);
			//se mettiamo la ricerca a commento c'e' bisogno di fare il controllo sugli hill
			//oerche' le formiche camminano sui propri hill
			//in tal caso andrebbero in timeout perche' impiegherebbero troppo tempo
			s.adaptiveSearch();
			Game.issueOrders(s.getOrders()); // FIXME controllare se sta cosa funziona, nel caso da l'ordine al contrario
		}
	}

	public static void attack(Set<Tile> myAnts, Set<Tile> enemyHills) {
		// questa ricerca e' giusta
		Search s = new Search(Game.getEnemyHills(), Game.getMyAnts(), null, false, false, true);
		s.adaptiveSearch();
		Set<Order> withoutHill = doNotStepOnMyHills(s.getOrders());
		Game.issueOrders(withoutHill);
	}

	private static Set<Order> doNotStepOnMyHills(Set<Order> orders) {
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
