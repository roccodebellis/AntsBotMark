package combat;

import java.util.LinkedList;
import java.util.List;

import game.Order;
import timing.Timing;

public class State {
	
	Set<Tile> myAnts;
	Set<Tile> enemyAnts;
	
	Set<Tile> myLosses;
	Set<Tile> enemyLosses;
	
	Set<Tile> myHillDestroyed
	Set<Tile> enemyHillDestroyed
	
	Set<Tile> myFoodCollected
	Set<Tile> enemyFoodCollected
	
	int currentTurn;
	
	State(int turn, List<Order> myAntsOrders, List<Order> enemyAntsOrders) {
		this.currentTurn = turn;



	}

	int getTurnsLeft(){
		return Timing.getTurnLeft(currentTurn);
	}

	public int getMyAntsNumber() {
		return my;
	}

	public int getEnemyAntsNumber() {
		return enemy;
	}

	public int getMyLosses() {
		return myLosses;
	}

	public int getEnemyLosses() {
		return enemyLosses;
	}

	public int getEnemyHillDestroyed() {

		return enemyHillDestroyed;
	}

	public int getMyHillDestroyed() {
		return myHillDestroyed;
	}

	public int getEnemyFoodCollected() {
		return enemyFoodCollected;
	}

	public int getMyFoodCollected() {
		return myFoodCollected;
	}
}
