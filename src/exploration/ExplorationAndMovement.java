package exploration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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

	private static Logger LOGGER = Logger.getLogger( ExplorationAndMovement.class.getName() );

	public ExplorationAndMovement() {// TODO ?
		LOGGER.info("ExplorationAndMovement()");
		LOGGER.info("ExplorationAndMovement():toUnexploredArea()");
		if(!toUnexploredArea(Game.getUnexplored())) {
			LOGGER.info("~ExplorationAndMovement():toUnexploredArea()");
			LOGGER.info("ExplorationAndMovement():toInvisibleArea()");
			if(!toInvisibleArea(Game.getOutOfSight())) {
				LOGGER.info("~ExplorationAndMovement():toInvisibleArea()");
				LOGGER.info("ExplorationAndMovement():toPriorityTarget()");
				if (!toPriorityTarget()) {
					LOGGER.info("~ExplorationAndMovement():toPriorityTarget()");
					LOGGER.info("ExplorationAndMovement():spreadOut()");
					spreadOut(Game.getOrderlyAnts());
					LOGGER.info("~ExplorationAndMovement():spreadOut()");
				}

			}
		}
		LOGGER.info("~ExplorationAndMovement()");
	}

	private boolean toUnexploredArea(Set<Tile> unexplored) {

		//LOGGER.info("unexplored: " + unexplored); 


		if (unexplored!=null && !unexplored.isEmpty() && !Game.getMyAnts().isEmpty()) {
			//LOGGER.info( " MyAnts: " +  Game.getMyAnts());
			// Search s = new Search(unexplored, Game.getMyAnts(), null, false, false,
			// true); //non utilizzare perche manda le formiche tutte ad uno stesso tile
			// inexplorato

			//Search s = new Search(Game.getMyAnts(), unexplored, null, false, false, false);
			//da valutare, il problema e' che all'inizio le tile inesplorate sono tantissime
			//ma sarebbe meglio mandare solo una formica ad un'unica tile inesplorata

			//BFS
			//Search s = new Search( unexplored, Game.getMyAnts(), null, false, true, false);
			//Set<Tile> results =  s.adaptiveSearch(); 
			Search s;
			Set<Tile> results;
			if(unexplored.size()<15) {
				s = new Search( unexplored, Game.getMyAnts(), null, true, false, true);
				results = s.EAStarSearch();
			} else {
				//s = new Search( Game.getMyAnts(),unexplored, null, true, false, false);
				s = new Search( Game.getMyAnts(),unexplored, null, false, false, false);
				results =  s.adaptiveSearch();
			}
			

			Set<Order> orders = s.getOrders();
			//issueAndRemoveOrders(orders, unexplored);
			LOGGER.info("results: " + results); 
			Game.issueOrders(orders);
			unexplored.removeAll(results);
		}
		return Game.getMyAnts().isEmpty();
	}

	private boolean toInvisibleArea(Set<Tile> outOfSight) {
		if (!outOfSight.isEmpty()) {
			// Search s = new Search(myAnts, outOfSight, null, false, false, false);
			// Search s = new Search(outOfSight, Game.getMyAnts(), null, false, false,
			// true); //sembra non funzionare

			Search s = new Search(outOfSight, Game.getMyAnts(), null, false, true, false);
			Set<Tile> results = s.adaptiveSearch();

			Set<Order> orders = s.getOrders();

			//issueAndRemoveOrders(orders, outOfSight);
			outOfSight.removeAll(results);
			Game.issueOrders(orders);
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

		int countPathFounded = 1;
		//TODO
		// IndexOutOfBoundsException: Index: 2, Size: 2 PERCHE'?? l'ha dato due volte dopo le modifiche
		//non lo da' sempre, solo quando ci sono troppe formiche ed e' in vantaggio
		//le altre volte ha funzionato benissimo
		//da 52 formiche in su eccezione
		//da 43 in su time-out

		//while (!targets.get(curTarget).isEmpty() && countPathFounded != 0) {

		while (!Game.getMyAnts().isEmpty() && countPathFounded != 0) {	
			//0%3= 0
			//1%3= 1
			//2%3= 2
			//3%3= 0
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



		List<Tile> myAnts = new ArrayList<>(Game.getMyAnts());
		Set<Tile> targets = new TreeSet<Tile>();

		targets.addAll(myAnts);
		targets.addAll(orderlyAnts);

		//Tile ant = null;

		int i = 0;
		while(i<myAnts.size()) {
			Tile ant = myAnts.get(i);
			//for(Tile ant : myAnts) {
			Set<Tile> targetsWithoutAnt = targets.parallelStream().filter(curAnt -> !curAnt.equals(ant)).collect(Collectors.toSet());

			Set<Tile> singoletto = new TreeSet<Tile>();
			singoletto.add(ant);

			Search s = new Search(singoletto, targetsWithoutAnt, null, false, false, false);// da singoletto alla formica piu'
			// vicina non il contrario

			// Search s = new Search(targets, singoletto, null, false, false, true);//BFS
			s.adaptiveSearch();

			Iterator<Order> orderIt = s.getOrders().iterator();
			if (orderIt.hasNext()) {
				Set<Order> toIssue = new HashSet<Order>();

				Order o = orderIt.next();
				Directions dir = o.getDirection();
				//TODO da controllare ma penso stia bene
				if (ant.getNeighbours().contains(dir.getOpponent()))
					toIssue.add(o.withOpponentDirection());
				else if(ant.getNeighbours().contains(dir.getOpponent().getNext()))
					toIssue.add(new Order(ant, dir.getOpponent().getNext(), Game.getTile(ant, dir.getOpponent().getNext().getOffset())));
				else if(ant.getNeighbours().contains(dir.getOpponent().getNext().getOpponent()))
					toIssue.add(new Order(ant, dir.getOpponent().getNext().getOpponent(), Game.getTile(ant, dir.getOpponent().getNext().getOpponent().getOffset())));
				Game.issueOrders(toIssue);
			}
			i++;
		}




		/*Iterator<Tile> antsItr = myAnts.iterator();
		while (antsItr.hasNext()) {


			Tile ant = antsItr.next();

			/*Iterator<Tile> targetsWithoutAntItr = targets.iterator();
			Set<Tile> targetsWithoutAnt = new HashSet<Tile>();
			while(targetsWithoutAntItr.hasNext()) {
				Tile t = targetsWithoutAntItr.next();
				if(!t.equals(ant))
					targetsWithoutAnt.add(t);
			}*


			Set<Tile> targetsWithoutAnt = targets.parallelStream().filter(curAnt -> !curAnt.equals(ant)).collect(Collectors.toSet());

			Set<Tile> singoletto = new TreeSet<Tile>();
			singoletto.add(ant);

			Search s = new Search(singoletto, targetsWithoutAnt, null, false, false, false);// da singoletto alla formica piu'
			// vicina non il contrario

			// Search s = new Search(targets, singoletto, null, false, false, true);//BFS
			s.adaptiveSearch();

			Iterator<Order> orderIt = s.getOrders().iterator();
			if (orderIt.hasNext()) {
				Set<Order> toIssue = new HashSet<Order>();

				Order o = orderIt.next();
				Directions dir = o.getDirection();
				//TODO da controllare ma penso stia bene
				if (ant.getNeighbour().containsKey(dir.getOpponent()))
					toIssue.add(o.withOpponentDirection());
				else if(ant.getNeighbour().containsKey(dir.getOpponent().getNext()))
					toIssue.add(new Order(ant, dir.getOpponent().getNext()));
				else if(ant.getNeighbour().containsKey(dir.getOpponent().getNext().getOpponent()))
					toIssue.add(new Order(ant, dir.getOpponent().getNext().getOpponent()));
				Game.issueOrders(toIssue);
			}

			// targets.add(ant);
		}*/

	}

	/*
	 * targets.addAll(Game.getBorders()); FIXME se le formiche non vanno sui bordi
	 *
	 */

	//TODO secondo me qui one_per_source, per evitare il time-out
	private boolean computeOrders(Set<Tile> targets) {
		boolean pathFounded = true;
		while (!Game.getMyAnts().isEmpty() && !targets.isEmpty() && pathFounded) {

			// io gli farei fare un A* quindi heuristic = true, che dici?
			

			//Search s = new Search(targets,Game.getMyAnts(),  null, false, true, false);
			
			// Search s = new Search(targets, Game.getMyAnts(), null, false, false, true);
			//non va bene quella di sopra, se dobbiamo utilizzare quella dobbiamo farci restituire
			//le tile di order
			
			//Set<Tile> results = s.EAStarSearch();
			/*s.adaptiveSearch();
			Set<Tile> results = s.getOrderTile(); */
			
			//A* non funziona, va in timeout for no reason
			
			//A* normale
			/*Search s = new Search(Game.getMyAnts(), targets, null, true, false, false);
			Set<Tile> results = s.EAStarSearch();
			*/
			
			//A* otps
			/*Search s = new Search(targets, Game.getMyAnts(), null, true, true, false);
			Set<Tile> results = s.EAStarSearch();*/
			
			
			//BFS otps
			/*Search s = new Search(targets, Game.getMyAnts(), null, false, true, false);
			Set<Tile> results = s.adaptiveSearch();
			*/
			
			//BFS normale
			/**/Search s = new Search(Game.getMyAnts(), targets, null, false, false, false);//questo funziona bene
			Set<Tile> results = s.adaptiveSearch();
			
			
			Set<Order> orders = s.getOrders();
			LOGGER.info("Results: " + results);
			LOGGER.info("Orders: " + orders);
			if (orders.isEmpty())
				pathFounded = false;
			else pathFounded = issueAndRemoveOrders(orders, targets);//targets.removeAll(results);
				//issueAndRemoveOrders(orders, targets);
				
			//Game.issueOrders(orders);

		}
		return pathFounded;
	}
	
	private boolean issueAndRemoveOrders(Set<Order> orders, Set<Tile> targets) {
		boolean pathFounded = false;
		boolean issued = false;
		for(Order o : orders) {
			LOGGER.info("Order first: " + o);
			issued=Game.issueOrder(o);
			if(!pathFounded && issued)
				pathFounded = true;
			targets.remove(o.getTarget());
		}
		LOGGER.info("pathFounded: " + pathFounded);
		return pathFounded;
	}


}
