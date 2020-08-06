package vision;

import java.util.HashMap;
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
		visionOffsets = new Offsets(viewRadius);
		inVision = new HashSet<Tile>();
		enemyToAnt = new HashMap<Node,Tile>();
		hillDefenceTargets = new TreeMap<Tile,Set<Tile>>();
	}

	public void addHillToDefend(Tile hill) {
		hillDefenceTargets.put(hill, Game.getTiles(hill, Offsets.getDefenceHillOffsets()));
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

	public void setVision(Set<Tile> myAnts_visible) {
		inVision.clear();
		/*
		visible_ants.parallelStream().forEachOrdered(ant -> inVision.addAll(Game.getTiles(ant, visionOffsets)));
		inVision.forEach(tile ->  Game.setVisible(tile,true));
		 */

		//for each visible ants we get the tiles in vision and we update the visibility incrementing
		//the tile vision index of 1
		myAnts_visible.parallelStream().forEachOrdered(
				ant -> 
				//for each tile in vision of the current ant, we update the vision index of the tile
				Game.getTiles(ant, visionOffsets).parallelStream().forEachOrdered(visibleTile ->{
					//setting the vision
					updateVision(visibleTile);


					//if the Tile (as a Node) contains an enemy, we update enemyToAnt
					//setting the value as the Node (wich corresponds to the enemy)
					//and (one of) its key as the ant (which it's able to see)
					if(visibleTile.isOccupiedByAnt() && visibleTile.getOwner() != 0)
						updateEnemyToAnt(visibleTile, ant);
					//antToEnemy.put(ant,tileVisible);
				}));
		updateOutOfSight();		
	}

	//TODO re-read it, not sure it has the behaviour we expect (see compareTo in Node)
	//non funzia
	private void updateEnemyToAnt(Tile enemyTile, Tile ant) {
		Node enemyTileAsANode = new Node(enemyTile,ant);
		if((enemyToAnt.containsKey(enemyTileAsANode) &&  Game.getDistance(ant, enemyTile) < Game.getDistance(enemyToAnt.get(enemyTileAsANode), enemyTile)) || !enemyToAnt.containsKey(enemyTileAsANode)) 
			enemyToAnt.put(enemyTileAsANode,ant);
		//if the distance between the ant and the enemy is lower than the distance between
		//another ant (already contained in enemyToAnt) and the enemy
		//we add another node with the enemy and the current ant

	}

	private void updateOutOfSight() {
		outOfSight = new TreeSet<Tile>(Tile.visionComparator());
		//outOfSight = new HashSet<Tile>();
		outOfSight.addAll(Game.getMapTiles());
		outOfSight.removeAll(inVision);
		outOfSight.removeAll(Game.getUnexplored());
		outOfSight.removeAll(Game.getWater());
	}

	private void updateVision(Tile visibleTile) {
		if(inVision.add(visibleTile))
			Game.setVisible(visibleTile,true);
	}

	public Map<Node,Tile> getEnemyToAnt() {
		return enemyToAnt;
	}

	public Set<Tile> getOutOfSight(){
		return outOfSight;
	}


}
