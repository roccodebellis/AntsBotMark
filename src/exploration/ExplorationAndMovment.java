package exploration;
import java.util.Set;
import java.util.TreeSet;

import game.Game;
import game.Order;
import game.Tile;
import search.Search;
import vision.Vision;

public class ExplorationAndMovment {

	//obiettivo inviare formiche fuori dai confini di cio che è conosciuto
	//oppure inviare dove è necessario (battaglie in corso, punti chiave, etc
	//in aree gia esplorate per minimizzare la distanza media dal cibo e mantenere un'area di visione

	public ExplorationAndMovment() {
	
	}

	private void toUnexploredArea() {
		Search s = new Search(Game.getMyAnts(), Game.getUnexplored(), null, false, false);
		Set<Order> orders = s.adaptiveSearch(); //FIXME
		Game.issueOrders(orders);
	}

	private void toInvisibleArea() {
		Search s = new Search(Game.getMyAnts(), Game.getOutOfSight(), null, true, false);
		Set<Order> orders = s.adaptiveSearch();
		Game.issueOrders(orders);
	}

	
	private void toPriorityTarget() {
		Set<Tile> available = Game.getMyAnts();
		Set<Tile> targets = new TreeSet<Tile>();
		targets.addAll(Game.getUnexplored());//ma siamo sicuri non si riferisca ai bordi della mappa?
		targets.addAll(Game.getEnemyHills());
		targets.addAll(Game.getEnemyAnts());
		Boolean pathFounded = true;
		while(!available.isEmpty() && !targets.isEmpty() && pathFounded) {
			//io gli farei fare un A* quindi heuristic = true, che dici?
			Search s = new Search(targets, available, null, true, true);
			Set<Order> order = s.adaptiveSearch();//qui mi riferisco al set di orders

			if(order.isEmpty())
				pathFounded = false;
			else {
				//rimuovo i target che sono stati assegnati alle formiche
				targets.removeAll(s.adaptiveSearch());//qui mi riferisco al set di results
				order.parallelStream().forEach(o -> {available.remove(o.getTile());});//rimuovo le formiche a cui
				//ho assegnato un order
				Game.issueOrders(order);
			}			
		}
		/*
		while(aviableAnts is Empty || targhets is Empty || no path can be found) { //molto probabilmente
															//no path indica che raggiunto un tempo limite 
															//non si è prodotto nessun percorso/assegnamento (alla formica)

			target = uneploredArea + identify enemy ants hill + visible enemy ants

			Search s = new Search(target, availableAnts, null, false, true);
			//BFS ???
			adaptiveSearch();


		}*/
	}
	
	//MASSIMIZZARE LA PERCENTUALE DI COPERTURA 
	
	spreadOut(){
		//bisogna modificare una funzione 
		//in modo da mandare le formiche verso i bordi 
		//piu formiche verso un obiettivo 
		
		
		se ci sono formiche aviableAnts
			spreadOut to maximizing the distance between tow ant //TODO mandare le formiche verso i bordi
																 // o verso zone non visibili
	}
	
	hillAnts(){
		di base le sposta sempre ma se 
		
		per ogni hill
		vedi se ci sta una formica dentro/sopra
		se nel (raggi di visione +3) di quella formica ci stanno formiche nemiche 
			e del cibo è stato raccolto nel turno precedente 
				manda ad uccidere la formica nemica 
			se non è stato raccolto cibo 
				fai rimanere la formica sul nido 
				
	}
}
