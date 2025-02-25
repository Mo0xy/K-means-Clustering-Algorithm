package mining;

import data.Data;
import data.OutofRangeSampleSize;

import java.io.*;

/**
 * Classe per l'esecuzione dell'algoritmo di clustering K-means.
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */
public class KmeansMiner {
    /**
     * Insieme dei cluster.
     */
    private ClusterSet C;

    /**
     * Costruttore che inizializza l'insieme dei cluster.
     *
     * @param k numero di cluster.
     */

    public KmeansMiner(int k){
        C = new ClusterSet(k);
    }

    /**
     * Costruttore che inizializza l'insieme dei cluster a partire da un file.
     *
     * @param fileName nome del file da cui caricare l'insieme dei cluster.
     *
     * @throws FileNotFoundException se il file non viene trovato.
     * @throws IOException se si verifica un errore di I/O.
     * @throws ClassNotFoundException se la classe non viene trovata.
     */
    public KmeansMiner(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException{
        try{
            FileInputStream inFile = new FileInputStream(fileName);
            ObjectInputStream inStream = new ObjectInputStream(inFile);
            C  = (ClusterSet) inStream.readObject();
            inStream.close();
            inFile.close();
        }
        catch(FileNotFoundException e){
            throw new FileNotFoundException("File not found");
        }
        catch(IOException e){
            throw new IOException("I/O error");
        }
        catch(ClassNotFoundException e){
            throw new ClassNotFoundException("Class not found");
        }
    }

    /**
     * Salva l'insieme dei cluster su file.
     * @param fileName nome del file su cui salvare l'insieme dei cluster.
     *
     * @throws FileNotFoundException se il file non viene trovato.
     * @throws IOException se si verifica un errore di I/O.
     */
    public void save(String fileName) throws FileNotFoundException, IOException{
        try{
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
            out.writeObject(this.C);
            out.close();
        }
        catch(FileNotFoundException e){
            throw new FileNotFoundException("File not found");
        }
        catch(IOException e){
            throw new IOException("I/O error");
        }
    }

    /**
     * Restituisce l'insieme dei cluster.
     * @return l'insieme dei cluster.
     */
    public ClusterSet getC(){
        return C;
    }

    /**
     * Esegue l'algoritmo di clustering K-means.
     * @param data insieme di dati.
     *
     * @return il numero d'iterazioni eseguite.
     *
     * @throws OutofRangeSampleSize se il numero di cluster è maggiore del numero di tuple.
     */
    public int kmeans(Data data) throws OutofRangeSampleSize {
        int numberOfIterations = 0;
        //STEP 1
        C.initializeCentroids(data);
        boolean changedCluster = false;
        do{
            numberOfIterations++;
            //STEP 2
            changedCluster = false;
            for(int i = 0; i < data.getNumberOfExamples(); i++){
                Cluster nearestCluster = C.nearestCluster(data.getItemSet(i));
                Cluster oldCluster = C.currentCluster(i);
                boolean currentChange = nearestCluster.addData(i);
                if(currentChange)
                    changedCluster = true;
                //rimuovo la tupla dal vecchio cluster
                if(currentChange && oldCluster != null)
                    //il nodo va rimosso dal suo vecchio cluster
                    oldCluster.removeTuple(i);
            }
            //STEP 3
            C.updateCentroids(data);
        }
        while(changedCluster);

        return numberOfIterations;
    }

}
