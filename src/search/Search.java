package search;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import vision.Offsets;
import vision.Offset;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import game.Directions;
import game.Game;
import game.Tile;

import java.util.Collections;
import java.util.Comparator;
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
	 * tile di cibo verso la formica pi� vicina.//TODO
	 */
	private Boolean search_from_one_source;

	public Search(Set<Tile> sources, Set<Tile> targets, Integer radius, Boolean heuristic, Boolean search_from_one_source) {
		this.sources = sources;
		this.targets = targets;
		this.radius = radius;
		this.heuristic = heuristic;
		this.search_from_one_source = search_from_one_source;
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
			return staticSearch();
		else if (heuristic) {//se i targhet sono tutti di un certo tipo 
			
			return extendedAStar();
			
		} else {
			
			return extendedBFS();
			
		}
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
	private void callback(Tile result, Tile target, Directions cardinal, Directions opposite) {
		// TODO
		//Game.setOrder() //ricordati di rimuovere la formica dalla lista delle formiche disponibili
		
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

		Set<Tile> results = new TreeSet<Tile>();// Collections.<Tile>emptySet();

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
		PriorityQueue<TileExtended> frontier = new PriorityQueue<TileExtended>();
		Set<TileExtended> expandedTile = new HashSet<>();
		Map<TileExtended,TileExtended> pathSources = new HashMap<>();
		Set<Tile> results = new HashSet<>();
		Map<Tile, Directions> directionFromSource = new HashMap<>();
		Map<Tile, Directions> directionFromTarget = new HashMap<>();
		Set<Tile> completedSources = new HashSet<>();

		//assegnare targhet ad ogni sorgente
		//il targhet piu vicino

		sources.forEach(source -> {
			TileExtended curSource = new TileExtended(source,targets);
			frontier.add(curSource);
			pathSources.put(curSource,curSource);
		});

		while(!frontier.isEmpty()) {

			TileExtended curTile = frontier.poll();
			TileExtended curTileSource = pathSources.get(curTile);

			expandedTile.add(curTile);//FIXME

			if(search_from_one_source && completedSources.contains(curTileSource.getTile()))//FIXME
				continue;//continue while

			if(targets.contains(curTile.getTile())) {
				results.add(curTile.getTile());
				if(search_from_one_source)//FIXME
					completedSources.add(curTileSource.getTile());
			}

			for(Entry<Directions, Tile> neighbourEntry : curTile.getTile().getNeighbour().entrySet()) {
				Tile neighbourTile = neighbourEntry.getValue();
				Directions neighborDirection = neighbourEntry.getKey();
				TileExtended neighbour = new TileExtended(neighbourTile,curTileSource.getTarget(),curTile.getPathCost());
				

				if(neighbourTile.isSuitable() || expandedTile.contains(neighbourTile))//FIXME see isSuitable
					continue;
				if( !pathSources.containsKey(neighbourTile) || expandedTile.parallelStream().filter(x -> x.equals(neighbour)).allMatch(x -> curTile.getPathCost()+1 < x.getPathCost())) {//FIXME
					//TileExtended neighbour = new TileExtended(neighbourTile,curTileSource.getTarget(),curTile.getPathCost()); //FIXME altrimenti curTileSource.getTarget()
					
					pathSources.put(neighbour,curTileSource);

					directionFromSource.put(neighbourTile, 
							directionFromSource.containsKey(curTile) ? directionFromSource.get(curTile) : neighborDirection);

					directionFromTarget.put(neighbourTile, neighborDirection.opponent());

					frontier.add(neighbour);
				}
			}
		}
		//result Results, PathSources, DirectionFromSource, DirectionFromTarghet;
	}

	private Set<Tile> extendedBFS() {

		Queue<Tile> frontier = new LinkedList<Tile>();
		Map<Tile,Tile> pathSources = new HashMap<>();
		Set<Tile> results = new HashSet<>();
		Map<Tile, Directions> directionFromSource = new HashMap<>();
		Map<Tile, Directions> directionFromTarget = new HashMap<>();
		Set<Tile> completedSources = new HashSet<>();

		sources.parallelStream().forEachOrdered(source -> {
			frontier.add(source);
			pathSources.put(source,source);
		});

		while(!frontier.isEmpty()) {
			Tile curTile = frontier.poll();

			Tile curTileSource = pathSources.get(curTile);
			if(search_from_one_source && completedSources.contains(curTileSource))
				continue;//continue while

			/**
			 * for(Entry<Directions, Tile> neighborEntry : curTile.getNeighbour().entrySet()) {
				Tile neighborTile = neighborEntry.getValue();
				Directions neighborDirection = neighborEntry.getKey();
			 */

			curTile.getNeighbour().entrySet().parallelStream().forEachOrdered(
					neighbourEntry -> { 
						Tile neighbourTile = neighbourEntry.getValue();
						Directions neighbourDirection = neighbourEntry.getKey();

						//TODO guarda Tile.isSuitable()
						if(neighbourTile.isSuitable() && !pathSources.containsKey(neighbourTile)) {

							pathSources.put(neighbourTile,curTileSource);

							if(directionFromSource.containsKey(curTile))
								directionFromSource.put(neighbourTile, directionFromSource.get(curTile));
							else
								directionFromSource.put(neighbourTile, neighbourDirection);

							directionFromTarget.put(neighbourTile, neighbourDirection.opponent());

							if(targets.contains(neighbourTile)) {
								results.add(neighbourTile);
								if(search_from_one_source)//FIXME
									completedSources.add(curTileSource);
								//TODO: di regola non bisogna rimuovere il targhet
								//ma se viene rieseguita la ricerca, è necessario rimuoverlo
							}

							frontier.add(neighbourTile);
						}
					});
		}
		//return result, pathSources, directionFromSource, directionFromTarget; //TODO
	}
}

