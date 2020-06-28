package vision;

import java.util.Set;
import java.util.TreeSet;
import game.Game;
import game.Tile;

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
	private final static Offsets visionOffsets = new Offsets((int) Math.sqrt(Game.getViewRadius()));
	
	private static Set<Tile> inVision;
	
	private static Set<Tile> outOfSight;//non so se sia meglio conservarli qui o meno
	
	private static void computeTilesInVision() {
		inVision = new TreeSet<Tile>();
		Game.getMyAnts().parallelStream().forEachOrdered(ant -> inVision.addAll(Game.getTiles(ant, visionOffsets)));
		inVision.forEach(tile -> { tile.setVisible(true); Game.getUnexplored().remove(tile);});
	}
	
	public static void clearAllVision(){
		Game.getMap().parallelStream().forEachOrdered(tList -> tList.parallelStream().forEachOrdered(tile -> tile.setVisible(false)));
	}
	
	public static void clearAntsVision() {
		inVision.parallelStream().forEach(tile -> tile.setVisible(false));
	}
	
	public static void setVision() {
		Set<Tile> allTile = new TreeSet<Tile>(Tile.visionComparator());
		computeTilesInVision();
		Game.getMap().forEach(row -> allTile.addAll(row));
		allTile.removeAll(inVision);
		allTile.removeAll(Game.getUnexplored());
		allTile.removeAll(Game.getWater());
		allTile.forEach(tile -> tile.setVisible(false));
		outOfSight = allTile;//non so se sia meglio tenerli qui o meno
		//per il momento lo setto sia qui che in Game, poi si decide
		Game.setOutOfSight(outOfSight);
	}
	
	//473 Game setVision static search
}
