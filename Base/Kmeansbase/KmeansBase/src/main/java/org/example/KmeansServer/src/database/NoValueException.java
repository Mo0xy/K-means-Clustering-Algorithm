package database;

/**
 * Classe che rappresenta un'eccezione che viene lanciata quando si cerca di accedere a un valore che non esiste.
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */
public class NoValueException extends Exception {

    /**
     * Costruttore di default.
     */
    public NoValueException(){
        super();
    }

    /**
     * Costruttore parametrizzato.
     * @param message messaggio da visualizzare.
     */

    public NoValueException(String message){
        super(message);
    }

}
