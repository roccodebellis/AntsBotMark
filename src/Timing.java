import moa.core.TimingUtils;

public class Timing {
	
	/**
	 *  //TODO preso da game
	 */
	private final int loadTime;
	
	/**
	 *  //TODO preso da game
	 */
	private final int turnTime;
	
	
	/**
	 * 
	 */
	private static long turnStartTime;
	
	//MODULI Combattimento
	
	//0.8
	Lista.addTempo(MOdulo) = nuovo tempo utilizzato da quel modulo * 0.9 + 0.1 * Lista.getTempo(Modulo);
	//lista di moduli che stimano quanto tempo in media utilizza un modulo per effettuare
	//effettuare i calcoli che deve fare 
	
	//tempo Combattimento = Tempo rimanente - sommatoria dell tempo medio utilizzato da ogni modulo
	
	
	/**
	 * Sets turn start time.
	 * 
	 * @param turnStartTime turn start time
	 */
	public static void setTurnStartTime(long turnStartTime) {
		this.turnStartTime = turnStartTime;
	
		System.nanoTime()
		long evalutateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
		double time = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread()-evalutateStartTime);
		
	}

	/**
	 * Returns how much time the bot has still has to take its turn before timing
	 * out.
	 * 
	 * @return how much time the bot has still has to take its turn before timing
	 *         out
	 */
	public int getTimeRemaining() {
		return turnTime - (int) (System.currentTimeMillis() - turnStartTime);
	}
	
	/**
	 * Returns timeout for initializing and setting up the bot on turn 0.
	 * 
	 * @return timeout for initializing and setting up the bot on turn 0
	 */
	public int getLoadTime() {
		return loadTime;
	}

	/**
	 * Returns timeout for a single game turn, starting with turn 1.
	 * 
	 * @return timeout for a single game turn, starting with turn 1
	 */
	public int getTurnTime() {
		return turnTime;
	}

}
