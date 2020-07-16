package vision;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import game.Game;
import game.Tile;
import search.Node;

/**
 * Vision module is the very first module to be run at a beginning of the
 * agent's turn. Its task is to provide other modules with information on
 * whether given map squares are visible or covered by fog-of-war.
 * 
 * 
 * To accomplish
 * this, the vision module first assigns all map squares as invisible.
 * 
 * The
 * squares that the agent was given explicit information about on this turn are
 * then marked visible. The module then runs a ADAPTIVESEARCH seeded from all
 * the agent's ants with a radius limited to the game's configured
 * ViewRadius. Since the radius is limited, the search will always run a
 * STATICSEARCH, with a minimal execution time. Every square found by the search
 * is marked visible.
 * 
 * @author Debellis, Lorusso
 *
 */
public class Vision {
	//necessari tutti questi static?? forse no, inizializzazione nel costruttore
	//tutti questi metodi li utilizza solo Game (potrebbe Bot ma ho fatto in modo di no)
	//quindi si potrebbe creare oggetto Vision in Game ed eliminare tutti questi static
	/**
	 * {@link Offsets} 
	 * 
	 */
	private Offsets visionOffsets;

	private Set<Tile> inVision;

	private Set<Tile> outOfSight;//non so se sia meglio conservarli qui o meno

	private Set<Tile> mapTiles;
	
	private Map<Node,Tile> enemyToAnt;
	
	private static Map<Tile,Set<Tile>> hillDefenceTargets; 

	public Vision(Set<Tile> mapTiles, int viewRadius){
		this.mapTiles = mapTiles;
		visionOffsets = new Offsets((int) Math.sqrt(viewRadius));
		inVision = new HashSet<Tile>();
		enemyToAnt = new TreeMap<Node,Tile>();
		hillDefenceTargets = new TreeMap<Tile,Set<Tile>>();
	}
	
	public void addHillToDefend(Tile hill) {
		hillDefenceTargets.put(hill, Game.getTiles(hill, new Offsets(-5)));
	}
	
	private void removeDefenceTargets(Tile hill, Tile target) {
		hillDefenceTargets.get(hill).remove(target);
	}
	
	public static Set<Tile> getHillDefenceTargets(Tile hill) {
		return hillDefenceTargets.get(hill);
	}
	
	public Set<Tile> getHillsToDefend() {
		return hillDefenceTargets.entrySet().parallelStream().map(entry -> entry.getKey()).collect(Collectors.toSet());
	}
	
	public void removeHillToDefend(Tile hill) {
		hillDefenceTargets.remove(hill);
	}

	public void clearAllVision(){
		enemyToAnt.clear();
		mapTiles.parallelStream().forEachOrdered(tile -> Game.setVisible(tile,false));
	}

	public void setVision(Set<Tile> visible_ants) {
		inVision.clear();
		/*
		visible_ants.parallelStream().forEachOrdered(ant -> inVision.addAll(Game.getTiles(ant, visionOffsets)));
		inVision.forEach(tile ->  Game.setVisible(tile,true));
		 */
		visible_ants.parallelStream().forEachOrdered(
				ant -> 
				Game.getTiles(ant, visionOffsets).parallelStream().forEachOrdered(tileVisible ->{
					Node visibleTileEx = new Node(tileVisible,ant);
					inVision.add(tileVisible);
					Game.setVisible(tileVisible,true);
					if(tileVisible.isOccupiedByAnt() && tileVisible.getOwner() != 0)
						if(enemyToAnt.containsKey(visibleTileEx) ) {
							if(Game.getDistance(ant, tileVisible) < Game.getDistance(enemyToAnt.get(visibleTileEx), tileVisible))
								visibleTileEx = new Node(tileVisible,ant);
						} else 
							enemyToAnt.put(visibleTileEx,ant);
						
						//antToEnemy.put(ant,tileVisible);
				}));



		outOfSight = new TreeSet<Tile>(Tile.visionComparator());
		//outOfSight = new HashSet<Tile>();
		
		outOfSight.addAll(Game.getMapTiles());
		outOfSight.removeAll(inVision);
		outOfSight.removeAll(Game.getUnexplored());
		outOfSight.removeAll(Game.getWater());		
	}
	
	public Map<Node,Tile> getEnemyToAnt() {
		return enemyToAnt;
	}

	public Set<Tile> getOutOfSight(){
		return outOfSight;
	}

	
}
