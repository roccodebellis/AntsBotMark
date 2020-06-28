package game;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import search.Search;

//TODO rivedi tutti javadoc
/**
 * <p>
 * Rappresenta e gestisce una {@code Tile}.<br>
 * Una {@code tile} corrisponde ad una singola area nella mappa, caratterizzata
 * da un paio di coordinate ( [{@link #row}, {@link #col}] ) indicanti la
 * posizione della {@doce tile} all'interno della mappa del gioco. su cui puo'
 * camminare - o meno - una formica.
 * </p>
 * 
 * <p>
 * Ad ogni {@code tile} e' assegnata una {@link #visible visibilita'} in base a
 * se le formiche del proprio agente riescono a visionarle, o meno, nel turno
 * corrente.
 * </p>
 * 
 * <p>
 * Ogni {@code tile} dispone inoltre di una lista dei suoi
 * {@link #neighbourTiles vicini}.
 * </p>
 * 
 * <p>
 * La {@code tile} puo' assumere una determinata tipologia tra quelle indicate
 * in {@link TileTypes}:
 * <ul>
 * 
 * <li><b>{@link TileTypes#UNEXPLORED UNEXPLORED}:</b> e' una tile inesplorata
 * di cui non se ne conosce l'effettiva tipologia.<br>
 * All'inizio del gioco tutte le {@code tile} sono poste ad
 * {@link TileTypes#UNEXPLORED UNEXPLORED}; una volta che una formica avra' nel
 * suo raggio di visione una {@code tile} contrassegnata come
 * {@link TileTypes#UNEXPLORED UNEXPLORED} sara' in grado di fornire
 * informazioni sulla sua effettiva tipologia assegnandogliene una tramite uno
 * dei metodi di setting che la classe stessa mette a disposizione
 * ({@link #setTypeHill(Integer)}, {@link #setTypeLand()}, ...};</li>
 * 
 * <li><b>{@link TileTypes#LAND LAND}:</b> e' una {@code tile} di tipo
 * {@code terreno} su cui le formiche possono camminare liberamente, a patto che
 * non sia {@link #occupiedByAnt occupata} da un'altra formica.<br>
 * Se una formica si posiziona su di una {@code tile} di tipo
 * {@link TileTypes#LAND LAND}, {@link #occupiedByAnt} assumera' valore
 * {@code true} mentre {@link #idOwner} sara' uguale al numero che identifica il
 * proprietario della formica.<br>
 * Se sulle varie {@code tile} di tipo {@code LAND} non sono presenti formiche e
 * se non ci sono formiche di fazioni opposte attorno ad esse, e' possibile che
 * vi si depositi del cibo.<br>
 * In tal caso {@link #containsFood} avra' valore {@code true}.<br>
 * <b>NB:</b>
 * <ul>
 * <li>se {@link #idOwner} e' diverso dallo {@code 0} signifca che quella
 * formica appartiene ad un avversario del nostro agente e si trattera' dunque
 * di una formica nemica;</li>
 * <li>formiche morte e {@link TileTypes#HILL formicai} rasi al suolo saranno
 * settati automaticamente alla {@link TileTypes tipologia}
 * {@link TileTypes#LAND LAND};</li>
 * </ul>
 * </li>
 * 
 * <li><b>{@link TileTypes#WATER WATER}:</b> e' una {@code tile} di tipo
 * {@code acqua} su cui le formiche <b>non</b> possono camminare.<br>
 * Nel momento in cui una formica incontra per la prima volta una {@code tile}
 * di tipo {@link TileTypes#WATER WATER}, verra' chiamato il metodo
 * {@link #setTypeWater()} in modo tale da contrassegnarla come
 * {@link TileTypes#WATER WATER}, da settare i propri {@link #neighbourTiles
 * vicini} a {@code null} e da {@link #removeNeighbour(Directions) rimuovere} la
 * {@code tile} dalla lista dei vicini delle {@code tile} ad essa adiacenti:
 * cio' viene fatto per semplificare le computazioni effettuate dal modulo di
 * {@link Search ricerca}.</li>
 * 
 * <li><b>{@link TileTypes#HILL HILL}:</b> e' una {@code tile} di tipo
 * {@code formicaio}.<br>
 * Se {@link #idOwner} e' diverso dallo {@code 0} signifca che quel
 * {@code formicaio} appartiene ad un avversario del nostro agente e si
 * trattera' dunque di un {@code formicaio} nemico.<br>
 * <b>NB:</b>Le formiche con lo stesso {@link #idOwner} del {@code formicaio}
 * dovranno evitare di calpestarlo.<br>
 * Calpestare un proprio {@code formicaio} significa infatti bloccare l'uscita a
 * potenziali formiche generate a seguito del raccoglimento del cibo. <br>
 * Questo controllo viene effettuato da {@link #isSuitable()}.<br>
 * Se una formica della fazione opposta a quella del proprietario del
 * {@code formicaio} si posiziona su di esso, lo radera' al suolo
 * automaticamente.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>NB:</b> per poter fare riferimento ad una formica si utilizza la
 * {@code tile} su cui e' correntemente posizionata.
 * </p>
 * 
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
	 * Insieme di {@link Tile tile} vicine alle corrente elencate in base alla loro
	 * {@link Directions direzione} ({@link Directions#NORTH NORTH},
	 * {@link Directions#EAST EAST}, {@link Directions#SOUTH SOUTH},
	 * {@link Directions#WEST WEST}).
	 */
	private Map<Directions, Tile> neighbourTiles;

	/**
	 * Indica se la {@link Tile tile} e' visibile da una formica dell'agente nel
	 * turno corrente.
	 */
	private int visible;

	/**
	 * Indica se la {@link Tile tile} corrente e' occupata da una formica o meno.
	 */
	private boolean occupiedByAnt;

	/**
	 * <p>
	 * Se {@link #occupiedByAnt} {@code = true}, corrisponde all'ID della formica
	 * che occupa la {@link Tile tile}.<br>
	 * Se {@link #occupiedByAnt} {@code = false} e {@link #type} {@code =}
	 * {@link TileTypes#HILL HILL}, corrisponde all'ID del proprietario del
	 * {@code formicaio}.
	 * </p>
	 * <p>
	 * Se {@code idOwner} e' diverso dallo {@code 0} significa che la formica o il
	 * formicaio non appartengono all'agente bensi' sono di una fazione nemica.
	 * </p>
	 * 
	 */
	private Integer idOwner;
	
	private Boolean isSuitable;

	/**
	 * Se il {@link #type} {@code =} {@link TileTypes#LAND} e {@link #occupiedByAnt}
	 * {@code = false} la {@link Tile tile} potrebbe contenere cibo. In tal caso
	 * {@code containsFood = true}.
	 */
	private boolean containsFood;// TODO da usare!

	private static final Comparator<Tile> visionComparator = (Tile o1, Tile o2) -> (Integer.compare(o1.getVisible(), o2.getVisible()));
	
	/**
	 * <p>
	 * Costruttore di un oggetto di {@link Tile}.
	 * </p>
	 * <p>
	 * Inizializza la sua {@link #row riga} e la sua {@link #col colonna} per poi
	 * settare la sua {@link #type tipologia} ad {@link TileTypes#UNEXPLORED
	 * inesplorata}.
	 * </p>
	 * 
	 * @param row riga della {@link Tile tile} nella mappa del gioco
	 * @param col colonna della {@link Tile tile} nella mappa del gioco
	 */
	public Tile(int row, int col) {
		this.row = row;
		this.col = col;
		type = TileTypes.UNEXPLORED;
		neighbourTiles = new HashMap<>();
		visible = 0;
		occupiedByAnt = false;
		idOwner = null;
		containsFood = false;
		isSuitable = false;
	}

	/**
	 * Restituisce la riga della {@link Tile tile} nella mappa del gioco.
	 * 
	 * @return {@link #row}
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Restituisce la colonna della {@link Tile tile} nella mappa del gioco.
	 * 
	 * @return {@link #col}
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Se la {@link Tile tile} e' visibile nel turno corrente da almeno una delle
	 * formiche dell'agente, restituisce {@code true}; {@code false} altrimenti.<br>
	 * 
	 * @return {@link #visible}
	 */
	private boolean isVisible() {// TODO non utilizzato?
		return visible == 0;
	}

	public int getVisible() {
		return visible;
	}

	/**
	 * Setta la {@link #visible visibilita'} della {@link Tile tile} a {@code true}
	 * se almeno una formica dell'agente, nel turno corrente, e' in grado di vederla
	 * {@link Tile tile}; altrimenti, {@link #visible} viene settata a
	 * {@code false}.
	 * 
	 * @param visible {@code true} se almeno una formica dell'agente, nel turno
	 *                corrente, e' in grado di vedere la {@link Tile tile};
	 *                {@code false}, altrimenti
	 */
	void setVisible(boolean visible) {
		this.visible = visible ? 0 : this.visible - 1;
	}

	/**
	 * Restituisce {@code true} se la {@link Tile tile} e' correntemente occupata da
	 * una formica.
	 * 
	 * @return {@link #occupiedByAnt} ossia {@code true} se la {@link Tile tile} e'
	 *         occupata da una formica; {@code false}, altrimenti
	 */
	public boolean isOccupiedByAnt() {// TODO non utilizzato?!
		return occupiedByAnt;
	}

	/**
	 * Restituisce la {@link TileTypes tipologia} della {@link Tile tile}.
	 * 
	 * @return {@link #type}
	 */
	private TileTypes getType() {// TODO non utilizzata??
		return type;
	}

	/**
	 * <p>
	 * Imposta il {@link #type tipo} della {@link Tile tile} a
	 * {@link TileTypes#WATER WATER}.
	 * </p>
	 * Imposta i suoi {@link #neighbourTiles vicini} a {@code null} e
	 * {@link #removeNeighbour(Directions) rimuove} se stessa dalla lista dei
	 * {@link #neighbourTiles vicini} delle {@link Tile tile} ad essa adiacenti.<br>
	 * Cio' viene fatto per semplificare le computazioni effettuate dal modulo di
	 * {@link Search ricerca}.
	 */
	void setTypeWater() {
		type = TileTypes.WATER;
		/*
		if(this.getCol()==0 && this.getRow()!=0 && !Game.getTile(this, Directions.EAST.getOffset()).type.equals(TileTypes.WATER) && !Directions.EAST.getOffset()).type.equals(null))
			Game.getBorders().add(Game.getTile(this, Directions.EAST.getOffset()));
		else if (this.getCol()==Game.getCols()-1 &&  this.getRow()!= Game.getRows()-1 && !Game.getTile(this, Directions.WEST.getOffset()).type.equals(TileTypes.WATER)&& !Game.getTile(this, Directions.WEST.getOffset()).type.equals(TileTypes.WATER))
			Game.getBorders().add(Game.getTile(this, Directions.WEST.getOffset()));
		else if (this.getRow()==0 && !Game.getTile(this, Directions.SOUTH.getOffset()).type.equals(TileTypes.WATER) && !Game.getTile(this, Directions.SOUTH.getOffset()).type.equals(null))
			Game.getBorders().add(Game.getTile(this, Directions.SOUTH.getOffset()));
		else if (this.getRow() == Game.getRows()-1 && !Game.getTile(this, Directions.WEST.getOffset()).type.equals(TileTypes.WATER)&& !Game.getTile(this, Directions.WEST.getOffset()).type.equals(null))
			Game.getBorders().add(Game.getTile(this, Directions.WEST.getOffset()));
		Game.getBorders().remove(this);
		*/
		neighbourTiles.forEach((dir, t) -> t.removeNeighbour(dir.getOpponent()));
		neighbourTiles = null;
		visible = 0;
		occupiedByAnt = false;
		idOwner = null;
		containsFood = false;
		
	}

	/**
	 * Imposta la {@link #type tipologia} della {@link Tile tile} a
	 * {@link TileTypes#HILL HILL} settando il suo {@link #idOwner proprietario} ad
	 * {@code idOwner}.
	 * 
	 * @param idOwner identificativo del proprietario del {@code formicaio}
	 */
	void setTypeHill(Integer idOwner) {
		type = TileTypes.HILL;
		// visible = true; fatto in set vision
		occupiedByAnt = false;
		this.idOwner = idOwner;
		containsFood = false;
	}

	/**
	 * Imposta la {@link #type tipologia} della {@link Tile tile} a
	 * {@link TileTypes#LAND LAND}.
	 */
	void setTypeLand() {
		type = TileTypes.LAND;
		// visible = true; fatto in set vision
		occupiedByAnt = false;
		idOwner = null;
		containsFood = false;
	}

	/**
	 * <p>
	 * Posiziona una formica sulla {@link Tile tile} e setta il suo {@link #idOwner
	 * proprietario} a {@code newIdOwner}.
	 * </p>
	 * <p>
	 * Se la {@link #type tipologia} della {@link Tile tile} e'
	 * {@link TileTypes#HILL HILL} ed il {@link #idOwner proprietario} del
	 * {@code formicaio} e' uguale a {@code newIdOwner}, significa che una formica
	 * si e' posizionata su di un {@code formicaio} nemico radendolo al suolo.<br>
	 * Di conseguenza, la {@link #type tipologia} della {@link Tile tile} diventa
	 * immediatamente {@link TileTypes#LAND LAND}.
	 * </p>
	 * 
	 * @param newIdOwner identificativo del proprietario della {@code formica}
	 */
	void placeAnt(Integer newIdOwner) {
		if (type == TileTypes.HILL && idOwner != newIdOwner)
			type = TileTypes.LAND;
		// else e' un Hill e sopra ci va una sua formica

		// visible = true; fatto da set vision!
		occupiedByAnt = true;
		idOwner = newIdOwner;
	}

	/**
	 * Rimuove una {@code formica} da una {@link Tile tile} impostando
	 * {@link #occupiedByAnt} {@code  = false}.
	 */
	void removeAnt() {
		// visible = false; fatto da clearvision!
		occupiedByAnt = false;
		idOwner = null; // TODO non te ne frega nulla tanto perdi tutti gli hill
	}

	/**
	 * Posiziona il cibo sulla {@link Tile tile} impostando {@link #containsFood} a
	 * {@code true}.
	 */
	void placeFood() {
		containsFood = true;
	}

	/**
	 * Rimuove il cibo dalla {@link Tile tile} impostando {@link #containsFood} a
	 * {@code false}.
	 */
	void removeFood() {
		containsFood = false;
	}

	/**
	 * Aggiunge {@code tile} nell'{@link #neighbourTiles insieme delle tile vicine}
	 * a quella corrente.<br>
	 * Si memorizza, inoltre, anche la direzione per poter raggiungere {@code tile}
	 * partendo dalla {@link Tile tile} corrente.
	 * 
	 * @param cardinal {@link Directions direzione} per poter raggiungere
	 *                 {@code tile} partendo dalla {@link Tile tile} corrente
	 * @param tile     vicina a quella corrente
	 */
	void addNeighbour(Directions cardinal, Tile tile) {
		neighbourTiles.put(cardinal, tile);
	}

	/**
	 * Rimuove il vicino della {@link Tile tile} corrente, raggiungibile da
	 * quest'ultima proseguendo in {@link Directions direzione} {@code cardinal},
	 * dall'{@link #neighbourTiles insieme delle tile vicine} a quella corrente.
	 * 
	 * @param cardinal {@link Directions direzione} per poter raggiungere
	 *                 {@code tile} partendo dalla {@link Tile tile} corrente
	 */
	private void removeNeighbour(Directions cardinal) {
		neighbourTiles.remove(cardinal);
	}

	/**
	 * Restituisce l'insieme di {@link #neighbourTiles vicini} della {@link Tile
	 * tile} corrente con le rispettive {@link Directions direzioni}.
	 * 
	 * @return {@link #neighbourTiles}
	 */
	public Map<Directions, Tile> getNeighbour() {
		return neighbourTiles;
	}

	/**
	 * Se la {@link Tile tile} e' occupata da una formica oppure il suo {@link #type
	 * tipo} {@code = }{@link TileTypes#HILL HILL} restituisce il corrispettivo
	 * {@link #idOwner id} del proprietario.
	 * 
	 * @return {@link #idOwner}
	 * @throws TileTypeException sollevata nel caso in cui la {@link Tile tile} non
	 *                           sia {@link #isOccupiedByAnt() occupata} da una
	 *                           formica oppure il suo {@link #type tipo}
	 *                           {@code != } {@link TileTypes#HILL HILL}
	 */
	private int getOwner() throws TileTypeException {// TODO non lo utilizziamo?!
		if (occupiedByAnt || type.equals(TileTypes.HILL))
			return idOwner;
		else
			throw new TileTypeException("Pensavi ci fosse una formica/un HILL invece era " + type);
	}

	/**
	 * <p>
	 * Restituisce il valore assoluto della differenza tra la {@link #row riga}
	 * della {@link Tile tile} corrente e la {@code riga} della {@link Tile tile}
	 * {@code t2}.
	 * </p>
	 * <p>
	 * Utile per il calcolo della {@link Game#getDistance(Tile, Tile) distanza} tra
	 * due {@link Tile tile}.
	 * </p>
	 * 
	 * @param t2 {@link Tile tile} da cui si vuole calcolare la distanza
	 * @return valore assoluto della differenza tra la {@link #row riga} della
	 *         {@link Tile tile} corrente e la {@code riga} della {@link Tile tile}
	 *         {@code t2}
	 */
	public int getDeltaRow(Tile t2) {
		return Math.abs(getRow() - t2.getRow());
	}

	/**
	 * <p>
	 * Restituisce il valore assoluto della differenza tra la {@link #col colonna}
	 * della {@link Tile tile} corrente e la {@code colonna} della {@link Tile tile}
	 * {@code t2}.
	 * </p>
	 * <p>
	 * Utile per il calcolo della {@link Game#getDistance(Tile, Tile) distanza} tra
	 * due {@link Tile tile}.
	 * </p>
	 * 
	 * @param t2 {@link Tile tile} da cui si vuole calcolare la distanza
	 * @return valore assoluto della differenza tra la {@link #col colonna} della
	 *         {@link Tile tile} corrente e la {@code colonna} della {@link Tile
	 *         tile} {@code t2}
	 */
	public int getDeltaCol(Tile t2) {
		return Math.abs(getCol() - t2.getCol());
	}

	/**
	 * <p>
	 * Riscrittura di {@code hashCode()} per assegnare un identificativo alla
	 * {@link Tile tile} che corrisponde a<br>
	 * {@code id:} {@link #row numero di riga} {@code *} {@link #col numero di
	 * colonna} .
	 * </p>
	 * <center>----------------</center> {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return row * 50000 + col;
	}

	/**
	 * <p>
	 * Riscrittura di {@code equals(Object)} per il confronto di due oggetti di tipo
	 * {@link Tile} in base al loro contenuto.
	 * </p>
	 * <center>----------------</center> {@inheritDoc}
	 */
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

	/**
	 * TODO DA CANCELLARE??????????
	 * 
	 * @return
	 */
	public boolean isSuitable() {// TODO da cancellare?? riguarda solo acqua??? bohboh
		
		return this.isSuitable;
		// return (occupiedByAnt || (type.equals(TileTypes.HILL) && idOwner==0 )) ?
		// false : true;
		// FIXME se è una formica nemica ????????

		// FIXME stiamo considerando isSuitable se c'è una formica nostra sopra
		// ma questo considera il turno corrente e non il turno successivo!

		// dovrebbe controllare in game se ad una formica è stato asssegnato di andare
		// in questa tile

		// viene utilizzata da BFS
	}
	
	public void setSuitable(boolean suitable) {
		this.isSuitable = suitable;
	}

	public static final Comparator<Tile> visionComparator() {
		return visionComparator;
	}
}
