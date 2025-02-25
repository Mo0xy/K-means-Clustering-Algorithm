package data;

import java.io.Serializable;
import java.util.Set;

/**
 * Classe per rappresentare una tupla di valori.
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */
public class Tuple implements Serializable {

    /**
     * Array d'Item che rappresenta la tupla.
     */
    private final Item [] tuple;

    /**
     * Costruttore della classe Tuple.
     *
     * @param size Dimensione della tupla.
     */
    Tuple(int size){
        tuple = new Item[size];
    }

    /**
     * Restituisce la dimensione della tupla.
     *
     * @return Dimensione della tupla.
     */
    public int getLength(){
        return tuple.length;
    }

    /**
     * Restituisce l'Item in posizione i della tupla.
     *
     * @param i Posizione dell'Item da restituire.
     * @return Item in posizione i della tupla.
     */
    public Item get(int i){
        return tuple[i];
    }

    /**
     * Aggiunge un Item alla tupla.
     *
     * @param c Item da aggiungere.
     * @param i Posizione in cui aggiungere l'Item.
     */
    void add(Item c, int i){
        tuple[i] = c;
    }

    /**
     * Restituisce la distanza tra la tupla e un'altra tupla.
     *
     * @param obj Tupla con cui calcolare la distanza.
     *
     * @return Distanza tra la tupla e obj.
     */
    public double getDistance(Tuple obj){
        double distance = 0;
        for(int i = 0; i < tuple.length; i++){
            distance += tuple[i].distance(obj.get(i).getValue());
        }
        return distance;
    }

    /**
     * Restituisce la distanza media tra la tupla e un'altra tupla.
     *
     * @param data Dataset.
     * @param clusteredData Tupla con cui calcolare la distanza.
     *
     * @return Distanza media tra la tupla e il cluster di riferimento.
     */
    public double avgDistance(Data data, Set<Integer> clusteredData){
        double p = 0.0, sumD = 0.0;
        for(Integer id: clusteredData){
            double d = getDistance(data.getItemSet(id));
            sumD += d;
        }
        p = sumD/clusteredData.size();
        return p;
    }

    /**
     * Restituisce una stringa che rappresenta la tupla.
     *
     * @return Stringa che rappresenta la tupla.
     */
    @Override
    public String toString() {
        String itemString = "";
        for (Item item : tuple) {
            itemString += item.toString() + ", ";
        }
        itemString = itemString.substring(0, itemString.length() - 2);

        return itemString;
    }

}
