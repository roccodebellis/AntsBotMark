package combat;

/**
 * Classe enumerativa che elenca tutte le possibili mosse
 * che si possono effettuare durante una situazione di
 * {@link CombatSimulation combattimento}.
 * @author Debellis, Lorusso
 *
 */
public enum MovesModels {
	
	/**
	 * Mossa di attacco in cui ogni formica di una fazione
	 * inizializza una ricerca alla scoperta della formica nemica piu' vicina
	 * o del nido {@code formicaio} piu' vicino e prova ad eseguire un passo
	 * nella direzione della fazione opposta;
	 */
	ATTACK,
	/**
	 * <p>Mossa di avanzamento/ritirata.</p>
	 * <p>La formica prova a restare ad una esatta distanza dalle formiche
	 * della fazione opposta in modo da restare fuori dalla distanza per l'inizio del combattimento.<br>
	 * Questa distanza sara' AttackRadius+2 per tutte le formiche degli agenti
	 * (assumendo che la fazione nemica possa effettuare una mossa prima della risoluzione
	 * della battaglia) e AttackRadius+1 per le formiche nemiche (poiche' la risoluzione della battaglia
	 * ha luogo immediatamente dopo la mossa delle formiche nemiche).</p>
	 * Nella sola mossa di {@code HOLD} e' possibile implementare allo stesso tempo sia una mossa di
	 * avanzamento sicura che una mossa di ritirata sicura:<br>
	 * essa dipende infatti dalla distanza dal nemico che permettera' alle formiche di scegliere
	 * di avanzare o di andare in ritirata per poter raggiungere la distanza richiesta.
	 *  le formiche avanzeranno o andranno in ritirata
	 * per raggiungere la distanza richiesta.
	 */
	HOLD,
	
	/**
	 * <p>Mossa di inattivita'.</p>
	 * Nessuna formica del gruppo sara' mossa.
	 */
	IDLE,
	
	/**
	 * <p>Mossa di avanzamento verso un'unica direzione.</p>
	 * Tutte le formiche della fazione corrente si sposteranno verso {@link Directions#NORTH}.
	 */
	NORTH,
	
	/**
	 * <p>Mossa di avanzamento verso un'unica direzione.</p>
	 * Tutte le formiche della fazione corrente si sposteranno verso {@link Directions#SOUTH}.
	 */
	SOUTH,
	
	/**
	 * <p>Mossa di avanzamento verso un'unica direzione.</p>
	 * Tutte le formiche della fazione corrente si sposteranno verso {@link Directions#EAST}.
	 */
	EAST,
	
	/**
	 * <p>Mossa di avanzamento verso un'unica direzione.</p>
	 * Tutte le formiche della fazione corrente si sposteranno verso {@link Directions#WEST}.
	 */
	WEST;

}
