/**
 * 
 */
package search;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import game.Directions;
import game.Game;
import game.Order;
import game.Tile;
import search.Search;

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

	/**
	 * DA RIFARE
	 * @return
	 
	boolean testBFS_OPS() {
		Game g = setUp();
		g.setAnt(10, 3, 0);
		g.setFood(10, 13);
		g.setWater(1, 1);g.setWater(1, 15);
		g.setWater(2, 1);g.setWater(2, 15);
		g.setWater(3, 1);g.setWater(3, 8);g.setWater(3, 15);
		g.setWater(4, 1);g.setWater(4, 8);g.setWater(4, 15);
		g.setWater(5, 1);g.setWater(5, 8);g.setWater(5, 15);
		g.setWater(6, 1);g.setWater(6, 8);g.setWater(6, 15);
		g.setWater(7, 1);
		g.setWater(8, 1);



		Search s = new Search(Game.getFoodTiles(), Game.getMyAnts(), null, false, true, false);
		s.adaptiveSearch();
		Set<Order> orders = s.getOrders();

		Set<Order> checkOrders = new HashSet<Order>();
		checkOrders.add(new Order(Game.getTile(10,8), Directions.NORTH));
		checkOrders.add(new Order(Game.getTile(11,10), Directions.EAST));
		checkOrders.add(new Order(Game.getTile(12,8), Directions.SOUTH));
		checkOrders.add(new Order(Game.getTile(11,7), Directions.WEST));

		return orders.equals(checkOrders);
	}*/

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
		checkOrders.add(new Order(Game.getTile(10,8), Directions.NORTH,null));
		checkOrders.add(new Order(Game.getTile(11,9), Directions.EAST,null));
		checkOrders.add(new Order(Game.getTile(12,8), Directions.SOUTH,null));
		checkOrders.add(new Order(Game.getTile(11,7), Directions.WEST,null));
		checkOrders.add(new Order(Game.getTile(7,3), Directions.SOUTH,null));
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
		checkOrders.add(new Order(Game.getTile(0,8), Directions.SOUTH,null));
		checkOrders.add(new Order(Game.getTile(6,8), Directions.NORTH,null));
		checkOrders.add(new Order(Game.getTile(18,0), Directions.WEST,null));
		checkOrders.add(new Order(Game.getTile(0,1), Directions.WEST,null));
		
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
		checkOrders.add(new Order(Game.getTile(0,8), Directions.SOUTH,null));
		checkOrders.add(new Order(Game.getTile(6,8), Directions.NORTH,null));
		checkOrders.add(new Order(Game.getTile(18,0), Directions.WEST,null));
		checkOrders.add(new Order(Game.getTile(0,1), Directions.WEST,null));
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
		checkOrders.add(new Order(Game.getTile(0,0), Directions.EAST,null));
		checkOrders.add(new Order(Game.getTile(0,4), Directions.SOUTH,null));
		checkOrders.add(new Order(Game.getTile(2,2), Directions.NORTH,null));
		checkOrders.add(new Order(Game.getTile(4,0), Directions.NORTH,null));
		checkOrders.add(new Order(Game.getTile(4,4), Directions.NORTH,null));
		checkOrders.add(new Order(Game.getTile(19,1), Directions.SOUTH,null));
		System.out.println(orders);
		System.out.println(checkOrders);
		return orders.equals(checkOrders);
	}
	
	
	boolean testAStar() {
		System.out.println("\ntestAStar()");
		Game g = setUp();
		g.setAnt(10, 3, 0);
		g.setFood(10, 13);
		g.setWater(0, 1);
		g.setWater(1, 1);g.setWater(1, 14);
		g.setWater(2, 1);g.setWater(2, 14);
		g.setWater(3, 1);g.setWater(3, 8);g.setWater(3, 14);
		g.setWater(4, 1);g.setWater(4, 8);g.setWater(4, 14);
		g.setWater(5, 1);g.setWater(5, 8);g.setWater(5, 14);
		g.setWater(6, 1);g.setWater(6, 8);g.setWater(6, 9);g.setWater(6, 10);g.setWater(6, 11);g.setWater(6, 14);
		g.setWater(7, 1);g.setWater(7, 5);g.setWater(7, 8);g.setWater(7, 11);g.setWater(7, 14);
		g.setWater(8, 1);g.setWater(8, 5);g.setWater(8, 8);g.setWater(8, 11);g.setWater(8, 14);
		g.setWater(9, 1);g.setWater(9, 5);g.setWater(9, 11);g.setWater(9, 14);
		g.setWater(10, 1);g.setWater(10, 5);g.setWater(10, 11);g.setWater(10, 14);
		g.setWater(11, 1);g.setWater(11, 5);g.setWater(11, 11);g.setWater(11, 14);
		g.setWater(12, 1);g.setWater(12, 5);g.setWater(12, 8);g.setWater(12, 11);g.setWater(12, 14);
		g.setWater(13, 1);g.setWater(13, 5);g.setWater(13, 8);g.setWater(13, 11);g.setWater(13, 14);
		g.setWater(14, 1);g.setWater(14, 5);g.setWater(14, 6);g.setWater(14, 7);g.setWater(14, 8);g.setWater(14, 11);g.setWater(14, 14);
		g.setWater(15, 1);g.setWater(15, 5);g.setWater(15, 8);g.setWater(15, 11);g.setWater(15, 14);
		g.setWater(16, 1);g.setWater(16, 8);g.setWater(16, 14);
		g.setWater(17, 1);g.setWater(17, 8);g.setWater(17, 14);
		g.setWater(18, 1);g.setWater(18, 8);g.setWater(18, 14);
		g.setWater(19, 1);g.setWater(19, 14);

		Search s = new Search(Game.getMyAnts(), Game.getFoodTiles(), null, false, false, false);
		s.AStarSearch();
		Set<Order> orders = s.getOrders();

		Set<Order> checkOrders = new HashSet<Order>();
		checkOrders.add(new Order(Game.getTile(10,3), Directions.NORTH,Game.getTile(10, 13)));
		
		System.out.println(orders);
		System.out.println(checkOrders);
		return orders.equals(checkOrders);
	}
	
	boolean testAStar2() {
		System.out.println("testAStar2()");
		Game g = setUp();
		g.setAnt(10, 3, 0);
		g.setFood(10, 13);
		g.setWater(0, 1);
		g.setWater(1, 1);g.setWater(1, 14);
		g.setWater(2, 1);g.setWater(2, 14);
		g.setWater(3, 1);g.setWater(3, 8);g.setWater(3, 14);
		g.setWater(4, 1);g.setWater(4, 8);g.setWater(4, 14);
		g.setWater(5, 1);g.setWater(5, 8);g.setWater(5, 14);
		g.setWater(6, 1);g.setWater(6, 8);g.setWater(6, 9);g.setWater(6, 10);g.setWater(6, 11);g.setWater(6, 14);
		g.setWater(7, 1);g.setWater(7, 5);g.setWater(7, 8);g.setWater(7, 11);g.setWater(7, 14);
		g.setWater(8, 1);g.setWater(8, 5);g.setWater(8, 8);g.setWater(8, 11);g.setWater(8, 14);
		g.setWater(9, 1);g.setWater(9, 5);g.setWater(9, 11);g.setWater(9, 14);
		g.setWater(10, 1);g.setWater(10, 5);g.setWater(10, 11);g.setWater(10, 14);
		g.setWater(11, 1);g.setWater(11, 5);g.setWater(11, 11);g.setWater(11, 14);
		g.setWater(12, 1);g.setWater(12, 5);g.setWater(12, 8);g.setWater(12, 11);g.setWater(12, 14);
		g.setWater(13, 1);g.setWater(13, 5);g.setWater(13, 8);g.setWater(13, 11);g.setWater(13, 14);
		g.setWater(14, 1);g.setWater(14, 5);g.setWater(14, 6);g.setWater(14, 7);g.setWater(14, 8);g.setWater(14, 11);g.setWater(14, 14);
		g.setWater(15, 1);g.setWater(15, 5);g.setWater(15, 8);g.setWater(15, 11);g.setWater(15, 14);
		g.setWater(16, 1);g.setWater(16, 8);g.setWater(16, 14);
		g.setWater(17, 1);g.setWater(17, 8);g.setWater(17, 14);
		g.setWater(18, 1);g.setWater(18, 8);g.setWater(18, 14);
		g.setWater(19, 1);g.setWater(19, 14);
		//g.setFood(15, 6);g.setFood(12, 4);
		
		Search s = new Search(Game.getMyAnts(), Game.getFoodTiles(), null, false, false, false);
		Tile result = s.AStarSearch().iterator().next();
		Set<Order> orders = s.getOrders();
		System.out.println("orders:"+ orders.iterator().next().toStringExtended());
		Map<Tile, Directions> dfs = s.getDirectionFromTarget();
		System.out.print(dfs+"\n");
		Tile curTile = result;
		
		System.out.print(curTile);
		while(!curTile.equals(Game.getTile(10, 3))) {
			curTile = curTile.getNeighbourTile(dfs.get(curTile));
			System.out.print(" -> "+curTile);
		}
		System.out.print("\n");

		Set<Order> checkOrders = new HashSet<Order>();
		checkOrders.add(new Order(Game.getTile(10,3), Directions.NORTH,Game.getTile(10, 13)));
		
		
		System.out.println(checkOrders);
		return orders.equals(checkOrders);
	}
	boolean testAStar3() {
		System.out.println("testAStar3()");
		Game g = setUp();
		g.setAnt(10, 3, 0);
		g.setFood(17, 13);
		g.setWater(0, 1);
		g.setWater(1, 1);g.setWater(1, 14);
		g.setWater(2, 1);g.setWater(2, 14);
		g.setWater(3, 1);g.setWater(3, 8);g.setWater(3, 14);
		g.setWater(4, 1);g.setWater(4, 8);g.setWater(4, 14);
		g.setWater(5, 1);g.setWater(5, 8);g.setWater(5, 14);
		g.setWater(6, 1);g.setWater(6, 8);g.setWater(6, 9);g.setWater(6, 10);g.setWater(6, 11);g.setWater(6, 14);
		g.setWater(7, 1);g.setWater(7, 5);g.setWater(7, 8);g.setWater(7, 11);g.setWater(7, 14);
		g.setWater(8, 1);g.setWater(8, 5);g.setWater(8, 8);g.setWater(8, 11);g.setWater(8, 14);
		g.setWater(9, 1);g.setWater(9, 5);g.setWater(9, 11);g.setWater(9, 14);
		g.setWater(10, 1);g.setWater(10, 5);g.setWater(10, 11);g.setWater(10, 14);
		g.setWater(11, 1);g.setWater(11, 5);g.setWater(11, 11);g.setWater(11, 14);
		g.setWater(12, 1);g.setWater(12, 5);g.setWater(12, 8);g.setWater(12, 11);g.setWater(12, 14);
		g.setWater(13, 1);g.setWater(13, 5);g.setWater(13, 8);g.setWater(13, 11);g.setWater(13, 14);
		g.setWater(14, 1);g.setWater(14, 5);g.setWater(14, 6);g.setWater(14, 7);g.setWater(14, 8);g.setWater(14, 11);g.setWater(14, 14);
		g.setWater(15, 1);g.setWater(15, 5);g.setWater(15, 8);g.setWater(15, 11);g.setWater(15, 14);
		g.setWater(16, 1);g.setWater(16, 8);g.setWater(16, 14);
		g.setWater(17, 1);g.setWater(17, 8);g.setWater(17, 14);
		g.setWater(18, 1);g.setWater(18, 8);g.setWater(18, 14);
		g.setWater(19, 1);g.setWater(19, 14);
		g.setFood(0, 0);g.setFood(2, 0);g.setFood(5, 0);g.setFood(10, 0);
		
		Search s = new Search(Game.getMyAnts(), Game.getFoodTiles(), null, false, false, false);
		Tile result = s.AStarSearch().iterator().next();
		Set<Order> orders = s.getOrders();
		System.out.println("orders:"+ orders.iterator().next().toStringExtended());
		Map<Tile, Directions> dfs = s.getDirectionFromTarget();
		System.out.print(dfs+"\n");
		Tile curTile = result;
		
		System.out.print(curTile);
		while(!curTile.equals(Game.getTile(10, 3))) {
			curTile = curTile.getNeighbourTile(dfs.get(curTile));
			System.out.print(" -> "+curTile);
		}
		System.out.print("\n");

		Set<Order> checkOrders = new HashSet<Order>();
		checkOrders.add(new Order(Game.getTile(10,3), Directions.NORTH,Game.getTile(10, 13)));
		
		
		System.out.println(checkOrders);
		return orders.equals(checkOrders);
	}
	
	boolean testAStarHill() {
		System.out.println("testAStarHill()");
		Game g = setUp();
		g.setAnt(10, 3, 0);
		g.setFood(17, 13);
		g.setWater(0, 1);
		
		g.setWater(1, 1);g.setWater(1, 14);
		g.setWater(2, 1);g.setWater(2, 14);
		g.setWater(3, 1);g.setWater(3, 8);g.setWater(3, 14);
		g.setWater(4, 1);g.setWater(4, 8);g.setWater(4, 14);
		g.setWater(5, 1);g.setWater(5, 8);g.setWater(5, 14);
		g.setWater(6, 1);g.setWater(6, 8);g.setWater(6, 9);g.setWater(6, 10);g.setWater(6, 11);g.setWater(6, 14);
		g.setWater(7, 1);g.setWater(7, 5);g.setWater(7, 8);g.setWater(7, 11);g.setWater(7, 14);
		g.setWater(8, 1);g.setWater(8, 5);g.setWater(8, 8);g.setWater(8, 11);g.setWater(8, 14);
		g.setWater(9, 1);g.setWater(9, 3);g.setWater(9, 5);g.setWater(9, 11);g.setWater(9, 14);
		g.setWater(10, 1);g.setWater(10,2);g.setHills(10, 4, 0);g.setWater(10, 5);g.setWater(10, 11);g.setWater(10, 14);
		g.setWater(11, 1);g.setWater(11, 5);g.setWater(11, 11);g.setWater(11, 14);
		g.setWater(12, 1);g.setWater(12, 5);g.setWater(12, 8);g.setWater(12, 11);g.setWater(12, 14);
		g.setWater(13, 1);g.setWater(11, 3);g.setWater(13, 5);g.setWater(13, 8);g.setWater(13, 11);g.setWater(13, 14);
		g.setWater(14, 1);g.setWater(14, 5);g.setWater(14, 6);g.setWater(14, 7);g.setWater(14, 8);g.setWater(14, 11);g.setWater(14, 14);
		g.setWater(15, 1);g.setWater(15, 5);g.setWater(15, 8);g.setWater(15, 11);g.setWater(15, 14);
		g.setWater(16, 1);g.setWater(16, 8);g.setWater(16, 14);
		g.setWater(17, 1);g.setWater(17, 8);g.setWater(17, 14);
		g.setWater(18, 1);g.setWater(18, 8);g.setWater(18, 14);
		g.setWater(19, 1);g.setWater(19, 14);
		//g.setFood(0, 0);g.setFood(2, 0);g.setFood(5, 0);g.setFood(10, 0);
		
		Search s = new Search(Game.getFoodTiles(), Game.getMyAnts(), null, false, false, false);
		Tile result = s.AStarSearch().iterator().next();
		Set<Order> orders = s.getOrders();
		System.out.println("orders:"+ orders.iterator().next().toStringExtended());
		Map<Tile, Directions> dft = s.getDirectionFromTarget();
		System.out.print("DFT:"+dft+"\n\n");
		Tile curTile = result;
		
		System.out.print(curTile);
		while(!curTile.equals(Game.getTile(10, 3))) {
			curTile = curTile.getNeighbourTile(dft.get(curTile));
			System.out.print(" -> "+curTile);
		}
		System.out.print("\n");
		
		Map<Tile, Directions> dfs = s.getDirectionFromSource();
		System.out.print("DFS:"+dfs+"\n\n");
		curTile = Game.getTile(10, 3);
		System.out.print(curTile);
		while(!curTile.equals(result)) {
			curTile = curTile.getNeighbourTile(dfs.get(curTile));
			System.out.print(" -> "+curTile);
		}
		System.out.print("\n");
		
		Set<Order> checkOrders = new HashSet<Order>();
		checkOrders.add(new Order(Game.getTile(10,3), Directions.NORTH,Game.getTile(10, 13)));
		
		
		System.out.println(checkOrders+"\n");
		return orders.equals(checkOrders);
	}
	
	boolean testEAStarSearch() {
		System.out.println("testEAStar()");
		Game g = setUp();
		//g.setAnt(10, 3, 0);//g.setAnt(13, 6, 0);
		//g.setFood(17, 13);g.setFood(7, 9);g.setFood(4, 3);
		g.setFood(5, 5);
		//g.setFood(6, 5);//g.setAnt(10, 7, 0);
		g.setWater(0, 1);g.setWater(0, 14);
		
		g.setWater(7, 4);g.setWater(6, 3);g.setWater(5, 4);g.setWater(4, 5);g.setWater(5, 6);g.setWater(6, 7);g.setWater(7, 6);
		
		g.setAnt(6, 4, 0);g.setAnt(6, 6, 0);
		
		g.setWater(1, 1);g.setWater(1, 14);
		g.setWater(2, 1);g.setWater(2, 14);
		g.setWater(3, 1);g.setWater(3, 8);g.setWater(3, 14);
		g.setWater(4, 1);g.setWater(4, 8);g.setWater(4, 14);
		g.setWater(5, 1);g.setWater(5, 8);g.setWater(5, 14);
		g.setWater(6, 1);g.setWater(6, 8);g.setWater(6, 9);g.setWater(6, 10);g.setWater(6, 11);g.setWater(6, 14);
		g.setWater(7, 1);g.setWater(7, 5);g.setWater(7, 8);g.setWater(7, 11);g.setWater(7, 14);
		g.setWater(8, 1);g.setWater(8, 5);g.setWater(8, 8);g.setWater(8, 11);g.setWater(8, 14);
		g.setWater(9, 1);g.setWater(9, 5);g.setWater(9, 11);g.setWater(9, 14);
		g.setWater(10, 1);g.setWater(10,2);g.setHills(10, 4, 0);g.setWater(10, 5);g.setWater(10, 11);g.setWater(10, 14);
		g.setWater(11, 1);g.setWater(11, 3);g.setWater(11, 5);g.setWater(11, 11);g.setWater(11, 14);
		g.setWater(12, 1);g.setWater(12, 5);g.setWater(12, 8);g.setWater(12, 11);g.setWater(12, 14);
		g.setWater(13, 1);g.setWater(13, 5);g.setWater(13, 8);g.setWater(13, 11);g.setWater(13, 14);
		g.setWater(14, 1);g.setWater(14, 5);g.setWater(14, 6);g.setWater(14, 7);g.setWater(14, 8);g.setWater(14, 11);g.setWater(14, 14);
		g.setWater(15, 1);g.setWater(15, 5);g.setWater(15, 8);g.setWater(15, 11);g.setWater(15, 14);
		g.setWater(16, 1);g.setWater(16, 8);g.setWater(16, 14);
		g.setWater(17, 1);g.setWater(17, 8);g.setWater(17, 14);
		g.setWater(18, 1);g.setWater(18, 8);g.setWater(18, 14);
		g.setWater(19, 1);g.setWater(19, 14);
		//Game.printMap();
		//g.setFood(0, 0);//g.setFood(2, 0);g.setFood(5, 0);g.setFood(10, 0);
		
																        // A*  , OTPS, rever 
		//Search s = new Search(Game.getMyAnts(), Game.getFoodTiles(), null, true, false, false); // A*
		//Search s = new Search(Game.getFoodTiles(), Game.getMyAnts(), null, true, false, true); // A* reverse
		Search s = new Search(Game.getFoodTiles(), Game.getMyAnts(), null, true, true, false); // OTPS
		Tile result = s.EAStarSearch().iterator().next();
		Set<Order> orders = s.getOrders();
		System.out.println("orders:"+ orders.iterator().next().toStringExtended());
		
		Set<Order> checkOrders = new HashSet<Order>();
		checkOrders.add(new Order(Game.getTile(10,3), Directions.NORTH,Game.getTile(10, 13)));
	
		return orders.equals(checkOrders);
	}
	
	/*
	@Test
	public void testSearch() {
		//Assert.assertTrue(testBFS_OPS());
		//Assert.assertTrue(testBFS_R());
		//Assert.assertTrue(testBFS_R1());
		//Assert.assertTrue(testBFS());
		//Assert.assertTrue(testBFS1());
		Assert.assertTrue(testAStar());
		
	}
	
	@Test
	public void testAstar() {
		
		Assert.assertTrue(testAStar2());
	}

	@Test
	public void testAstar3() {
		Assert.assertTrue(testAStar3());
	}
	
	@Test
	public void testAstarHill() {
		Assert.assertTrue(testAStarHill());
	}

*/
	@Test
	public void testEAStar() {
		Assert.assertTrue(testEAStarSearch());
	}

	@Test
	public void testTrue() {
		Assert.assertTrue(true);
	}
	
	
	
}
