package defaultpackage;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import attackdefencehills.AttackDefenceHills;
//import attackdefensehills.AttackDefenseHills;
import game.Directions;
import game.Game;
import game.Order;
import game.Tile;


public class MyBot extends Bot {

	/**
	 * Main method executed by the game engine for starting the bot.
	 * 
	 * @param args command line arguments
	 * 
	 * @throws IOException if an I/O error occurs
	 */
	public static void main(String[] args) throws IOException {
		new MyBot().readSystemInput();
	}

	@Override //DA FARW
	public void doTurn() {
		Game state = getGame();

		//1 VISION MODULE
		//2 COMBAT SIMULATION
		//state.doCombat();
		//2.5\3.5 HILL ATTACK AND DEFENSE
		//state.doDefence();
		//state.doDefenceHills();
		//3 FOOD COLLECTION
		state.doFood();
		
		state.doAttackHills();
		//4 EXPLORATION AND MOVEMENTS
		state.doExploration();

		//Game.printMapVision();
		//Game.printNeigbour();
	}
}
