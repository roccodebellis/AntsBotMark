package timing;

public class Timing {
	
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
	private final long turnTime;
	
	private static long turnStartTime;
	
	private long vision;
	private long combat;
	private long food;
	private long defense;
	private long exploration;
		
	public Timing(long loadTime, long turnTime, int turns) {
		this.loadTime = loadTime;
		maxTurns = turns;
		this.turnTime = turnTime;
		vision=0;
		combat=0;
		food=0;
		defense=0;
		exploration=0;
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
	
	/**
	 * Returns timeout for a single game turn, starting with turn 1.
	 * 
	 * @return timeout for a single game turn, starting with turn 1
	 */
	public long getTurnTime() {
		return turnTime;
	}
	
	/**
	 * Sets turn start time.
	 * 
	 * @param turnStartTime turn start time
	 */
	public static void setTurnStartTime() {
		turnStartTime = System.currentTimeMillis();
	}

	/**
	 * Returns how much time the bot has still has to take its turn before timing
	 * out.
	 * 
	 * @return how much time the bot has still has to take its turn before timing
	 *         out
	 */
	public long getTimeRemaining() {
		return turnTime - (System.currentTimeMillis() - turnStartTime);
	}

	public static long getCurTime() {	
		return System.currentTimeMillis();
	}
	
	public long getVisionTime() {
		return vision;
	}
	
	public long getCombatTime() {
		return combat;
	}
	
	public long getFoodTime() {
		return food;
	}

	public long getDefenseTime() {
		return defense;
	}

	public long getExplorationTime() {
		return exploration;
	}
	
	public void update(long module, long start) {
		module = (long) ((start-getCurTime()) * 0.85 + module * 0.15);
	}
	
	public long getCombatTimeStime(){
		return getTimeRemaining() - (vision+food+defense+exploration);
	}
	
	
	
	
}
