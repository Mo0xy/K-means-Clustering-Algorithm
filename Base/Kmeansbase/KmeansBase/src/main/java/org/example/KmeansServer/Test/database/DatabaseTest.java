package database;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per il package database.
 */

public class DatabaseTest {

    /**
     * Accesso al database.
     */
    private final DbAccess db = new DbAccess();
    /**
     * Istanziazione di TableData.
     */
    private final TableData tbdata = new TableData(db);
    /**
     * Nome della tabella.
     */
    private final String table = "playtennis";

    /**
     * Test per il metodo initConnection.
     */
    @Test
    void initConnectionTest() {
        assertDoesNotThrow(() -> db.initConnection());
    }

    /**
     * Test per il metodo getDistinctTransazioni.
     */
    @Test
    void getDistinctTransazioniTest() throws DatabaseConnectionException {
        db.initConnection();
        assertDoesNotThrow(() -> tbdata.getDistinctTransazioni(table));
    }
}
