package mining;

import data.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Classe di test per il package mining.
 */
public class MiningTest {


    @Test
    void kmeansMinerTest() {
        KmeansMiner kmeansMiner = new KmeansMiner(3);
        assertDoesNotThrow(() -> kmeansMiner.kmeans(new Data("playtennis")));
    }

    @Test
    void kmeansMinerFromFileTest() {
        assertDoesNotThrow(() -> new KmeansMiner("saves/playtennis_3.dat"));
    }

    @Test
    void kmeansMinerSaveTest(){
        assertDoesNotThrow(() -> new KmeansMiner(3).save("playtennis_3"));
    }

}
