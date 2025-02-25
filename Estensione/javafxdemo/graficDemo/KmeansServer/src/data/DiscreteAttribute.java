package data;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Classe che rappresenta un attributo discreto (categorico).
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */
public class DiscreteAttribute extends Attribute implements Iterable<String>{
    /**
     * Insieme di valori distinti che può assumere l'attributo.
     */
    private final TreeSet<String> values;

    /**
     * Invoca il costruttore della classe madre e inizializza il membro values con il parametro in input.
     *
     * @param name nome dell'attributo
     * @param index identificativo numerico dell'attributo
     * @param values dominio dell'attributo
     */
    DiscreteAttribute(String name, int  index, TreeSet<String> values){
        super(name, index);
        this.values = values;
    }

    /**
     * Restituisce il numero di valori distinti che può assumere l'attributo.
     *
     * @return numero di valori distinti
     */
    int getNumberOfDistinctValues(){
        return values.size();
    }


    /**
     * Determina il numero di volte che il valore v compare
     * in corrispondenza dell'attributo corrente (indice di colonna) negli
     * esempi memorizzati in data e indicizzate (per riga) da clusteredData.
     *
     * @param data insieme di dati
     * @param clusteredData insieme di dati clusterizzati
     * @param v valore di cui si vuole conoscere la frequenza
     * @return count numero di occorrenze di v nell'attributo
     */
    int frequency(Data data, Set<Integer> clusteredData, String v){
        int count = 0;
        for(Integer i : clusteredData)
            if (data.getAttributeValue(i, this.getIndex()) != null){
                if (data.getAttributeValue(i, this.getIndex()).equals(v))
                    count++;
            }
        return count;

    }

    /**
     * Restituisce il valore dell'attributo in posizione index.
     * @return valore dell'attributo
     */
    @Override
    public Iterator<String> iterator() {
        return values.iterator();
    }

}
