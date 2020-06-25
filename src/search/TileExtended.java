package search;
import java.util.Iterator;
import java.util.Set;

import game.Tile;

public class TileExtended implements Comparable<TileExtended>{

	private Tile tile;
	private int pathCost;
	private int heuristicValue;
	private Tile target;


	/*
	 * utilizzato per inizializzare le tile dalle sorgenti/sources
	 */
	TileExtended(Tile nodo, Set<Tile> targets) {
		this.tile = nodo;
		pathCost = 0;
		assignTarget(targets);
	}

	/**
	 * utilizzato per inizializzare tile da un neighbourTile
	 * @param neighbourTile
	 * @param target
	 * @param i
	 */
	public TileExtended(Tile neighbourTile, Tile target, int pathCost) {
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
	public int compareTo(TileExtended o) {
		return Integer.compareUnsigned(pathCost + heuristicValue, o.pathCost + o.heuristicValue);
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

	@Override
	public int hashCode(){
		return tile.getRow() * 10000 + tile.getCol() ;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TileExtended other = (TileExtended) obj;
		if (tile == null) {
			if (other.tile != null)
				return false;
		} else if (!tile.equals(other.tile))
			return false;
		return true;
	}
}
