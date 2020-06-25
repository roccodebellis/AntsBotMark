package game;

import vision.Offset;

/**
 * Classe enumerativa che indica le possibili direzioni di percorrimento
 * a partire da/per raggiungere una {@link Tile}.
 *
 * @author Debellis, Lorusso
 *
 */
public enum Directions {
	/**
	 * Corrisponde al {@code NORD}.
	 */
	NORTH{
		/**
		 * Restituisce la direzione opposta a quella corrente ossia "SOUTH".
		 * {@inheritDoc}
		 */
		@Override
		public Directions opponent() {
			return Directions.SOUTH;
		}
		
		@Override
		public Directions next() {
			return Directions.EAST;
		}
		
		/**
		 * <p>Restituisce "N" ossia NORTH.</p>
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "N";
		}
		
		/**
		 * <p>Restituisce {@code [-1, 0]} ossia l'offset della direzione NORTH.<p>
		 * {@inheritDoc}
		 */
		@Override
		public Offset getOffset() {
			return new Offset(-1,0);
		}
	},
	/**
	 * Corrisponde all'{@code EST}.
	 */
	EAST{
		/**
		 * <p>Restituisce la direzione opposta a quella corrente ossia "WEST".</p>
		 * {@inheritDoc}
		 */
		@Override
		public Directions opponent() {
			return Directions.WEST;
		}
		
		@Override
		public Directions next() {
			return Directions.SOUTH;
		}
		
		/**
		 * <p>Restituisce "E" ossia EAST.</p>
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "E";
		}
		
		/**
		 * <p>Restituisce {@code [0, 1]} ossia l'offset della direzione EAST.</p>
		 * {@inheritDoc}
		 */
		@Override
		public Offset getOffset() {
			return new Offset(0,1);
		}
	},
	/**
	 * Corrisponde al {@code SUD}.
	 */
	SOUTH{
		/**
		 * Restituisce la direzione opposta a quella corrente ossia "NORTH".
		 * {@inheritDoc}
		 */
		@Override
		public Directions opponent() {
			return Directions.NORTH;
		}
		
		@Override
		public Directions next() {
			return Directions.WEST;
		}
		
		/**
		 * <p>Restituisce "S" ossia SOUTH.</p>
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "S";
		}
		
		/**
		 * Restituisce {@code [1, 0]} ossia l'offset della direzione SOUTH
		 * {@inheritDoc}
		 */
		@Override
		public Offset getOffset() {
			return new Offset(1,0);
		}
	},
	/**
	 * Corrisponde a {@code OVEST}.
	 */
	WEST{
		/**
		 * <p>Restituisce la direzione opposta a quella corrente ossia "EAST".</p>
		 * {@inheritDoc}
		 */
		@Override
		public Directions opponent() {
			return Directions.EAST;
		}
		
		@Override
		public Directions next() {
			return Directions.NORTH ;
		}
		
		/**
		 * <p>Restituisce "W" ossia WEST.</p>
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "W";
		}
		
		/**
		 * <p>Restituisce {@code [0, -1]} ossia l'offset della direzione WEST.</p>
		 * {@inheritDoc}
		 */
		@Override
		public Offset getOffset() {
			return new Offset(0,-1);
		}
	},
	/**
	 * Indica che non c'e' nessuno spostamento: la formica deve rimanere ferma.
	 */
	STAYSTILL {
		/**
		 * <p>Restituisce la direzione opposta a quella corrente ossia se stessa.</p>
		 * {@inheritDoc}
		 */
		@Override
		public Directions opponent() {
			return STAYSTILL;
		}

		/**
		 * <p>Restituisce "Stay Still!" ossia STAYSTILL.</p>
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "Stay Still!";
		}
		
		/**
		 * <p>Restituisce {@code [0, 0]} ossia l'offset della direzione WEST.</p>
		 * {@inheritDoc}
		 */
		@Override
		public Offset getOffset() {
			return new Offset(0,0);
		}
	};
	
	/**
	 * @return la direzione opposta a quella corrente
	 */
	public abstract Directions opponent();
	
	public abstract Directions next();
	
	/**
	 * @return la stringa contenente la lettera
	 * corrispondente alla direzione corrente
	 */
	public abstract String toString();
    
	/**
	 * 
	 * @return l'{@link Offset offset} della {@link Directions direzione} corrente.
	 */
	public abstract Offset getOffset();
}
