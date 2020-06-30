package defaultpackage;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import game.Game;
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

	/**
     * To track information about where ants are moving,
     * we are going to use a HashMap.
     * It is a data structure that will store locations,
     * and then allow us to check if a location has already been stored.
     * Each key and value of the HashMap will be a Tile object.
     * A Tile object is the row and column of a location on the map.
     * The <b>key</b> will be the new location to move to and
     * the <b>value</b> will be the location of the ant moving to the new location.
     * We can then check the HashMap before making a move
     * to ensure we don't move 2 ants to the same spot.
     * Every time we move an ant, we need to be sure to update the HashMap.
     * This check will come in handy later in the tutorial,
     * so we will make a function to attempt moves and
     * check to make sure the move is to an empty location.
     * It will return a boolean (true or false) to let us know if the move worked.
     */
    private Map<Tile, Tile> orders = new HashMap<Tile, Tile>();
	
    /**
     * The unseenTiles will be a set of all tiles
     * we have not seen during the game.
     * (Note: this could be a large list and not very memory efficient.
     * This is just the easiest way to make the code look nice.
     * You'll probably want to try and use a different technique for a real bot.)
     */
    private Set<Tile> unseenTiles;

    /**
     * This will be the list of all enemy hills we have found.
     */
    private Set<Tile> enemyHills = new HashSet<Tile>();
    


	

	
	


	@Override //DA FARW
	public void doTurn() {
		Game state = getGame();
		
		state.doCombat();
		
		state.doFood();
		
		state.doDefense();
		
		state.doExploration();
		
		//1 VISION MODULE
		//2 COMBAT SIMULATION
		//3 FOOD COLLECTION
		//2.5\3.5 HILL ATTACK AND DEFENSE 
		//4 EXPLORATION AND MOVEMENTS
		
	}

}
