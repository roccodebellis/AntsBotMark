package game;
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
		
		/**
		 * <p>Restituisce "N" ossia NORTH.</p>
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "N";
		}
	},
	/**
	 * Corrisponde all'{@code EST}.
	 */
	EAST{
		/**
		 * Restituisce la direzione opposta a quella corrente ossia "WEST".
		 * {@inheritDoc}
		 */
		@Override
		public Directions opponent() {
			return Directions.WEST;
		}
		/**
		 * <p>Restituisce "E" ossia EAST.</p>
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "E";
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
		/**
		 * <p>Restituisce "S" ossia SOUTH.</p>
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "S";
		}
	},
	/**
	 * Corrisponde a {@code OVEST}.
	 */
	WEST{
		/**
		 * Restituisce la direzione opposta a quella corrente ossia "EAST".
		 * {@inheritDoc}
		 */
		@Override
		public Directions opponent() {
			return Directions.EAST;
		}
		
		/**
		 * <p>Restituisce "W" ossia WEST.</p>
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "W";
		}
	};
	
	/**
	 * @return la direzione opposta a quella corrente
	 */
	public abstract Directions opponent();
	
	/**
	 * @return la stringa contenente la lettera
	 * corrispondente alla direzione corrente
	 */
	public abstract String toString();
    
}
