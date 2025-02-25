package database;

/**
 * Classe per la gestione delle eccezioni di connessione al database.
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */
public class DatabaseConnectionException extends Exception{

    /**
     * Costruttore della classe.
     *
     * @param message messaggio di errore da restituire.
     */
    public DatabaseConnectionException(String message){
        super(message);
    }

}
