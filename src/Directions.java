import java.util.HashMap;
import java.util.Map;

public enum Directions {
	NORTH {
		@Override
		public Directions opponent() {
			return Directions.SOUTH;
		}
		
		@Override
		public String toString() {
			return "N";
		}
	},
	EAST(0, 1, 'e'){
		@Override
		public Directions opponent() {
			return Directions.WEST;
		}
	},
	SOUTH(1, 0, 's'){
		@Override
		public Directions opponent() {
			return Directions.NORTH;
		}
	},
	WEST(0, -1, 'w'){
		@Override
		public Directions opponent() {
			return Directions.EAST;
		}
	};
	
	public abstract Directions opponent();
	public abstract String toString();
	
private static final Map<Character, Directions> symbolLookup = new HashMap<Character, Directions>();
    
    static {
        symbolLookup.put('n', NORTH);
        symbolLookup.put('e', EAST);
        symbolLookup.put('s', SOUTH);
        symbolLookup.put('w', WEST);
    }
    
    private final int rowDelta;
    
    private final int colDelta;
    
    private final char symbol;
    
    Directions(int rowDelta, int colDelta, char symbol) {
        this.rowDelta = rowDelta;
        this.colDelta = colDelta;
        this.symbol = symbol;
    }
    
    /**
     * Returns rows delta.
     * 
     * @return rows delta.
     */
    public int getRowDelta() {
        return rowDelta;
    }
    
    /**
     * Returns columns delta.
     * 
     * @return columns delta.
     */
    public int getColDelta() {
        return colDelta;
    }
    
    /**
     * Returns symbol associated with this direction.
     * 
     * @return symbol associated with this direction.
     */
    public char getSymbol() {
        return symbol;
    }
    
    /**
     * Returns direction associated with specified symbol.
     * 
     * @param symbol <code>n</code>, <code>e</code>, <code>s</code> or <code>w</code> character
     * 
     * @return direction associated with specified symbol
     */
    public static Directions fromSymbol(char symbol) {
        return symbolLookup.get(symbol);
    }
}
