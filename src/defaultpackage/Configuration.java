package defaultpackage;

import game.Game;

public class Configuration {
	
	private static final int CombatModuleMinMaxMaxDepth = 6;
	private static final int MilSecUsedForEachAntsInCS = 10;
	private static final int CombatModuleSearchRadius = Game.getAttackRadius2() * 9;
	
	public static int getCombatModuleMinMaxMaxDepth(){
		return CombatModuleMinMaxMaxDepth;
	}
	
	/*tempo presunto utilizzato da combattimento per simulare la battaglia con una formica*/
	public static int getMilSecUsedForEachAntsInCS(){
		return MilSecUsedForEachAntsInCS;
	}
	
	public static int getCombatModuleSearchRadius(){
		return CombatModuleSearchRadius;
	}
	
	
}
