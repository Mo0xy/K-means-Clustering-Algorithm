package data;

/**
 * Classe che modella un Item discreto (categorico).
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */
class DiscreteItem extends Item {

    /**
     * Invoca il costruttore della classe madre e inizializza i membri aggiunti per estensione.
     *
     * @param attribute attributo discreto
     * @param value valore discreto
     */
    DiscreteItem(DiscreteAttribute attribute, Object value) {
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
        return  getValue().equals(a) ? 0 : 1;
    }

}
