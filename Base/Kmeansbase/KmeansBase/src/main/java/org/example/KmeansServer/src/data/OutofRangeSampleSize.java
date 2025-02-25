package data;

/**
 * Eccezione che viene lanciata quando la dimensione del sample size è maggiore della dimensione del dataset.
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */
public class OutofRangeSampleSize extends Throwable {

    /**
     * Costruttore di default.
     */
    public OutofRangeSampleSize(){
        super();
    }

    /**
     * Costruttore parametrizzato.
     *
     * @param s messaggio da stampare
     */
    public OutofRangeSampleSize(String s){
        super(s);
    }
}
