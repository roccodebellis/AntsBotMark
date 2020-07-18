package combat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import defaultpackage.Configuration;
import game.Directions;
import game.Game;
import game.Order;
import game.Tile;
import search.Node;
import search.Search;
import timing.Timing;
import vision.Offset;

/**
 * <p>Questo modulo processa tutte le situazioni in cui le formiche degli agenti incontrano formiche nemiche
 * ad una certa distanza.<br>
 * Il modulo provera' a determinare le migliori mosse per le formiche dell'agente in una data situazione
 * usando una ricerca stati-spazio, simulando le possibili mosse che potrebbero compiere l'agente
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
public class CombatSimulation implements Comparable<CombatSimulation>{

	Set<Tile> myAntSet;
	Map<Integer, Set<Tile>> enemyAntSet;
	Map<Integer, Set<Tile>> enemyHills;
	long deadLine;
	Assignment root;

	/**
	 * Considero la formica e la formica nemica che hanno fatto scattare la situazione di battaglia.
	 * 
	 * @param myAnt
	 * @param enemyAnt
	 * @param deadLine
	 */
	public CombatSimulation(Tile myAnt, Tile enemyAnt, long deadLine) {
		Game.getMyHills().parallelStream().forEachOrdered(hill -> hill.setSuitable(true)); //perche ' in combattimento
		//System.out.println("- "+myAnt);
		//System.out.println("- "+enemyAnt);

		situationRecognition(myAnt,enemyAnt);
		this.deadLine = deadLine;
		enemyHills = new TreeMap<Integer, Set<Tile>>();
		IntStream.range(0, Game.getNumberEnemy()).forEach(id -> enemyHills.put(id, new HashSet<Tile>()));

		Game.getEnemyHills().forEach(eHill -> enemyHills.get(eHill.getOwner()-1).add(eHill));


	}

	public Set<Order> getMoves(){
		return root.getFirstChild();
	}

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
	private void situationRecognition(Tile myAnt, Tile enemyAnt){ 
		//TROVARE UN MODO PER SAPERE SE LA FORMICA POSIZIONATA SUL TILE è disponibile 
		// no, claudia isOccupied dice solo se quella tile è occupata da una formica!
		myAntSet = new HashSet<>();
		enemyAntSet = new HashMap<Integer, Set<Tile>>();

		myAntSet.add(myAnt);
		IntStream.range(0, Game.getNumberEnemy()).forEach(id -> enemyAntSet.put(id, new HashSet<Tile>()));
		//System.out.println("* "+ enemyAntSet);
		//System.out.println("* "+ enemyAnt.getOwner());

		enemyAntSet.get(enemyAnt.getOwner()-1).add(enemyAnt);

		int attackRadius = Game.getAttackRadius2() * 9;

		//FIXME ciclare solo sulle formiche aggiunte, prendere la diffrenza tra l'intersezione e l'insisme nuovo
		//e considerare quelle formiche per la successiva iterata aggiungendole cmq alla lista delle myAntsSet

		//FIXME controllare se funziona
		boolean addedAnts = false;
		while(addedAnts){
			addedAnts = false;
			Search forEnemyAnts = new Search(myAntSet, Game.getEnemyAnts(), attackRadius, false, false, false);
			Set<Tile> newEnemyFound = forEnemyAnts.adaptiveSearch();

			if(newEnemyFound.size() > (int) enemyAntSet.entrySet().parallelStream().count()) {
				addedAnts = true;
				newEnemyFound.parallelStream().forEachOrdered(eA ->
					enemyAntSet.get(eA.getOwner()).add(eA)
						);

			}




			Set<Tile> enemyAntsSet = new HashSet<Tile>();
			enemyAntSet.values().forEach( enemySet -> enemyAntsSet.addAll(enemySet));

			Search forMyAnts = new Search(enemyAntsSet, Game.getMyAnts(), attackRadius, false, false, true);
			Set<Tile> newMyAntsFound = forMyAnts.adaptiveSearch();
			addedAnts =	myAntSet.addAll(newMyAntsFound);
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
	 * <li><b>Directional</b>: tutte le formiche dell'agente (o quelle nemiche) si muoveranno,
	 * semplicemente, in una delle quattro direzioni cardinali - N S E o W.</li>
	 * </ul>
	 * </p>
	 */
	private Map<MovesModels,Set<Order>> movesGenerator(Assignment s) {
		Map<MovesModels,Set<Order>> output = new HashMap<MovesModels,Set<Order>>();

		output.put(MovesModels.ATTACK, attack(s));
		/*output.put(MovesModels.HOLD, hold(s));
		output.put(MovesModels.IDLE, idle(s));
		output.put(MovesModels.NORTH, directional(s,Directions.NORTH));
		output.put(MovesModels.SOUTH, directional(s,Directions.SOUTH));
		output.put(MovesModels.EAST, directional(s,Directions.EAST));
		output.put(MovesModels.WEST, directional(s,Directions.WEST));*/
		
		return output;
	}

	private Set<Order> attack(Assignment s) {
		Set<Tile> targets = new HashSet<Tile>();
		targets.addAll(s.getOpponentAnts());
		targets.addAll(s.getOpponentHills());

		Search search = new Search(targets, s.getAnts(), null, false, true, false);
		search.adaptiveSearch();
		return search.getOrders();
	}


	private Set<Order> hold(Assignment s) {
		//E' UGUALE!! EXPLORATIONANDMOVEMENT.spreadOut(); TODO

		double targetDistance = Math.sqrt(Game.getAttackRadius2()) + (s.isEnemyMove() ? 1 : 2);
		Set<Order> ordersAssigned = new HashSet<Order>();

		Iterator<Tile> antsItr = s.getAnts().iterator();
		while(antsItr.hasNext()) {

			Tile ant = antsItr.next();

			Iterator<Tile> targetsItr = s.getOpponentAnts().iterator();

			if(targetsItr.hasNext()) {
				Tile minTarget = targetsItr.next();
				int minDist = Game.getDistance(ant,minTarget);

				while (targetsItr.hasNext()) {
					Tile next = targetsItr.next();
					int nextDist = Game.getDistance(ant,next);
					if (nextDist < minDist) {
						minTarget = next;
						minDist = nextDist;
					}
				}
				ordersAssigned.add(moveBackOrForward(ordersAssigned, ant, minTarget, minDist, targetDistance));
			}
		}
		return ordersAssigned;
	}

	private Order moveBackOrForward(Set<Order> ordersAssigned, Tile ant, Tile enemy, int distance, double targetDistance) {

		Directions dir = Game.getDirection(ant, enemy);
		if(distance < targetDistance)
			dir = dir.getOpponent();
		Order order;

		if(distance != targetDistance) {
			order = new Order(ant, dir);
			/*
			if(ordersAssigned.contains(order) || !Game.getTile(ant, dir.getOffset()).isAccessible()) {
				order = new Order(ant,dir.getNext());
				if(ordersAssigned.contains(order)|| !Game.getTile(ant, dir.getNext().getOffset()).isAccessible()) {
					order = new Order(ant,dir.getOpponent().getNext());
					if(ordersAssigned.contains(order)|| !Game.getTile(ant, dir.getOpponent().getNext().getOffset()).isAccessible())
						order = new Order(ant,Directions.STAYSTILL); //FIXME
				}
			} 
			 */

		} else order = new Order(ant, Directions.STAYSTILL);
		return order;
	}


	private Set<Order> idle(Assignment s) {
		return s.getAnts().parallelStream().map(ant -> new Order(ant,Directions.STAYSTILL)).collect(Collectors.toSet());
	}

	private Set<Order> directional(Assignment s, Directions m) {
		Set<Order> orders = new HashSet<Order>();

		s.getAnts().forEach(a -> {
			Tile target = a.getNeighbour().get(m);

			if(target==null) //acqua
				orders.add(new Order(a,Directions.STAYSTILL)); //FIXME
			else {
				Order o = new Order(a,m);
				/*
				if(orders.contains(o)) 
					o = new Order(a,m.getNext());
				else if(orders.contains(o))
					o = new Order(a,m.getOpponent().getNext());
				if(orders.contains(o))
					o = new Order(a, Directions.STAYSTILL);
				orders.add(o);
				 */
			}
		});
		return orders;
	}




	private double MinMax(Assignment state, long deadLine, int depth) {
		if(AllowExtension(deadLine, depth)) {

			Map<MovesModels,Set<Order>> movesSet = movesGenerator(state);

			movesSet.entrySet().parallelStream().forEachOrdered( movesEntry -> {
				MovesModels moveType = movesEntry.getKey();
				Set<Order> moves = movesEntry.getValue();

				Assignment childState = state.performMove(moves);
				long curTime = Timing.getCurTime();
				long childDeadline = curTime + (deadLine-curTime)/(movesSet.size()-moveType.ordinal());//FIXME getNumber of Moves forse è il numero di mosse in questo calcolo

				if(childState.isEnemyMove())
					childState.resolveCombatAndFoodCollection();

				MinMax(childState, childDeadline, depth+1);

				state.addChild(childState);
			});}
		return state.evaluate();
	}

	boolean AllowExtension(long deadline, int depth){
		return (depth%2 == 0) || (depth < Configuration.getCombatModuleMinMaxMaxDepth() && deadline > Timing.getCurTime()+85); //FIXME GetExtensionEstimate
	}



	private int antInvolved() {
		return myAntSet.size() + (int) enemyAntSet.entrySet().parallelStream().count(); //TODO check deve contare il numero di formiche coinvolte
	}

	@Override
	public int compareTo(CombatSimulation o) {
		return Integer.compare(o.antInvolved(),antInvolved());//FIXME SONO INVERTITI per ordinarli in ordine decrescente, o almeno si spera
	}

	public void combatResolution() {
		root = new Assignment(0, myAntSet, Game.getMyHills(), enemyAntSet, enemyHills, Game.getFoodTiles(), false);
		MinMax(root, deadLine, 0);	
		Game.getMyHills().parallelStream().forEachOrdered(hill -> hill.setSuitable(false));

	}

}
