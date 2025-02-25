package data;

import org.junit.jupiter.api.Test;

import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Classe di test per la classe Data.
 */

public class DataTest {
    private final String table = "playtennis";

    /**
     * Test per il costruttore di Data.
     */
    @Test
    void dataInitTest() {
        assertDoesNotThrow(() -> new Data(table));
    }

    /**
     * Test per il metodo {@link ContinuousItem#distance(Object)}.
     */
    @Test
    void ContinuousItemDistanceTest() {
        ContinuousAttribute attribute = new ContinuousAttribute("test", 0, 0, 1);
        ContinuousItem c = new ContinuousItem(attribute, 0.5);

        assertEquals(0, c.distance(0.5));
    }

    /**
     * Test per il metodo {@link DiscreteItem#distance(Object)}.
     */
    @Test
    void DiscreteItemDistanceTest() {
        DiscreteItem d = new DiscreteItem(new DiscreteAttribute("test", 0, new TreeSet<>()), "equalsString");
        assertEquals(0, d.distance("equalsString"));
    }
}
