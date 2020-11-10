package defaultpackage;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import timing.Timing;

/**
 * Handles system input stream parsing.
 */
public abstract class AbstractSystemInputParser extends AbstractSystemInputReader {
    private static final String READY = "ready";
    
    private static final String GO = "go";
    
    private static final char COMMENT_CHAR = '#';
    
    private final List<String> input = new ArrayList<String>();
    
    
    Timing time;
    
    /**
     * Classe enumerativa che specifica i token contenenti le informazioni base
     * per l'inizzializzazione del gioco:<ul>
     * <li>LOADTIME:</li>//////////////////////////////////////////////////////////TODO
     * <li>TURNTIME:</li>
     * <li>ROWS:</li>
     * <li>COLS:</li>
     * <li>TURNS:</li>
     * <li>VIEWRADIUS2:</li>
     * <li>ATTACKRADIUS2:</li>
     * <liSPAWNRADIUS2:></li>
     * </ul>
     * 
     * 
     * @author Debellis, Lorusso
     *
     */
    private enum SetupToken {
        LOADTIME, TURNTIME, ROWS, COLS, TURNS, VIEWRADIUS2, ATTACKRADIUS2, SPAWNRADIUS2;
        /**
         * Pattern per la compilazione dei token da inizializzare.<br>
         * Il pattern avra' il seguente aspetto:<BR>
         * <strong>(LOADTIME|TURNTIME|ROWS|COLS|TURNS|VIEWRADIUS2|ATTACKRADIUS2|SPAWNRADIUS2)</strong>
         */
        private static final Pattern PATTERN = compilePattern(SetupToken.class);
    }
    
    /**
     * Classe enumerativa che specifica i token da aggiornare ad ogni turno:<ul>
     * <li>W: corrisponde all'acqua (water);</li>
     * <li>A: corrisponde ad una formica in vita (ant);</li>
     * <li>F: corrisponde al cibo (food)</li>
     * <li>D: corrisponde ad una formica morta (dead ant);</li>
     * <li>H: corrisponde ad un formicaio (hill).</li>
     * </ul>
     * 
     * 
     * @author Debellis, Lorusso
     *
     */
    public enum UpdateToken {
        W, A, F, D, H;
        /**
         * Pattern per la compilazione dei token da aggiornare.<br>
         * Il pattern avra' il seguente aspetto: <strong>(W|A|F|D|H)</strong>
         */
        private static final Pattern PATTERN = compilePattern(UpdateToken.class);
    }
    
    /**
     * Compila i pattern per l'aggiornamento ({@link UpdateToken}) o
     * per l'inizializzazione del gioco ({@link SetupToken}).
     * @param clazz classe enumerativa contentente i token da aggiornare o inizializzare
     * @return il pattern corrispondente ad una delle due classi enumerative
     * 
     * @see UpdateToken
     * @see SetupToken
     */
    private static Pattern compilePattern(Class<? extends Enum> clazz) {
        StringBuilder builder = new StringBuilder("(");
        for (Enum enumConstant : clazz.getEnumConstants()) {
            //ordinal:Returns the ordinal of this enumeration constant
        	//(its position in its enum declaration, where the initial constant is assignedan ordinal of zero).
        	if (enumConstant.ordinal() > 0) {
                builder.append("|");//La linea verticale equivale ad OR per l'esclusivita'
            }
            builder.append(enumConstant.name());
        }
        builder.append(")");
        //builder avra' il seguente aspetto:" (TOKEN1|TOKEN2|---|TOKENn) "
        return Pattern.compile(builder.toString());
    }
    
    /**
     * Colleziona righe lette ed estrapolate dallo stream in input finche' non compare una parola
     * chiave (<i>keyword</i>) per poi analizzarle effettuandone il <i>parse</i>.<br>
     * La lettura di ogni riga viene effettuata dal metodo
     * {@link AbstractSystemInputParser#readSystemInput() readSystemInput}.
     */
    @Override
    public void processLine(String line) {
    	
		
        if (line.equals(READY)) { //PRIMO TURNO
        	
            parseSetup(input);
            finishTurn();
            input.clear();
            
        } else if (line.equals(GO)) { //TURNI SUCCESSIVI
        	
        	time.setTurnStartTime();
        	
            parseUpdate(input);
            doTurn();
            
            finishTurn();
            input.clear();
            
            
            
        } else if (!line.isEmpty()) {
            input.add(line);
        }
        
        
        
    }
    
    /**
     * Parses the setup information from system input stream.
     * 
     * @param input setup information
     */
    public void parseSetup(List<String> input) {
        int loadTime = 0;
        int turnTime = 0;
        int rows = 0;
        int cols = 0;
        int turns = 0;
        int viewRadius2 = 0;
        int attackRadius2 = 0;
        int spawnRadius2 = 0;
        for (String line : input) {
            line = removeComment(line);
            if (line.isEmpty()) {
                continue;
            }
            Scanner scanner = new Scanner(line);
            if (!scanner.hasNext()) {
                continue;
            }
            String token = scanner.next().toUpperCase();
            if (!SetupToken.PATTERN.matcher(token).matches()) {
                continue;
            }
            SetupToken setupToken = SetupToken.valueOf(token);
            switch (setupToken) {
                case LOADTIME:
                    loadTime = scanner.nextInt();
                break;
                case TURNTIME:
                    turnTime = scanner.nextInt();
                break;
                case ROWS:
                    rows = scanner.nextInt();
                break;
                case COLS:
                    cols = scanner.nextInt();
                break;
                case TURNS:
                    turns = scanner.nextInt();
                break;
                case VIEWRADIUS2:
                    viewRadius2 = scanner.nextInt();
                break;
                case ATTACKRADIUS2:
                    attackRadius2 = scanner.nextInt();
                break;
                case SPAWNRADIUS2:
                    spawnRadius2 = scanner.nextInt();
                break;
            }
        }
        this.time = new Timing(loadTime,turnTime,turns);
        setup(rows, cols, viewRadius2, attackRadius2, spawnRadius2);
        
    }
    
    /**
     * Parses the update information from system input stream.
     * 
     * @param input update information
     */
    public void parseUpdate(List<String> input) {
        beforeUpdate();
        for (String line : input) {
            line = removeComment(line);
            if (line.isEmpty()) {
                continue;
            }
            Scanner scanner = new Scanner(line);
            if (!scanner.hasNext()) {
                continue;
            }
            String token = scanner.next().toUpperCase();
            if (!UpdateToken.PATTERN.matcher(token).matches()) {
                continue;
            }
            UpdateToken updateToken = UpdateToken.valueOf(token);
            int row = scanner.nextInt();
            int col = scanner.nextInt();
            switch (updateToken) {
                case W:
                    addWater(row, col);
                break;
                case A:
                    if (scanner.hasNextInt()) {
                        addAnt(row, col, scanner.nextInt());
                    }
                break;
                case F:
                    addFood(row, col);
                break;
                case D:
                    if (scanner.hasNextInt()) {
                        removeAnt(row, col, scanner.nextInt());
                    }
                break;
                case H:
                    if (scanner.hasNextInt()) {
                        addHill(row, col, scanner.nextInt());
                    }
                break;
            }
        }
        afterUpdate();
    }
    
    /**
     * Sets up the game state.
     * 
     * @param loadTime timeout for initializing and setting up the bot on turn 0
     * @param turnTime timeout for a single game turn, starting with turn 1
     * @param rows game map height
     * @param cols game map width
     * @param turns maximum number of turns the game will be played
     * @param viewRadius2 squared view radius of each ant
     * @param attackRadius2 squared attack radius of each ant
     * @param spawnRadius2 squared spawn radius of each ant
     */
    public abstract void setup(int rows, int cols, int viewRadius2, int attackRadius2, int spawnRadius2);
    
    /**
     * Enables performing actions which should take place prior to updating the game state, like
     * clearing old game data.
     */
    public abstract void beforeUpdate();
    
    /**
     * Adds new water tile.
     * 
     * @param row row index
     * @param col column index
     */
    public abstract void addWater(int row, int col);
    
    /**
     * Adds new ant tile.
     * 
     * @param row row index
     * @param col column index
     * @param owner player id
     */
    public abstract void addAnt(int row, int col, int owner);
    
    /**
     * Adds new food tile.
     * 
     * @param row row index
     * @param col column index
     */
    public abstract void addFood(int row, int col);
    
    /**
     * Removes dead ant tile.
     * 
     * @param row row index
     * @param col column index
     * @param owner player id
     */
    public abstract void removeAnt(int row, int col, int owner);
    
    /**
     * Adds new hill tile.
     *
     * @param row row index
     * @param col column index
     * @param owner player id
     */
    public abstract void addHill(int row, int col, int owner);
    
    /**
     * Enables performing actions which should take place just after the game state has been
     * updated.
     */
    public abstract void afterUpdate();
    
    /**
     * Subclasses are supposed to use this method to process the game state and send orders.
     */
    public abstract void doTurn();
    
    /**
     * Finishes turn.
     */
    public void finishTurn() {
 
        System.out.println("go");
        System.out.flush();
    }
    
    private String removeComment(String line) {
        int commentCharIndex = line.indexOf(COMMENT_CHAR);
        String lineWithoutComment;
        if (commentCharIndex >= 0) {
            lineWithoutComment = line.substring(0, commentCharIndex).trim();
        } else {
            lineWithoutComment = line;
        }
        return lineWithoutComment;
    }
}
