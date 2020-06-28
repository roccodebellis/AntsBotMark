package gathering;

import game.Game;
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

	public FoodCollection() {
		foodCollection();
	}

	private void foodCollection() {//private Set<Order> foodCollection() {
		Search s = new Search(Game.getFoodTiles(), Game.getMyAnts(), null, false, true);
		s.adaptiveSearch();
		
		// secondo me la callback function e' la nostra Search.computeOneOrder()
		//e' da integrare nella ricerca nel caso in cui one_target_per_source sia true
		//la richiamiamo e per ogni order lo manda direttamente al system output (non aspetta di
		//collezionarli tutti: in questo modo le formiche a cui sono state assegnate Tile di cibo
		//verranno tolte da MyAnts (ossia la lista di target del cibo) in modo che solo una formica
		//vada verso il cibo
		//in tal caso, non c'e' bisogno di fare s.getOrders() perche' lo fa gia' callback aka Search.computeOneOrder()
		
		//return s.getOrders();
	}
}
