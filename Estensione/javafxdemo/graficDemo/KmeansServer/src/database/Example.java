package database;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che rappresenta un insieme di dati.
 * Implementa l'interfaccia {@link Comparable} per consentire il confronto
 * con altre istanze di Example in base ai dati contenuti.
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */
public class Example implements Comparable<Example>{
    /**
     * Lista interna che contiene gli oggetti che costituiscono l'insieme di dati.
     */
    private List<Object> example = new ArrayList<Object>();

    /**
     * Aggiunge un oggetto all'insieme di dati.
     *
     * @param o L'oggetto da aggiungere all'insieme di dati.
     */
    public void add(Object o){
        example.add(o);
    }

    /**
     * Restituisce l'oggetto nella posizione specificata nell'insieme di dati.
     *
     * @param i L'indice dell'oggetto da recuperare.
     *
     * @return L'oggetto nella posizione specificata nell'insieme di dati.
     */
    public Object get(int i){
        return example.get(i);
    }

    /**
     * Confronta questa istanza di Example con un'altra istanza di Example in base ai dati contenuti.
     *
     * @param ex L'istanza di Example da confrontare.
     *
     * @return Un valore negativo se questa istanza è "minore" di {@code ex}, un valore positivo se è "maggiore",
     *         o zero se sono considerate uguali in base ai dati contenuti.
     */
    public int compareTo(Example ex) {

        int i=0;
        for(Object o:ex.example){
            if(!o.equals(this.example.get(i)))
                return ((Comparable)o).compareTo(example.get(i));
            i++;
        }
        return 0;
    }

    /**
     * Restituisce una che rappresenta l'insieme di dati contenuto in questa istanza di Example.
     *
     * @return Una stringa che rappresenta l'insieme di dati contenuto in questa istanza di Example.
     */
    @Override
    public String toString(){
        String str="";
        for(Object o:example)
            str+=o.toString()+ " ";
        return str;
    }

}