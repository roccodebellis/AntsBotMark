package timing;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Timing {
	
	private static int turnNo;
	/**
	 * in milliseconds, time given for bot to start up after it is given "ready" (see below)
	 */
	private final long loadTime;
	
	/**
	 * maximum number of turns in the game
	 */
	private static int maxTurns;
	
	/**
	 * in milliseconds, time given to the bot each turn
	 */
	private static long _turnTime;
	
	private static long turnStartTime;
	
	private static Map<Modules,Long> modulesTime;
	private Map<Modules,Long> modulesTimeStart;
		
	public Timing(long loadTime, long turnTime, int turns) {
		turnNo = 0;
		this.loadTime = loadTime;
		maxTurns = turns;
		_turnTime = turnTime;
		modulesTime = new TreeMap<Modules,Long> ();
		modulesTimeStart = new TreeMap<Modules,Long> ();
		
		EnumSet.allOf(Modules.class).parallelStream().forEachOrdered(v -> {modulesTime.put(v, 0L);modulesTimeStart.put(v, 0L);});
		
	}
	
	public void start(Modules module) {
		modulesTimeStart.put(module, getCurTime());
	}
	public void end(Modules module) {
		if(turnNo>1)
			modulesTime.put(module, (long) ((getCurTime()-modulesTimeStart.get(module)) * 0.9 + modulesTime.get(module) * 0.1));
		else
			modulesTime.put(module, (getCurTime()-modulesTimeStart.get(module)));
	}
	
	
	public static long getCombatTimeStime(){
		return getTimeRemaining() - modulesTime.entrySet().stream().mapToLong(Map.Entry::getValue).sum();
	}

	/**
	 * Returns timeout for initializing and setting up the bot on turn 0.
	 * 
	 * @return timeout for initializing and setting up the bot on turn 0
	 */
	public long getLoadTime() {
		return loadTime;
	}
	
	public static int getTurnLeft(int currentTurn) {
		return maxTurns - currentTurn;
	}
	
	public static int getMaxTurns() {
		return maxTurns;
	}
	
	/**
	 * Returns timeout for a single game turn, starting with turn 1.
	 * 
	 * @return timeout for a single game turn, starting with turn 1
	 */
	public long getTurnTime() {
		return _turnTime;
	}
	
	/**
	 * Sets turn start time.
	 * 
	 * @param turnStartTime turn start time
	 */
	public static void setTurnStartTime() {
		turnStartTime = System.currentTimeMillis();
		turnNo++;
	}

	public static int getTurnNumber() {
		return turnNo;
	}
	
	/**
	 * Returns how much time the bot has still has to take its turn before timing
	 * out.
	 * 
	 * @return how much time the bot has still has to take its turn before timing
	 *         out
	 */
	public static long getTimeRemaining() {
		return _turnTime - (System.currentTimeMillis() - turnStartTime);
	}

	public static long getCurTime() {	
		return System.currentTimeMillis();
	}
	
	

	
	
	
}
