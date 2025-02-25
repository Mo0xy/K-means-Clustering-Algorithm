package server;

/**
 * Classe che gestisce le eccezioni lanciate dal server.
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */
public class ServerException extends Exception {
    /**
     * Costruttore di classe che stampa il messaggio di errore passato come
     * argomento.
     *
     * @param text Stringa corrispondente al messaggio da visualizzare.
     */
    public ServerException(String text) {
        super(text);
    }
}