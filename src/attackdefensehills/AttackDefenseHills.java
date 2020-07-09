package attackdefensehills;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import game.Directions;
import game.Game;
import game.Tile;
import search.Search;


public class AttackDefenseHills {

	public AttackDefenseHills(Set<Tile> myAnts, Set<Tile> myHills, Set<Tile> enemy,  Set<Tile> enemyHills) {
		defense(myAnts, myHills, enemy);
		attack(myAnts, enemyHills);

	}

	private void defense(Set<Tile> myAnts, Set<Tile> myHills, Set<Tile> enemy) {
		int avaiableAnts = myAnts.size();

		double antsForHill = avaiableAnts/(myHills.size()+1);

		Set<Tile> defender = new TreeSet<Tile>();

		if(antsForHill>0) {
			Game.getMyHills().parallelStream().forEachOrdered(hill -> {
				Directions sentinel = Directions.random();
				defender.add(Game.getTile(hill,sentinel.getDiagonal()));

				//ricerca la formica nemica più vicina
				Iterator<Tile> enemyItr = enemy.iterator();

				if(enemyItr.hasNext()) {
					Tile minTarget = enemyItr.next();
					int minDist = Game.getDistance(hill,minTarget);

					while (enemyItr.hasNext()) {
						Tile next = enemyItr.next();
						int nextDist = Game.getDistance(hill,next);
						if (nextDist < minDist) {
							minTarget = next;
							minDist = nextDist;
						}
					}
					if(antsForHill>1 && minDist < Game.getViewRadius())
						defender.add(Game.getTile(hill,sentinel.getOpponent().getDiagonal()));
					if(antsForHill>2 && minDist < Game.getAttackRadius()+3)
						defender.add(Game.getTile(hill,sentinel.getNext().getDiagonal()));
					if(antsForHill>3 && minDist < Game.getAttackRadius())
						defender.add(Game.getTile(hill,sentinel.getOpponent().getNext().getDiagonal()));
				}

			});	

		}else if(avaiableAnts > 0 ) {
			Directions sentinel = Directions.random();
			Tile hill = myHills.iterator().next();
			//difendi almeno un nido
			defender.add(Game.getTile(hill,sentinel.getDiagonal()));

			if(antsForHill>1) //TODO non abbiamo cercato la formica vicina
				defender.add(Game.getTile(hill,sentinel.getOpponent().getDiagonal()));
		}

		Search s = new Search(defender, Game.getMyAnts(), null, false, true, false);
		s.adaptiveSearch();
		Game.issueOrders(s.getOrders()); //FIXME controllare se sta cosa funziona, nel caso da l'ordine al contrario
	}

	private void attack(Set<Tile> myAnts, Set<Tile> enemyHills) {
		
		Search s = new Search(Game.getEnemyHills(), Game.getMyAnts(), null, false, false, true);
		s.adaptiveSearch();
		Game.issueOrders(s.getOrders());
		
	}

}
