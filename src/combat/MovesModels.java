package combat;

public enum MovesModels implements comparator{
	ATTACK,
	HOLD,
	IDLE,
	NORTH,
	SOUTH,
	EAST,
	WEST;

	abstract int getMoveNumber();
	
}
