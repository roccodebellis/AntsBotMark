package vision;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import game.Tile;
/**
 * <p>Gestisce e computa gli insiemi di {@link Offset offset} per poter determinare
 * le {@link Tile} che ne circondano una di partenza in base ad un determinato raggio,
 * {@code radius}.</p>
 * Il calcolo di ogni insieme di {@link Offset offset} viene eseguito una singola volta
 * per ogni raggio e ne viene memorizzato il risultato all'interno di un contenitore
 * {@link #computedOffsets}.
 * 
 * @see Offset
 * @author Debellis, Lorusso
 *
 */
public class Offsets extends HashSet<Offset> {
	
	/**
	 * Contenitore degli insiemi di {@link Offset} computati per diversi raggi.
	 */
	private static Map<Integer, Set<Offset>> computedOffsets = new TreeMap<>();
	
	/**
	 * Costruisce un oggetto di {@link Offsets}.<br> Il primo insieme di
	 * {@link Offset} che viene inserito in {@link #computedOffsets} e'
	 * computato grazie a {@code radius}. 
	 * @param radius raggio con cui calcolare il primo insieme di {@link Offset}
	 * da inserire in {@link #computedOffsets}
	 */
	public Offsets(int radius){
		super(get(radius));
	}
	
	public Offsets() {
	}
	
	/**
	 * Restituisce l'insieme di {@link Offset} con raggio {@code radius}.<br>
	 * Se non e' stato ancora calcolato, si richiama
	 * {@link #computeOffset(int) computeOffset(radius)} per effettuare la computazione.
	 * @param radius raggio di cui si desidera ottenere il corrispondente {@link Offset}
	 * @return elemento di {@link #computedOffsets} che ha chiave {@code radius}
	 */
	private static Collection<? extends Offset> get(int radius) {//TODO public?
		if(!computedOffsets.containsKey(radius))
			computeOffset(radius);
		return computedOffsets.get(radius);
	}
	
	/**
	 * Calcola l'insieme di {@link Offset} con raggio {@code radius}.
	 * @param radius raggio di cui si desidera ottenere il corrispondente {@link Offset}
	 */
	private static void computeOffset(int viewRadius2) {
		Set<Offset> visionOffsets = new HashSet<Offset>();
		int mx = (int) Math.sqrt(viewRadius2);
		for (int row = -mx; row <= mx; ++row) {
			for (int col = -mx; col <= mx; ++col) {
				int d = row * row + col * col;
				if (d <= viewRadius2) {
					visionOffsets.add(new Offset(row, col));
				}
			}
		}
		computedOffsets.put(viewRadius2, visionOffsets);
	}
	
	public static Offsets getDefenceHillOffsets() {
		Offsets defenceTargets = new Offsets();
		defenceTargets.add(new Offset(1,1));
		defenceTargets.add(new Offset(1,-1));
		defenceTargets.add(new Offset(-1,1));
		defenceTargets.add(new Offset(-1,-1));
		return defenceTargets;
	}
	
	private static final long serialVersionUID = 7748104783701050648L;
}
