package mining;

import data.Data;
import data.OutofRangeSampleSize;
import data.Tuple;

import java.io.Serializable;

/**
 * Classe che rappresenta l'insieme dei cluster.
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */
public class ClusterSet implements Serializable {
    /**
     * Array di cluster.
     */
    private Cluster[] C;
    /**
     * Indice d'inserimento.
     */
    private int i = 0;

    /**
     * Costruttore parametrizzato.
     *
     * @param k numero di cluster.
     */
    ClusterSet(int k){
        C = new Cluster[k];
    }

    /**
     * Aggiunge un cluster.
     * @param c cluster da aggiungere.
     */
    void add(Cluster c){
        C[i] = c;
        i++;
    }

    /**
     * Restituisce il cluster in posizione i.
     *
     * @param i indice del cluster.
     *
     * @return cluster in posizione i.
     */
    Cluster get(int i){
        return C[i];
    }

    /**
     * Inizializza i centroidi.
     *
     * @param data dataset sul quale inizializzare i centroidi.
     *
     * @throws OutofRangeSampleSize se il numero di cluster è maggiore del numero di tuple del dataset.
     */
    void initializeCentroids(Data data) throws OutofRangeSampleSize {
        int[] centroidIndexes = data.sampling(C.length);
        for(int i : centroidIndexes)
        {
            Tuple centroidI = data.getItemSet(i);
            add(new Cluster(centroidI));
        }
    }

    /**
     * Restituisce il cluster più vicino alla tupla.
     *
     * @param tuple tupla.
     *
     * @return cluster più vicino alla tupla.
     */
    Cluster nearestCluster(Tuple tuple){
        Cluster nearestCluster = null;
        double minDistance = Double.MAX_VALUE;
        for(int i = 0; i < C.length; i++){
            double distance = tuple.getDistance(C[i].getCentroid());
            if(distance < minDistance){
                minDistance = distance;
                nearestCluster = C[i];
            }
        }
        return nearestCluster;
    }


    /**
     * Restituisce il cluster che contiene la tupla.
     *
     * @param d indice della tupla.
     *
     * @return cluster che contiene la tupla.
     */
    Cluster currentCluster(int d){
        for(Cluster c : C)
            if(c.contain(d))
                return c;
        return null;
    }

    /**
     * Aggiorna i centroidi.
     *
     * @param data dataset.
     */
    void updateCentroids(Data data){
        for(Cluster c : C)
            c.computeCentroid(data);
    }

    /**
     * Restituisce una stringa che rappresenta l'insieme dei cluster.
     *
     * @return stringa che rappresenta l'insieme dei cluster.
     */
    public String toString(){
        String str = "";
        for(int i = 0; i < C.length; i++)
            str += C[i].toString()+"\n";
        return str;
    }

    /**
     * Restituisce una stringa che rappresenta l'insieme dei cluster con le
     * relative tuple che vi appartengono.
     *
     * @param data dataset.
     *
     * @return stringa che rappresenta l'insieme dei cluster con le relative tuple che vi appartengono.
     */
    public String toString(Data data) {
        String str = "";
        for(int i = 0; i < C.length; i++){
            if (C[i] != null){
                str += i+":"+C[i].toString(data)+"\n";
            }
        }
        return str;
    }

}

