package game;
/**
 * Classe enumerativa che elenca le varie tipologie che ogni {@link Tile tile}
 * puo' assumere.
 * @author Debellis, Lorusso
 *
 */
public enum TileTypes {
	/**
	 * E' una {@link TileTypes tipologia} che indica che la {@link Tile tile}
	 * non e' stata ancora esplorata: la si assegna di default all'inizio del gioco
	 * a tutte le {@link Tile tile} della mappa.<br>
	 * Man mano che le formiche esplorano la mappa, se la {@link Tile tile}
	 * entra nel raggio di visione di una formica, verra' ottenuta l'informazione
	 * sulla sua reale tipologia (una delle sottostanti).
	 */
	UNEXPLORED,
	/**
	 * E' una {@link TileTypes tipologia} che indica che la {@link Tile tile}
	 * e' di tipo {@code terreno}.<br>Le formiche possono camminarci sopra
	 * a patto che non risulti essere occupata da un'altra formica.
	 * Se sulle varie {@link Tile tile} di tipo {@code LAND} non sono presenti formiche
	 * e se non ci sono formiche di fazioni opposte attorno ad esse, e' possibile che
	 * vi si depositi del cibo. 
	 */
	LAND,
	/**
	 * E' una {@link TileTypes tipologia} che indica che la {@link Tile tile}
	 * e' di tipo {@code acqua}.<br>Le formiche <b>non</b> possono camminarci sopra.
	 */
	WATER,
	/**
	 * E' una {@link TileTypes tipologia} che indica che la {@link Tile tile}
	 * e' di tipo {@code formicaio}. Il {@code formicaio} puo' appartenere, o meno,
	 * all'agente.
	 */
	HILL
}
