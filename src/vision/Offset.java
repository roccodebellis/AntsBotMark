package vision;
import game.Tile;

/**
 * Determina di quante {@link #deltaRow righe} e di quante {@link #deltaCol colonne}
 * ci si debba spostare, partendo da una {@link Tile}, per ottenerne un'altra
 * che che la circonda.<br>
 * {@link #deltaRow} e {@link #deltaCol} sono, rispettivamente, il numero di righe ed
 * il numero di colonne da sommare alla riga ed alla colonna di una {@link Tile tile}
 * nella mappa del gioco per poterne ottenere un'altra che la circonda.
 * @author Debellis, Lorusso
 *
 */
public class Offset {
	/**
	 * Riga dell'{@link Offset} corrente.<br>E' il numero di righe da aggiungere
	 * alla riga della {@link Tile tile} di partenza per poter ottenere
	 * la riga corrispondente ad una {@link Tile tile} che si trova nella
	 * zona circostante alla prima.
	 */
	private int deltaRow;
	/**
	 * Colonna dell'{@link Offset} corrente.<br>E' il numero di colonne da aggiungere
	 * alla colonna della {@link Tile tile} di partenza per poter ottenere
	 * la colonna corrispondente ad una {@link Tile tile} che si trova nella
	 * zona circostante alla prima.
	 */
	private int deltaCol;
	/**
	 * Costruisce un oggetto di {@link Offset} impostando
	 * {@link #deltaRow} e {@link #deltaCol} per poter
	 * ottenere una {@link Tile} che circorda quella di partenza.
	 * @param row Riga dell'{@link Offset} corrente
	 * @param col Colonna dell'{@link Offset} corrente
	 */
	Offset(int row, int col) {
		this.deltaRow = row;
		this.deltaCol = col;
	}
	/**
	 * Restutuisce la {@link #deltaRow riga} dell'{@link Offset} corrente
	 * @return {@link #deltaRow}
	 */
	public int getRow() {
		return deltaRow;
	}
	/**
	 * Restutuisce la {@link #deltaCol colonna} dell'{@link Offset} corrente
	 * @return {@link #deltaCol}
	 */
	public int getCol() {
		return deltaCol;
	}
}
