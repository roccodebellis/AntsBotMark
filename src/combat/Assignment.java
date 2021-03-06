package combat;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import defaultpackage.Configuration;
import game.Game;
import game.Order;
import game.Tile;
import timing.Timing;
import vision.Offsets;

class Assignment implements Comparable<Assignment> {

	private MovesModels moveType;
	private static Logger LOGGER = Logger.getLogger(Assignment.class.getName());
	private Set<Tile> ants;
	private int antsLosses;

	private Set<Tile> antsHills;
	private int antsHillsDestroyed;

	private Map<Integer, Set<Tile>> enemyAnts;
	/**
	 * non mettere +1 pd
	 */
	private List<Integer> enemyLosses;

	private Map<Integer, Set<Tile>> enemyHills;
	private List<Integer> enemyHillsDestroyed;

	private Set<Tile> foodTiles;
	private int antsFoodCollected;
	private List<Integer> enemyFoodCollected;

	private int currentTurn;

	private boolean isEnemyMoves;

	private TreeSet<Assignment> child;

	private Set<Order> antsMove;

	private double value;

	Assignment(int turn, Set<Tile> myAntSet, Set<Tile> antHills, int antsLosses, int antsHillsDestroyed,
			int antsFoodCollected, Map<Integer, Set<Tile>> enemyAntSet, Map<Integer, Set<Tile>> enemyHills,
			List<Integer> enemyLosses, List<Integer> enemyHillsDestroyed, List<Integer> enemyFoodCollected,
			Set<Tile> foodTiles, boolean enemyMoves, MovesModels moveType, Set<Order> antsMove) {
		this.moveType = moveType;

		this.currentTurn = turn;
		this.isEnemyMoves = enemyMoves;
		this.child = new TreeSet<Assignment>(Collections.reverseOrder());

		this.ants = myAntSet;
		this.antsHills = antHills;
		this.antsLosses = antsLosses;
		this.antsHillsDestroyed = antsHillsDestroyed;

		this.enemyAnts = enemyAntSet;
		this.enemyHills = enemyHills;
		this.enemyLosses = enemyLosses;

		this.enemyHillsDestroyed = enemyHillsDestroyed;

		this.foodTiles = foodTiles;
		this.antsFoodCollected = antsFoodCollected;
		this.enemyFoodCollected = enemyFoodCollected;

		this.antsMove = new HashSet<Order>(antsMove);
	}

	/*
	 * Assignment(int turn, Set<Tile> myAntSet, Set<Tile> antHills, Map<Integer,
	 * Set<Tile>> enemyAntSet, Map<Integer, Set<Tile>> enemyHills, Set<Tile>
	 * foodTiles, boolean enemyMoves, MovesModels moveType, Set<Order> antsMove) {
	 * this.moveType = moveType;
	 * 
	 * this.currentTurn = turn; this.isEnemyMoves = enemyMoves; this.child = new
	 * TreeSet<Assignment>(Collections.reverseOrder());
	 * 
	 * this.ants = myAntSet; this.antsHills = antHills; this.antsLosses = 0;
	 * this.antsHillsDestroyed = 0;
	 * 
	 * this.enemyAnts = enemyAntSet; this.enemyHills = enemyHills; this.enemyLosses
	 * = new ArrayList<Integer>(enemyAnts.size()); IntStream.range(0,
	 * enemyAnts.size()).parallel().forEachOrdered(i -> enemyLosses.add(0));
	 * 
	 * this.enemyHillsDestroyed = new ArrayList<Integer>(enemyAnts.size());
	 * IntStream.range(0, enemyAnts.size()).parallel().forEachOrdered(i ->
	 * enemyHillsDestroyed.add(0));
	 * 
	 * this.foodTiles = foodTiles; this.antsFoodCollected = 0;
	 * this.enemyFoodCollected = new ArrayList<Integer>(enemyAnts.size());
	 * IntStream.range(0, enemyAnts.size()).parallel().forEachOrdered(i ->
	 * enemyFoodCollected.add(0));
	 * 
	 * antsMove = new HashSet<Order>(); this.antsMove = new
	 * HashSet<Order>(antsMove); }
	 */

	public boolean isEnemyMove() {
		return isEnemyMoves;
	}

	public Set<Tile> getAnts() {
		Set<Tile> output;
		if (isEnemyMoves) {
			output = new HashSet<Tile>();
			this.enemyAnts.forEach((key, enemySet) -> output.addAll(enemySet));
		} else
			output = new HashSet<Tile>(this.ants);
		return output;
	}

	public int getAnts_number() {
		return ants.size();
	}

	public Set<Tile> getOpponentAnts() {
		Set<Tile> output;
		if (!isEnemyMoves) {
			output = new HashSet<Tile>();
			this.enemyAnts.forEach((key, enemySet) -> output.addAll(enemySet));
		} else
			output = new HashSet<Tile>(this.ants);
		return output;
	}

	public Set<Tile> getOpponentHills() {
		Set<Tile> curEnemyHills;
		if (isEnemyMoves) {
			curEnemyHills = new HashSet<Tile>();
			enemyHills.values().forEach(enemySet -> curEnemyHills.addAll(enemySet));
		} else
			curEnemyHills = new HashSet<Tile>(antsHills);
		return curEnemyHills;
	}

	public int getOpponentAnts_number() {
		return enemyAnts.size();
	}

	public int getTurnsLeft() {
		return Timing.getTurnLeft(currentTurn);
	}

	public int getAntsLosses_number() {
		return antsLosses;
	}

	public int getOpponentLosses_number() {
		return enemyLosses.parallelStream().mapToInt(Integer::intValue).sum();
	}

	public int getOpponentHillDestroyed_number() {
		return enemyHillsDestroyed.parallelStream().mapToInt(Integer::intValue).sum();
	}

	public int getAntsHillDestroyed_number() {
		return antsHillsDestroyed;
	}

	public int getAntsFoodCollected_number() {
		return antsFoodCollected;
	}

	public int getOpponentFoodCollected_number() {
		return enemyFoodCollected.parallelStream().mapToInt(Integer::intValue).sum();
	}

	public Assignment performMove(Set<Order> moves, MovesModels moveType) {
		//LOGGER.info("\tperformMove()");// +moves+",
		// "+moveType+")["+ants+"]["+enemyAnts+"]**********");
		Set<Tile> newAnts = new HashSet<Tile>(ants);
		Map<Integer, Set<Tile>> newEnemyAnts = new HashMap<Integer, Set<Tile>>();
		enemyAnts.forEach((key, set) -> newEnemyAnts.put(key, new HashSet<Tile>(set)));
		//LOGGER.severe("enemy: " + enemyAnts);
		//LOGGER.severe("NEW enemyANTS: " + newEnemyAnts);
		if (isEnemyMoves) {
			moves.parallelStream().forEachOrdered(move -> {
				IntStream.range(0, newEnemyAnts.size()).parallel().forEachOrdered(i -> {
					if (newEnemyAnts.get(i).remove(move.getOrigin()))
						newEnemyAnts.get(i).add(move.getOrderedTile());
				});
			});
			/*
			 * LOGGER.info("\t~performMove() - isEnemyMoves(antsMove:"+this.antsMove+
			 * ")***********"); return new Assignment(currentTurn+1, newAnts, antsHills,
			 * newEnemyAnts, enemyHills, foodTiles, !isEnemyMoves, moveType, this.antsMove);
			 */

		} else {
			moves.parallelStream().forEachOrdered(move -> {
				newAnts.remove(move.getOrigin());
				newAnts.add(move.getOrderedTile());
				// this.antsMove.add(move);
			});
			// antsMove = moves;
		}
		//LOGGER.info("\t~performMove() - end**************");
		return new Assignment(currentTurn + 1, newAnts, antsHills, antsLosses, antsHillsDestroyed, antsFoodCollected,
				newEnemyAnts, enemyHills, enemyLosses, enemyHillsDestroyed, enemyFoodCollected, foodTiles,
				!isEnemyMoves, moveType, moves);
	}

	public void resolveCombatAndFoodCollection() {
		if (!ants.equals(null) && !enemyAnts.equals(null)) {
			// LOGGER.info("resolveCombatAndFoodCollection("+currentTurn+"°
			// isEM:"+isEnemyMoves+" a:"+ants+" e:"+enemyAnts+")");
			// istantKill();
			battle();
			hillRazing();
			foodResolution();
		}
		// LOGGER.info("~resolveCombatAndFoodCollection()");
	}

	/**
	 * Per ogni formica calcola il focus di attacco, in base a quanti nemici ha in
	 * vista.
	 * 
	 * @param ants  formiche del bot
	 * @param enemy formiche avversarie
	 * @return per ogni formica, il numero di formiche avversarie nel raggio di
	 *         attacco
	 */
	private HashMap<Tile, Integer> computeFocusAttack(Set<Tile> ants, Set<Tile> enemy) {
		HashMap<Tile, Integer> focusAttack = new HashMap<Tile, Integer>();

		Offsets attack = new Offsets(Game.getAttackRadius2());

		ants.parallelStream().forEachOrdered(ant -> {
			Set<Tile> shape = Game.getTiles(ant, attack);
			focusAttack.put(ant, (int) shape.parallelStream().filter(t -> enemy.contains(t)).count());
		});

		return focusAttack;
	}

	/**
	 * Si occupa della gestione della battaglia. La battaglia si fonda sul focus di
	 * ogni formica, e' necessario, infatti, calcolare il focus delle formiche (sia
	 * del bot che nemiche) in modo da stimare i morti durante la simulazione del
	 * combattimento:
	 * 
	 * <ul>
	 * <li>Se il focus della formica del bot e' maggiore rispetto a quello
	 * dell'avversaria Significa che il bot ha il focus diviso tra piu' nemici
	 * rispetto alla nemica. Di conseguenza, muore.
	 * <li>Se il focus della formica del bot e' minore rispetto a quello del nemico,
	 * la formica nemica muore;</li>
	 * <li>Se i due avversari hanno focus equivalente, muoiono entrambe.</li>
	 * </ul>
	 * Si aggiornano di volta in volta i set di formiche morte (per entrambe le
	 * fazioni) e si incrementa il numero dei morti.
	 * 
	 */
	private void battle() {
		// LOGGER.info("\tbattle()");
		HashMap<Tile, Integer> focusAttack = new HashMap<Tile, Integer>();

		Set<Tile> enemy = new HashSet<Tile>();
		enemyAnts.forEach((id, set) -> enemy.addAll(set));

		// _________________CALCOLO FOCUS ATTACK di ogni fazione

		focusAttack.putAll(computeFocusAttack(ants, enemy));
		IntStream.range(0, enemyAnts.size()).parallel().forEachOrdered(i -> {
			//LOGGER.severe("-------------enemyAnts" + enemyAnts);
			Set<Tile> ienemySet = enemyAnts.get(i);
			Set<Tile> tempEenemy = new HashSet<Tile>();
			tempEenemy.addAll(ants);
			if (i > 0)
				IntStream.range(0, i + 1).parallel().forEachOrdered(j -> tempEenemy.addAll(enemyAnts.get(j)));
			if (i + 2 < enemyAnts.size())
				IntStream.range(i + 2, enemyAnts.size()).parallel()
						.forEachOrdered(j -> tempEenemy.addAll(enemyAnts.get(j )));

			// System.out.println("* ienemySet"+ ienemySet);
			// System.out.println("* enemy"+ enemy);

			focusAttack.putAll(computeFocusAttack(ienemySet, tempEenemy));
		});
		// _________________FINE CALCOLO FOCUS ATTACK

		Set<Tile> deadAnts = new TreeSet<Tile>();

		// Per ogni formica del bot
		ants.parallelStream().forEachOrdered(ant -> {
			// si ottiene il focus attack della formica corrente
			int curAntFA = focusAttack.get(ant);

			Map<Integer, Set<Tile>> deadEnemyAnts = new HashMap<Integer, Set<Tile>>();//
			enemyAnts.forEach((k, v) -> deadEnemyAnts.put(k, new HashSet<Tile>()));

			// LOGGER.info("\tenemyAnts: " + enemyAnts);
			// LOGGER.info("\tdeadEnemyAnts: " + deadEnemyAnts);

			// Per ogni nemico
			IntStream.range(0, enemyAnts.size()).parallel().forEachOrdered(i -> {
				// si ottiene il numero di formiche nemiche morte nel livello MinMax precedente
				int enemyLossesNumber = enemyLosses.get(i);
				// si salva in un set temporaneo le formiche del nemico corrente
				Set<Tile> ienemySet = enemyAnts.get(i);

				Set<Tile> iOpponent = new HashSet<Tile>();
				Map<Tile, Integer> gimmeID = new HashMap<Tile, Integer>();
				if (i > 0)
					IntStream.range(0, i + 1).parallel().forEachOrdered(j -> {
						Set<Tile> jEnemy = enemyAnts.get(j);
						iOpponent.addAll(jEnemy);
						gimmeID.putAll(
								jEnemy.stream().collect(HashMap<Tile, Integer>::new, (m, c) -> m.put(c, j), (m, u) -> {
								}));
					});

				// per ogni nemico
				ienemySet.parallelStream().forEachOrdered(enemyAnt -> {
					// Si ottiene il focus attack del nemico corrente
					int curEnemyFA = focusAttack.get(enemyAnt);
					// Se sia il bot che la formica nemica hanno pi� di una formica
					// avversaria in vista
					if (curAntFA > 0 && curEnemyFA > 0) {
						/*- Se il focus della formica del bot e' maggiore rispetto a quello dell'avversaria
							Significa che il bot ha il focus diviso tra pie' nemici
							rispetto alla nemica. Di conseguenza, muore.
						  - Se  il focus della formica del bot e' minore rispetto a quello
						  	del nemico, la formica nemica muore;
						  - Se i due avversari hanno focus equivalente, muoiono entrambe.
						  Si aggiornano di volta in volta i set di formiche morte (per entrambe
						  le fazioni) e si incrementa il numero dei morti
						 */
						if (curAntFA > curEnemyFA) {
							antsLosses++;
							deadAnts.add(ant);
						} else if (curAntFA < curEnemyFA) {
							deadEnemyAnts.get(i).add(enemyAnt);
							enemyLosses.set(i, enemyLossesNumber + 1);
						} else {
							antsLosses++;
							deadAnts.add(ant);
							deadEnemyAnts.get(i).add(enemyAnt);
							enemyLosses.set(i, enemyLossesNumber + 1);
						}
					}
					// losses di enemy contro gli altri opponents
					iOpponent.parallelStream().forEachOrdered(opponentAnt -> {
						int idOpponent = gimmeID.get(opponentAnt);
						int opponentLossesNumber = enemyLosses.get(idOpponent);
						// int enemyLossesNumber = enemyLosses.get(i);
						// Si ottiene il focus attack del nemico corrente
						int curOpponentFA = focusAttack.get(opponentAnt);

						// Se sia il bot che la formica nemica hanno pi� di una formica
						// avversaria in vista
						if (curEnemyFA > 0 && curOpponentFA > 0) {
							/*- Se il focus della formica del bot e' maggiore rispetto a quello dell'avversaria
								Significa che il bot ha il focus diviso tra pie' nemici
								rispetto alla nemica. Di conseguenza, muore.
							  - Se  il focus della formica del bot e' minore rispetto a quello
							  	del nemico, la formica nemica muore;
							  - Se i due avversari hanno focus equivalente, muoiono entrambe.
							  Si aggiornano di volta in volta i set di formiche morte (per entrambe
							  le fazioni) e si incrementa il numero dei morti
							 */
							if (curEnemyFA > curOpponentFA) {
								deadEnemyAnts.get(i).add(enemyAnt);
								enemyLosses.set(i, enemyLossesNumber + 1);
							} else if (curEnemyFA < curOpponentFA) {
								deadEnemyAnts.get(idOpponent).add(opponentAnt);
								enemyLosses.set(idOpponent, enemyLossesNumber + 1);
							} else {
								deadEnemyAnts.get(idOpponent).add(opponentAnt);
								deadEnemyAnts.get(i).add(enemyAnt);
								enemyLosses.set(i, enemyLossesNumber + 1);
								enemyLosses.set(idOpponent, opponentLossesNumber + 1);
							}
						}
					});
				});
			});
			// remove enemy
			//LOGGER.severe("-------------deadEnemyAnts" + deadEnemyAnts);
			deadEnemyAnts.forEach((i, v) -> {
				if (!v.isEmpty() && enemyAnts.containsKey(i) && !enemyAnts.get(i).isEmpty()) {
					// String out = "Enemy " + i + 1 + " before: " + enemyAnts.get(i).size();
					enemyAnts.get(i).removeAll(v);
					// LOGGER.info("\t\t" + out + " after: " + enemyAnts.get(i).size() + ", losses:
					// "
					// + deadEnemyAnts.get(i).size());
				}
			});

		});

		ants.removeAll(deadAnts);
		// LOGGER.info("\t~battle()");
	}

	/**
	 * 
	 */
	private void istantKill() {

		// formiche tue/di ogni nemico si suicidano tra di loro

		// formiche nemiche si suicidano tra di loro

		// Per ogni nemico
		IntStream.range(0, enemyAnts.size()).parallel().forEachOrdered(i -> {
			Set<Tile> iremoveAnts = new TreeSet<Tile>();
			Set<Tile> ienemyAnts = enemyAnts.get(i + 1);

			// Per ogni formica nemica
			ienemyAnts.parallelStream().forEachOrdered(ienemy -> {
				IntStream.range(i + 1, enemyAnts.size()).parallel().forEachOrdered(j -> {
					/*
					 * se il nemico vuole camminare su di una tile in cui e' contenuto un altro
					 * nemico significa che due formiche, nemiche, si stanno suicidando. Si
					 * incrementa, dunque, il numero di nemici morti
					 */
					if (enemyAnts.get(j + 1).remove(ienemy)) {
						if (!iremoveAnts.contains(ienemy)) {
							iremoveAnts.add(ienemy);
							enemyLosses.set(i, enemyLosses.get(i) + 1);
						}
						enemyLosses.set(j, enemyLosses.get(j) + 1);
					}
				});
			});
			ienemyAnts.removeAll(iremoveAnts);
		});

		// formiche nostre e formiche nemiche si suicidono
		Set<Tile> antsKilled = new TreeSet<Tile>();
		ants.parallelStream().forEachOrdered(ant -> {
			IntStream.range(0, enemyAnts.size()).parallel().forEachOrdered(i -> {
				if (enemyAnts.get(i + 1).remove(ant)) {
					if (!antsKilled.contains(ant)) {
						antsKilled.add(ant);
						antsLosses++;
					}
					enemyLosses.set(i, enemyLosses.get(i) + 1);
				}
			});
		});
		ants.removeAll(antsKilled);
	}

	/**
	 * Metodo che calcola se hill nemiche/nostre sono state rase al suolo durante la
	 * simulazione del turno in uno dei livelli di MinMax (Assignment Corrente).
	 */
	private void hillRazing() { // TODO fare una sola funzione

		Set<Tile> antHillDestroyed = new TreeSet<Tile>();

		antsHills.parallelStream().forEachOrdered(aHill -> {
			IntStream.range(0, enemyAnts.size()).parallel().forEachOrdered(i -> {

				Set<Tile> iOpponent = new HashSet<Tile>();
				if (i > 0)
					IntStream.range(0, i + 1).parallel().forEachOrdered(j -> iOpponent.addAll(enemyAnts.get(j)));

				if (enemyAnts.get(i).contains(aHill)) {
					antsHillsDestroyed++;
					antHillDestroyed.add(aHill);
				}

				//LOGGER.severe("enemy hills: " + enemyHills);
				//LOGGER.severe("enemy hills destroyed: " + enemyHillsDestroyed);
				Set<Tile> curEnemyHills = enemyHills.get(i);//////////////////////////////
				if (!curEnemyHills.isEmpty()) {
					Set<Tile> hillDestroyed = new TreeSet<Tile>();
					curEnemyHills.parallelStream().forEachOrdered(eHill -> {
						if (ants.contains(eHill)) {
							enemyHillsDestroyed.set(i, enemyHillsDestroyed.get(i) + 1);
							hillDestroyed.add(eHill);
						} else if (iOpponent.contains(eHill)) {
							enemyHillsDestroyed.set(i, enemyHillsDestroyed.get(i) + 1);
						}
					});
					enemyHills.get(i).removeAll(hillDestroyed);
				}
			});

		});
		antsHills.removeAll(antHillDestroyed);
	}

	/**
	 * 
	 * 
	 * Sapendo che spawn_radius è uguale ad 1 vengono presi tutti i vicini delle
	 * tile di cibo
	 * 
	 * 
	 * NB: è scritta sapendo che spawn radius == 1, il modo corretto sarebbe usare
	 * una static search
	 */
	private void foodResolution() {
		Set<Tile> foodGathered = new TreeSet<Tile>();

		foodTiles.parallelStream().forEachOrdered(food -> {
			Set<Integer> opponentID = new HashSet<Integer>();

			Set<Tile> neighbours = food.getNeighbours().parallelStream().map(nDir -> food.getNeighbourTile(nDir))
					.collect(Collectors.toSet());
			Iterator<Tile> neItr = neighbours.iterator();
			while (neItr.hasNext()) {
				Tile neighbour = neItr.next();

				if (ants.contains(neighbour))
					opponentID.add(0);

				int fakeID = -1;
				while (++fakeID < enemyAnts.size())
					if (enemyAnts.get(fakeID).contains(neighbour))///////////////////////////////////
						opponentID.add(fakeID + 1);
			}

			if (opponentID.size() == 1) {
				if (opponentID.contains(0))
					this.antsFoodCollected++;
				else {
					int enemyID = opponentID.iterator().next() - 1;
					enemyFoodCollected.set(enemyID, enemyFoodCollected.get(enemyID) + 1);
				}
				foodGathered.add(food);
			} else if (opponentID.size() > 1)
				foodGathered.add(food);
		});

		foodTiles.removeAll(foodGathered);
	}

	public void addChild(Assignment childState) {
		if (child.size() == 0 || childState.getValue() > getValue())
			value = childState.getValue();
		child.add(childState);
	}

	double getValue() {
		return value;
	}

	public Set<Order> getFirstChild() {
		/*
		 * LOGGER.severe("\t\t\t\tgetFirstChild()"); LOGGER.severe("\t\t\t\t"+child);
		 * LOGGER.severe("\t\t\t\t~getFirstChild()");
		 */
		return child.first().getMoves();
	}

	Set<Order> getMoves() {
		return antsMove;
	}

	long GetExtensionEstimate() {
		/*
		 * LOGGER.severe("\t\tGetExtensionEstimate()");
		 * LOGGER.severe("\t\t\tants:"+ants); LOGGER.severe("\t\t\tenemy:"+enemyAnts);
		 * LOGGER.severe("\t\t~GetExtensionEstimate()");
		 */
		return (long) (ants.size()
				+ enemyAnts.entrySet().parallelStream().mapToInt(eASet -> eASet.getValue().size()).sum())
				* Configuration.getMilSecUsedForEachAntsInCS();
	}

	/**
	 * 
	 * Lo stato di valutazione è per la maggior parte basato sull'ammontare di
	 * perdite da parte di una fazione nella risoluzione del combattimento in questo
	 * nodo e lungo tutti il percorso del miglior figlio attraverso l'albero. Le
	 * perdite nemiche incrementano i risultati di valutazione, mentre le perdite
	 * delle formiche dell'agente lo decrementano. Viene assegnato un bonus per la
	 * completa eliminazione di tutte le formiche nemiche, mentre viene sottratto un
	 * malus se tutte le formiche dell'agente vengono sterminate. Il valore dei
	 * nemici persi è incrementato nel caso in cui il numero delle formiche
	 * dell'agente sia sufficientemente alto rispetto al numero delle formiche
	 * nemiche, o quando il gioco è vicino al turno finale. Tutti i valori numerici
	 * sono stati
	 * 
	 * 
	 * FIXME aggiungere quando veine effettuata la traduzione che un punteggio di 1
	 * eè assegnato se un nido nemico viene distruto
	 */
	double evaluate() {
		Double AntsMultiplier = 1.1D;
		Double OpponentMultiplier = 1.0D;

		double value;

		// TODO MassRatioThreshold impostare mass radio in base al numero di formiche
		if (getAnts_number() > 3) // FIXME MassRatioThreshold
			OpponentMultiplier *= Math.max(1, Math.pow((getAnts_number() + 1) / (getOpponentAnts_number() + 1), 2));

		// TODO crescita logaritmica col passare dei turni a partire da una certa soglia
		// if (getTurnsLeft() < 50)
		OpponentMultiplier *= Math.pow(getTurnsLeft(), 1.5D);

		value = OpponentMultiplier * getOpponentLosses_number() - AntsMultiplier * getAntsLosses_number();

		if (0 == getAnts_number())
			value -= 100;
		else if (0 == getOpponentAnts_number())
			value += 90;

		// TODO RISCRIVERE FUNZIONE
		// considerando un pareggio
		// considerando in caso di pareggio se il numero di formiche uccise da me è
		// maggiore
		// del numero di formiche perse

		value += getOpponentHillDestroyed_number() * 10;
		value -= getAntsHillDestroyed_number() * 9;

		value += getAntsFoodCollected_number() * 7;
		value -= getOpponentFoodCollected_number() * 5;
		// LOGGER.severe("\t\tvalue before: " + value);
		/*
		 * value -= enemyAnts.values().parallelStream().mapToDouble(es ->
		 * es.parallelStream().mapToDouble(e -> ants.parallelStream().mapToDouble(a ->
		 * Game.getDistance(e,a)).sum()).sum()).sum();
		 */
		this.value = value;
		// LOGGER.severe("\t\tants: " + ants);
		// LOGGER.severe("\t\tenemyAnts: " + enemyAnts);
		// LOGGER.severe("\t\tvalue after: " + value);
		return value;
	}

	@Override
	public int compareTo(Assignment o) {
		int compValue = Double.compare(value, o.value);

		return compValue == 0 ? moveType.compareTo(o.moveType) : compValue;
	}

	@Override
	public String toString() {
		return "Assignment movetype=" + moveType + " [Turn=" + currentTurn + " value=" + value + "]";
	}

	public Set<Assignment> getChildren() {

		return child;
	}

	public MovesModels getMoveType() {
		return moveType;
	}

}
