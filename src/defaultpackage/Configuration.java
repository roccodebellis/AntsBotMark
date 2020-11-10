package defaultpackage;

import game.Game;

public class Configuration {
	
<<<<<<< HEAD
	private static final int CombatModuleMinMaxMaxDepth = 5;
	private static final int MillSecUsedForEachAntsInCS = 12;
=======
	private static final int CombatModuleMinMaxMaxDepth = 3;
	private static final int MillSecUsedForEachAntsInCS = 10;
>>>>>>> 639102bbb6dbbd92a7db2df8ebd99bad8226828d
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
