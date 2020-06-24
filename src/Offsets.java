import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
/**
 * <p>Gestisce e computa gli insiemi di {@link Offset} per poter determinare
 * le {@link Tile} di espansione a partire da una {@link Tile tile} situata
 * nella mappa del gioco; l'espansione cambia in base al raggio che e' stato
 * impostato per la computazione degli {@link Offset offset}.</p>
 * <p>Il calcolo di ogni insieme di {@link Offset} viene eseguito una singola volta
 * per ogni raggio e ne viene memorizzato il risultato all'interno di un contenitore
 * {@link #computedOffsets}.<br>
 * 
 * @see Offset
 * @author Debellis, Lorusso
 *
 */
public class Offsets extends TreeSet<Offset> {
	
	/**
	 * Contenitore degli insiemi di {@link Offset} computati per diversi raggi.
	 */
	static private Map<Integer, Set<Offset>> computedOffsets = new TreeMap<>();
	
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
		computedOffsets.put(radius, visionOffsets);
	}
	
	private static final long serialVersionUID = 7748104783701050648L;
}
