package com.example.graficdemo;

/**
 * Questa classe rappresenta un'eccezione personalizzata che viene lanciata.
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