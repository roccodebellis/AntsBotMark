package defaultpackage;

public class Configuration {
	
	private static final int CombatModuleMinMaxMaxDepth = 6;
	private static final int MilSecUsedForEachAntsInCS = 10;
	
	public static int getCombatModuleMinMaxMaxDepth(){
		return CombatModuleMinMaxMaxDepth;
	}
	
	/*tempo presunto utilizzato da combattimento per simulare la battaglia con una formica*/
	public static int getMilSecUsedForEachAntsInCS(){
		return MilSecUsedForEachAntsInCS;
	}
	
	
	
}
