package server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per il server.
 */

public class ServerTest {
    @Test
    void serverStartUpTest() {
        assertDoesNotThrow(() -> new MultiServer(8080));
        }
}

