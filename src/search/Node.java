package search;
import java.util.Comparator;
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

	public void setPathCost(int oldPathCost) {
		this.pathCost = oldPathCost + 1;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + heuristicValue;
		result = prime * result + pathCost;
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((tile == null) ? 0 : tile.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (heuristicValue != other.heuristicValue)
			return false;
		if (pathCost != other.pathCost)
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (tile == null) {
			if (other.tile != null)
				return false;
		} else if (!tile.equals(other.tile))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(Node o) {
		int thisCompare = pathCost + heuristicValue;
		int otheCompare = o.pathCost + o.heuristicValue;
		int tileCompare = tile.compareTo(o.tile);
		return (thisCompare == otheCompare) ? (tileCompare == 0 ? target.compareTo(o.target) : tileCompare) : Integer.compareUnsigned(thisCompare, otheCompare);
	}

	/** 
	 * compare tile cordinate 
	 * @return
	 */
	public static final Comparator<Node> nodeComparator() {
		return new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2){

				int compareTile = Integer.compare(o1.getTile().hashCode(), o2.getTile().hashCode());
				return compareTile==0 ? Integer.compare(o1.getTarget().hashCode(), o2.getTarget().hashCode()) : compareTile;


			}
		};
		//return (Tile o1, Tile o2) -> (Integer.compare(o1.getVisible(), o2.getVisible()));
	}
	public static final Comparator<Node> nodeComparator2() {
		return (Node o1, Node o2) -> (Integer.compare(o1.getTile().hashCode(), o2.getTile().hashCode()));
	}
	
	@Override
	public String toString() {
		return "Node [tile=" + tile + ", pathCost=" + pathCost + ", heuristicValue=" + heuristicValue + ", target="
				+ target + "]";
	}

}
