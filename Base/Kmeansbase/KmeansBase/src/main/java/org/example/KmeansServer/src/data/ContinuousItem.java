package data;

/**
 * Classe che modella un Item continuo (numerico).
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */
public class ContinuousItem extends Item {

    /**
     * Invoca il costruttore della classe madre e inizializza i membri aggiunti per estensione.
     *
     * @param attribute attributo continuo
     * @param value valore continuo
     */
    ContinuousItem(ContinuousAttribute attribute, Double value){
        super(attribute, value);
    }

    /**
     * Sovrascrive metodo ereditato dalla superclasse e restuisce la distanza tra due oggetti.
     *
     * @param a oggetto da confrontare
     * @return distanza tra due oggetti
     */
    @Override
    double distance(Object a) {
        double thisvalue = ((ContinuousAttribute) this.getAttribute()).getScaledValue((Double) this.getValue());
        double othervalue = ((ContinuousAttribute) this.getAttribute()).getScaledValue((Double) a);
        return (Double.isNaN(Math.abs(thisvalue - othervalue)) ? 0 : Math.abs(thisvalue - othervalue));
    }
}