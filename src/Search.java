import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Search {

	/**
	 * Una o piu' posizioni di partenza da cui far partire la ricerca
	 */
	private Set<Tile> sources; // TODO getter setter

	/**
	 * Insieme di obiettivi: possono essere Tile e/o tipologia dell'obbiettivo
	 */
	private Set<Tile> targets;// TODO
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
	private Boolean multi_target = false;
	/**
	 * Se limitare la ricerca ed includere solo un {@link #targets} per ogni tile di
	 * partenza {@link #source}.<br>
	 * Impostato a true se si vuole effettuare una ricerca a partire da un singolo
	 * tile di cibo verso la formica pi� vicina.//TODO
	 */
	private Boolean search_from_one_source = false; // TODO MODIFICA NOME

	// Set<Tile> offsets = new HashSet<Tile>();//TODO

	Search(Set<Tile> sources, Set<Tile> targets, Integer radius, Boolean multi_target, Boolean search_from_one_source) {
		this.sources = sources;
		this.targets = targets;
		this.radius = radius;
		this.multi_target = multi_target;
		this.search_from_one_source = search_from_one_source;
		// TODO
	}

	/**
	 * Viene eseguita ogni volta che viene trovata una tile contenente un obiettivo
	 * Ordina una mossa dalla formica trovata nella direzione del cibo
	 * 
	 * @param t        tile contenente l'obiettivo/target (O coordinate, da decidere
	 *                 //TODO)
	 * @param origin   tile di partenza da cui ha avuto inizio il percorso che ha
	 *                 permesso di scovare il target {code t}
	 * @param cardinal la direzione da cui inizia il percorso partendo da {code
	 *                 origin}
	 * @param opposite la direzione inversa con cui il percorso arriva al target
	 *                 {@code t}
	 */
	private void callback(Tile t, Tile origin, Directions cardinal, Directions opposite) {
		// TODO
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
		if (!radius.equals(null))
			return staticSearch(sources, targets);
		else if (HeuristicAivalableFor(targets))
			return extendedAStar(sources, targets);
		else
			return extendedBFS(sources, targets);
	}

	/**
	 * Restituisce un flag che asserisce se la ricerca e' limitata dal raggio,
	 * {@link #radius}:
	 * <ul>
	 * <li>Se {@code radius} ha valore pari ad x significa che l'obiettivo e'
	 * raccogliere cibo. In tal caso viene restituito {@code true};</li>
	 * <li>Se {@code radius} ha valore pari a y significa che l'obiettivo e'
	 * attaccare. In tal caso viene restituito {@code true};</li>
	 * <li>Se {@code radius} ha valore pari ad z significa che l'obiettivo e'
	 * esplorare. in tal caso viene restituito {@code false};</li>
	 * </ul>
	 * 
	 * @return {@code true} se il raggio e' limitato; {@code false} altrimenti.
	 */
	private Boolean isLimited() {
		
		return null;// TODO da controllare
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

		// radius

		Set<Tile> results = Collections.<Tile>emptySet();// new HashSet<Tile>();

		Set<Offset> offsets = Offsets.getOffsets(radius);

		sources.forEach(source -> offsets.forEach(offset -> {
			Tile curTile = Game.getTile(source, offset);
			// if(targets.apply(curfile))
			if (TileMeetsTargetCriteria(curTile, targets))
				results.add(curTile);

		}));

		return results;
	}

	private Boolean HeuristicAivalableFor(HashMap<AimType, Tile> targets) {
		// se si conosce posizione dell'obiettivo, distanza di Manhattan,
		// CASI: GatherFood, AttackHill

		return false;// TODO
	}

	private Set<Tile> extendedAStar() {
		PriorityQueue<TileExtended> frontier = new PriorityQueue<TileExtended>();
		Set<Tile> expandedTile = new HashSet<>();
		Map<Tile,Tile> pathSources = new HashMap<>();
		Set<Tile> results = new HashSet<>();
		Map<Tile, Directions> directionFromSource = new HashMap<>();
		Map<Tile, Directions> directionFromTarget = new HashMap<>();
		Set<Tile> completedSources = new HashSet<>();

		//assegnare targhet ad ogni sorgente
		//il targhet piu vicino 

		sources.forEach(source -> {
			frontier.add(source, euristica);
			pathSources.put(source,source);
		});

		while(!frontier.isEmpty()) {

			TileExtended curTile = frontier.poll();
			TileExtended curTileSource = pathSources.get(curTile);

			expandedTile.add(curTile);

			if(!(completedSources.contains(curTileSource) && search_from_one_source))//FIXME
				break;//continue while

			if(TileMeetsTargetCriteria(curTile, targets)) {
				results.add(curTile);
				if(search_from_one_source)//FIXME
					completedSources.add(curTile);
			}

			for(Entry<Directions, Tile> neighborEntry : curTile.getAdjacentNodes().entrySet()) {
				TileExtended neighborTile = neighborEntry.getValue();
				Directions neighborDirection = neighborEntry.getKey();

				if(!(TileIsUnsuitable(neighborTile) || expandedTile.contains(neighborTile)))//FIXME
					break;
				if( !pathSources.containsKey(neighborTile) || curTile.getCost()+1 < neighborTile.getCost()) {//TODO +1
					pathSources.put(neighborTile,curTileSource);

					int neighborCost = curTile.getCost()+1;
					neighborTile.setCost(neighborCost);

					directionFromSource.put(neighborTile, 
							directionFromSource.containsKey(curTile) ? directionFromSource.get(curTile) : neighborDirection);

					directionFromTarget.put(neighborTile, neighborDirection.opponent());

					frontier.add(neighborTile,neighborCost);
				}
			}
		}
		result Results, PathSources, DirectionFromSource, DirectionFromTarghet;
	}

	private Set<Tile> extendedBFS(Set<Tile> sources, HashMap<AimType, Tile> targets) {
		Queue<Tile> frontier = new LinkedList<Tile>();
		Map<Tile,Tile> pathSources = new HashMap<>();
		Set<Tile> results = new HashSet<>();
		Map<Tile, Directions> directionFromSource = new HashMap<>();
		Map<Tile, Directions> directionFromTarget = new HashMap<>();
		Set<Tile> completedSources = new HashSet<>();

		sources.forEach(source -> {
			frontier.add(source);
			pathSources.put(source,source);
		});

		while(!frontier.isEmpty()) {
			Tile curTile = frontier.poll();
			Tile curTileSource = pathSources.get(curTile);
			if(!(completedSources.contains(curTileSource) && search_from_one_source))//FIXME
				break;//continue while

			for(Entry<Directions, Tile> neighborEntry : curTile.getAdjacentNodes().entrySet()) {
				Tile neighborTile = neighborEntry.getValue();
				Directions neighborDirection = neighborEntry.getKey();

				if(!(TileIsUnsuitable(neighborTile) || pathSources.containsKey(neighborTile)))//FIXME
					break;

				pathSources.put(neighborTile,curTile);

				if(directionFromSource.containsKey(curTile))
					directionFromSource.put(neighborTile, directionFromSource.get(curTile));
				else
					directionFromSource.put(neighborTile, neighborDirection);

				directionFromTarget.put(neighborTile, neighborDirection.opponent());

				if(TileMeetsTargetCriteria(neighborTile, targets)) {
					results.add(neighborTile);
					if(search_from_one_source)//FIXME
						completedSources.add(curTileSource);
				}
				frontier.add(neighborTile);
			}
		}
		return result, pathSources, directionFromSource, directionFromTarget;
		/*Extended breadth-first search. Based on a classic breadth-first search 
		 * [Cormen et al., 2001, p. 531 ff.], our variation allows the use of multiple sources.
		 *  All sources are added to the open set initially and are treated as normal unexpanded 
		 *  nodes. Search returns all found target squares, their respective sources, and two 
		 *  directions (path beginning direction from source and reverse path beginning direction
		 *   from target).
		 */
	}

	/*

	// Tile a sinistra del Map (chiave) e' il target, a destra (valore) e' source
	private Map<Tile, Tile> assignTargets(List<Tile> s, Targets t) {              //DA RIVEDERE
		Map<Tile, Tile> assignments = new TreeMap<Tile, Tile>();

		Tile source;
		Tile target;

		Iterator<Tile> it = s.iterator();


		while (it.hasNext()) {
			source = it.next();
			if (assignments.containsKey(target = getTarget(source, t))) {
				if (target.getDistance(assignments.get(target)) > target.getDistance(source))
					assignments.put(target, source);
			} else
				assignments.put(target, source);
		}
	}

	private Tile getTarget(Tile source, Targets t) {  ///STA BENE
		Iterator<Tile> it = t.iterator();
		Tile min;
		int minDist;


		 //t.setSource(source); //Target deve implemenare comparable o comparator sulla distanza
		 // Tile min = Collection.min(t.getList());


		if(it.hasNext()) {
			min = it.next();
			minDist = min.getDistance(source);
		}
		while (it.hasNext()) {
			Tile next = it.next();
			int nextDist = next.getDistance(source);
			if (nextDist < minDist) {
				min = next;
				minDist = nextDist;
			}
		}

		return min;
	}*/
}

