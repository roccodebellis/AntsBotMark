package game;
import java.util.HashMap;
import java.util.Map;

import search.Search;

/**
 * <p>Rappresenta e gestisce una {@code Tile}.<br>
 * Una {@code tile} corrisponde ad una singola area nella mappa, caratterizzata da un paio
 * di coordinate ( [{@link #row}, {@link #col}] ) indicanti la posizione della {@doce tile}
 * all'interno della mappa del gioco.  su cui puo' camminare - o meno -
 * una formica.</p>
 * 
 * <p>Ad ogni {@code tile} e' assegnata una {@link #visible visibilita'} in base a se
 * le formiche del proprio agente riescono a visionarle, o meno, nel turno corrente.</p>
 * 
 * <p>Ogni {@code tile} dispone inoltre di una lista dei suoi {@link #neighbourTiles vicini}.</p>
 * 
 * <p>La {@code tile} puo' assumere una determinata tipologia tra quelle indicate
 * in {@link TileTypes}:<ul>
 * 
 * <li><b>{@link TileTypes#UNEXPLORED UNEXPLORED}:</b> e' una tile inesplorata
 * di cui non se ne conosce l'effettiva tipologia.<br>All'inizio del gioco tutte le {@code tile}
 * sono poste ad {@link TileTypes#UNEXPLORED UNEXPLORED}; una volta che una formica
 * avra' nel suo raggio di visione una {@code tile} contrassegnata come
 * {@link TileTypes#UNEXPLORED UNEXPLORED} sara' in grado di fornire informazioni
 * sulla sua effettiva tipologia assegnandogliene una tramite uno dei metodi di setting che la
 * classe stessa mette a disposizione ({@link #setTypeHill(Integer)},
 * {@link #setTypeLand()}, ...};</li>
 * 
 * <li><b>{@link TileTypes#LAND LAND}:</b> e' una {@code tile} di tipo {@code terreno} su cui le
 * formiche possono camminare liberamente, a patto che non sia {@link #occupiedByAnt occupata}
 * da un'altra formica.<br>Se una formica si posiziona su di una {@code tile}
 * di tipo {@link TileTypes#LAND LAND}, {@link #occupiedByAnt} assumera' valore {@code true}
 * mentre {@link #idOwner} sara' uguale al numero che identifica il proprietario
 * della formica.<br>
 * Se sulle varie {@code tile} di tipo {@code LAND} non sono presenti formiche
 * e se non ci sono formiche di fazioni opposte attorno ad esse, e' possibile che
 * vi si depositi del cibo.<br>In tal caso {@link #containsFood} avra' valore {@code true}.<br>
 * <b>NB:</b> <ul><li>se {@link #idOwner} e' diverso dallo {@code 0} signifca che quella formica
 * appartiene ad un avversario del nostro agente e si trattera' dunque di una formica nemica;</li>
 * <li>se una formica si deposita su di una {@code tile} ti tipo {@link TileTypes#LAND LAND}
 * ed il suo {@link #idOwner} {@code = 0} allora se le e' stato assegnato un ordine,
 * {@link #antIsAvailable} avra' valore {@code false}.<br>
 * Cio' significa che se {@link #antIsAvailable} {@code = true} la formica sara' disponibile
 * per l'assegnazione un compito da effettuare. Questo controllo viene effettuato da
 * {@link #isIdle()};</li>
 * <li>formiche morte e {@link TileTypes#HILL formicai} rasi al suolo saranno settati automaticamente
 * alla {@link TileTypes tipologia} {@link TileTypes#LAND LAND};</li>
 * </ul></li>
 * 
 * <li><b>{@link TileTypes#WATER WATER}:</b> e' una {@code tile} di tipo {@code acqua} su cui le
 * formiche <b>non</b> possono camminare.<br>Nel momento in cui una formica incontra per
 * la prima volta una {@code tile} di tipo {@link TileTypes#WATER WATER}, verra'
 * chiamato il metodo {@link #setTypeWater()} in modo tale da contrassegnarla come
 * {@link TileTypes#WATER WATER}, da settare i propri {@link #neighbourTiles vicini}
 * a {@code null} e da {@link #removeNeighbour(Directions) rimuovere} la {@code tile} dalla
 * lista dei vicini delle {@code tile} ad essa adiacenti:
 * cio' viene fatto per semplificare le computazioni effettuate dal modulo di
 * {@link Search ricerca}.</li>
 *  
 * <li><b>{@link TileTypes#HILL HILL}:</b> e' una {@code tile} di tipo {@code formicaio}.<br>Se
 * {@link #idOwner} e' diverso dallo {@code 0} signifca che quel {@code formicaio}
 * appartiene ad un avversario del nostro agente e si trattera' dunque di un {@code formicaio}
 * nemico.<br>
 * <b>NB:</b>Le formiche con lo stesso {@link #idOwner} del {@code formicaio} dovranno
 * evitare di calpestarlo.<br>Calpestare un proprio {@code formicaio} significa infatti
 * bloccare l'uscita a potenziali formiche generate a seguito del raccoglimento del cibo.
 * <br> Questo controllo viene effettuato da {@link #isSuitable()}.<br>
 * Se una formica della fazione opposta a quella del proprietario del {@code formicaio}
 * si posiziona su di esso, lo radera' al suolo automaticamente.</li>
 * </ul></p>
 * 
 * <p><b>NB:</b> per poter fare riferimento ad una formica si utilizza la {@code tile}
 * su cui e' correntemente posizionata.</p>
 * @author Debellis, Lorusso
 *
 */
public class Tile {
	
	/**
	 * Riga della {@link Tile tile} nella mappa del gioco.
	 */
	private Integer row;
	
	/**
	 * Colonna della {@link Tile tile} nella mappa del gioco.
	 */
	private Integer col;
	
	/**
	 * {@link TileTypes Tipologia} della {@link Tile tile}.
	 */
	private TileTypes type;
	
	/**
	 * Insieme di {@link Tile tile} vicine alle corrente elencate in base
	 * alla loro {@link Directions direzione} ({@link Directions#NORTH NORTH},
	 * {@link Directions#EAST EAST}, {@link Directions#SOUTH SOUTH},
	 * {@link Directions#WEST WEST}).
	 */
	private Map<Directions, Tile> neighbourTiles;
	
	/**
	 * Indica se la {@link Tile tile} e' visibile da una formica dell'agente
	 * nel turno corrente.
	 */
	private boolean visible;
	
	/**
	 * Indica se la {@link Tile tile} corrente e' occupata da una formica o meno.
	 */
	private boolean occupiedByAnt;
	
	/**
	 * <p>Se {@link #occupiedByAnt} {@code = true}, corrisponde all'ID della formica che
	 * occupa la {@link Tile tile}.<br>Se {@link #occupiedByAnt} {@code = false}
	 * e {@link #type} {@code =} {@link TileTypes#HILL HILL}, corrisponde all'ID
	 * del proprietario del {@code formicaio}.</p>
	 * <p> Se {@code idOwner} e' diverso dallo {@code 0} significa che la formica o il
	 * formicaio non appartengono all'agente bensi' sono di una fazione nemica.</p>
	 * 
	 */
	private Integer idOwner;
	
	/**
	 * Se {@link #occupiedByAnt} {@code = true} e {@link #idOwner} {@code = 0},
	 * indica se e' possibile assegnare un ordine ad una formica.
	 */
	private boolean antIsAvailable;

	/**
	 * Se il {@link #type} {@code =} {@link TileTypes#LAND} e
	 * {@link #occupiedByAnt} {@code = false} la {@link Tile tile} potrebbe
	 * contenere cibo. In tal caso {@code containsFood = true}.
	 */
	private boolean containsFood;

	/**
	 * <p>Costruttore di un oggetto di {@link Tile}.</p>
	 * <p>Inizializza la sua {@link #row riga} e la sua {@link #col colonna} per poi settare
	 * la sua {@link #type tipologia} ad {@link TileTypes#UNEXPLORED inesplorata}.</p>
	 * @param row riga della {@link Tile tile} nella mappa del gioco
	 * @param col colonna della {@link Tile tile} nella mappa del gioco
	 */
	public Tile(int row, int col) {
		this.row = row;
		this.col = col;
		type = TileTypes.UNEXPLORED;
		neighbourTiles = new HashMap<>();
		visible = false;
		occupiedByAnt = false;
		idOwner = null;
		antIsAvailable = false;
		containsFood = false;
	}

	/**
	 * Restituisce la riga della {@link Tile tile} nella mappa del gioco.
	 * @return {@link #row}
	 */
	int getRow() {
		return row;
	}

	/**
	 * Restituisce la colonna della {@link Tile tile} nella mappa del gioco.
	 * @return {@link #col}
	 */
	int getCol() {
		return col;
	}

	/**
	 * Se la {@link Tile tile} e' visibile nel turno corrente
	 * da almeno una delle formiche dell'agente, restituisce {@code true}; {@code false} altrimenti.<br>
	 * @return {@link #visible}
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Setta la {@link #visible visibilita'} della {@link Tile tile} a {@code true} se
	 * almeno una formica dell'agente, nel turno corrente, e' in grado
	 * di vedere la {@link Tile tile}; altrimenti, {@link #visible} viene settata a {@code false}.
	 * @param visible {@code true} se almeno una formica dell'agente, nel turno corrente,
	 * e' in grado di vedere la {@link Tile tile}; {@code false}, altrimenti
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isOccupiedByAnt() {
		return occupiedByAnt;
	}

	/**
	 * prima di chiamarla chiamare isOccupiedByAnt() e getOwner()
	 * 
	 * @return
	 * @throws TileTypeException
	 */
	public boolean isIdle() throws TileTypeException {//TODO da gestire
		if (occupiedByAnt && idOwner == 0)
			return antIsAvailable;
		else
			throw new TileTypeException("Pensavi fosse un tua formica e/o pensavi fosse occupata");
	}

	/**
	 * 
	 * @return
	 */
	public TileTypes getType() {
		return type;
	}

	/**
	 * 
	 */
	public void setTypeWater() {
		type = TileTypes.WATER;
		neighbourTiles.forEach((dir, t) -> t.removeNeighbour(dir.opponent()));
		neighbourTiles = null;
		visible = true;
		occupiedByAnt = false;
		idOwner = null;
		antIsAvailable = false;
		containsFood = false;
	}

	/**
	 * 
	 * @param idOwner
	 */
	public void setTypeHill(Integer idOwner) {
		type = TileTypes.HILL;
		// visible = true; fatto in set vision
		occupiedByAnt = false;
		this.idOwner = idOwner;
		antIsAvailable = false;
		containsFood = false;
	}

	/**
	 * 
	 */
	public void setTypeLand() {
		type = TileTypes.LAND;
		// visible = true; fatto in set vision
		occupiedByAnt = false;
		idOwner = null;
		antIsAvailable = false;
		containsFood = false;
	}

	/**
	 * 
	 * @param newIdOwner
	 */
	public void placeAnt(Integer newIdOwner) {
		if (type == TileTypes.HILL && idOwner != newIdOwner)
			type = TileTypes.LAND;
		// else e' un Hill e sopra ci va una sua formica

		// visible = true; fatto da set vision!
		occupiedByAnt = true;
		idOwner = newIdOwner;
		antIsAvailable = true;
	}

	/**
	 * 
	 */
	public void removeAnt() {
		// visible = false; fatto da clea vision!
		occupiedByAnt = false;

		idOwner = null; // TODO non te ne frega nulla tanto perdi tutti gli hill
		antIsAvailable = false;
	}

	/**
	 * 
	 */
	public void placeFood() {
		containsFood = true;
	}

	/**
	 * 
	 */
	public void removeFood() {
		containsFood = false;
	}

	/**
	 * 
	 * @param cardinal
	 * @param tile
	 */
	public void addNeighbour(Directions cardinal, Tile tile) {
		neighbourTiles.put(cardinal, tile);
	}

	/**
	 * 
	 * @param cardinal
	 */
	private void removeNeighbour(Directions cardinal) {
		neighbourTiles.remove(cardinal);
	}

	/**
	 * 
	 * @return
	 */
	public Map<Directions, Tile> getNeighbour() {
		return neighbourTiles;
	}

	/**
	 * 
	 * @return
	 * @throws TileTypeException
	 */
	public int getOwner() throws TileTypeException {
		if (occupiedByAnt || type.equals(TileTypes.HILL))
			return idOwner;
		else
			throw new TileTypeException("Pensavi fosse un HILL invece era " + type);
	}

	/**
	 * 
	 * @param t2
	 * @return
	 */
	public int getRowDelta(Tile t2) {
		return Math.abs(getRow() - t2.getRow());
	}

	/**
	 * 
	 * @param t2
	 * @return
	 */
	public int getColDelta(Tile t2) {
		return Math.abs(getCol() - t2.getCol());
	}



	public boolean isSuitable() {
		return true;
		//return (occupiedByAnt || (type.equals(TileTypes.HILL) && idOwner==0 )) ? false : true;
		//FIXME se è una formica nemica ????????
		
		//FIXME stiamo considerando isSuitable se c'è una formica nostra sopra
		//ma questo considera il turno corrente e non il turno successivo! 
		
		//dovrebbe controllare in game se ad una formica è stato asssegnato di andare in questa tile
		
		//viene utilizzata da BFS
	}
	
	@Override
	public int hashCode(){
		return row * 50000 + col ;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tile other = (Tile) obj;
		if (col == null) {
			if (other.col != null)
				return false;
		} else if (!col.equals(other.col))
			return false;
		if (row == null) {
			if (other.row != null)
				return false;
		} else if (!row.equals(other.row))
			return false;
		return true;
	}

}
