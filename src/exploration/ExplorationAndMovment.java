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

public class ExplorationAndMovment {

	//obiettivo inviare formiche fuori dai confini di cio che e' conosciuto
	//oppure inviare dove e' necessario (battaglie in corso, punti chiave, etc
	//in aree gia esplorate per minimizzare la distanza media dal cibo e mantenere un'area di visione

	public ExplorationAndMovment() {
		
	}

	private void toUnexploredArea() {
		Search s = new Search(Game.getMyAnts(), Game.getUnexplored(), null, false, false);
		Set<Order> orders = s.adaptiveSearch(); //FIXME idea, sino ad ora ci siamo resi conto che
												//abbiamo bisogno soltanto si Set<Order> e del target per ogni order: possiamo aggiungere all'order
												//una variabile del Tipo: Tile target che se lo conservi
		//preventSteppingOwnHill(orders);FIXME 
		Game.issueOrders(orders);
	}

	private void toInvisibleArea() {
		Search s = new Search(Game.getMyAnts(), Game.getOutOfSight(), null, false, false);
		Set<Order> orders = s.adaptiveSearch();
		//preventSteppingOwnHill(orders);FIXME
		
		Game.issueOrders(orders);
	}
	
	private void toPriorityTarget() {
		ArrayList<Set<Tile>> targets = new ArrayList<Set<Tile>>(3);
		targets.get(0).addAll(Game.getUnexplored());
		targets.get(1).addAll(Game.getEnemyHills());
		targets.get(2).addAll(Game.getEnemyAnts());
		
		int i = 0;
		while(!Game.getMyAnts().isEmpty() && targets.get(i).isEmpty()) {
			i = i%3;
			issueOrders(targets.get(i++));
		}
	}
	
	//MASSIMIZZARE LA PERCENTUALE DI COPERTURA 
	private void spreadOut() {
		//E' uguale a CombatSimulation.Hold
		
		Set<Tile> source = Game.getMyAnts();
		Set<Tile> targets = new TreeSet<>();
		targets.addAll(Game.getMyAnts());
		targets.addAll(Game.getOrderlyAnts());
		
		Set<Order> orders = new TreeSet<Order>();
		
		Iterator<Tile> antsItr = source.iterator();
		
		while(antsItr.hasNext()) {

			Tile ant = antsItr.next();
			targets.remove(ant);
			Set<Tile> singoletto = new TreeSet<Tile>();
			singoletto.add(ant);
			Search s = new Search(singoletto, targets, null, false, false);//BFS
			res = s.adaptiveSearch();
			Order o = res;
			
			Game.issueOrder(o.withOpponentDirection());
			
			targets.add(ant);
		}	
	}
	/*
	 * targets.addAll(Game.getBorders()); FIXME se le formiche non vanno sui bordi
	 *
	 */
	
	private void issueOrders(Set<Tile> targets) {
		Boolean pathFounded = true;
		while(!Game.getMyAnts().isEmpty() && !targets.isEmpty() && pathFounded) {
			//io gli farei fare un A* quindi heuristic = true, che dici?
			Search s = new Search(targets, Game.getMyAnts(), null, true, true);
			Set<Order> order = s.adaptiveSearch();
			if(order.isEmpty())
				pathFounded = false;
			else {
				targets.removeAll(s.getResults());
				Game.issueOrders(order);
			}			
		}
	}

}
