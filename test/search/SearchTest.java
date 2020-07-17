/**
 * 
 */
package search;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import game.Directions;
import game.Game;
import game.Order;

/**
 * @author roccodebellis
 *
 */
public class SearchTest {



	/**
	 * @throws java.lang.Exception
	 */
	//@BeforeEach
	public Game setUp() {
		return new Game(20, 15, 77, 5, 1);

	}

	boolean testBFS_OPS() {
		Game g = setUp();
		g.setAnt(10, 8, 0);
		g.setAnt(11, 10, 0);
		g.setAnt(12, 8, 0);
		g.setAnt(11, 7, 0);
		g.setFood(6, 8);
		g.setFood(11, 13);
		g.setFood(16, 8);
		g.setFood(11, 3);

		Search s = new Search(Game.getFoodTiles(), Game.getMyAnts(), null, false, true, false);
		s.adaptiveSearch();
		Set<Order> orders = s.getOrders();

		Set<Order> checkOrders = new HashSet<Order>();
		checkOrders.add(new Order(Game.getTile(10,8), Directions.NORTH));
		checkOrders.add(new Order(Game.getTile(11,10), Directions.EAST));
		checkOrders.add(new Order(Game.getTile(12,8), Directions.SOUTH));
		checkOrders.add(new Order(Game.getTile(11,7), Directions.WEST));

		return orders.equals(checkOrders);
	}

	boolean testBFS_R() {
		Game g = setUp();
		g.setAnt(10, 8, 0);
		g.setAnt(11, 9, 0);
		g.setAnt(12, 8, 0);
		g.setAnt(11, 7, 0);
		g.setAnt(7, 3, 0);
		g.setFood(6, 8);
		g.setFood(11, 13);
		g.setFood(16, 8);
		g.setFood(11, 3);

		Search s = new Search(Game.getFoodTiles(), Game.getMyAnts(), null, false, false, true);
		s.adaptiveSearch();
		Set<Order> orders = s.getOrders();

		Set<Order> checkOrders = new HashSet<Order>();
		checkOrders.add(new Order(Game.getTile(10,8), Directions.NORTH));
		checkOrders.add(new Order(Game.getTile(11,9), Directions.EAST));
		checkOrders.add(new Order(Game.getTile(12,8), Directions.SOUTH));
		checkOrders.add(new Order(Game.getTile(11,7), Directions.WEST));
		checkOrders.add(new Order(Game.getTile(7,3), Directions.SOUTH));
		//System.out.println(orders);
		//System.out.println(checkOrders);
		return orders.equals(checkOrders);
	}


	boolean testBFS_R1() {
		Game g = setUp();
		g.setAnt(0, 8, 0);
		g.setAnt(6, 8, 0);
		g.setFood(3, 8);

		g.setFood(0, 14);
		g.setAnt(18, 0, 0);
		g.setAnt(0, 1, 0);

		Search s = new Search(Game.getFoodTiles(), Game.getMyAnts(), null, false, false, true);
		s.adaptiveSearch();
		Set<Order> orders = s.getOrders();

		Set<Order> checkOrders = new HashSet<Order>();
		checkOrders.add(new Order(Game.getTile(0,8), Directions.SOUTH));
		checkOrders.add(new Order(Game.getTile(6,8), Directions.NORTH));
		checkOrders.add(new Order(Game.getTile(18,0), Directions.WEST));
		checkOrders.add(new Order(Game.getTile(0,1), Directions.WEST));
		
		return orders.equals(checkOrders);
	}
	
	boolean testBFS() {
		Game g = setUp();
		g.setAnt(0, 8, 0);
		g.setAnt(6, 8, 0);
		g.setFood(3, 8);

		g.setFood(0, 14);
		g.setAnt(18, 0, 0);
		g.setAnt(0, 1, 0);

		Search s = new Search(Game.getMyAnts(), Game.getFoodTiles(), null, false, false, false);
		s.adaptiveSearch();
		Set<Order> orders = s.getOrders();

		Set<Order> checkOrders = new HashSet<Order>();
		checkOrders.add(new Order(Game.getTile(0,8), Directions.SOUTH));
		checkOrders.add(new Order(Game.getTile(6,8), Directions.NORTH));
		checkOrders.add(new Order(Game.getTile(18,0), Directions.WEST));
		checkOrders.add(new Order(Game.getTile(0,1), Directions.WEST));
		//System.out.println(orders);
		//System.out.println(checkOrders);
		
		return orders.equals(checkOrders);
	}
	
	boolean testBFS1() {
		Game g = setUp();
		g.setAnt(0, 0, 0);
		g.setAnt(0, 4, 0);
		g.setAnt(2, 2, 0);
		g.setAnt(4, 0, 0);
		g.setAnt(4, 4, 0);
		g.setAnt(19, 1, 0);
		g.setFood(0, 2);
		g.setFood(2, 0);
		g.setFood(2, 4);
		g.setFood(4, 2);
		g.setWater(18, 1);
		g.setWater(19, 2);

		Search s = new Search(Game.getMyAnts(), Game.getFoodTiles(), null, false, false, false);
		s.adaptiveSearch();
		Set<Order> orders = s.getOrders();

		Set<Order> checkOrders = new HashSet<Order>();
		checkOrders.add(new Order(Game.getTile(0,0), Directions.EAST));
		checkOrders.add(new Order(Game.getTile(0,4), Directions.SOUTH));
		checkOrders.add(new Order(Game.getTile(2,2), Directions.NORTH));
		checkOrders.add(new Order(Game.getTile(4,0), Directions.NORTH));
		checkOrders.add(new Order(Game.getTile(4,4), Directions.NORTH));
		checkOrders.add(new Order(Game.getTile(19,1), Directions.SOUTH));
		System.out.println(orders);
		System.out.println(checkOrders);
		return orders.equals(checkOrders);
	}
	
	
	@Test
	@DisplayName("test Search.java")
	public void testSearch() {
		Assert.assertTrue(testBFS_OPS());
		Assert.assertTrue(testBFS_R());
		Assert.assertTrue(testBFS_R1());
		Assert.assertTrue(testBFS());
		Assert.assertTrue(testBFS1());
	}


	@Test
	public void testTrue() {
		Assert.assertTrue(true);
	}
}
