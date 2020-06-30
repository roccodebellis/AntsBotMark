package exploration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import game.Directions;
import game.Game;
import game.Order;
import game.Tile;
import search.Search;
import vision.Vision;

public class ExplorationAndMovement {

	//obiettivo inviare formiche fuori dai confini di cio che e' conosciuto
	//oppure inviare dove e' necessario (battaglie in corso, punti chiave, etc
	//in aree gia esplorate per minimizzare la distanza media dal cibo e mantenere un'area di visione

	public ExplorationAndMovement() {//TODO ?
		if(!toUnexploredArea())
			if(!toInvisibleArea()) 
				if(!toPriorityTarget()) 
					spreadOut();

	}

	private boolean toUnexploredArea() {
		Search s = new Search(Game.getMyAnts(), Game.getUnexplored(), null, false, false);
		s.adaptiveSearch();
		Set<Order> orders = s.getOrders(); 

		Game.issueOrders(orders);

		return Game.getMyAnts().isEmpty();
	}

	private boolean toInvisibleArea() {
		Search s = new Search(Game.getMyAnts(), Game.getOutOfSight(), null, false, false);
		s.adaptiveSearch();
		Set<Order> orders = s.getOrders();

		Game.issueOrders(orders);
		return Game.getMyAnts().isEmpty();
	}

	private boolean toPriorityTarget() {
		ArrayList<Set<Tile>> targets = new ArrayList<Set<Tile>>(3);
		targets.get(0).addAll(Game.getUnexplored());
		targets.get(1).addAll(Game.getEnemyHills());
		targets.get(2).addAll(Game.getEnemyAnts());

		int curTarget = 0;
		int countPathFounded =0;

		while(!Game.getMyAnts().isEmpty() && targets.get(curTarget).isEmpty() && countPathFounded != 3) {
			curTarget = curTarget%3;
			if(curTarget == 0)
				countPathFounded =0;

			countPathFounded += computeOrders(targets.get(curTarget++)) ? 0 : 1;
		}
		return Game.getMyAnts().isEmpty();
	}

	//MASSIMIZZARE LA PERCENTUALE DI COPERTURA 
	private void spreadOut() {
		//E' uguale a CombatSimulation.Hold

		Set<Tile> source = Game.getMyAnts();
		Set<Tile> targets = new TreeSet<>();
		targets.addAll(Game.getMyAnts());
		targets.addAll(Game.getOrderlyAnts());

		Iterator<Tile> antsItr = source.iterator();

		while(antsItr.hasNext()) {

			Tile ant = antsItr.next();
			targets.remove(ant);
			Set<Tile> singoletto = new TreeSet<Tile>();
			singoletto.add(ant);
			Search s = new Search(singoletto, targets, null, false, false);//BFS
			s.adaptiveSearch();
			Order o = s.getOrders().iterator().next();

			Game.issueOrder(o.withOpponentDirection());

			targets.add(ant);
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
			Search s = new Search(targets, Game.getMyAnts(), null, true, true);
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
