package exploration;
import search.Search;
import vision.Vision;

public class ExplorationAndMovment {

	//obiettivo inviare formiche fuori dai confini di cio che è conosciuto
	//oppure inviare dove è necessario (battaglie in corso, punti chiave, etc
	//in aree gia esplorate per minimizzare la distanza media dal cibo e mantenere un'area di visione


	inviare formiche in aree non esplorate

	toUnexploredArea() {
		Search s = new Search(availableAnts, targetOfAllUnexploratedArea, null =radius, true, false);
		//BFS
		adaptiveSearch();
	}

	toInvisibleArea() {
		targhetInvisible = Vision.getTargets();
		Search s = new Search(availableAnts, targhetInvisible, null =radius, true, false);
		//BFS ???
		adaptiveSearch();
	}

	toPriorityTarghet() {
		while(aviableAnts is Empty || targhets is Empty || no path can be found) { //molto probabilmente
															//no path indica che raggiunto un tempo limite 
															//non si è prodotto nessun percorso/assegnamento (alla formica)

			target = uneploredArea + identify enemy ants hill + visible enemy ants

			Search s = new Search(target, availableAnts, null =radius, false, true);
			//BFS ???
			adaptiveSearch();


		}
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
