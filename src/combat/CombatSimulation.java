package combat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import game.Directions;
import game.Game;
import game.Order;
import game.Tile;
import search.Search;
import vision.Offset;

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

		int attackRadius = Game.getAttackRadius() * 3;

		//FIXME ciclare solo sulle formiche aggiunte, prendere la diffrenza tra l'intersezione e l'insisme nuovo
		//e considerare quelle formiche per la successiva iterata aggiungendole cmq alla lista delle myAntsSet

		boolean addedAnts = false;
		while(addedAnts){
			addedAnts = false;
			Search forEnemyAnts = new Search(myAntSet, Game.getEnemyAnts(), attackRadius, false, false);
			Set<Tile> newEnemyFound = forEnemyAnts.adaptiveSearch();
			if(newEnemyFound.size()> enemyAntSet.size()) {
				addedAnts = true;
				enemyAntSet = newEnemyFound;
			}

			Search forMyAnts = new Search(enemyAntSet, Game.getMyAnts(), attackRadius, false, false);
			Set<Tile> newMyAntsFound = forMyAnts.adaptiveSearch();
			if(newMyAntsFound.size()> myAntSet.size()) {
				addedAnts = true;
				myAntSet = newMyAntsFound;
			}
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
	private List<Order> moveGenerator(State s) {
		List<Order> output = new LinkedList<Order>();
		switch(move) {
		case ATTACK:
			attack();
			break;
		}

		return output;
	}

	Map<MovesModels, State> assignments = new TreeMap<>();
	//per ogni moveModel

	private Set<Order> attack(State s) {
		Set<Tile> targets = new HashSet();
		targets.addAll(s.getEnemyAnts());
		targets.addAll(s.getEnemyHills());

		Search search = new Search(s.getMyAnts(), targets, null, false, false);

		return search.adaptiveSearch();
	}


	private Set<Order> hold(State s) {
		double targetDistance = isEnemyMove(s) ? AttackRadius+1 : AttackRadius+2;
		
		
		Iterator<Tile> targetsItr = targets.iterator();

		if(targetsItr.hasNext()) {
			Tile minTarget = targetsItr.next();
			int minDist = Game.getDistance(minTarget,tile);

			while (targetsItr.hasNext()) {
				Tile next = targetsItr.next();
				int nextDist = Game.getDistance(next,tile);
				if (nextDist < minDist) {
					minTarget = next;
					minDist = nextDist;
				}
			}
			heuristicValue = minDist;
			target = minTarget;
		}

		Search search = new Search(s.getMyAnts(),s.getEnemyAnts(),null,false,false);
		targhetTiles = search.adaptiveSearch();

		search = new Search(s.getMyAnts(),targhetTiles,null,false,false);
		return search.adaptiveSearch();
	}

	private Set<Order> idle(State s) {
		return s.getMyAnts().parallelStream().map(ant -> new Order(ant,Directions.STAYSTILL)).collect(Collectors.toSet());
	}

	private Set<Order> directional(State s, Direction m) {
		//m deve essere NORD SUD EST OVEST
		Set<Order> orders = new HashSet();

		s.getMyAnts().forEach(a -> {
			Tile target = a.getNeighbour().get(m);
	
			if(target.equals(null)) //acqua
				if(orders.contains(a)) //order.getTile == a
					dovecazzo ti mando ??;
				else
					orders.add(new Order(a,STAYSTILL));
			else 
				if(orders.contains(targhet))
					if(orders.contains(a))
						doveti mando;
					else
						orders.add(new Order(a,STAYSTILL));
				else
					orders.add(new Order(a,m));
		});
	}

	/**
	 * 
	 * FIXME aggiungere quando veine effettuata la traduzione che un punteggio di 1 eè assegnato se un nido
	 * nemico viene distruto
	 */
	private double Evaluate(State s) {
		Double MyLossMultiplier = 1.1D;
		Double EnemyLossMultuplier = 1.0D;

		double value;

		//TODO impostare mass radio in base al numero di formiche
		if (s.getMyAntsNumber() > MassRatioThreshold) {
			double massRatio = Math.max(1,Math.pow((s.getMyAntsNumber()+1)/(s.getEnemyAntsNumber()+1),2));
			EnemyLossMultuplier *= massRatio; 
		}

		//TODO crescita logaritmica col passare dei turni a partire da una certa soglia
		if(s.getTurnsLeft()<50) 
			EnemyLossMultuplier *= 1.5D;

		value = EnemyLossMultuplier * s.getEnemyLossesNumber() - MyLossMultiplier * s.getMyLossesNumber();

		if(s.getMyLossesNumber() == s.getMyAntsNumber())
			value -= 0.5;
		else if (s.getEnemyLossesNumber() == s.getEnemyAntsNumber())
			value += 0.4;

		//TODO RISCRIVERE FUNZIONE 
		//considerando un pareggio 
		//considerando in caso di pareggio se il numero di formiche uccise da me è maggiore 
		//del numero di formiche perse

		value += s.getEnemyHillDestroyedNumber();
		value -= s.getMyHillDestroyedNumber() * 5;

		value += s.getFoodCollectedFromMyAnts() /2;
		value -= s.getFoodCollectedFromEnemy();


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

	boolean AllowExtension(deadline, depth){
		return (depth%2 == 0) || (depth < maximundepth && deadline > getCurrentTime+GetExtensikonEstimate); 
	}
	
	//dir sara' verso nemico o retrocessione
	//radius sara' attackRadius+2 se ant = myAnt, attackRadius+1 altrimenti
	/*private List<Order> moveBackOrForward(Tile ant, Tile enemy, Directions dir, int radius){
		List<Order> orders = new LinkedList<>();
		int deltaRow = dir.getOffset().getRow();
		int deltaCol = dir.getOffset().getCol();
		int approach;
		int distance = Game.getDistance(ant, enemy);
		if(distance > radius) {
			approach = Game.getDistance(ant, enemy) - radius;
			for(int i=0; i < approach; i++) {
				Tile succ = Game.getTile(ant, new Offset(deltaRow, deltaCol));
				if(succ.isOccupiedByAnt()) {
					for(Directions d : Directions.values()) {
						if(d!=Directions.STAYSTILL && d!=dir && d!= dir.opponent()) {
							succ = Game.getTile(ant, new Offset(d.getOffset().getRow(), d.getOffset().getRow()));
							if(!succ.isOccupiedByAnt()) {
								orders.add(new Order(succ, d));
								break;
							}
						}
					}
				} else orders.add(new Order(succ, dir));
			}
		}else if (distance<radius) {
			
		} //else stay still
		return orders;
	}*/
	
	private List<Order> moveBackOrForward(Tile ant, Tile enemy, Directions dir, int radius){
		List<Order> orders = new LinkedList<Order>();
		int approach;
		int distance = Game.getDistance(ant, enemy);
		if(distance > radius) {
			approach = Game.getDistance(ant, enemy) - radius;
			orders = checkOrders(ant, approach, dir);
		}else if (distance<radius) {
			approach = Game.getDistance(ant, enemy) + radius;
			orders = checkOrders(ant, approach, dir.opponent());
		} else orders.add(new Order(ant, Directions.STAYSTILL));
		return orders;
	}
	
	private List<Order> checkOrders(Tile ant, int distance, Directions dir) {
		List<Order> orders = new LinkedList<>();
		int deltaRow = dir.getOffset().getRow();
		int deltaCol = dir.getOffset().getCol();
		for(int i=0; i < distance; i++) {
			Tile succ = Game.getTile(ant, new Offset(deltaRow, deltaCol));
			if(succ.isOccupiedByAnt()) {
				for(Directions d : Directions.values()) {
					if(d!=Directions.STAYSTILL && d!=dir && d!= dir.opponent()) {
						succ = Game.getTile(ant, new Offset(d.getOffset().getRow(), d.getOffset().getRow()));
						if(!succ.isOccupiedByAnt()) {
							orders.add(new Order(succ, d));
							break;
						}
					}
				}
			} else orders.add(new Order(succ, dir));
		}
		return orders;
	}





}
