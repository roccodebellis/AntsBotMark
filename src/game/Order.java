package game;
/**
 * <p>Rappresenta un ordine da assegnare ad una formica posizionata su di una {@link Tile}
 * {@code t} verso una determinata {@link Directions direzione}, {@link #direction}.</p>
 * {@code t} e' caratterizzata da una {@link #row riga} e da una {@link #col colonna}.
 */
public class Order {
	
	private Tile tile;

    /**
     * Direzione verso cui la formica deve effettuare lo spostamento.
     */
    private Directions direction;

	/**
     * Crea un nuovo oggetto {@link Order} estrapolando da {@code t}
     * la sua corrispettiva  {@link #row riga} e {@link #col colonna}.
     * @param t {@link Tile} della mappa su cui si trova la mia formica
     * @param direction {@link Directions direzione}e verso cui muovere la mia formica
     */
    public Order(Tile t, Directions direction) {
    	this.setTile(t);

        this.direction = direction;
    }
    
    private void setTile(Tile t) {
		this.tile = t;
		
	}
    
    public Order withOpponentDirection() {
    	this.direction = this.direction.getOpponent();
    	return this;
    }

	/**
     * <p>Genera la stringa contenente l'ordine da mandare al System Output.<br>
     * La stringa avra' il seguente aspetto: "{@code o }{@link #row} {@link #col} {@link #direction}" 
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "o " + getRow() + " " + getCol() + " " + direction;
    }

	private int getCol() {
		return tile.getCol();
	}

	private int getRow() {
		return tile.getRow();
	}

	public Tile getTile() {
		return tile;
	}
	
	public Directions getDirection() {
		return direction;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((direction == null) ? 0 : direction.hashCode());
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
		Order other = (Order) obj;
		if (direction != other.direction)
			return false;
		if (tile == null) {
			if (other.tile != null)
				return false;
		} else if (!tile.equals(other.tile))
			return false;
		return true;
	}
	
	
}
