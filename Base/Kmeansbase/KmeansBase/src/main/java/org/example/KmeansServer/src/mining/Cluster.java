package mining;
import data.Data;
import data.Tuple;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe che rappresenta un cluster.
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */

public class Cluster implements Serializable {

    /**
     * Centroide del cluster.
     */
    private final Tuple centroid;

    /**
     * Insieme degli indici delle tuple appartenenti al cluster.
     */
    private final Set<Integer> clusteredData;

    /**
     * Costruttore parametrizzato.
     *
     * @param centroid centroide del cluster.
     */
    Cluster(Tuple centroid){
        this.centroid = centroid;
        clusteredData = new HashSet<Integer>();
    }

    /**
     * Restituisce il centroide del cluster.
     *
     * @return centroide del cluster.
     */
    Tuple getCentroid(){
        return centroid;
    }

    /**
     * Aggiorna il centroide del cluster.
     *
     * @param data dataset.
     */
    void computeCentroid(Data data){
        for(int i = 0; i < centroid.getLength(); i++){
            centroid.get(i).update(data, clusteredData);
        }
    }

    /**
     * Aggiunge un'istanza al cluster.
     *
     * @param id indice dell'istanza da aggiungere.
     *
     * @return {@code true} se l'istanza viene aggiunta, {@code false} altrimenti.
     */
    boolean addData(int id){
        return clusteredData.add(id);
    }

    /**
     * Verifica se il cluster contiene un'istanza.
     *
     * @param id indice dell'istanza da verificare.
     *
     * @return {@code true} se il cluster contiene l'istanza, {@code false} altrimenti.
     */
    boolean contain(int id){
        return clusteredData.contains(id);
    }

    /**
     * Rimuove un'istanza dal cluster.
     *
     * @param id indice dell'istanza da rimuovere.
     */
    void removeTuple(int id){
        clusteredData.remove(id);
    }

    /**
     * Restituisce una rappresentazione del cluster.
     *
     * @return una stringa che rappresenta il cluster mediante la stampa dei valori del centroide.
     */
    public String toString(){
        String str = "Centroid=(";
        for(int i = 0; i < centroid.getLength(); i++)
            str += centroid.get(i)+" ";
        str += ")";
        return str;
    }


    /***
     * Restituisce una stringa che rappresenta il cluster con le relative tuple(esempi) che vi appartengono.
     *
     * @param data dataset.
     *
     * @return stringa che rappresenta il cluster con le relative tuple(esempi) che vi appartengono.
     */
    public String toString(Data data){
        String str = "Centroid=(";
        for(int i = 0; i < centroid.getLength(); i++)
            str += centroid.get(i)+ " ";
        str += ")\nExamples:\n";

        Set<Integer> array = clusteredData;
        for(Integer id : array) {
            str += "[";
                str += data.getItemSet(id)+ " ";
            str += "] dist=" + getCentroid().getDistance(data.getItemSet(id)) + "\n";
        }
        str += "AvgDistance="+getCentroid().avgDistance(data, array);
        str += "\n";
        return str;

    }

}
