package exploration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import game.Directions;
import game.Game;
import game.Order;
import game.Tile;
import search.Search;

public class ExplorationAndMovement {

	// obiettivo inviare formiche fuori dai confini di cio che e' conosciuto
	// oppure inviare dove e' necessario (battaglie in corso, punti chiave, etc
	// in aree gia esplorate per minimizzare la distanza media dal cibo e mantenere
	// un'area di visione

	public ExplorationAndMovement() {// TODO ?
		//le parentesi graffe le ho lasciate in modo che se dobbiamo provare
		//singoli task ci basta mettere a commento cio' che non serve
		if(!toUnexploredArea(Game.getUnexplored())) {}
		if(!toInvisibleArea(Game.getOutOfSight())) {}
		if (!toPriorityTarget()) {
		}
		spreadOut(Game.getOrderlyAnts());

	}

	private boolean toUnexploredArea(Set<Tile> unexplored) {
		if (!unexplored.isEmpty()) {
			// Search s = new Search(unexplored, Game.getMyAnts(), null, false, false,
			// true); //non utilizzare perche manda le formiche tutte ad uno stesso tile
			// inexplorato
			// Search s = new Search(unexplored, Game.getMyAnts(), null, false, true,
			// false);
			Search s = new Search(Game.getMyAnts(), unexplored, null, false, false, false);
			Set<Tile> targetCompleted = s.adaptiveSearch();
			Set<Order> orders = s.getOrders();

			Set<Order> withoutHill = doNotStepOnMyHills(orders);
			Game.issueOrders(withoutHill);

			// Game.issueOrders(orders);

			unexplored.removeAll(targetCompleted);

		}
		return Game.getMyAnts().isEmpty();
	}

	private boolean toInvisibleArea(Set<Tile> outOfSight) {
		if (!outOfSight.isEmpty()) {
			// Search s = new Search(myAnts, outOfSight, null, false, false, false);
			// Search s = new Search(outOfSight, Game.getMyAnts(), null, false, false,
			// true); //sembra non funzionare
			Search s = new Search(outOfSight, Game.getMyAnts(), null, false, true, false);
			Set<Tile> targetCompleted = s.adaptiveSearch();
			Set<Order> orders = s.getOrders();

			Set<Order> withoutHill = doNotStepOnMyHills(orders);
			Game.issueOrders(withoutHill);

			// Game.issueOrders(orders);

			outOfSight.removeAll(targetCompleted);
		}
		return Game.getMyAnts().isEmpty();
	}

	private boolean toPriorityTarget() {
		ArrayList<Set<Tile>> targets = new ArrayList<Set<Tile>>();

		if (!Game.getUnexplored().isEmpty())
			targets.add(Game.getUnexplored());
		if (!Game.getEnemyHills().isEmpty())
			targets.add(Game.getEnemyHills());
		if (!Game.getEnemyAnts().isEmpty())
			targets.add(Game.getEnemyAnts());

		int curTarget = 0;

		int size = targets.size();

		// Boolean pathFounded = false;
		// int priorityCount = 0;
		
		//TODO
		/*  do {
			 computeOrders(targets.get(curTarget++));
		 } while (curTarget!=size);
		 */
		
		
		int countPathFounded = 1;
		// IndexOutOfBoundsException: Index: 2, Size: 2 PERCHE'?? l'ha dato solo una volta dopo le modifiche
		//le altre volte ha funzionato benissimo
		while (!Game.getMyAnts().isEmpty() && !targets.get(curTarget).isEmpty() && countPathFounded != 0) {
			curTarget = curTarget % size;
			if (curTarget == 0)
				countPathFounded = 0;
			countPathFounded += computeOrders(targets.get(curTarget++)) ? 1 : 0;
		}
		
		
		
		/*
		 * do { pathFounded = computeOrders(targets.get(curTarget)); if (pathFounded)
		 * priorityCount++; curTarget++; if (curTarget != size) pathFounded = true; else
		 * if (priorityCount == 0) pathFounded = false; else { pathFounded = false; } }
		 * while (pathFounded);
		 */
		return Game.getMyAnts().isEmpty();
	}

	// MASSIMIZZARE LA PERCENTUALE DI COPERTURA
	private void spreadOut(Set<Tile> orderlyAnts) {
		// E' uguale a CombatSimulation.Hold

		Set<Tile> myAnts = Game.getMyAnts();
		Set<Tile> targets = new TreeSet<Tile>();

		targets.addAll(myAnts);
		targets.addAll(orderlyAnts);

		Iterator<Tile> antsItr = myAnts.iterator();

		while (antsItr.hasNext()) {

			Tile ant = antsItr.next();
			Set<Tile> targetsWithoutAnt = new HashSet<Tile>(targets);

			targetsWithoutAnt.remove(ant);

			// targets.remove(ant);

			Set<Tile> singoletto = new TreeSet<Tile>();
			singoletto.add(ant);

			Search s = new Search(singoletto, targets, null, false, false, false);// da singoletto alla formica piu'
																					// vicina non il contrario

			// Search s = new Search(targets, singoletto, null, false, false, true);//BFS
			s.adaptiveSearch();
			
			Iterator<Order> orderIt = s.getOrders().iterator();
			if (orderIt.hasNext()) {
				Set<Order> toIssue = new HashSet<Order>();
				
				Order o = orderIt.next();
				Directions dir = o.getDirection();
				
				if (ant.getNeighbour().containsKey(dir.getOpponent()))
					toIssue.add(o.withOpponentDirection());
				else if(ant.getNeighbour().containsKey(dir.getOpponent().getNext()))
					toIssue.add(new Order(ant, dir.getOpponent().getNext()));
				else if(ant.getNeighbour().containsKey(dir.getOpponent().getNext().getOpponent()))
					toIssue.add(new Order(ant, dir.getOpponent().getNext().getOpponent()));
				toIssue = doNotStepOnMyHills(toIssue);
				Game.issueOrders(toIssue);
			}

			// targets.add(ant);
		}
	}
	
	/*
	 * targets.addAll(Game.getBorders()); FIXME se le formiche non vanno sui bordi
	 *
	 */

	private boolean computeOrders(Set<Tile> targets) {
		boolean pathFounded = true;
		while (!Game.getMyAnts().isEmpty() && !targets.isEmpty() && pathFounded) {

			// io gli farei fare un A* quindi heuristic = true, che dici?
			Search s = new Search(Game.getMyAnts(), targets, null, false, false, false);
			// Search s = new Search(targets, Game.getMyAnts(), null, false, false, true);
			//non va bene quella di sopra, se dobbiamo utilizzare quella dobbiamo farci restituire
			//le tile di order
			Set<Tile> results = s.adaptiveSearch();
			Set<Order> orders = s.getOrders();
			if (orders.isEmpty())
				pathFounded = false;
			else {
				targets.removeAll(results);
				// Game.issueOrders(orders);
				Set<Order> withoutHill = doNotStepOnMyHills(orders);
				Game.issueOrders(withoutHill);
			}
		}
		return pathFounded;
	}

	private Set<Order> doNotStepOnMyHills(Set<Order> orders) {
		Set<Order> withoutHill = new HashSet<Order>();
		orders.parallelStream().forEachOrdered(o -> {
			if (!Game.getMyHills().contains(o.getOrderedTile())) {
				withoutHill.add(o);
			}
		});

		return withoutHill;
	}

}
