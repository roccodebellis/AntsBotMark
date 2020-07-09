package gathering;

import java.util.Set;

import game.Game;
import game.Tile;
import search.Search;
//import java.util.Set;
//import game.Order;


/**
 * 
 * The food collection module is responsible for ants collecting as much food as
 * possible. The module is run during the turn computation, after the combat
 * simulation module, to prevent putting ants in danger when sending them to
 * collect food. Collection of food in dangerous situations is handled by the
 * combat simulation module itself, this module only concerns itself with
 * efficient food collection in safe situations. Initialize an ADAPTIVESEARCH
 * with all visible food items as seeds and available ants as targets Set the
 * one-per-seed parameter to true (no need to direct multiple ants to a single
 * food square) Set the callback function to order a move of the found ant in
 * the direction of the food Perform the search This will result in one closest
 * ant being sent on the way to every visible food item. If more food is visible
 * than ants are available, the closer located food items will be preferred
 * 
 * @author Debellis, Lorusso
 *
 */
public class FoodCollection {

	public FoodCollection(Set<Tile> foodTiles, Set<Tile> myAnts) {
		Search s = new Search(foodTiles, myAnts, null, false, true, false);
		s.adaptiveSearch();
		Game.issueOrders(s.getOrders());
	}
}
