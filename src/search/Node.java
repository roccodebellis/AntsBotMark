package search;
import java.util.Iterator;
import java.util.Set;

import game.Game;
import game.Tile;

public class Node implements Comparable<Node>{

	private Tile tile;
	private int pathCost;
	private int heuristicValue;
	private Tile target;
	
	/*
	 * utilizzato per inizializzare le tile dalle sorgenti/sources
	 */
	Node(Tile nodo, Set<Tile> targets) {
		this.tile = nodo;
		this.pathCost = 0;
		assignTarget(targets);
		//heuristicValue = Game.getDistance(enemy,ant);
	}
	
	public Node(Tile enemy, Tile ant){
		this.tile = enemy;
		this.pathCost = 0;
		this.target = ant;
		heuristicValue = Game.getDistance(enemy,ant);
	}

	/**
	 * utilizzato per inizializzare tile da un neighbourTile
	 * @param neighbourTile
	 * @param target
	 * @param i
	 */
	public Node(Tile neighbourTile, Tile target, int pathCost) {
		this.tile = neighbourTile;
		this.pathCost = pathCost + 1;
		this.target =  target;
		heuristicValue = Game.getDistance(target,neighbourTile);
	}

	private void assignTarget(Set<Tile> targets) {
		Iterator<Tile> targetsItr = targets.iterator();

		if(targetsItr.hasNext()) {
			Tile minTarget = targetsItr.next();
			int minDist = Game.getDistance(minTarget,tile);

			while (targetsItr.hasNext()) {
				Tile next = targetsItr.next();
				int nextDist = Game.getDistance(next,tile);
				if (nextDist < minDist) {
					minTarget = next;
					minDist = nextDist;
				}
			}
			heuristicValue = minDist;
			target = minTarget;
		}
	}

	void setHeuristicValue(int heuristicValue) {
		this.heuristicValue = heuristicValue;
	}

	@Override
	public int compareTo(Node o) {
		return Integer.compareUnsigned(pathCost + heuristicValue, o.pathCost + o.heuristicValue);//TODO what if they are equals? we lose one node...
	}

	public Tile getTile() {
		return tile;
	}

	public Tile getTarget() {
		return target;
	}

	public int getPathCost() {
		return pathCost;
	}
	
	public int getHeuristicValue() {
		return heuristicValue;
	}

	@Override
	public int hashCode(){
		return tile.getRow() * 10000 + tile.getCol() ;
	}
	
	/**
	 * Verifica se due ExtendedTile sono uguali anche in base al loro contenuto.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (tile == null) {
			if (other.tile != null)
				return false;
		} else if (!tile.equals(other.tile))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Node [tile=" + tile + ", pathCost=" + pathCost + ", heuristicValue=" + heuristicValue + ", target="
				+ target + "]";
	}
	
	
	
	
}
