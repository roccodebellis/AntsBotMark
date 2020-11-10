package defaultpackage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import attackdefencehills.AttackDefenceHills;
//import attackdefensehills.AttackDefenseHills;
import game.Directions;
import game.Game;
import game.Order;
import game.Tile;
import timing.Modules;
import timing.Timing;


public class MyBot extends Bot {

	private static Logger LOGGER = Logger.getLogger( MyBot.class.getName() );

	private static void logger(String[] args) {
		FileHandler fh; 
		System.setProperty("java.util.logging.SimpleFormatter.format", "\t[%4$-7s] %5$s %n");
		try {  

			// This block configure the logger with handler and formatter  
			String filePath = MyBot.class.getProtectionDomain().getCodeSource().getLocation().getFile();
			filePath = filePath.replaceAll("%20"," ");
			filePath = filePath.substring(0, filePath.length()-4).concat("_logger.log");
			File file = new File(filePath);
			file.getParentFile().mkdirs();
			fh = new FileHandler(file.getAbsolutePath(),true);  
			LOGGER.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);   

		} catch (SecurityException e) {  
			e.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		} 
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		  

		LOGGER.severe("-----"+dtf.format(now));
	}





	/**
	 * Main method executed by the game engine for starting the bot.
	 * 
	 * @param args command line arguments
	 * 
	 * @throws IOException if an I/O error occurs
	 */
	public static void main(String[] args) throws IOException {
		logger(args);
		new MyBot().readSystemInput();
	}

	@Override //DA FARW
	public void doTurn() {
		LOGGER.severe("BOT MARK GIUSTO-----"+Timing.getTurnNumber());
		Game state = getGame();


		//1 VISION MODULE
		//2 COMBAT SIMULATION
			//time.start(Modules.Combat);
			//LOGGER.info("state.doCombat()");
		state.doCombat();
			//LOGGER.info("~state.doCombat()");
			//time.end(Modules.Combat);
		
		//2.5 HILL DEFENSE
			time.start(Modules.Defence);
			//LOGGER.info("state.doDefenceHills()");
		state.doDefenceHills();
			//LOGGER.info("~state.doDefenceHills()");
			time.end(Modules.Defence);
		
		//3 FOOD COLLECTION
			time.start(Modules.Food);
			//LOGGER.info("state.doFood()");
		state.doFood();
			//LOGGER.info("~state.doFood()");
			time.end(Modules.Food);
		
		//3.5 HILL ATTACK
			time.start(Modules.Attack);
			//LOGGER.info("state.doAttackHills()");
		state.doAttackHills();
			//LOGGER.info("~state.doAttackHills()");
			time.end(Modules.Attack);
		
		//4 EXPLORATION AND MOVEMENTS
			time.start(Modules.Exploration);
			//LOGGER.info("state.doExploration()");
		state.doExploration();
			//LOGGER.info("~state.doExploration()");
			time.end(Modules.Exploration);

		
		//LOGGER.info("Available Ants: "+ Game.getMyAnts());
		//Game.printMapVision();
		//Game.printNeigbour();
		state.resetTargetSuitable();
		//state.doFood();
	}
}
