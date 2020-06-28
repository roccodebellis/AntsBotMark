package vision;

/**
 * Vision module is the very first module to be run at a beginning of the
 * agent's turn. Its task is to provide other modules with information on
 * whether given map squares are visible or covered by fog-of-war. To accomplish
 * this, the vision module first assigns all map squares as invisible. The
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

}
