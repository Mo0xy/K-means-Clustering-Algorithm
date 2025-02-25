package data;

import java.io.Serializable;
import java.util.Set;

/**
 * Classe astratta che rappresenta la entità item.
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */
public abstract class Item implements Serializable {
    /**
     * Attributo dell'item.
     */
    private final Attribute attribute;

    /**
     * Valore dell'item.
     */
    private Object value;

    /**
     * Costruttore parametrizzato che inizializza i valori dei membri {@link #attribute} e {@link #value}.
     *
     * @param attribute attributo dell'item
     * @param value valore dell'item
     */
    Item(Attribute attribute, Object value){
        this.attribute = attribute;
        this.value = value;
    }

    /**
     * Restituisce l'attributo dell'item.
     *
     * @return attributo dell'item
     */
    Attribute getAttribute(){
        return  this.attribute;
    }

    /**
     * Restituisce il valore dell'item.
     *
     * @return valore dell'item
     */
    Object getValue(){
        return this.value;
    }

    /**
     * Sovrascrive metodo ereditato dalla superclasse Object e restuisce la stringa rappresentante lo stato dell'oggetto.
     *
     * @return stato dell'attributo
     */
    public String toString(){
        return this.value.toString();
    }

    /**
     * Calcola e restituisce la distanza tra due oggetti.
     *
     * @param a oggetto da confrontare
     * @return distanza tra due oggetti
     */
    abstract double distance(Object a);

    /**
     * Aggiorna il valore dell'item.
     *
     * @param data dataset
     * @param clusterData insieme d'indici di esempi
     */
    public void update(Data data, Set<Integer> clusterData){
        value = data.computePrototype(clusterData, attribute);
    }

}
