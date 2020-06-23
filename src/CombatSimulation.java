import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>Questo modulo processa tutte le situazioni in cui le formiche degli agenti incontrano formiche nemiche
 * ad una certa distanza.<br>
 * Il modulo provera' a determinare le migliori mosse per le formiche dell'agente in una data situazione
 * usando una ricerca spazio-tempo, simulando le possibili mosse che potrebbero compiere l'agente
 * ed il nemico e valutando le situazioni risultanti.</p>
 * <p>In questo modulo le formiche vengono raggruppate in clusters simulando, in modo sincronizzato,
 * le mosse del gruppo.<br>
 * {@code CombatSimulation} consiste di quattro elementi principali:<ul>
 * <li><b>Blocco di riconoscimento delle situazioni</b>: dopo aver identificato una potenziale
 * situazione di combattimento, trova gruppi di formiche nemiche;</li>
 * <li><b>Generatore di mosse</b>: genera le possibili mosse che devono eseguire i gruppi
 * di formiche;</li>
 * <li><b>Funzione di valuatazione</b>: assegna un valore ad una data situazione in base
 * alle perdite (sia dell'agente che del nemico) ed in base ai valori euristici;</li>
 * <li><b>Algoritmo di ricerca principale</b>: combina questi elementi per identificare
 * la migliore mossa per l'agente in una situazione di combattimento.</li>
 * </ul>
 * </p>
 * @author Debellis, Lorusso
 *
 */
public class CombatSimulation {

	private Set<Tile> myAntSet = new HashSet<>();
	private Set<Tile> enemyAntSet = new HashSet<>();
	/**
	 * Dopo aver incontrato una formica nemica nel range di visione di una delle formiche
	 * dell'agente, {@link CombatSimulation} provera' per prima cosa a trovare tutte le formiche
	 * che potrebbero partecipare alla potenziale battaglia.<br>
	 * L'algoritmo si basa sul <i>flood-fill</i> (L'algoritmo Flood fill individua un punto
	 * all'interno dell'area e, a partire da quel punto, colora tutto quello che ha intorno
	 * fermandosi solo quando incontra un confine, ovvero un pixel di colore differente)
	 * utilizzando una ricerca adattiva a raggio limitato ad ogni passo di espansione.<br>
	 * Funziona come segue:<ul>
	 * <li>Inizializza {@link #myAntSet} con la formica di origine;</li>
	 * <li>Inizializza {@link #enemyAntSet} con la formica di origine nemica;</li>
	 * <li>Ripetere i seguenti punti sino a che non ci sono piu' formiche da aggiungere:<ul></ul>
	 * <li>Esegui una ricerca adattiva ({@link Search#adaptiveSearch adaptiveSearch}
	 * il cui insieme di origine delle formiche e' {@link #myAntSet} andando alla ricerca di
	 * nuovi nemici, limitando il range del raggio a 3*attackRadius. Aggiungi tutte le formiche nemiche
	 * trovate a {@link #enemyAntSet};</li>
	 * <li>Esegui una ricerca adattiva ({@link Search#adaptiveSearch adaptiveSearch}
	 * il cui insieme di origine delle formiche e' {@link #enemyAntsSet} che vanno alla ricerca
	 * delle formiche dell'agente, limitando il range del raggion a 3*attackRadius.
	 * Aggiungi tutte le formiche trovate in {@link #myAntSet};</li>
	 * </li>
	 * </ul>
	 */
	public void situationRecognition(Tile myAnt, Tile enemy){
		//TROVARE UN MODO PER SAPERE SE LA FORMICA POSIZIONATA SUL TILE è disponibile 
		// no, claudia isOccupied dice solo se quella tile è occupata da una formica!
		myAntSet.add(myAnt);
		enemyAntSet.add(enemy);

		int attackRadius = getAttackRadius()*3;

		finche non ci sono piu formiche da aggiungere{
			** = new Search(myAntSet, enemyAntSet, attackRadius, true, false);
			//search.adaptiveSearch(myAntSet, enemyAntSet);
			** = new Search(enemyAntSet, myAntSet, attackRadius, true, false);
			//search.adaptiveSearch(enemyAntSet, myAntSet);
		}
	}

	/**
	 * <p>Il generatore di mosse e' la parte del modulo di simulazione di combattimento che
	 * genera le possibili mosse da uno dei gruppi nemici in una data situazione.<br>
	 * Le mosse generate sono simulate in una data situazione, generando un insieme di stati
	 * figli.</p>
	 * <p>Questi stati sono poi valutati da una funzione di valutazione e viene in seguito scelta
	 * la mossa migliore.<br> Il generatore di mosse e' in grado di generare i seguenti tipi di mosse:
	 * <ul>
	 * <li><b>Attack</b>: nella mossa di attacco, ogni formica di una fazione
	 * inizializza una ricerca alla scoperta della formica nemica piu' vicina o del nido nemico
	 * piu' vicino e prova ad eseguire un passo nella direzione del nemico;</li>
	 * <li><b>Hold</b>: la formica prova a restare ad una esatta distanza dalle formiche
	 * nemiche in modo da restare fuori dalla distanza per l'inizio del combattimento.<br>
	 * questa distanza sara' AttackRadius+2 per tutte le formiche degli agenti
	 * (assumendo che la fazione nemica possa effettuare una mossa prima della risoluzione
	 * della battaglia) e AttackRadius+1 per le formiche nemiche (poiche' la risoluzione della battaglia
	 * ha luogo immediatamente dopo la mossa delle formiche nemiche).<br>
	 * Questa mossa implementa sia una mossa di avanzamento sicura che una mossa di ritirata sicura (in una),
	 * poiche' dipende dalla distanza corrente dal nemico, le formiche avanzeranno o andranno in ritirata
	 * per raggiungere la distanza richiesta.</li>
	 * <li><b>Idle</b>: nessuna formica del gruppo sara' mossa</li>
	 * <li><b>Directional</b>: le quattro mosse direzionali proveranno semplicemente a muovere tutti le
	 * formiche dell'agente (o quelle nemiche) in una direzione - N S E o W.</li>
	 * </ul>
	 * </p>
	 */
	private void moveGenerator(){

	}

	/**
	 * 
	 * aggiungere quando veine effettuata la traduzione che un punteggio di 1 eè assegnato se un nido
	 * nemico viene distruto
	 */
	Double private Evaluate( state) {
		Double MyLossMultiplier = 1.1D;
		Double EnemyLossMultuplier = 1.0D;

		//TODO impostare mass radio in base al numero di formiche
		if (state.getMyAntsNumber() > MassRatioThreshold) {
			MassRatio = Math.max(1,Math.pow((state.getMyAntsNumber()+1)/(state.getEnemyAntsNumber()+1),2));
			EnemyLossMultuplier * = MassRatio; 
		}

		//TODO crescita logaritmica col passare dei turni a partire da una certa soglia
		if(state.getTurnLeft()<50) 
			EnemyLossMultuplier *=1,5D;

		value = EnemyLossMultuplier * state.getEnemyLossesNumber() - MyLossMultiplier * state.getMyLossesNumber();

		if(state.getMyLossesNumber() == state.getMyAntsNumber())
			value -= 0.5;
		else if (state.getEnemyLossesNumber() == state.getEnemyAntsNumber())
			value += 0.4;

			//TODO RISCRIVERE FUNZIONE 
			//considerando un pareggio 
			//considerando in caso di pareggio se il numero di formiche uccise da me è maggiore 
			//del numero di formiche perse

		value += state.getEnemyHillDestroyedNumber();
		value -= state.getMyHillDestroyedNumber() * 5;
		
		value += state.getFoodCollectedFromMyants() /2;
		value -= state.getFoodCollectedFromEnemy();
		
		
		return value;
	}



	MinMax(state, deadLine, deepth = 0) {
		if(!AllowExtension(deadLine, depht))
			return evaluate(state);
		
		Moves = generateMoves(state);
		foreach(Move move: Moves){
			childState = performMove(state,move);
			childDeadline = getCurTime() + (DeadLine-getCurrentTime)/(GetNumberOfMoves-GetMoveNumber(move));
			
			if(isEnemyMove(childState))
				resolveCombatAndFoodCollection(childState);
			
			MinMax(ChildState, ChildDeadline, Depth+1);
			
			state.addChild(childState);
		}
		
	}

	boolean AllowExtension(dedline, depth){
		return (depth%2 == 0) || (depth < maximundepth && deadline > getCurrentTime+GetExtensikonEstimate); 
	}






}
