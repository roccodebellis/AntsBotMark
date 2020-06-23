import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Offsets extends TreeSet<Offset> {
	
	
	static private Map<Integer, Set<Offset>> offsetsComputed = new TreeMap<>();
	
	public Offsets(int radius){
		super(get(radius));
	}
	
	private static Collection<? extends Offset> get(int radius) {
		if(!offsetsComputed.containsKey(radius))
			computeOffset(radius);
		return offsetsComputed.get(radius);
	}

	static private void computeOffset(int radius) {
		Set<Offset> visionOffsets = new TreeSet<Offset>();
		//int mx = (int) Math.sqrt(viewRadius2); TODO
		for (int row = -radius; row <= radius; ++row) {
			for (int col = -radius; col <= radius; ++col) {
				int d = row * row + col * col;
				if (d <= radius * radius) {
					visionOffsets.add(new Offset(row, col));
				}
			}
		}
		offsetsComputed.put(radius, visionOffsets);
	}
	
	private static final long serialVersionUID = 7748104783701050648L;
}
