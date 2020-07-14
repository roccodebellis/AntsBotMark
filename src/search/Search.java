package search;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import vision.Offset;
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
	 * tile di cibo verso la formica piu' vicina o quando si vuole cercare un'unica formica
	 * piu' vicina ad un nemico. //TODO
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

	/**
	 * 
	 */
	private Map<Tile, Directions> directionFromSource;

	/**
	 * 
	 */
	private Map<Tile, Directions> directionFromTarget;

	/**
	 * 
	 */
	private Set<Order> orders;

	private HashSet<Tile> orderTile;

	public Search(final Set<Tile> sources, final Set<Tile> targets, Integer radius, Boolean heuristic, Boolean search_from_one_source, Boolean reverse) {
		this.sources = sources;
		this.targets = new HashSet<Tile>(targets);

		this.orderTile = new HashSet<Tile>();

		this.radius = radius;
		this.heuristic = heuristic;
		this.one_target_per_source = search_from_one_source;
		this.reverse = reverse;

		//Init results
		results = new HashSet<Tile>();
		pathSources = new HashMap<Tile, Tile>();
		directionFromSource = new HashMap<Tile, Directions>();
		directionFromTarget = new HashMap<Tile, Directions>();
		orders = new HashSet<Order>();


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
		else if (heuristic)//se i target sono tutti di un certo tipo 
			return extendedAStar();
		else 			
			return extendedBFS();
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
	//TODO secondo me questa e' callback ed e' da integrare nelle ricerche se one_target_per_source e' true
	private boolean createOneOrder (Tile origin, Tile target, Directions direction) {
		//if(one_target_per_source)
		//	this.targets.remove(target);

		//if( this.orders.add(new Order(origin, direction))) {
		//	if(one_target_per_source || reverse)
		//		orderTile.add(target);
		//	else
		//		orderTile.add(origin);*/
		//	return true;

		//}else if( this.orders.add(new Order(origin, direction.getOpponent().getNext()))) {
		/*if(one_target_per_source || reverse)
				orderTile.add(target);
			else
				orderTile.add(origin);*/
		//	return true;

		//}else if( this.orders.add(new Order(origin, direction.getNext()))) {
		/*if(one_target_per_source || reverse)
				orderTile.add(target);
			else
				orderTile.add(origin);*/
		//	return true;

		//} else if( this.orders.add(new Order(origin, direction.getOpponent()))) {
		//	return true;
		//}else
		//	return false;

		//System.out.println("o:" +origin +" ->t:"+target+" "+direction);



		Order o = new Order(origin, direction);

		if(orderTile.add(o.getOrderedTile())) {
			this.orders.add(o);
			return true;
		} else
			return false;
	}




	/*
	//TODO controllare
	private void computeOrders () {
		Iterator<Tile> itRes = results.iterator();
		while(itRes.hasNext()) {
			Tile res = itRes.next();
			Tile seed = pathSources.get(res);
			Order newOrder;
			if(one_target_per_source)
				newOrder = computeOneOrder(res, seed, directionFromSource.get(seed), directionFromTarget.get(seed));
			else newOrder = computeOneOrder(seed, res, directionFromSource.get(seed), directionFromTarget.get(seed));
			orders.add(newOrder);
		}
	}*/

	public Set<Order> getOrders(){
		return orders;
	}

	private Map<Tile,Tile> getPathSources(){
		return this.pathSources;
	}

	private Set<Tile> targets() {
		return this.targets;
	}

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
	// TODO in teoria no param
	private Set<Tile> staticSearch() {

		Offsets offsets = new Offsets(radius);

		sources.parallelStream().forEachOrdered(
				source -> offsets.parallelStream().forEachOrdered(
						offset -> {
							Tile curTile = Game.getTile(source, offset);
							if (targets.contains(curTile))
								results.add(curTile);
						})
				);

		return results;
	}

	private Set<Tile> extendedAStar() {
		PriorityQueue<Node> frontier = new PriorityQueue<Node>();
		Set<Node> expandedTile = new HashSet<>();
		Map<Node,Node> tileSources = new TreeMap<>();// is pathSources but with Node (Tile extended) instead of Tile
		Set<Tile> completedSources = new HashSet<>();

		//assegnare targhet ad ogni sorgente
		//il targhet piu vicino

		sources.forEach(source -> {
			Node curSource = new Node(source,targets);
			frontier.add(curSource);
			tileSources.put(curSource,curSource);
		});

		while(!frontier.isEmpty()) {

			Node curTile = frontier.poll();
			Node curTileSource = tileSources.get(curTile);

			expandedTile.add(curTile);//FIXME

			if(one_target_per_source && completedSources.contains(curTileSource.getTile()))//FIXME
				continue;//continue while

			if(targets.contains(curTile.getTile())) {
				results.add(curTile.getTile());
				if(one_target_per_source) {
					completedSources.add(curTileSource.getTile());
					createOneOrder(curTile.getTile(), curTileSource.getTile(), directionFromTarget.get(curTile.getTile()));
				}else { 
					if(reverse)
						createOneOrder(curTile.getTile(), curTileSource.getTile(), directionFromTarget.get(curTile.getTile()));
					else
						createOneOrder(curTileSource.getTile(), curTile.getTile(), directionFromSource.get(curTileSource.getTile()));}
			}

			/*TODO change for to iterator 
			Iterator<Map.Entry<Directions, Tile>> itNeigh = curTile.getTile().getNeighbour().entrySet().iterator();
			Entry<Directions, Tile> neighbourEntry;
			while(it.hasNext()) {

			}*/
			for(Entry<Directions, Tile> neighbourEntry : curTile.getTile().getNeighbour().entrySet()) {
				Tile neighbourTile = neighbourEntry.getValue();
				Directions neighborDirection = neighbourEntry.getKey();
				Node neighbour = new Node(neighbourTile,curTileSource.getTarget(),curTile.getPathCost());		

				if(!neighbourTile.isSuitable() || expandedTile.contains(neighbour))
					continue;
				//DA CONTROLLARE TODO
				if(!tileSources.containsKey(neighbour) || expandedTile.parallelStream().filter(x -> x.equals(neighbour)).allMatch(x -> curTile.getPathCost()+1 < x.getPathCost())){	

					//if( !tileSources.containsKey(neighbour) || expandedTile.parallelStream().filter(x -> x.equals(neighbour)).allMatch(x -> curTile.getPathCost()+1 < x.getPathCost())) {//FIXME
					//TileExtended neighbour = new TileExtended(neighbourTile,curTileSource.getTarget(),curTile.getPathCost()); //FIXME altrimenti curTileSource.getTarget()

					tileSources.put(neighbour,curTileSource);

					directionFromSource.put(curTile.getTile(), neighborDirection);//TODO FIXME
					//directionFromSource.put(neighbourTile,directionFromSource.containsKey(curTile.getTile()) ? directionFromSource.get(curTile.getTile()) : neighborDirection);
					directionFromTarget.put(neighbourTile, neighborDirection.getOpponent());

					frontier.add(neighbour);
				}
			}
		}
		this.pathSources = new TreeMap<Tile,Tile>();
		this.pathSources.putAll(tileSources.entrySet().parallelStream().collect(Collectors.toMap(e -> e.getKey().getTile(), e -> e.getValue().getTile())));
		//computeOrders(); TODO
		return this.results;
		//return results, tileSources, directionFromSource, directionFromTarget;
	}

	/**
	 * one_target_per_source trova per ogni sorgente il targhet più vicino e te li restituisce
	 * altrimenti per ogni source invia tutti i targhet vicini verso quelle sorgenti (sicuramente
	 * i più vicini ad ogni sorgente)
	 * 
	 * @return
	 */
	private Set<Tile> extendedBFS() {
		/*
		Queue<Tile> frontier = new LinkedList<Tile>();
		Set<Tile> completedSources = new HashSet<>();

		sources.parallelStream().forEachOrdered(source -> {
			frontier.add(source);
			pathSources.put(source,source);
		});

		while(!frontier.isEmpty()) {
			Tile curTile = frontier.poll();

			Tile curTileSource = pathSources.get(curTile);
			if(one_target_per_source && completedSources.contains(curTileSource))
				continue;//continue while

		 **
		 * for(Entry<Directions, Tile> neighborEntry : curTile.getNeighbour().entrySet()) {
				Tile neighborTile = neighborEntry.getValue();
				Directions neighborDirection = neighborEntry.getKey();
		 *

			curTile.getNeighbour().entrySet().parallelStream().forEachOrdered(
					neighbourEntry -> { 
						Tile neighbourTile = neighbourEntry.getValue();
						Directions neighbourDirection = neighbourEntry.getKey();

						if(!neighbourTile.isSuitable() || !pathSources.containsKey(neighbourTile)) {

							pathSources.put(neighbourTile,curTileSource);

							directionFromSource.put(curTile, neighbourDirection);/*
							if(directionFromSource.containsKey(curTile))
								directionFromSource.put(neighbourTile, directionFromSource.get(curTile));
							else
								directionFromSource.put(neighbourTile, neighbourDirection);*

							directionFromTarget.put(neighbourTile, neighbourDirection.getOpponent());

							if(targets.contains(neighbourTile)) {
								results.add(neighbourTile);
								if(one_target_per_source) {//FIXME
									completedSources.add(curTileSource);
									createOneOrder(curTile, curTileSource, directionFromTarget.get(curTile));
								}else createOneOrder(curTileSource, curTile, directionFromSource.get(curTileSource));
								//TODO: di regola non bisogna rimuovere il targhet
								//ma se viene rieseguita la ricerca, è necessario rimuoverlo
							}//*else*

							frontier.add(neighbourTile);
						}//continue for
					});
		}
		//return result, pathSources, directionFromSource, directionFromTarget; //TODO
		//computeOrders(); cambiare in base a come vengono impostati target e source
		return this.results;
		 */




		Queue<Tile> frontier = new LinkedList<Tile>();
		Set<Tile> completedSources = new HashSet<>();
		//Set<Tile> visited = new TreeSet<>(Tile.tileComparator());
		Map<Tile,Set<Tile>>  visited =new HashMap<>();
		Map<Tile,Set<Tile>> closedList = new HashMap<>();

		sources.parallelStream().forEachOrdered(source -> {
			frontier.add(source);
			pathSources.put(source,source);
			//visited.add(source);
			Set<Tile> expanded = new HashSet<Tile>();
			expanded.add(source);
			visited.put(source, expanded);
			closedList.put(source, expanded);
		});

		//while(!frontier.isEmpty() || !(!(results.containsAll(targets) || targets.isEmpty()) || orderTile.containsAll(sources))) { //FIXME togliere targhets
		while(!frontier.isEmpty()) {
			Tile curTile = frontier.poll();

			Tile curTileSource = pathSources.get(curTile);

			Set<Tile> expanded = closedList.get(curTileSource);
			expanded.remove(curTile);


			if( completedSources.contains(curTileSource))
				continue;//continue while


			Iterator<Entry<Directions, Tile>> neighboursIt = curTile.getNeighbour().entrySet().iterator();
			while(neighboursIt.hasNext()) {
				Entry<Directions, Tile> neighbourEntry= neighboursIt.next();
				Tile neighbourTile = neighbourEntry.getValue();
				Directions neighbourDirection = neighbourEntry.getKey();

				//neighbourTile.isSuitable() &&
				//if( !visited.containsKey(neighbourTile) || !visited.get(neighbourTile).contains(curTileSource)) {
				if( ((one_target_per_source || reverse) && !pathSources.containsKey(neighbourTile)) ^ (!(one_target_per_source ^ reverse) && (!visited.containsKey(neighbourTile) || !visited.get(neighbourTile).contains(curTileSource)))) {

					pathSources.put(neighbourTile,curTileSource);



					if(!(one_target_per_source ^ reverse))
						directionFromSource.put(neighbourTile, directionFromSource.containsKey(curTile) ? directionFromSource.get(curTile) : neighbourDirection);
					else 
						directionFromSource.put(curTile, neighbourDirection);
					directionFromTarget.put(neighbourTile, neighbourDirection.getOpponent());

					if(targets.contains(neighbourTile)) {
						//System.out.println("cur"+curTile + " nei"+neighbourTile +"("+neighbourDirection+") " + "sourc" + curTileSource);

						boolean checkOrder = false;
						//TODO aggiungere solo se create one order ha restituito true
						if(one_target_per_source) {//FIXME

							checkOrder = createOneOrder(neighbourTile, curTileSource, directionFromTarget.get(neighbourTile));
							if(checkOrder) {
								targets.remove(neighbourTile);
								completedSources.add(curTileSource);
							}
						}else {
							if(reverse)
								checkOrder = createOneOrder(neighbourTile, curTileSource, directionFromTarget.get(neighbourTile));
							else {
								checkOrder = createOneOrder(curTileSource, neighbourTile, directionFromSource.get(neighbourTile));
								//checkOrder = createOneOrder(curTileSource, neighbourTile, directionFromSource.get(curTileSource));
								completedSources.add(curTileSource);
							}
						}


						if(checkOrder) {
							results.add(neighbourTile);
							//frontier.removeAll(expanded);

						}	

						break;
					} else {


						if(!visited.containsKey(neighbourTile) || !visited.get(neighbourTile).contains(curTileSource)) {

							if(!visited.containsKey(neighbourTile)) {
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
		}

		//sources.forEach(s -> System.out.println(s +" -> " +directionFromSource.get(s)));
		//return result, pathSources, directionFromSource, directionFromTarget; //TODO
		//computeOrders(); cambiare in base a come vengono impostati target e source
		return this.results;
	}
	
	public HashSet<Tile> getOrderTile() {
		return orderTile;
	}

}

