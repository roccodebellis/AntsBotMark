package game;
/**
 * <p>Rappresenta un ordine da assegnare ad una formica posizionata su di una {@link Tile}
 * {@code t} verso una determinata {@link Directions direzione}, {@link #direction}.</p>
 * {@code t} e' caratterizzata da una {@link #row riga} e da una {@link #col colonna}.
 */
public class Order {
	
	private final Tile tile;
	/**
	 * Riga della tile su cui si trova la formica che deve effettuare lo spostamento.
	 */
    private final int row;
    /**
     * Colonna della tile su cui si trova la formica che deve effettuare lo spostamento.
     */
    private final int col;
    /**
     * Direzione verso cui la formica deve effettuare lo spostamento.
     */
    private final Directions direction;
    
    /**
     * Crea un nuovo oggetto {@link Order} estrapolando da {@code t}
     * la sua corrispettiva  {@link #row riga} e {@link #col colonna}.
     * @param t {@link Tile} della mappa su cui si trova la mia formica
     * @param direction {@link Directions direzione}e verso cui muovere la mia formica
     */
    public Order(Tile t, Directions direction) {
    	this.setTile(t);
    	this.row = t.getRow();
        this.col = t.getCol();
        this.direction = direction;
    }
    
    /**
     * <p>Genera la stringa contenente l'ordine da mandare al System Output.<br>
     * La stringa avra' il seguente aspetto: "{@code o }{@link #row} {@link #col} {@link #direction}" 
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "o " + row + " " + col + " " + direction;
    }

	public Tile getTile() {
		return tile;
	}
}
