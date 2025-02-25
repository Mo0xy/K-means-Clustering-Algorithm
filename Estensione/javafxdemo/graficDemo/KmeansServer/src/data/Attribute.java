package data;

import java.io.Serializable;

/**
 *Classe astratta che rappresenta la entità attributo.
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */
public abstract class Attribute implements Serializable {
    /**
     *Nome simbolico dell'attributo.
     */
    private final String name;
    /**
     *Identificativo numerico dell'attributo.
     */
    private final int index;

    /**
     * Costruttore parametrizzato che inizializza i valori dei membri {@link #name} e {@link #index}.
     *
     * @param name nome dell'attributo
     * @param index identificativo numerico dell'attributo
     */
    Attribute(final String name, final int index) {
        this.name = name;
        this.index = index;
    }

    /**
     * Restituisce il nome dell'attributo.
     *
     * @return nome dell'attributo
     */
    String getName() {
        return this.name;
    }

    /**
     * Restituisce l'identificativo numerico dell'attributo.
     *
     * @return index identificativo numerico dell'attributo
     */
    int getIndex() {
        return this.index;
    }

    /**
     * Sovrascrive metodo ereditato dalla superclasse e restuisce la string rappresentante lo stato dell'oggetto
     *
     * @return name nome dell'attributo
     */
    @Override
    public String toString() {
        return this.name;
    }
}
