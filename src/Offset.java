/**
 * Corrisponde alle coordinate di un {@code Offset}, computate in {@link Offsets}
 * per un determinato raggio.<br>
 * Dispone della rispettiva {@link #row riga} e {@link #col colonna} di espansione da
 * una qualsiasi {@link Tile} della mappa del gioco, per un dato raggio.
 * @author Debellis, Lorusso
 *
 */
class Offset {
	/**
	 * Riga dell'{@link Offset} corrente
	 */
	private int row;
	/**
	 * Colonna dell'{@link Offset} corrente
	 */
	private int col;
	/**
	 * Costruisce un oggetto di {@link Offset} assegnandogli la rispettiva
	 * {@link #row riga} e {@link #col colonna} che assume la
	 * {@link Tile} nella mappa del gioco.
	 * @param row Riga dell'{@link Offset} corrente
	 * @param col Colonna dell'{@link Offset} corrente
	 */
	Offset(int row, int col) {
		this.row = row;
		this.col = col;
	}

	/**
	 * Restutuisce la {@link #row riga} dell'{@link Offset} corrente
	 * @return {@link #row}
	 */
	public int getRow() {
		return row;
	}
	
	/**
	 * Restutuisce la {@link #col colonna} dell'{@link Offset} corrente
	 * @return {@link #col}
	 */
	public int getCol() {
		return col;
	}
}
