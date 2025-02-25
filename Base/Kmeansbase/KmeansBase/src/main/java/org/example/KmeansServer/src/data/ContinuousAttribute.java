package data;

/**
 * Classe che modella un attributo continuo (numerico).
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */
public class ContinuousAttribute extends Attribute {
    /**
     * Valore massimo dell'attributo.
     */
    private final double max;
    /**
     * Valore minimo dell'attributo.
     */
    private final double min;

    /**
     * Invoca il costruttore della classe madre e inizializza i membri aggiunti per estensione.
     *
     * @param name nome dell'attributo continuo
     * @param index identificativo dell'attributo continuo
     * @param min valore minimo dell'attributo continuo
     * @param max valore massimo dell'attributo continuo
     */
    ContinuousAttribute(String name, int index, double min, double max){
        super(name, index);
        this.max = max;
        this.min = min;
    }

    /**
     * Calcola e restituisce il valore normalizzato del parametro passato in input.
     *
     * @param v valore da normalizzare
     *
     * @return valore normalizzato
     */
    double getScaledValue(double v){
        return (v - min) / (max - min);
    }
}
