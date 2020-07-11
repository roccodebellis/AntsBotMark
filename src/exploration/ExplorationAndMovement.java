package exploration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import game.Game;
import game.Order;
import game.Tile;
import search.Search;

public class ExplorationAndMovement {

	//obiettivo inviare formiche fuori dai confini di cio che e' conosciuto
	//oppure inviare dove e' necessario (battaglie in corso, punti chiave, etc
	//in aree gia esplorate per minimizzare la distanza media dal cibo e mantenere un'area di visione

	public ExplorationAndMovement() {//TODO ?
		//if(!toUnexploredArea(Game.getUnexplored()))
		//	if(!toInvisibleArea( Game.getOutOfSight())) 
				if(!toPriorityTarget()) 
					spreadOut(Game.getOrderlyAnts());

	}

	private boolean toUnexploredArea(Set<Tile> unexploreted) {
		if(!unexploreted.isEmpty()) {
			//Search s = new Search(unexploreted, Game.getMyAnts(), null, false, false, true); //non utilizzare perche manda le formiche tutte ad uno stesso tile inexplorato
			//Search s = new Search(unexploreted, Game.getMyAnts(), null, false, true, false);
			Search s = new Search(Game.getMyAnts(), unexploreted, null, false, false, false);
			Set<Tile> targetCompleted = s.adaptiveSearch();
			Set<Order> orders = s.getOrders(); 

			Game.issueOrders(orders);
			unexploreted.removeAll(targetCompleted);


		}
		return Game.getMyAnts().isEmpty();
	}

	private boolean toInvisibleArea( Set<Tile> outOfSight) {
		if(!outOfSight.isEmpty()) {
			//Search s = new Search(myAnts, outOfSight, null, false, false, false);
			//Search s = new Search(outOfSight, Game.getMyAnts(), null, false, false, true); //sembra non funzionare
			Search s = new Search(outOfSight, Game.getMyAnts(), null, false, true, false);
			Set<Tile> targetCompleted = s.adaptiveSearch();
			Set<Order> orders = s.getOrders();

			Game.issueOrders(orders);

			outOfSight.removeAll(targetCompleted);


		}
		return Game.getMyAnts().isEmpty();
	}

	private boolean toPriorityTarget() {
		ArrayList<Set<Tile>> targets = new ArrayList<Set<Tile>>(3);
		targets.add(Game.getUnexplored());
		targets.add(Game.getEnemyHills());
		targets.add(Game.getEnemyAnts());

		int curTarget = 0;
		int countPathFounded = 1;

		while(!Game.getMyAnts().isEmpty() && !targets.get(curTarget).isEmpty() && countPathFounded!=0 ) {
			curTarget = curTarget%3;
			if(curTarget == 0)
				countPathFounded = 0;

			countPathFounded += computeOrders(targets.get(curTarget++)) ? 1 : 0;
		}

		return Game.getMyAnts().isEmpty();
	}

	//MASSIMIZZARE LA PERCENTUALE DI COPERTURA 
	private void spreadOut(Set<Tile> orderlyAnts) {
		//E' uguale a CombatSimulation.Hold

		Set<Tile> source = Game.getMyAnts();
		Set<Tile> targets = new TreeSet<Tile>();
		targets.addAll(source);
		targets.addAll(orderlyAnts);

		Iterator<Tile> antsItr = source.iterator();

		while(antsItr.hasNext()) {

			Tile ant = antsItr.next();
			Set<Tile> targetsWithoutAnt = new HashSet<Tile>(targets);

			targetsWithoutAnt.remove(ant);
			//targets.remove(ant);
			Set<Tile> singoletto = new TreeSet<Tile>();
			singoletto.add(ant);
			Search s = new Search(targets, singoletto, null, false, false, true);//BFS
			s.adaptiveSearch();

			Iterator<Order> orderIt = s.getOrders().iterator();
			if(orderIt.hasNext()) {
				Order o = orderIt.next();
				//TODO aggiungere next e next ...
				Game.issueOrder(o.withOpponentDirection());
			}

			//targets.add(ant);
		}	
	}
	/*
	 * targets.addAll(Game.getBorders()); FIXME se le formiche non vanno sui bordi
	 *
	 */

	private boolean computeOrders(Set<Tile> targets) {
		boolean pathFounded = true;
		while(!Game.getMyAnts().isEmpty() && !targets.isEmpty() && pathFounded) {

			//io gli farei fare un A* quindi heuristic = true, che dici?
			Search s = new Search(targets, Game.getMyAnts(), null, false, true, false);
			//Search s = new Search(targets, Game.getMyAnts(), null, false, false, true);
			Set<Tile> results = s.adaptiveSearch();
			Set<Order> orders = s.getOrders();
			if(orders.isEmpty())
				pathFounded = false;
			else {
				targets.removeAll(results);
				Game.issueOrders(orders);
			}			
		}
		return pathFounded;
	}

}
