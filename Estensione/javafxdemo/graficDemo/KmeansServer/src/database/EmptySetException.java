package database;

/**
 * Classe che gestisce l'eccezione di un insieme vuoto.
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */
public class EmptySetException extends Exception{

    /**
     * Costruttore della classe.
     * @param message messaggio di errore.
     */
    public EmptySetException(String message){
        super(message);
    }

}
