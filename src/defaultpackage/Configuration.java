package defaultpackage;

import game.Game;

public class Configuration {
	

	private static final int CombatModuleMinMaxMaxDepth = 5;
	private static final int MillSecUsedForEachAntsInCS = 10;

	private static final int CombatModuleSearchRadius = Game.getAttackRadius2() * 9;
	
	public static int getCombatModuleMinMaxMaxDepth(){
		return CombatModuleMinMaxMaxDepth;
	}
	
	/*tempo presunto utilizzato da combattimento per simulare la battaglia con una formica*/
	public static int getMilSecUsedForEachAntsInCS(){
		return MillSecUsedForEachAntsInCS;
	}
	
	public static int getCombatModuleSearchRadius(){
		return CombatModuleSearchRadius;
	}
	
	
}
