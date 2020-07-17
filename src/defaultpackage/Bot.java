package defaultpackage;

import game.Game;
import timing.Modules;
import timing.Timing;

/**
 * Provides basic game state handling.
 */
public abstract class Bot extends AbstractSystemInputParser {
	private Game state;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(int rows, int cols, int viewRadius2, int attackRadius2, int spawnRadius2) {
		setGame(new Game(rows, cols, viewRadius2, attackRadius2, spawnRadius2));
	}

	/**
	 * Returns game state information.
	 * 
	 * @return game state information
	 */
	public Game getGame() {
		return state;
	}

	/**
	 * Sets game state information.
	 * 
	 * @param ants game state information to be set
	 */
	protected void setGame(Game new_state) {
		this.state = new_state;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeUpdate() {
		Timing.setTurnStartTime(); 
		time.start(Modules.Parse);
		state.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addWater(int row, int col) {
		state.setWater(row, col);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAnt(int row, int col, int owner) {
		state.setAnt(row, col, owner);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addFood(int row, int col) {
		state.setFood(row, col);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeAnt(int row, int col, int owner) {
		state.setDead(row, col, owner);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addHill(int row, int col, int owner) {
		state.setHills(row, col, owner);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterUpdate() {
		state.doVision();
		time.end(Modules.Parse);
	}
}
