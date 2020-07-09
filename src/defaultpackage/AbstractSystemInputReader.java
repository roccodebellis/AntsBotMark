package defaultpackage;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Handles system input stream reading.
 */
public abstract class AbstractSystemInputReader {
    /**
     * Legge lo stream in input del sistema riga per riga. <br>
     * Tutti i caratteri sono convertiti in lettere minuscole ed ogni riga
     * viene passata e processata dal metodo {@link #processLine(String) processLine}.
     * 
     * @throws IOException if an I/O error occurs
     */
    public void readSystemInput() throws IOException {
        StringBuilder line = new StringBuilder();
        int c;
        while ((c = System.in.read()) >= 0) {
            if (c == '\r' || c == '\n') {
            	/*
            	OutputStream f = new FileOutputStream("input.ants",false);
            	f.write(String.valueOf(line+"\n").getBytes());
            	f.close();
    */        	
                processLine(line.toString().toLowerCase().trim());
                line.setLength(0);
            } else {
                line = line.append((char)c);
            }
        }
    }
    
    /**
     * Processa una riga letta dal metodo {@link #readSystemInput() readSystemInput}
     * in un modo definito dall'implementazione della sottoclasse {@link AbstractSystemInputParser}.
     * 
     * @param line singola riga estrapolata (e tagliata) dal sistema in input
     */
    public abstract void processLine(String line);
}
