package combat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import defaultpackage.Configuration;
import game.Directions;
import game.Game;
import game.Order;
import game.Tile;
import search.Search;
import timing.Timing;

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

	private static Logger LOGGER = Logger.getLogger( CombatSimulation.class.getName() );
	private Set<Tile> myAntSet;
	private Map<Integer, Set<Tile>> enemyAntSet;
	private Map<Integer, Set<Tile>> enemyHills;
	private long deadLine;
	private Assignment root;

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
		IntStream.range(0, Game.getNumberEnemy()).forEach(id -> enemyHills.put(id+1, new HashSet<Tile>()));
		Set<Tile> gameEnemyHills = Game.getEnemyHills();
		if(!gameEnemyHills.isEmpty())
			gameEnemyHills.forEach(eHill -> enemyHills.get(eHill.getOwner()).add(eHill));
	}

	public Set<Order> getMoves(){
		LOGGER.severe("\tSE MI CERCHI SONO QUI");
		LOGGER.severe("\tgetMoves("+ root +" moves"+root.getMoves()+")++++++++++++");
		
		root.getChildren().forEach(c -> LOGGER.severe("\tAssignment: " + c+"["+c.getMoves()+"]"));
		
		LOGGER.severe("\t\t~getMoves()++++++++++++");
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

		IntStream.range(0, Game.getNumberEnemy()).forEach(id -> enemyAntSet.put(id+1, new HashSet<Tile>()));
		enemyAntSet.get(enemyAnt.getOwner()).add(enemyAnt);

		int combatSearchRadius = Configuration.getCombatModuleSearchRadius();

		//FIXME ciclare solo sulle formiche aggiunte, prendere la diffrenza tra l'intersezione e l'insisme nuovo
		//e considerare quelle formiche per la successiva iterata aggiungendole cmq alla lista delle myAntsSet

		//FIXME controllare se funziona
		boolean addedAnts = true;
		while(addedAnts){
			addedAnts = false;
			Search forEnemyAnts = new Search(myAntSet, Game.getEnemyAnts(), combatSearchRadius, false, false, false);
			Set<Tile> newEnemyFound = forEnemyAnts.adaptiveSearch();

			if(newEnemyFound.size() > enemyAntSet.entrySet().parallelStream().mapToInt(eASet -> eASet.getValue().size()).sum()) {

				addedAnts = true;
				newEnemyFound.parallelStream().forEachOrdered(eA -> enemyAntSet.get(eA.getOwner()).add(eA));
				//newEnemyFound.parallelStream().forEachOrdered(enemyAntSet::add);
			}

			Set<Tile> enemyAntsSet = new HashSet<Tile>();
			enemyAntSet.values().forEach( enemySet -> enemyAntsSet.addAll(enemySet));

			Search forMyAnts = new Search(enemyAntsSet, Game.getMyAnts(), combatSearchRadius, false, false, false);
			Set<Tile> newMyAntsFound = forMyAnts.adaptiveSearch();
			addedAnts |= myAntSet.addAll(newMyAntsFound);
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
		//LOGGER.severe("\tmovesGenerator()");
		Map<MovesModels,Set<Order>> output = new HashMap<MovesModels,Set<Order>>();

		output.put(MovesModels.ATTACK, attack(s));
		output.put(MovesModels.HOLD, hold(s));
		//output.put(MovesModels.IDLE, idle(s));
		output.put(MovesModels.NORTH, directional(s,Directions.NORTH));
		output.put(MovesModels.SOUTH, directional(s,Directions.SOUTH));
		output.put(MovesModels.EAST, directional(s,Directions.EAST));
		output.put(MovesModels.WEST, directional(s,Directions.WEST));


		//LOGGER.severe("\t"+output);
		//LOGGER.severe("\t\tants"+s.getAnts());
		//LOGGER.severe("\t\tenemy"+s.getOpponentAnts());
		//LOGGER.severe("\t~movesGenerator()");
		return output;
	}

	private Set<Order> attack(Assignment currAssignment) {
		//LOGGER.info("attack()");
		Set<Tile> targets = new HashSet<Tile>();
		targets.addAll(currAssignment.getOpponentAnts());
		targets.addAll(currAssignment.getOpponentHills());
		//TODO forse bisogna migliorare questa ricerca 
		//ora abbiamo provato a mettere che piu formiche nostre attaccano un unico targhet
		if(targets!=null && currAssignment.getAnts()!=null) {
			Search search = new Search(currAssignment.getAnts(),targets, null, false, false, false); //bfs classica
			//Search search = new Search(targets, currAssignment.getAnts(), null, false, false, true); //bfs reverse
			search.adaptiveSearch();
			//LOGGER.info("~attack("+search.getOrders()+")");
			return search.getOrders();
		}
		//LOGGER.info("~attack()");
		return new HashSet<Order>();
	}

	private Set<Order> hold(Assignment currAssignment) {
		//LOGGER.info("hold()");
		double targetDistance = Math.sqrt(Game.getAttackRadius2()) + (currAssignment.isEnemyMove() ? 1 : 2);
		Set<Order> ordersAssigned = new HashSet<Order>();

		Iterator<Tile> antsItr = currAssignment.getAnts().iterator();//TODO
		while(antsItr.hasNext()) {
			Tile ant = antsItr.next();

			Iterator<Tile> targetsItr = currAssignment.getOpponentAnts().iterator();

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
		//LOGGER.info("~hold("+ordersAssigned+")");
		return ordersAssigned;
	}

	private Order moveBackOrForward(Set<Order> ordersAssigned, Tile ant, Tile enemy, int distance, double targetDistance) {

		Directions dir = Game.getDirection(ant, enemy);
		if(distance < targetDistance)
			dir = dir.getOpponent();
		Order order;

		if(distance != targetDistance) {
			//avvicinarsi/allontanarsi
			if(!Game.getTile(ant,dir.getOffset()).isAccessible()) {
				//non accessibile //ordine non impartibile
				dir = dir.getNext();
				if(!Game.getTile(ant,dir.getOffset()).isAccessible()) {
					dir = dir.getOpponent();
					if(Game.getTile(ant,dir.getOffset()).isAccessible())
						order = new Order(ant, dir, ant.getNeighbourTile(dir));
					else 
						order = new Order(ant, Directions.STAYSTILL, ant);
				} else {
					order = new Order(ant, dir, ant.getNeighbourTile(dir));
					if(ordersAssigned.contains(order)) {
						dir = dir.getOpponent();
						if(Game.getTile(ant,dir.getOffset()).isAccessible())
							order = new Order(ant, dir, ant.getNeighbourTile(dir));
						else 
							order = new Order(ant, Directions.STAYSTILL, ant);
					}
				}
			} else {
				//accessobile
				order = new Order(ant, dir, ant.getNeighbourTile(dir));
				if(ordersAssigned.contains(order)) {
					//non accessibile //ordine non impartibile
					dir = dir.getNext();
					if(!Game.getTile(ant,dir.getOffset()).isAccessible()) {
						dir = dir.getOpponent();
						if(Game.getTile(ant,dir.getOffset()).isAccessible())
							order = new Order(ant, dir, ant.getNeighbourTile(dir));
						else 
							order = new Order(ant, Directions.STAYSTILL, ant);

					} else {
						order = new Order(ant, dir, ant.getNeighbourTile(dir));
						if(ordersAssigned.contains(order)) {
							dir = dir.getOpponent();
							if(Game.getTile(ant,dir.getOffset()).isAccessible())
								order = new Order(ant, dir, ant.getNeighbourTile(dir));
							else 
								order = new Order(ant, Directions.STAYSTILL, ant);
						}
					}
				}
			}
		}else {
			order = new Order(ant, Directions.STAYSTILL, ant);
			if(ordersAssigned.contains(order)) {
				dir = dir.getNext();
				if(!Game.getTile(ant,dir.getOffset()).isAccessible()) {
					dir = dir.getOpponent();
					if(Game.getTile(ant,dir.getOffset()).isAccessible())
						order = new Order(ant, dir, ant.getNeighbourTile(dir));
					else 
						order = new Order(ant, Directions.STAYSTILL, ant);

				} else {
					order = new Order(ant, dir, ant.getNeighbourTile(dir));
					if(ordersAssigned.contains(order)) {
						dir = dir.getOpponent();
						if(Game.getTile(ant,dir.getOffset()).isAccessible())
							order = new Order(ant, dir, ant.getNeighbourTile(dir));
						else 
							order = new Order(ant, Directions.STAYSTILL, ant);
					}
				}
			} 
		}

		//LOGGER.severe("~directional(,dir:"+dir+")");
		return order;
	}


	/*private Set<Order> idle(Assignment ordersAssigned) {
		return ordersAssigned.getAnts().stream().map(ant -> new Order(ant,Directions.STAYSTILL, ant)).collect(Collectors.toSet());
	}*/

	private Set<Order> directional(Assignment ordersAssigned, Directions tDir) {
		//LOGGER.severe("directional(,tDir:"+tDir+")");
		Set<Order> orders = new HashSet<Order>();


		ordersAssigned.getAnts().forEach(ant -> {
			Order order;
			Directions dir = tDir;

			if(Game.getTile(ant, dir.getOffset()).isAccessible()) {
				Tile target = ant.getNeighbourTile(dir);
				order = new Order(ant,dir, target);
				if(!orders.add(order)) {
					order = new Order(ant,Directions.STAYSTILL, ant);
					orders.add(order);
				}
			} else {
				order = new Order(ant,Directions.STAYSTILL, ant);
				orders.add(order);
			}
		});
		//LOGGER.severe("~directional("+orders+")");
		return orders;
	}

	/**
	 * Dopo aver identificato una situazione di battaglia si costruisce un albero di 
	 * ricerca per determinare la mossa migliore che dovra' performare l'agente.
	 * In ants, tutti i giocatori muovono le loro formiche simultaneamente. Il motore
	 * di ricerca di conseguenza tratta ogni turno del gioco come una ricerca a due
	 * strati. Uno strato e' la mossa dell'agente, mentre l'altro e' la mossa del nemico
	 * e la risoluzione del turno (combattimento e raccolta del cibo). Per questa 
	 * ragione la profondita' della ricerca e' sempre pari. La ricerca e' inizializzata
	 * con un nodo neutrale il quale contine lo stato corrente del gioco e la situazione
	 * riconosciuta (i gruppi delle formiche). Dopo di che il generatore di mosse 
	 * viene utizzato per generare i figli (nodi) dello strato successivo di ricerca.
	 * Gli strati dispari riguardano le mosse dell'agente, mentre quelli pari sono le 
	 * mosse dei nemici. Dopo ogni strato pari ha luogo una simulazione di risoluzione di
	 * combattimento e di raccolta cibo. I nodi figli sono generati dal generatore di mosse.
	 * Dopo che il nemico ha effettuato la mossa su tutti i figli pari, avviene la risoluzione
	 * del combattimento e la raccolta del cibo, nel caso dovvessero esserci le condizioni per 
	 * tali avvenimenti. In seguito ogni nodo viene valutato tramite la funzione euristica. 
	 * Gli insiemi dei nodi figli di ogni nodo (dell'albero di gioco) sono insiemi di alberi
	 * ordinati (ovviamente tramite il valore assegnato ad ogni nodo dalla funzione euristica).
	 * Aggiungendo un nuovo nodo figlio (generato e valutato) si fa in modo di aggiungerlo 
	 * nell'albero in accordo all'ordinamento naturale che si basa sulla valutazione dei nodi. 
	 * Di conseguenza il primo elemento dell'albero contine la mossa migliore, eliminando 
	 * la neccissita' di eseguire in seguito un ordinamento esplicito. 
	 * La profondita' di ricerca e' dinamica ed e' limitata da una soglia superiore di tempo
	 * proprio come accade per il tempo assegnato alla simulazione del combattimento.
	 * La simulazione e' interrotta alla massima profondita' o quando il tempo a disposizione
	 * e' stato esaurito. 
	 * 
	 * 
	 * @param state
	 * @param deadLine
	 * @param depth
	 * @return
	 */
	private void MinMax(Assignment state, long deadLine, int depth) {
		//LOGGER.severe("MINMAX-> depth:"+depth+" MT:"+state.getMoveType()+" state:"+ state.isEnemyMove()+" deadline"+deadLine+" ct:"+Timing.getCurTime());
		//LOGGER.severe("\t(MINMAX) - ["+depth+" MT:"+state.getMoveType()+" v:"+state.getValue()+" ants:"+state.getAnts() +" enemy:"+state.getOpponentAnts() + "]");
		if(depth!=0 && !(depth < Configuration.getCombatModuleMinMaxMaxDepth() && deadLine > state.GetExtensionEstimate())) {
			//LOGGER.severe("\t[(if)"+depth+" MT:"+state.getMoveType()+" v:"+state.getValue()+"]");
			state.evaluate();
			return;
		}

		Map<MovesModels,Set<Order>> movesSet = movesGenerator(state);

		movesSet.entrySet().parallelStream().forEachOrdered( movesEntry -> {
			MovesModels moveType = movesEntry.getKey();
			Set<Order> moves = movesEntry.getValue();

			Assignment childState = state.performMove(moves, moveType);
			long curTime = Timing.getCurTime();
			long childDeadline = curTime + (deadLine-curTime)/(movesSet.size()-moveType.ordinal());

			if(!childState.isEnemyMove()) {
				childState.resolveCombatAndFoodCollection();
				state.evaluate();
			}

			MinMax(childState, childDeadline, depth+1);
			//LOGGER.severe("\t["+depth+" MT:"+state.getMoveType()+" v:"+state.getValue()+"]");
			state.addChild(childState);

		});
	}

	private int antInvolved() {
		return myAntSet.size() + enemyAntSet.entrySet().parallelStream().mapToInt(eASet -> eASet.getValue().size()).sum(); //TODO check deve contare il numero di formiche coinvolte
	}

	@Override
	public int compareTo(CombatSimulation o) {
		int antInv = o.antInvolved();
		int enemyInv = antInvolved();
		return antInv==enemyInv ? -1 : Integer.compare(antInv,enemyInv);//FIXME SONO INVERTITI per ordinarli in ordine decrescente, o almeno si spera
	}

	public void combatResolution() {
		//LOGGER.severe("\tcombatResolution()");
		List<Integer> enemyLosses = new ArrayList<Integer>(enemyAntSet.size());
		IntStream.range(0, enemyAntSet.size()).parallel().forEachOrdered(i -> enemyLosses.add(0));
		
		List<Integer> enemyHillsDestroyed = new ArrayList<Integer>(enemyAntSet.size());
		IntStream.range(0, enemyAntSet.size()).parallel().forEachOrdered(i -> enemyHillsDestroyed.add(0));
		
		List<Integer> enemyFoodCollected = new ArrayList<Integer>(enemyAntSet.size());
		IntStream.range(0, enemyAntSet.size()).parallel().forEachOrdered(i -> enemyFoodCollected.add(0));
		
		root = new Assignment(0, myAntSet, Game.getMyHills(), 0, 0, 0, enemyAntSet, enemyHills, enemyLosses, enemyHillsDestroyed, enemyFoodCollected, Game.getFoodTiles(), false, null, new HashSet<Order>());

		MinMax(root, Timing.getCurTime() + deadLine, 0);	

		Game.getMyHills().parallelStream().forEachOrdered(hill -> hill.setSuitable(false));

		//LOGGER.severe("\t\t->->->child: " + root.getChildren());

		//LOGGER.severe("\t~combatResolution()");
	}

	@Override
	public String toString() {
		return "CombatSimulation [root=" + root + "]";
	}


}
