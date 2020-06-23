import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Targets  implements Iterable<Tile>{
	
	//extends LinkedList<Tile>
	
	//Per il momento lista di Tile
	private List<Tile> targets = new LinkedList<>();

	@Override
	public Iterator<Tile> iterator() {
		return targets.iterator();
	}

	void addTarget(Tile t) {
		targets.add(t);
	}
	
	List<Tile> getTargets(){
		return targets;
	}


	
}
