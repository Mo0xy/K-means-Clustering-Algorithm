package data;

/**
 * Eccezione che viene lanciata quando la dimensione del sample size è maggiore della dimensione del dataset.
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */
public class OutofRangeSampleSize extends Throwable {

    /**
     * Costruttore parametrizzato.
     *
     * @param message messaggio da stampare
     */
    public OutofRangeSampleSize(String message){
        super(message);
    }
}
