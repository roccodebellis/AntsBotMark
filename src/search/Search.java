package search;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import vision.Offsets;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import game.Directions;
import game.Game;
import game.Order;
import game.Tile;

import java.nio.file.Path;
import java.util.HashMap;

/**
 * 
 * @author Debellis, Lorusso
 *
 */
public class Search {
	/**
	 * Una o piu' posizioni di partenza da cui far partire la ricerca
	 */
	private Set<Tile> sources;

	/**
	 * Insieme di obiettivi: possono essere Tile e/o tipologia dell'obbiettivo
	 */
	private Set<Tile> targets;

	/**
	 * La massima distanza attorno ad ogni tile di partenza {@link #source} da cui
	 * iniziare la ricerca dei {@link #targets}.<br>
	 * Puo' essere di tre tipi:
	 * <ul>
	 * <li>Attack radius: raggio di attacco, nel caso l'obiettivo sia quello di
	 * attaccare una formica nemica;</li>
	 * <li>View radius: raggio di visione, con l'obiettivo di andare in esplorazione
	 * (per la ricerca di hills nemiche), trovare cluster di formiche (in caso di
	 * nemici, valutare se attaccare, difendersi o andare in ritirata);</li>
	 * <li>Spawn radius: con l'obiettivo della riproduzione delle formiche e dunque
	 * della raccolta del cibo.</li>
	 * </ul>
	 */
	private Integer radius;

	/**
	 * Se andare alla ricerca di un singolo {@link #targets} vicino o tutti i target
	 */
	private Boolean heuristic;

	/**
	 * Se limitare la ricerca ed includere solo un {@link #targets} per ogni tile di
	 * partenza {@link #source}.<br>
	 * Impostato a true se si vuole effettuare una ricerca a partire da un singolo
	 * tile di cibo verso la formica piu' vicina o quando si vuole cercare un'unica
	 * formica piu' vicina ad un nemico. //TODO
	 */
	private Boolean one_target_per_source;

	private Boolean reverse;

	/**
	 * 
	 */
	private Set<Tile> results;

	/**
	 * 
	 */
	private Map<Tile, Tile> pathSources;

	private Set<Tile> completedSources;

	/**
	 * 
	 */
	private Map<Tile, Directions> directionFromSource;

	/**
	 * 
	 */
	private Map<Tile, Directions> directionFromTarget;

	/**
	 * set ordini
	 */
	private Set<Order> orders;

	/**
	 * tile obiettivo
	 */
	private HashSet<Tile> orderTile;

	public Search(final Set<Tile> sources, final Set<Tile> targets, Integer radius, Boolean heuristic,
			Boolean search_from_one_source, Boolean reverse) {
		this.sources = new HashSet<Tile>(sources);
		this.targets = new HashSet<Tile>(targets);
		this.completedSources = new HashSet<Tile>();
		this.orderTile = new HashSet<Tile>();

		this.radius = radius;
		this.heuristic = heuristic;
		this.one_target_per_source = search_from_one_source;
		this.reverse = reverse;

		// Init results
		results = new HashSet<Tile>();
		pathSources = new HashMap<Tile, Tile>();
		directionFromSource = new HashMap<Tile, Directions>();
		directionFromTarget = new HashMap<Tile, Directions>();
		orders = new TreeSet<Order>();

	}

	/**
	 * <p>
	 * Ricerca di tipo multi-purpose ossia non riguardera' necessariamente il
	 * raggiungimento di un singolo obiettivo ma potra' prevederne piu' di uno in
	 * base a quanto dettato da {@link #multitarget}.
	 * </p>
	 * Utilizza una delle tipologia di ricerca locale disponibile scelta in base al
	 * tipo di configurazione o di target.
	 * 
	 * @param sources //TODO I PARAM (probabilmente) SE NE VANNO A FANCULISSIMO
	 * @param targets
	 * @return
	 */
	// In teoria no param
	public Set<Tile> adaptiveSearch() {
		if (radius != null)
			return staticSearch();
		else if (heuristic)// se i target sono tutti di un certo tipo
			return extendedAStar();
		else return extendedBFS();
		// return bfs();
	}

	public Set<Tile> AStarSearch() {
		return AStar();
	}

	public Set<Tile> EAStarSearch() {
		return eAStar();
	}

	/**
	 * Viene eseguita ogni volta che viene trovata una tile contenente un obiettivo
	 * Ordina una mossa dalla formica trovata nella direzione del cibo
	 * 
	 * @param result   tile contenente l'obiettivo/target (O coordinate, da decidere
	 *                 //TODO)
	 * @param origin   tile di partenza da cui ha avuto inizio il percorso che ha
	 *                 permesso di scovare il target {code t}
	 * @param cardinal la direzione da cui inizia il percorso partendo da {code
	 *                 origin}
	 * @param opposite la direzione inversa con cui il percorso arriva al target
	 *                 {@code t}
	 */
	// TODO secondo me questa e' callback ed e' da integrare nelle ricerche se
	// one_target_per_source e' true
	private boolean createOneOrder(Tile origin, Tile target, Directions direction) {
		Order o = new Order(origin, direction, target);

		if (orderTile.add(o.getOrderedTile())) { // se viene modificato perche non funziona
			// avere la decenza di utilizzare hashMap o treeMap
			return this.orders.add(o);	     //K tile arrivo V order
		} else
			return false;
	}

	/*
	 * //TODO controllare private void computeOrders () { Iterator<Tile> itRes =
	 * results.iterator(); while(itRes.hasNext()) { Tile res = itRes.next(); Tile
	 * seed = pathSources.get(res); Order newOrder; if(one_target_per_source)
	 * newOrder = computeOneOrder(res, seed, directionFromSource.get(seed),
	 * directionFromTarget.get(seed)); else newOrder = computeOneOrder(seed, res,
	 * directionFromSource.get(seed), directionFromTarget.get(seed));
	 * orders.add(newOrder); } }
	 */

	public Set<Order> getOrders() {
		return orders;
	}

	public Set<Tile> getCompletedSources() {
		return completedSources;
	}

	/*
	 * private Set<Tile> targets() { return this.targets; }
	 */

	/**
	 * <p>
	 * Utilizza un set di coordinate <i>offsets</i> per un dato raggio di ricerca e
	 * controlla, in tempo lineare, tutte le {@link Tile} <i>offsets</i> attorno ad
	 * ogni {@code source} per i parametri di {@code targets}.
	 * </p>
	 * <p>
	 * Gli <i>offsets</i> per una data distanza sono calcolati solo la prima volta
	 * che sono richiesti e salvati per futuri utilizzi.<br>
	 * Cio' permette di salvare tempo computazionale per ricerche multiple che
	 * utilizzano lo stesso raggio (es. nel modulo di visione).
	 * 
	 * @param sources
	 * @param targets
	 * @return
	 */
	private Set<Tile> staticSearch() {

		Offsets offsets = new Offsets(radius);

		// System.out.println("***Offsets("+radius+"):"+offsets);
		/*
		 * sources.parallelStream().forEachOrdered( source ->
		 * offsets.parallelStream().forEachOrdered( offset -> { Tile curTile =
		 * Game.getTile(source, offset); if (targets.contains(curTile)) {
		 * results.add(curTile); System.out.println("SS:"+curTile); } }) );
		 * 
		 */
		// System.out.println("SS: source"+sources+" targets"+targets);
		/*
		 * la sorgente è una se viene passata piu di una sorgente il risultato non è
		 * quello desiderato
		 */
		Set<Tile> output = sources.parallelStream().map(source -> Game.getTiles(source, offsets))
				.flatMap(set -> set.parallelStream()).filter(x -> targets.contains(x))
				.collect(Collectors.toCollection(TreeSet<Tile>::new));

		return output;
	}

	/**
	 * souces
	 * targets
	 * 
	 * 
	 * 
	 * @return
	 */
	private Set<Tile> AStar() {
		PriorityQueue<Node> frontier = new PriorityQueue<Node>();
		Set<Node> closedSet = new HashSet<>();

		Tile source = sources.iterator().next();
		Node curSource = new Node(source, targets); //controllare se funziona con equals
		frontier.add(curSource);

		//directionFromSource.put(curSource.getTile(), Directions.STAYSTILL);
		//directionFromTarget.put(curSource.getTarget(), Directions.STAYSTILL);

		while(!frontier.isEmpty()) {
			//System.out.println("AStar:while");
			Node cur = frontier.poll();
			Tile curTile = cur.getTile();
			closedSet.add(cur);

			if(targets.contains(curTile)) {
				//createOneOrder(source, curTile, directionFromSource.get(source));
				Order o = new Order(source, directionFromSource.get(source), curTile);

				orderTile.add(o.getOrderedTile());
				orders.add(o);

				results.add(curTile);
				return results;

			} else for(Directions dirNeighbours :curTile.getNeighbours()) {

				Tile neighbour = curTile.getNeighbourTile(dirNeighbours);

				if((!neighbour.isSuitable() && (targets.contains(neighbour)||curTile.getNeighbours().size() == 1)) || neighbour.isSuitable()) {
					Node neighbourNode = new Node(neighbour, targets);//, cur.getPathCost());
					neighbourNode.setPathCost(cur.getPathCost());
					//Node neighbourNode = new Node(neighbour, cur.getTarget(), cur.getPathCost());

					//System.out.println(" - " + neighbourNode);
					if(!closedSet.contains(neighbourNode) ||
							closedSet.parallelStream().filter(x -> x.equals(neighbourNode)).allMatch(x -> neighbourNode.getPathCost() < x.getPathCost())){
						closedSet.add(neighbourNode);
						frontier.add(neighbourNode);
						if(!directionFromSource.containsKey(curTile))
							directionFromSource.put(curTile, dirNeighbours);
						directionFromTarget.put(neighbour, dirNeighbours.getOpponent());
					}

				}

			}
		}
		if(frontier.isEmpty()) //no path exists
			return new HashSet<Tile>();

		return null; //unreachable
	}

	private Set<Tile> eAStar() {

		PriorityQueue<Node> frontier = new PriorityQueue<Node>();
		Set<Node> closedSet = new TreeSet<>(Node.nodeComparator());
		//pathSources = new HashMap<Tile, Tile>()
		//results = new HashSet<Tile>();
		//directionFromSource = new HashMap<Tile, Directions>();
		//directionFromTarget = new HashMap<Tile, Directions>();
		//completedSources = new HashSet<>();

		Iterator<Tile> sourcesIt = sources.iterator();
		while(sourcesIt.hasNext()) {

			Tile curSource = sourcesIt.next();
			Node curSourceN = new Node(curSource, targets);
			frontier.add(curSourceN);
			closedSet.add(curSourceN);
			pathSources.put(curSource, curSource);
		}

		while(!frontier.isEmpty()) {

			Node curN = frontier.poll();
			Tile curT = curN.getTile();
			Tile curSource = pathSources.get(curT);

			closedSet.add(curN);

			if(completedSources.contains(curSource))
				continue;

			if(targets.contains(curT)) {
				results.add(curT);
				//Order o;

				if(!reverse)
					completedSources.add(curSource);
				if(one_target_per_source || reverse) {
					if(one_target_per_source) 
						targets.remove(curT);

					/*
					Tile tempT = curSource;
					Directions tempDir = null;
					while(tempT != curT) {
						tempDir = directionFromSource.get(tempT);
						tempT = tempT.getNeighbourTile(tempDir);
					};

					o = new Order(curT, tempDir, curSource);
					 */
					createOneOrder(curT, curSource, directionFromTarget.get(curT)); //o = new Order(curT, directionFromTarget.get(curT), curSource);

					curT.getNeighbourTile(directionFromTarget.get(curT)).setSuitable(false);

				} else {

					Tile tempT = curT;
					Directions tempDir = null;
					while(tempT != curSource) {
						tempDir = directionFromTarget.get(tempT);
						tempT = tempT.getNeighbourTile(tempDir);
					}

					createOneOrder(curSource, curT, tempDir.getOpponent()); //o = new Order(curSource, tempDir.getOpponent(), curT);

					pathSources.remove(curT);
					closedSet.remove(curN);
					curSource.getNeighbourTile(tempDir.getOpponent()).setSuitable(false);

					//o = new Order(curSource, directionFromSource.get(curSource), curT);
				}

			} else {


				for(Directions dirNeighbours : curT.getNeighbours()) {
					Tile neighbourT = curT.getNeighbourTile(dirNeighbours);

					if(((!neighbourT.isSuitable() && (targets.contains(neighbourT) || curT.getNeighbours().size() == 1)) || neighbourT.isSuitable()) && !targets.isEmpty()) {
						//Node neighbourNode = new Node(neighbourT, curN.getTarget(), curN.getPathCost());

						Node neighbourNode = new Node(neighbourT, targets);//, cur.getPathCost());
						neighbourNode.setPathCost(curN.getPathCost());

						if(!closedSet.contains(neighbourNode) || closedSet.parallelStream().filter(x -> x.equals(neighbourNode)).allMatch(x -> neighbourNode.getPathCost() < x.getPathCost()) //missed !pathSource.containsKey(neighbourT)
								|| !pathSources.containsKey(neighbourT) ){ //FIXME

							frontier.add(neighbourNode);
							pathSources.put(neighbourT, curSource);

							//if(!directionFromSource.containsKey(curT))
							directionFromSource.put(curT, dirNeighbours);

							directionFromTarget.put(neighbourT, dirNeighbours.getOpponent());


						}
					}	

				}
			}

		}

		return results;
	}

	private Set<Tile> extendedAStar() {
		PriorityQueue<Node> frontier = new PriorityQueue<Node>();
		Set<Node> expandedTile = new HashSet<>();
		Map<Node, Node> tileSources = new TreeMap<>();// is pathSources but with Node (Tile extended) instead of Tile
		Set<Tile> completedSources = new HashSet<>();

		// assegnare targhet ad ogni sorgente
		// il targhet piu vicino

		sources.forEach(source -> {
			Node curSource = new Node(source, targets);
			frontier.add(curSource);
			tileSources.put(curSource, curSource);
		});

		while (!frontier.isEmpty()) {

			Node curTile = frontier.poll();
			Node curTileSource = tileSources.get(curTile);

			expandedTile.add(curTile);// FIXME

			if (one_target_per_source && completedSources.contains(curTileSource.getTile()))// FIXME
				continue;// continue while

			if (targets.contains(curTile.getTile())) {
				results.add(curTile.getTile());
				if (one_target_per_source) {
					completedSources.add(curTileSource.getTile());
					createOneOrder(curTile.getTile(), curTileSource.getTile(),
							directionFromTarget.get(curTile.getTile()));
				} else {
					if (reverse)
						createOneOrder(curTile.getTile(), curTileSource.getTile(),
								directionFromTarget.get(curTile.getTile()));
					else
						createOneOrder(curTileSource.getTile(), curTile.getTile(),
								directionFromSource.get(curTileSource.getTile()));
				}
			}

			/*
			 * TODO change for to iterator Iterator<Map.Entry<Directions, Tile>> itNeigh =
			 * curTile.getTile().getNeighbour().entrySet().iterator(); Entry<Directions,
			 * Tile> neighbourEntry; while(it.hasNext()) {
			 * 
			 * }
			 */
			Iterator<Directions> neighboursIt = curTile.getTile().getNeighbours().iterator();

			while (neighboursIt.hasNext()) {
				Directions neighbourDirection = neighboursIt.next();
				Tile neighbourTile = curTile.getTile().getNeighbourTile(neighbourDirection);
				Node neighbour = new Node(neighbourTile, curTileSource.getTarget(), curTile.getPathCost());

				if (!neighbourTile.isSuitable() || expandedTile.contains(neighbour))
					continue;
				// DA CONTROLLARE TODO
				if (!tileSources.containsKey(neighbour) || expandedTile.parallelStream()
						.filter(x -> x.equals(neighbour)).allMatch(x -> curTile.getPathCost() + 1 < x.getPathCost())) {

					// if( !tileSources.containsKey(neighbour) ||
					// expandedTile.parallelStream().filter(x -> x.equals(neighbour)).allMatch(x ->
					// curTile.getPathCost()+1 < x.getPathCost())) {//FIXME
					// TileExtended neighbour = new
					// TileExtended(neighbourTile,curTileSource.getTarget(),curTile.getPathCost());
					// //FIXME altrimenti curTileSource.getTarget()

					tileSources.put(neighbour, curTileSource);

					directionFromSource.put(curTile.getTile(), neighbourDirection);// TODO FIXME
					// directionFromSource.put(neighbourTile,directionFromSource.containsKey(curTile.getTile())
					// ? directionFromSource.get(curTile.getTile()) : neighborDirection);
					directionFromTarget.put(neighbourTile, neighbourDirection.getOpponent());

					frontier.add(neighbour);
				}
			}
		}
		this.pathSources = new TreeMap<Tile, Tile>();
		this.pathSources.putAll(tileSources.entrySet().parallelStream()
				.collect(Collectors.toMap(e -> e.getKey().getTile(), e -> e.getValue().getTile())));
		// computeOrders(); TODO
		return this.results;
		// return results, tileSources, directionFromSource, directionFromTarget;
	}

	/**
	 * one_target_per_source trova per ogni sorgente il targhet più vicino e te li
	 * restituisce altrimenti per ogni source invia tutti i targhet vicini verso
	 * quelle sorgenti (sicuramente i più vicini ad ogni sorgente)
	 * 
	 * @return
	 */
	private Set<Tile> extendedBFS() {
		/*
		 * Queue<Tile> frontier = new LinkedList<Tile>(); Set<Tile> completedSources =
		 * new HashSet<>();
		 * 
		 * sources.parallelStream().forEachOrdered(source -> { frontier.add(source);
		 * pathSources.put(source,source); });
		 * 
		 * while(!frontier.isEmpty()) { Tile curTile = frontier.poll();
		 * 
		 * Tile curTileSource = pathSources.get(curTile); if(one_target_per_source &&
		 * completedSources.contains(curTileSource)) continue;//continue while
		 **
		 * 
		 * for(Entry<Directions, Tile> neighborEntry :
		 * curTile.getNeighbour().entrySet()) { Tile neighborTile =
		 * neighborEntry.getValue(); Directions neighborDirection =
		 * neighborEntry.getKey();
		 *
		 * 
		 * curTile.getNeighbour().entrySet().parallelStream().forEachOrdered(
		 * neighbourEntry -> { Tile neighbourTile = neighbourEntry.getValue();
		 * Directions neighbourDirection = neighbourEntry.getKey();
		 * 
		 * if(!neighbourTile.isSuitable() || !pathSources.containsKey(neighbourTile)) {
		 * 
		 * pathSources.put(neighbourTile,curTileSource);
		 * 
		 * directionFromSource.put(curTile, neighbourDirection);/*
		 * if(directionFromSource.containsKey(curTile))
		 * directionFromSource.put(neighbourTile, directionFromSource.get(curTile));
		 * else directionFromSource.put(neighbourTile, neighbourDirection);*
		 * 
		 * directionFromTarget.put(neighbourTile, neighbourDirection.getOpponent());
		 * 
		 * if(targets.contains(neighbourTile)) { results.add(neighbourTile);
		 * if(one_target_per_source) {//FIXME completedSources.add(curTileSource);
		 * createOneOrder(curTile, curTileSource, directionFromTarget.get(curTile));
		 * }else createOneOrder(curTileSource, curTile,
		 * directionFromSource.get(curTileSource)); //TODO: di regola non bisogna
		 * rimuovere il targhet //ma se viene rieseguita la ricerca, è necessario
		 * rimuoverlo }//*else*
		 * 
		 * frontier.add(neighbourTile); }//continue for }); } //return result,
		 * pathSources, directionFromSource, directionFromTarget; //TODO
		 * //computeOrders(); cambiare in base a come vengono impostati target e source
		 * return this.results;
		 */

		Queue<Tile> frontier = new LinkedList<Tile>();

		// Set<Tile> visited = new TreeSet<>(Tile.tileComparator());
		Map<Tile, Set<Tile>> visited = new HashMap<>();
		Map<Tile, Set<Tile>> closedList = new HashMap<>();

		sources.parallelStream().forEachOrdered(source -> {
			if(targets.contains(source)) {
				if (createOneOrder(source, source, Directions.STAYSTILL)) {
					targets.remove(source);
					completedSources.add(source);
					results.add(source);
				}
			} else {
				frontier.add(source);
				pathSources.put(source, source);
				// visited.add(source);
				Set<Tile> expanded = new HashSet<Tile>();
				expanded.add(source);
				visited.put(source, expanded);
				closedList.put(source, expanded);

			}
		});



		// while(!frontier.isEmpty() || !(!(results.containsAll(targets) ||
		// targets.isEmpty()) || orderTile.containsAll(sources))) { //FIXME togliere
		// targhets
		while (!frontier.isEmpty()) {
			Tile curTile = frontier.poll();

			Tile curTileSource = pathSources.get(curTile);

			Set<Tile> expanded = closedList.get(curTileSource);
			expanded.remove(curTile);

			if (completedSources.contains(curTileSource))
				continue;// continue while


			try {
				Iterator<Directions> neighboursIt = curTile.getNeighbours().iterator();

				while (neighboursIt.hasNext()) {
					Directions neighbourDirection = neighboursIt.next();
					Tile neighbourTile = curTile.getNeighbourTile(neighbourDirection);


					// neighbourTile.isSuitable() &&
					// if( !visited.containsKey(neighbourTile) ||
					// !visited.get(neighbourTile).contains(curTileSource)) {

					/*
					 * try { if(!neighbourTile.isSuitable()) throw new NullPointerException();
					 * }catch(NullPointerException e) { throw new
					 * NullPointerException("\nIs suitable: " + neighbourTile.isSuitable() +
					 * "\nTileType: " + neighbourTile.getType() + "\nNeigh:" + neighbourTile +
					 * "\nMy grandMother: " + curTileSource +"\nMy mother: " + curTile +
					 * "\nMyHills: " + Game.getMyHills()); }
					 */
					/*if (((!neighbourTile.isSuitable() && targets.contains(neighbourTile)) || neighbourTile.isSuitable()
								|| (!neighbourTile.isSuitable() && curTile.getNeighbour().size() == 1))
								&& !pathSources.containsKey(neighbourTile))*/
					if (((!neighbourTile.isSuitable() && (targets.contains(neighbourTile)||curTile.getNeighbours().size() == 1)) || neighbourTile.isSuitable())
							&& (((one_target_per_source || reverse) && !pathSources.containsKey(neighbourTile))
									^ (!(one_target_per_source ^ reverse) && (!visited.containsKey(neighbourTile)
											|| !visited.get(neighbourTile).contains(curTileSource))))) {
						pathSources.put(neighbourTile, curTileSource);

						if (!(one_target_per_source ^ reverse))
							directionFromSource.put(neighbourTile,
									directionFromSource.containsKey(curTile) ? directionFromSource.get(curTile)
											: neighbourDirection);
						else
							directionFromSource.put(curTile, neighbourDirection);
						directionFromTarget.put(neighbourTile, neighbourDirection.getOpponent());

						if (targets.contains(neighbourTile)) {
							// System.out.println("cur"+curTile + " nei"+neighbourTile
							// +"("+neighbourDirection+") " + "sourc" + curTileSource);

							boolean checkOrder = false;
							// TODO aggiungere solo se create one order ha restituito true
							if (one_target_per_source) {// FIXME

								checkOrder = createOneOrder(neighbourTile, curTileSource,
										directionFromTarget.get(neighbourTile));
								if (checkOrder) {
									targets.remove(neighbourTile);
									completedSources.add(curTileSource);
								}
							} else {
								if (reverse)
									checkOrder = createOneOrder(neighbourTile, curTileSource,
											directionFromTarget.get(neighbourTile));
								else {
									checkOrder = createOneOrder(curTileSource, neighbourTile,
											directionFromSource.get(neighbourTile));
									// checkOrder = createOneOrder(curTileSource, neighbourTile,
									// directionFromSource.get(curTileSource));

								}
							}

							if (checkOrder) {
								completedSources.add(curTileSource);
								results.add(neighbourTile);
								// frontier.removeAll(expanded);

							}

							break;
						} else {

							if (!visited.containsKey(neighbourTile)
									|| !visited.get(neighbourTile).contains(curTileSource)) {

								if (!visited.containsKey(neighbourTile)) {
									Set<Tile> set = new HashSet<Tile>();
									set.add(curTileSource);
									visited.put(neighbourTile, set);
								} else {
									Set<Tile> set = visited.get(neighbourTile);
									set.add(curTileSource);
									visited.put(neighbourTile, set);
								}
								frontier.add(neighbourTile);
								expanded.add(neighbourTile);
							}

						}

					}
				}
			} catch (NullPointerException e) {

				throw new NullPointerException("\nSorgenti: " + sources + "\nCurTile: " + curTile + "\nCurTileSource: "
						+ curTileSource + "\nNeigh: " + curTile.getNeighbours());
			}

		}
		// sources.forEach(s -> System.out.println(s +" -> "
		// +directionFromSource.get(s)));
		// return result, pathSources, directionFromSource, directionFromTarget; //TODO
		// computeOrders(); cambiare in base a come vengono impostati target e source
		return this.results;
	}

	private HashSet<Tile> getOrderTile() {
		return orderTile;
	}

	public Map<Tile, Directions> getDirectionFromSource() {
		return directionFromSource;
	}

	public Map<Tile, Directions> getDirectionFromTarget() {
		return directionFromTarget;
	}

}

