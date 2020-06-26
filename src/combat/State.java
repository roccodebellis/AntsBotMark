package combat;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import game.Directions;
import game.Game;
import game.Order;
import game.Tile;
import timing.Timing;

public class State {


	Set<Tile> ants;
	Set<Tile> opponentAnts;
	
	Set<Tile> antsHills;
	Set<Tile> opponentHills;
	
	Set<Tile> antsHillsDestroyed;
	Set<Tile> opponentHillsDestroyed;

	Set<Tile> antsLosses;
	Set<Tile> opponentLosses;

	Set<Tile> antsFoodCollected;
	Set<Tile> opponentFoodCollected;

	int currentTurn;

	boolean isEnemyMoves;
	
	Set<State> child;


	State(int turn, List<Order> myAntsOrders, List<Order> enemyAntsOrders, boolean enemyMoves) {
		child = new TreeSet<State>();
		isEnemyMoves = enemyMoves;
		this.currentTurn = turn;
	}


	public boolean isEnemyMove() {
		return isEnemyMoves;
	}
	
	public Set<Tile> getAnts() {
		return ants;
	}

	public int getAnts_number() {
		return ants.size();
	}

	public Set<Tile> getOpponentAnts() {
		return opponentAnts;
	}
	
	public Set<Tile> getOpponentHills() {
		return opponentHills;
	}
	
	public int getOpponentAnts_number() {
		return opponentAnts.size();
	}

	public int getTurnsLeft() {
		return Timing.getTurnLeft(currentTurn);
	}

	public int getAntsLosses_number() {
		return antsLosses.size();
	}

	public int getOpponentLosses_number() {
		return opponentLosses.size();
	}

	public int getOpponentHillDestroyed_number() {
		return opponentHillsDestroyed.size();
	}

	public int getAntsHillDestroyed_number() {
		return antsHillsDestroyed.size();
	}

	public int getAntsFoodCollected_number() {
		return antsFoodCollected.size();
	}
	
	public int getOpponentFoodCollected_number() {
		return opponentFoodCollected.size();
	}


	public State performMove(Set<Order> moves) {
		// TODO Auto-generated method stub
		return null;
	}


	public void resolveCombatAndFoodCollection() {
		// TODO Auto-generated method stub
	}


	public void addChild(State childState) {
		// TODO Auto-generated method stub
		
	}

}
